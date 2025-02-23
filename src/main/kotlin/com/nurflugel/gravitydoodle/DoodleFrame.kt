package com.nurflugel.gravitydoodle

import java.awt.*
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.EAST
import java.awt.event.*
import javax.swing.JFrame

/** Created by IntelliJ IDEA. User: Douglas Bullard Date: Oct 26, 2003 Time: 4:21:02 PM To change this template use Options | File Templates.  */
class DoodleFrame : JFrame() {
    private var doodlePanel: DoodlePanel
    private var uiManager: UiManager
    private var useFullScreenMode = false

//        private var defaultScreenSize = Toolkit.getDefaultToolkit().screenSize
    private var defaultScreenSize = Dimension(800, 300)


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

            doodlePanel = DoodlePanel(this)
            uiManager = UiManager(this)
            contentPane.layout = BorderLayout()
            contentPane.add(CENTER, doodlePanel)
            contentPane.add(EAST, uiManager)

            size = defaultScreenSize

            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(evt: WindowEvent) {
                    System.exit(0)
                }
            })

        } finally {

        }

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(evt: WindowEvent?) {
                System.exit(0)
            }

            override fun windowStateChanged(e: WindowEvent?) {
                super.windowStateChanged(e)
                println("windowStateChanged e = ${e}")
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
        uiManager.isVisible = !uiManager.isVisible
        doodlePanel.refresh()
    }

    fun getDoodlePanel(): DoodlePanel {
        return doodlePanel
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val doodleFrame = DoodleFrame()
            doodleFrame.isVisible = true
        }
    }

    fun getUiManager(): UiManager {
        return uiManager;
    }

}