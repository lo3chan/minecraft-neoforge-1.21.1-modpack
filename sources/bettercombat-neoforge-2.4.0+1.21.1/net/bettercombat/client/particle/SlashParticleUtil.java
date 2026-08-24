package net.bettercombat.client.particle;

import java.util.LinkedHashMap;
import java.util.List;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.fx.ConditionalTrailAppearance;
import net.bettercombat.api.fx.ParticlePlacement;
import net.bettercombat.api.fx.TrailAppearance;
import net.bettercombat.api.fx.TrailAppearanceOverride;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.config.TrailConfig;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.particle.SlashParticleEffect;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class SlashParticleUtil {
   public static void spawnParticles(SlashParticleUtil.SpawnArgs args) {
      spawnParticles(args.player, args.isOffhand, args.weaponRange, args.settingsList, args.appearance);
   }

   public static void spawnParticles(
      AbstractClientPlayer player, boolean isOffhand, float weaponRange, List<ParticlePlacement> settingsList, TrailAppearance appearance
   ) {
      if (BetterCombatClientMod.config.isShowingWeaponTrails) {
         if (!settingsList.isEmpty()) {
            if (appearance != null) {
               boolean isLeftHanded = player.getMainArm() == HumanoidArm.LEFT;
               boolean mirror = isOffhand;
               if (isLeftHanded) {
                  mirror = !isOffhand;
               }

               weaponRange -= -0.25F;

               for (ParticlePlacement settings : settingsList) {
                  String id = settings.particle_type();
                  List<TrailParticles.Entry> trails = TrailParticles.ENTRIES.get(id);
                  if (trails != null) {
                     float offsetX = settings.x_addition();
                     float offsetY = settings.y_addition();
                     float offsetZ = settings.z_addition();
                     float offhandRoll = mirror ? 180.0F : 0.0F;
                     float offhandFlip = mirror ? -1.0F : 1.0F;
                     float yaw = player.getYRot();
                     float pitch = player.getXRot();
                     Vec3 right = Vec3.directionFromRotation(0.0F, yaw + 90.0F).normalize();
                     Vec3 forward = Vec3.directionFromRotation(pitch, yaw).normalize();
                     double baseX = player.getX();
                     double baseY = player.getEyeY() - 0.25 + offsetY;
                     double baseZ = player.getZ();
                     Vec3 finalPosition = new Vec3(baseX, baseY, baseZ).add(forward.scale(offsetZ)).add(right.scale(offsetX * offhandFlip));
                     Vec3 stabFinalPosition = new Vec3(finalPosition.x(), finalPosition.y(), finalPosition.z()).add(forward.scale(weaponRange - 1.5));
                     double x = finalPosition.x();
                     double y = finalPosition.y();
                     double z = finalPosition.z();
                     double xStab = stabFinalPosition.x();
                     double yStab = stabFinalPosition.y();
                     double zStab = stabFinalPosition.z();

                     for (TrailParticles.Entry trail : trails) {
                        double posX = trail.stabPosition() ? xStab : x;
                        double posY = trail.stabPosition() ? yStab : y;
                        double posZ = trail.stabPosition() ? zStab : z;

                        for (TrailParticles.LayeredParticle layeredParticle : trail.particles()) {
                           if (appearance.primary != null) {
                              player.level()
                                 .addParticle(
                                    new SlashParticleEffect(
                                       layeredParticle.bottom(),
                                       weaponRange,
                                       player.getXRot() + settings.pitch_addition(),
                                       player.getYRot(),
                                       settings.local_yaw() * offhandFlip,
                                       (settings.roll_set() + trail.rollOffset() + offhandRoll) * offhandFlip,
                                       appearance.primary.glows(),
                                       appearance.primary.color_rgba()
                                    ),
                                    posX,
                                    posY,
                                    posZ,
                                    0.0,
                                    0.0,
                                    0.0
                                 );
                           }

                           if (appearance.secondary != null) {
                              player.level()
                                 .addParticle(
                                    new SlashParticleEffect(
                                       layeredParticle.top(),
                                       weaponRange,
                                       player.getXRot() + settings.pitch_addition(),
                                       player.getYRot(),
                                       settings.local_yaw() * offhandFlip,
                                       (settings.roll_set() + trail.rollOffset() + offhandRoll) * offhandFlip,
                                       appearance.secondary.glows(),
                                       appearance.secondary.color_rgba()
                                    ),
                                    posX,
                                    posY,
                                    posZ,
                                    0.0,
                                    0.0,
                                    0.0
                                 );
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static List<ParticlePlacement> trailParticlesFromAttack(AttackHand attackHand) {
      if (!attackHand.attack().trailParticles().isEmpty()) {
         return attackHand.attack().trailParticles();
      } else {
         TrailConfig config = (TrailConfig)BetterCombatMod.trailConfig.value;
         LinkedHashMap<String, List<ParticlePlacement>> animations = config.animation_based;
         if (animations != null) {
            List<ParticlePlacement> animationSpecific = animations.get(attackHand.attack().animation());
            if (animationSpecific != null) {
               return animationSpecific;
            }
         }

         return List.of();
      }
   }

   public static TrailAppearance appearanceFor(Player attacker, ItemStack stack) {
      ConditionalTrailAppearance defaults = ((TrailConfig)BetterCombatMod.trailConfig.value).trail_appearance;
      WeaponAttributes weaponAttributes = WeaponRegistry.getAttributes(stack);
      TrailAppearance resolved;
      if (weaponAttributes != null && weaponAttributes.trailAppearance() != null) {
         resolved = defaults.merge(weaponAttributes.trailAppearance()).resolve(stack);
      } else {
         resolved = defaults.resolve(stack);
      }

      return TrailAppearanceOverride.apply(attacker, stack, resolved);
   }

   public record ScheduledSpawnArgs(SlashParticleUtil.SpawnArgs args, int time) {
   }

   public record SpawnArgs(AbstractClientPlayer player, boolean isOffhand, float weaponRange, List<ParticlePlacement> settingsList, TrailAppearance appearance) {
   }
}
