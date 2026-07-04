package com.example.twitturin

interface Platform {
    val name: String

    /** True on desktop (JVM) — drives desktop-only chrome like the navigation rail. */
    val isDesktop: Boolean get() = false
}

expect fun getPlatform(): Platform
