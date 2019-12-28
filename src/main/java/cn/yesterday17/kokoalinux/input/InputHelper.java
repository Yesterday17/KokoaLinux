package cn.yesterday17.kokoalinux.input;

import cn.yesterday17.kokoalinux.KokoaLinux;
import cn.yesterday17.kokoalinux.display.DisplayHelper;
import org.apache.logging.log4j.Level;

public class InputHelper {
    public static void createActivateIC() {
        long xim = DisplayHelper.getXIM(),
                window = DisplayHelper.getCurrentWindow();
        long xic = InputNative.instance.createActiveIC(xim, window);
        KokoaLinux.logger.printf(Level.DEBUG, "Activate XIC: %d", xic);
        DisplayHelper.setXIC(xic);

        if (xic == 0) {
            KokoaLinux.logger.printf(Level.DEBUG, "xim: %d, window: %d", xim, window);
        }
    }

    public static void createInactiveIC() {
        long xic = InputNative.instance.createInactiveIC(DisplayHelper.getXIM(), DisplayHelper.getCurrentWindow());
        KokoaLinux.logger.printf(Level.DEBUG, "Inactivate XIC: %d", xic);
        DisplayHelper.setXIC(xic);
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

    public static void openIM() {
        long xim = InputNative.instance.openIM(DisplayHelper.getDisplay());
        DisplayHelper.setXIM(xim);
    }
}
