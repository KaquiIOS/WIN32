package org.example

import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME
import org.example.win32.data.ProcessInfo
import org.example.win32.util.IROSKernel32Util


fun main() {
    val lib = Kernel32.INSTANCE
    val time = SYSTEMTIME()
    lib.GetSystemTime(time)
    println(time)


    val targetProcess: List<String> = listOf("IPRTCrsIgmPrintXCtrl.exe", "iprtcrsIgmprintxctrl.xgd")

    val irosProcess = IROSKernel32Util.listProcesses().filter { targetProcess.contains(it.processName) }


    println(irosProcess)




}
