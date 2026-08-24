package at.petrak.hexcasting.common.lib;

import at.petrak.hexcasting.api.HexAPI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Builder;
import net.minecraft.world.item.CreativeModeTab.Row;

public class HexCreativeTabs {
   private static final Map<ResourceLocation, CreativeModeTab> TABS = new LinkedHashMap<>();
   public static final CreativeModeTab HEX = register("hexcasting", CreativeModeTab.builder(Row.TOP, 7).icon(() -> new ItemStack(HexItems.SPELLBOOK)));

   public static void registerCreativeTabs(BiConsumer<CreativeModeTab, ResourceLocation> r) {
      for (Entry<ResourceLocation, CreativeModeTab> e : TABS.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   private static CreativeModeTab register(String name, Builder tabBuilder) {
      CreativeModeTab tab = tabBuilder.title(Component.translatable("itemGroup." + name)).build();
      CreativeModeTab old = TABS.put(HexAPI.modLoc(name), tab);
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + name);
      } else {
         return tab;
      }
   }
}
