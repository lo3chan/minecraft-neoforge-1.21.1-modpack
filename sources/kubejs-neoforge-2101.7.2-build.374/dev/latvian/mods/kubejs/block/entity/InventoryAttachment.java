package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class InventoryAttachment implements BlockEntityAttachment {
   public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType(KubeJS.id("inventory"), InventoryAttachment.Factory.class);
   public final int width;
   public final int height;
   public final KubeBlockEntity blockEntity;
   public final ItemPredicate inputFilter;
   public final InventoryAttachment.Wrapped inventory;

   public InventoryAttachment(KubeBlockEntity blockEntity, int width, int height, @Nullable ItemPredicate inputFilter) {
      this.width = width;
      this.height = height;
      this.blockEntity = blockEntity;
      this.inputFilter = inputFilter;
      this.inventory = this.createInventory();
   }

   protected InventoryAttachment.Wrapped createInventory() {
      return new InventoryAttachment.Wrapped(this);
   }

   @Override
   public Object getWrappedObject() {
      return this.inventory;
   }

   @Nullable
   @Override
   public <CAP, SRC> CAP getCapability(BlockCapability<CAP, SRC> capability) {
      return (CAP)(capability == ItemHandler.BLOCK ? this.inventory : null);
   }

   public ListTag serialize(Provider registries) {
      ListTag list = new ListTag();

      for (int i = 0; i < this.width * this.height; i++) {
         ItemStack stack = (ItemStack)this.inventory.stacks().get(i);
         if (!stack.isEmpty()) {
            CompoundTag itemTag = (CompoundTag)stack.save(registries, new CompoundTag());
            itemTag.putByte("slot", (byte)i);
            list.add(itemTag);
         }
      }

      return list;
   }

   @Override
   public void deserialize(Provider registries, Tag tag) {
      this.inventory.setSize(this.width * this.height);
      if (tag instanceof ListTag list) {
         for (int i = 0; i < list.size(); i++) {
            CompoundTag itemTag = list.getCompound(i);
            byte slot = itemTag.getByte("slot");
            if (slot >= 0 && slot < this.width * this.height) {
               this.inventory.stacks().set(slot, ItemStack.parse(registries, itemTag).orElse(ItemStack.EMPTY));
            }
         }
      }
   }

   @Override
   public void onRemove(ServerLevel level, KubeBlockEntity blockEntity, BlockState newState) {
      Containers.dropContents(blockEntity.getLevel(), blockEntity.getBlockPos(), this.inventory.stacks());
   }

   public record Factory(int width, int height, Optional<ItemPredicate> inputFilter) implements BlockEntityAttachmentFactory {
      @Override
      public BlockEntityAttachment create(BlockEntityAttachmentInfo info, KubeBlockEntity entity) {
         return new InventoryAttachment(entity, this.width, this.height, this.inputFilter.orElse(null));
      }

      @Override
      public List<BlockCapability<?, ?>> getCapabilities() {
         return List.of(ItemHandler.BLOCK);
      }
   }

   public static class Wrapped extends ItemStackHandler implements InventoryKJS {
      protected final InventoryAttachment attachment;

      public Wrapped(InventoryAttachment attachment) {
         super(attachment.width * attachment.height);
         this.attachment = attachment;
      }

      public NonNullList<ItemStack> stacks() {
         return this.stacks;
      }

      protected void onContentsChanged(int slot) {
         this.attachment.blockEntity.save();
      }

      public boolean isItemValid(int slot, ItemStack stack) {
         return (this.attachment.inputFilter == null || this.attachment.inputFilter.test(stack)) && super.isItemValid(slot, stack);
      }

      @Override
      public int kjs$getWidth() {
         return this.attachment.width;
      }

      @Override
      public int kjs$getHeight() {
         return this.attachment.height;
      }
   }
}
