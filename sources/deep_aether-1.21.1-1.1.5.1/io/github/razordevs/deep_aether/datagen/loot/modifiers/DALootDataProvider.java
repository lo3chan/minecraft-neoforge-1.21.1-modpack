package io.github.razordevs.deep_aether.datagen.loot.modifiers;

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.loot.AetherLoot;
import com.aetherteam.nitrogen.loot.modifiers.AddDungeonLootModifier;
import io.github.razordevs.deep_aether.datagen.loot.DALoot;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.init.DAItems;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

public class DALootDataProvider extends GlobalLootModifierProvider {
   public DALootDataProvider(PackOutput output, CompletableFuture<Provider> registries) {
      super(output, registries, "deep_aether");
   }

   protected void start() {
      this.add(
         "silver_loot_modifiers",
         new DAAddDungeonLootModifier(
            new LootItemCondition[]{LootTableIdCondition.builder(AetherLoot.SILVER_DUNGEON.location()).build()},
            List.of(
               WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.SKYJADE.get(), 1), 90),
               WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.SKYJADE.get(), 2), 10)
            ),
            100,
            0.0F
         ),
         new ICondition[0]
      );
      this.add(
         "bronze_loot_reward_modifiers",
         new DAAddDungeonLootModifier(
            new LootItemCondition[]{LootTableIdCondition.builder(AetherLoot.BRONZE_DUNGEON_REWARD.location()).build()},
            List.of(WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.MUSIC_DISC_ATTA.get(), 1), 40)),
            100,
            0.0F
         ),
         new ICondition[0]
      );
      this.add(
         "silver_loot_reward_modifiers",
         new DAAddDungeonLootModifier(
            new LootItemCondition[]{LootTableIdCondition.builder(AetherLoot.SILVER_DUNGEON_REWARD.location()).build()},
            List.of(
               WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.SKYJADE.get(), 2), 45),
               WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.STRATUS_INGOT.get(), 1), 10),
               WeightedEntry.wrap(new ItemStack((ItemLike)DABlocks.STERLING_AERCLOUD.get(), 1), 15),
               WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.STRATUS_SMITHING_TEMPLATE.get(), 1), 25),
               WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.MUSIC_DISC_FAENT.get(), 1), 40)
            ),
            100,
            0.0F
         ),
         new ICondition[0]
      );
      this.add(
         "gold_loot_modifiers",
         new DAAddDungeonLootModifier(
            new LootItemCondition[]{LootTableIdCondition.builder(AetherLoot.GOLD_DUNGEON_REWARD.location()).build()},
            List.of(
               WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.SKYJADE.get(), 2), 50),
               WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.SKYJADE.get(), 5), 20),
               WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.STRATUS_INGOT.get(), 1), 10),
               WeightedEntry.wrap(new ItemStack((ItemLike)DABlocks.STERLING_AERCLOUD.get(), 2), 7),
               WeightedEntry.wrap(new ItemStack((ItemLike)DABlocks.STERLING_AERCLOUD.get(), 1), 20),
               WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.MUSIC_DISC_HIMININN.get(), 1), 40)
            ),
            100,
            0.25F
         ),
         new ICondition[0]
      );
      this.add(
         "gold_loot_stratus_upgrade",
         new DAAddDungeonLootModifier(
            new LootItemCondition[]{LootTableIdCondition.builder(AetherLoot.GOLD_DUNGEON_REWARD.location()).build()},
            List.of(WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.STRATUS_SMITHING_TEMPLATE.get(), 1), 100)),
            100,
            0.0F
         ),
         new ICondition[0]
      );
      this.add(
         "fish_aether",
         new DAFishingLootModifier(
            new LootItemCondition[]{LootTableIdCondition.builder(BuiltInLootTables.FISHING.location()).build()},
            List.of(
               WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.RAW_AERGLOW_FISH.get(), 1), 100),
               WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.AERGLOW_BLOSSOM.get(), 1), 12),
               WeightedEntry.wrap(new ItemStack((ItemLike)AetherItems.SKYROOT_STICK.get(), 1), 12),
               WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.MUSIC_DISC_ABOVE_THE_RAIN.get(), 1), 11)
            ),
            135,
            0.75F
         ),
         new ICondition[0]
      );
      this.add(
         "stormforged_upgrade_treasure",
         new AddDungeonLootModifier(
            new LootItemCondition[]{LootTableIdCondition.builder(DALoot.BRASS_DUNGEON_REWARD.location()).build()},
            List.of(WeightedEntry.wrap(new ItemStack((ItemLike)DAItems.STORMFORGED_SMITHING_TEMPLATE.get()), 2)),
            UniformInt.of(1, 1)
         ),
         new ICondition[]{new ModLoadedCondition("aether_treasure_reforging")}
      );
      this.add(
         "halloween_loot_modifiers",
         new DAHalloweenLootModifier(
            new LootItemCondition[]{LootItemKilledByPlayerCondition.killedByPlayer().build()},
            List.of(WeightedEntry.wrap(new ItemStack((ItemLike)AetherItems.CANDY_CANE.get()), 100)),
            100,
            0.5F
         ),
         new ICondition[0]
      );
   }
}
