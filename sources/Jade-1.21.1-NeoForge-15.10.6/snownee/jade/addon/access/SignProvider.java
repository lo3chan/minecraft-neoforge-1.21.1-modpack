package snownee.jade.addon.access;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;

public class SignProvider implements IBlockComponentProvider {
   public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
      if (accessor.getBlockEntity() instanceof SignBlockEntity be) {
         boolean var11 = be.isFacingFrontText(accessor.getPlayer());
         tooltip.add(Component.translatable("jade.access.sign." + (var11 ? "front" : "back")));
         int i = 0;

         for (Component message : be.getFrontText().getMessages(true)) {
            i++;
            if (accessor.showDetails()) {
               tooltip.add(Component.translatable("jade.access.sign.line" + i, new Object[]{message}));
            } else {
               tooltip.add(message);
            }

            if (i >= 4) {
               break;
            }
         }
      }
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.ACCESS_SIGN;
   }
}
