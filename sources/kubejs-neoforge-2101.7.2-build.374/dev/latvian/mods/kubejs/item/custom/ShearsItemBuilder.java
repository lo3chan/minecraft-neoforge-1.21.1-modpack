package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags.Items;

@ReturnsSelf
public class ShearsItemBuilder extends ItemBuilder {
   public static final ResourceLocation[] SHEAR_TAGS = new ResourceLocation[]{Items.TOOLS_SHEAR.location()};
   public transient float speedBaseline;

   public static boolean isCustomShears(ItemStack stack) {
      return stack.getItem() instanceof ShearsItemBuilder.ShearsItemKJS;
   }

   public ShearsItemBuilder(ResourceLocation i) {
      super(i);
      this.speedBaseline(5.0F);
      this.parentModel(KubeAssetGenerator.HANDHELD_ITEM_MODEL);
      this.unstackable();
      this.tag(SHEAR_TAGS);
      this.tool = ShearsItem.createToolProperties();
   }

   public ShearsItemBuilder speedBaseline(float f) {
      this.speedBaseline = f;
      return this;
   }

   @Override
   public Item createObject() {
      ShearsItemBuilder.ShearsItemKJS item = new ShearsItemBuilder.ShearsItemKJS(this);
      DispenserBlock.registerBehavior(item, new ShearsDispenseItemBehavior());
      return item;
   }

   public static class ShearsItemKJS extends ShearsItem {
      public final ShearsItemBuilder builder;

      public ShearsItemKJS(ShearsItemBuilder builder) {
         super(builder.createItemProperties());
         this.builder = builder;
      }

      public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
         if (blockState.is(BlockTags.LEAVES)) {
            return 15.0F;
         } else if (blockState.is(Blocks.COBWEB)) {
            return this.builder.speedBaseline * 3.0F;
         } else if (blockState.is(Blocks.VINE) || blockState.is(Blocks.GLOW_LICHEN)) {
            return this.builder.speedBaseline / 2.5F;
         } else {
            return blockState.is(BlockTags.WOOL) ? this.builder.speedBaseline : super.getDestroySpeed(itemStack, blockState);
         }
      }
   }
}
