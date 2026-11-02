package com.example.rachapro.backend.security

import com.example.rachapro.backend.shared.tracing.RequestIdFilter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class RequestIdFilterTest {

    private val filter = RequestIdFilter()

    @Test
    fun `request without ID generates and returns one`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = MockFilterChain()

        filter.doFilter(request, response, chain)

        val headerId = response.getHeader(RequestIdFilter.HEADER_NAME)
        assertNotNull(headerId)
        assert(headerId!!.isNotBlank())
    }

    @Test
    fun `request with ID preserves it`() {
        val request = MockHttpServletRequest().apply {
            addHeader(RequestIdFilter.HEADER_NAME, "custom-req-id-123")
        }
        val response = MockHttpServletResponse()
        val chain = MockFilterChain()

        filter.doFilter(request, response, chain)

        assertEquals("custom-req-id-123", response.getHeader(RequestIdFilter.HEADER_NAME))
    }

    @Test
    fun `two independent requests do not share ID`() {
        val req1 = MockHttpServletRequest()
        val res1 = MockHttpServletResponse()
        val req2 = MockHttpServletRequest()
        val res2 = MockHttpServletResponse()

        filter.doFilter(req1, res1, MockFilterChain())
        filter.doFilter(req2, res2, MockFilterChain())

        val id1 = res1.getHeader(RequestIdFilter.HEADER_NAME)
        val id2 = res2.getHeader(RequestIdFilter.HEADER_NAME)

        assertNotNull(id1)
        assertNotNull(id2)
        assertNotEquals(id1, id2)
    }
}
