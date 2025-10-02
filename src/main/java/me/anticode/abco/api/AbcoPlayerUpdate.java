package me.anticode.abco.api;

import net.bettercombat.logic.AnimatedHand;

public class AbcoPlayerUpdate {
    public final int playerId;
    public final int comboCount;
    public final boolean heavyAttack;
    public final AnimatedHand animatedHand;
    public final String animationName;
    public final float length;
    public final float upswing;

    public AbcoPlayerUpdate(int id, boolean heavy, int combo, AnimatedHand animatedHand, String animation, float length, float upswing) {
        this.playerId = id;
        this.comboCount = combo;
        this.heavyAttack = heavy;
        this.animatedHand = animatedHand;
        this.animationName = animation;
        this.length = length;
        this.upswing = upswing;
    }
}
