package org.example

import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME
import com.sun.jna.platform.win32.WinNT.HANDLE
import com.sun.jna.platform.win32.WinUser
import org.example.win32.data.ProcessInfo
import org.example.win32.data.WindowHandleInfo
import org.example.win32.intf.IROSKernel32
import org.example.win32.intf.IROSUser32
import org.example.win32.util.IROSKernel32Util
import org.example.win32.util.IROSUser32Util


// 실행 시, 관리자 권한으로 실행해야 Handle을 가져올 수 있음
fun main() {

    val processLst: List<ProcessInfo> = IROSKernel32Util.getProcessLst()
    val targetProcess: List<String> = listOf("iprtcrsIgmprintxctrl.xgd", "IPRTCrsIgmPrintXCtrl.exe")



    targetProcess.forEach { findTargetPidHwnd(processLst, it, listOf("Button", "ComboBox")) }


    //WM_SETCURSOR
    //WinUser.

}

fun findTargetPidHwnd(processLst: List<ProcessInfo>, targetProcessName: String, targetClassNameLst: List<String>) {

    println("-----------------------------------------")

    val irosViewerProcess = processLst.firstOrNull { it.processName == targetProcessName }

    if (irosViewerProcess != null) {

        val irosViewerParentHwnd = IROSUser32Util.getAllWindows().firstOrNull { it.pid == irosViewerProcess.pid }

        // 여기서 Parent Window Handle 을 찾고, 없으면 에러 처리
        if (irosViewerParentHwnd == null)
            return

        for(targetClassName in targetClassNameLst) {

            val irosViewerChildHwndLst: List<WindowHandleInfo> = IROSUser32Util.FindWindowExByClassName(irosViewerParentHwnd.hwnd, targetClassName)

            if(irosViewerChildHwndLst.isEmpty())
                continue

            println(irosViewerParentHwnd)

            for (irosChildHwnd in irosViewerChildHwndLst) {
                println(irosChildHwnd)
            }
        }
    }
}