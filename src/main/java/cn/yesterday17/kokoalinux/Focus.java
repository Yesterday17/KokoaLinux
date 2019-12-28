package cn.yesterday17.kokoalinux;

public class Focus {
    private static LinuxIMEOperator focused = null;

    static void focus(LinuxIMEOperator op) {
        focused = op;
    }

    public static void release() {
        focused = null;
    }

    static void release(LinuxIMEOperator op) {
        if (op == focused) {
            release();
        }
    }

    public static boolean isFocused() {
        return focused != null;
    }

    static LinuxIMEOperator op() {
        return focused;
    }
}
