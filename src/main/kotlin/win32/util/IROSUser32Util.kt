package org.example.win32.util

import com.sun.jna.Native
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.WPARAM
import com.sun.jna.platform.win32.WinDef.LPARAM
import com.sun.jna.platform.win32.WinDef.RECT
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC
import com.sun.jna.ptr.IntByReference
import org.example.win32.data.WindowHandleInfo
import org.example.win32.intf.IROSUser32


class IROSUser32Util {

    companion object {

        fun FindWindowExByClassName(parent: HWND?, className: String?): MutableList<WindowHandleInfo> {

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
                    val windowText: CharArray = CharArray(512)
                    user32.GetWindowText(hwnd, windowText, 512)
                    val windowTextString: String = Native.toString(windowText)

                    // 빈 텍스트면 취소
                    if (windowTextString.isEmpty()) {
                        return@WNDENUMPROC true
                    }

                    // 각 윈도우 핸들의 클래스 이름
                    val className: CharArray = CharArray(512)
                    user32.GetClassName(hwnd, className, 512)
                    val classNameString: String = Native.toString(className)

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
         * 허용 이벤트 : WM_SETCURSOR
         *  - wParam : 커서가 포함된 창에 대한 핸들입니다.
         *  - lParam : lParam의 하위 단어는 커서 위치에 대한 적중 테스트 결과를 지정합니다. 가능한 값은 WM_NCHITTEST 반환 값을 참조하세요.
         *             lParam의 상위 단어는 이 이벤트를 트리거한 마우스 창 메시지(예: WM_MOUSEMOVE)를 지정합니다. 창이 메뉴 모드로 전환되면 이 값은 0입니다.
         *  - LRESULT : 애플리케이션이 이 메시지를 처리하는 경우 추가 처리를 중지하려면 TRUE를 반환하고 계속하려면 FALSE를 반환해야 합니다.
         */
        fun SendMessage(hWnd: HWND, wParam: WPARAM?, lParam: LPARAM?) {

            val user32: IROSUser32 = IROSUser32.INSTANCE

            val hWndPos: RECT = RECT()

            user32.GetWindowRect(hWnd, hWndPos)

            println(hWndPos)


            //  SendMessage 0x0020
            val SEND_MESSAGE: Int = 0x0020

            // Message 별로 파라미터가 다 다르다.
 


        }


    }
}