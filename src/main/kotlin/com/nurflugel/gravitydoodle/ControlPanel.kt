package com.nurflugel.gravitydoodle

import java.awt.*
import java.awt.GridBagConstraints.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseWheelEvent
import javax.swing.*
import javax.swing.BorderFactory.createTitledBorder
import javax.swing.BoxLayout.Y_AXIS
import javax.swing.border.EtchedBorder
import kotlin.system.exitProcess


/**
 * @author Douglas Bullard
 */
class ControlPanel(val doodleFrame: DoodleFrame, val settings: Settings) : JPanel(BorderLayout()), KeyListener {

    private val addLocusPointsRadioButton = JRadioButton("Add New Locus Points")
    val frameBorderButton = JRadioButton("Frame Border")
    private val circularBorderButton = JRadioButton("Circular Border")
    private val moveLocusPointsRadioButton = JRadioButton("Move Locus Points")
    private val removeLocusPointsRadioButton = JRadioButton("Remove Locus Points")
    private val fixedModeRadioButton = JRadioButton("Fixed Mode")
    val animationModeRadioButton = JRadioButton("Animation Mode")

    private val sunCheckbox = JCheckBox("First point is a sun")
    private val bigPlanetsCheckbox = JCheckBox("Planets are very heavy (wilder movement)")
    private val drawRaysCheckbox = JCheckBox("Draw rays")

    private var addMoveRemoteButtonGroup = ButtonGroup()
    private var fixedWanderButtonGroup = ButtonGroup()
    private var borderButtonGroup = ButtonGroup()

    private val borderButtonPanel = JPanel()
    private val fixedWanderModePanel = JPanel()
    private val locusRadioButtonPanel = JPanel()
    private val contentPanel = JPanel()

    private val clearButton = JButton("Clear")
    private val quitButton = JButton("Quit")
    private lateinit var doodlePanel: DoodlePanel

    private val numberOfRaysLabel = JLabel("Number of Rays: ")
    private val numberOfRaysSpinner = JSpinner(SpinnerNumberModel(settings.initialRaysValue, settings.minRaysValue, settings.maxRaysValue, 1))

    fun initialize() {
        initComponents()
        layoutComponents()
        addActionListeners()
    }


    private inner class ButtonKeyPressListener : KeyAdapter() {
        override fun keyPressed(e: KeyEvent) {
            println("..e.keyCode = ${e.keyCode}")
            val currentValue = numberOfRaysSpinner.value.toString().toInt()
            when (e.keyCode) {
                // "-"
                45 -> if (currentValue > settings.minRaysValue) setSpinnerValue(numberOfRaysSpinner.previousValue)
                // "+"
                61 -> if (currentValue < settings.maxRaysValue) setSpinnerValue(numberOfRaysSpinner.nextValue)
            }
        }
    }

    private fun setSpinnerValue(value: Any) {
        numberOfRaysSpinner.value = value
        settings.numberOfRays = value.toString().toInt()
    }

    private fun initComponents() {
        doodlePanel = doodleFrame.doodlePanel
        add(contentPanel)
        layout = GridBagLayout()

        addKeyListener(this)

        locusRadioButtonPanel.layout = GridLayout(3, 1)
        locusRadioButtonPanel.border = EtchedBorder()
        addLocusPointsRadioButton.isSelected = true

        locusRadioButtonPanel.add(addLocusPointsRadioButton)
        locusRadioButtonPanel.add(moveLocusPointsRadioButton)
        locusRadioButtonPanel.add(removeLocusPointsRadioButton)

        numberOfRaysLabel.horizontalAlignment = SwingConstants.RIGHT
        sunCheckbox.horizontalAlignment = SwingConstants.RIGHT
        sunCheckbox.isSelected = settings.firstPointIsSun
        bigPlanetsCheckbox.isSelected = settings.bigPlanets
        numberOfRaysSpinner.value = settings.numberOfRays

        drawRaysCheckbox.isSelected = settings.drawRays

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
        fixedWanderModePanel.add(animationModeRadioButton)
        fixedModeRadioButton.isSelected = true
        fixedWanderButtonGroup.add(fixedModeRadioButton)
        fixedWanderButtonGroup.add(animationModeRadioButton)

        numberOfRaysSpinner.toolTipText = "Controls how many points per side"

        addMoveRemoteButtonGroup.add(addLocusPointsRadioButton)
        addMoveRemoteButtonGroup.add(moveLocusPointsRadioButton)
        addMoveRemoteButtonGroup.add(removeLocusPointsRadioButton)

    }

