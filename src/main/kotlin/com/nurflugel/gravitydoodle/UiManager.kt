package com.nurflugel.gravitydoodle

import java.awt.*
import java.awt.Font.PLAIN
import java.awt.GridBagConstraints.HORIZONTAL
import java.awt.GridBagConstraints.NORTH
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseWheelEvent
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.BorderFactory.createTitledBorder
import javax.swing.BoxLayout.Y_AXIS
import javax.swing.border.EtchedBorder
import kotlin.system.exitProcess

private const val MIN_POINTS_VALUE = 2
private const val MAX_POINTS_VALUE = 200
const val INITIAL_POINTS_VALUE = 80

/**
 * @author Douglas Bullard
 */
class UiManager(val doodleFrame: DoodleFrame) : JPanel(BorderLayout()), KeyListener {
    private val addLocusPointsRadioButton = JRadioButton("Add New Locus Points")
    val frameBorderButton = JRadioButton("Frame Border")
    private val circularBorderButton = JRadioButton("Circular Border")
    private val moveLocusPointsRadioButton = JRadioButton("Move Locus Points")
    private val removeLocusPointsRadioButton = JRadioButton("Remove Locus Points")
    private val fixedModeRadioButton = JRadioButton("Fixed Mode")
    val wanderModeRadioButton = JRadioButton("Wander Mode")

    private val sunIsImmobileCheckbox = JCheckBox("Sun is Immobile", Constants.sunIsImmobile)

    private var addMoveRemoteButtonGroup = ButtonGroup()
    private var fixedWanderButtonGroup = ButtonGroup()
    private var borderButtonGroup = ButtonGroup()

    private val borderButtonPanel = JPanel()
    private val fixedWanderModePanel = JPanel()
    private val locusRadioButtonPanel = JPanel()
    private val contentPanel = JPanel()

    private val clearButton = JButton("Clear")
    private val quitButton = JButton("Quit")
    private val printButton = JButton("Print")
    private lateinit var doodlePanel: DoodlePanel

    private val numberOfEdgePointsLabel = JLabel("Number of Edge Points: ")
    private val numberOfEdgePointsSpinner = JSpinner(SpinnerNumberModel(INITIAL_POINTS_VALUE, MIN_POINTS_VALUE, MAX_POINTS_VALUE, 1))

    /**
     * Creates new form ControlPanel
     */
    init {
        initComponents()
        layoutComponents()
        addActionListeners()
    }


    private inner class ButtonKeyPressListener : KeyAdapter() {
        override fun keyPressed(e: KeyEvent) {
            println("..e.keyCode = ${e.keyCode}")
            val currentValue = numberOfEdgePointsSpinner.value.toString().toInt()
            when (e.keyCode) {
                // "-"
                45 -> if (currentValue > MIN_POINTS_VALUE) numberOfEdgePointsSpinner.value = numberOfEdgePointsSpinner.previousValue
                // "+"
                61 -> if (currentValue < MAX_POINTS_VALUE) numberOfEdgePointsSpinner.value = numberOfEdgePointsSpinner.nextValue
            }
        }
    }

    private fun initComponents() {
        doodlePanel = doodleFrame.getDoodlePanel()
        add(contentPanel)
        layout = GridBagLayout()

        addKeyListener(this)

        locusRadioButtonPanel.layout = GridLayout(3, 1)
        locusRadioButtonPanel.border = EtchedBorder()
        addLocusPointsRadioButton.isSelected = true

        locusRadioButtonPanel.add(addLocusPointsRadioButton)
        locusRadioButtonPanel.add(moveLocusPointsRadioButton)
        locusRadioButtonPanel.add(removeLocusPointsRadioButton)

        borderButtonPanel.layout = BoxLayout(borderButtonPanel, Y_AXIS)
        borderButtonPanel.border = createTitledBorder(EtchedBorder(), "Doodle Boundary")
        borderButtonPanel.add(frameBorderButton)
        borderButtonPanel.add(circularBorderButton)
        borderButtonGroup.add(frameBorderButton)
        borderButtonGroup.add(circularBorderButton)
        circularBorderButton.isSelected = true

        fixedWanderModePanel.layout = BoxLayout(fixedWanderModePanel, Y_AXIS)
        fixedWanderModePanel.border = createTitledBorder(EtchedBorder(), "Movement")
        fixedWanderModePanel.add(fixedModeRadioButton)
        fixedWanderModePanel.add(wanderModeRadioButton)
        fixedModeRadioButton.isSelected = true
        fixedWanderButtonGroup.add(fixedModeRadioButton)
        fixedWanderButtonGroup.add(wanderModeRadioButton)

        numberOfEdgePointsSpinner.toolTipText = "Controls how many points per side"

        addMoveRemoteButtonGroup.add(addLocusPointsRadioButton)
        addMoveRemoteButtonGroup.add(moveLocusPointsRadioButton)
        addMoveRemoteButtonGroup.add(removeLocusPointsRadioButton)

    }

