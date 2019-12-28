package cn.yesterday17.kokoalinux.input;

import cn.yesterday17.kokoalinux.KokoaLinux;
import cn.yesterday17.kokoalinux.display.DisplayHelper;
import org.apache.logging.log4j.Level;

public class InputHelper {
    public static void createIC(boolean toActivate) {
        long xic,
                xim = DisplayHelper.getXIM(),
                window = DisplayHelper.getCurrentWindow();
        if (toActivate) {
            xic = InputNative.instance.createActiveIC(xim, window);

            if (xic == 0) {
                // Current XIM is not available, so NULL is returned
                // Sp close current XIM and open a new one
                // Also, Locale is reset, So we MUST prepareLocale again
                DisplayHelper.closeLWJGLIM();
                InputHelper.prepareLocale();
                xim = InputHelper.openIM();
                xic = InputNative.instance.createActiveIC(xim, window);
            }

            KokoaLinux.logger.printf(Level.DEBUG, "Activate XIC: %d", xic);
        } else {
            xic = InputNative.instance.createInactiveIC(xim, window);
            KokoaLinux.logger.printf(Level.DEBUG, "Inactivate XIC: %d", xic);
        }
        DisplayHelper.setXIC(xic);

        // Unexpected situation
        if (xic == 0) {
            KokoaLinux.logger.error("Unexpected zero XIC when creating IC");
            KokoaLinux.logger.printf(Level.ERROR, "XIM: %d", xim);
            KokoaLinux.logger.printf(Level.ERROR, "Window: %d", window);
        }
    }

    public static void prepareLocale() {
        InputNative.instance.setEmptyLocaleModifier();
        InputNative.instance.setLocale();
    }

    public static void destroyIC() {
        long xic = DisplayHelper.getXIC();
        KokoaLinux.logger.printf(Level.DEBUG, "Destroy XIC pointer value: %d", xic);
        InputNative.instance.destroyIC(xic);
    }

    public static long openIM() {
        long xim = InputNative.instance.openIM(DisplayHelper.getDisplay());
        DisplayHelper.setXIM(xim);
        return xim;
    }

    public static void closeIM() {
        InputNative.instance.closeIM(DisplayHelper.getXIM());
        DisplayHelper.setXIM(0);
    }
}
