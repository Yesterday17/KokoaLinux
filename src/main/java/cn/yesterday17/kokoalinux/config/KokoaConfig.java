package cn.yesterday17.kokoalinux.config;

import cn.yesterday17.kokoalinux.KokoaLinux;
import net.minecraftforge.common.config.Config;

@Config(modid = KokoaLinux.MOD_ID, name = KokoaLinux.NAME)
public class KokoaConfig {
    // Used by org.lwjgl.opengl.LinuxEvent.filterEventX
    public static boolean enableIME = true;

    public static boolean debug = false;
}
