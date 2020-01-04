package cn.yesterday17.kokoalinux.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.objectweb.asm.*;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Set;

public class LwjglTransformer implements IClassTransformer {
    public static void prepare(LaunchClassLoader classLoader) {
        try {
            // At this time, libraries like lwjgl has been loaded
            // That's Why LaunchWrapper added them to ClassLoaderExclusion
            // Because they'll cause an exception after reloading.

            // Protect org.lwjgl.(?!opengl)
            String[] lwjgl = new String[]{
                    "B", "D", "J", "L", "M", "P", "S", "W", // Classes
                    "input", "openal", "opencl", "opengles",// Packages
            };
            for (String append : lwjgl) {
                classLoader.addTransformerExclusion("org.lwjgl." + append);
            }

            // Protect org.lwjgl.opengl.(?!LinuxEvent|LinuxDisplay|LinuxKeyboard)
            String[] opengl = new String[]{
                    "A", "B", "C", "D", "E", "F", "G", "I", "K",
                    "LinuxA", "LinuxB", "LinuxC", "LinuxDisplayPeerInfo", "LinuxKeycodes", "LinuxM", "LinuxP", "LinuxA",
                    "M", "N", "O", "P", "R", "S", "U", "W", "X",
            };
            for (String append : opengl) {
                classLoader.addTransformerExclusion("org.lwjgl.opengl." + append);
            }

            // Make org.lwjgl Tweakable
            Field f = LaunchClassLoader.class.getDeclaredField("classLoaderExceptions");
            f.setAccessible(true);
            Set<String> classLoaderExceptions = (Set<String>) f.get(classLoader);
            classLoaderExceptions.remove("org.lwjgl.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (transformedName) {
            case "org.lwjgl.opengl.LinuxEvent":
                // TODO: ASM instead of base64
                return LinuxEventTweak.getBytes();
            case "org.lwjgl.opengl.LinuxDisplay": {
                ClassReader cr = new ClassReader(basicClass);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {
                    @Override
                    public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
                        MethodVisitor mv = cv.visitMethod(access, methodName, desc, signature, exceptions);
                        switch (methodName) {
                            case "incDisplay":
                                mv = new MethodVisitor(Opcodes.ASM5, mv) {
                                    @Override
                                    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                                        super.visitFieldInsn(opcode, owner, name, desc);
                                        if (name.equals("display")) {
                                            // Update latest display
                                            super.visitFieldInsn(Opcodes.GETSTATIC, owner, "display", "J");
                                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/yesterday17/kokoalinux/input/InputHelper", "setDisplay", "(J)V", false);
                                        }
                                    }
                                };
                                break;
                            case "createWindow":
                                mv = new MethodVisitor(Opcodes.ASM5, mv) {
                                    @Override
                                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                                        if (name.equals("nCreateWindow")) {
                                            // Set window to what nCreateWindow returns
                                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/yesterday17/kokoalinux/input/InputHelper", "setWindow", "(J)J", false);
                                        }
                                    }
                                };
//                                break;
                            case "decDisplay":
                                mv = new MethodVisitor(Opcodes.ASM5, mv) {
                                    @Override
                                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                                        if (name.equals("nDestroyWindow")) {
                                            // Set window to 0 when destroy window
                                            super.visitVarInsn(Opcodes.LLOAD, 0);
                                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/yesterday17/kokoalinux/input/InputHelper", "setWindow", "(J)V", false);
                                        }
                                    }
                                };
                                break;
                        }
                        return mv;
                    }
                };
                cr.accept(cv, ClassReader.EXPAND_FRAMES);
                return cw.toByteArray();
            }
            case "org.lwjgl.opengl.LinuxKeyboard": {
                ClassReader cr = new ClassReader(basicClass);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
                        switch (name) {
                            case "<init>":
                                mv = new MethodVisitor(Opcodes.ASM5, mv) {
                                    @Override
                                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                                        if (name.equals("openIM") || name.equals("createIC")) {
                                            // Replace with libkokoa ones
                                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/yesterday17/kokoalinux/input/InputHelper", name, desc, false);
                                        } else {
                                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                                        }
                                    }
                                };
                                break;
                            case "destroy":
                                mv = new MethodVisitor(Opcodes.ASM5, mv) {
                                    @Override
                                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                                        if (name.equals("closeIM") || name.equals("destroyIC")) {
                                            // Replace with libkokoa ones
                                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/yesterday17/kokoalinux/input/InputHelper", name, desc, false);
                                        } else {
                                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                                        }
                                    }
                                };
                                break;
                        }
                        return mv;
                    }
                };
                cr.accept(cv, ClassReader.EXPAND_FRAMES);
                return cw.toByteArray();
            }
        }
        return basicClass;
    }

    static class LinuxEventTweak {
        static byte[] getBytes() {
            return Base64.getDecoder().decode(
                    "yv66vgAAADEAywoAJAChCQAjAKIKACMAowkAIwCkCgClAKYKAKUApwoApQCoCgAjAKkJACMAqgoA" +
                            "IwCrCgAjAKwKACMArQoAIwCuCgAjAK8KACMAsAoAIwCxCgAjALIKACMAswoAIwC0CgAjALUKACMA" +
                            "tgoAIwC3CgAjALgKACMAuQoAIwC6CgAjALsKACMAvAoAIwC9CgAjAL4KACMAvwoAIwDACgAjAMEK" +
                            "ACMAwgoAIwDDBwDEBwDFAQAHRm9jdXNJbgEAAUkBAA1Db25zdGFudFZhbHVlAwAAAAkBAAhGb2N1" +
                            "c091dAMAAAAKAQAIS2V5UHJlc3MDAAAAAgEACktleVJlbGVhc2UDAAAAAwEAC0J1dHRvblByZXNz" +
                            "AwAAAAQBAA1CdXR0b25SZWxlYXNlAwAAAAUBAAxNb3Rpb25Ob3RpZnkDAAAABgEAC0VudGVyTm90" +
                            "aWZ5AwAAAAcBAAtMZWF2ZU5vdGlmeQMAAAAIAQALVW5tYXBOb3RpZnkDAAAAEgEACU1hcE5vdGlm" +
                            "eQMAAAATAQAGRXhwb3NlAwAAAAwBAA9Db25maWd1cmVOb3RpZnkDAAAAFgEADUNsaWVudE1lc3Nh" +
                            "Z2UDAAAAIQEADGV2ZW50X2J1ZmZlcgEAFUxqYXZhL25pby9CeXRlQnVmZmVyOwEAEmZpbmFsRXZl" +
                            "bnRGaWx0ZXJlZAEAAVoBAAllbmFibGVJTUUBAAY8aW5pdD4BAAMoKVYBAARDb2RlAQAPTGluZU51" +
                            "bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAEdGhpcwEAHUxvcmcvbHdqZ2wvb3Blbmds" +
                            "L0xpbnV4RXZlbnQ7AQARY3JlYXRlRXZlbnRCdWZmZXIBABcoKUxqYXZhL25pby9CeXRlQnVmZmVy" +
                            "OwEACGNvcHlGcm9tAQAgKExvcmcvbHdqZ2wvb3BlbmdsL0xpbnV4RXZlbnQ7KVYBAAVldmVudAEA" +
                            "A3BvcwEACWV2ZW50X3BvcwEACmdldFBlbmRpbmcBAAQoSilJAQAJc2VuZEV2ZW50AQAHKEpKWkop" +
                            "VgEAB2Rpc3BsYXkBAAFKAQAGd2luZG93AQAJcHJvcGFnYXRlAQAKZXZlbnRfbWFzawEACm5TZW5k" +
                            "RXZlbnQBABwoTGphdmEvbmlvL0J5dGVCdWZmZXI7SkpaSilWAQALZmlsdGVyRXZlbnQBAAQoSila" +
                            "AQAMbkZpbHRlckV2ZW50AQAZKExqYXZhL25pby9CeXRlQnVmZmVyO0opWgEADGZpbHRlckV2ZW50" +
                            "WAEACW5leHRFdmVudAEABChKKVYBAApuTmV4dEV2ZW50AQAZKEpMamF2YS9uaW8vQnl0ZUJ1ZmZl" +
                            "cjspVgEAB2dldFR5cGUBAAMoKUkBAAhuR2V0VHlwZQEAGChMamF2YS9uaW8vQnl0ZUJ1ZmZlcjsp" +
                            "SQEACWdldFdpbmRvdwEAAygpSgEACm5HZXRXaW5kb3cBABgoTGphdmEvbmlvL0J5dGVCdWZmZXI7" +
                            "KUoBAAlzZXRXaW5kb3cBAApuU2V0V2luZG93AQAZKExqYXZhL25pby9CeXRlQnVmZmVyO0opVgEA" +
                            "DGdldEZvY3VzTW9kZQEADW5HZXRGb2N1c01vZGUBAA5nZXRGb2N1c0RldGFpbAEAD25HZXRGb2N1" +
                            "c0RldGFpbAEAFGdldENsaWVudE1lc3NhZ2VUeXBlAQAVbkdldENsaWVudE1lc3NhZ2VUeXBlAQAN" +
                            "Z2V0Q2xpZW50RGF0YQEABChJKUkBAAVpbmRleAEADm5HZXRDbGllbnREYXRhAQAZKExqYXZhL25p" +
                            "by9CeXRlQnVmZmVyO0kpSQEAD2dldENsaWVudEZvcm1hdAEAEG5HZXRDbGllbnRGb3JtYXQBAA1n" +
                            "ZXRCdXR0b25UaW1lAQAObkdldEJ1dHRvblRpbWUBAA5nZXRCdXR0b25TdGF0ZQEAD25HZXRCdXR0" +
                            "b25TdGF0ZQEADWdldEJ1dHRvblR5cGUBAA5uR2V0QnV0dG9uVHlwZQEAD2dldEJ1dHRvbkJ1dHRv" +
                            "bgEAEG5HZXRCdXR0b25CdXR0b24BAA1nZXRCdXR0b25Sb290AQAObkdldEJ1dHRvblJvb3QBAA5n" +
                            "ZXRCdXR0b25YUm9vdAEAD25HZXRCdXR0b25YUm9vdAEADmdldEJ1dHRvbllSb290AQAPbkdldEJ1" +
                            "dHRvbllSb290AQAKZ2V0QnV0dG9uWAEAC25HZXRCdXR0b25YAQAKZ2V0QnV0dG9uWQEAC25HZXRC" +
                            "dXR0b25ZAQANZ2V0S2V5QWRkcmVzcwEADm5HZXRLZXlBZGRyZXNzAQAKZ2V0S2V5VGltZQEAC25H" +
                            "ZXRLZXlUaW1lAQAKZ2V0S2V5VHlwZQEAC25HZXRLZXlUeXBlAQANZ2V0S2V5S2V5Q29kZQEADm5H" +
                            "ZXRLZXlLZXlDb2RlAQALZ2V0S2V5U3RhdGUBAAxuR2V0S2V5U3RhdGUBAAg8Y2xpbml0PgEAClNv" +
                            "dXJjZUZpbGUBAA9MaW51eEV2ZW50LmphdmEMAEgASQwARQBGDABPAFAMAEMARAcAxgwAxwBrDADI" +
                            "AMkMAMcAygwAXwBgDABHAEYMAGMAZAwAaABpDABlAGIMAGwAbQwAcABxDABzAHQMAHYAbQwAeABt" +
                            "DAB6AHEMAH4AfwwAgQBtDACDAHEMAIUAbQwAhwBtDACJAG0MAIsAcQwAjQBtDACPAG0MAJEAbQwA" +
                            "kwBtDACVAHEMAJcAbQwAmQBtDACbAG0MAJ0AbQEAG29yZy9sd2pnbC9vcGVuZ2wvTGludXhFdmVu" +
                            "dAEAEGphdmEvbGFuZy9PYmplY3QBABNqYXZhL25pby9CeXRlQnVmZmVyAQAIcG9zaXRpb24BAANw" +
                            "dXQBACwoTGphdmEvbmlvL0J5dGVCdWZmZXI7KUxqYXZhL25pby9CeXRlQnVmZmVyOwEAFChJKUxq" +
                            "YXZhL25pby9CdWZmZXI7ADAAIwAkAAAAEQAZACUAJgABACcAAAACACgAGQApACYAAQAnAAAAAgAq" +
                            "ABkAKwAmAAEAJwAAAAIALAAZAC0AJgABACcAAAACAC4AGQAvACYAAQAnAAAAAgAwABkAMQAmAAEA" +
                            "JwAAAAIAMgAZADMAJgABACcAAAACADQAGQA1ACYAAQAnAAAAAgA2ABkANwAmAAEAJwAAAAIAOAAZ" +
                            "ADkAJgABACcAAAACADoAGQA7ACYAAQAnAAAAAgA8ABkAPQAmAAEAJwAAAAIAPgAZAD8AJgABACcA" +
                            "AAACAEAAGQBBACYAAQAnAAAAAgBCABIAQwBEAAAAAgBFAEYAAAAJAEcARgAAADgAAABIAEkAAQBK" +
                            "AAAARwACAAEAAAARKrcAASoDtQACKrgAA7UABLEAAAACAEsAAAASAAQAAABAAAQAPQAJAEEAEABC" +
                            "AEwAAAAMAAEAAAARAE0ATgAAAQoATwBQAAAAAQBRAFIAAQBKAAAAiwACAAQAAAAvKrQABLYABT0r" +
                            "tAAEtgAFPiq0AAQrtAAEtgAGVyq0AAQctgAHVyu0AAQdtgAHV7EAAAACAEsAAAAaAAYAAABGAAgA" +
                            "RwAQAEgAHABJACUASgAuAEsATAAAACoABAAAAC8ATQBOAAAAAAAvAFMATgABAAgAJwBUACYAAgAQ" +
                            "AB8AVQAmAAMBCQBWAFcAAAABAFgAWQABAEoAAABkAAgACAAAAA4qtAAEHyEVBRYGuAAIsQAAAAIA" +
                            "SwAAAAoAAgAAAFAADQBRAEwAAAA0AAUAAAAOAE0ATgAAAAAADgBaAFsAAQAAAA4AXABbAAMAAAAO" +
                            "AF0ARgAFAAAADgBeAFsABgEKAF8AYAAAAAEAYQBiAAEASgAAADkAAQADAAAABSq0AAKsAAAAAgBL" +
                            "AAAABgABAAAAVQBMAAAAFgACAAAABQBNAE4AAAAAAAUAXABbAAEBCgBjAGQAAAABAGUAYgABAEoA" +
                            "AABHAAMAAwAAABOyAAmZAA4qtAAEH7gACqcABAOsAAAAAgBLAAAABgABAAAAWgBMAAAAFgACAAAA" +
                            "EwBNAE4AAAAAABMAXABbAAEAAQBmAGcAAQBKAAAATgAEAAMAAAASHyq0AAS4AAsqKgm2AAy1AAKx" +
                            "AAAAAgBLAAAADgADAAAAXgAIAF8AEQBgAEwAAAAWAAIAAAASAE0ATgAAAAAAEgBaAFsAAQEKAGgA" +
                            "aQAAAAEAagBrAAEASgAAADIAAQABAAAACCq0AAS4AA2sAAAAAgBLAAAABgABAAAAZABMAAAADAAB" +
                            "AAAACABNAE4AAAEKAGwAbQAAAAEAbgBvAAEASgAAADIAAgABAAAACCq0AAS4AA6tAAAAAgBLAAAA" +
                            "BgABAAAAaQBMAAAADAABAAAACABNAE4AAAEKAHAAcQAAAAEAcgBnAAEASgAAAEEAAwADAAAACSq0" +
                            "AAQfuAAPsQAAAAIASwAAAAoAAgAAAG4ACABvAEwAAAAWAAIAAAAJAE0ATgAAAAAACQBcAFsAAQEK" +
                            "AHMAdAAAAAEAdQBrAAEASgAAADIAAQABAAAACCq0AAS4ABCsAAAAAgBLAAAABgABAAAAdQBMAAAA" +
                            "DAABAAAACABNAE4AAAEKAHYAbQAAAAEAdwBrAAEASgAAADIAAQABAAAACCq0AAS4ABGsAAAAAgBL" +
                            "AAAABgABAAAAegBMAAAADAABAAAACABNAE4AAAEKAHgAbQAAAAEAeQBvAAEASgAAADIAAgABAAAA" +
                            "CCq0AAS4ABKtAAAAAgBLAAAABgABAAAAgQBMAAAADAABAAAACABNAE4AAAEKAHoAcQAAAAEAewB8" +
                            "AAEASgAAAD0AAgACAAAACSq0AAQbuAATrAAAAAIASwAAAAYAAQAAAIYATAAAABYAAgAAAAkATQBO" +
                            "AAAAAAAJAH0AJgABAQoAfgB/AAAAAQCAAGsAAQBKAAAAMgABAAEAAAAIKrQABLgAFKwAAAACAEsA" +
                            "AAAGAAEAAACLAEwAAAAMAAEAAAAIAE0ATgAAAQoAgQBtAAAAAQCCAG8AAQBKAAAAMgACAAEAAAAI" +
                            "KrQABLgAFa0AAAACAEsAAAAGAAEAAACSAEwAAAAMAAEAAAAIAE0ATgAAAQoAgwBxAAAAAQCEAGsA" +
                            "AQBKAAAAMgABAAEAAAAIKrQABLgAFqwAAAACAEsAAAAGAAEAAACXAEwAAAAMAAEAAAAIAE0ATgAA" +
                            "AQoAhQBtAAAAAQCGAGsAAQBKAAAAMgABAAEAAAAIKrQABLgAF6wAAAACAEsAAAAGAAEAAACcAEwA" +
                            "AAAMAAEAAAAIAE0ATgAAAQoAhwBtAAAAAQCIAGsAAQBKAAAAMgABAAEAAAAIKrQABLgAGKwAAAAC" +
                            "AEsAAAAGAAEAAAChAEwAAAAMAAEAAAAIAE0ATgAAAQoAiQBtAAAAAQCKAG8AAQBKAAAAMgACAAEA" +
                            "AAAIKrQABLgAGa0AAAACAEsAAAAGAAEAAACmAEwAAAAMAAEAAAAIAE0ATgAAAQoAiwBxAAAAAQCM" +
                            "AGsAAQBKAAAAMgABAAEAAAAIKrQABLgAGqwAAAACAEsAAAAGAAEAAACrAEwAAAAMAAEAAAAIAE0A" +
                            "TgAAAQoAjQBtAAAAAQCOAGsAAQBKAAAAMgABAAEAAAAIKrQABLgAG6wAAAACAEsAAAAGAAEAAACw" +
                            "AEwAAAAMAAEAAAAIAE0ATgAAAQoAjwBtAAAAAQCQAGsAAQBKAAAAMgABAAEAAAAIKrQABLgAHKwA" +
                            "AAACAEsAAAAGAAEAAAC1AEwAAAAMAAEAAAAIAE0ATgAAAQoAkQBtAAAAAQCSAGsAAQBKAAAAMgAB" +
                            "AAEAAAAIKrQABLgAHawAAAACAEsAAAAGAAEAAAC6AEwAAAAMAAEAAAAIAE0ATgAAAQoAkwBtAAAA" +
                            "AQCUAG8AAQBKAAAAMgACAAEAAAAIKrQABLgAHq0AAAACAEsAAAAGAAEAAADBAEwAAAAMAAEAAAAI" +
                            "AE0ATgAAAQoAlQBxAAAAAQCWAG8AAQBKAAAAMwACAAEAAAAJKrQABLgAH4WtAAAAAgBLAAAABgAB" +
                            "AAAAxgBMAAAADAABAAAACQBNAE4AAAEKAJcAbQAAAAEAmABrAAEASgAAADIAAQABAAAACCq0AAS4" +
                            "ACCsAAAAAgBLAAAABgABAAAAywBMAAAADAABAAAACABNAE4AAAEKAJkAbQAAAAEAmgBrAAEASgAA" +
                            "ADIAAQABAAAACCq0AAS4ACGsAAAAAgBLAAAABgABAAAA0ABMAAAADAABAAAACABNAE4AAAEKAJsA" +
                            "bQAAAAEAnABrAAEASgAAADIAAQABAAAACCq0AAS4ACKsAAAAAgBLAAAABgABAAAA1QBMAAAADAAB" +
                            "AAAACABNAE4AAAEKAJ0AbQAAAAgAngBJAAEASgAAAB0AAQAAAAAABQSzAAmxAAAAAQBLAAAABgAB" +
                            "AAAAPgABAJ8AAAACAKA="
            );
        }
    }
}
