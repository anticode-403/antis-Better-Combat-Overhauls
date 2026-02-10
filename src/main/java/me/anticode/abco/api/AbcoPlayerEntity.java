package me.anticode.abco.api;

public interface AbcoPlayerEntity {
    boolean antisBetterCombatOverhauls$wasLastAttackSpecial();
    void antisBetterCombatOverhauls$setLastAttackSpecial(boolean attack);

    int antisBetterCombatOverhauls$getParryTicks();
    void antisBetterCombatOverhauls$setParryTicks(int ticks);
}
