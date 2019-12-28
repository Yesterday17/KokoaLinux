package cn.yesterday17.kokoalinux.kokoa;

import cn.yesterday17.kokoalinux.KokoaLinux;
import org.apache.logging.log4j.Level;

class InputHelper {
    static void ActivateIC() {
        long xic = InputNative.instance.createActiveIC(DisplayHelper.getXIM(), DisplayHelper.getCurrentWindow(), DisplayHelper.getDisplay());
        KokoaLinux.logger.printf(Level.FATAL, "Activate XIC: %d", xic);
        DisplayHelper.setXIC(xic);
        if (xic == 0) {
            X11Helper.OpenIM();
        }
    }

    static void DeactivateIC() {
        long xic = InputNative.instance.createDeactiveIC(DisplayHelper.getXIM(), DisplayHelper.getCurrentWindow(), DisplayHelper.getDisplay());
        KokoaLinux.logger.printf(Level.FATAL, "Deactivate XIC: %d", xic);
        DisplayHelper.setXIC(xic);
    }
}
