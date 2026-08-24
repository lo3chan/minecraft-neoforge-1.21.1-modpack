package mezz.jei.library.config;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag.Default;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

final class ModIdFormatDetectionHelper {
   private static final Logger LOGGER = LogManager.getLogger();

   private ModIdFormatDetectionHelper() {
   }

   public static Component detectModNameTooltipFormatting() {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      List<Component> tooltip = getTestTooltip(player, new ItemStack(Items.APPLE));
      return ModIdFormatConfig.detectModNameTooltipFormatting(tooltip);
   }

   private static List<Component> getTestTooltip(@Nullable Player player, ItemStack itemStack) {
      try {
         return itemStack.getTooltipLines(TooltipContext.EMPTY, player, Default.NORMAL);
      } catch (RuntimeException | LinkageError var3) {
         LOGGER.error("Error while Testing for mod name formatting", var3);
         return List.of();
      }
   }
}
