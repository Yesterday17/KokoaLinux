package cn.yesterday17.kokoalinux.x11;

import cn.yesterday17.kokoalinux.KokoaLinux;
import cn.yesterday17.kokoalinux.display.DisplayHelper;
import com.sun.jna.Pointer;
import org.apache.logging.log4j.Level;

public class X11Helper {
    public static void DestroyICIfExist() {
        long xic = DisplayHelper.getXIC();
        KokoaLinux.logger.printf(Level.FATAL, "Destroy XIC pointer value: %d", xic);
        if (xic != 0) {
            X11Native.instance.XDestroyIC(new Pointer(xic));
        }
    }

    public static void OpenIM() {
        Pointer xim = X11Native.instance.XOpenIM(DisplayHelper.getDisplayPointer(), null, null, null);
        KokoaLinux.logger.printf(Level.FATAL, "OpenIM xim value: %d", xim.getInt(0));
        DisplayHelper.setXIM(Pointer.nativeValue(xim));
    }
}
