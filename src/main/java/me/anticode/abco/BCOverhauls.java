package me.anticode.abco;

import me.anticode.abco.config.AbcoServerConfig;
import me.anticode.abco.config.AbcoServerConfigWrapper;
import me.anticode.abco.network.AbcoServerNetwork;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class BCOverhauls implements ModInitializer {
    public static String modID = "abco";
    public static final Logger LOGGER = LoggerFactory.getLogger(modID);
    public static AbcoServerConfig config;

    @Override
    public void onInitialize() {
        AutoConfig.register(AbcoServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        config = (AutoConfig.getConfigHolder(AbcoServerConfigWrapper.class).getConfig()).server;
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modID);
        modContainer.ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(
                new Identifier(modID, "abco"), container,
                Text.translatable("pack.abco.abco"),
                ResourcePackActivationType.ALWAYS_ENABLED
        ));
        AbcoServerNetwork.initialize();
    }
}
