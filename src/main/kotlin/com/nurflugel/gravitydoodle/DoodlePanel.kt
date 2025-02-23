package com.nurflugel.gravitydoodle

//import com.nurflugel.gravitydoodle.SwingWorker.get
import com.nurflugel.gravitydoodle.Constants.Companion.G
import com.nurflugel.gravitydoodle.Constants.Companion.dt
import com.nurflugel.gravitydoodle.Constants.Companion.sunIsImmobile
import java.awt.*
import java.awt.RenderingHints.KEY_ANTIALIASING
import java.awt.RenderingHints.VALUE_ANTIALIAS_ON
import java.awt.event.*
import java.awt.geom.GeneralPath
import java.awt.print.PageFormat
import java.awt.print.Printable
import java.awt.print.PrinterException
import java.time.Instant
import javax.swing.JPanel
import javax.swing.SwingWorker
import kotlin.math.sqrt
import kotlin.random.Random


class DoodlePanel(val theFrame: DoodleFrame, val uiManager: UiManager) : JPanel(true), MouseListener, MouseMotionListener, Printable {
    private var doodleWidth = 0
    private var doodleHeight = 0
    private var rayColor = Color(150, 0, 0)
    private var numPointsPerSide: Int = INITIAL_RAYS_VALUE
    private var sides: List<Side> = listOf()
    private var locusList: MutableList<Locus> = mutableListOf()
    private var selectedLocus: Locus? = null
    private lateinit var worker: SwingWorker<String, Any>

    private val rand = Random(Instant.now().nano)

    companion object {
        private const val LOCUS_POINT_RADIUS: Int = 10
        private const val MIN_LOCUS_DISTANCE: Int = 25
        const val XOFFSET = 0 // no border offset
        const val YOFFSET = 0
    }

    init {
        addMouseListener(this)
        addMouseMotionListener(this)
    }

    fun wander() {
        // Time step
        worker = object : SwingWorker<String, Any>() {
            override fun doInBackground(): String? {
                while (uiManager.isWandering) {
                    if (locusList.isNotEmpty()) {
                        for (j in locusList.indices) {
                            for (k in j + 1..<locusList.size) {
                                val p1 = locusList[j]
                                val p2 = locusList[k]

                                val dx = p2.x - p1.x
                                val dy = p2.y - p1.y
                                val distSq = dx * dx + dy * dy
                                val force = G * p1.mass * p2.mass / distSq

                                val fx = force * dx / sqrt(distSq)
                                val fy = force * dy / sqrt(distSq)

                                p1.applyForce(fx, fy, dt)
                                p2.applyForce(-fx, -fy, dt)
                            }
                        }
                        locusList.forEachIndexed { i, locus ->
                            if (i > 0 || !sunIsImmobile) locus.updatePosition(dt)
                        }
                    }
                    repaint()
                }
                return "Success"
            }
        }
        worker.execute()
    }

    private fun drawInnerStuffForLocus(graphics2D: Graphics2D, locus: Locus) {
        val numPoints = points.size
        val hintsMap: MutableMap<RenderingHints.Key, Any> = mutableMapOf()

        hintsMap[KEY_ANTIALIASING] = VALUE_ANTIALIAS_ON
        graphics2D.addRenderingHints(hintsMap)
        var point: Point?
        (0 until numPoints step 2).forEach {
            val path = GeneralPath()
            path.moveTo(locus.x, locus.y)

            point = points[it]
            path.lineTo(point.x, point.y)

            point = when (it) {
                numPoints - 1 -> points[0]
                else          -> points[it + 1]
            }

            path.lineTo(point.x, point.y)
            path.closePath()
            graphics2D.setXORMode(background)
            graphics2D.fill(path)
        }
    }

    private val points: Array<Point>
        get() {
            return sides
                .map { it.points }
                .map { it.toList() }
                .flatten()
                .toTypedArray<Point>()
        }

