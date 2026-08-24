package snownee.jade.addon.vanilla;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import snownee.jade.api.Accessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum NextEntityDropProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
   INSTANCE;

   public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
      appendSeconds(tooltip, accessor, "NextEggIn", "jade.nextEgg");
      appendSeconds(tooltip, accessor, "NextScuteIn", "jade.nextScute");
   }

   public static void appendSeconds(ITooltip tooltip, Accessor<?> accessor, String tagKey, String translationKey) {
      if (accessor.getServerData().contains(tagKey)) {
         tooltip.add(
            Component.translatable(translationKey, new Object[]{IThemeHelper.get().seconds(accessor.getServerData().getInt(tagKey), accessor.tickRate())})
         );
      }
   }

   public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
      int max = 48000;
      if (accessor.getEntity() instanceof Chicken chicken) {
         if (!chicken.isBaby() && chicken.eggTime < max) {
            tag.putInt("NextEggIn", chicken.eggTime);
         }
      } else if (accessor.getEntity() instanceof Armadillo armadillo && !armadillo.isBaby() && armadillo.scuteTime < max) {
         tag.putInt("NextScuteIn", armadillo.scuteTime);
      }
   }

   public boolean shouldRequestData(EntityAccessor accessor) {
      return accessor.getEntity() instanceof LivingEntity living ? !living.isBaby() : true;
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_NEXT_ENTITY_DROP;
   }
}
