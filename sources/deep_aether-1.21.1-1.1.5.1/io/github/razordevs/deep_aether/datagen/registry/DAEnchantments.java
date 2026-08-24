package io.github.razordevs.deep_aether.datagen.registry;

import com.aetherteam.aether.AetherTags.Items;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Builder;

public class DAEnchantments {
   public static final ResourceKey<Enchantment> GLOVES_REACH = registerKey("gloves_reach");

   private static ResourceKey<Enchantment> registerKey(String name) {
      return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("deep_aether", name));
   }

   public static void bootstrap(BootstrapContext<Enchantment> context) {
      HolderGetter<Item> items = context.lookup(Registries.ITEM);
      register(
         context,
         GLOVES_REACH,
         new Builder(
            Enchantment.definition(
               items.getOrThrow(Items.ACCESSORIES_GLOVES),
               1,
               3,
               Enchantment.dynamicCost(25, 25),
               Enchantment.dynamicCost(75, 25),
               8,
               new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
            )
         )
      );
   }

   private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Builder builder) {
      context.register(key, builder.build(key.location()));
   }
}
