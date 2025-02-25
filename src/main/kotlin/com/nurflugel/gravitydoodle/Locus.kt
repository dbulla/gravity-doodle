package com.nurflugel.gravitydoodle


/**
 * Created by IntelliJ IDEA.
 * User: Douglas Bullard
 * Date: Nov 15, 2003
 * Time: 11:54:29 PM
 * To change this template use Options | File Templates.
 */
class Locus(var x: Double, var y: Double, var vX: Double, var vY: Double, val mass: Double, dt: Double) {
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

    override fun toString(): String {
        return "x: ${"%.2f".format(x)}, y: ${"%.2f".format(y)}, vX: ${"%.2f".format(vX)}, vY: ${"%.2f".format(vY)}"
    }
}