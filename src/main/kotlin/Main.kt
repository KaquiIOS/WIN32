package org.example

import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.PointerType
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.LRESULT
import org.example.win32.data.ProcessInfo
import org.example.win32.data.WindowHandleInfo
import org.example.win32.intf.IROSUser32
import org.example.win32.util.IROSKernel32Util
import org.example.win32.util.IROSUser32Util


// 실행 시, 관리자 권한으로 실행해야 Handle을 가져올 수 있음
fun main() {

    val processLst: List<ProcessInfo> = IROSKernel32Util.getProcessLst()

    // process 와 찾을 컨트롤 클래스 이름
    val targetHwndMap: Map<String, String> = mutableMapOf(
        "iprtcrsIgmprintxctrl.xgd" to "ComboBox"
    )

    // targetProcess 를 순회하면서
    for((targetProcessName, className) in targetHwndMap) {

        val targetHwndFamily =
            IROSUser32Util.findTargetHwndByClassName(processLst, targetProcessName, className) ?: continue

        val targetParentHwnd = targetHwndFamily.first
        val targetChildHwndOfClassName = targetHwndFamily.second

        when(className) {
            "ComboBox" -> targetChildHwndOfClassName.forEach { IROSUser32Util.setPrintToToPDF(targetParentHwnd.hwnd, it.hwnd) }
            "Button" -> TODO()
        }
    }
}