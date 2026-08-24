package net.bettercombat.network;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.CombatFlags;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.TargetHelper;
import net.bettercombat.logic.knockback.ConfigurableKnockback;
import net.bettercombat.mixin.player.LivingEntityAccessor;
import net.bettercombat.utils.AttributeModifierHelper;
import net.bettercombat.utils.MathHelper;
import net.bettercombat.utils.SoundHelper;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class ServerNetwork {
   static final Logger LOGGER = LogUtils.getLogger();
   public static ResourceLocation TEMPORARY_ATTACK = ResourceLocation.fromNamespaceAndPath("bettercombat", "temp_attack");

   public static void handleAttackAnimation(Packets.AttackAnimation packet, MinecraftServer server, ServerPlayer player) {
      ServerLevel world = (ServerLevel)Iterables.tryFind(server.getAllLevels(), element -> element == player.level()).orNull();
      if (world != null && !world.isClientSide) {
         Packets.AttackAnimation forwardPacket = new Packets.AttackAnimation(
            player.getId(),
            packet.animatedHand(),
            packet.animationName(),
            packet.length(),
            packet.upswing(),
            packet.weaponRange(),
            packet.upswingTicks(),
            packet.particles()
         );

         try {
            if (Platform.networkS2C_CanSend(player, Packets.AttackAnimation.ID)) {
               Platform.networkS2C_Send(player, forwardPacket);
            }
         } catch (Exception var6) {
            var6.printStackTrace();
         }

         Platform.tracking(player).forEach(serverPlayer -> {
            try {
               if (Platform.networkS2C_CanSend(serverPlayer, Packets.AttackAnimation.ID)) {
                  Platform.networkS2C_Send(serverPlayer, forwardPacket);
               }
            } catch (Exception var3x) {
               var3x.printStackTrace();
            }
         });
      }
   }

   public static void handleAttackRequest(Packets.C2S_AttackRequest request, MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler) {
      ServerLevel world = (ServerLevel)Iterables.tryFind(server.getAllLevels(), element -> element == player.level()).orNull();
      if (world != null && !world.isClientSide) {
         if (!CombatFlags.isAttackDisabled(player)) {
            AttackHand hand = PlayerAttackHelper.getCurrentAttack(player, request.comboCount());
            if (hand == null) {
               LOGGER.error("Server handling Packets.C2S_AttackRequest - No current attack hand!");
               LOGGER.error("Combo count: " + request.comboCount() + " is dual wielding: " + PlayerAttackHelper.isDualWielding(player));
               LOGGER.error("Main-hand stack: " + player.getMainHandItem());
               LOGGER.error("Off-hand stack: " + player.getOffhandItem());
               LOGGER.error("Selected slot server: " + player.getInventory().selected + " | client: " + request.selectedSlot());
            } else {
               WeaponAttributes.Attack attack = hand.attack();
               WeaponAttributes attributes = hand.attributes();
               boolean useVanillaPacket = Packets.C2S_AttackRequest.UseVanillaPacket;
               world.getServer()
                  .executeIfPossible(
                     () -> {
                        ((PlayerAttackProperties)player).setComboCount(request.comboCount());
                        PlayerAttackHelper.swapHandAttributes(
                           player,
                           hand.isOffHand(),
                           () -> {
                              double damageBaseMultiplier = 0.0;
                              double range = 18.0;
                              boolean isDualWielding = PlayerAttackHelper.isDualWielding(player);
                              if (attributes != null && attack != null) {
                                 range = PlayerAttackHelper.getRangeForItem(player, hand.itemStack());
                                 double comboMultiplier = attack.damageMultiplier() - 1.0;
                                 damageBaseMultiplier += comboMultiplier;
                                 float dualWieldingMultiplier = PlayerAttackHelper.getDualWieldingAttackDamageMultiplier(player, hand) - 1.0F;
                                 damageBaseMultiplier += dualWieldingMultiplier;
                                 SoundHelper.playSound(world, player, attack.swingSound());
                                 if (BetterCombatMod.config.allow_reworked_sweeping && request.entityIds().length > 1) {
                                    double multiplier = 0.0F
                                       - BetterCombatMod.config.reworked_sweeping_maximum_damage_penalty
                                          / BetterCombatMod.config.reworked_sweeping_extra_target_count
                                          * Math.min(BetterCombatMod.config.reworked_sweeping_extra_target_count, request.entityIds().length - 1);
                                    double sweepRatio = player.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO);
                                    damageBaseMultiplier += multiplier + BetterCombatMod.config.reworked_sweeping_maximum_damage_penalty * sweepRatio;
                                    boolean playEffects = !BetterCombatMod.config.reworked_sweeping_sound_and_particles_only_for_swords
                                       || hand.itemStack().getItem() instanceof SwordItem;
                                    if (BetterCombatMod.config.reworked_sweeping_plays_sound && playEffects) {
                                       world.playSound(
                                          null,
                                          player.getX(),
                                          player.getY(),
                                          player.getZ(),
                                          SoundEvents.PLAYER_ATTACK_SWEEP,
                                          player.getSoundSource(),
                                          1.0F,
                                          1.0F
                                       );
                                    }

                                    if (BetterCombatMod.config.reworked_sweeping_emits_particles && playEffects) {
                                       player.sweepAttack();
                                    }
                                 }
                              }

                              Multimap<Holder<Attribute>, AttributeModifier> damageModifier = null;
                              if (damageBaseMultiplier != 0.0) {
                                 AttributeModifierHelper.fromModifier(Attributes.ATTACK_DAMAGE, null);
                                 damageModifier = AttributeModifierHelper.fromModifier(
                                    Attributes.ATTACK_DAMAGE, new AttributeModifier(TEMPORARY_ATTACK, damageBaseMultiplier, Operation.ADD_MULTIPLIED_BASE)
                                 );
                                 player.getAttributes().addTransientAttributeModifiers(damageModifier);
                              }

                              float attackCooldown = PlayerAttackHelper.getAttackCooldownTicksCapped(player);
                              float knockbackMultiplier = 1.0F;
                              if (BetterCombatMod.config.knockback_reduced_for_fast_attacks) {
                                 knockbackMultiplier = MathHelper.clamp(attackCooldown / BetterCombatMod.config.knockback_reduction_threshold, 0.1F, 1.0F);
                                 switch (BetterCombatMod.config.knockback_reduction_curve) {
                                    case SQUARE:
                                       knockbackMultiplier *= knockbackMultiplier;
                                       break;
                                    case HALF_SQUARE:
                                       knockbackMultiplier = (knockbackMultiplier * knockbackMultiplier + knockbackMultiplier) * 0.5F;
                                 }
                              }

                              int lastAttackedTicks = ((LivingEntityAccessor)player).getAttackStrengthTicker();
                              if (!useVanillaPacket) {
                                 player.setShiftKeyDown(request.isSneaking());
                              }

                              double validationRangeSquared = range * range * BetterCombatMod.config.target_search_range_multiplier;

                              for (int entityId : request.entityIds()) {
                                 boolean isBossPart = false;
                                 Entity entity = world.getEntity(entityId);
                                 if (entity == null) {
                                    isBossPart = true;
                                    entity = world.getEntityOrPart(entityId);
                                 }

                                 if (entity != null
                                    && (!entity.equals(player.getVehicle()) || TargetHelper.isAttackableMount(entity))
                                    && (!(entity instanceof ArmorStand) || !((ArmorStand)entity).isMarker())) {
                                    boolean isDirectHit = entityId == request.cursorTarget();
                                    if (TargetHelper.isHitAllowed(isDirectHit, TargetHelper.getRelation(player, entity))) {
                                       if (entity instanceof LivingEntity livingEntity) {
                                          if (BetterCombatMod.config.allow_fast_attacks) {
                                             livingEntity.invulnerableTime = 0;
                                          }

                                          if (knockbackMultiplier != 1.0F) {
                                             ((ConfigurableKnockback)livingEntity).setKnockbackMultiplier_BetterCombat(knockbackMultiplier);
                                          }
                                       }

                                       ((LivingEntityAccessor)player).setLastAttackedTicks(lastAttackedTicks);
                                       if (!isBossPart && useVanillaPacket) {
                                          ServerboundInteractPacket vanillaAttackPacket = ServerboundInteractPacket.createAttackPacket(
                                             entity, request.isSneaking()
                                          );
                                          handler.handleInteract(vanillaAttackPacket);
                                       } else if (!BetterCombatMod.config.server_target_range_validation
                                          || player.distanceToSqr(entity) <= validationRangeSquared) {
                                          if (entity instanceof ItemEntity
                                             || entity instanceof ExperienceOrb
                                             || entity instanceof AbstractArrow
                                             || entity == player) {
                                             handler.disconnect(Component.translatable("multiplayer.disconnect.invalid_entity_attacked"));
                                             LOGGER.warn("Player {} tried to attack an invalid entity", player.getName().getString());
                                             return;
                                          }

                                          player.attack(entity);
                                       }

                                       if (entity instanceof LivingEntity livingEntity && knockbackMultiplier != 1.0F) {
                                          ((ConfigurableKnockback)livingEntity).setKnockbackMultiplier_BetterCombat(1.0F);
                                       }
                                    }
                                 }
                              }

                              if (!useVanillaPacket) {
                                 player.resetLastActionTime();
                              }

                              if (damageModifier != null) {
                                 player.getAttributes().removeAttributeModifiers(damageModifier);
                              }

                              ((PlayerAttackProperties)player).setComboCount(-1);
                           }
                        );
                     }
                  );
            }
         }
      }
   }

   public static void handleBlockHit(Packets.C2S_BlockHit packet, MinecraftServer server, ServerPlayer player) {
      Level world = player.level();
      if (world != null) {
         BlockState block = world.getBlockState(packet.pos());
         if (block != null && !block.isAir()) {
            SoundType soundGroup = block.getSoundType();
            if (soundGroup != null) {
               world.playSound(
                  null, packet.pos().getX(), packet.pos().getY(), packet.pos().getZ(), soundGroup.getHitSound(), player.getSoundSource(), 1.0F, 1.0F
               );
            }
         }
      }
   }
}
