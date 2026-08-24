package snownee.jade.addon.core;

import java.text.DecimalFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IToggleableProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.theme.ThemeHelper;

public abstract class DistanceProvider implements IToggleableProvider {
   public static final DecimalFormat fmt = new DecimalFormat("#.#");
   private static final int[] colors = new int[]{15702682, 10868391, 9489145, 11545143, 1673044, 678090};

   public static DistanceProvider.ForBlock getBlock() {
      return DistanceProvider.ForBlock.INSTANCE;
   }

   public static DistanceProvider.ForEntity getEntity() {
      return DistanceProvider.ForEntity.INSTANCE;
   }

   public static String distance(Accessor<?> accessor) {
      float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
      return fmt.format(accessor.getPlayer().getEyePosition(partialTick).distanceTo(accessor.getHitResult().getLocation()));
   }

   public static void xyz(ITooltip tooltip, Vec3i pos) {
      Component display = Component.translatable("jade.blockpos", new Object[]{display(pos.getX(), 0), display(pos.getY(), 1), display(pos.getZ(), 2)});
      String narrate = I18n.get("narration.jade.blockpos", new Object[]{narrate(pos.getX()), narrate(pos.getY()), narrate(pos.getZ())});
      tooltip.add(IElementHelper.get().text(display).message(narrate));
   }

   public static Component display(int i, int colorIndex) {
      if (IThemeHelper.get().isLightColorScheme()) {
         colorIndex += 3;
      }

      return Component.literal(Integer.toString(i)).withStyle(ThemeHelper.colorStyle(colors[colorIndex]));
   }

   public static String narrate(int i) {
      return i >= 0 ? Integer.toString(i) : I18n.get("narration.jade.negative", new Object[]{-i});
   }

   public void append(ITooltip tooltip, Accessor<?> accessor, BlockPos pos, IPluginConfig config) {
      boolean distance = config.get(JadeIds.CORE_DISTANCE);
      String distanceVal = distance ? distance(accessor) : null;
      String distanceMsg = distance ? I18n.get("narration.jade.distance", new Object[]{distanceVal}) : null;
      if (config.get(JadeIds.CORE_COORDINATES)) {
         if (config.get(JadeIds.CORE_REL_COORDINATES) && Screen.hasControlDown()) {
            xyz(tooltip, pos.subtract(BlockPos.containing(accessor.getPlayer().getEyePosition())));
         } else {
            xyz(tooltip, pos);
         }

         if (distance) {
            tooltip.append(IElementHelper.get().text(Component.translatable("jade.distance1", new Object[]{distanceVal})).message(distanceMsg));
         }
      } else if (distance) {
         tooltip.add(IElementHelper.get().text(Component.translatable("jade.distance2", new Object[]{distanceVal})).message(distanceMsg));
      }
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.CORE_DISTANCE;
   }

   @Override
   public boolean isRequired() {
      return true;
   }

   @Override
   public int getDefaultPriority() {
      return -4600;
   }

   public static class ForBlock extends DistanceProvider implements IBlockComponentProvider {
      private static final DistanceProvider.ForBlock INSTANCE = new DistanceProvider.ForBlock();

      public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
         this.append(tooltip, accessor, accessor.getPosition(), config);
      }
   }

   public static class ForEntity extends DistanceProvider implements IEntityComponentProvider {
      private static final DistanceProvider.ForEntity INSTANCE = new DistanceProvider.ForEntity();

      public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
         this.append(tooltip, accessor, accessor.getEntity().blockPosition(), config);
      }
   }
}
