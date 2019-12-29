package cn.yesterday17.kokoalinux.input;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface InputNative extends Library {
    InputNative instance = Native.loadLibrary("kokoa", InputNative.class);

    long createInactiveIC(long xim, long currentWindow);

    long createActiveIC(long xim, long currentWindow);

    void setLocale();

    void setDisplayPositionCallback(DrawCallback draw);

    interface DrawCallback extends Callback {
        Pointer invoke();
    }

    // X11
    void destroyIC(long xic);

    void closeIM(long xim);

    long openIM(long display);

    void setEmptyLocaleModifier();
}