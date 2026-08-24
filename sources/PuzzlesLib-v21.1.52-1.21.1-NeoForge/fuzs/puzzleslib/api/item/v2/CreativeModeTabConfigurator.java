package fuzs.puzzleslib.api.item.v2;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.impl.item.CreativeModeTabConfiguratorImpl;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.level.ItemLike;

@Deprecated
public interface CreativeModeTabConfigurator {
   static CreativeModeTabConfigurator from(String modId, Holder<? extends ItemLike> icon) {
      return from(modId).icon(icon);
   }

   static CreativeModeTabConfigurator from(String modId, Supplier<ItemStack> icon) {
      return from(modId).icon(icon);
   }

   static CreativeModeTabConfigurator from(String modId) {
      return from(modId, "main");
   }

   static CreativeModeTabConfigurator from(String modId, String tabId) {
      return from(ResourceLocationHelper.fromNamespaceAndPath(modId, tabId));
   }

   static CreativeModeTabConfigurator from(ResourceLocation resourceLocation) {
      return new CreativeModeTabConfiguratorImpl(resourceLocation);
   }

   default CreativeModeTabConfigurator icon(Holder<? extends ItemLike> icon) {
      return this.icon((Supplier<ItemStack>)(() -> new ItemStack((ItemLike)icon.value())));
   }

   CreativeModeTabConfigurator icon(Supplier<ItemStack> var1);

   CreativeModeTabConfigurator icons(Supplier<ItemStack[]> var1);

   CreativeModeTabConfigurator displayItems(DisplayItemsGenerator var1);

   CreativeModeTabConfigurator withSearchBar();

   CreativeModeTabConfigurator appendEnchantmentsAndPotions();
}
