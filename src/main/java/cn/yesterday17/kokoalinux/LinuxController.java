package cn.yesterday17.kokoalinux;

import cn.yesterday17.kokoalinux.display.DisplayHelper;
import cn.yesterday17.kokoalinux.input.InputHelper;
import cn.yesterday17.kokoalinux.input.InputNative;
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

        // Destroy original IC & IMC
        DisplayHelper.destroyLWJGLIC();
        DisplayHelper.closeLWJGLIM();

        InputHelper.prepareLocale();
        InputHelper.openIM();
        InputHelper.createIC(false);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public IMEOperator generateIMEOperator(IMEReceiver owner) {
        return new LinuxIMEOperator(owner);
    }

    @SubscribeEvent
    public void didChangeGui(GuiOpenEvent event) {
        boolean currentGuiIsIMEReceiver = event.getGui() instanceof IMEReceiver;
        if (!currentGuiIsIMEReceiver) {
            KokoaLinux.logger.debug("GUI");
            InputHelper.destroyIC();
            InputHelper.createIC(false);
            Focus.release();
        }
    }
}
