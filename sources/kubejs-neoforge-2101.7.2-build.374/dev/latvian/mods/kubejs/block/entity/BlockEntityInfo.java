package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.core.ServerPlayerKJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.DirectionWrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class BlockEntityInfo implements BlockEntityAttachmentHandler {
   public final transient BlockBuilder blockBuilder;
   public transient BlockEntityType<?> entityType;
   public transient CompoundTag initialData;
   public transient boolean serverTicking;
   public transient boolean clientTicking;
   public transient boolean attachmentsTicking;
   public transient int tickFrequency;
   public transient int tickOffset;
   public transient boolean sync;
   public transient Map<String, BlockEntityAttachmentInfo> attachments;
   public transient Int2ObjectMap<BlockEntityEventCallback> eventHandlers;

   public BlockEntityInfo(BlockBuilder blockBuilder) {
      this.blockBuilder = blockBuilder;
      this.initialData = new CompoundTag();
      this.serverTicking = false;
      this.clientTicking = false;
      this.attachmentsTicking = false;
      this.tickFrequency = 1;
      this.tickOffset = 0;
      this.sync = false;
      this.attachments = new HashMap<>(1);
      this.eventHandlers = new Int2ObjectArrayMap(0);
   }

   public void initialData(CompoundTag data) {
      this.initialData = data;
   }

   public void serverTicking() {
      this.serverTicking = true;
   }

   public void clientTicking() {
      this.clientTicking = true;
   }

   public void ticking() {
      this.serverTicking();
      this.clientTicking();
   }

   public void tickFrequency(int frequency) {
      this.tickFrequency = Math.max(1, frequency);
   }

   public void tickOffset(int offset) {
      this.tickOffset = Math.max(0, offset);
   }

   public void enableSync() {
      this.sync = true;
   }

   @Override
   public void attach(String id, BlockEntityAttachmentType type, Set<Direction> directions, BlockEntityAttachmentFactory factory) {
      this.attachments
         .put(
            id,
            new BlockEntityAttachmentInfo(
               id,
               type,
               this.attachments.size(),
               directions != null && !directions.isEmpty() ? EnumSet.copyOf(directions) : DirectionWrapper.EMPTY_SET,
               factory
            )
         );
      if (!this.attachmentsTicking && factory.isTicking()) {
         this.attachmentsTicking = true;
      }
   }

   public void eventHandler(int eventId, BlockEntityEventCallback callback) {
      this.eventHandlers.put(eventId, callback);
   }

   public void rightClickOpensInventory(String id) {
      this.blockBuilder.rightClick = e -> {
         if (e.getPlayer() instanceof ServerPlayerKJS p
            && e.getBlock().getEntity() instanceof KubeBlockEntity entity
            && entity.attachments.get(id) instanceof InventoryKJS inv) {
            p.kjs$openInventoryGUI(inv, this.blockBuilder.get().getName());
         }
      };
   }

   public void rightClickFillsTank(String id) {
      this.blockBuilder.rightClick = e -> {
         if (e.getPlayer() instanceof ServerPlayerKJS
            && e.getBlock().getEntity() instanceof KubeBlockEntity entity
            && entity.attachments.get(id) instanceof IFluidHandler tank) {
            FluidUtil.interactWithFluidHandler(e.getPlayer(), e.getHand(), tank);
         }
      };
   }

   @HideFromJS
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new KubeBlockEntity(pos, state, this);
   }

   @HideFromJS
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level) {
      if (level.isClientSide()) {
         return (BlockEntityTicker<T>)(this.clientTicking ? KubeBlockEntity.TICKER : null);
      } else {
         return (BlockEntityTicker<T>)(!this.serverTicking && !this.attachmentsTicking ? null : KubeBlockEntity.TICKER);
      }
   }

   @Override
   public String toString() {
      return "BlockEntityInfo[" + this.blockBuilder.id + "]";
   }
}
