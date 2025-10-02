package me.anticode.abco.api;

public class AbcoPlayerUpdate {
    public final int playerId;
    public final int comboCount;
    public final boolean heavyAttack;

    public AbcoPlayerUpdate(int id, boolean heavy, int combo) {
        this.playerId = id;
        this.comboCount = combo;
        this.heavyAttack = heavy;
    }
}
