package org.example

import com.sun.jna.platform.win32.WinDef.HWND
import org.example.win32.const.CommonConst.Companion.BUTTON
import org.example.win32.const.CommonConst.Companion.COMBO_BOX
import org.example.win32.const.CommonConst.Companion.EXPLORER_PROCESS_NAME
import org.example.win32.const.CommonConst.Companion.IROS_PROCESS_NAME
import org.example.win32.const.CommonConst.Companion.PRINT_OK
import org.example.win32.data.ProcessInfo
import org.example.win32.data.WindowHandleInfo
import org.example.win32.util.IROSKernel32Util
import org.example.win32.util.IROSUser32Util
import org.example.win32.util.IROSUser32Util.Companion.findWindowExByClassName
import org.example.win32.ext.join
import org.example.win32.intf.IROSUser32
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Path
import java.nio.file.attribute.FileAttribute
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.math.exp


// 실행 시, 관리자 권한으로 실행해야 Handle을 가져올 수 있음
fun main() {

    val todayString: String = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()).toString()

    val filePath: String = "C:\\iros\\$todayString\\"
    val fileName: String = "_".join(todayString, "법인등기", "동양건설") + ".pdf"

    val processLst: List<ProcessInfo> = IROSKernel32Util.getProcessLst()

    // process 와 찾을 컨트롤 클래스 이름
    val targetHwndMap: Map<String, List<String>> = mutableMapOf(
        IROS_PROCESS_NAME to listOf(COMBO_BOX, BUTTON)
    )


    // exeploer 를 찾아서, exploer 가 특정 위치에 있는지 확인을 하거나, 이름으로 처리할 수 밖에 없을듯
    val printHwnd = IROSUser32Util.findTargetHwndByProcessName(processLst, EXPLORER_PROCESS_NAME)

    if (printHwnd != null) {

        // Find Edit Class
        val editHwnd = IROSUser32Util.findWindowEx(printHwnd.hwnd, null, "DUIViewWndClassName", null)
        requireNotNull(editHwnd)
        val editWinHwndInfo = IROSUser32Util.findTargetHwndRecursivelyByClassName(editHwnd, "Edit")

        val pathHwnd = IROSUser32Util.findWindowEx(printHwnd.hwnd, null, "WorkerW", null)
        requireNotNull(pathHwnd)

        val pathWinHwndInfo = IROSUser32Util.findTargetHwndRecursivelyByClassName(pathHwnd, "Breadcrumb Parent")
        requireNotNull(pathWinHwndInfo)

        val pathTextWinHwndInfo = IROSUser32Util.findWindowEx(pathWinHwndInfo.hwnd, null, "ToolbarWindow32", null)
        requireNotNull(pathTextWinHwndInfo)

        // Find Save Button
        val saveBtn = IROSUser32Util.findWindowExByClassName(printHwnd.hwnd, BUTTON).filter { it.titleName.contains("저장") }


    }

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

                                // exeploer 를 찾아서, exploer 가 특정 위치에 있는지 확인을 하거나, 이름으로 처리할 수 밖에 없을듯
                                val printHwnd = IROSUser32Util.findTargetHwndByProcessName(processLst, EXPLORER_PROCESS_NAME)

                                if (printHwnd != null) {

                                    // Find Edit Class
                                    IROSUser32Util.findTargetHwndRecursivelyByClassName(printHwnd.hwnd, "Edit")

                                    // Find Path
                                    IROSUser32Util.findTargetHwndRecursivelyByClassName(printHwnd.hwnd, "Breadcrumb Parent")

                                    // Find Save Button
                                    val saveBtn = IROSUser32Util.findWindowExByClassName(printHwnd.hwnd, BUTTON).filter { it.titleName.contains("저장") }


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

fun getForegroundWindow() {

    val hWnd = IROSUser32Util.getForegroundWindow()

    println(hWnd)


}

fun makeSavePath(filePath:String, fileName: String): String = kotlin.io.path.Path(filePath).createDirectories().absolutePathString()


fun saveFile(parentHwnd: HWND, targetClassName: String, filePath: String): Boolean {

    // check folder exist (추가할 속성은 따로 필요없음)
    try {

        // make file save path
        val absFileSavePath: String = kotlin.io.path.Path(filePath).createDirectories().absolutePathString()

        // Get Foreground window handle
        //val explorerWindowHandle: WindowHandleInfo? = IROSUser32Util.findTargetHwndRecursivelyByClassName(parentHwnd, )
        val explorerHwnd: HWND? = IROSUser32Util.getForegroundWindow()

        // Set File Directory


        // Set File Name



    } catch (e: IOException) {

        return false
    } catch (e: FileAlreadyExistsException) {

        return false
    } catch (e: Exception) {

        return false
    }

    return true

// https://stackoverflow.com/questions/6298907/how-to-get-hwnd-of-the-currently-active-windows-explorer-window
//  GetForegroundWindow().

}