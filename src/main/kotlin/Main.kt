package org.example

import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import org.example.win32.const.CommonConst.Companion.BUTTON
import org.example.win32.const.CommonConst.Companion.COMBO_BOX
import org.example.win32.const.CommonConst.Companion.EXPLORER_PROCESS_NAME
import org.example.win32.const.CommonConst.Companion.IROS_PROCESS_NAME
import org.example.win32.const.CommonConst.Companion.PRINT_OK
import org.example.win32.const.CommonConst.Companion.PRINT_SAVE
import org.example.win32.const.CommonConst.Companion.PRT_DIAG_FILE_NAME_CLASS
import org.example.win32.const.CommonConst.Companion.PRT_DIAG_PATH_EDIT_CLASS
import org.example.win32.const.CommonConst.Companion.PRT_DIAG_PATH_EDIT_WRAPPER_CLASS
import org.example.win32.const.CommonConst.Companion.PRT_DIAG_PATH_PARENT_CLASS
import org.example.win32.const.CommonConst.Companion.PRT_DIAG_UI_VIEW_CLASS
import org.example.win32.const.Win32Const
import org.example.win32.data.ProcessInfo
import org.example.win32.ext.join
import org.example.win32.util.IROSKernel32Util
import org.example.win32.util.IROSUser32Util
import org.example.win32.util.IROSUser32Util.Companion.findWindowExByClassName
import java.io.IOException
import java.nio.file.FileAlreadyExistsException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories


// 실행 시, 관리자 권한으로 실행해야 Handle을 가져올 수 있음
fun main() {

    val todayString: String = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()).toString()

    val filePath: String = "C:\\iros\\$todayString"
    val fileName: String = "_".join(todayString, "법인등기", "동양건설") + ".pdf"

    val processLst: List<ProcessInfo> = IROSKernel32Util.getProcessLst()


    // 파일 경로 만들어주기
    makeSavePath(filePath)

    val printHwnd = IROSUser32Util.findTargetHwndByProcessName(processLst, EXPLORER_PROCESS_NAME)

    if (printHwnd != null) {

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

        // 실제 경로 핸들
        val pathTextWinHwndInfo = IROSUser32Util.findWindowEx(pathWinHwndInfo.hwnd, null, PRT_DIAG_PATH_EDIT_CLASS, null)
        requireNotNull(pathTextWinHwndInfo)

        val result = IROSUser32Util.setEditText(pathTextWinHwndInfo, "주소: $filePath")
        IROSUser32Util.sendMessage(pathTextWinHwndInfo, Win32Const.WM_SETREDRAW, WinDef.WPARAM(0), WinDef.LPARAM(0))

        // RDW_FRAME | RDW_INVALIDATE | RDW_ALLCHILDREN

        // Find Save Button
        val saveBtn = IROSUser32Util.findWindowExByClassName(printHwnd.hwnd, BUTTON).filter { it.titleName.contains(PRINT_SAVE) }


    }







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

                                // 파일 경로 만들어주기
                                makeSavePath(filePath)
                                
                                val printHwnd = IROSUser32Util.findTargetHwndByProcessName(processLst, EXPLORER_PROCESS_NAME)

                                if (printHwnd != null) {

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

                                    // 실제 경로 핸들
                                    val pathTextWinHwndInfo = IROSUser32Util.findWindowEx(pathWinHwndInfo.hwnd, null, PRT_DIAG_PATH_EDIT_CLASS, null)
                                    requireNotNull(pathTextWinHwndInfo)

                                    // Find Save Button
                                    val saveBtn = IROSUser32Util.findWindowExByClassName(printHwnd.hwnd, BUTTON).filter { it.titleName.contains(PRINT_SAVE) }


                                }




                                //saveFile(targetParentHwnd.hwnd, ,makeSavePath(filePath, fileName))
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

fun makeSavePath(filePath:String): String = kotlin.io.path.Path(filePath).createDirectories().absolutePathString()
