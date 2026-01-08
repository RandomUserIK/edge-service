package com.polarbookshop.edgeservice.web.config

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono

@Configuration(proxyBeanMethods = false)
class RateLimiterConfiguration {

	@Bean
	fun keyResolver(): KeyResolver =
		KeyResolver { Mono.just("anonymous") }
}
