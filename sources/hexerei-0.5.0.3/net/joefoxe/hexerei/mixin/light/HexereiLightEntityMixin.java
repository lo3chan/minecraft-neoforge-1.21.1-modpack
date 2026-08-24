package net.joefoxe.hexerei.mixin.light;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.joefoxe.hexerei.event.EventQueue;
import net.joefoxe.hexerei.event.FadeLightTimedEventHexerei;
import net.joefoxe.hexerei.light.DynamicLightUtil;
import net.joefoxe.hexerei.light.LambHexereiDynamicLight;
import net.joefoxe.hexerei.light.LightManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Entity.class})
public abstract class HexereiLightEntityMixin implements LambHexereiDynamicLight {
   @Shadow
   public Level level;
   @Shadow
   private ChunkPos chunkPosition;
   @Unique
   protected int lambdynlights$luminance = 0;
   @Unique
   private int lambdynlights$lastLuminance = 0;
   @Unique
   private long lambdynlights$lastUpdate = 0L;
   @Unique
   private double lambdynlights$prevX;
   @Unique
   private double lambdynlights$prevY;
   @Unique
   private double lambdynlights$prevZ;
   @Unique
   private LongOpenHashSet lambdynlights$trackedLitChunkPos = new LongOpenHashSet();

   @Shadow
   public abstract double getX();

   @Shadow
   public abstract double getEyeY();

   @Shadow
   public abstract double getZ();

   @Shadow
   public abstract double getY();

   @Shadow
   public abstract EntityType<?> getType();

   @Shadow
   public abstract BlockPos blockPosition();

   @Shadow
   public abstract boolean isRemoved();

   @Shadow
   public abstract Level level();

   @Shadow
   public abstract BlockPos getOnPos();

   @Shadow
   public abstract double getZ(double var1);

   @Shadow
   public abstract Vec3 position();

   @Shadow
   public abstract Component getName();

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   public void onTick(CallbackInfo ci) {
      if (this.level.isClientSide && !LightManager.shouldUpdateDynamicLight()) {
         this.lambdynlights$luminance = 0;
      }

      if (this.level.isClientSide() && LightManager.shouldUpdateDynamicLight()) {
         if (this.isRemoved()) {
            this.setHexereiDynamicLightEnabled(false);
         } else {
            this.dynamicLightTickH();
            LightManager.updateLightTracking(this);
         }
      }
   }

   @Inject(
      method = {"remove"},
      at = {@At("HEAD")}
   )
   public void onRemove(CallbackInfo ci) {
      if (this.level.isClientSide()) {
         this.setHexereiDynamicLightEnabled(false);
      }
   }

   @Inject(
      method = {"onClientRemoval"},
      at = {@At("HEAD")}
   )
   public void removed(CallbackInfo ci) {
      if (this.level.isClientSide()) {
         this.setHexereiDynamicLightEnabled(false);
         if (this.lambdynlights$luminance > 0) {
            EventQueue.getClientQueue().addEvent(new FadeLightTimedEventHexerei(this.level(), this.position(), 8, this.lambdynlights$luminance));
         }
      }
   }

   @Override
   public double getDynamicLightXH() {
      return this.getX();
   }

   @Override
   public double getDynamicLightYH() {
      return this.getEyeY();
   }

   @Override
   public double getDynamicLightZH() {
      return this.getZ();
   }

   @Override
   public Level getDynamicLightWorldH() {
      return this.level;
   }

   @Override
   public void resetDynamicLightH() {
      this.lambdynlights$lastLuminance = 0;
   }

   @Override
   public boolean shouldUpdateDynamicLightH() {
      return LightManager.shouldUpdateDynamicLight() && DynamicLightUtil.couldGiveLight((Entity)this);
   }

   @Override
   public void dynamicLightTickH() {
      this.lambdynlights$luminance = 0;
      int luminance = DynamicLightUtil.lightForEntity((Entity)this);
      if (luminance > this.lambdynlights$luminance) {
         this.lambdynlights$luminance = luminance;
      }
   }

   @Override
   public int getLuminanceH() {
      return this.lambdynlights$luminance;
   }

   @Override
   public boolean lambdynlights$updateDynamicLightH(LevelRenderer renderer) {
      if (!this.shouldUpdateDynamicLightH()) {
         return false;
      } else {
         double deltaX = this.getX() - this.lambdynlights$prevX;
         double deltaY = this.getY() - this.lambdynlights$prevY;
         double deltaZ = this.getZ() - this.lambdynlights$prevZ;
         int luminance = this.getLuminanceH();
         if (!(Math.abs(deltaX) > 0.1) && !(Math.abs(deltaY) > 0.1) && !(Math.abs(deltaZ) > 0.1) && luminance == this.lambdynlights$lastLuminance) {
            return false;
         } else {
            this.lambdynlights$prevX = this.getX();
            this.lambdynlights$prevY = this.getY();
            this.lambdynlights$prevZ = this.getZ();
            this.lambdynlights$lastLuminance = luminance;
            LongOpenHashSet newPos = new LongOpenHashSet();
            if (luminance > 0) {
               ChunkPos entityChunkPos = this.chunkPosition;
               MutableBlockPos chunkPos = new MutableBlockPos(entityChunkPos.x, DynamicLightUtil.getSectionCoord(this.getEyeY()), entityChunkPos.z);
               LightManager.scheduleChunkRebuild(renderer, chunkPos);
               LightManager.updateTrackedChunks(chunkPos, this.lambdynlights$trackedLitChunkPos, newPos);
               Direction directionX = (this.blockPosition().getX() & 15) >= 8 ? Direction.EAST : Direction.WEST;
               Direction directionY = (Mth.floor(this.getEyeY()) & 15) >= 8 ? Direction.UP : Direction.DOWN;
               Direction directionZ = (this.blockPosition().getZ() & 15) >= 8 ? Direction.SOUTH : Direction.NORTH;

               for (int i = 0; i < 7; i++) {
                  if (i % 4 == 0) {
                     chunkPos.move(directionX);
                  } else if (i % 4 == 1) {
                     chunkPos.move(directionZ);
                  } else if (i % 4 == 2) {
                     chunkPos.move(directionX.getOpposite());
                  } else {
                     chunkPos.move(directionZ.getOpposite());
                     chunkPos.move(directionY);
                  }

                  LightManager.scheduleChunkRebuild(renderer, chunkPos);
                  LightManager.updateTrackedChunks(chunkPos, this.lambdynlights$trackedLitChunkPos, newPos);
               }
            }

            this.lambdynlights$scheduleTrackedChunksRebuildH(renderer);
            this.lambdynlights$trackedLitChunkPos = newPos;
            return true;
         }
      }
   }

   @Override
   public void lambdynlights$scheduleTrackedChunksRebuildH(LevelRenderer renderer) {
      if (Minecraft.getInstance().level == this.level) {
         LongIterator var2 = this.lambdynlights$trackedLitChunkPos.iterator();

         while (var2.hasNext()) {
            long pos = (Long)var2.next();
            LightManager.scheduleChunkRebuild(renderer, pos);
         }
      }
   }
}
