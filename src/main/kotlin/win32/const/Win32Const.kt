package org.example.win32.const

import com.sun.jna.Platform

/**
 * Win32 관련 상수 모음
 */
class Win32Const {

    companion object {

        // DropBox Item Count 획득
        const val CB_GETCOUNT = 0x0146

        // DropBox Item Text 획득
        const val CB_GETLBTEXT = 0x0148

        // DropBox Item Text 길이 획득
        const val CB_GETLBTEXTLEN = 0x0149

        // DropBox 선택된 Item Index 획득
        const val CB_SETCURSEL = 0x014e

        /**
         * Takes a snapshot of the specified processes, as well as the heaps, modules, and threads used by these processes.
         * Includes all processes in the system in the snapshot. To enumerate the processes, see Process32First.
         * 현재 실행 중인 모든 Process 들의 목록을 가져오는 Option
         *
         * Ref: https://learn.microsoft.com/en-us/windows/win32/api/tlhelp32/nf-tlhelp32-createtoolhelp32snapshot
         */
        const val TH32CS_SNAPPROCESS: Long = 0x00000002

        // DLL 명
        const val USER32: String = "user32"
        const val KERNEL32: String = "kernel32"
    }
}