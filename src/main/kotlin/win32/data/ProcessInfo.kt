package org.example.win32.data

import com.sun.jna.platform.win32.WinDef.DWORD

data class ProcessInfo(
    val pid: String,
    val processName: String
)