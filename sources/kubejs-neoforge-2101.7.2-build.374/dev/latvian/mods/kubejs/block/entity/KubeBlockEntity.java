package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.plugin.builtin.event.BlockEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class KubeBlockEntity extends BlockEntity {
   public static final BlockEntityTicker<KubeBlockEntity> TICKER = (level, pos, state, entity) -> entity.tick();
   public final BlockEntityInfo info;
   public final ResourceKey<Block> blockKey;
   protected LevelBlock block;
   public final int x;
   public final int y;
   public final int z;
   public int tick;
   public int cycle;
   public CompoundTag data;
   public final Map<String, Object> attachments;
   public final transient BlockEntityAttachmentHolder[] attachmentArray;
   public UUID placerId;
   private BlockEntityTickKubeEvent tickEvent;
   private boolean save;
   private boolean sync;

   public KubeBlockEntity(BlockPos blockPos, BlockState blockState, BlockEntityInfo entityInfo) {
      super(entityInfo.entityType, blockPos, blockState);
      this.info = entityInfo;
      this.blockKey = blockState.kjs$getKey();
      this.x = blockPos.getX();
      this.y = blockPos.getY();
      this.z = blockPos.getZ();
      this.data = this.info.initialData.copy();
      if (entityInfo.attachments != null) {
         HashMap<String, Object> map = new HashMap<>(entityInfo.attachments.size());
         this.attachmentArray = new BlockEntityAttachmentHolder[entityInfo.attachments.size()];

         for (BlockEntityAttachmentInfo aInfo : entityInfo.attachments.values()) {
            BlockEntityAttachment f = aInfo.factory().create(aInfo, this);
            map.put(aInfo.id(), f.getWrappedObject());
            this.attachmentArray[aInfo.index()] = new BlockEntityAttachmentHolder(aInfo, f);
         }

         this.attachments = Map.copyOf(map);
      } else {
         this.attachments = Map.of();
         this.attachmentArray = BlockEntityAttachmentHolder.EMPTY_ARRAY;
      }
   }

   public void setLevel(Level level) {
      super.setLevel(level);
      this.block = null;
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.put("data", this.data);
      if (this.tick > 0) {
         tag.putInt("tick", this.tick);
      }

      if (this.cycle > 0) {
         tag.putInt("cycle", this.cycle);
      }

      if (this.placerId != null) {
         tag.putUUID("placer", this.placerId);
      }

      if (this.attachmentArray.length > 0) {
         CompoundTag data = new CompoundTag();

         for (BlockEntityAttachmentHolder entry : this.attachmentArray) {
            Tag t = entry.attachment().serialize(registries);
            if (t != null) {
               data.put(entry.info().id(), t);
            }
         }

         tag.put("attachments", data);
      }
   }

   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.data = tag.getCompound("data");
      this.tick = tag.getInt("tick");
      this.cycle = tag.getInt("cycle");
      this.placerId = tag.contains("placer") ? tag.getUUID("placer") : null;
      if (this.attachmentArray.length > 0) {
         CompoundTag data = tag.getCompound("attachments");

         for (BlockEntityAttachmentHolder entry : this.attachmentArray) {
            entry.attachment().deserialize(registries, data.get(entry.info().id()));
         }
      }
   }

   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = new CompoundTag();
      if (this.info.sync && !this.data.isEmpty()) {
         tag.put("data", this.data);
      }

      if (this.tick > 0) {
         tag.putInt("tick", this.tick);
      }

      if (this.cycle > 0) {
         tag.putInt("cycle", this.cycle);
      }

      return tag;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void save() {
      if (this.level != null) {
         if (this.info.getTicker(this.level) != null) {
            this.save = true;
         } else {
            this.level.blockEntityChanged(this.worldPosition);
         }
      }
   }

   public void sync() {
      if (this.level != null) {
         if (this.info.getTicker(this.level) != null) {
            this.sync = true;
         } else {
            this.save();
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 11);
         }
      }
   }

   public void sendEvent(int eventId, int data) {
      this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), eventId, data);
   }

   public boolean triggerEvent(int eventId, int data) {
      if (this.info.eventHandlers != null) {
         BlockEntityEventCallback e = (BlockEntityEventCallback)this.info.eventHandlers.get(eventId);
         if (e != null) {
            e.accept(this, data);
            return true;
         }
      }

      return false;
   }

   public LevelBlock getBlock() {
      if (this.block == null) {
         this.block = this.level.kjs$getBlock(this.worldPosition).cache(this).cache(this.getBlockState());
      }

      return this.block;
   }

   private void tick() {
      if (this.level != null) {
         if (this.level.isClientSide ? this.info.clientTicking : this.info.serverTicking) {
            if (this.tick % this.info.tickFrequency == this.info.tickOffset) {
               ScriptType side = this.level.kjs$getScriptType();

               try {
                  if (this.tickEvent == null) {
                     this.tickEvent = new BlockEntityTickKubeEvent(this);
                  }

                  BlockEvents.BLOCK_ENTITY_TICK.post(side, this.blockKey, this.tickEvent);
               } catch (Exception var5) {
                  side.console.error("Error while ticking KubeJS block entity '" + this.info.blockBuilder.id + "'", var5);
               }

               this.cycle++;
            }

            this.tick++;
         }

         if (!this.level.isClientSide && this.info.attachmentsTicking) {
            for (BlockEntityAttachmentHolder entry : this.attachmentArray) {
               entry.attachment().serverTick();
            }
         }

         if ((this.sync || this.save) && this.level.getGameTime() % 20L == 0L) {
            if (this.sync) {
               this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 11);
               this.save = true;
               this.sync = false;
            }

            if (this.save) {
               this.level.blockEntityChanged(this.worldPosition);
               this.save = false;
            }
         }
      }
   }

   @Nullable
   public Entity getPlacer() {
      return this.level == null ? null : this.level.kjs$getEntityByUUID(this.placerId);
   }
}
