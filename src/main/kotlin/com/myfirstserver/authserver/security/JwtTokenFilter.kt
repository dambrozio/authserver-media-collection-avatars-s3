package com.myfirstserver.authserver.security

import jakarta.servlet.FilterChain
import jakarta.servlet.GenericFilter
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class JwtTokenFilter(private val jwt: Jwt) : GenericFilter() {
    override fun doFilter(
        req: ServletRequest,
        res: ServletResponse,
        chain: FilterChain
    ) {
        val auth = jwt.extract(req as HttpServletRequest)
        if (auth != null) SecurityContextHolder.getContext().authentication = auth
        chain.doFilter(req, res)
    }
}