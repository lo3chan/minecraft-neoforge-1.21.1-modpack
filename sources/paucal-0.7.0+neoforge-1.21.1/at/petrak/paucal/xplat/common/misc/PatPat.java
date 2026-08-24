package at.petrak.paucal.xplat.common.misc;

import at.petrak.paucal.api.contrib.Contributor;
import at.petrak.paucal.xplat.PaucalGamerules;
import at.petrak.paucal.xplat.common.ContributorsManifest;
import at.petrak.paucal.xplat.common.ModRegistries;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PatPat {
   public static InteractionResult onPat(Player player, Level world, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
      if (!world.getGameRules().getBoolean(PaucalGamerules.ALLOW_HEADPATS)) {
         return InteractionResult.PASS;
      } else if (player.getItemInHand(hand).isEmpty() && player.isDiscrete() && hand == InteractionHand.MAIN_HAND && entity instanceof Player target) {
         if (player.level() instanceof ServerLevel sworld) {
            Vec3 pos = target.getEyePosition();
            sworld.sendParticles(ParticleTypes.HEART, pos.x, pos.y + 0.5, pos.z, 1, 0.0, 0.0, 0.0, 0.1);
         } else {
            player.swing(hand);
         }

         tryPlayPatSound(target.getUUID(), target.getEyePosition(), player, world);
         player.awardStat((ResourceLocation)ModRegistries.PLAYERS_PATTED.get());
         target.awardStat((ResourceLocation)ModRegistries.HEADPATS_GOTTEN.get());
         if (target.isOnFire()) {
            target.clearFire();
            if (player.level() instanceof ServerLevel sworld) {
               Vec3 pos = target.getEyePosition();
               sworld.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y + 0.5, pos.z, 10, 0.0, 0.0, 0.0, 0.1);
            }

            player.level().playSound(player, target.getX(), target.getY(), target.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0F, 1.0F);
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public static boolean tryPlayPatSound(UUID pattee, Vec3 patteePos, @Nullable Player patter, Level world) {
      Contributor contributor = ContributorsManifest.getContributor(pattee);
      return contributor != null ? contributor.doHeadpatSound(patteePos, patter, world) : false;
   }
}
