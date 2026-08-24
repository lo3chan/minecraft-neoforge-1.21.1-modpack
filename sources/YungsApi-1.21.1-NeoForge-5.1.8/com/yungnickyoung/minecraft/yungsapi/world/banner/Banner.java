package com.yungnickyoung.minecraft.yungsapi.world.banner;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class Banner {
   private List<ColoredBannerPattern> patterns;
   private BlockState state;
   private CompoundTag nbt;
   private boolean isWallBanner;

   public Banner(List<ColoredBannerPattern> patterns, BlockState state, CompoundTag nbt) {
      this.patterns = patterns;
      this.state = state;
      this.nbt = nbt;
      this.isWallBanner = this.state.getBlock() instanceof WallBannerBlock;
   }

   public Banner(List<ColoredBannerPattern> patterns, BlockState state, CompoundTag nbt, boolean isWallBanner) {
      this.patterns = patterns;
      this.state = state;
      this.nbt = nbt;
      this.isWallBanner = isWallBanner;
   }

   public List<ColoredBannerPattern> getPatterns() {
      return this.patterns;
   }

   public void setPatterns(List<ColoredBannerPattern> patterns) {
      this.patterns = patterns;
   }

   public BlockState getState() {
      return this.state;
   }

   public void setState(BlockState state) {
      this.state = state;
   }

   public CompoundTag getNbt() {
      return this.nbt;
   }

   public void setNbt(CompoundTag nbt) {
      this.nbt = nbt;
   }

   public boolean isWallBanner() {
      return this.isWallBanner;
   }

   public void setWallBanner(boolean wallBanner) {
      this.isWallBanner = wallBanner;
   }

   public static class Builder {
      private final List<ColoredBannerPattern> patterns = new ArrayList<>();
      private String customNameTranslate;
      private String customNameFallback;
      private TextColor customColor;
      @Nullable
      private Boolean showPatternsInTooltip = null;
      @Nullable
      private Rarity rarity = null;
      private BlockState state = Blocks.BLACK_WALL_BANNER.defaultBlockState();

      public Banner.Builder blockState(BlockState state) {
         this.state = state;
         return this;
      }

      public Banner.Builder pattern(ColoredBannerPattern pattern) {
         this.patterns.add(pattern);
         return this;
      }

      public Banner.Builder pattern(ResourceKey<BannerPattern> pattern, DyeColor color) {
         this.patterns.add(new ColoredBannerPattern(pattern, color));
         return this;
      }

      public Banner.Builder customName(String translatableNamePath) {
         return this.customName(translatableNamePath, null);
      }

      public Banner.Builder customName(String translatableNamePath, String fallback) {
         this.customNameTranslate = translatableNamePath;
         this.customNameFallback = fallback;
         if (this.showPatternsInTooltip == null) {
            this.showPatternsInTooltip = false;
         }

         if (this.rarity == null) {
            this.rarity = Rarity.UNCOMMON;
         }

         return this;
      }

      public Banner.Builder showPatternsInTooltip() {
         this.showPatternsInTooltip = true;
         return this;
      }

      public Banner.Builder customColor(String colorString) {
         this.customColor = (TextColor)TextColor.parseColor(colorString).getOrThrow();
         return this;
      }

      public Banner.Builder customColor(TextColor textColor) {
         this.customColor = textColor;
         return this;
      }

      public Banner.Builder rarity(Rarity rarity) {
         this.rarity = rarity;
         return this;
      }

      public Banner build() {
         CompoundTag nbt = this.createBannerNBT();
         return new Banner(this.patterns, this.state, nbt);
      }

      private CompoundTag createBannerNBT() {
         CompoundTag nbt = new CompoundTag();
         ListTag patternList = new ListTag();
         this.patterns.forEach(pattern -> {
            CompoundTag patternNBT = new CompoundTag();
            patternNBT.putString("pattern", pattern.getPattern().location().toString());
            patternNBT.putString("color", pattern.getColor().getName());
            patternList.add(patternNBT);
         });
         net.minecraft.core.component.DataComponentMap.Builder components = DataComponentMap.builder();
         if (this.customNameTranslate != null) {
            components.set(
               DataComponents.ITEM_NAME,
               Component.translatableWithFallback(this.customNameTranslate, this.customNameFallback)
                  .withStyle(s -> this.customColor == null ? s : s.withColor(this.customColor))
            );
         }

         if (this.showPatternsInTooltip != null && !this.showPatternsInTooltip) {
            components.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
         }

         if (this.rarity != null) {
            components.set(DataComponents.RARITY, this.rarity);
         }

         nbt.put("components", (Tag)DataComponentMap.CODEC.encodeStart(NbtOps.INSTANCE, components.build()).result().orElseThrow());
         nbt.put("patterns", patternList);
         nbt.putString("id", "minecraft:banner");
         return nbt;
      }
   }
}
