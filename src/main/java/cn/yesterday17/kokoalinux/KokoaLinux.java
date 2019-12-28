package cn.yesterday17.kokoalinux;

import com.Axeryok.CocoaInput.CocoaInput;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

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
    public List<ArtifactVersion> getDependencies() {
        return Collections.singletonList(VersionParser.parseVersionReference("CocoaInput@[3.1.0,)"));
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
        try {
            Field f = CocoaInput.class.getDeclaredField("instance");
            f.setAccessible(true);
            CocoaInput instance = (CocoaInput) f.get(null);
            instance.applyController(new LinuxController());
        } catch (NoSuchFieldException | IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
