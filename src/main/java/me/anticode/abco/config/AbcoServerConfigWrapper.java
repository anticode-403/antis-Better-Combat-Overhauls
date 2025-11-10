package me.anticode.abco.config;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(
        name = "abco"
)
public class AbcoServerConfigWrapper extends PartitioningSerializer.GlobalData {
    @Category("server")
    @Excluded
    public AbcoServerConfig server = new AbcoServerConfig();
}
