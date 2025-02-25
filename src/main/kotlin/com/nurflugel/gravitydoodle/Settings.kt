package com.nurflugel.gravitydoodle

import java.awt.Color
import java.util.prefs.Preferences

class Settings {


    val bigPlanetaryMass = 1e14
    val regularPlanetaryMass = 1e12

    val stellarMass = 1e14
    val dt = .000001
    val G: Double = 6.6743e-11 // Gravitational constant
    val rayColor = Color(150, 0, 0)

    val minRaysValue = 2
    val maxRaysValue = 200
    val initialRaysValue = 80

    // todo store these when closing, restore when init
    var firstPointIsSun = true
    var bigPlanets = false
    var drawRays = true
    var numberOfRays = initialRaysValue
    private val preferences: Preferences = Preferences.userNodeForPackage(Settings::class.java)

    /** Return the correct planetary mass  based on the checkbox's value */
    fun getPlanetaryMass(): Double {
        return when (bigPlanets) {
            true -> bigPlanetaryMass
            else -> regularPlanetaryMass
        }
    }

    fun restoreData() {
        firstPointIsSun = preferences.getBoolean("firstPointIsSun", true)
        bigPlanets = preferences.getBoolean("BigPlanetaryMass", false)
        numberOfRays = preferences.getInt("numberOfRays", initialRaysValue)
        drawRays = preferences.getBoolean("DrawRays", true)
    }

    fun storeData() {
        preferences.putBoolean("firstPointIsSun", firstPointIsSun)
        preferences.putBoolean("bigPlanets", bigPlanets)
        preferences.putInt("numberOfRays", numberOfRays)
        preferences.putBoolean("DrawRays", drawRays)
    }

    init {
        restoreData()
    }
}