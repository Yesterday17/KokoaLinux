package cn.yesterday17.kokoalinux.display;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.lwjgl.opengl.Display;

public class DisplayHelper {
    private static DisplayHelper instance;

    private Object keyboard; // LinuxKeyboard

    // Current X11 Display pointer
    private long display;
    private long currentWindow;

    private long xim;
    private long xic;

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
    }

    public static long getDisplay() {
        refresh();
        return instance.display;
    }

    public static long getCurrentWindow() {
        refresh();
        return instance.currentWindow;
    }

    public static long getXIM() {
        refresh();
        return instance.xim;
    }

    public static void setXIM(long ptr) {
        try {
            Field f = instance.keyboard.getClass().getDeclaredField("xim");
            f.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            f.setLong(instance.keyboard, ptr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getXIC() {
        refresh();
        return instance.xic;
    }

    public static void setXIC(long ptr) {
        try {
            Field f = instance.keyboard.getClass().getDeclaredField("xic");
            f.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            f.setLong(instance.keyboard, ptr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void destroyLWJGLIC() {
        refresh();
        if (instance.xic == 0) return;

        try {
            Method method = instance.keyboard.getClass().getDeclaredMethod("destroyIC", long.class);
            method.setAccessible(true);
            method.invoke(null, instance.xic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeLWJGLIM() {
        refresh();
        if (instance.xic == 0) return;

        try {
            Method method = instance.keyboard.getClass().getDeclaredMethod("closeIM", long.class);
            method.setAccessible(true);
            method.invoke(null, instance.xim);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}