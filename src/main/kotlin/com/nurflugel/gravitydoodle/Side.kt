package com.nurflugel.gravitydoodle



/** Representation of a side of the doodle panel - a straight line */
open class Side
    (
    startPoint: Point,
    endPoint: Point,
    numPointsPerSide: Int,
) {
    constructor() : this(Point(0.0, 0.0), Point(0.0, 0.0), numPointsPerSide = 0)

    var points: List<Point>

    init {
        val deltaX = endPoint.x - startPoint.x
        val deltaY = endPoint.y - startPoint.y

        val xIncrement = deltaX / (numPointsPerSide - 1).toDouble()
        val yIncrement = deltaY / (numPointsPerSide - 1).toDouble()
        var x: Double
        var y: Double

        points = (0..<(numPointsPerSide - 1)).map { i ->
            x = startPoint.x + (xIncrement * i.toDouble())
            y = startPoint.y + (yIncrement * i.toDouble())
            Point(x, y)
        }
    }
}