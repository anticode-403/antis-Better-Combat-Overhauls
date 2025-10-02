package me.anticode.abco.api;

import net.bettercombat.logic.AnimatedHand;

public record AbcoPlayerUpdate(int playerId, boolean heavyAttack, int comboCount, AnimatedHand animatedHand,
                               String animationName, float length, float upswing) {
}
