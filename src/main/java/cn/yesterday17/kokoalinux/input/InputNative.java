package cn.yesterday17.kokoalinux.input;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;

public interface InputNative extends Library {
    InputNative instance = Native.loadLibrary("kokoa", InputNative.class);

    long createInactiveIC(long xim, long currentWindow);

    long createActiveIC(long xim, long currentWindow);

    void setLocale();

    void setCallback(DrawCallback draw, DoneCallback done);

    interface DoneCallback extends Callback {
        void invoke();
    }

    interface DrawCallback extends Callback {
        Pointer invoke(int param1Int1, int param1Int2, int param1Int3, short param1Short, boolean param1Boolean, String param1String, WString param1WString, int param1Int4, int param1Int5, int param1Int6);
    }

    // X11
    void destroyIC(long xic);

    void closeIM(long xim);

    long openIM(long display);

    void setEmptyLocaleModifier();
}