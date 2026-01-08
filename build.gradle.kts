import org.gradle.kotlin.dsl.named
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import kotlin.apply

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.allopen") version "2.2.21"

	id("org.springframework.boot") version "3.5.8"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.polarbookshop"
version = "0.0.1-SNAPSHOT"
description = "API gateway and cross-cutting concerns."

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.springframework.cloud:spring-cloud-starter-config")
	implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")
	implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("org.springframework.session:spring-session-data-redis")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("io.kotest:kotest-assertions-core-jvm:${property("kotest.version")}")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("spring-cloud.version")}")
		mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainers.version")}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named<BootBuildImage>("bootBuildImage") {
	imageName.set(project.name)
	environment.set(
		mapOf(
			"BP_JVM_VERSION" to "21.*"
		)
	)

	docker.apply {
		publishRegistry.apply {
			username.set(project.findProperty("registryUsername") as String?)
			password.set(project.findProperty("registryToken") as String?)
			url.set(project.findProperty("registryUrl") as String?)
		}
	}
}
