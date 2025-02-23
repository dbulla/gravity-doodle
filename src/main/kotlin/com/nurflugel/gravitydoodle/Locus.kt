package com.nurflugel.gravitydoodle

import java.util.*
import kotlin.math.sin

/**
 * Created by IntelliJ IDEA.
 * User: Douglas Bullard
 * Date: Nov 15, 2003
 * Time: 11:54:29 PM
 * To change this template use Options | File Templates.
 */
class Locus(var x: Double, var y: Double, var vX: Double, var vY: Double, val mass: Double) {
    //    private var xPeriod: Double
    //    private var yPeriod: Double
    //    private val rand = Random(Instant.now().nano)

    init {
        //        xPeriod = (rand.nextInt(MAX_PERIOD - MIN_PERIOD) + MIN_PERIOD).toDouble() / 1.2
        //        yPeriod = (rand.nextInt(MAX_PERIOD - MIN_PERIOD) + MIN_PERIOD).toDouble() / 1.2
    }

    /** Make the locus move on a periodic schedule - back and forth */
    fun wander(
        screenWidth: Double,
        screenHeight: Double,
        screenCenter: Point,
    ) {
        //        val xFactor = getFactor(xPeriod)
        //        val yFactor = getFactor(yPeriod)

        //        x = screenCenter.x + xFactor * screenCenter.x
        //        y = screenCenter.y + yFactor * screenCenter.y
    }

    private fun clip(value: Int, maxValue: Int): Int {
        return when {
            value < 0        -> 0
            value > maxValue -> maxValue
            else             -> value
        }
    }

    private fun getFactor(period: Double): Double {
        val time = Date().time
        val n = (time.toDouble()) / period
        val s = sin(n + Math.PI * 2)
        return s
    }

    fun setLocation(x: Double, y: Double) {
        this.x = x
        this.y = y
    }


    fun applyForce(fx: Double, fy: Double, dt: Double) {
        val deltaVx = fx / mass * dt
        val deltaVy = fy / mass * dt
        vX += deltaVx
        vY += deltaVy
    }

    fun updatePosition(dt: Double) {
        x += vX * dt
        y += vY * dt
    }

    override fun toString(): String {
        return "x: ${"%.2f".format(x)}, y: ${"%.2f".format(y)}, vX: ${"%.2f".format(vX)}, vY: ${"%.2f".format(vY)}"
    }

    companion object {
        // in milliseconds
        private const val MIN_PERIOD = 3000
        private const val MAX_PERIOD = 60000
    }
}