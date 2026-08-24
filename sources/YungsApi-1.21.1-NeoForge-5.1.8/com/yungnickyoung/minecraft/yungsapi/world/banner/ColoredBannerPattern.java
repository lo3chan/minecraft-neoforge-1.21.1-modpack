package com.yungnickyoung.minecraft.yungsapi.world.banner;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;

public class ColoredBannerPattern {
   private ResourceKey<BannerPattern> pattern;
   private DyeColor color;

   public ColoredBannerPattern(ResourceKey<BannerPattern> pattern, DyeColor color) {
      this.pattern = pattern;
      this.color = color;
   }

   public ResourceKey<BannerPattern> getPattern() {
      return this.pattern;
   }

   public void setPattern(ResourceKey<BannerPattern> pattern) {
      this.pattern = pattern;
   }

   public DyeColor getColor() {
      return this.color;
   }

   public void setColor(DyeColor color) {
      this.color = color;
   }
}
