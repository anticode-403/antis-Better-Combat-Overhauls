package me.anticode.abco.init;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import static me.anticode.abco.BCOverhauls.modID;

public class AbcoSounds {
    public static final Identifier PARRY_SOUND_ID = new Identifier(modID, "finesse_parry");
    public static SoundEvent PARRY_SOUND_EVENT = SoundEvent.of(PARRY_SOUND_ID);

    public static void initialize() { Registry.register(Registries.SOUND_EVENT, PARRY_SOUND_ID, PARRY_SOUND_EVENT); }
}
