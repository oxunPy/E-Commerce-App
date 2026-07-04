package com.simplecoding.orderservice.domain.dto

data class ErrorDto(
    val status: Int,
    val code: String,
    val details: Any?
)
