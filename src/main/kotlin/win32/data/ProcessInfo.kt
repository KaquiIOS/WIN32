package org.example.win32.data

data class ProcessInfo(
    val pid: Int,
    val parentPid: Int,
    val processName: String
)