package cn.yesterday17.kokoalinux.tweaker;

import cn.yesterday17.kokoalinux.transformer.LwjglTransformer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

import java.io.File;
import java.util.List;

public class KokoaTweaker implements ITweaker {
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        // Prepare for Lwjgl Transformer
        LwjglTransformer.prepare(classLoader);

        // Register Transformer
        classLoader.registerTransformer("cn.yesterday17.kokoalinux.transformer.LwjglTransformer");
        classLoader.registerTransformer("cn.yesterday17.kokoalinux.transformer.GuiTextFieldTransformer");

        // Add Mod
        FMLInjectionData.containers.add("cn.yesterday17.kokoalinux.KokoaLinux");
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
