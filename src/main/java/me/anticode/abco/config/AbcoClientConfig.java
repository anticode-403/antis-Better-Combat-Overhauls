package me.anticode.abco.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "client")
public class AbcoClientConfig implements ConfigData {
    @Comment("If you swing at a block, it will attempt to mine it only if the weapon is suitable for it.\nVanilla examples are swords and cobwebs, as well as axes and wood.")
    public boolean fix_mining_check = true;
}
