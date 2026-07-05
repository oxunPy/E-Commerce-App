package com.simplecoding.apigateway

import org.springframework.web.server.ResponseStatusException
import java.util.function.Predicate

class HttpInternalServicePredicate : Predicate<ResponseStatusException> {
    override fun test(t: ResponseStatusException): Boolean {
        return t.statusCode.is5xxServerError
    }
}