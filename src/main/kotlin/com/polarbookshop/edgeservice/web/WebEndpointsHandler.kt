package com.polarbookshop.edgeservice.web

import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

class WebEndpointsHandler {

	fun getCatalogFallbacK(request: ServerRequest): Mono<ServerResponse> =
		ok().bodyValue("")

	fun catalogFallbacK(request: ServerRequest): Mono<ServerResponse> =
		ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE.value()).build()
}
