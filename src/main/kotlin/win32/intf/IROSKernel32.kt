package org.example.win32.intf

import com.sun.jna.Native
import com.sun.jna.platform.win32.Tlhelp32
import com.sun.jna.platform.win32.WinDef.BOOL
import com.sun.jna.platform.win32.WinDef.DWORD
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinNT.HANDLE
import com.sun.jna.platform.win32.Wincon
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions
import org.example.win32.const.Win32Const.Companion.KERNEL32


interface IROSKernel32 : StdCallLibrary, WinNT, Wincon {

    companion object {
        // kernel32 접근 인스턴스
        val INSTANCE: IROSKernel32 = Native.load(
            KERNEL32, IROSKernel32::class.java,
            W32APIOptions.DEFAULT_OPTIONS
        ) as IROSKernel32

        // Optional: wraps every call to the native library in a synchronized block, limiting native calls to one at a time
        // 선택 사항: 네이티브 라이브러리에 대한 모든 호출을 동기화된 블록으로 전달하여 네이티브 호출을 한 번에 하나로 제한합니다
        val SYNC_INSTANCE: IROSKernel32 = Native.synchronizedLibrary(INSTANCE) as IROSKernel32
    }

    fun CreateToolhelp32Snapshot(dwFlags: DWORD, th32ProcessID: DWORD): HANDLE

    fun Process32First(hSnapshot: HANDLE, lppe: Tlhelp32.PROCESSENTRY32): Boolean

    fun Process32Next(hSnapshot: HANDLE, lppe: Tlhelp32.PROCESSENTRY32): Boolean

    // 핸들 닫기
    fun CloseHandle(hObject: HANDLE): BOOL
}