package com.simplecoding.inventoryservice.metrics.aspect

import com.simplecoding.inventoryservice.metrics.annotation.BusinessMetric
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.Timer
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Aspect
@Component
class BusinessMetricAspect(
    private val meterRegistry: MeterRegistry
) {
    private val timerCache = ConcurrentHashMap<String, Timer>()

    companion object {
        private val log = LoggerFactory.getLogger(BusinessMetricAspect::class.java)
    }

    @Around("@annotation(metric)")
    fun measure(joinPoint: ProceedingJoinPoint, metric: BusinessMetric): Any {
        val startNanos = System.nanoTime()
        var status = "success"

        try {
            return joinPoint.proceed()
        } catch (e: Exception) {
            status = "error"
            throw e
        } finally {
            val durationNanos = System.nanoTime() - startNanos
            recordMetrics(metric, joinPoint, durationNanos, status)
        }
    }

    fun recordMetrics(metric: BusinessMetric, joinPoint: ProceedingJoinPoint, durationNanos: Long, status: String) {
        try {


            val metricName = metric.value
            val className = joinPoint.target.javaClass.name

            var allTags = Tags.of("status", status, "class", className)
            allTags = addCustomTags(allTags, metric.tags)

            Counter.builder("$metricName.total")
                .tags(allTags)
                .description("Total call")
                .register(meterRegistry)
                .increment()

            val timerKey = buildTimerKey(metricName, className, metric.tags, status)
            val timer = timerCache.computeIfAbsent(timerKey) { key ->
                var timerTags = Tags.of("class", className, "status", status)
                timerTags = addCustomTags(timerTags, metric.tags)

                Timer.builder("$metricName.duration")
                    .tags(timerTags)
                    .description("Execution duration")
                    .publishPercentileHistogram()
                    .sla(
                        Duration.ofMillis(50),
                        Duration.ofMillis(100),
                        Duration.ofMillis(500),
                        Duration.ofSeconds(1),
                        Duration.ofSeconds(2)
                    )
                    .register(meterRegistry)
            }

            timer.record(durationNanos, TimeUnit.NANOSECONDS)
        } catch (e: Exception) {
            log.warn("Failed to record metrics for {}", metric.value, e)
        }
    }

    fun addCustomTags(base: Tags, tagExpressions: Array<String>?): Tags {
        if (tagExpressions == null || tagExpressions.isEmpty()) {
            return base
        }

        var result = base
        for (tagExpr in tagExpressions) {
            val equalIndex = tagExpr.indexOf("=")
            if (equalIndex > 0) {
                val key = tagExpr.take(equalIndex).trim()
                val value = tagExpr.substring(equalIndex + 1).trim()
                result = result.and(key, value)
            }
        }

        return result
    }

    fun buildTimerKey(metricName: String, className: String, customTags: Array<String>, status: String): String {
        val builder = StringBuilder()
        builder.append(".$metricName")
        builder.append(".$className")

        if (customTags.isNotEmpty()) {
            customTags.sort()
            for (tag in customTags) {
                builder.append(".$tag")
            }
        }

        return builder.toString()
    }
}