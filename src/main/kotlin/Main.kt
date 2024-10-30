package org.example

import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME
import com.sun.jna.platform.win32.WinNT.HANDLE
import org.example.win32.data.ProcessInfo
import org.example.win32.data.WindowHandleInfo
import org.example.win32.intf.IROSKernel32
import org.example.win32.intf.IROSUser32
import org.example.win32.util.IROSKernel32Util
import org.example.win32.util.IROSUser32Util


// 실행 시, 관리자 권한으로 실행해야 Handle을 가져올 수 있음
fun main() {

    for(processInfo in IROSKernel32Util.getProcessLst()) {
        println(processInfo)
    }

    println("-----------------------------------------")

    val irosViewerProcess = IROSKernel32Util.getProcessLst().firstOrNull { it.processName == "iprtcrsIgmprintxctrl.xgd" }

    if (irosViewerProcess != null) {

        val windowHandleInfoLst: MutableList<WindowHandleInfo> = mutableListOf()

        val irosViewerParentHwnd = IROSUser32Util.getAllWindows().firstOrNull { it.pid == irosViewerProcess.pid }

        // 여기서 Parent Window Handle 을 찾고, 없으면 에러 처리
        checkNotNull(irosViewerParentHwnd)

        val irosViewerChildHwndLst: List<WindowHandleInfo> = IROSUser32Util.FindWindowExByClassName(irosViewerParentHwnd.hwnd, "Button")

        if(irosViewerChildHwndLst.isNotEmpty()) {

            println(irosViewerParentHwnd)

            for (irosChildHwnd in irosViewerChildHwndLst) {
                println(irosChildHwnd)
            }
        }
    }
}
