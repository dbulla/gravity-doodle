package com.nurflugel.gravitydoodle

class Line
/** m=(y1-y2)/(x1-x2) b=y-mx. */
internal constructor(private val start: Point, private val end: Point) {
    private val m: Double = when (start.x) {
        end.x -> Double.MAX_VALUE
        else  -> (start.y - end.y) / (start.x - end.x)
    }
    private val b: Double = start.y - m * start.x

    val midpoint: Point
        get() {
            val x = (start.x + end.x) / 2
            val y = (start.y + end.y) / 2

            return Point(x, y)
        }

    private fun getY(x: Double): Double {
        return m * x + b
    }

    /**
     * find the interception point with another line.
     *
     * y=m1 x + b1 y=m2 x + b2
     *
     * x = (b2-b1)/(m1-m2)
     */
    fun findIntercept(otherLine: Line): Point {
        val x = (otherLine.b - b) / (m - otherLine.m)
        val y = getY(x)

        return Point(x, y)
    }

    override fun toString(): String {
        return "m: $m; $start-$end"
    }
}
