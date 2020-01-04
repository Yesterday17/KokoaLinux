package cn.yesterday17.kokoalinux.config;

import cn.yesterday17.kokoalinux.KokoaLinux;
import com.google.common.collect.Sets;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class ConfigManager {
    private static Configuration config;

    @SuppressWarnings("unchecked")
    public static void inject() {
        try {
            // MOD_CONFIG_CLASSES
            Field f = net.minecraftforge.common.config.ConfigManager.class.getDeclaredField("MOD_CONFIG_CLASSES");
            f.setAccessible(true);
            Map<String, Set<Class<?>>> configClasses = (Map<String, Set<Class<?>>>) f.get(null);
            Set<Class<?>> modConfigClasses = configClasses.computeIfAbsent(KokoaLinux.MOD_ID, k -> Sets.newHashSet());
            modConfigClasses.add(KokoaConfig.class);

            // Configuration File
            File file = new File(Loader.instance().getConfigDir(), "KokoaLinux.cfg");
            config = new Configuration(file);
            config.load();

            // CONFIGS
            f = net.minecraftforge.common.config.ConfigManager.class.getDeclaredField("CONFIGS");
            f.setAccessible(true);
            Map<String, Configuration> map = (Map<String, Configuration>) f.get(null);
            map.put(file.getAbsolutePath(), config);

            // Sync and save
            sync();
            config.save();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void sync() {
        try {
            Method m = net.minecraftforge.common.config.ConfigManager.class.getDeclaredMethod("sync", Configuration.class, Class.class, String.class, String.class, boolean.class, Object.class);
            m.setAccessible(true);
            m.invoke(null, config, KokoaConfig.class, KokoaLinux.MOD_ID, "general", !Loader.instance().hasReachedState(LoaderState.AVAILABLE), null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(KokoaLinux.MOD_ID)) {
            sync();
            config.save();
        }
    }
}