    /** Invoked when the mouse button has been clicked (pressed and released) on a component.  */
    override fun mouseClicked(e: MouseEvent) {
        initializePoints()
        // check to see if full screen mode is requested
        if (e.isMetaDown) {
            theFrame.invertControlPanelVisibility() // use the OS full screen mechanism
        }
        else {
            if (uiManager.isAddLocusMode) {
                val point = e.point
                val newLocus = when (locusList.isEmpty()) {
                    true -> Locus(point.getX(), point.getY(), 0.0, 0.0, rand.nextDouble() * Constants.STELLAR_MASS)
                    else -> {

                        Locus(
                            point.getX(),
                            point.getY(),
                            (rand.nextDouble() - 0.5) * rand.nextDouble() * 10.5,
                            (rand.nextDouble() - 0.5) * rand.nextDouble() * 10.5,
                            (rand.nextDouble() * Constants.PLANETARY_MASS) // significant mass, or use 100 for no mass
                        )
                    }
                }

                locusList.add(newLocus)
            }
            else {
                val x = e.x
                val y = e.y
                val numLocusPoints: Int = locusList.size

                for (i in 0..<numLocusPoints) {
                    determineSelectedLocusPoint(i, x, y)
                }
            }
        }

        repaint()
    }

    fun setNumPoints(numPointsPerSide: Int) {
        this.numPointsPerSide = numPointsPerSide
        initializePoints()
        repaint()
    }

    fun clear() {
        locusList.clear()
        repaint()
    }

    override fun mouseDragged(e: MouseEvent) {
        if (uiManager.isMoveLocusMode) {
            val x = e.x
            val y = e.y

            for (i in 0..<locusList.size) {
                determineSelectedLocusPoint(i, x, y)
                selectedLocus?.setLocation(x.toDouble(), y.toDouble())
                repaint()
                break
            }
        }
    }

    private fun determineSelectedLocusPoint(i: Int, x: Int, y: Int) {
        val locusPoint: Locus = locusList[i]

        val deltaX = (locusPoint.x - x)
        val deltaY = (locusPoint.y - y)

        if (((deltaX * deltaX) + (deltaY * deltaY)) < MIN_LOCUS_DISTANCE) {
            selectedLocus = locusPoint
        }
    }

    /** Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.  */
    override fun mouseMoved(e: MouseEvent) {
        if (uiManager.isMoveLocusMode) {
            val x = e.x
            val y = e.y

            selectedLocus = null

            for (i in 0..<locusList.size) {
                val locusPoint: Locus = locusList[i]
                val deltaX = (locusPoint.x - x)
                val deltaY = (locusPoint.y - y)

                if (((deltaX * deltaX) + (deltaY * deltaY)) < MIN_LOCUS_DISTANCE) {
                    selectedLocus = locusPoint
                    repaint()

                    break
                }
            }
        }
    }

    private fun drawLocusPoint(graphics2D: Graphics2D, locus: Locus, index: Int) {
        //        if (theFrame.isMoveLocusMode) {
        //            val theSelectedLocus: Locus? = getSelectedLocus()

        //            if (locus == theSelectedLocus) {

        graphics2D.setPaintMode()

        val oldColor = graphics2D.color

        graphics2D.color = Color.black
        //                graphics2D.color = Color.red
        val radius = when (index) {
            0    -> LOCUS_POINT_RADIUS * 2
            else -> LOCUS_POINT_RADIUS
        }
        graphics2D.fillArc(
            (locus.x - (radius / 2.0)).toInt(),
            (locus.y - (radius / 2.0)).toInt(),
            radius,
            radius,
            0,
            360
        )
        graphics2D.color = oldColor

        //            }
        //        }
    }

    private fun getSelectedLocus(): Locus? {
        return selectedLocus
    }

    /** Create the points around the perimeter of the drawing */
    private fun initializePoints() {
        sides = when {
            uiManager.frameBorderButton.isSelected -> {
                val sides0 = Side(Point(XOFFSET, YOFFSET), Point(XOFFSET + doodleWidth, YOFFSET), numPointsPerSide)
                val sides1 = Side(Point(XOFFSET + doodleWidth, YOFFSET), Point(XOFFSET + doodleWidth, YOFFSET + doodleHeight), numPointsPerSide)
                val sides2 = Side(Point(XOFFSET + doodleWidth, YOFFSET + doodleHeight), Point(XOFFSET, YOFFSET + doodleHeight), numPointsPerSide)
                val sides3 = Side(Point(XOFFSET, YOFFSET + doodleHeight), Point(XOFFSET, YOFFSET), numPointsPerSide)
                listOf(sides0, sides1, sides2, sides3)
            }

            else                                   -> {
                //      val circularSide=CircularSide(doodleHeight/2.0, Point(doodleWidth/2.0, doodleHeight/2.0),nuwmPointsPerSide)
                val circularSide = CircularSide(doodleWidth * 2.0, Point(doodleWidth / 2.0, doodleHeight / 2.0), numPointsPerSide)
                listOf(circularSide)
            }
        }
    }

