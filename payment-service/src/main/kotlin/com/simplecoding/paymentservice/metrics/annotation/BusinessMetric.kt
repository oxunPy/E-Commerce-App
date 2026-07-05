package com.simplecoding.paymentservice.metrics.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class BusinessMetric(
    val value: String,
    val tags: Array<String> = [],
)