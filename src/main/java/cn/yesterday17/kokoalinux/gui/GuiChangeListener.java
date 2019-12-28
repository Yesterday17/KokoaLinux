package cn.yesterday17.kokoalinux.gui;

import cn.yesterday17.kokoalinux.KokoaLinux;
import cn.yesterday17.kokoalinux.Focus;
import cn.yesterday17.kokoalinux.input.InputHelper;
import cn.yesterday17.kokoalinux.x11.X11Helper;
import com.Axeryok.CocoaInput.plugin.IMEReceiver;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiChangeListener {
    private static boolean lastGuiIsIMEReceiver = false;

    @SubscribeEvent
    public void didChangeGui(GuiOpenEvent event) {
        // Situation: GUI switched from 'with imd' to 'no ime' and not focused
        boolean currentGuiIsIMEReceiver = event.getGui() instanceof IMEReceiver;
        if (lastGuiIsIMEReceiver && !currentGuiIsIMEReceiver && !Focus.isFocused()) {
            KokoaLinux.logger.fatal("GUI");
            X11Helper.DestroyICIfExist();
            InputHelper.createInactiveIC();
            Focus.release();
        }
        lastGuiIsIMEReceiver = currentGuiIsIMEReceiver;
    }
}
