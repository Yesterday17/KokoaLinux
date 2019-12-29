package cn.yesterday17.kokoalinux;

import cn.yesterday17.kokoalinux.display.DisplayHelper;
import cn.yesterday17.kokoalinux.gui.GuiChange;
import cn.yesterday17.kokoalinux.input.InputHelper;
import cn.yesterday17.kokoalinux.input.InputNative;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.sun.jna.Memory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.lang.reflect.Field;

public class KokoaLinux extends DummyModContainer {
    private static final String MOD_ID = "kokoalinux";
    private static final String NAME = "KokoaLinux";

    public static Logger logger;

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
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.getLevel();
    }

    @Subscribe
    public void init(FMLInitializationEvent event) {
        // This prepares for the position to display IME
        InputNative.instance.setDisplayPositionCallback(
                () -> {
                    int[] point = {Display.getX(), Display.getY()};
                    Memory memory = new Memory(8L);
                    memory.write(0L, point, 0, 2);
                    return memory;
                }
        );

        // In fact it's public after tweaked
        try {
            Field f = Class.forName("org.lwjgl.opengl.LinuxEvent").getDeclaredField("enableIME");
            f.setAccessible(true);
            f.set(null, true);
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Destroy original IC & IMC
        DisplayHelper.destroyLWJGLIC();
        DisplayHelper.closeLWJGLIM();

        // Construct IM-friendly environment
        InputHelper.prepareLocale();
        InputHelper.openIM();
        InputHelper.createIC(false);

        // For subscribe events
        MinecraftForge.EVENT_BUS.register(GuiChange.class);
    }
}
