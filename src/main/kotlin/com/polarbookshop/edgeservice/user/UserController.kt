package com.polarbookshop.edgeservice.user

import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@RestController
class UserController {

	// @GetMapping("user")
	// fun getUser(): Mono<User> =
	// 	ReactiveSecurityContextHolder
	// 		.getContext()
	// 		.map(SecurityContext::getAuthentication)
	// 		.map { it: Authentication ->
	// 			(it.principal as OidcUser).let { oidcUser ->
	// 				User(
	// 					username = oidcUser.preferredUsername,
	// 					firstName = oidcUser.givenName,
	// 					lastName = oidcUser.familyName,
	// 					roles = listOf("employee", "customer"),
	// 				)
	// 			}
	// 		}

	@GetMapping("user")
	fun getUser(
		@AuthenticationPrincipal oidcUser: OidcUser,
	): Mono<User> =
		User(
			username = oidcUser.preferredUsername,
			firstName = oidcUser.givenName,
			lastName = oidcUser.familyName,
			roles = oidcUser.getClaimAsStringList("roles"),
		).toMono()
}
