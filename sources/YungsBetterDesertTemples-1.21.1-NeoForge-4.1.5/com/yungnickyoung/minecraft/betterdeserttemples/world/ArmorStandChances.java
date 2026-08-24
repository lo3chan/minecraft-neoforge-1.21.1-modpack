package com.yungnickyoung.minecraft.betterdeserttemples.world;

import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.ItemRandomizer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ArmorStandChances {
   public static ArmorStandChances instance;
   private ItemRandomizer armoryHelmets = new ItemRandomizer(Items.AIR).addItem(Items.CHAINMAIL_HELMET, 0.3F).addItem(Items.GOLDEN_HELMET, 0.2F);
   private ItemRandomizer wardrobeHelmets;
   private ItemRandomizer armoryChestplates = new ItemRandomizer(Items.AIR).addItem(Items.CHAINMAIL_CHESTPLATE, 0.3F).addItem(Items.GOLDEN_CHESTPLATE, 0.2F);
   private ItemRandomizer wardrobeChestplates;
   private ItemRandomizer armoryLeggings = new ItemRandomizer(Items.AIR).addItem(Items.CHAINMAIL_LEGGINGS, 0.3F).addItem(Items.GOLDEN_LEGGINGS, 0.2F);
   private ItemRandomizer wardrobeLeggings;
   private ItemRandomizer armoryBoots = new ItemRandomizer(Items.AIR).addItem(Items.CHAINMAIL_BOOTS, 0.3F).addItem(Items.GOLDEN_BOOTS, 0.2F);
   private ItemRandomizer wardrobeBoots;

   public static ArmorStandChances get() {
      if (instance == null) {
         instance = new ArmorStandChances();
      }

      return instance;
   }

   private ArmorStandChances() {
      this.wardrobeHelmets = new ItemRandomizer(Items.AIR).addItem(Items.CHAINMAIL_HELMET, 0.2F).addItem(Items.LEATHER_HELMET, 0.4F);
      this.wardrobeChestplates = new ItemRandomizer(Items.AIR).addItem(Items.CHAINMAIL_CHESTPLATE, 0.2F).addItem(Items.LEATHER_CHESTPLATE, 0.4F);
      this.wardrobeLeggings = new ItemRandomizer(Items.AIR).addItem(Items.CHAINMAIL_LEGGINGS, 0.2F).addItem(Items.LEATHER_LEGGINGS, 0.4F);
      this.wardrobeBoots = new ItemRandomizer(Items.AIR).addItem(Items.CHAINMAIL_BOOTS, 0.2F).addItem(Items.LEATHER_BOOTS, 0.4F);
   }

   public Item getArmoryHelmet(RandomSource randomSource) {
      return this.armoryHelmets.get(randomSource);
   }

   public Item getWardrobeHelmet(RandomSource randomSource) {
      return this.wardrobeHelmets.get(randomSource);
   }

   public Item getArmoryChestplate(RandomSource randomSource) {
      return this.armoryChestplates.get(randomSource);
   }

   public Item getWardrobeChestplate(RandomSource randomSource) {
      return this.wardrobeChestplates.get(randomSource);
   }

   public Item getArmoryLeggings(RandomSource randomSource) {
      return this.armoryLeggings.get(randomSource);
   }

   public Item getWardrobeLeggings(RandomSource randomSource) {
      return this.wardrobeLeggings.get(randomSource);
   }

   public Item getArmoryBoots(RandomSource randomSource) {
      return this.armoryBoots.get(randomSource);
   }

   public Item getWardrobeBoots(RandomSource randomSource) {
      return this.wardrobeBoots.get(randomSource);
   }
}
