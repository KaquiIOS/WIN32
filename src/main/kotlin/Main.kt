package org.example

import kotlinx.coroutines.*
import org.example.win32.const.CommonConst.Companion.BUTTON
import org.example.win32.const.CommonConst.Companion.COMBO_BOX
import org.example.win32.const.CommonConst.Companion.COMBO_BOX_EX_32
import org.example.win32.const.CommonConst.Companion.IROS_PROCESS_NAME
import org.example.win32.const.CommonConst.Companion.PRINT_OK
import org.example.win32.const.CommonConst.Companion.PRT_DIAG_FILE_NAME_CLASS
import org.example.win32.const.CommonConst.Companion.PRT_DIAG_PATH_EDIT_WRAPPER_CLASS
import org.example.win32.const.CommonConst.Companion.PRT_DIAG_PATH_PARENT_CLASS
import org.example.win32.const.CommonConst.Companion.PRT_DIAG_UI_VIEW_CLASS
import org.example.win32.const.CommonConst.Companion.PRT_DIALOG_CAPTION
import org.example.win32.data.ProcessInfo
import org.example.win32.ext.join
import org.example.win32.util.IROSKernel32Util
import org.example.win32.util.IROSUser32Util
import org.example.win32.util.IROSUser32Util.Companion.findWindowExByClassName
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories

fun main() = runBlocking {
    val todayString = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()).toString()
    val filePath = "C:\\iros\\$todayString"
    val fileName = "_".join(todayString, "법인등기", "동양건설") + ".pdf"
    val processLst: List<ProcessInfo> = IROSKernel32Util.getProcessLst()

    println(IROSUser32Util.getAllWindows())

    // Process 및 컨트롤 클래스 이름 설정
    val targetHwndMap: Map<String, List<String>> = mapOf(
        IROS_PROCESS_NAME to listOf(COMBO_BOX, BUTTON)
    )

    // targetProcess 를 순회하면서
    targetHwndMap.keys.forEach { processName ->
        val targetParentHwnd = IROSUser32Util.findTargetHwndByProcessName(processLst, processName)

        if (targetParentHwnd != null) {
            targetHwndMap[processName]?.forEach { control ->
                val targetChildHwndLst = findWindowExByClassName(targetParentHwnd.hwnd, control)

                when (control) {
                    COMBO_BOX -> targetChildHwndLst.forEach {
                        IROSUser32Util.setPrintToToPDF(targetParentHwnd.hwnd, it.hwnd)
                    }
                    BUTTON -> {
                        targetChildHwndLst.firstOrNull { it.titleName == PRINT_OK }?.let {
                            if (!IROSUser32Util.sendButtonDownMessage(it.hwnd, true)) {
                                println("LButton Down Fail")
                            } else {
                                // 파일 저장 로직을 별도 코루틴으로 실행
                                println("Launching file save coroutine...")
                                launch(Dispatchers.IO) {
                                    saveFile(fileName, filePath, processLst)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

suspend fun saveFile(fileName: String, filePath: String, processLst: List<ProcessInfo>) = coroutineScope {
    val absPath = kotlin.io.path.Path(filePath).createDirectories().absolutePathString()
    var tryCnt = 0

    println("Starting file save logic with file path: $absPath")

    while (tryCnt < 5) {
        if (tryCnt > 0) {
            delay(3000) // 주기적으로 창 상태를 확인
            println("Retrying... attempt $tryCnt")
        }

        tryCnt += 1

        val printHwnd = IROSUser32Util.getAllWindows().find { it.titleName == PRT_DIALOG_CAPTION }?.hwnd

        if (printHwnd != null) {
            println("Explorer process window detected.")

            val fileNameParentHwnd = IROSUser32Util.findWindowEx(printHwnd, null, PRT_DIAG_UI_VIEW_CLASS, null)
            requireNotNull(fileNameParentHwnd) { "File name parent HWND not found." }

            val fileNameHwnd = IROSUser32Util.findTargetHwndRecursivelyByClassName(fileNameParentHwnd, PRT_DIAG_FILE_NAME_CLASS)
            requireNotNull(fileNameHwnd) { "File name HWND not found." }

            IROSUser32Util.setEditText(fileNameHwnd.hwnd, fileName)
            println("Set file name to $fileName")

            val pathHwnd = IROSUser32Util.findWindowEx(printHwnd, null, PRT_DIAG_PATH_PARENT_CLASS, null)
            requireNotNull(pathHwnd) { "Path parent HWND not found." }

            val pathWinHwndInfo = IROSUser32Util.findTargetHwndRecursivelyByClassName(pathHwnd, PRT_DIAG_PATH_EDIT_WRAPPER_CLASS)
            requireNotNull(pathWinHwndInfo) { "Path wrapper HWND not found." }

            val comboBoxHwnd = IROSUser32Util.findWindowEx(
                IROSUser32Util.getParentHwnd(pathWinHwndInfo.hwnd),
                pathWinHwndInfo.hwnd,
                COMBO_BOX_EX_32,
                null
            )
            requireNotNull(comboBoxHwnd) { "ComboBox HWND not found." }

            if (IROSUser32Util.setEditText(comboBoxHwnd, absPath)) {
                IROSUser32Util.sendEnterToHwnd(pathWinHwndInfo.hwnd)
                println("Set path to $absPath and confirmed.")
            }

            val saveBtn = IROSUser32Util.findWindowEx(printHwnd, null, BUTTON, null)
            requireNotNull(saveBtn) { "Save button HWND not found." }

            IROSUser32Util.setForegroundWindow(printHwnd)
            IROSUser32Util.sendButtonDownMessage(saveBtn, true)
            println("Save button clicked.")

            delay(3000) // 저장 완료 대기
            break // 저장이 완료되면 반복 종료
        } else {
            println("Explorer process window not found. Retrying...")
        }
    }
}
