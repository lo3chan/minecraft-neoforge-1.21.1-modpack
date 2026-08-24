package snownee.jade.addon.vanilla;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum CropProgressProvider implements IBlockComponentProvider {
   INSTANCE;

   public IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
      if (accessor.getBlock() == Blocks.WHEAT) {
         return IElementHelper.get().item(new ItemStack(Items.WHEAT));
      } else {
         return accessor.getBlock() == Blocks.BEETROOTS ? IElementHelper.get().item(new ItemStack(Items.BEETROOT)) : null;
      }
   }

   public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
      BlockState state = accessor.getBlockState();
      Block block = state.getBlock();
      if (block instanceof CropBlock crop) {
         addMaturityTooltip(tooltip, (float)crop.getAge(state) / crop.getMaxAge());
      } else if (block instanceof BushBlock || block instanceof BonemealableBlock) {
         if (state.hasProperty(BlockStateProperties.AGE_2)) {
            addMaturityTooltip(tooltip, ((Integer)state.getValue(BlockStateProperties.AGE_2)).intValue() / 2.0F);
         } else if (state.hasProperty(BlockStateProperties.AGE_3)) {
            addMaturityTooltip(tooltip, ((Integer)state.getValue(BlockStateProperties.AGE_3)).intValue() / 3.0F);
         } else if (state.hasProperty(BlockStateProperties.AGE_4)) {
            addMaturityTooltip(tooltip, ((Integer)state.getValue(BlockStateProperties.AGE_4)).intValue() / 4.0F);
         } else if (state.hasProperty(BlockStateProperties.AGE_5)) {
            addMaturityTooltip(tooltip, ((Integer)state.getValue(BlockStateProperties.AGE_5)).intValue() / 5.0F);
         } else if (state.hasProperty(BlockStateProperties.AGE_7)) {
            addMaturityTooltip(tooltip, ((Integer)state.getValue(BlockStateProperties.AGE_7)).intValue() / 7.0F);
         } else if (state.hasProperty(BlockStateProperties.AGE_15)) {
            addMaturityTooltip(tooltip, ((Integer)state.getValue(BlockStateProperties.AGE_15)).intValue() / 15.0F);
         } else if (state.is(BlockTags.MAINTAINS_FARMLAND) && accessor.getLevel().getBlockState(accessor.getPosition().below()).getBlock() instanceof FarmBlock
            )
          {
            addMaturityTooltip(tooltip, 1.0F);
         }
      }
   }

   private static void addMaturityTooltip(ITooltip tooltip, float growthValue) {
      MutableComponent component;
      if (growthValue < 1.0F) {
         component = IThemeHelper.get().info(String.format("%.0f%%", growthValue * 100.0F));
      } else {
         component = IThemeHelper.get().success(Component.translatable("tooltip.jade.crop_mature"));
      }

      tooltip.add(Component.translatable("tooltip.jade.crop_growth", new Object[]{component}));
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_CROP_PROGRESS;
   }
}
