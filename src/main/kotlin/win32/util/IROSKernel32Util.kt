package org.example.win32.util

import com.sun.jna.platform.win32.Tlhelp32
import com.sun.jna.platform.win32.WinDef.DWORD
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinNT.HANDLE
import org.example.win32.data.ProcessInfo
import org.example.win32.intf.IROSKernel32


class IROSKernel32Util {

    companion object {

        fun getProcessLst(): List<ProcessInfo> {

            val processLst: MutableList<ProcessInfo> = mutableListOf()

            val lib = IROSKernel32.INSTANCE

            val snapshot: HANDLE = lib.CreateToolhelp32Snapshot(
                DWORD(IROSKernel32.TH32CS_SNAPPROCESS),
                DWORD(0)
            )

            val processEntry = Tlhelp32.PROCESSENTRY32.ByReference()

            if (lib.Process32First(snapshot, processEntry)) {
                do {
                    processLst.add(ProcessInfo(processEntry.th32ProcessID.toString(), String(processEntry.szExeFile).trimEnd('\u0000')))
                } while (lib.Process32Next(snapshot, processEntry))
            }

            lib.CloseHandle(snapshot)

            return processLst
        }

        fun getProcessHandle(pid: Int): HANDLE? = getProcessHandle(WinNT.PROCESS_QUERY_INFORMATION or WinNT.PROCESS_VM_READ, false, pid)

        // dwDesiredAccess : access option
        fun getProcessHandle(dwDesiredAccess: Int, bInheritHandle: Boolean, pid: Int): HANDLE? =
            IROSKernel32.INSTANCE.OpenProcess(dwDesiredAccess, bInheritHandle, pid)
    }
}