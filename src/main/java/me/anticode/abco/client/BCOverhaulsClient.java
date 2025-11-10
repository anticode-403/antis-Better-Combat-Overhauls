package me.anticode.abco.client;

import me.anticode.abco.config.AbcoClientConfig;
import me.anticode.abco.config.AbcoClientConfigWrapper;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ClientModInitializer;

public class BCOverhaulsClient implements ClientModInitializer {
    public static AbcoClientConfig config;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(AbcoClientConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        config = (AutoConfig.getConfigHolder(AbcoClientConfigWrapper.class).getConfig()).client;
    }
}
