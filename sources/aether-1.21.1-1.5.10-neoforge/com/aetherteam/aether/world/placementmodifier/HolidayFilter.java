package com.aetherteam.aether.world.placementmodifier;

import com.aetherteam.aether.AetherConfig;
import com.mojang.serialization.MapCodec;
import java.util.Calendar;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class HolidayFilter extends PlacementFilter {
   public static final MapCodec<HolidayFilter> CODEC = MapCodec.unit(HolidayFilter::new);

   protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
      Calendar calendar = Calendar.getInstance();
      boolean isChristmas = calendar.get(2) == 11 || calendar.get(2) == 0;
      return (Boolean)AetherConfig.SERVER.generate_holiday_tree_always.get()
         || (Boolean)AetherConfig.SERVER.generate_holiday_tree_seasonally.get() && isChristmas;
   }

   public PlacementModifierType<?> type() {
      return (PlacementModifierType<?>)AetherPlacementModifiers.HOLIDAY_FILTER.get();
   }
}
