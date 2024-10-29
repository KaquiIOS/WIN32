package org.example

import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME
import com.sun.jna.platform.win32.WinNT.HANDLE
import org.example.win32.data.ProcessInfo
import org.example.win32.util.IROSKernel32Util


fun main() {
    val lib = Kernel32.INSTANCE
    val time = SYSTEMTIME()
    lib.GetSystemTime(time)
    println(time)


    val targetProcessNameLst: List<String> = listOf("IPRTCrsIgmPrintXCtrl.exe", "iprtcrsIgmprintxctrl.xgd")

    val irosProcessInfoLst = IROSKernel32Util.getProcessLst().filter { targetProcessNameLst.contains(it.processName) }

    val processInfo: ProcessInfo = irosProcessInfoLst[0]


    // 실행 시, 관리자 권한으로 실행해야 Handle을 가져올 수 있음
    val handle: HANDLE? = IROSKernel32Util.getProcessHandle(processInfo.pid.toInt())

    println(handle)


    lib.CloseHandle(handle)

}
