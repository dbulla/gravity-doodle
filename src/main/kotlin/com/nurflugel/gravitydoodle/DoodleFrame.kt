package com.nurflugel.gravitydoodle

import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.EAST
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.SwingUtilities

/** Created by IntelliJ IDEA. User: Douglas Bullard Date: Oct 26, 2003 Time: 4:21:02 PM To change this template use Options | File Templates.  */
class DoodleFrame : JFrame() {
    private var controlPanel: ControlPanel
    internal var doodlePanel: DoodlePanel
    private var useFullScreenMode = false
    private val settings = Settings()
    private var defaultScreenSize = Toolkit.getDefaultToolkit().screenSize
    //    private var defaultScreenSize = Dimension(800, 300)


    init {
        val graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val screen = graphicsEnvironment.defaultScreenDevice
        val isFullScreenSupported = screen.isFullScreenSupported

        check(isFullScreenSupported) { "full screen mode not supported" }

        val myWindow = owner

        try {
            if (useFullScreenMode) {
                isUndecorated = isFullScreenSupported
                isResizable = !isFullScreenSupported
                screen.fullScreenWindow = myWindow
            }
            controlPanel = ControlPanel(this, settings)
            doodlePanel = DoodlePanel(this, controlPanel, settings)
            controlPanel.initialize() // this is where Spring injection is nice, avoids "chicken and the egg" dependencies
            doodlePanel.initialize()
            contentPane.layout = BorderLayout()
            contentPane.add(CENTER, doodlePanel)
            contentPane.add(EAST, controlPanel)

            size = defaultScreenSize
        } finally {
            isVisible = true
            defaultScreenSize = Toolkit.getDefaultToolkit().screenSize
        }

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(evt: WindowEvent?) {
                controlPanel.exit()
            }

            override fun windowStateChanged(e: WindowEvent?) {
                super.windowStateChanged(e)
                println("windowStateChanged e = $e")
            }
        })
    }

    fun isFullScreen(): Boolean {
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val gd = ge.defaultScreenDevice
        val isFullScreen = gd.fullScreenWindow != null
        println("isFullScreen = $isFullScreen")
        return isFullScreen
    }

    fun invertControlPanelVisibility() {
        controlPanel.isVisible = !controlPanel.isVisible
        doodlePanel.refresh()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater {
                val doodleFrame = DoodleFrame()
                doodleFrame.isVisible = true
            }
        }
    }

}