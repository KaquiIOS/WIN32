package org.example

import kotlinx.*
import com.sun.jna.Native
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.example.win32.const.CommonConst.Companion.BUTTON
import org.example.win32.const.CommonConst.Companion.COMBO_BOX
import org.example.win32.const.CommonConst.Companion.COMBO_BOX_EX_32
import org.example.win32.const.CommonConst.Companion.EXPLORER_PROCESS_NAME
import org.example.win32.const.CommonConst.Companion.IROS_PROCESS_NAME
import org.example.win32.const.CommonConst.Companion.PRINT_OK
import org.example.win32.const.CommonConst.Companion.PRT_DIAG_FILE_NAME_CLASS
import org.example.win32.const.CommonConst.Companion.PRT_DIAG_PATH_EDIT_WRAPPER_CLASS
import org.example.win32.const.CommonConst.Companion.PRT_DIAG_PATH_PARENT_CLASS
import org.example.win32.const.CommonConst.Companion.PRT_DIAG_UI_VIEW_CLASS
import org.example.win32.data.ProcessInfo
import org.example.win32.ext.join
import org.example.win32.util.IROSKernel32Util
import org.example.win32.util.IROSUser32Util
import org.example.win32.util.IROSUser32Util.Companion.findWindowExByClassName
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories


// 실행 시, 관리자 권한으로 실행해야 Handle을 가져올 수 있음
fun main() = runBlocking {

    val todayString: String = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()).toString()

    val filePath: String = "C:\\iros\\$todayString"
    val fileName: String = "_".join(todayString, "법인등기", "동양건설") + ".pdf"

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
                                delay(3000)
                                // 파일 경로 생성
                                makeSavePath(filePath)
                                saveFile(fileName, filePath, processLst)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun makeSavePath(filePath:String): String = kotlin.io.path.Path(filePath).createDirectories().absolutePathString()

suspend fun saveFile(fileName: String, filePath: String, processLst: List<ProcessInfo>) = coroutineScope {

    var tryCnt = 0

    while(tryCnt < 5) {

        if(tryCnt > 0) delay(3000)
        tryCnt += 1

        val printHwnd = IROSUser32Util.findTargetHwndByProcessName(processLst, EXPLORER_PROCESS_NAME)

        if (printHwnd != null) {

            // 각 윈도우 핸들의 클래스 이름
            val text: CharArray = CharArray(512)
            IROSUser32Util.getWindowText(printHwnd.hwnd, text, 512)

            if (!Native.toString(text).contains("다른"))
                continue

            // [미사용] Find Edit Class
            val fileNameParentHwnd = IROSUser32Util.findWindowEx(printHwnd.hwnd, null, PRT_DIAG_UI_VIEW_CLASS, null)
            requireNotNull(fileNameParentHwnd)

            // 실제 EDIT 핸들
            val fileNameHwnd = IROSUser32Util.findTargetHwndRecursivelyByClassName(fileNameParentHwnd, PRT_DIAG_FILE_NAME_CLASS)
            requireNotNull(fileNameHwnd)

            // 파일 이름 설정
            IROSUser32Util.setEditText(fileNameHwnd.hwnd, fileName)

            // [미사용] 경로 부모 핸들
            val pathHwnd = IROSUser32Util.findWindowEx(printHwnd.hwnd, null, PRT_DIAG_PATH_PARENT_CLASS, null)
            requireNotNull(pathHwnd)

            // [미사용] 경로 부모 핸들2
            val pathWinHwndInfo = IROSUser32Util.findTargetHwndRecursivelyByClassName(pathHwnd, PRT_DIAG_PATH_EDIT_WRAPPER_CLASS)
            requireNotNull(pathWinHwndInfo)

            val comboBoxHwnd = IROSUser32Util.findWindowEx(
                IROSUser32Util.getParentHwnd(pathWinHwndInfo.hwnd),
                pathWinHwndInfo.hwnd,
                COMBO_BOX_EX_32,
                null
            )

            requireNotNull(comboBoxHwnd)

            // WM_SETTEXT
            val result = IROSUser32Util.setEditText(comboBoxHwnd, filePath)

            if (result) {
                IROSUser32Util.sendEnterToHwnd(pathWinHwndInfo.hwnd)
            }

            // Find Save Button
            // Text 에 "저장"이라는 단어가 들어오는지 확인하는 로직 필요
            val saveBtn = IROSUser32Util.findWindowEx(printHwnd.hwnd, null, BUTTON, null)

            requireNotNull(saveBtn)

            IROSUser32Util.sendButtonDownMessage(saveBtn, true)
        }
    }
}