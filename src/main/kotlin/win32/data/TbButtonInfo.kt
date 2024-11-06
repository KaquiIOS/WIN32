package org.example.win32.data

import com.sun.jna.Structure
import com.sun.jna.Structure.FieldOrder
import com.sun.jna.platform.win32.BaseTSD.DWORD_PTR
import com.sun.jna.platform.win32.WTypes.LPSTR
import org.example.win32.const.Win32Const.Companion.I_IMAGECALLBACK
/**
 * typedef struct {
 *   UINT      cbSize;
 *   DWORD     dwMask;
 *   int       idCommand;
 *   int       iImage;
 *   BYTE      fsState;
 *   BYTE      fsStyle;
 *   WORD      cx;
 *   DWORD_PTR lParam;
 *   LPSTR     pszText;
 *   int       cchText;
 * } TBBUTTONINFOA, *LPTBBUTTONINFOA;
 */

@FieldOrder("cbSize", "dwMask", "idCommand", "iImage", "fsState", "fsStyle", "cx", "lParam", "pszText", "cchText")
data class TbButtonInfo(
    @JvmField var cbSize: Long = 0,
    @JvmField var dwMask: Int = 0,
    @JvmField var idCommand: Int = 0,
    @JvmField var iImage: Int = I_IMAGECALLBACK, // 이미지는 안받게 설정
    @JvmField var fsState: Byte = 0,
    @JvmField var fsStyle: Byte = 0,
    @JvmField var cx: Short = 0,
    @JvmField var lParam: LPSTR = LPSTR(""),
    @JvmField var pszText: DWORD_PTR = DWORD_PTR(0),
    @JvmField var cchText: Int = 512
): Structure()