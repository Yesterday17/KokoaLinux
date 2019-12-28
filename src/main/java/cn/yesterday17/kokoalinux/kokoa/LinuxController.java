package cn.yesterday17.kokoalinux.kokoa;

import cn.yesterday17.kokoalinux.KokoaLinux;
import com.Axeryok.CocoaInput.plugin.Controller;
import com.Axeryok.CocoaInput.plugin.IMEOperator;
import com.Axeryok.CocoaInput.plugin.IMEReceiver;
import com.sun.jna.Memory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

import java.lang.reflect.Field;

public class LinuxController implements Controller {
    public LinuxController() {
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

        // Disable IM at first
        DisplayHelper.destroyIC();
        DisplayHelper.closeIM();

        X11Helper.OpenIM();
        InputHelper.DeactivateIC();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private static boolean lastGuiIsIMEReceiver = false;

    @SubscribeEvent
    public void didChangeGui(GuiOpenEvent event) {
        // Situation: GUI switched from 'with imd' to 'no ime' and not focused
        boolean currentGuiIsIMEReceiver = event.getGui() instanceof IMEReceiver;
        if (lastGuiIsIMEReceiver && !currentGuiIsIMEReceiver && !Focus.isFocused()) {
            KokoaLinux.logger.fatal("GUI");
            X11Helper.DestroyICIfExist();
            InputHelper.DeactivateIC();
            Focus.release();
        }
        lastGuiIsIMEReceiver = currentGuiIsIMEReceiver;
    }

    public IMEOperator generateIMEOperator(IMEReceiver owner) {
        return new LinuxIMEOperator(owner);
    }
}
