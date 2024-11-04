package org.example

import com.sun.jna.platform.win32.WinDef.HWND
import org.example.win32.const.CommonConst.Companion.BUTTON
import org.example.win32.const.CommonConst.Companion.COMBO_BOX
import org.example.win32.const.CommonConst.Companion.IROS_PROCESS_NAME
import org.example.win32.const.CommonConst.Companion.PRINT_OK
import org.example.win32.data.ProcessInfo
import org.example.win32.util.IROSKernel32Util
import org.example.win32.util.IROSUser32Util
import org.example.win32.util.IROSUser32Util.Companion.findWindowExByClassName
import org.example.win32.ext.join
import java.time.LocalDate
import java.time.format.DateTimeFormatter


// 실행 시, 관리자 권한으로 실행해야 Handle을 가져올 수 있음
fun main() {

    val fileName: String = "_".join(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()).toString(), "법인등기", "동양건설") + ".pdf"

    val processLst: List<ProcessInfo> = IROSKernel32Util.getProcessLst()

    // process 와 찾을 컨트롤 클래스 이름
    val targetHwndMap: Map<String, List<String>> = mutableMapOf(
        IROS_PROCESS_NAME to listOf(COMBO_BOX, BUTTON)
    )

    // targetProcess 를 순회하면서
    targetHwndMap.keys.forEach { processName ->

        val targetParentHwnd = IROSUser32Util.findTargetHwndByProcessName(processLst, processName)

        if (targetParentHwnd != null) {

            targetHwndMap[processName]?.forEach { control ->
                val targetChildHwndLst = findWindowExByClassName(targetParentHwnd.hwnd, control)

                when (control) {
                    COMBO_BOX -> targetChildHwndLst.onEach { IROSUser32Util.setPrintToToPDF(targetParentHwnd.hwnd, it.hwnd) }
                    BUTTON -> {
                        targetChildHwndLst.firstOrNull { it.titleName == PRINT_OK }?.let {
                            if(!IROSUser32Util.sendButtonDownMessage(it.hwnd, true)) {
                                println("LButton Down Fail")
                            } else {
                                saveFile(fileName, targetParentHwnd.hwnd)
                                // 프린트 성공하면, 저장을 위한 윈도우 탐색기를 확인해야함.
                                // 윈도우 탐색기 확인 시, Title 을 저장하는 Combo Box 찾기
                                // Combo Box 를 찾으면, Title 을 설정해줌 (yyyyMMdd_등기구분_기업명.pdf)
                            }
                        }
                    }
                }
            }

            // yyyyMMdd_등기구분_기업명.pdf 파일이 저장된 경우
            // 해당 파일을 분석하는 로직 추가

        }
    }
}


fun saveFile(fileName: String, parentHwnd: HWND) {

// https://stackoverflow.com/questions/6298907/how-to-get-hwnd-of-the-currently-active-windows-explorer-window
//  GetForegroundWindow().



}