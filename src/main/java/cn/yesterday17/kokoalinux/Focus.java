package cn.yesterday17.kokoalinux;

class Focus {
    private static LinuxIMEOperator focused = null;

    private static void focus(LinuxIMEOperator op) {
        focused = op;
    }

    static void release() {
        focused = null;
    }

    private static void release(LinuxIMEOperator op) {
        if (op == focused) {
            release();
        }
    }

    static void operate(LinuxIMEOperator op, boolean isFocused) {
        if (isFocused) {
            focus(op);
        } else {
            release(op);
        }
    }

    static boolean isFocused() {
        return focused != null;
    }

    static LinuxIMEOperator op() {
        return focused;
    }
}
