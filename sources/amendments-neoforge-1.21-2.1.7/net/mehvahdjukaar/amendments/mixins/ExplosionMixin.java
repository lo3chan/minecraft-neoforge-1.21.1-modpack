package net.mehvahdjukaar.amendments.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Set;
import net.mehvahdjukaar.amendments.common.entity.FireballExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Explosion.class})
public class ExplosionMixin {
   @WrapOperation(
      method = {"explode"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
      )}
   )
   public boolean amendments$HurtWithContext(Entity entity, DamageSource source, float amount, Operation<Boolean> original) {
      return this instanceof FireballExplosion fe ? fe.hurtHitEntity(entity, source, amount) : (Boolean)original.call(new Object[]{entity, source, amount});
   }

   @WrapWithCondition(
      method = {"explode"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
      )}
   )
   public boolean amendments$cancelKnockback(Entity instance, Vec3 deltaMovement) {
      return !(this instanceof FireballExplosion fe && !fe.hasKnockback());
   }

   @Inject(
      method = {"explode"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/ExplosionDamageCalculator;getBlockExplosionResistance(Lnet/minecraft/world/level/Explosion;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)Ljava/util/Optional;"
      )}
   )
   public void amendments$addBlockSideEffects(CallbackInfo ci, @Local BlockPos pos, @Local BlockState state, @Local Set<BlockPos> set) {
      if (this instanceof FireballExplosion fe) {
         fe.addVisitedBlock(pos, state);
      }
   }

   @ModifyArg(
      method = {"finalizeExplosion"},
      index = 5,
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
      )
   )
   public float amendments$changeSoundVolume(float volume) {
      return this instanceof FireballExplosion fe ? fe.getExplosionVolume() : volume;
   }
}
