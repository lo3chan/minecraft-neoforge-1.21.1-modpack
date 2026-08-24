package snownee.jade.addon.vanilla;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.jetbrains.annotations.Nullable;
import snownee.jade.addon.core.ObjectNameProvider;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.CompoundElement;

public enum WaxedProvider implements IBlockComponentProvider {
   INSTANCE;

   @Nullable
   public IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
      if (accessor.getPickedResult().isEmpty()) {
         return currentIcon;
      } else {
         IElementHelper helper = IElementHelper.get();
         IElement largeIcon = helper.item(accessor.getPickedResult());
         if (accessor.getBlockEntity() instanceof SignBlockEntity sign) {
            return (IElement)(sign.isWaxed() ? new CompoundElement(largeIcon, helper.item(Items.HONEYCOMB.getDefaultInstance(), 0.5F)) : largeIcon);
         } else {
            return currentIcon;
         }
      }
   }

   public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
      if (IWailaConfig.get().getGeneral().getEnableAccessibilityPlugin() && accessor.getBlockEntity() instanceof SignBlockEntity sign && sign.isWaxed()) {
         String message = tooltip.getMessage(JadeIds.CORE_OBJECT_NAME);
         if (!message.isBlank()) {
            tooltip.replace(JadeIds.CORE_OBJECT_NAME, IThemeHelper.get().title(Component.translatable("jade.waxed", new Object[]{message})));
         }
      }
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_WAXED;
   }

   @Override
   public int getDefaultPriority() {
      return ObjectNameProvider.getBlock().getDefaultPriority() + 10;
   }
}
