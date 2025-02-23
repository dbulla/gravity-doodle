package com.nurflugel.gravitydoodle

data class Point(var x: Double, var y: Double) {
    constructor(xx: Int, yy: Int) : this(xx.toDouble(), yy.toDouble())
}
