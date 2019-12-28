package cn.yesterday17.kokoalinux.lwjgl;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

public class Tweaker implements ITweaker {
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        try {
            // At this time, libraries like lwjgl has been loaded
            // That's Why LaunchWrapper added them to ClassLoaderException
            // Because they'll cause an 'exception' after reloading.

            // Protect org.lwjgl.(?!opengl)
            String[] lwjgl = new String[]{
                    "B", "D", "J", "L", "M", "P", "S", "W", // Classes
                    "input", "openal", "opencl", "opengles",// Packages
            };
            for (String append : lwjgl) {
                classLoader.addTransformerExclusion("org.lwjgl." + append);
            }

            // Protect org.lwjgl.opengl.(?!LinuxEvent)
            String[] opengl = new String[]{
                    "A", "B", "C", "D", "E", "F", "G", "I", "K",
                    "LinuxA", "LinuxB", "LinuxC", "LinuxD", "LinuxK", "LinuxM", "LinuxP", "LinuxA",
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

            // Register Transformer
            classLoader.registerTransformer("cn.yesterday17.kokoalinux.lwjgl.Transformer");

            // Add Mod
            FMLInjectionData.containers.add("cn.yesterday17.kokoalinux.KokoaLinux");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
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
