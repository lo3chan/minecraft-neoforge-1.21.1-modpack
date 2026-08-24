package net.mehvahdjukaar.amendments.common.tile;

import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.amendments.reg.ModBlockProperties;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.amendments.reg.ModTags;
import net.mehvahdjukaar.moonlight.api.block.DynamicRenderedItemDisplayTile;
import net.mehvahdjukaar.moonlight.api.client.model.ModelDataKey;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData.Builder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ToolHookBlockTile extends DynamicRenderedItemDisplayTile {
   public static final ModelDataKey<ItemStack> ITEM = ModBlockProperties.ITEM;

   public ToolHookBlockTile(BlockPos pos, BlockState state) {
      super(ModRegistry.TOOL_HOOK_TILE.get(), pos, state);
   }

   protected Component getDefaultName() {
      return Component.literal("tool hook");
   }

   public boolean isNeverFancy() {
      return ClientConfigs.FAST_HOOKS.get();
   }

   public void addExtraModelData(Builder builder) {
      super.addExtraModelData(builder);
      builder.with(ITEM, this.getDisplayedItem());
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      Item item = stack.getItem();
      return isValidTool(item) ? super.canPlaceItem(index, stack) : false;
   }

   public static boolean isValidTool(Item item) {
      return item instanceof DiggerItem
         || item instanceof SwordItem
         || item instanceof TridentItem
         || item.builtInRegistryHolder().is(ModTags.GOES_IN_TRIPWIRE_HOOK);
   }

   public void updateTileOnInventoryChanged() {
      super.updateTileOnInventoryChanged();
      if (this.getDisplayedItem().isEmpty() && this.level != null) {
         this.level.setBlockAndUpdate(this.worldPosition, Blocks.TRIPWIRE_HOOK.withPropertiesOf(this.getBlockState()));
      }
   }

   public void updateClientVisualsOnLoad() {
      super.updateClientVisualsOnLoad();
      this.requestModelReload();
   }
}
