package com.polarbookshop.edgeservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository
import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
@Configuration(proxyBeanMethods = false)
class SecurityConfiguration {

	@Bean
	fun springSecurityFilterChain(
		http: ServerHttpSecurity,
		clientRegistrationRepository: ReactiveClientRegistrationRepository,
	): SecurityWebFilterChain =
		http
			.authorizeExchange {
				it
					.pathMatchers("/", "/*.css", "/*.js", "/favicon.ico").permitAll()
					.pathMatchers(HttpMethod.GET, "/books/**").permitAll()
					.anyExchange()
					.authenticated()
			}
			.exceptionHandling {
				it.authenticationEntryPoint(
					HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED),
				)
			}
			.oauth2Login(Customizer.withDefaults())
			.logout {
				it.logoutSuccessHandler(
					oidcLogoutSuccessHandler(clientRegistrationRepository)
				)
			}
			.csrf {
				// it.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
				// it.csrfTokenRequestHandler(SpaServerCsrfTokenRequestHandler())
				it.disable()
			}
			.build()

	@Bean
	fun csrfCookieWebFilter(): WebFilter =
		WebFilter { exchange, chain ->
			exchange.getAttributeOrDefault(CsrfToken::class.java.name, Mono.empty<CsrfToken>()).subscribe()
			chain.filter(exchange)
		}

	@Bean
	fun authorizedClientRepository() =
		WebSessionServerOAuth2AuthorizedClientRepository()

	private fun oidcLogoutSuccessHandler(
		clientRegistrationRepository: ReactiveClientRegistrationRepository,
	): ServerLogoutSuccessHandler =
		OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository)
			.apply { setPostLogoutRedirectUri("{baseUrl}") }

	internal class SpaServerCsrfTokenRequestHandler : ServerCsrfTokenRequestAttributeHandler() {
		private val delegate: ServerCsrfTokenRequestAttributeHandler = XorServerCsrfTokenRequestAttributeHandler()

		override fun handle(exchange: ServerWebExchange, csrfToken: Mono<CsrfToken>) {
			/*
			 * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of the CsrfToken when it is rendered in the response body.
			 */
			delegate.handle(exchange, csrfToken)
		}

		override fun resolveCsrfTokenValue(exchange: ServerWebExchange, csrfToken: CsrfToken): Mono<String> {
			val hasHeader = exchange.request.headers[csrfToken.headerName]
				?.count { it == csrfToken.headerName }
				?.let { it > 0 } ?: false

			val hasCookie = exchange.request.cookies[csrfToken.headerName]
				?.count { it.name == csrfToken.headerName }
				?.let { it > 0 } ?: false

			return if (hasHeader || hasCookie) super.resolveCsrfTokenValue(exchange, csrfToken)
			else delegate.resolveCsrfTokenValue(exchange, csrfToken)
		}
	}
}
