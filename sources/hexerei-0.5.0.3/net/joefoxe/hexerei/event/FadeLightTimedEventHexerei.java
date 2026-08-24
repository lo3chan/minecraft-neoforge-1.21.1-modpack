package net.joefoxe.hexerei.event;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.joefoxe.hexerei.light.DynamicLightUtil;
import net.joefoxe.hexerei.light.LambHexereiDynamicLight;
import net.joefoxe.hexerei.light.LightManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FadeLightTimedEventHexerei implements ITimedEvent, LambHexereiDynamicLight {
   protected int lambdynlights$luminance = 0;
   private int lambdynlights$lastLuminance = 0;
   private long lambdynlights$lastUpdate = 0L;
   private LongOpenHashSet lambdynlights$trackedLitChunkPos = new LongOpenHashSet();
   public Vec3 targetPos;
   public int ticksLeft;
   int starterTicks;
   int startLuminance;
   Level level;

   public FadeLightTimedEventHexerei(Level level, Vec3 pos, int duration, int startLuminance) {
      this.targetPos = pos;
      this.ticksLeft = duration;
      this.starterTicks = duration;
      this.startLuminance = startLuminance;
      this.level = level;
   }

   @Override
   public void tick(boolean serverSide) {
      if (!serverSide && !LightManager.shouldUpdateDynamicLight()) {
         this.lambdynlights$luminance = 0;
      }

      if (!serverSide && LightManager.shouldUpdateDynamicLight()) {
         if (this.isExpired()) {
            this.setHexereiDynamicLightEnabled(false);
         } else {
            this.dynamicLightTickH();
            LightManager.updateLightTracking(this);
         }
      }

      this.ticksLeft--;
      if (this.ticksLeft <= 0) {
         this.setHexereiDynamicLightEnabled(false);
      }
   }

   @Override
   public boolean isExpired() {
      return this.ticksLeft <= 0;
   }

   @Override
   public double getDynamicLightXH() {
      return this.targetPos.x;
   }

   @Override
   public double getDynamicLightYH() {
      return this.targetPos.y;
   }

   @Override
   public double getDynamicLightZH() {
      return this.targetPos.z;
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
   public int getLuminanceH() {
      return this.lambdynlights$luminance;
   }

   @Override
   public void dynamicLightTickH() {
      this.lambdynlights$luminance = this.starterTicks == 0 ? 0 : (int)(this.startLuminance * ((double)this.ticksLeft / this.starterTicks));
   }

   @Override
   public boolean shouldUpdateDynamicLightH() {
      return LightManager.shouldUpdateDynamicLight();
   }

   @Override
   public boolean lambdynlights$updateDynamicLightH(LevelRenderer renderer) {
      int luminance = this.getLuminanceH();
      if (luminance == this.lambdynlights$lastLuminance) {
         return false;
      } else {
         this.lambdynlights$lastLuminance = luminance;
         LongOpenHashSet newPos = new LongOpenHashSet();
         if (luminance > 0) {
            ChunkPos entityChunkPos = new ChunkPos(new BlockPos((int)this.targetPos.x, (int)this.targetPos.y, (int)this.targetPos.z));
            MutableBlockPos chunkPos = new MutableBlockPos(entityChunkPos.x, DynamicLightUtil.getSectionCoord(this.targetPos.y), entityChunkPos.z);
            LightManager.scheduleChunkRebuild(renderer, chunkPos);
            LightManager.updateTrackedChunks(chunkPos, this.lambdynlights$trackedLitChunkPos, newPos);
            BlockPos blockPos = new BlockPos((int)this.targetPos.x, (int)this.targetPos.y, (int)this.targetPos.z);
            Direction directionX = (blockPos.getX() & 15) >= 8 ? Direction.EAST : Direction.WEST;
            Direction directionY = (Mth.floor(blockPos.getY()) & 15) >= 8 ? Direction.UP : Direction.DOWN;
            Direction directionZ = (blockPos.getZ() & 15) >= 8 ? Direction.SOUTH : Direction.NORTH;

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
