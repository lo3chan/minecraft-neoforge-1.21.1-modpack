package net.Pandarix.item;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.Pandarix.BACommon;
import net.Pandarix.sound.ModSounds;
import net.Pandarix.util.BetterBrushTiers;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.DispenserBlock;

public class ModItems {
   public static final Registrar<Item> ITEMS = BACommon.REGISTRIES.get().get(Registries.ITEM);
   public static final RegistrySupplier<Item> DISC_SWINGS = registerItem(
      "music_disc_swings", () -> new Item(new Properties().jukeboxPlayable(ModSounds.SWINGS_SONG_KEY).stacksTo(1).rarity(Rarity.RARE))
   );
   public static final RegistrySupplier<Item> IRON_BRUSH = registerItem(
      "iron_brush", () -> new BetterBrushItem(new Properties().durability(128), BetterBrushTiers.IRON)
   );
   public static final RegistrySupplier<Item> DIAMOND_BRUSH = registerItem(
      "diamond_brush", () -> new BetterBrushItem(new Properties().durability(512), BetterBrushTiers.DIAMOND)
   );
   public static final RegistrySupplier<Item> NETHERITE_BRUSH = registerItem(
      "netherite_brush", () -> new BetterBrushItem(new Properties().durability(1024).fireResistant(), BetterBrushTiers.NETHERITE)
   );
   public static final RegistrySupplier<Item> ARTIFACT_SHARDS = registerItem("artifact_shards", () -> new Item(new Properties().rarity(Rarity.UNCOMMON)));
   public static final RegistrySupplier<Item> UNIDENTIFIED_ARTIFACT = registerItem(
      "unidentified_artifact", () -> new Item(new Properties().rarity(Rarity.UNCOMMON))
   );
   public static final RegistrySupplier<Item> BOMB_ITEM = registerItem("bomb", () -> new BombItem(new Properties().rarity(Rarity.COMMON).stacksTo(16)));
   public static final RegistrySupplier<Item> TORRENT_TOTEM = registerItem(
      "torrent_totem", () -> new TorrentTotemItem(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1).durability(32))
   );
   public static final RegistrySupplier<Item> SOUL_TOTEM = registerItem(
      "soul_totem", () -> new SoulTotemItem(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1).durability(32))
   );

   private static RegistrySupplier<Item> registerItem(String id, Supplier<Item> supplier) {
      return ITEMS.register(BACommon.createResource(id), supplier);
   }

   public static void register() {
      BACommon.logRegistryEvent(ITEMS);
      registerDispenseBehaviors();
   }

   private static void registerDispenseBehaviors() {
      Consumer<Item> registerBrushBehavior = item -> {
         DispenseItemBehavior brushBehavior = (DispenseItemBehavior)DispenserBlock.DISPENSER_REGISTRY.get(Items.BRUSH);
         if (brushBehavior == null) {
            BACommon.LOGGER.warn("No dispense behavior registered for minecraft:brush; skipping dispenser support for {}", item);
         } else {
            DispenserBlock.registerBehavior(item, brushBehavior);
         }
      };
      IRON_BRUSH.listen(registerBrushBehavior);
      DIAMOND_BRUSH.listen(registerBrushBehavior);
      NETHERITE_BRUSH.listen(registerBrushBehavior);
   }
}
