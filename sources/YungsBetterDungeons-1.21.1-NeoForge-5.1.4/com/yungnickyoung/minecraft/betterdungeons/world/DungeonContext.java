package com.yungnickyoung.minecraft.betterdungeons.world;

import java.lang.ref.WeakReference;

public class DungeonContext {
   private static final ThreadLocal<DungeonContext> CONTEXT = new ThreadLocal<>();
   private WeakReference<Integer> bannerCount = new WeakReference<>(0);
   private WeakReference<Integer> chestCount = new WeakReference<>(0);

   public int getBannerCount() {
      Integer value = this.bannerCount.get();
      return value == null ? 0 : value;
   }

   public int getChestCount() {
      Integer value = this.chestCount.get();
      return value == null ? 0 : value;
   }

   public void incrementBannerCount() {
      Integer boxedVal = this.bannerCount.get();
      int val = boxedVal == null ? 0 : boxedVal;
      this.bannerCount.clear();
      this.bannerCount = new WeakReference<>(val + 1);
   }

   public void incrementChestCount() {
      Integer boxedVal = this.chestCount.get();
      int val = boxedVal == null ? 0 : boxedVal;
      this.chestCount.clear();
      this.chestCount = new WeakReference<>(val + 1);
   }

   public static DungeonContext pop() {
      DungeonContext context = CONTEXT.get();
      CONTEXT.set(null);
      return context;
   }

   public static DungeonContext peek() {
      return CONTEXT.get();
   }

   public static void initialize() {
      CONTEXT.set(new DungeonContext());
   }
}
