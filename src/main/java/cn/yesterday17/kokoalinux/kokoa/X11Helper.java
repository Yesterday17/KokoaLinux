package cn.yesterday17.kokoalinux.kokoa;

import cn.yesterday17.kokoalinux.KokoaLinux;
import com.sun.jna.Pointer;
import org.apache.logging.log4j.Level;

class X11Helper {
    static void DestroyICIfExist() {
        long xic = DisplayHelper.getXIC();
        KokoaLinux.logger.printf(Level.FATAL, "Destroy XIC pointer value: %d", xic);
        if (xic != 0) {
            X11Native.instance.XDestroyIC(new Pointer(xic));
        } else {
            KokoaLinux.logger.printf(Level.FATAL, "[DEBUG]\ndisplay: %d\ncurrentWindow: %d\nxim: %d\nxic: %d", DisplayHelper.instance.display, DisplayHelper.instance.currentWindow, DisplayHelper.instance.xim, DisplayHelper.instance.xic);
        }
    }

    static void OpenIM() {
        Pointer xim = X11Native.instance.XOpenIM(DisplayHelper.getDisplayPointer(), null, null, null);
        KokoaLinux.logger.printf(Level.FATAL, "OpenIM xim value: %d", xim.getInt(0));
        DisplayHelper.setXIM(Pointer.nativeValue(xim));
    }
}
