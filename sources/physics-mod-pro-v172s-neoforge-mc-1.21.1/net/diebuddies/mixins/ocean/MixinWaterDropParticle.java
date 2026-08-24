package net.diebuddies.mixins.ocean;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ocean.OceanWorld;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({WaterDropParticle.class})
public class MixinWaterDropParticle extends MixinParticle {
   @Unique
   private double physicsOffset;
   @Unique
   private MutableBlockPos mutable;

   @Inject(
      at = {@At("TAIL")},
      method = {"<init>"}
   )
   protected void constructor(ClientLevel clientLevel, double x, double y, double z, CallbackInfo info) {
      if (ConfigClient.areOceanPhysicsEnabled() && this.level != null) {
         OceanWorld oceanWorld = PhysicsMod.getInstance(this.level).getPhysicsWorld().getOceanWorld();
         this.physicsOffset = oceanWorld.calculateYOffset(x, y, z);
         this.yo = y + this.physicsOffset;
         Particle particle = (Particle)this;
         particle.setPos(x, y + this.physicsOffset, z);
      }
   }

   @Override
   protected void getLightColor(float renderPercent, CallbackInfoReturnable<Integer> info) {
      if (ConfigClient.areOceanPhysicsEnabled() && this.level != null) {
         if (this.mutable == null) {
            this.mutable = new MutableBlockPos();
         }

         this.mutable.set(this.x, this.y - this.physicsOffset, this.z);
         if (this.level.hasChunkAt(this.mutable)) {
            info.setReturnValue(LevelRenderer.getLightColor(this.level, this.mutable));
         } else {
            info.setReturnValue(0);
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"tick"},
      cancellable = true
   )
   public void tick(CallbackInfo info) {
      if (ConfigClient.areOceanPhysicsEnabled()) {
         Particle particle = (Particle)this;
         this.xo = this.x;
         this.yo = this.y;
         this.zo = this.z;
         if (this.lifetime-- <= 0) {
            particle.remove();
            return;
         }

         this.yd = this.yd - this.gravity;
         particle.move(this.xd, this.yd, this.zd);
         this.xd *= 0.9800000190734863;
         this.yd *= 0.9800000190734863;
         this.zd *= 0.9800000190734863;
         if (this.onGround) {
            if (Math.random() < 0.5) {
               particle.remove();
            }

            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
         }

         BlockPos blockPos;
         double d;
         if ((
                  d = Math.max(
                     this.level
                        .getBlockState(blockPos = BlockPos.containing(this.x, this.y - this.physicsOffset, this.z))
                        .getCollisionShape(this.level, blockPos)
                        .max(Axis.Y, this.x - blockPos.getX(), this.z - blockPos.getZ()),
                     (double)this.level.getFluidState(blockPos).getHeight(this.level, blockPos)
                  )
               )
               > 0.0
            && this.y < blockPos.getY() + d) {
            particle.remove();
         }

         info.cancel();
      }
   }
}
