package com.mcwfurnitures.kikoz.init;

import com.mcwfurnitures.kikoz.storage.FurnitureStorageContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ContainerInit {
   public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, "mcwfurnitures");
   public static final DeferredHolder<MenuType<?>, MenuType<FurnitureStorageContainer>> EXAMPLE_CHEST = CONTAINERS.register(
      "example_chest", () -> new MenuType(FurnitureStorageContainer::new, FeatureFlags.REGISTRY.allFlags())
   );
}
