package com.simplecoding.inventoryservice.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.web.filter.OncePerRequestFilter

class MdcFilter: OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            MDC.put("method", request.method)
            MDC.put("url", request.requestURI)
            MDC.put("client_ip", request.remoteAddr)

            filterChain.doFilter(request, response)
        } finally {
            MDC.clear()
        }
    }
}