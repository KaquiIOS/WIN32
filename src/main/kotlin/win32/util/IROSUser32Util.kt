package org.example.win32.util

import com.sun.jna.Native
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC
import com.sun.jna.ptr.IntByReference
import org.example.win32.data.WindowHandleInfo
import org.example.win32.intf.IROSUser32


class IROSUser32Util {

    companion object {

        fun FindWindowExByClassName(parent: HWND?, className: String?): MutableList<WindowHandleInfo> {

            val user32: User32 = IROSUser32.INSTANCE

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

                val user32: User32 = IROSUser32.INSTANCE

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
    }
}