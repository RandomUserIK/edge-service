package com.polarbookshop.edgeservice.web.config

import com.polarbookshop.edgeservice.web.WebEndpointsHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration(proxyBeanMethods = false)
class WebEndpointsConfiguration {

	@Bean
	fun webEndpointsHandler() = WebEndpointsHandler()

	@Bean
	fun routerFunction(
		webEndpointsHandler: WebEndpointsHandler,
	) = router {
		GET("/catalog-fallback", webEndpointsHandler::getCatalogFallbacK)
		POST("/catalog-fallback", webEndpointsHandler::catalogFallbacK)
	}
}