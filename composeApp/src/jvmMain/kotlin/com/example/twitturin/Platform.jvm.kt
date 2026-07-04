package com.example.twitturin

class JvmPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val isDesktop: Boolean = true
}

actual fun getPlatform(): Platform = JvmPlatform()
