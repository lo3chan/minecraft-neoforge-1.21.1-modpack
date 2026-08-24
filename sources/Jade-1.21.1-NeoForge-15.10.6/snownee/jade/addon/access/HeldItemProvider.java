package snownee.jade.addon.access;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;

public class HeldItemProvider implements IEntityComponentProvider {
   public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
      LivingEntity entity = (LivingEntity)accessor.getEntity();
      if (!entity.getMainHandItem().isEmpty()) {
         tooltip.add(Component.translatable("jade.access.held_item.main", new Object[]{entity.getMainHandItem().getHoverName()}));
      }

      if (!entity.getOffhandItem().isEmpty()) {
         tooltip.add(Component.translatable("jade.access.held_item.off", new Object[]{entity.getOffhandItem().getHoverName()}));
      }
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.ACCESS_HELD_ITEM;
   }
}
