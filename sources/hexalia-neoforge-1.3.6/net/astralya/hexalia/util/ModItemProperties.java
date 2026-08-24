package net.astralya.hexalia.util;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.component.item.SpiritrootTetherData;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ItemLike;

public final class ModItemProperties {
   private ModItemProperties() {
   }

   public static void register() {
      registerBottledMoth();
      registerThornbow();
      registerSpiritrootTether();
   }

   private static void registerBottledMoth() {
      ItemPropertiesRegistry.register(
         (ItemLike)ModItems.BOTTLED_MOTH.get(), ResourceLocation.fromNamespaceAndPath("hexalia", "variant"), (stack, level, entity, seed) -> {
            DataComponentType<MothData> type = (DataComponentType<MothData>)ModComponents.MOTH.get();
            MothData data = (MothData)stack.get(type);
            return data != null ? data.variantId() : 0.0F;
         }
      );
   }

   private static void registerThornbow() {
      ItemPropertiesRegistry.register(
         (ItemLike)ModItems.THORNBOW.get(),
         ResourceLocation.withDefaultNamespace("pull"),
         (stack, level, entity, seed) -> entity instanceof LivingEntity && entity.getUseItem() == stack
            ? (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F
            : 0.0F
      );
      ItemPropertiesRegistry.register((ItemLike)ModItems.THORNBOW.get(), ResourceLocation.withDefaultNamespace("pulling"), (stack, level, entity, seed) -> {
         if (entity instanceof LivingEntity && entity.isUsingItem()) {
            return entity.getUseItem() == stack ? 1.0F : 0.0F;
         } else {
            return 0.0F;
         }
      });
   }

   private static void registerSpiritrootTether() {
      ItemPropertiesRegistry.register(
         (ItemLike)ModItems.SPIRITROOT_TETHER.get(), ResourceLocation.fromNamespaceAndPath("hexalia", "bound"), (stack, level, entity, seed) -> {
            DataComponentType<SpiritrootTetherData> type = (DataComponentType<SpiritrootTetherData>)ModComponents.SPIRITROOT_TETHER.get();
            SpiritrootTetherData data = (SpiritrootTetherData)stack.get(type);
            return data != null && data.hasMob() ? 1.0F : 0.0F;
         }
      );
   }
}
