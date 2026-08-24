package com.yungnickyoung.minecraft.betterstrongholds.world;

import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.ItemRandomizer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ArmorStandChances {
   public static ArmorStandChances instance;
   private ItemRandomizer commonHelmets = new ItemRandomizer(Items.AIR)
      .addItem(Items.CHAINMAIL_HELMET, 0.3F)
      .addItem(Items.LEATHER_HELMET, 0.1F)
      .addItem(Items.IRON_HELMET, 0.3F)
      .addItem(Items.CARVED_PUMPKIN, 0.01F);
   private ItemRandomizer rareHelmets = new ItemRandomizer(Items.AIR).addItem(Items.DIAMOND_HELMET, 0.3F).addItem(Items.CARVED_PUMPKIN, 0.2F);
   private ItemRandomizer commonChestplates = new ItemRandomizer(Items.AIR)
      .addItem(Items.CHAINMAIL_CHESTPLATE, 0.3F)
      .addItem(Items.LEATHER_CHESTPLATE, 0.1F)
      .addItem(Items.IRON_CHESTPLATE, 0.3F);
   private ItemRandomizer rareChestplates = new ItemRandomizer(Items.AIR).addItem(Items.DIAMOND_CHESTPLATE, 0.3F);
   private ItemRandomizer commonLeggings = new ItemRandomizer(Items.AIR)
      .addItem(Items.CHAINMAIL_LEGGINGS, 0.3F)
      .addItem(Items.LEATHER_LEGGINGS, 0.1F)
      .addItem(Items.IRON_LEGGINGS, 0.3F);
   private ItemRandomizer rareLeggings = new ItemRandomizer(Items.AIR).addItem(Items.DIAMOND_LEGGINGS, 0.3F);
   private ItemRandomizer commonBoots = new ItemRandomizer(Items.AIR)
      .addItem(Items.CHAINMAIL_BOOTS, 0.3F)
      .addItem(Items.LEATHER_BOOTS, 0.1F)
      .addItem(Items.IRON_BOOTS, 0.3F);
   private ItemRandomizer rareBoots = new ItemRandomizer(Items.AIR).addItem(Items.DIAMOND_BOOTS, 0.3F);

   public static ArmorStandChances get() {
      if (instance == null) {
         instance = new ArmorStandChances();
      }

      return instance;
   }

   private ArmorStandChances() {
   }

   public Item getCommonHelmet(RandomSource randomSource) {
      return this.commonHelmets.get(randomSource);
   }

   public Item getRareHelmet(RandomSource randomSource) {
      return this.rareHelmets.get(randomSource);
   }

   public Item getCommonChestplate(RandomSource randomSource) {
      return this.commonChestplates.get(randomSource);
   }

   public Item getRareChestplate(RandomSource randomSource) {
      return this.rareChestplates.get(randomSource);
   }

   public Item getCommonLeggings(RandomSource randomSource) {
      return this.commonLeggings.get(randomSource);
   }

   public Item getRareLeggings(RandomSource randomSource) {
      return this.rareLeggings.get(randomSource);
   }

   public Item getCommonBoots(RandomSource randomSource) {
      return this.commonBoots.get(randomSource);
   }

   public Item getRareBoots(RandomSource randomSource) {
      return this.rareBoots.get(randomSource);
   }
}