    private fun addActionListeners() {
        clearButton.addActionListener { doodlePanel.clear() }
        quitButton.addActionListener {
            exit()
        }
        animationModeRadioButton.addActionListener { doodlePanel.wander() }
        fixedModeRadioButton.addActionListener { setWandering(false) }
        numberOfRaysSpinner.addChangeListener {
            val numPointsPerSide = numberOfRaysSpinner.model.value.toString().toInt()
            doodlePanel.setNumPoints(numPointsPerSide)
            settings.numberOfRays = numPointsPerSide
        }
        numberOfRaysSpinner.addMouseWheelListener { numPointsSpinnerMouseWheelMoved(it) }
        frameBorderButton.addActionListener { doodlePanel.refresh() }
        circularBorderButton.addActionListener { doodlePanel.refresh() }

        // need to have _something_ registered to listen for key clicks, as jPanel and jFrame don't weem to work... maybe event thread issue?
        fixedModeRadioButton.addKeyListener(ButtonKeyPressListener())
        sunCheckbox.addActionListener { settings.firstPointIsSun = sunCheckbox.isSelected }
        bigPlanetsCheckbox.addActionListener { settings.bigPlanets = bigPlanetsCheckbox.isSelected }
        drawRaysCheckbox.addActionListener {

            settings.drawRays = drawRaysCheckbox.isSelected
            doodlePanel.refresh()
        }
    }

    fun exit(): Nothing {
        settings.storeData()
        exitProcess(0)
    }

    /** grid bag stuff */
    private fun layoutComponents() {
        /// left hand side
        addComponent(locusRadioButtonPanel, 0, 0, 2, 2)
        addComponent(numberOfRaysLabel, 0, 2, anchor = WEST, fill = NONE)
        addComponent(numberOfRaysSpinner, 1, 2, fill = HORIZONTAL)
        addComponent(sunCheckbox, 0, 3, fill = EAST, anchor = WEST)
        addComponent(bigPlanetsCheckbox, 0, 4, fill = EAST, anchor = WEST)
        addComponent(drawRaysCheckbox, 0, 5, fill = EAST, anchor = WEST)
        /// right hand side controls
        var y = 0
        addComponent(fixedWanderModePanel, 2, y++)
        addComponent(borderButtonPanel, 2, y++)
        addComponent(clearButton, 2, y++)
        addComponent(quitButton, 2, y++)
    }

    private fun addComponent(
        component: Component,
        gridX: Int,
        gridY: Int,
        gridWidth: Int = 1,
        gridHeight: Int = 1,
        fill: Int = HORIZONTAL,
        anchor: Int = NORTH,
        iPadX: Int = 0,
        iPadY: Int = 0,
    ) {
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
        val value = (numberOfRaysSpinner.value as Int)

        if (value in settings.minRaysValue..settings.maxRaysValue) {
            numberOfRaysSpinner.value = value + wheelRotation
            doodlePanel.setNumPoints(wheelRotation)
        }
    }

    fun setWandering(isWandering: Boolean) {
        animationModeRadioButton.isSelected = isWandering
    }

    val isWandering: Boolean
        get() = animationModeRadioButton.isSelected

    val isAddLocusMode: Boolean
        get() = addLocusPointsRadioButton.isSelected

    val isMoveLocusMode: Boolean
        get() = moveLocusPointsRadioButton.isSelected

    val isRemoveLocusMode: Boolean
        get() = removeLocusPointsRadioButton.isSelected // End of variables declaration//GEN-END:variables

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
