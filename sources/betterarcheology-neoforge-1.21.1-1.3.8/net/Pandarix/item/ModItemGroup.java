package net.Pandarix.item;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.Pandarix.BACommon;
import net.Pandarix.block.ModBlocks;
import net.Pandarix.enchantment.ModEnchantments;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Row;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;

public class ModItemGroup {
   public static final Registrar<CreativeModeTab> TABS = BACommon.REGISTRIES.get().get(Registries.CREATIVE_MODE_TAB);
   public static final RegistrySupplier<CreativeModeTab> BETTER_ARCHEOLOGY_ITEMGROUP = TABS.register(
      BACommon.createResource("betterarcheology"),
      () -> CreativeModeTab.builder(Row.TOP, 0)
         .icon(() -> new ItemStack((ItemLike)ModItems.UNIDENTIFIED_ARTIFACT.get()))
         .title(Component.translatable("itemGroup.betterarcheology"))
         .displayItems((parameters, output) -> {
            output.accept((ItemLike)ModItems.IRON_BRUSH.get());
            output.accept((ItemLike)ModItems.DIAMOND_BRUSH.get());
            output.accept((ItemLike)ModItems.NETHERITE_BRUSH.get());
            output.accept((ItemLike)ModItems.BOMB_ITEM.get());
            output.accept((ItemLike)ModItems.TORRENT_TOTEM.get());
            output.accept((ItemLike)ModItems.SOUL_TOTEM.get());
            output.accept((ItemLike)ModBlocks.GROWTH_TOTEM.get());
            output.accept((ItemLike)ModBlocks.RADIANCE_TOTEM.get());
            output.accept((ItemLike)ModItems.ARTIFACT_SHARDS.get());
            output.accept((ItemLike)ModItems.UNIDENTIFIED_ARTIFACT.get());
            output.accept((ItemLike)ModBlocks.ARCHEOLOGY_TABLE.get());
            output.accept((ItemLike)ModBlocks.CREEPER_FOSSIL.get());
            output.accept((ItemLike)ModBlocks.CREEPER_FOSSIL_BODY.get());
            output.accept((ItemLike)ModBlocks.CREEPER_FOSSIL_HEAD.get());
            output.accept((ItemLike)ModBlocks.VILLAGER_FOSSIL.get());
            output.accept((ItemLike)ModBlocks.VILLAGER_FOSSIL_BODY.get());
            output.accept((ItemLike)ModBlocks.VILLAGER_FOSSIL_HEAD.get());
            output.accept((ItemLike)ModBlocks.CHICKEN_FOSSIL.get());
            output.accept((ItemLike)ModBlocks.CHICKEN_FOSSIL_BODY.get());
            output.accept((ItemLike)ModBlocks.CHICKEN_FOSSIL_HEAD.get());
            output.accept((ItemLike)ModBlocks.OCELOT_FOSSIL.get());
            output.accept((ItemLike)ModBlocks.OCELOT_FOSSIL_BODY.get());
            output.accept((ItemLike)ModBlocks.OCELOT_FOSSIL_HEAD.get());
            output.accept((ItemLike)ModBlocks.WOLF_FOSSIL.get());
            output.accept((ItemLike)ModBlocks.WOLF_FOSSIL_BODY.get());
            output.accept((ItemLike)ModBlocks.WOLF_FOSSIL_HEAD.get());
            output.accept((ItemLike)ModBlocks.SHEEP_FOSSIL.get());
            output.accept((ItemLike)ModBlocks.SHEEP_FOSSIL_BODY.get());
            output.accept((ItemLike)ModBlocks.SHEEP_FOSSIL_HEAD.get());
            output.accept((ItemLike)ModBlocks.GUARDIAN_FOSSIL.get());
            output.accept((ItemLike)ModBlocks.GUARDIAN_FOSSIL_HEAD.get());
            output.accept((ItemLike)ModBlocks.GUARDIAN_FOSSIL_BODY.get());
            output.accept((ItemLike)ModBlocks.SUSPICIOUS_RED_SAND.get());
            output.accept((ItemLike)ModBlocks.SUSPICIOUS_DIRT.get());
            output.accept((ItemLike)ModBlocks.FOSSILIFEROUS_DIRT.get());
            output.accept((ItemLike)ModBlocks.CHISELED_BONE_BLOCK.get());
            output.accept((ItemLike)ModBlocks.ROTTEN_LOG.get());
            output.accept((ItemLike)ModBlocks.ROTTEN_PLANKS.get());
            output.accept((ItemLike)ModBlocks.ROTTEN_STAIRS.get());
            output.accept((ItemLike)ModBlocks.ROTTEN_SLAB.get());
            output.accept((ItemLike)ModBlocks.ROTTEN_TRAPDOOR.get());
            output.accept((ItemLike)ModBlocks.ROTTEN_DOOR.get());
            output.accept((ItemLike)ModBlocks.ROTTEN_PRESSURE_PLATE.get());
            output.accept((ItemLike)ModBlocks.ROTTEN_FENCE.get());
            output.accept((ItemLike)ModBlocks.ROTTEN_FENCE_GATE.get());
            output.accept((ItemLike)ModBlocks.INFESTED_MUD_BRICKS.get());
            output.accept((ItemLike)ModBlocks.CRACKED_MUD_BRICKS.get());
            output.accept((ItemLike)ModBlocks.CRACKED_MUD_BRICK_STAIRS.get());
            output.accept((ItemLike)ModBlocks.CRACKED_MUD_BRICK_SLAB.get());
            output.accept((ItemLike)ModBlocks.VASE.get());
            output.accept((ItemLike)ModBlocks.VASE_CREEPER.get());
            output.accept((ItemLike)ModBlocks.VASE_GREEN.get());
            output.accept((ItemLike)ModBlocks.EVOKER_TRAP.get());

            try {
               HolderGetter<Enchantment> getter = parameters.holders().asGetterLookup().lookupOrThrow(Registries.ENCHANTMENT);
               ModEnchantments.registerEnchantedBookWith(output, getter.getOrThrow(ModEnchantments.TUNNELING_KEY));
               ModEnchantments.registerEnchantedBookWith(output, getter.getOrThrow(ModEnchantments.PENETRATING_STRIKE_KEY));
               ModEnchantments.registerEnchantedBookWith(output, getter.getOrThrow(ModEnchantments.SOARING_WINDS_KEY));
            } catch (Exception var3) {
               BACommon.LOGGER.error("Could not add Enchanted Book to creative tab!", var3);
            }

            output.accept((ItemLike)ModItems.DISC_SWINGS.get());
         })
         .build()
   );

   public static void register() {
      BACommon.logRegistryEvent(TABS);
   }
}
