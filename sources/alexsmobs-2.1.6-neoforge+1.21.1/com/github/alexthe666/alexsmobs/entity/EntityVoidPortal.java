package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemDimensionalCarver;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTeleportQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.apache.commons.lang3.tuple.Triple;

public class EntityVoidPortal extends Entity {
   protected static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.defineId(EntityVoidPortal.class, EntityDataSerializers.DIRECTION);
   protected static final EntityDataAccessor<Integer> LIFESPAN = SynchedEntityData.defineId(EntityVoidPortal.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Boolean> SHATTERED = SynchedEntityData.defineId(EntityVoidPortal.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Optional<BlockPos>> DESTINATION = SynchedEntityData.defineId(
      EntityVoidPortal.class, EntityDataSerializers.OPTIONAL_BLOCK_POS
   );
   private static final EntityDataAccessor<Optional<UUID>> SISTER_UUID = SynchedEntityData.defineId(EntityVoidPortal.class, EntityDataSerializers.OPTIONAL_UUID);
   public ResourceKey<Level> exitDimension;
   private boolean madeOpenNoise = false;
   private boolean madeCloseNoise = false;
   private boolean isDummy = false;
   private boolean hasClearedObstructions;

   public EntityVoidPortal(EntityType<?> entityTypeIn, Level worldIn) {
      super(entityTypeIn, worldIn);
   }

   public EntityVoidPortal(Level world, ItemDimensionalCarver item) {
      this(AMEntityRegistry.VOID_PORTAL.get(), world);
      if (item == AMItemRegistry.SHATTERED_DIMENSIONAL_CARVER.get()) {
         this.setShattered(true);
         this.setLifespan(2000);
      } else {
         this.setShattered(false);
         this.setLifespan(1200);
      }
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity amServerEntity) {
      return AMPlatform.getEntitySpawningPacket(this, amServerEntity);
   }

   public void tick() {
      super.tick();
      if (this.tickCount == 1 && this.getLifespan() == 0) {
         this.setLifespan(2000);
      }

      if (!this.madeOpenNoise) {
         this.gameEvent(GameEvent.ENTITY_PLACE);
         this.playSound(AMSoundRegistry.VOID_PORTAL_OPEN.get(), 1.0F, 1.0F + this.random.nextFloat() * 0.2F);
         this.madeOpenNoise = true;
      }

      Direction direction2 = this.getAttachmentFacing().getOpposite();
      float minX = -0.15F;
      float minY = -0.15F;
      float minZ = -0.15F;
      float maxX = 0.15F;
      float maxY = 0.15F;
      float maxZ = 0.15F;
      switch (direction2) {
         case NORTH:
         case SOUTH:
            minX = -1.5F;
            maxX = 1.5F;
            minY = -1.5F;
            maxY = 1.5F;
            break;
         case EAST:
         case WEST:
            minZ = -1.5F;
            maxZ = 1.5F;
            minY = -1.5F;
            maxY = 1.5F;
            break;
         case UP:
         case DOWN:
            minX = -1.5F;
            maxX = 1.5F;
            minZ = -1.5F;
            maxZ = 1.5F;
      }

      AABB bb = new AABB(this.getX() + minX, this.getY() + minY, this.getZ() + minZ, this.getX() + maxX, this.getY() + maxY, this.getZ() + maxZ);
      this.setBoundingBox(bb);
      if (this.level().isClientSide() && this.random.nextFloat() < 0.5F && Math.min(this.tickCount, this.getLifespan()) >= 20) {
         double particleX = this.getBoundingBox().minX + this.random.nextFloat() * (this.getBoundingBox().maxX - this.getBoundingBox().minX);
         double particleY = this.getBoundingBox().minY + this.random.nextFloat() * (this.getBoundingBox().maxY - this.getBoundingBox().minY);
         double particleZ = this.getBoundingBox().minZ + this.random.nextFloat() * (this.getBoundingBox().maxZ - this.getBoundingBox().minZ);
         this.level()
            .addParticle(
               (ParticleOptions)AMParticleRegistry.WORM_PORTAL.get(),
               particleX,
               particleY,
               particleZ,
               0.1 * this.random.nextGaussian(),
               0.1 * this.random.nextGaussian(),
               0.1 * this.random.nextGaussian()
            );
      }

      List<Entity> entities = new ArrayList<>();
      entities.addAll(this.level().getEntities(this, bb.deflate(0.20000000298023224)));
      entities.addAll(this.level().getEntitiesOfClass(EntityVoidWorm.class, bb.inflate(1.5)));
      if (!this.level().isClientSide()) {
         MinecraftServer server = this.level().getServer();
         if (this.getDestination() != null && this.getLifespan() > 20 && this.tickCount > 20) {
            BlockPos offsetPos = this.getDestination().relative(this.getAttachmentFacing().getOpposite(), 2);

            for (Entity e : entities) {
               if (!e.isOnPortalCooldown()
                  && !e.isShiftKeyDown()
                  && !(e instanceof EntityVoidPortal)
                  && !AMCompat.isMultipart(e)
                  && !(e instanceof PartEntity)
                  && !e.getType().builtInRegistryHolder().is(AMTagRegistry.VOID_PORTAL_IGNORES)) {
                  if (e instanceof EntityVoidWormPart) {
                     if (this.getLifespan() < 22) {
                        this.setLifespan(this.getLifespan() + 1);
                     }
                  } else if (e instanceof EntityVoidWorm) {
                     ((EntityVoidWorm)e).teleportTo(Vec3.atCenterOf(this.getDestination()));
                     e.setPortalCooldown();
                     ((EntityVoidWorm)e).resetPortalLogic();
                  } else {
                     boolean flag = true;
                     if (this.exitDimension != null) {
                        ServerLevel dimWorld = server.getLevel(this.exitDimension);
                        if (dimWorld != null && this.level().dimension() != this.exitDimension) {
                           this.teleportEntityFromDimension(e, dimWorld, offsetPos, true);
                           flag = false;
                        }
                     }

                     if (flag) {
                        e.teleportTo(offsetPos.getX() + 0.5F, offsetPos.getY() + 0.5F, offsetPos.getZ() + 0.5F);
                        e.setPortalCooldown();
                     }
                  }
               }
            }
         }
      }

      this.setLifespan(this.getLifespan() - 1);
      if (this.getLifespan() <= 20 && !this.madeCloseNoise) {
         this.gameEvent(GameEvent.ENTITY_PLACE);
         this.playSound(AMSoundRegistry.VOID_PORTAL_CLOSE.get(), 1.0F, 1.0F + this.random.nextFloat() * 0.2F);
         this.madeCloseNoise = true;
      }

      if (this.getLifespan() <= 0) {
         this.remove(RemovalReason.DISCARDED);
      }

      if (this.tickCount > 1) {
         this.clearObstructions();
      }
   }

   private void teleportEntityFromDimension(Entity entity, ServerLevel endpointWorld, BlockPos endpoint, boolean b) {
      if (entity instanceof ServerPlayer) {
         AMTeleportQueue.PLAYERS.add(Triple.of((ServerPlayer)entity, endpointWorld, endpoint));
         if (this.getSisterId() == null) {
            this.createAndSetSister(endpointWorld, Direction.DOWN);
         }
      } else {
         entity.unRide();
         entity.setLevel(endpointWorld);
         Entity teleportedEntity = AMCompat.create(entity.getType(), endpointWorld);
         if (teleportedEntity != null) {
            teleportedEntity.restoreFrom(entity);
            teleportedEntity.moveTo(endpoint.getX() + 0.5, endpoint.getY() + 0.5, endpoint.getZ() + 0.5, entity.getYRot(), entity.getXRot());
            teleportedEntity.setYHeadRot(entity.getYHeadRot());
            teleportedEntity.setPortalCooldown();
            endpointWorld.addFreshEntity(teleportedEntity);
         }

         entity.remove(RemovalReason.DISCARDED);
      }
   }

   public void clearObstructions() {
      if (!this.hasClearedObstructions && this.isShattered() && this.getDestination() != null) {
         this.hasClearedObstructions = true;

         for (int i = -1; i <= -1; i++) {
            for (int j = -1; j <= -1; j++) {
               for (int k = -1; k <= -1; k++) {
                  BlockPos toAir = this.getDestination().offset(i, j, k);
                  this.level().destroyBlock(toAir, true);
               }
            }
         }
      }
   }

   public Direction getAttachmentFacing() {
      return (Direction)this.entityData.get(ATTACHED_FACE);
   }

   public void setAttachmentFacing(Direction facing) {
      this.entityData.set(ATTACHED_FACE, facing);
   }

   public int getLifespan() {
      return (Integer)this.entityData.get(LIFESPAN);
   }

   public void setLifespan(int i) {
      this.entityData.set(LIFESPAN, i);
   }

   public boolean isShattered() {
      return (Boolean)this.entityData.get(SHATTERED);
   }

   public void setShattered(boolean set) {
      this.entityData.set(SHATTERED, set);
   }

   public BlockPos getDestination() {
      return (BlockPos)((Optional)this.entityData.get(DESTINATION)).orElse(null);
   }

   public void setDestination(BlockPos destination) {
      this.entityData.set(DESTINATION, Optional.ofNullable(destination));
      if (this.getSisterId() == null && (this.exitDimension == null || this.exitDimension == this.level().dimension())) {
         this.createAndSetSister(this.level(), null);
      }
   }

   public void createAndSetSister(Level world, Direction dir) {
      EntityVoidPortal portal = AMCompat.create(AMEntityRegistry.VOID_PORTAL.get(), world);
      portal.setAttachmentFacing(dir != null ? dir : this.getAttachmentFacing().getOpposite());
      BlockPos safeDestination = this.getDestination();
      portal.teleportTo(safeDestination.getX() + 0.5F, safeDestination.getY() + 0.5F, safeDestination.getZ() + 0.5F);
      portal.link(this);
      portal.exitDimension = this.level().dimension();
      world.addFreshEntity(portal);
      portal.setShattered(this.isShattered());
   }

   public void setDestination(BlockPos destination, Direction dir) {
      this.entityData.set(DESTINATION, Optional.ofNullable(destination));
      if (this.getSisterId() == null && (this.exitDimension == null || this.exitDimension == this.level().dimension())) {
         this.createAndSetSister(this.level(), dir);
      }
   }

   public void link(EntityVoidPortal portal) {
      this.setSisterId(portal.getUUID());
      portal.setSisterId(this.getUUID());
      portal.setLifespan(this.getLifespan());
      this.setDestination(portal.blockPosition());
      portal.setDestination(this.blockPosition());
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(ATTACHED_FACE, Direction.DOWN);
      builder.define(LIFESPAN, 300);
      builder.define(SHATTERED, false);
      builder.define(SISTER_UUID, Optional.empty());
      builder.define(DESTINATION, Optional.empty());
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.entityData.set(ATTACHED_FACE, Direction.from3DDataValue(AMCompat.getByte(compound, "AttachFace")));
      this.setLifespan(AMCompat.getInt(compound, "Lifespan"));
      if (AMCompat.contains(compound, "Shattered")) {
         this.setShattered(AMCompat.getBoolean(compound, "Shattered"));
      }

      if (AMCompat.contains(compound, "DX")) {
         int i = AMCompat.getInt(compound, "DX");
         int j = AMCompat.getInt(compound, "DY");
         int k = AMCompat.getInt(compound, "DZ");
         this.entityData.set(DESTINATION, Optional.of(new BlockPos(i, j, k)));
      } else {
         this.entityData.set(DESTINATION, Optional.empty());
      }

      if (AMCompat.hasUUID(compound, "SisterUUID")) {
         this.setSisterId(AMCompat.getUUID(compound, "SisterUUID"));
      }

      if (AMCompat.contains(compound, "ExitDimension")) {
         this.exitDimension = Level.RESOURCE_KEY_CODEC
            .parse(NbtOps.INSTANCE, AMCompat.getTag(compound, "ExitDimension"))
            .resultOrPartial(AlexsMobs.LOGGER::error)
            .orElse(Level.OVERWORLD);
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putByte("AttachFace", (byte)((Direction)this.entityData.get(ATTACHED_FACE)).get3DDataValue());
      compound.putInt("Lifespan", this.getLifespan());
      compound.putBoolean("Shattered", this.isShattered());
      BlockPos blockpos = this.getDestination();
      if (blockpos != null) {
         compound.putInt("DX", blockpos.getX());
         compound.putInt("DY", blockpos.getY());
         compound.putInt("DZ", blockpos.getZ());
      }

      if (this.getSisterId() != null) {
         AMCompat.putUUID(compound, "SisterUUID", this.getSisterId());
      }

      if (this.exitDimension != null) {
         ResourceLocation.CODEC
            .encodeStart(NbtOps.INSTANCE, this.exitDimension.location())
            .resultOrPartial(AlexsMobs.LOGGER::error)
            .ifPresent(p_241148_1_ -> AMCompat.put(compound, "ExitDimension", p_241148_1_));
      }
   }

   public Entity getSister() {
      UUID id = this.getSisterId();
      return id != null && !this.level().isClientSide() ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   @Nullable
   public UUID getSisterId() {
      return (UUID)((Optional)this.entityData.get(SISTER_UUID)).orElse(null);
   }

   public void setSisterId(@Nullable UUID uniqueId) {
      this.entityData.set(SISTER_UUID, Optional.ofNullable(uniqueId));
   }
}
