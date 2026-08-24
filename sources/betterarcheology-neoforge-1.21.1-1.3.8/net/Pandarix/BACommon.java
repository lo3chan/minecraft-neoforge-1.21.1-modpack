package net.Pandarix;

import com.google.common.base.Suppliers;
import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import java.util.function.Supplier;
import net.Pandarix.block.ModBlocks;
import net.Pandarix.block.entity.ModBlockEntities;
import net.Pandarix.config.BAConfig;
import net.Pandarix.enchantment.ModEnchantments;
import net.Pandarix.entity.ModEntityTypes;
import net.Pandarix.events.ModEvents;
import net.Pandarix.item.ModItemGroup;
import net.Pandarix.item.ModItems;
import net.Pandarix.recipe.ModRecipes;
import net.Pandarix.screen.ModMenuTypes;
import net.Pandarix.sound.ModSounds;
import net.Pandarix.util.ModTags;
import net.Pandarix.villager.ModVillagers;
import net.Pandarix.world.structure.ModStructures;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BACommon {
   public static final String MOD_ID = "betterarcheology";
   public static final String MOD_NAME = "Better Archeology";
   public static final Logger LOGGER = LoggerFactory.getLogger("Better Archeology");
   public static final Configurator CONFIGURATOR = new Configurator("betterarcheology");
   public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get("betterarcheology"));

   public static void init() {
      CONFIGURATOR.register(BAConfig.class);
      ModStructures.register();
      ModEntityTypes.register();
      ModEnchantments.register();
      ModItemGroup.register();
      ModItems.register();
      ModBlocks.register();
      ModTags.register();
      ModBlockEntities.register();
      ModMenuTypes.register();
      ModRecipes.register();
      ModVillagers.register();
      ModEvents.register();
      ModSounds.register();
   }

   public static ResourceLocation createResource(String path) {
      return ResourceLocation.fromNamespaceAndPath("betterarcheology", path);
   }

   public static void logRegistryEvent(Registrar<?> registry) {
      LOGGER.info("Registering {} for {}", WordUtils.capitalize(registry.key().location().getPath().replace("_", " ") + "s"), "Better Archeology");
   }
}