    override fun paint(g: Graphics) {
        val graphics2D = g as Graphics2D
        var locus: Locus

        //        isPrinting = theFrame.isPrinting
        doodleWidth = width - (2 * XOFFSET)
        doodleHeight = height - (2 * YOFFSET)

        drawBounds(graphics2D)

        // Draw the "rays"
        (0..<locusList.size).forEach {
            locus = locusList[it]
            if (locus.x > 0 && locus.x < doodleWidth && locus.y > 0 && locus.y < doodleHeight) {
                drawInnerStuffForLocus(graphics2D, locus)
            }
        }
        // do this last so the XOR stuff doesn't make it wierd
        (0..<locusList.size).forEach {
            locus = locusList[it]
            if (locus.x > 0 && locus.x < doodleWidth && locus.y > 0 && locus.y < doodleHeight) {
                drawLocusPoint(graphics2D, locus, it)
            }
        }
        drawBorder(graphics2D)
    }

    private fun drawBorder(graphics2D: Graphics2D) {
        graphics2D.setPaintMode()
        graphics2D.color = foreground

        var rectangle = Rectangle(0, 0, doodleWidth + (2 * XOFFSET), YOFFSET)
        graphics2D.fill(rectangle)

        rectangle = Rectangle(doodleWidth + XOFFSET, YOFFSET, XOFFSET, doodleHeight)
        graphics2D.fill(rectangle)

        rectangle = Rectangle(0, doodleHeight + YOFFSET, doodleWidth + (2 * XOFFSET), YOFFSET)
        graphics2D.fill(rectangle)

        rectangle = Rectangle(0, YOFFSET, XOFFSET, doodleHeight)
        graphics2D.fill(rectangle)
    }

    @Suppress("DuplicatedCode")
    private fun drawBounds(graphics2D: Graphics2D): Graphics2D {
        var rectangle = Rectangle(0, 0, doodleWidth + (2 * XOFFSET), doodleHeight + (2 * YOFFSET))

        graphics2D.fill(rectangle)

        rectangle = Rectangle(XOFFSET, YOFFSET, doodleWidth, doodleHeight)

        graphics2D.color = getBackground()
        graphics2D.fill(rectangle)
        graphics2D.color = foreground
        graphics2D.draw(rectangle)

        return graphics2D
    }

    override fun getBackground(): Color {
        //        var background = when {
        //            super.getBackground() == null -> Color.white
        //            !isPrinting                   -> super.getBackground()
        //            else                          -> Color.white
        //        }
        //        background = Color.red
        //        background = rayColor
        //        return background
        return rayColor
    }

    /** Invoked when the mouse enters a component.  */
    override fun mouseEntered(e: MouseEvent) {
        // System.out.println("DoodlePanel.mouseEntered");
    }

    /** Invoked when the mouse exits a component.  */
    override fun mouseExited(e: MouseEvent) {
        // System.out.println("DoodlePanel.mouseExited");
    }

    /** Invoked when a mouse button has been pressed on a component.  */
    override fun mousePressed(e: MouseEvent) {
        // System.out.println("DoodlePanel.mousePressed");
    }

    /** Invoked when a mouse button has been released on a component.  */
    override fun mouseReleased(e: MouseEvent) {
        // System.out.println("DoodlePanel.mouseReleased");
    }

    fun refresh() {
        initializePoints()
        invalidate()
        repaint()
    }

    @Throws(PrinterException::class)
    override fun print(g: Graphics, pf: PageFormat, pi: Int): Int {
        if (pi >= 1) {
            return Printable.NO_SUCH_PAGE
        }

        paint(g)

        return Printable.PAGE_EXISTS
    }
}