package com.example.rachapro.backend.shared.tracing

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestIdFilter : Filter {

    companion object {
        const val HEADER_NAME = "X-Request-Id"
        const val MDC_KEY = "requestId"
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val requestId = httpRequest.getHeader(HEADER_NAME)?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()

        MDC.put(MDC_KEY, requestId)
        httpResponse.setHeader(HEADER_NAME, requestId)

        try {
            chain.doFilter(request, response)
        } finally {
            MDC.remove(MDC_KEY)
        }
    }
}
