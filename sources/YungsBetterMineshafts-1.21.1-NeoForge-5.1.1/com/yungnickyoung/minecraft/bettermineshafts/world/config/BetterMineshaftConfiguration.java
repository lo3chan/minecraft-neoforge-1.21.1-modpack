package com.yungnickyoung.minecraft.bettermineshafts.world.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;

public class BetterMineshaftConfiguration {
   public static final Codec<BetterMineshaftConfiguration> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf("replacementRate").forGetter(config -> config.replacementRate),
            BetterMineshaftConfiguration.LegVariant.CODEC.fieldOf("legVariant").forGetter(config -> config.legVariant),
            BetterMineshaftConfiguration.MineshaftDecorationChances.CODEC.fieldOf("decorationChances").forGetter(config -> config.decorationChances),
            BetterMineshaftConfiguration.MineshaftBlockStates.CODEC.fieldOf("blockStates").forGetter(config -> config.blockStates),
            BetterMineshaftConfiguration.MineshaftBlockstateRandomizers.CODEC
               .fieldOf("blockStateRandomizers")
               .forGetter(config -> config.blockStateRandomizers)
         )
         .apply(instance, BetterMineshaftConfiguration::new)
   );
   public float replacementRate;
   public BetterMineshaftConfiguration.LegVariant legVariant;
   public BetterMineshaftConfiguration.MineshaftDecorationChances decorationChances;
   public BetterMineshaftConfiguration.MineshaftBlockStates blockStates;
   public BetterMineshaftConfiguration.MineshaftBlockstateRandomizers blockStateRandomizers;

   public BetterMineshaftConfiguration(
      float replacementRate,
      BetterMineshaftConfiguration.LegVariant legVariant,
      BetterMineshaftConfiguration.MineshaftDecorationChances decorationChances,
      BetterMineshaftConfiguration.MineshaftBlockStates blockStates,
      BetterMineshaftConfiguration.MineshaftBlockstateRandomizers blockStateRandomizers
   ) {
      this.replacementRate = replacementRate;
      this.legVariant = legVariant;
      this.decorationChances = decorationChances;
      this.blockStates = blockStates;
      this.blockStateRandomizers = blockStateRandomizers;
   }

   public static enum LegVariant implements StringRepresentable {
      EDGE("edge"),
      INNER("inner");

      public static final Codec<BetterMineshaftConfiguration.LegVariant> CODEC = StringRepresentable.fromEnum(BetterMineshaftConfiguration.LegVariant::values);
      private static final Map<String, BetterMineshaftConfiguration.LegVariant> BY_NAME = Arrays.stream(values())
         .collect(Collectors.toMap(BetterMineshaftConfiguration.LegVariant::getName, variant -> (BetterMineshaftConfiguration.LegVariant)variant));
      private final String name;

      private LegVariant(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      private static BetterMineshaftConfiguration.LegVariant byName(String name) {
         return BY_NAME.get(name);
      }

      public static BetterMineshaftConfiguration.LegVariant byId(int id) {
         return id >= 0 && id < values().length ? values()[id] : EDGE;
      }

      public String getSerializedName() {
         return this.name;
      }
   }

   public static class MineshaftBlockStates {
      public static final Codec<BetterMineshaftConfiguration.MineshaftBlockStates> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               BlockState.CODEC.fieldOf("mainBlockState").forGetter(config -> config.mainBlockState),
               BlockState.CODEC.fieldOf("supportBlockState").forGetter(config -> config.supportBlockState),
               BlockState.CODEC.fieldOf("slabBlockState").forGetter(config -> config.slabBlockState),
               BlockState.CODEC.fieldOf("gravelBlockState").forGetter(config -> config.gravelBlockState),
               BlockState.CODEC.fieldOf("stoneWallBlockState").forGetter(config -> config.stoneWallBlockState),
               BlockState.CODEC.fieldOf("stoneSlabBlockState").forGetter(config -> config.stoneSlabBlockState),
               BlockState.CODEC.fieldOf("trapdoorBlockState").forGetter(config -> config.trapdoorBlockState),
               BlockState.CODEC.fieldOf("smallLegBlockState").forGetter(config -> config.smallLegBlockState)
            )
            .apply(instance, BetterMineshaftConfiguration.MineshaftBlockStates::new)
      );
      public BlockState mainBlockState;
      public BlockState supportBlockState;
      public BlockState slabBlockState;
      public BlockState gravelBlockState;
      public BlockState stoneWallBlockState;
      public BlockState stoneSlabBlockState;
      public BlockState trapdoorBlockState;
      public BlockState smallLegBlockState;

      public MineshaftBlockStates(
         BlockState mainBlockState,
         BlockState supportBlockState,
         BlockState slabBlockState,
         BlockState gravelBlockState,
         BlockState stoneWallBlockState,
         BlockState stoneSlabBlockState,
         BlockState trapdoorBlockState,
         BlockState smallLegBlockState
      ) {
         this.mainBlockState = mainBlockState;
         this.supportBlockState = supportBlockState;
         this.slabBlockState = slabBlockState;
         this.gravelBlockState = gravelBlockState;
         this.stoneWallBlockState = stoneWallBlockState;
         this.stoneSlabBlockState = stoneSlabBlockState;
         this.trapdoorBlockState = trapdoorBlockState;
         this.smallLegBlockState = smallLegBlockState;
      }
   }

   public static class MineshaftBlockstateRandomizers {
      public static final Codec<BetterMineshaftConfiguration.MineshaftBlockstateRandomizers> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               BlockStateRandomizer.CODEC.fieldOf("mainRandomizer").forGetter(config -> config.mainRandomizer),
               BlockStateRandomizer.CODEC.fieldOf("floorRandomizer").forGetter(config -> config.floorRandomizer),
               BlockStateRandomizer.CODEC.fieldOf("brickRandomizer").forGetter(config -> config.brickRandomizer),
               BlockStateRandomizer.CODEC.fieldOf("legRandomizer").forGetter(config -> config.legRandomizer)
            )
            .apply(instance, BetterMineshaftConfiguration.MineshaftBlockstateRandomizers::new)
      );
      public BlockStateRandomizer mainRandomizer;
      public BlockStateRandomizer floorRandomizer;
      public BlockStateRandomizer brickRandomizer;
      public BlockStateRandomizer legRandomizer;

      public MineshaftBlockstateRandomizers(
         BlockStateRandomizer mainRandomizer, BlockStateRandomizer floorRandomizer, BlockStateRandomizer brickRandomizer, BlockStateRandomizer legRandomizer
      ) {
         this.mainRandomizer = mainRandomizer;
         this.floorRandomizer = floorRandomizer;
         this.brickRandomizer = brickRandomizer;
         this.legRandomizer = legRandomizer;
      }
   }

   public static class MineshaftDecorationChances {
      public static final Codec<BetterMineshaftConfiguration.MineshaftDecorationChances> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               Codec.floatRange(0.0F, 1.0F).fieldOf("vineChance").forGetter(config -> config.vineChance),
               Codec.floatRange(0.0F, 1.0F).fieldOf("snowChance").forGetter(config -> config.snowChance),
               Codec.floatRange(0.0F, 1.0F).fieldOf("cactusChance").forGetter(config -> config.cactusChance),
               Codec.floatRange(0.0F, 1.0F).fieldOf("deadBushChance").forGetter(config -> config.deadBushChance),
               Codec.floatRange(0.0F, 1.0F).fieldOf("mushroomChance").forGetter(config -> config.mushroomChance),
               Codec.floatRange(0.0F, 1.0F).fieldOf("gravelPileChance").forGetter(config -> config.gravelPileChance),
               Codec.BOOL.fieldOf("lushDecorations").forGetter(config -> config.lushDecorations),
               Codec.BOOL.fieldOf("dripstoneDecorations").forGetter(config -> config.dripstoneDecorations)
            )
            .apply(instance, BetterMineshaftConfiguration.MineshaftDecorationChances::new)
      );
      public float vineChance;
      public float snowChance;
      public float cactusChance;
      public float deadBushChance;
      public float mushroomChance;
      public float gravelPileChance;
      public boolean lushDecorations;
      public boolean dripstoneDecorations;

      public MineshaftDecorationChances(
         float vineChance,
         float snowChance,
         float cactusChance,
         float deadBushChance,
         float mushroomChance,
         float gravelPileChance,
         boolean lushDecorations,
         boolean dripstoneDecorations
      ) {
         this.vineChance = vineChance;
         this.snowChance = snowChance;
         this.cactusChance = cactusChance;
         this.deadBushChance = deadBushChance;
         this.mushroomChance = mushroomChance;
         this.gravelPileChance = gravelPileChance;
         this.lushDecorations = lushDecorations;
         this.dripstoneDecorations = dripstoneDecorations;
      }
   }
}
