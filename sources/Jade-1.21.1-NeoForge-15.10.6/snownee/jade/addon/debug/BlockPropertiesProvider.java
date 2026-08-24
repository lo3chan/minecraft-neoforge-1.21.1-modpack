package snownee.jade.addon.debug;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum BlockPropertiesProvider implements IBlockComponentProvider {
   INSTANCE;

   public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
      Properties properties = accessor.getBlock().properties();
      IThemeHelper themes = IThemeHelper.get();
      tooltip.add(Component.translatable("jade.block_destroy_time", new Object[]{themes.info(properties.destroyTime)}));
      tooltip.add(Component.translatable("jade.block_explosion_resistance", new Object[]{themes.info(properties.explosionResistance)}));
      if (properties.jumpFactor != 1.0F) {
         tooltip.add(Component.translatable("jade.block_jump_factor", new Object[]{themes.info(properties.jumpFactor)}));
      }

      if (properties.speedFactor != 1.0F) {
         tooltip.add(Component.translatable("jade.block_speed_factor", new Object[]{themes.info(properties.speedFactor)}));
      }

      int igniteOdds = ((FireBlock)Blocks.FIRE).getIgniteOdds(accessor.getBlockState());
      if (igniteOdds != 0) {
         tooltip.add(Component.translatable("jade.block_ignite_odds", new Object[]{themes.info(igniteOdds)}));
      }

      int burnOdds = ((FireBlock)Blocks.FIRE).getBurnOdds(accessor.getBlockState());
      if (burnOdds != 0) {
         tooltip.add(Component.translatable("jade.block_burn_odds", new Object[]{themes.info(burnOdds)}));
      }
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.DEBUG_BLOCK_PROPERTIES;
   }

   @Override
   public boolean enabledByDefault() {
      return false;
   }
}
