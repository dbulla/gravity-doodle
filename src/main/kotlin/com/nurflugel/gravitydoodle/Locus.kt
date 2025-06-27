package com.nurflugel.gravitydoodle


/**
 * Represents a locus in a two-dimensional space with position, velocity, and mass attributes.
 *
 * @property x The x-coordinate of the locus.
 * @property y The y-coordinate of the locus.
 * @property vX The velocity of the locus along the x-axis.
 * @property vY The velocity of the locus along the y-axis.
 * @property mass The mass of the locus.
 * @property dt Time delta used for calculations.
 * @property dtOverMass Precomputed value of `dt / mass` for optimized calculations.
 */
class Locus(var x: Double,
            var y: Double,
            var vX: Double,
            var vY: Double,
            val mass: Double,
            val dt: Double) {
    //    Save a little CPU time by doing the division once
    val dtOverMass = dt / mass

    fun setLocation(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    fun applyForce(fx: Double, fy: Double) {
        vX += fx * dtOverMass
        vY += fy * dtOverMass
    }

    fun updatePosition(dt: Double) {
        x += vX * dt
        y += vY * dt
    }

    fun bounce(doodleWith: Int, doodleHeight: Int) {
        if (x > doodleWith) vX = -vX
        if (x < 0) vX = -vX
        if (y > doodleHeight) vY = -vY
        if (y < 0) vY = -vY
    }

    override fun toString(): String {
        return "x: ${"%.2f".format(x)}, y: ${"%.2f".format(y)}, vX: ${"%.2f".format(vX)}, vY: ${"%.2f".format(vY)}"
    }
}