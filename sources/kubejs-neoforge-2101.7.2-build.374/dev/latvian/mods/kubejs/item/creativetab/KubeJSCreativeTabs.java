package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryObjectStorage;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface KubeJSCreativeTabs {
   DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "kubejs");
   Supplier<CreativeModeTab> TAB = REGISTRY.register(
      "tab",
      () -> CreativeModeTab.builder()
         .title(CommonProperties.get().getCreativeModeTabName())
         .icon(
            () -> {
               ItemStack is = ItemStack.OPTIONAL_CODEC
                  .parse(RegistryAccessContainer.BUILTIN.json(), CommonProperties.get().creativeModeTabIcon)
                  .result()
                  .orElse(ItemStack.EMPTY);
               return is.isEmpty() ? Items.PURPLE_DYE.getDefaultInstance() : is;
            }
         )
         .displayItems((params, output) -> {
            for (BuilderBase<? extends Item> b : RegistryObjectStorage.ITEM) {
               output.accept(b.get().getDefaultInstance());
            }
         })
         .build()
   );
}
