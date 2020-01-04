package cn.yesterday17.kokoalinux.input;

import cn.yesterday17.kokoalinux.KokoaGlobal;
import cn.yesterday17.kokoalinux.config.KokoaConfig;

public class InputHelper {
    // Used by org.lwjgl.opengl.LinuxDisplay.incDisplay
    public static void setDisplay(long display) {
        InputNative.instance.setDisplay(display);
        KokoaGlobal.display = display;
    }

    // Used by org.lwjgl.opengl.LinuxDisplay.createWindow
    public static long setWindow(long window) {
        InputNative.instance.setWindow(window);
        KokoaGlobal.window = window;
        return window;
    }

    // Used by org.lwjgl.opengl.LinuxKeyboard.<init>
    public static long openIM(long display) {
        InputNative.instance.prepareLocale();
        KokoaGlobal.xim = InputNative.instance.openIM();
        return KokoaGlobal.xim;
    }

    // Used by org.lwjgl.opengl.LinuxKeyboard.<init>
    public static long createIC(long xim, long window) {
        KokoaGlobal.xic = InputNative.instance.createIC();
        return KokoaGlobal.xic;
    }

    // Used by org.lwjgl.opengl.LinuxKeyboard.destroy
    public static void closeIM(long xim) {
        KokoaGlobal.xim = 0;
        InputNative.instance.closeIM();
    }

    // Used by org.lwjgl.opengl.LinuxKeyboard.destroy
    public static void destroyIC(long xic) {
        KokoaGlobal.xic = 0;
        InputNative.instance.destroyIC();
    }

    public static void toggleIC(boolean active) {
        KokoaGlobal.xic = InputNative.instance.toggleIC(active ? 1 : 0);
    }

    public static void setDebug() {
        InputNative.instance.setDebug(KokoaConfig.debug ? 1 : 0);
    }
}
