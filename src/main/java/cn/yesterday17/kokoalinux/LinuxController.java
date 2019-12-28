package cn.yesterday17.kokoalinux;

import cn.yesterday17.kokoalinux.display.DisplayHelper;
import cn.yesterday17.kokoalinux.input.InputHelper;
import cn.yesterday17.kokoalinux.input.InputNative;
import cn.yesterday17.kokoalinux.x11.X11Helper;
import cn.yesterday17.kokoalinux.x11.X11Native;
import com.Axeryok.CocoaInput.plugin.Controller;
import com.Axeryok.CocoaInput.plugin.IMEOperator;
import com.Axeryok.CocoaInput.plugin.IMEReceiver;
import com.sun.jna.Memory;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.Display;

import java.lang.reflect.Field;

public class LinuxController implements Controller {
    LinuxController() {
        InputNative.instance.setCallback(
                (caret, chg_first, chg_length, length, iswstring, rawstring, rawwstring, primary, secondary, tertiary) -> {
                    String string = iswstring ? rawwstring.toString() : rawstring;
                    if (Focus.isFocused()) {
                        Focus.op().owner.setMarkedText(string, secondary, tertiary - secondary, 0, 0);
                    }

                    int[] point = {Display.getX(), Display.getY()};
                    Memory memory = new Memory(8L);
                    memory.write(0L, point, 0, 2);
                    return memory;
                },
                () -> {
                    if (Focus.isFocused()) {
                        Focus.op().owner.insertText("", 0, 0);
                    }
                }
        );

        // In fact it's public after tweaked
        try {
            Field f = Class.forName("org.lwjgl.opengl.LinuxEvent").getDeclaredField("enableIME");
            f.setAccessible(true);
            f.set(null, true);
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        X11Native.instance.XSetLocaleModifiers("");
        InputNative.instance.setLocale();

        // Destroy original IC & IMC
        DisplayHelper.destroyIC();
        DisplayHelper.closeIM();

        X11Helper.OpenIM();
        InputHelper.createInactiveIC();

        MinecraftForge.EVENT_BUS.register(this);
    }

    public IMEOperator generateIMEOperator(IMEReceiver owner) {
        return new LinuxIMEOperator(owner);
    }
}
