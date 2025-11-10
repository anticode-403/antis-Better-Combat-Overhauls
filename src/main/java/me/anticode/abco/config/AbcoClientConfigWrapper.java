package me.anticode.abco.config;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(
        name = "abco"
)
public class AbcoClientConfigWrapper extends PartitioningSerializer.GlobalData {
    @Category("client")
    @TransitiveObject
    public AbcoClientConfig client = new AbcoClientConfig();
}