    private fun addActionListeners() {
        clearButton.addActionListener { doodleFrame.getDoodlePanel().clear() }
        quitButton.addActionListener { exitProcess(0) }
        wanderModeRadioButton.addActionListener { doodleFrame.getDoodlePanel().wander() }
        fixedModeRadioButton.addActionListener { setWandering(false) }
        numberOfEdgePointsSpinner.addChangeListener { doodleFrame.getDoodlePanel().setNumPoints(numberOfEdgePointsSpinner.model.value.toString().toInt()) }
        numberOfEdgePointsSpinner.addMouseWheelListener { numPointsSpinnerMouseWheelMoved(it) }
        printButton.addActionListener { printScreen() }
        frameBorderButton.addActionListener { doodleFrame.getDoodlePanel().refresh() }
        circularBorderButton.addActionListener { doodleFrame.getDoodlePanel().refresh() }
        // need to have _something_ registered to listen for key clicks, as jPanel and jFrame don't weem to work... maybe event thread issue?
        fixedModeRadioButton.addKeyListener(ButtonKeyPressListener())
        sunIsImmobileCheckbox.addActionListener { Constants.sunIsImmobile = sunIsImmobileCheckbox.isSelected }
    }

    /** grid bag stuff */
    private fun layoutComponents() {
        /// left hand side
        addComponent(locusRadioButtonPanel, 0, 0, 2, 2, HORIZONTAL, NORTH)
        addComponent(numberOfEdgePointsLabel, 0, 2, 1, 1, HORIZONTAL, GridBagConstraints.CENTER)
        addComponent(numberOfEdgePointsSpinner, 1, 2, 1, 1, HORIZONTAL, GridBagConstraints.CENTER, 12)

        /// right hand side controls
        var y = 0
        addComponent(sunIsImmobileCheckbox, 2, y++, 1, 1, HORIZONTAL, GridBagConstraints.CENTER)
        addComponent(fixedWanderModePanel, 2, y++, 1, 1, HORIZONTAL, NORTH)
        addComponent(borderButtonPanel, 2, y++, 1, 1, HORIZONTAL, NORTH)
        addComponent(clearButton, 2, y++, 1, 1, HORIZONTAL, NORTH)
        addComponent(printButton, 2, y++, 1, 1, HORIZONTAL, NORTH)
        addComponent(quitButton, 2, y++, 1, 1, HORIZONTAL, NORTH)
    }

    private fun addComponent(component: Component, gridX: Int, gridY: Int, gridWidth: Int, gridHeight: Int, fill: Int, anchor: Int, iPadX: Int = 0, iPadY: Int = 0) {
        val gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = gridX
        gridBagConstraints.gridy = gridY
        gridBagConstraints.gridwidth = gridWidth
        gridBagConstraints.gridheight = gridHeight
        gridBagConstraints.fill = fill
        gridBagConstraints.anchor = anchor
        gridBagConstraints.ipadx = iPadX
        gridBagConstraints.ipady = iPadY
        add(component, gridBagConstraints)
    }

    private fun numPointsSpinnerMouseWheelMoved(e: MouseWheelEvent) {
        val wheelRotation = e.wheelRotation
        val value = (numberOfEdgePointsSpinner.value as Int)

        if (value in MIN_POINTS_VALUE..MAX_POINTS_VALUE) {
            numberOfEdgePointsSpinner.value = value + wheelRotation
            doodlePanel.setNumPoints(wheelRotation)
        }
    }

    fun setWandering(isWandering: Boolean) {
        wanderModeRadioButton.isSelected = isWandering
    }

    val isWandering: Boolean
        get() = wanderModeRadioButton.isSelected

    val isAddLocusMode: Boolean
        get() = addLocusPointsRadioButton.isSelected

    val isMoveLocusMode: Boolean
        get() = moveLocusPointsRadioButton.isSelected

    val isRemoveLocusMode: Boolean
        get() = removeLocusPointsRadioButton.isSelected // End of variables declaration//GEN-END:variables

    private fun printScreen() {
        //        val printJob = PrinterJob.getPrinterJob()
        //
        //        printJob.setPrintable(doodleFrame.getDoodlePanel())
        //
        //        if (printJob.printDialog()) {
        //            try {
        //                isPrinting = true
        //                printJob.print()
        //                isPrinting = false
        //            } catch (ex: Exception) {
        //                ex.printStackTrace()
        //            }
        //        }
        val WIDTH = 2560
        val HEIGHT = 1440
        val localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val bufferedImage = BufferedImage(WIDTH, HEIGHT, TYPE_INT_RGB)
        val graphics2D = localGraphicsEnvironment.createGraphics(bufferedImage)

        val doodlePanel = doodlePanel
        graphics2D.clearRect(0, 0, WIDTH, HEIGHT)
        //        graphics2D.setRenderingHints(aliasedRenderingHints) // todo get this is it works
        graphics2D.font = Font("Helvetica", PLAIN, 13)

        val bImg = BufferedImage(doodlePanel.width, doodlePanel.height, TYPE_INT_RGB)
        val cg = bImg.createGraphics()
        doodlePanel.paintAll(cg)
        //        doodlePanel.paint(cg)
        val imageFile = File("dibble2.png")
        ImageIO.write(bufferedImage, "png", imageFile)
    }

    override fun keyPressed(e: KeyEvent) {
        println(" UiManager:::key pressed: ${e.keyCode}")
    }

    override fun keyTyped(e: KeyEvent) {
        println(" UiManager:::keyTyped: ${e.keyCode}")
    }

    override fun keyReleased(e: KeyEvent) {
        println(" UiManager:::keyReleased: ${e.keyCode}")
    }

}
