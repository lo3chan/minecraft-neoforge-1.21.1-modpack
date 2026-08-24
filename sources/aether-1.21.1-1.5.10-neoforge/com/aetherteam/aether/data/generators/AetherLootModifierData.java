package com.aetherteam.aether.data.generators;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.loot.AetherLoot;
import com.aetherteam.aether.loot.modifiers.DoubleDropsModifier;
import com.aetherteam.aether.loot.modifiers.EnchantedGrassModifier;
import com.aetherteam.aether.loot.modifiers.GlovesLootModifier;
import com.aetherteam.aether.loot.modifiers.PigDropsModifier;
import com.aetherteam.aether.loot.modifiers.RemoveSeedsModifier;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

public class AetherLootModifierData extends GlobalLootModifierProvider {
   public AetherLootModifierData(PackOutput output, CompletableFuture<Provider> completableFuture) {
      super(output, completableFuture, "aether");
   }

   protected void start() {
      this.add(
         "remove_seeds",
         new RemoveSeedsModifier(
            new LootItemCondition[]{
               AnyOfCondition.anyOf(
                     new Builder[]{
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SHORT_GRASS),
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TALL_GRASS)
                     }
                  )
                  .build(),
               MatchTool.toolMatches(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(new ItemLike[]{Items.SHEARS})).invert().build()
            }
         ),
         new ICondition[0]
      );
      this.add(
         "enchanted_grass_berry_bush",
         new EnchantedGrassModifier(
            new LootItemCondition[]{LootItemBlockStatePropertyCondition.hasBlockStateProperties((Block)AetherBlocks.BERRY_BUSH.get()).build()},
            new ItemStack((ItemLike)AetherItems.BLUE_BERRY.get())
         ),
         new ICondition[0]
      );
      this.add("double_drops", new DoubleDropsModifier(new LootItemCondition[0]), new ICondition[0]);
      this.add("pig_drops", new PigDropsModifier(new LootItemCondition[0]), new ICondition[0]);
      this.add(
         "gloves_loot_leather",
         new GlovesLootModifier(new LootItemCondition[0], new ItemStack((ItemLike)AetherItems.LEATHER_GLOVES.get()), ArmorMaterials.LEATHER),
         new ICondition[0]
      );
      this.add(
         "gloves_loot_chain",
         new GlovesLootModifier(
            new LootItemCondition[]{InvertedLootItemCondition.invert(LootTableIdCondition.builder(AetherLoot.GOLD_DUNGEON_REWARD.location())).build()},
            new ItemStack((ItemLike)AetherItems.CHAINMAIL_GLOVES.get()),
            ArmorMaterials.CHAIN
         ),
         new ICondition[0]
      );
      this.add(
         "gloves_loot_iron",
         new GlovesLootModifier(
            new LootItemCondition[]{InvertedLootItemCondition.invert(LootTableIdCondition.builder(AetherLoot.RUINED_PORTAL.location())).build()},
            new ItemStack((ItemLike)AetherItems.IRON_GLOVES.get()),
            ArmorMaterials.IRON
         ),
         new ICondition[0]
      );
      this.add(
         "gloves_loot_gold",
         new GlovesLootModifier(new LootItemCondition[0], new ItemStack((ItemLike)AetherItems.GOLDEN_GLOVES.get()), ArmorMaterials.GOLD),
         new ICondition[0]
      );
      this.add(
         "gloves_loot_diamond",
         new GlovesLootModifier(new LootItemCondition[0], new ItemStack((ItemLike)AetherItems.DIAMOND_GLOVES.get()), ArmorMaterials.DIAMOND),
         new ICondition[0]
      );
      this.add(
         "gloves_loot_netherite",
         new GlovesLootModifier(new LootItemCondition[0], new ItemStack((ItemLike)AetherItems.NETHERITE_GLOVES.get()), ArmorMaterials.NETHERITE),
         new ICondition[0]
      );
   }
}
