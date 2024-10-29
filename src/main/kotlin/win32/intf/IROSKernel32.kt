package org.example.win32.intf

import com.sun.jna.Native
import com.sun.jna.platform.win32.*
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinNT.HANDLE
import com.sun.jna.platform.win32.Wincon
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME
import com.sun.jna.platform.win32.WinDef.DWORD
import com.sun.jna.win32.W32APIOptions


interface IROSKernel32 : StdCallLibrary, WinNT, Wincon {

    companion object {
        // kernel32 접근 인스턴스
        val INSTANCE: Kernel32 = Native.load("kernel32", Kernel32::class.java, W32APIOptions.DEFAULT_OPTIONS) as Kernel32

        // Optional: wraps every call to the native library in a synchronized block, limiting native calls to one at a time
        // 선택 사항: 네이티브 라이브러리에 대한 모든 호출을 동기화된 블록으로 전달하여 네이티브 호출을 한 번에 하나로 제한합니다
        val SYNC_INSTANCE: Kernel32 = Native.synchronizedLibrary(INSTANCE) as Kernel32


        /**
         * Takes a snapshot of the specified processes, as well as the heaps, modules, and threads used by these processes.
         * Includes all processes in the system in the snapshot. To enumerate the processes, see Process32First.
         * 현재 실행 중인 모든 Process 들의 목록을 가져오는 Option
         *
         * Ref: https://learn.microsoft.com/en-us/windows/win32/api/tlhelp32/nf-tlhelp32-createtoolhelp32snapshot
         */
        val TH32CS_SNAPPROCESS: Long = 0x00000002
    }

    fun CreateToolhelp32Snapshot(dwFlags: DWORD, th32ProcessID: DWORD): HANDLE
    fun Process32First(hSnapshot: HANDLE, lppe: Tlhelp32.PROCESSENTRY32): Boolean
    fun Process32Next(hSnapshot: HANDLE, lppe: Tlhelp32.PROCESSENTRY32): Boolean

    // 현재 시간을 구해오는 함수
    fun GetSystemTime(result: SYSTEMTIME)


    // 특정 프로세스에


}