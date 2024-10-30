package org.example.win32.data

import com.sun.jna.platform.win32.WinDef.HWND

data class WindowHandleInfo(
    val titleName: String,
    val className: String,
    val pid: Int,
    val hwnd: HWND
)