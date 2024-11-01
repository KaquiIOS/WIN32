package org.example.win32.util

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC
import com.sun.jna.ptr.IntByReference
import org.example.win32.data.ProcessInfo
import org.example.win32.data.WindowHandleInfo
import org.example.win32.intf.IROSUser32


class IROSUser32Util {

    companion object {

        fun findWindowExByClassName(parent: HWND?, className: String?): List<WindowHandleInfo> {

            val user32: IROSUser32 = IROSUser32.INSTANCE

            val childWindowHandleInfoLst: MutableList<WindowHandleInfo> = mutableListOf()

            var findWindowHandle: HWND? = null

            while(true) {

                findWindowHandle = user32.FindWindowEx(parent, findWindowHandle, className, null)

                if (findWindowHandle == null)
                    break

                // 각 윈도우 핸들의 Text 를 수집
                val windowText: CharArray = CharArray(512)
                user32.GetWindowText(findWindowHandle, windowText, 512)

                // 각 윈도우 핸들의 클래스 이름
                val classText: CharArray = CharArray(512)
                user32.GetClassName(findWindowHandle, classText, 512)

                // 각 윈도우 핸들의 PID
                val pid = IntByReference(0)
                user32.GetWindowThreadProcessId(findWindowHandle, pid)

                childWindowHandleInfoLst.add(WindowHandleInfo(
                    Native.toString(windowText),
                    Native.toString(classText),
                    pid.value,
                    findWindowHandle
                ))
            }

            return childWindowHandleInfoLst
        }

        // boolean callback(WinDef.HWND var1, Pointer var2);
        fun getAllWindows(): List<WindowHandleInfo> {

            val windowHwndLst: MutableList<WindowHandleInfo> = mutableListOf()

            IROSUser32.INSTANCE.EnumWindows(WNDENUMPROC { hwnd, _ ->

                val user32: IROSUser32 = IROSUser32.INSTANCE

                if (user32.IsWindowVisible(hwnd)) {

                    // 각 윈도우 핸들의 Text 를 수집
                    val text: CharArray = CharArray(512)
                    user32.GetWindowText(hwnd, text, 512)
                    val windowTextString: String = Native.toString(text)

                    // 빈 텍스트면 취소
                    if (windowTextString.isEmpty()) {
                        return@WNDENUMPROC true
                    }

                    // 각 윈도우 핸들의 클래스 이름
                    user32.GetClassName(hwnd, text, 512)
                    val classNameString: String = Native.toString(text)

                    // 각 윈도우 핸들의 PID
                    val pid = IntByReference(0)
                    user32.GetWindowThreadProcessId(hwnd, pid)

                    windowHwndLst.add(WindowHandleInfo(windowTextString, classNameString, pid.value, hwnd))
                }

                return@WNDENUMPROC true

            }, null)

            return windowHwndLst
        }

        /**
         * 선택된 등기를 PDF 로 출력하기 위해, 다이얼로그를 PDF 로 설정한다.
         * Microsoft Print to PDF
         */
        fun setPrintToToPDF(parentHwnd: HWND, comboBoxHwnd: HWND, targetPrintName: String = "Microsoft Print to PDF") : Boolean {

            // PDF 설정을 위한 이벤트들
            val CB_GETLBTEXT = 0x0148
            val CB_GETLBTEXTLEN = 0x0149
            val CB_SETCURSEL = 0x014e

            val user32: IROSUser32 = IROSUser32.INSTANCE

            // CB_GETCOUNT : DropBox 의 행이 몇 줄인지 확인
            val comboBoxItemCnt: Int = user32.SendMessage(comboBoxHwnd, 0x0146, WPARAM(0), LPARAM(0)).toInt()

            // 컨트롤 ID를 얻어, 드롭 박스의 Item 을 확인한다
            val controlHwnd: HWND = IROSUser32.INSTANCE.GetDlgItem(parentHwnd, user32.GetDlgCtrlID(comboBoxHwnd))

            for(rowIdx in 0..<comboBoxItemCnt) {

                // Pointer 설정을 위한 메모리 사이즈 설정
                // CB_GETLBTEXTLEN 로 각 row 의 Text 가 몇 byte 인지 확인 필요 + 1(\u0000)
                val len : Long = user32.SendMessage(controlHwnd, CB_GETLBTEXTLEN, WPARAM(rowIdx.toLong()), LPARAM()).toLong() * Native.WCHAR_SIZE + 1

                // 문자열의 길이가 0 이상인 경우만 처리
                if (len > 0) {
                    // 직접 pointerAddress 를 설정
                    val pointerAddress: Long = Native.malloc(len)

                    // CB_GETLBTEXT 로 텍스트를 pointerAddress 에 입력한다.
                    // 결과로 몇 글자의 Text 를 가지고 있는지 나타냄
                    val resultLen: LRESULT =
                        user32.SendMessage(controlHwnd, CB_GETLBTEXT, WPARAM(rowIdx.toLong()), LPARAM(pointerAddress))

                    val itemStr = String(Pointer(pointerAddress).getCharArray(0, resultLen.toInt()))

                    // 변경하고 싶은 Print 이름이 나온 경우
                    // CB_SETCURSEL 로 특정 행을 Select 한다.
                    if (itemStr == targetPrintName) {
                        user32.SendMessage(controlHwnd, CB_SETCURSEL, WPARAM(rowIdx.toLong()), LPARAM(0))
                        return true
                    }

                    // free malloc memory preventing memory leak
                    Native.free(pointerAddress)
                }
            }

            return false
        }

        fun findTargetHwndByClassName(processLst: List<ProcessInfo>, targetProcessName: String, targetClassName: String): Pair<WindowHandleInfo, List<WindowHandleInfo>>? {

            val targetProcess = processLst.firstOrNull { it.processName == targetProcessName }

            // check target process which has target Process Name
            if (targetProcess == null)
                return null

            // Find Window Handle Info by pid
            val allWindowsHwnd : List<WindowHandleInfo> = getAllWindows()

            val irosViewerParentHwnd = allWindowsHwnd.firstOrNull { it.pid == targetProcess.pid }

            if (irosViewerParentHwnd == null)
                return null

            // find child Window Handle Info by ClassName
            return Pair<WindowHandleInfo, List<WindowHandleInfo>>(irosViewerParentHwnd, findWindowExByClassName(irosViewerParentHwnd.hwnd, targetClassName))
        }
    }
}