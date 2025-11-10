package me.anticode.abco.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "server")
public class AbcoServerConfig implements ConfigData {
    @Comment("Disables other forms of critical hits, like critical hits from jumping or the Critical Strike mod.")
    public boolean disable_other_criticals = true;

    @Comment("Whether the specific attack in a combo should guarantee a critical hit. By default, the last attack of most combos in ABCO are critical hits as well as most special attacks.")
    public boolean attack_based_criticals = true;
}
