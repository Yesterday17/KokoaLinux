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
            ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = cv.visitMethod(access, methodName, desc, signature, exceptions);
                    String s = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(name, methodName, desc);
                    if ("setFocused".equals(s) || "func_146195_b".equals(s) || "setFocused".equals(name) || "func_146195_b".equals(name)) {
                        mv = new MethodVisitor(Opcodes.ASM5, mv) {
                            @Override
                            public void visitCode() {
                                super.visitIntInsn(Opcodes.ALOAD, 0);
                                super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiTextField", "field_146213_o", "Z");
                                super.visitIntInsn(Opcodes.ILOAD, 1);
                                super.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/yesterday17/kokoalinux/gui/GuiChange", "focus", "(ZZ)V", false);
                                super.visitCode();
                            }
                        };
                    }
                    return mv;
                }
            };
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        }
        return basicClass;
    }
}
