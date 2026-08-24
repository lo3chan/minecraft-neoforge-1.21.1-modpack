package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CrystalBallTile extends BlockEntity {
   public float degreesSpun;
   public float degreesSpunOld;
   public float orbOffset = 0.0F;
   public float moonAlpha;
   public float centerYaw;
   public float centerYawO;
   public float centerYawIncrement;
   public float centerPitch;
   public float centerPitchO;
   public float centerPitchIncrement;
   public long lastInteractedWith;
   public Player nearestPlayer;
   public long lastLocatedNearestPlayer;

   public CrystalBallTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
      super(tileEntityTypeIn, blockPos, blockState);
      this.degreesSpun = 0.0F;
      this.degreesSpunOld = 0.0F;
      this.moonAlpha = 0.0F;
      this.centerYaw = 0.0F;
      this.centerYawO = 0.0F;
      this.centerYawIncrement = 0.0F;
      this.centerPitch = 0.0F;
      this.centerPitchO = 0.0F;
      this.centerPitchIncrement = 0.0F;
      this.lastInteractedWith = 0L;
      this.nearestPlayer = null;
   }

   public CrystalBallTile(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)ModTileEntities.CRYSTAL_BALL_TILE.get(), blockPos, blockState);
   }

   public static double getDistanceToEntity(Entity entity, BlockPos pos) {
      double deltaX = entity.position().x - pos.getX();
      double deltaY = entity.position().y - pos.getY();
      double deltaZ = entity.position().z - pos.getZ();
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   public float updateIncrement(float currentAngle, float targetAngle, float lastIncrement) {
      targetAngle = this.normalizeAngle(targetAngle);
      currentAngle = this.normalizeAngle(currentAngle);
      float angleDifference = targetAngle - currentAngle;
      if (angleDifference > 180.0F) {
         angleDifference -= 360.0F;
      } else if (angleDifference < -180.0F) {
         angleDifference += 360.0F;
      }

      float distance = Math.abs(angleDifference);
      return Mth.abs(lastIncrement) < 0.3F && distance < 1.0F
         ? 0.0F
         : (lastIncrement + (distance / 180.0F * (distance / 180.0F) + 0.125F) * (angleDifference > 0.0F ? 1 : -1)) * 0.96F;
   }

   public float updateAngle(float currentAngle, float maxIncrement) {
      currentAngle = this.normalizeAngle(currentAngle);
      currentAngle += maxIncrement;
      return this.normalizeAngle(currentAngle);
   }

   private float normalizeAngle(float angle) {
      while (angle > 90.0F) {
         angle -= 360.0F;
      }

      while (angle < -270.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   private float normalize(float angle) {
      while (angle > 360.0F) {
         angle -= 360.0F;
      }

      while (angle < 0.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   public void tick() {
      this.degreesSpunOld = this.degreesSpun;
      this.centerPitchO = this.centerPitch;
      this.centerYawO = this.centerYaw;
      float currentTime = (float)this.level.getGameTime();
      if (this.level.getGameTime() - this.lastLocatedNearestPlayer > 20L) {
         this.lastLocatedNearestPlayer = this.level.getGameTime();
         this.nearestPlayer = this.level.getNearestPlayer(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), 4.0, false);
      }

      if (this.nearestPlayer != null && getDistanceToEntity(this.nearestPlayer, this.worldPosition) < 4.0) {
         this.degreesSpun = this.normalize(this.degreesSpun + 0.5F);
         this.orbOffset = HexereiUtil.moveTo(this.orbOffset, (float)Math.sin(3.141592653589793 * currentTime / 30.0) / 4.0F, 0.25F);
         this.moonAlpha = HexereiUtil.moveTo(this.moonAlpha, 1.0F, 0.05F);
         Vec3 playerPos = this.nearestPlayer.position();
         double dx = playerPos.x - this.getBlockPos().getX() - 0.5;
         double dy = playerPos.y + this.nearestPlayer.getEyeHeight() - this.getBlockPos().getY() - 0.5;
         double dz = playerPos.z - this.getBlockPos().getZ() - 0.5;
         double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
         float yaw = (float)(Math.atan2(dz, dx) * 57.29577951308232) - 90.0F;
         float pitch = (float)(-(Math.atan2(dy, distance) * 57.29577951308232));
         if (this.level.getGameTime() - this.lastInteractedWith > 20L) {
            this.centerYawIncrement = this.updateIncrement(this.centerYaw, yaw, this.centerYawIncrement);
            this.centerPitchIncrement = this.updateIncrement(this.centerPitch, pitch, this.centerPitchIncrement);
         }

         this.centerYaw = this.updateAngle(this.centerYaw, this.centerYawIncrement);
         this.centerPitch = this.updateAngle(this.centerPitch, this.centerPitchIncrement);
         this.centerPitch = HexereiUtil.moveTo(this.centerPitch, 0.0F, Math.abs(this.centerYawIncrement / 10.0F));
      } else {
         this.orbOffset = HexereiUtil.moveTo(this.orbOffset, -1.0F, 0.1F);
         this.moonAlpha = HexereiUtil.moveTo(this.moonAlpha, 0.0F, 0.05F);
      }
   }

   public boolean triggerEvent(int pId, int pType) {
      if (pId == 1) {
         this.centerYawIncrement = Mth.clamp(
            this.centerYawIncrement + (this.centerYawIncrement > 0.0F ? 1 : -1) + this.centerYawIncrement / 10.0F, -100.0F, 100.0F
         );
         this.lastInteractedWith = this.level.getGameTime();
         return true;
      } else {
         return super.triggerEvent(pId, pType);
      }
   }
}
