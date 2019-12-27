package cn.yesterday17.kokoalinux.lwjgl;

import net.minecraft.launchwrapper.IClassTransformer;

public class Transformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("org.lwjgl.opengl.LinuxEvent")) {
            return new LinuxEventTweak().getBytes();
        }
        return basicClass;
    }
}
