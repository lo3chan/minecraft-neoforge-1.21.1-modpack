package com.yungnickyoung.minecraft.betterstrongholds.world;

import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.ItemRandomizer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemFrameChances {
   public static ItemFrameChances instance;
   private ItemRandomizer armouryItems = new ItemRandomizer(Items.AIR)
      .addItem(Items.STONE_SWORD, 0.05F)
      .addItem(Items.IRON_SWORD, 0.1F)
      .addItem(Items.GOLDEN_SWORD, 0.05F)
      .addItem(Items.STONE_AXE, 0.05F)
      .addItem(Items.IRON_AXE, 0.1F)
      .addItem(Items.GOLDEN_AXE, 0.05F)
      .addItem(Items.SHIELD, 0.1F)
      .addItem(Items.BOW, 0.1F)
      .addItem(Items.ARROW, 0.05F)
      .addItem(Items.NAME_TAG, 0.05F);
   private ItemRandomizer storageItems = new ItemRandomizer(Items.AIR)
      .addItem(Items.PAPER, 0.25F)
      .addItem(Items.MAP, 0.25F)
      .addItem(Items.FLINT, 0.05F)
      .addItem(Items.COMPASS, 0.05F)
      .addItem(Items.LEAD, 0.05F)
      .addItem(Items.CAKE, 0.05F)
      .addItem(Items.SLIME_BALL, 0.05F)
      .addItem(Items.BEETROOT_SEEDS, 0.025F)
      .addItem(Items.WHEAT_SEEDS, 0.025F)
      .addItem(Items.MELON_SEEDS, 0.025F)
      .addItem(Items.PUMPKIN_SEEDS, 0.025F)
      .addItem(Items.RABBIT_FOOT, 0.01F);

   public static ItemFrameChances get() {
      if (instance == null) {
         instance = new ItemFrameChances();
      }

      return instance;
   }

   private ItemFrameChances() {
   }

   public Item getArmouryItem(RandomSource randomSource) {
      return this.armouryItems.get(randomSource);
   }

   public Item getStorageItem(RandomSource randomSource) {
      return this.storageItems.get(randomSource);
   }
}
