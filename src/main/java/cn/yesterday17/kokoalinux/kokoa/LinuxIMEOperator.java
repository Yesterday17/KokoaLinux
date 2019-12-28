package cn.yesterday17.kokoalinux.kokoa;

import cn.yesterday17.kokoalinux.KokoaLinux;
import com.Axeryok.CocoaInput.plugin.IMEOperator;
import com.Axeryok.CocoaInput.plugin.IMEReceiver;

public class LinuxIMEOperator implements IMEOperator {
    IMEReceiver owner;

    LinuxIMEOperator(IMEReceiver owner) {
        this.owner = owner;
    }

    public void discardMarkedText() {
    }

    public void removeInstance() {
    }

    public void setFocused(boolean focused) {
        KokoaLinux.logger.fatal("FOCUS");
        X11Helper.DestroyICIfExist();

        if (focused) {
            Focus.focus(this);
            InputHelper.ActivateIC();
        } else {
            Focus.release(this);
            InputHelper.DeactivateIC();
        }
    }
}