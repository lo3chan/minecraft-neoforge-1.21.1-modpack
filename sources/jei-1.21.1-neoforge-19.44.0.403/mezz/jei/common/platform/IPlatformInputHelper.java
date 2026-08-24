package mezz.jei.common.platform;

import com.mojang.blaze3d.platform.InputConstants.Key;
import mezz.jei.common.input.keys.IJeiKeyMappingCategoryBuilder;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.TooltipFlag;

public interface IPlatformInputHelper {
   boolean isActiveAndMatches(KeyMapping var1, Key var2);

   IJeiKeyMappingCategoryBuilder createKeyMappingCategoryBuilder(String var1);

   default TooltipFlag getClientTooltipFlag(TooltipFlag tooltipFlag) {
      return tooltipFlag;
   }

   default TooltipFlag getSearchTooltipFlag(TooltipFlag tooltipFlag) {
      return tooltipFlag;
   }
}
