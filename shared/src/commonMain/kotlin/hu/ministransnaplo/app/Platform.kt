package hu.ministransnaplo.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform