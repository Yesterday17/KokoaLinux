package cn.yesterday17.kokoalinux;

import cn.yesterday17.kokoalinux.input.InputHelper;
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
        KokoaLinux.logger.debug("FOCUS");
        InputHelper.destroyIC();

        if (focused) {
            Focus.focus(this);
            InputHelper.createActivateIC();
        } else {
            Focus.release(this);
            InputHelper.createInactiveIC();
        }
    }
}