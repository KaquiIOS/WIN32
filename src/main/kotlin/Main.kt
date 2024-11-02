package org.example

import org.example.win32.const.CommonConst.Companion.BUTTON
import org.example.win32.const.CommonConst.Companion.COMBO_BOX
import org.example.win32.const.CommonConst.Companion.IROS_PROCESS_NAME
import org.example.win32.data.ProcessInfo
import org.example.win32.util.IROSKernel32Util
import org.example.win32.util.IROSUser32Util


// 실행 시, 관리자 권한으로 실행해야 Handle을 가져올 수 있음
fun main() {

    val processLst: List<ProcessInfo> = IROSKernel32Util.getProcessLst()

    // process 와 찾을 컨트롤 클래스 이름
    val targetHwndMap: Map<String, String> = mutableMapOf(
        IROS_PROCESS_NAME to COMBO_BOX
    )

    // targetProcess 를 순회하면서
    for((targetProcessName, className) in targetHwndMap) {

        val targetHwndFamily =
            IROSUser32Util.findTargetHwndByClassName(processLst, targetProcessName, className) ?: continue

        val targetParentHwnd = targetHwndFamily.first
        val targetChildHwndOfClassName = targetHwndFamily.second

        when(className) {
            COMBO_BOX -> targetChildHwndOfClassName.forEach { IROSUser32Util.setPrintToToPDF(targetParentHwnd.hwnd, it.hwnd) }
            BUTTON -> TODO()
        }
    }
}