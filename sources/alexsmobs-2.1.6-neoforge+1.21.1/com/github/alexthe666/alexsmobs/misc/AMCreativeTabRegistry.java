package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.CustomTabBehavior;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMCreativeTabRegistry {
   public static final DeferredRegister<CreativeModeTab> DEF_REG = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "alexsmobs");
   public static final Supplier<CreativeModeTab> TAB = DEF_REG.register(
      "alexsmobs",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("itemGroup.alexsmobs"))
         .withTabsBefore(new ResourceKey[]{CreativeModeTabs.SPAWN_EGGS})
         .icon(() -> new ItemStack((ItemLike)AMItemRegistry.TAB_ICON.get()))
         .displayItems((enabledFeatures, output) -> {
            for (Supplier<? extends Item> item : AMItemRegistry.DEF_REG.getEntries()) {
               if (item.get() instanceof CustomTabBehavior customTabBehavior) {
                  customTabBehavior.fillItemCategory(output);
               } else {
                  output.accept((ItemLike)item.get());
               }
            }
         })
         .build()
   );
}
