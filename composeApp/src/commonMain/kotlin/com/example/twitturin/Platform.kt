package com.example.twitturin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
