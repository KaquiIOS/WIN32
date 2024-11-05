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

        // Edit 전체 텍스트 수정
        const val WM_SETTEXT = 0x000C

        // Left button click
        const val WM_LBUTTONDOWN = 0x0201
        const val WM_LBUTTONUP = 0x0202

        // Right button click
        const val WM_RBUTTONDOWN = 0x0204
        const val WM_RBUTTONUP = 0x0205

        // ******Button Option********
        // 왼쪽 버튼 클릭
        const val MK_LBUTTON = 0x0001

        // 마우스 왼쪽 단추가 눌려져 있습니다.
        const val MK_MBUTTON = 0x0010

        // Ctrl 키가 눌려져 있습니다.
        const val MK_CONTROL = 0x0008

        // 마우스 가운데 단추가 눌려져 있습니다.
        const val MK_RBUTTON = 0x0002

        // 마우스 오른쪽 단추가 눌려져 있습니다.
        const val MK_SHIFT = 0x0004

        // 첫 번째 X 단추가 눌려져 있습니다.
        const val MK_XBUTTON1 = 0x0020

        // 첫 번째 X 단추가 눌려져 있습니다.
        const val MK_XBUTTON2 = 0x0040
        // ******************************

        // REDRAW
        const val WM_SETREDRAW = 0x000b

        // enter
        const val VK_RETURN	= 0x0D

        const val WM_KEYDOWN = 0x0100
        const val WM_KEYUP = 0x0101

        // 파일탐색기 클래스 (win10)
        // const val EXPLORER_CLASS = "CabinetWClass"
        const val EXPLORER_CLASS = "Microsoft.UI.Content.DesktopChildSiteBridge" // win11

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