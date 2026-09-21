package com.example.puzzlesolver

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform