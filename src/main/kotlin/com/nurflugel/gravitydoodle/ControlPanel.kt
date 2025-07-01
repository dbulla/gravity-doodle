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

    private val sunCheckbox = JCheckBox("First point is a sun")
    private val bigPlanetsCheckbox = JCheckBox("Planets are very heavy (wilder movement)")
    private val bigSunCheckbox = JCheckBox("Big sun (faster orbits)")
    private val drawRaysCheckbox = JCheckBox("Draw rays")
    private val planetsInteractCheckbox = JCheckBox("Planets interact with each other (slower)")
    private val planetsBounceCheckbox = JCheckBox("Planets bounce off the edges")

    private var addMoveRemoteButtonGroup = ButtonGroup()
    private var borderButtonGroup = ButtonGroup()

    private val borderButtonPanel = JPanel()
    private val locusRadioButtonPanel = JPanel()
    private val contentPanel = JPanel()

    private val clearButton = JButton("Clear")
    private val quitButton = JButton("Quit")
    private lateinit var doodlePanel: DoodlePanel

    private val numberOfRaysLabel = JLabel("Number of Rays: ")
    private val numberOfRaysSpinner = JSpinner(SpinnerNumberModel(settings.initialRaysValue, settings.minRaysValue, settings.maxRaysValue, 2))

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
        bigSunCheckbox.isSelected = settings.bigSun
        numberOfRaysSpinner.value = settings.numberOfRays
        planetsInteractCheckbox.isSelected = settings.planetsInteractWithEachOther
        planetsBounceCheckbox.isSelected = settings.planetsBounce

        drawRaysCheckbox.isSelected = settings.drawRays

        borderButtonPanel.layout = BoxLayout(borderButtonPanel, Y_AXIS)
        borderButtonPanel.border = createTitledBorder(EtchedBorder(), "Doodle Boundary")
        borderButtonPanel.add(frameBorderButton)
        borderButtonPanel.add(circularBorderButton)
        borderButtonGroup.add(frameBorderButton)
        borderButtonGroup.add(circularBorderButton)
        circularBorderButton.isSelected = true
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
        numberOfRaysSpinner.addChangeListener {
            val numPointsPerSide = numberOfRaysSpinner.model.value.toString().toInt()
            doodlePanel.setNumPoints(numPointsPerSide)
            settings.numberOfRays = numPointsPerSide
        }
        numberOfRaysSpinner.addMouseWheelListener { numPointsSpinnerMouseWheelMoved(it) }
        frameBorderButton.addActionListener { doodlePanel.refresh() }
        circularBorderButton.addActionListener { doodlePanel.refresh() }

        sunCheckbox.addActionListener {
            settings.firstPointIsSun = sunCheckbox.isSelected
            bigSunCheckbox.isVisible = settings.firstPointIsSun
        }
        bigPlanetsCheckbox.addActionListener { settings.bigPlanets = bigPlanetsCheckbox.isSelected }
        planetsInteractCheckbox.addActionListener { settings.planetsInteractWithEachOther = planetsInteractCheckbox.isSelected }
        planetsBounceCheckbox.addActionListener { settings.planetsBounce = planetsBounceCheckbox.isSelected }
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
        var y = 0
        /// left-hand side
        addComponent(locusRadioButtonPanel, 0, y, 2, 2)
        y = 2
        addComponent(numberOfRaysLabel, 0, 2, anchor = WEST, fill = NONE)
        addComponent(numberOfRaysSpinner, 1, y++, fill = HORIZONTAL)
        addComponent(sunCheckbox, 0, y++, fill = EAST, anchor = WEST)
        addComponent(bigPlanetsCheckbox, 0, y++, fill = EAST, anchor = WEST)
        addComponent(bigSunCheckbox, 0, y++, fill = EAST, anchor = WEST)
        addComponent(planetsInteractCheckbox, 0, y++, fill = HORIZONTAL, anchor = EAST)
        addComponent(planetsBounceCheckbox, 0, y++, fill = HORIZONTAL, anchor = EAST)
        addComponent(drawRaysCheckbox, 0, y++, fill = EAST, anchor = WEST)
        /// right-hand side controls
        y = 0
        addComponent(borderButtonPanel, 2, y++)
        addComponent(clearButton, 2, y++)
        addComponent(quitButton, 2, y++)

        bigSunCheckbox.isVisible = settings.firstPointIsSun
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
        println("Setting wandering to $isWandering")
//        animationModeRadioButton.isSelected = isWandering
        when {
            isWandering -> doodlePanel.wander()
            !isWandering        -> doodlePanel.stopWorker()
        }
    }

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
