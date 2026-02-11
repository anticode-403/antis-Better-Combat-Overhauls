package me.anticode.abco.network;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import me.anticode.abco.BCOverhauls;
import me.anticode.abco.api.AbcoPlayerEntity;
import me.anticode.abco.api.HeavyAttackComboApi;
import me.anticode.abco.logic.ExpandedPlayerAttackHelper;
import net.bettercombat.BetterCombat;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.TargetHelper;
import net.bettercombat.logic.knockback.ConfigurableKnockback;
import net.bettercombat.mixin.LivingEntityAccessor;
import net.bettercombat.network.Packets;
import net.bettercombat.utils.MathHelper;
import net.bettercombat.utils.SoundHelper;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.slf4j.Logger;

import java.util.UUID;

/*
 * This is particularly ugly, imo, because we're basically completely overwriting how the server handles half of BC's
 * packets, but with the current tools available, I'm not really sure that there was another option.
 */
public class AbcoServerNetwork {
    private static final UUID COMBO_DAMAGE_MODIFIER_ID = UUID.randomUUID();
    private static final UUID DUAL_WIELDING_MODIFIER_ID = UUID.randomUUID();
    private static final UUID SWEEPING_MODIFIER_ID = UUID.randomUUID();

    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(AbcoPackets.C2S_AttackRequest.ID, (server, player, handler, buf, responseSender) -> {
            ServerWorld world = (ServerWorld)Iterables.tryFind(server.getWorlds(), (element) -> element == player.getWorld()).orNull();
            if (world != null && !world.isClient) {
                AbcoPackets.C2S_AttackRequest request = AbcoPackets.C2S_AttackRequest.read(buf);
                AttackHand hand = request.heavy() ? ExpandedPlayerAttackHelper.getCurrentHeavyAttack(player, request.comboCount()) : PlayerAttackHelper.getCurrentAttack(player, request.comboCount());
                if (hand == null) {
                    BCOverhauls.LOGGER.error("Server handling Packets.C2S_AttackRequest - No current attack hand!");
                    Logger var10000 = BCOverhauls.LOGGER;
                    int var10001 = request.comboCount();
                    var10000.error("Combo count: " + var10001 + " is dual wielding: " + PlayerAttackHelper.isDualWielding(player));
                    BCOverhauls.LOGGER.error("Main-hand stack: " + player.getMainHandStack());
                    BCOverhauls.LOGGER.error("Off-hand stack: " + player.getOffHandStack());
                    var10001 = player.getInventory().selectedSlot;
                    var10000.error("Selected slot server: " + var10001 + " | client: " + request.selectedSlot());
                } else {
                    WeaponAttributes.Attack attack = hand.attack();
                    WeaponAttributes attributes = hand.attributes();
                    boolean useVanillaPacket = Packets.C2S_AttackRequest.UseVanillaPacket;
                    world.getServer().executeSync(() -> {
                        ((PlayerAttackProperties)player).setComboCount(request.comboCount());
                        Multimap<EntityAttribute, EntityAttributeModifier> comboAttributes = null;
                        Multimap<EntityAttribute, EntityAttributeModifier> dualWieldingAttributes = null;
                        Multimap<EntityAttribute, EntityAttributeModifier> sweepingModifiers = HashMultimap.create();
                        double range = (double)18.0F;
                        if (attributes != null && attack != null) {
                            range = attributes.attackRange();
                            comboAttributes = HashMultimap.create();
                            double comboMultiplier = attack.damageMultiplier() - (double)1.0F;
                            comboAttributes.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(COMBO_DAMAGE_MODIFIER_ID, "COMBO_DAMAGE_MULTIPLIER", comboMultiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE));
                            player.getAttributes().addTemporaryModifiers(comboAttributes);
                            float dualWieldingMultiplier = PlayerAttackHelper.getDualWieldingAttackDamageMultiplier(player, hand) - 1.0F;
                            if (dualWieldingMultiplier != 0.0F) {
                                dualWieldingAttributes = HashMultimap.create();
                                dualWieldingAttributes.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(DUAL_WIELDING_MODIFIER_ID, "DUAL_WIELDING_DAMAGE_MULTIPLIER", (double)dualWieldingMultiplier, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
                                player.getAttributes().addTemporaryModifiers(dualWieldingAttributes);
                            }

                            if (hand.isOffHand()) {
                                PlayerAttackHelper.setAttributesForOffHandAttack(player, true);
                            }

                            SoundHelper.playSound(world, player, attack.swingSound());
                            if (BetterCombat.config.allow_reworked_sweeping && request.entityIds().length > 1) {
                                double multiplier = (double)1.0F - (double)(BetterCombat.config.reworked_sweeping_maximum_damage_penalty / (float)BetterCombat.config.reworked_sweeping_extra_target_count * (float)Math.min(BetterCombat.config.reworked_sweeping_extra_target_count, request.entityIds().length - 1));
                                int sweepingLevel = EnchantmentHelper.getLevel(Enchantments.SWEEPING, hand.itemStack());
                                double sweepingSteps = (double)BetterCombat.config.reworked_sweeping_enchant_restores / (double)Enchantments.SWEEPING.getMaxLevel();
                                multiplier += (double)sweepingLevel * sweepingSteps;
                                multiplier = Math.min(multiplier, (double)1.0F);
                                sweepingModifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(SWEEPING_MODIFIER_ID, "SWEEPING_DAMAGE_MODIFIER", multiplier - (double)1.0F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
                                player.getAttributes().addTemporaryModifiers(sweepingModifiers);
                                boolean playEffects = !BetterCombat.config.reworked_sweeping_sound_and_particles_only_for_swords || hand.itemStack().getItem() instanceof SwordItem;
                                if (BetterCombat.config.reworked_sweeping_plays_sound && playEffects) {
                                    world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
                                }

                                if (BetterCombat.config.reworked_sweeping_emits_particles && playEffects) {
                                    player.spawnSweepAttackParticles();
                                }
                            }
                        }

                        float attackCooldown = PlayerAttackHelper.getAttackCooldownTicksCapped(player);
                        float knockbackMultiplier = BetterCombat.config.knockback_reduced_for_fast_attacks ? MathHelper.clamp(attackCooldown / 12.5F, 0.1F, 1.0F) : 1.0F;
                        int lastAttackedTicks = ((LivingEntityAccessor)player).getLastAttackedTicks();
                        if (!useVanillaPacket) {
                            player.setSneaking(request.isSneaking());
                        }

                        for(int entityId : request.entityIds()) {
                            boolean isBossPart = false;
                            Entity entity = world.getEntityById(entityId);
                            if (entity == null) {
                                isBossPart = true;
                                entity = world.getDragonPart(entityId);
                            }

                            if (entity != null && (!entity.equals(player.getVehicle()) || TargetHelper.isAttackableMount(entity)) && (!(entity instanceof ArmorStandEntity) || !((ArmorStandEntity)entity).isMarker())) {
                                if (entity instanceof LivingEntity) {
                                    LivingEntity livingEntity = (LivingEntity)entity;
                                    if (BetterCombat.config.allow_fast_attacks) {
                                        livingEntity.timeUntilRegen = 0;
                                    }

                                    if (knockbackMultiplier != 1.0F) {
                                        ((ConfigurableKnockback)livingEntity).setKnockbackMultiplier_BetterCombat(knockbackMultiplier);
                                    }
                                }

                                ((LivingEntityAccessor)player).setLastAttackedTicks(lastAttackedTicks);
                                if (!isBossPart && useVanillaPacket) {
                                    PlayerInteractEntityC2SPacket vanillaAttackPacket = PlayerInteractEntityC2SPacket.attack(entity, request.isSneaking());
                                    handler.onPlayerInteractEntity(vanillaAttackPacket);
                                } else {
                                    if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof PersistentProjectileEntity || entity == player) {
                                        handler.disconnect(Text.translatable("multiplayer.disconnect.invalid_entity_attacked"));
                                        BCOverhauls.LOGGER.warn("Player {} tried to attack an invalid entity", player.getName().getString());
                                        return;
                                    }

                                    player.attack(entity);
                                }

                                if (entity instanceof LivingEntity) {
                                    LivingEntity livingEntity = (LivingEntity)entity;
                                    if (knockbackMultiplier != 1.0F) {
                                        ((ConfigurableKnockback)livingEntity).setKnockbackMultiplier_BetterCombat(1.0F);
                                    }
                                }
                            }
                        }

                        if (!useVanillaPacket) {
                            player.updateLastActionTime();
                        }

                        if (comboAttributes != null) {
                            player.getAttributes().removeModifiers(comboAttributes);
                            if (hand.isOffHand()) {
                                PlayerAttackHelper.setAttributesForOffHandAttack(player, false);
                            }
                        }

                        if (dualWieldingAttributes != null) {
                            player.getAttributes().removeModifiers(dualWieldingAttributes);
                        }

                        if (!sweepingModifiers.isEmpty()) {
                            player.getAttributes().removeModifiers(sweepingModifiers);
                        }

                        ((PlayerAttackProperties)player).setComboCount(-1);
                    });
                }
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(AbcoPackets.C2S_PlayerUpdaterRequest.ID, (server, player, handler, buf, responseSender) -> {
            ServerWorld world = Iterables.tryFind(server.getWorlds(), (element) -> element == player.getWorld()).orNull();
            if (world == null || world.isClient) return;
            final AbcoPackets.C2S_PlayerUpdaterRequest packet = AbcoPackets.C2S_PlayerUpdaterRequest.read(buf);
            final var forwardBuffer = new Packets.AttackAnimation(player.getId(), packet.animatedHand(), packet.animationName(), packet.length(), packet.upswing()).write();
            try {
                //send info back for Replaymod Compat
                if (ServerPlayNetworking.canSend(player, Packets.AttackAnimation.ID)) {
                    ServerPlayNetworking.send(player, Packets.AttackAnimation.ID, forwardBuffer);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            PlayerLookup.tracking(player).forEach(serverPlayer -> {
                try {
                    if (ServerPlayNetworking.canSend(serverPlayer, Packets.AttackAnimation.ID)) {
                        ServerPlayNetworking.send(serverPlayer, Packets.AttackAnimation.ID, forwardBuffer);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
            world.getServer().executeSync(() -> {
                AbcoPlayerEntity abcoPlayerEntity = (AbcoPlayerEntity)player;
                HeavyAttackComboApi comboPlayer = (HeavyAttackComboApi)player;
                if (packet.heavyAttack()) {
                    comboPlayer.antisBetterCombatOverhauls$setHeavyCombo(packet.comboCount());
                    abcoPlayerEntity.antisBetterCombatOverhauls$setLastAttackSpecial(true);
                } else {
                    abcoPlayerEntity.antisBetterCombatOverhauls$setLastAttackSpecial(false);
                }
            });
        });
    }
}
