package zank.mods.open_in_inventory.impl.compat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;

public class CommonOpenInInventoryPlugin implements OpenInInventoryPlugin {
   @Override
   public void registerAction(OpenActionRegistry registry) {
      registry.registerIfPresent(ResourceLocation.tryParse("written_book"));
      registry.registerIfPresent(ResourceLocation.tryParse("writable_book"));
      ModSupportHelper helper = new ModSupportHelper(registry);
      if (helper.check("scannable")) {
         helper.tryRegister("scanner", true);
      }

      if (helper.check("extendedcrafting")) {
         helper.tryRegister("handheld_table");
      }

      if (helper.check("crafting_on_a_stick")) {
         for (String item : Arrays.asList(
            "crafting_table", "loom", "grindstone", "cartography_table", "stonecutter", "smithing_table", "anvil", "chipped_anvil", "damaged_anvil"
         )) {
            helper.tryRegister(item);
         }
      }

      if (helper.check("refinedstorage")) {
         for (String item : Arrays.asList(
            "portable_grid",
            "creative_portable_grid",
            "wireless_grid",
            "creative_wireless_grid",
            "wireless_fluid_grid",
            "creative_wireless_fluid_grid",
            "wireless_autocrafting_monitor",
            "creative_wireless_autocrafting_monitor",
            "wireless_crafting_monitor",
            "creative_wireless_crafting_monitor"
         )) {
            helper.tryRegister(item);
         }
      }

      if (helper.check("ae2")) {
         helper.tryRegister("wireless_terminal");
         helper.tryRegister("wireless_crafting_terminal");
         helper.tryRegister("certus_quartz_cutting_knife", true);
         helper.tryRegister("nether_quartz_cutting_knife", true);
         helper.tryRegister("portable_item_cell_{ae2:capacity}");
         helper.tryRegister("portable_fluid_cell_{ae2:capacity}");
      }

      if (helper.check("patchouli") && BuiltInRegistries.ITEM.containsKey(helper.id("guide_book"))) {
         Item baseItem = (Item)BuiltInRegistries.ITEM.get(helper.id("guide_book"));
         BuiltInRegistries.ITEM.stream().filter(baseItem.getClass()::isInstance).forEach(registry::register);
      }
   }

   @Override
   public void registerReplaceTemplate(Map<String, Collection<String>> registry) {
      registry.put("armor", List.of("helmet", "chestplate", "leggings", "boots"));
      registry.put("color", Arrays.stream(DyeColor.values()).<String>map(DyeColor::getName).toList());
      registry.put("ae2:capacity", List.of("1k", "4k", "16k", "64k", "256k"));
   }
}
