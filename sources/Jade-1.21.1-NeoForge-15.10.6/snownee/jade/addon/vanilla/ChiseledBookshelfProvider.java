package snownee.jade.addon.vanilla;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.properties.Property;
import snownee.jade.addon.universal.ItemStorageProvider;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IDisplayHelper;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum ChiseledBookshelfProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, ItemStack> {
   INSTANCE;

   private ItemStack getHitBook(BlockAccessor accessor) {
      return accessor.showDetails() ? ItemStack.EMPTY : this.decodeFromData(accessor).orElse(ItemStack.EMPTY);
   }

   public boolean shouldRequestData(BlockAccessor accessor) {
      if (accessor.showDetails()) {
         return false;
      } else {
         OptionalInt slot = ((ChiseledBookShelfBlock)accessor.getBlock()).getHitSlot(accessor.getHitResult(), accessor.getBlockState());
         return !slot.isEmpty() && slot.getAsInt() < ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.size()
            ? (Boolean)accessor.getBlockState().getValue((Property)ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(slot.getAsInt()))
            : false;
      }
   }

   public IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
      ItemStack item = this.getHitBook(accessor);
      return item.isEmpty() ? null : IElementHelper.get().item(item);
   }

   public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
      ItemStack item = this.getHitBook(accessor);
      if (!item.isEmpty()) {
         tooltip.remove(JadeIds.UNIVERSAL_ITEM_STORAGE);
         tooltip.add(IDisplayHelper.get().stripColor(item.getHoverName()));
         if (item.has(DataComponents.STORED_ENCHANTMENTS)) {
            List<Component> list = Lists.newArrayList();
            ((ItemEnchantments)item.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY))
               .addToTooltip(TooltipContext.of(accessor.getLevel()), list::add, TooltipFlag.NORMAL);
            tooltip.addAll(list);
         }
      }
   }

   public ItemStack streamData(BlockAccessor accessor) {
      int slot = ((ChiseledBookShelfBlock)accessor.getBlock()).getHitSlot(accessor.getHitResult(), accessor.getBlockState()).orElse(-1);
      return slot == -1 ? null : ((ChiseledBookShelfBlockEntity)accessor.getBlockEntity()).getItem(slot);
   }

   @Override
   public StreamCodec<RegistryFriendlyByteBuf, ItemStack> streamCodec() {
      return ItemStack.OPTIONAL_STREAM_CODEC;
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_CHISELED_BOOKSHELF;
   }

   @Override
   public int getDefaultPriority() {
      return ItemStorageProvider.getBlock().getDefaultPriority() + 1;
   }
}
