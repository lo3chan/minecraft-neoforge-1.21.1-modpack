package net.conczin.immersive_gateways.block;

import java.util.EnumSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.conczin.immersive_gateways.BlockEntityTypes;
import net.conczin.immersive_gateways.Sounds;
import net.conczin.immersive_gateways.data.PortalDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class GatewayBlockEntity extends BlockEntity {
   public static final double DISTANCE = 16.0;
   public static final int COOLDOWN = 30;
   public static final float OFFSET = 2.5F;
   public static final Vector2f[] offsets = new Vector2f[]{
      new Vector2f(0.25F, 0.25F), new Vector2f(0.75F, 0.25F), new Vector2f(0.75F, 0.75F), new Vector2f(0.25F, 0.75F)
   };
   final Vector3f[] offsets1;
   final Vector3f[] offsets2;
   final Quaternionf[] rotations;
   final float[] lastTime = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
   final float[] time = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
   final boolean[] state = new boolean[]{false, false, false, false};
   final Random random = new Random();
   int color = 0;
   static final Map<UUID, Long> lastCloseTick = new ConcurrentHashMap<>();

   private Vector3f[] getOffsets() {
      return this.getOffsets(
         new Vector3f[]{new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F)}
      );
   }

   private float getRandom() {
      return this.random.nextFloat() * 2.0F - 1.0F;
   }

   private Vector3f offsetVector(Vector3f offset) {
      return new Vector3f(offset.x() + this.getRandom() * 2.5F, offset.y() + this.getRandom() * 2.5F, offset.z() + this.getRandom() * 2.5F);
   }

   private Vector3f[] getOffsets(Vector3f[] offset) {
      return new Vector3f[]{this.offsetVector(offset[0]), this.offsetVector(offset[1]), this.offsetVector(offset[2]), this.offsetVector(offset[3])};
   }

   public Quaternionf randomQuaternion() {
      float u1 = this.random.nextFloat();
      float u2 = this.random.nextFloat();
      float u3 = this.random.nextFloat();
      double theta1 = 6.283185307179586 * u1;
      double theta2 = 6.283185307179586 * u2;
      double s1 = Math.sqrt(1.0 - u3);
      double s2 = Math.sqrt(u3);
      float x = (float)(s1 * Math.sin(theta1));
      float y = (float)(s1 * Math.cos(theta1));
      float z = (float)(s2 * Math.sin(theta2));
      float w = (float)(s2 * Math.cos(theta2));
      return new Quaternionf(x, y, z, w).normalize();
   }

   public GatewayBlockEntity(BlockPos pos, BlockState blockState) {
      super(BlockEntityTypes.GATEWAY, pos, blockState);
      this.rotations = new Quaternionf[]{this.randomQuaternion(), this.randomQuaternion(), this.randomQuaternion(), this.randomQuaternion()};
      this.offsets1 = this.getOffsets();
      this.offsets2 = this.getOffsets(this.offsets1);
   }

   public Vector3d getPosition(BlockPos pos, BlockState state, int i) {
      Axis value = (Axis)state.getValue(GatewayBlock.AXIS);
      return new Vector3d(
         pos.getX() + (value == Axis.X ? offsets[i].x : (value == Axis.Y ? offsets[i].x : 0.5F)),
         pos.getY() + (value == Axis.X ? offsets[i].y : (value == Axis.Y ? 0.5F : offsets[i].y)),
         pos.getZ() + (value == Axis.X ? 0.5F : (value == Axis.Y ? offsets[i].y : offsets[i].x))
      );
   }

   public static void clientTick(Level level, BlockPos pos, BlockState state, GatewayBlockEntity blockEntity) {
      for (int i = 0; i < 4; i++) {
         long time = level.getGameTime();
         if ((time ^ pos.getX() * 77L ^ pos.getY() * 66L ^ pos.getZ() * 55L + i * 44L) % 30L == 0L) {
            Vector3d position = blockEntity.getPosition(pos, state, i);
            Player player = level.getNearestPlayer(position.x, position.y, position.z, 32.0, false);
            boolean isClose = false;
            if (player != null) {
               double distance = player.distanceToSqr(position.x, position.y, position.z);
               if (distance < 256.0) {
                  lastCloseTick.put(player.getUUID(), time);
               }

               isClose = lastCloseTick.getOrDefault(player.getUUID(), 0L) + 30L > time;
            }

            blockEntity.state[i] = isClose;
         }

         blockEntity.lastTime[i] = blockEntity.time[i];
         if (blockEntity.state[i]) {
            blockEntity.time[i] = Math.min(1.0F, blockEntity.time[i] + 0.033333335F);
         } else {
            blockEntity.time[i] = Math.max(0.0F, blockEntity.time[i] - 0.033333335F);
         }

         float threshold = 0.75F;
         if (blockEntity.time[i] > threshold && blockEntity.lastTime[i] <= threshold) {
            playSound(level, pos, Sounds.ASSEMBLE);
         } else if (blockEntity.time[i] <= threshold && blockEntity.lastTime[i] > threshold) {
            playSound(level, pos, Sounds.DISASSEMBLE);
         }
      }
   }

   public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, GatewayBlockEntity blockEntity) {
      if (blockEntity.color == 0) {
         blockEntity.color = 1;
         GatewayExecutorController.submit(() -> {
            try {
               PortalDataManager.PortalPair pair = PortalDataManager.search(level, pos, true);
               applyPortalColor(level, pos, blockEntity, pair);
            } catch (Throwable var4) {
               throw new RuntimeException("Exception in searcher thread", var4);
            }
         });
      }
   }

   private static void applyPortalColor(ServerLevel level, BlockPos pos, GatewayBlockEntity blockEntity, PortalDataManager.PortalPair pair) {
      blockEntity.setColor(pair.getTarget(pos).color());
      level.getChunkSource().blockChanged(pos);
   }

   public void setColor(int color) {
      this.color = color;
      this.setChanged();
   }

   private static void playSound(Level level, BlockPos pos, SoundEvent sound) {
      float volume = level.random.nextFloat() * 0.1F + 0.1F;
      float pitch = level.random.nextFloat() * 0.4F + 0.8F;
      level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), sound, SoundSource.BLOCKS, volume, pitch, false);
   }

   public static void teleportEntity(ServerLevel level, BlockPos pos, Entity entity) {
      PortalDataManager.PortalPair pair = PortalDataManager.search(level, pos, false);
      if (pair == null) {
         entity.sendSystemMessage(Component.translatable("immersive_gateways.not_loaded_yet"));
      } else {
         if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50, 0));
         }

         PortalDataManager.Portal portal = pair.getTarget(pos);
         BlockPos targetPos = portal.getSafePosition(level);
         if (!level.getWorldBorder().isWithinBounds(targetPos)) {
            entity.sendSystemMessage(Component.translatable("immersive_gateways.outside_world_border"));
         } else {
            double portalCenterX = (portal.boundingBox().maxX() + portal.boundingBox().minX()) / 2.0;
            double portalCenterZ = (portal.boundingBox().maxZ() + portal.boundingBox().minZ()) / 2.0;
            double deltaX = portalCenterX - targetPos.getX();
            double deltaZ = portalCenterZ - targetPos.getZ();
            float targetYRot = (float)(Math.toDegrees(Math.atan2(deltaZ, deltaX)) + 450.0) % 360.0F;
            float finalTargetYRot = Math.round(targetYRot / 90.0F) * 90;
            double targetX = targetPos.getX() + 0.5;
            double targetY = targetPos.getY();
            double targetZ = targetPos.getZ() + 0.5;
            level.getServer().execute(() -> {
               teleportEntity(level, entity, targetX, targetY, targetZ, finalTargetYRot, entity.getXRot());
               level.playSound(null, pair.first().boundingBox().getCenter(), Sounds.GATEWAY, SoundSource.BLOCKS, 1.0F, 1.0F);
               level.playSound(null, pair.second().boundingBox().getCenter(), Sounds.GATEWAY, SoundSource.BLOCKS, 1.0F, 1.0F);
            });
         }
      }
   }

   public static void teleportEntity(ServerLevel level, Entity entity, double x, double y, double z, float yaw, float pitch) {
      BlockPos pos = BlockPos.containing(x, y, z);
      if (Level.isInSpawnableBounds(pos)) {
         Set<RelativeMovement> flags = EnumSet.noneOf(RelativeMovement.class);
         flags.add(RelativeMovement.X);
         flags.add(RelativeMovement.Y);
         flags.add(RelativeMovement.Z);
         flags.add(RelativeMovement.X_ROT);
         flags.add(RelativeMovement.Y_ROT);
         if (entity.teleportTo(level, x, y, z, flags, Mth.wrapDegrees(yaw), Mth.wrapDegrees(pitch))) {
            if (!(entity instanceof LivingEntity living && living.isFallFlying())) {
               entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
               entity.setOnGround(true);
            }

            if (entity instanceof PathfinderMob mob) {
               mob.getNavigation().stop();
            }

            if (entity instanceof ServerPlayer player) {
               player.hurtMarked = true;
            }
         }
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      if (this.level != null && this.level.isClientSide()) {
         this.color = tag.getInt("Color");
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = new CompoundTag();
      tag.put("Color", IntTag.valueOf(this.color));
      return tag;
   }

   public int getColor() {
      return this.color;
   }
}
