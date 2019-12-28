package cn.yesterday17.kokoalinux.input;

import cn.yesterday17.kokoalinux.KokoaLinux;
import cn.yesterday17.kokoalinux.display.DisplayHelper;
import org.apache.logging.log4j.Level;

public class InputHelper {
    public static void createActivateIC() {
        long xim = DisplayHelper.getXIM(),
                window = DisplayHelper.getCurrentWindow(),
                display = DisplayHelper.getDisplay();
        long xic = InputNative.instance.createActiveIC(xim, window, display);
        KokoaLinux.logger.printf(Level.FATAL, "Activate XIC: %d", xic);
        DisplayHelper.setXIC(xic);

        if (xic == 0) {
            KokoaLinux.logger.printf(Level.FATAL, "xim: %d, window: %d, display: %d", xim, window, display);
        }
    }

    public static void createInactiveIC() {
        long xic = InputNative.instance.createDeactiveIC(DisplayHelper.getXIM(), DisplayHelper.getCurrentWindow(), DisplayHelper.getDisplay());
        KokoaLinux.logger.printf(Level.FATAL, "Inactivate XIC: %d", xic);
        DisplayHelper.setXIC(xic);
    }
}
