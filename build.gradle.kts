import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.anies.try"
version = "1.0-SNAPSHOT"

buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.2.0"

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlin_version))
    }

}

apply {
    plugin("kotlin")
}

val kotlin_version: String by extra

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlinModule("stdlib-jdk8", kotlin_version))
    compile(group = "org.http4k", name = "http4k-core", version = "3.3.1")
    compile(group = "org.http4k", name = "http4k-server-jetty", version = "3.3.1")
    compile(group = "org.http4k", name = "http4k-client-okhttp", version = "3.3.1")
    compile(group = "org.http4k", name = "http4k-client-websocket", version = "3.3.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

