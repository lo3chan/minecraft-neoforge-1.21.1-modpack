package com.yungnickyoung.minecraft.betterfortresses.world;

import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.ItemRandomizer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemFrameChances {
   public static ItemFrameChances instance;
   private ItemRandomizer weaponItems = new ItemRandomizer(Items.AIR)
      .addItem(Items.STONE_SWORD, 0.025F)
      .addItem(Items.IRON_SWORD, 0.025F)
      .addItem(Items.GOLDEN_SWORD, 0.05F)
      .addItem(Items.NETHERITE_SWORD, 0.005F)
      .addItem(Items.STONE_AXE, 0.025F)
      .addItem(Items.IRON_AXE, 0.025F)
      .addItem(Items.GOLDEN_AXE, 0.05F)
      .addItem(Items.SHIELD, 0.025F);
   private ItemRandomizer lootItems = new ItemRandomizer(Items.AIR)
      .addItem(Items.GOLD_NUGGET, 0.2F)
      .addItem(Items.GOLD_INGOT, 0.1F)
      .addItem(Items.NETHER_WART, 0.1F);
   private ItemRandomizer studyItems = new ItemRandomizer(Items.AIR)
      .addItem(Items.BOOK, 0.4F)
      .addItem(Items.PAPER, 0.1F)
      .addItem(Items.WRITABLE_BOOK, 0.1F)
      .addItem(Items.ENCHANTED_BOOK, 0.1F);
   private ItemRandomizer messHallItems = new ItemRandomizer(Items.AIR)
      .addItem(Items.PORKCHOP, 0.3F)
      .addItem(Items.COOKED_PORKCHOP, 0.3F)
      .addItem(Items.GOLD_INGOT, 0.2F);
   private ItemRandomizer alchemyItems = new ItemRandomizer(Items.AIR)
      .addItem(Items.QUARTZ, 0.3F)
      .addItem(Items.MAGMA_CREAM, 0.3F)
      .addItem(Items.FIRE_CHARGE, 0.2F);

   public static ItemFrameChances get() {
      if (instance == null) {
         instance = new ItemFrameChances();
      }

      return instance;
   }

   private ItemFrameChances() {
   }

   public Item getWeaponItem(RandomSource randomSource) {
      return this.weaponItems.get(randomSource);
   }

   public Item getLootItem(RandomSource randomSource) {
      return this.lootItems.get(randomSource);
   }

   public Item getStudyItem(RandomSource randomSource) {
      return this.studyItems.get(randomSource);
   }

   public Item getMessHallItem(RandomSource randomSource) {
      return this.messHallItems.get(randomSource);
   }

   public Item getAlchemyItem(RandomSource randomSource) {
      return this.alchemyItems.get(randomSource);
   }
}
