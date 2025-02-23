package com.nurflugel.gravitydoodle

import com.nurflugel.gravitydoodle.Line

/** Representation of the spine of the doodle.
 * @param  center            the center of the spine
 * @param  length            the length
 * @param  angle             in degrees
 * @param  numPointsPerSide  the number of points per spine - must be the same for all
 */
class Spine
    (
    center: Point,
    length: Double,
    private val angle: Double,
    private val numPointsPerSide: Int,
) {
    var points: List<Point>

    init {
        val angleInRadians = Math.toRadians(angle)
        val deltaLength = length / numPointsPerSide

        points = (0..numPointsPerSide).map { i ->
            val x = center.x + (deltaLength * i * StrictMath.cos(angleInRadians))
            val y = center.y + (deltaLength * i * StrictMath.sin(angleInRadians))
            Point(x, y)
        }
    }

    fun getLine(): Line {
        return Line(points[0], points[points.size - 1])
    }

    override fun toString(): String {
        return "Spine angle: $angle, numPointsPerSide: $numPointsPerSide"
    }
}