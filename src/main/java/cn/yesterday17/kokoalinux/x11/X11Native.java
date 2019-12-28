package cn.yesterday17.kokoalinux.x11;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface X11Native extends Library {
    X11Native instance = Native.loadLibrary("X11", X11Native.class);

    String XSetLocaleModifiers(String paramString);

    Pointer XOpenIM(Pointer display, Pointer xrmDatabase, String mut_1, String mut_2);

    long XCreateIC(Pointer paramPointer);

    void XDestroyIC(Pointer paramPointer);
}