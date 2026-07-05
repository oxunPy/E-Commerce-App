package com.simplecoding.notificationservice.config

import io.micrometer.tracing.Span
import io.micrometer.tracing.Tracer
import org.springframework.core.task.TaskDecorator
import org.springframework.stereotype.Component

@Component
class TracingTaskDecorator(
    private val tracer: Tracer
) : TaskDecorator {

    override fun decorate(runnable: Runnable): Runnable {
        val currentSpan: Span? = tracer.currentSpan()
        return Runnable {
            if (currentSpan != null) {
                tracer.withSpan(currentSpan).use { scope ->
                    runnable.run()
                }
            } else {
                runnable.run()
            }
        }
    }
}