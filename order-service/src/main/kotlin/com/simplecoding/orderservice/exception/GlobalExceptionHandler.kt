package com.simplecoding.orderservice.exception

import com.simplecoding.orderservice.domain.dto.ErrorDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleException(exception: MethodArgumentNotValidException): ResponseEntity<ErrorDto> {
        val errors = exception.bindingResult
            .fieldErrors
            .stream()
            .map<String?> { error: FieldError? -> error!!.field + ":" + error.defaultMessage }
            .toList()

        return ResponseEntity.badRequest()
            .body(ErrorDto(exception.statusCode.value(), "Validation Failed", errors))
    }

    @ExceptionHandler(NotFoundOrderException::class)
    fun handleException(exception: NotFoundOrderException): ResponseEntity<ErrorDto> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body<ErrorDto>(ErrorDto(404, "Order Not Found", exception.message))
    }

    @ExceptionHandler(OrderCreateException::class)
    fun handleException(exception: OrderCreateException): ResponseEntity<ErrorDto> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorDto(404, "Order create exception", exception.message))
    }

    @ExceptionHandler(OrderCheckoutException::class)
    fun handleException(exception: OrderCheckoutException): ResponseEntity<ErrorDto> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorDto(404, "Order Checkout exception", exception.message))
    }

    @ExceptionHandler(OrderCancelException::class)
    fun handleException(exception: OrderCancelException): ResponseEntity<ErrorDto> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorDto(404, "Order Cancel exception", exception.message))
    }
}