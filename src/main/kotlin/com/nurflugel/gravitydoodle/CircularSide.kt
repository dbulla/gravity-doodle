package com.nurflugel.gravitydoodle

import kotlin.math.cos
import kotlin.math.sin

/** A single "side" - it's a circle, with numPointsPerSide points */
class CircularSide(radius: Double, center: Point, numPointsPerSide: Int) : Side() {

    /** From the center point, draw an arc in a clockwise manner, subdividing that arc numPointsPerSide times */
    init {
        // first, determine the geometry of the circle -
        val deltaTheta = 360.0 / numPointsPerSide
        points = (0..numPointsPerSide).map { i ->
            val theta = i * deltaTheta
            val x = center.x - radius * cos(Math.toRadians(theta))
            val y = center.y - radius * sin(Math.toRadians(theta))
            Point(x, y)
        }
    }
}