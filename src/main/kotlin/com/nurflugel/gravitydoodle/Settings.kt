package com.nurflugel.gravitydoodle

import java.awt.Color
import java.time.Instant
import java.util.prefs.Preferences
import kotlin.random.Random

class Settings {


    val bigPlanetaryMass = 1e14
    val regularPlanetaryMass = 1e12

    val regularStellarMass = 1e14
    val bigStellarMass = 1e15

    val dt = .000001
    val G: Double = 6.6743e-11 // Gravitational constant

    val rayColor = Color(150, 0, 0)
    private val rand = Random(Instant.now().nano)
    val minRaysValue = 2
    val maxRaysValue = 200
    val initialRaysValue = 80

    var firstPointIsSun = true
    var bigPlanets = false
    var bigSun = false
    var drawRays = true
    var planetsInteractWithEachOther=true
    var numberOfRays = initialRaysValue
    private val preferences: Preferences = Preferences.userNodeForPackage(Settings::class.java)

    /** Return the correct planetary mass  based on the checkbox's value */
    fun getPlanetaryMass(): Double {
        return when (bigPlanets) {
            true -> rand.nextDouble() *bigPlanetaryMass + 1e11
            else -> rand.nextDouble() *regularPlanetaryMass + 1e11
        }
    }

    /** Return the correct planetary mass  based on the checkbox's value */
    fun getStellarMass(): Double {
        return when (bigSun) {
            true -> regularStellarMass
            else -> bigStellarMass
        }
    }

    fun restoreData() {
        firstPointIsSun = preferences.getBoolean("firstPointIsSun", true)
        bigPlanets = preferences.getBoolean("BigPlanetaryMass", false)
        numberOfRays = preferences.getInt("numberOfRays", initialRaysValue)
        drawRays = preferences.getBoolean("DrawRays", true)
        bigSun = preferences.getBoolean("BigSun", false)
        planetsInteractWithEachOther=preferences.getBoolean("planetsInteractWithEachOther", true)
    }

    fun storeData() {
        preferences.putBoolean("firstPointIsSun", firstPointIsSun)
        preferences.putBoolean("bigPlanets", bigPlanets)
        preferences.putInt("numberOfRays", numberOfRays)
        preferences.putBoolean("DrawRays", drawRays)
        preferences.putBoolean("BigSun", bigSun)
        preferences.putBoolean("planetsInteractWithEachOther", planetsInteractWithEachOther)
    }

    init {
        restoreData()
    }
}