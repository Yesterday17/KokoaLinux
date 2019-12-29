package cn.yesterday17.kokoalinux.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.*;

public class GuiTextFieldTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.gui.GuiTextField")) {
            ClassReader cr = new ClassReader(basicClass);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = new GuiTextFieldVisitor(cw, name);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        }
        return basicClass;
    }

    public static class GuiTextFieldVisitor extends ClassVisitor {
        private String name;

        GuiTextFieldVisitor(ClassVisitor cv, String name) {
            super(Opcodes.ASM5, cv);
            this.name = name;
        }

        @Override
        public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, methodName, desc, signature, exceptions);
            String s = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(name, methodName, desc);
            if ("setFocused".equals(s) || "func_146195_b".equals(s) || "setFocused".equals(name) || "func_146195_b".equals(name)) {
                mv = new GuiTextFieldSetFocusMethodAdapter(mv);
            }
            return mv;
        }
    }

    public static class GuiTextFieldSetFocusMethodAdapter extends MethodVisitor {
        GuiTextFieldSetFocusMethodAdapter(MethodVisitor mv) {
            super(Opcodes.ASM5, mv);
        }

        @Override
        public void visitCode() {
            this.visitIntInsn(Opcodes.ALOAD, 0); // this -> stack
            this.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiTextField", "field_146213_o", "Z"); // stack -> this ; old_focused -> stack
            this.visitIntInsn(Opcodes.ILOAD, 1); // now_focused -> stack
            super.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "cn/yesterday17/kokoalinux/gui/GuiChange",
                    "focus",
                    "(ZZ)V",
                    false);
            super.visitCode();
        }
    }
}
