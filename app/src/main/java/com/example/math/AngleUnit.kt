package com.example.math

enum class AngleUnit(val label: String, val fullDescription: String) {
    DEG("DEG", "Grados sexagesimales (360°)"),
    RAD("RAD", "Radianes (2π)"),
    GRAD("GRAD", "Gradianes (400ᵍ)");

    fun toRadians(value: Double): Double {
        return when (this) {
            DEG -> Math.toRadians(value)
            RAD -> value
            GRAD -> value * (Math.PI / 200.0)
        }
    }

    fun fromRadians(radians: Double): Double {
        return when (this) {
            DEG -> Math.toDegrees(radians)
            RAD -> radians
            GRAD -> radians * (200.0 / Math.PI)
        }
    }

    fun next(): AngleUnit {
        return when (this) {
            DEG -> RAD
            RAD -> GRAD
            GRAD -> DEG
        }
    }
}
