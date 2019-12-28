package cn.yesterday17.kokoalinux.input;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;

public interface InputNative extends Library {
    InputNative instance = Native.loadLibrary("kokoalinux", InputNative.class);

    long createDeactiveIC(long xim, long currentWindow, long display);

    long createActiveIC(long xim, long currentWindow, long display);

    void setLocale();

    void setCallback(DrawCallback draw, DoneCallback done);

    interface DoneCallback extends Callback {
        void invoke();
    }

    interface DrawCallback extends Callback {
        Pointer invoke(int param1Int1, int param1Int2, int param1Int3, short param1Short, boolean param1Boolean, String param1String, WString param1WString, int param1Int4, int param1Int5, int param1Int6);
    }
}