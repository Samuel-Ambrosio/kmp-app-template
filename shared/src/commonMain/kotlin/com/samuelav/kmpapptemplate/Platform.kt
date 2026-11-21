package com.samuelav.kmpapptemplate

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform