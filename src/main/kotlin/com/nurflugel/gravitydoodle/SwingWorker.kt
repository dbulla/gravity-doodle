package com.nurflugel.gravitydoodle

import javax.swing.SwingUtilities

/**
 * This is the 3rd version of SwingWorker (also known as
 * SwingWorker 3), an abstract class that you subclass to
 * perform GUI-related work in a dedicated thread.  For
 * instructions on using this class, see:
 *
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 * Note that the API changed slightly in the 3rd version:
 * You must now invoke start() on the SwingWorker after
 * creating it.
 */
abstract class SwingWorker
protected constructor() {
    /** Get the value produced by the worker thread, or null if it hasn't been constructed yet. */
    /** Set the value produced by worker thread */
    @get:Synchronized
    @set:Synchronized
    protected var value: Any? = null // see getValue(), setValue()
        /** Set the value produced by worker thread */
        private set

    private var threadVar: ThreadVar

    /**
     * Start a thread that will call the `construct` method
     * and then exit.
     */
    init {
        val doFinished = Runnable {
            finished()
        }

        val doConstruct = Runnable {
            try {
                value = construct()
            } finally {
                threadVar.clear()
            }

            SwingUtilities.invokeLater(doFinished)
        }

        val t = Thread(doConstruct)

        threadVar = ThreadVar(t)
    }

    /**
     * Compute the value to be returned by the `get` method.
     */
    abstract fun construct(): Any

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the `construct` method has returned.
     */
    private fun finished() {}

    /**
     * A new method that interrupts the worker thread.  Call this method
     * to force the worker to stop what it's doing.
     */
    fun interrupt() {
        println("SwingWorker.interrupt")

        val t = threadVar.get()

        t?.interrupt()

        threadVar.clear()
    }

    /**
     * Return the value created by the `construct` method.
     * Returns null if either the constructing thread or the current
     * thread was interrupted before a value was produced.
     *
     * @return the value created by the `construct` method
     */
    fun get(): Any? {
        println("SwingWorker.get")

        while (true) {
            val t = threadVar.get()
                    ?: return value

            try {
                t.join()
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt() // propagate

                return null
            }
        }
    }

    /**
     * Start the worker thread.
     */
    fun start() {
        println("SwingWorker.start")

        val t = threadVar.get()

        t?.start()
    }

    /**
     * Class to maintain reference to current worker thread
     * under separate synchronization control.
     */
    private class ThreadVar
        (private var theThread: Thread?) {
        @Synchronized
        fun get(): Thread? {
            return theThread
        }

        @Synchronized
        fun clear() {
            theThread = null
        }
    }
}