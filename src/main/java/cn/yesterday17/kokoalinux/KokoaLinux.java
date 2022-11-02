package cn.yesterday17.kokoalinux;

import cn.yesterday17.kokoalinux.config.ConfigManager;
import cn.yesterday17.kokoalinux.gui.GuiChange;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.Logger;

public class KokoaLinux extends DummyModContainer {
    public static final String MOD_ID = "kokoalinux";
    public static final String NAME = "KokoaLinux";

    public KokoaLinux() {
        super(new ModMetadata());

        ModMetadata metadata = getMetadata();
        metadata.modId = MOD_ID;
        metadata.name = NAME;
        metadata.version = "@VERSION@";
        metadata.description = "IME solution for Minecraft under Linux.";
        metadata.authorList.add("Yesterday17");
        metadata.url = "https://github.com/Yesterday17/KokoaLinux";
        metadata.credits = "Axeryok";
    }

    @Override
    public String getGuiClassName() {
        return "cn.yesterday17.kokoalinux.config.GuiFactory";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent event) {
        // Inject Config
        ConfigManager.inject();
    }

    @Subscribe
    public void init(FMLInitializationEvent event) {
        // Subscribe events
        if(SystemUtils.IS_OS_UNIX || SystemUtils.IS_OS_FREE_BSD || SystemUtils.IS_OS_NET_BSD || SystemUtils.IS_OS_OPEN_BSD) {
            MinecraftForge.EVENT_BUS.register(GuiChange.class);
        }
        else {
            System.out.println("KokoaLinux can only work on Linux or UNIX-like systems.");
        }
        MinecraftForge.EVENT_BUS.register(ConfigManager.class);
    }
}
