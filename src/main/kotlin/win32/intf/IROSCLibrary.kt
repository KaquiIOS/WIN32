package org.example.win32.intf

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Platform
import com.sun.jna.win32.W32APIOptions

interface IROSCLibrary : Library {

    companion object {
        val INSTANCE: IROSCLibrary = Native.load(if (Platform.isWindows()) "msvcrt" else "c", IROSCLibrary::class.java, W32APIOptions.DEFAULT_OPTIONS) as IROSCLibrary
    }

    fun printf(format: String, vararg args: Any)
}