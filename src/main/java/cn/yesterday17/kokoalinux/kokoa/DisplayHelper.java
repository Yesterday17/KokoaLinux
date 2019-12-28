package cn.yesterday17.kokoalinux.kokoa;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.yesterday17.kokoalinux.KokoaLinux;
import com.sun.jna.Pointer;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.Display;

class DisplayHelper {
    static DisplayHelper instance;

    private Object keyboard; // LinuxKeyboard

    // Current X11 Display pointer
    long display;
    long currentWindow;

    long xim;
    long xic;

    private DisplayHelper() {
        try {
            // Display Implementor
            Field f = Display.class.getDeclaredField("display_impl");
            f.setAccessible(true);

            // LinuxDisplay
            Object linuxDisplay = f.get(null);

            f = linuxDisplay.getClass().getDeclaredField("keyboard");
            f.setAccessible(true);
            this.keyboard = f.get(linuxDisplay);

            f = linuxDisplay.getClass().getDeclaredField("display");
            f.setAccessible(true);
            this.display = f.getLong(linuxDisplay);

            f = linuxDisplay.getClass().getDeclaredField("current_window");
            f.setAccessible(true);
            this.currentWindow = f.getLong(linuxDisplay);

            f = keyboard.getClass().getDeclaredField("xim");
            f.setAccessible(true);
            this.xim = f.getLong(keyboard);

            f = keyboard.getClass().getDeclaredField("xic");
            f.setAccessible(true);
            this.xic = f.getLong(keyboard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void refresh() {
        instance = new DisplayHelper();
        if (instance.xim == 0) {
            X11Helper.OpenIM();
        }
        if (instance.xim != 0 && instance.xic == 0) {
            instance.xic = createIC();
        }
    }

    static long getDisplay() {
        refresh();
        return instance.display;
    }

    static Pointer getDisplayPointer() {
        long d = getDisplay();
        KokoaLinux.logger.printf(Level.DEBUG, "display pointer value: %d", d);
        return new Pointer(d);
    }

    static long getCurrentWindow() {
        refresh();
        return instance.currentWindow;
    }

    static long getXIM() {
        refresh();
        return instance.xim;
    }

    static void setXIM(long ptr) {
        refresh();
        try {
            Field f = instance.keyboard.getClass().getDeclaredField("xim");
            f.setAccessible(true);
            f.setLong(instance.keyboard, ptr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static long getXIC() {
        refresh();
        return instance.xic;
    }

    static void setXIC(long ptr) {
        refresh();
        try {
            Field f = instance.keyboard.getClass().getDeclaredField("xic");
            f.setAccessible(true);
            f.setLong(instance.keyboard, ptr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static void destroyIC() {
        refresh();
        try {
            Method method = instance.keyboard.getClass().getDeclaredMethod("destroyIC", long.class);
            method.setAccessible(true);
            method.invoke(null, instance.xic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void closeIM() {
        refresh();
        try {
            Method method = instance.keyboard.getClass().getDeclaredMethod("closeIM", long.class);
            method.setAccessible(true);
            method.invoke(null, instance.xim);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long createIC() {
        try {
            Method method = instance.keyboard.getClass().getDeclaredMethod("createIC", long.class, long.class);
            method.setAccessible(true);
            return (Long) method.invoke(null, new Object[]{instance.xim, instance.currentWindow});
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static void setupIMEventMask(long display, long window, long xic) {
        try {
            Method method = instance.keyboard.getClass().getDeclaredMethod("setupIMEventMask", long.class, long.class, long.class);
            method.setAccessible(true);
            method.invoke(null, display, window, xic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}