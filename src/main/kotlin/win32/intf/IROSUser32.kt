package org.example.win32.intf

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions


interface IROSUser32: StdCallLibrary, WinUser, WinNT {

    companion object {
        // kernel32 접근 인스턴스
        val INSTANCE: User32 = Native.load("user32", User32::class.java, W32APIOptions.DEFAULT_OPTIONS) as User32

        // Optional: wraps every call to the native library in a synchronized block, limiting native calls to one at a time
        // 선택 사항: 네이티브 라이브러리에 대한 모든 호출을 동기화된 블록으로 전달하여 네이티브 호출을 한 번에 하나로 제한합니다
        val SYNC_INSTANCE: User32 = Native.synchronizedLibrary(INSTANCE) as User32
    }

    // 프로세스에 Event Message 전송
    fun SendMessage(hWnd: HWND, msg: Int, wParam: WPARAM, lParam: LPARAM): LRESULT

    // 자식 창을 찾는 함수
    fun FindWindowEx(parent: HWND?, child: HWND?, className: String?, window: String?): HWND?

    // 띄워진 Window 를 순회하는 함수
    fun EnumWindows(lpEnumFunc: WNDENUMPROC?, data: Pointer?): Boolean

    // 윈도우 Text 가져오는 함수
    fun GetWindowText(hWnd: HWND?, lpString: CharArray?, nMaxCount: Int): Int
}