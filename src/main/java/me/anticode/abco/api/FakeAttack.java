package me.anticode.abco.api;

import net.bettercombat.api.WeaponAttributes;

public class FakeAttack {
    private WeaponAttributes.Condition[] conditions;
    private WeaponAttributes.HitBoxShape hitbox;
    private double damage_multiplier = (double)1.0F;
    private double angle = (double)0.0F;
    private double upswing = (double)0.0F;
    private String animation = null;
    private WeaponAttributes.Sound swing_sound = null;
    private WeaponAttributes.Sound impact_sound = null;
    private boolean critical = false;
    private double knockback_multiplier = 1.0D;

    public WeaponAttributes.Attack convert() {
        WeaponAttributes.Attack attack = new WeaponAttributes.Attack(conditions, hitbox, damage_multiplier, angle, upswing, animation, swing_sound, impact_sound);
        ExpandedAttack expandedAttack = (ExpandedAttack)(Object)attack;
        expandedAttack.antisBetterCombatOverhauls$setCritical(critical);
        expandedAttack.antisBetterCombatOverhauls$setKnockbackMultiplier(knockback_multiplier);
        return attack;
    }
}
