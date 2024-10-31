package org.example

import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.LRESULT
import org.example.win32.data.ProcessInfo
import org.example.win32.data.WindowHandleInfo
import org.example.win32.intf.IROSUser32
import org.example.win32.util.IROSKernel32Util
import org.example.win32.util.IROSUser32Util


// 실행 시, 관리자 권한으로 실행해야 Handle을 가져올 수 있음
fun main() {

    val processLst: List<ProcessInfo> = IROSKernel32Util.getProcessLst()

    val targetProcess: List<String> = listOf("iprtcrsIgmprintxctrl.xgd", "IPRTCrsIgmPrintXCtrl.exe")



    targetProcess.forEach { findTargetPidHwnd(processLst, it, listOf("ComboBox")) }
}

fun findTargetPidHwnd(processLst: List<ProcessInfo>, targetProcessName: String, targetClassNameLst: List<String>) {

    println("-----------------------------------------")

    val irosViewerProcess = processLst.firstOrNull { it.processName == targetProcessName }

    if (irosViewerProcess != null) {

        val irosViewerParentHwnd = IROSUser32Util.getAllWindows().firstOrNull { it.pid == irosViewerProcess.pid }

        // 여기서 Parent Window Handle 을 찾고, 없으면 에러 처리
        if (irosViewerParentHwnd == null)
            return

        for(targetClassName in targetClassNameLst) {

            val irosViewerChildHwndLst: List<WindowHandleInfo> = IROSUser32Util.FindWindowExByClassName(irosViewerParentHwnd.hwnd, targetClassName)

            if(irosViewerChildHwndLst.isEmpty())
                continue

            println(irosViewerParentHwnd)

            for (irosChildHwnd in irosViewerChildHwndLst) {

                IROSUser32Util.SendMessage(irosChildHwnd.hwnd, null, null)

                println(irosChildHwnd)

                val dlgCtrlId:Int = IROSUser32.INSTANCE.GetDlgCtrlID(irosChildHwnd.hwnd)

                val controlHwnd: HWND = IROSUser32.INSTANCE.GetDlgItem(irosViewerParentHwnd.hwnd, dlgCtrlId)

                val windowText: CharArray = CharArray(512)
                IROSUser32.INSTANCE.GetWindowText(controlHwnd, windowText, 512)
                println(Native.toString(windowText))

                IROSUser32.INSTANCE.GetDlgItemTextA(controlHwnd, dlgCtrlId, windowText, 512)
                println(Native.toString(windowText))

                IROSUser32.INSTANCE.GetDlgItemTextA(irosViewerParentHwnd.hwnd, dlgCtrlId, windowText, 512)
                println(Native.toString(windowText))

                IROSUser32.INSTANCE.GetWindowText(controlHwnd, windowText, 512)
                println(Native.toString(windowText))

                val CB_GETLBTEXT = 0x0148
                val CB_GETLBTEXTLEN = 0x0149

                val len : LRESULT = IROSUser32.INSTANCE.SendMessage(controlHwnd, CB_GETLBTEXTLEN, WinDef.WPARAM(0L), WinDef.LPARAM())
                val comboBoxText = CharArray(len.toInt() + 1)

                if (len.toInt() > 0) {

                    val pointer = Memory(len.toLong())
                    pointer.write(0, comboBoxText, 0, len.toInt())

                    IROSUser32.INSTANCE.SendMessage(controlHwnd, CB_GETLBTEXT, WinDef.WPARAM(0), WinDef.LPARAM(Pointer.nativeValue(pointer)))

                    pointer.close()
                }


//                val length = User32.INSTANCE.SendMessage(comboBoxHandle, WinUser.CB_GETLBTEXTLEN, 0, 0)
//                val buffer = ByteArray(length.toInt() + 1) // 널 종료 문자를 위한 공간 추가
//                User32.INSTANCE.SendMessage(comboBoxHandle, WinUser.CB_GETLBTEXT, 0, buffer)
//                return String(buffer).trim { it <= ' ' } // Trim to remove any trailing spaces
//
//                println("$dlgCtrlId : $controlHwnd")

            }
        }
    }
}
