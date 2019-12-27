package cn.yesterday17.kokoalinux.kokoa;

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
        X11Native.instance.XDestroyIC(DisplayHelper.getXICPointer());
        long xic;

        if (focused) {
            LinuxController.focusedOperator = this;
            xic = InputNative.instance.createActiveIC(DisplayHelper.getXIM(), DisplayHelper.getCurrentWindow(), DisplayHelper.getDisplay());
        } else {
            if (LinuxController.focusedOperator == this) {
                LinuxController.focusedOperator = null;
            }
            xic = InputNative.instance.createDeactiveIC(DisplayHelper.getXIM(), DisplayHelper.getCurrentWindow(), DisplayHelper.getDisplay());
        }

        DisplayHelper.setXIC(xic);
    }
}