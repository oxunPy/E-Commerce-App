package com.simplecoding.apigateway

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/fallback")
class FallbackController {

    @GetMapping("/order-fallback")
    fun orderFallback(): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("Hozirda buyurtma xizmati vaqtincha ishlamayapti. Iltimos, birozdan so'ng qayta urining.")
    }

    @GetMapping("/inventory-fallback")
    fun inventoryFallback(): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("Hozirda inventar xizmati vaqtincha ishlamayapti. Iltimos, birozdan so'ng qayta urining.")
    }

    @GetMapping("/notification-fallback")
    fun notificationFallback(): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("Hozirda eslatma xizmati vaqtincha ishlamayapti. Iltimos, birozdan so'ng qayta urining.")
    }

    @GetMapping("/payment-fallback")
    fun paymentFallback(): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("Hozirda to'lov xizmati vaqtincha ishlamayapti. Iltimos, birozdan so'ng qayta urining.")
    }

    @GetMapping("/product-fallback")
    fun productFallback(): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("Hozirda mahsulot xizmati vaqtincha ishlamayapti. Iltimos, birozdan so'ng qayta urining.")
    }
}