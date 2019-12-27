package cn.yesterday17.kokoalinux.kokoa;

import com.Axeryok.CocoaInput.plugin.Controller;
import com.Axeryok.CocoaInput.plugin.IMEOperator;
import com.Axeryok.CocoaInput.plugin.IMEReceiver;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

import java.lang.reflect.Field;

public class LinuxController implements Controller {
    static LinuxIMEOperator focusedOperator = null;

    public LinuxController() {
        InputNative.instance.setCallback(
                (caret, chg_first, chg_length, length, iswstring, rawstring, rawwstring, primary, secondary, tertiary) -> {
                    String string = iswstring ? rawwstring.toString() : rawstring;
                    if (LinuxController.focusedOperator != null) {
                        LinuxController.focusedOperator.owner.setMarkedText(string, secondary, tertiary - secondary, 0, 0);
                    }

                    int[] point = {Display.getX(), Display.getY()};
                    Memory memory = new Memory(8L);
                    memory.write(0L, point, 0, 2);
                    return memory;
                },
                () -> {
                    System.out.println("done");
                    if (LinuxController.focusedOperator != null)
                        LinuxController.focusedOperator.owner.insertText("", 0, 0);
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
        DisplayHelper.destroyIC();
        DisplayHelper.closeIM();
        Pointer ptr = X11Native.instance.XOpenIM(new Pointer(DisplayHelper.getDisplay()), null, null, null);
        DisplayHelper.setXIM(Pointer.nativeValue(ptr));

        long xic = InputNative.instance.createDeactiveIC(DisplayHelper.getXIM(), DisplayHelper.getCurrentWindow(), DisplayHelper.getDisplay());
        DisplayHelper.setXIC(xic);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void didChangeGui(GuiOpenEvent event) {
        // TODO: Fix fullscreen input
        if (!(event.getGui() instanceof IMEReceiver) && !Minecraft.getMinecraft().isFullScreen()) {
            X11Native.instance.XDestroyIC(DisplayHelper.getXICPointer());
            long xic = InputNative.instance.createDeactiveIC(DisplayHelper.getXIM(), DisplayHelper.getCurrentWindow(), DisplayHelper.getDisplay());
            DisplayHelper.setXIC(xic);
            focusedOperator = null;
        }
    }

    public IMEOperator generateIMEOperator(IMEReceiver owner) {
        return new LinuxIMEOperator(owner);
    }
}
