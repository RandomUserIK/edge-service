package com.polarbookshop.edgeservice.user

import com.polarbookshop.edgeservice.config.SecurityConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.oidc.StandardClaimNames
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@WebFluxTest(UserController::class)
@Import(SecurityConfiguration::class)
internal class UserControllerTest @Autowired constructor(
	private val webClient: WebTestClient,
) {
	@MockitoBean
	private lateinit var clientRegistrationRepository: ReactiveClientRegistrationRepository

	@Test
	fun whenNotAuthenticatedThen401() {
		// WHEN
		webClient
			.get()
			.uri("/user")
			.exchange()
			.expectStatus()
			// THEN
			.isUnauthorized
	}

	@Test
	fun whenAuthenticatedThenReturnUser() {
		// GIVEN
		val expectedUser = User(
			username = "john.doe",
			firstName = "John",
			lastName = "Doe",
			roles = listOf("employee", "customer"),
		)

		// WHEN
		webClient
			.mutateWith(configureMockOidcLogin(expectedUser))
			.get()
			.uri("/user")
			.exchange()
			.expectStatus()
			.is2xxSuccessful
			.expectBody<User>()
			.isEqualTo(expectedUser)
	}

	private fun configureMockOidcLogin(expectedUser: User): SecurityMockServerConfigurers.OidcLoginMutator =
		SecurityMockServerConfigurers.mockOidcLogin().idToken {
			it.claim(StandardClaimNames.PREFERRED_USERNAME, expectedUser.username)
			it.claim(StandardClaimNames.GIVEN_NAME, expectedUser.firstName)
			it.claim(StandardClaimNames.FAMILY_NAME, expectedUser.lastName)
			it.claim("roles", expectedUser.roles)
		}
}