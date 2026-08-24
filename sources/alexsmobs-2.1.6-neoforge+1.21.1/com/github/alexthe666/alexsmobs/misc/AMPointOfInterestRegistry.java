package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMPointOfInterestRegistry {
   public static final DeferredRegister<PoiType> DEF_REG = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, "alexsmobs");
   public static final Supplier<PoiType> END_PORTAL_FRAME = DEF_REG.register(
      "end_portal_frame", () -> new PoiType(getBlockStates(Blocks.END_PORTAL_FRAME), 32, 6)
   );
   public static final Supplier<PoiType> LEAFCUTTER_ANT_HILL = DEF_REG.register(
      "leafcutter_anthill", () -> new PoiType(getBlockStates(AMBlockRegistry.LEAFCUTTER_ANTHILL.get()), 32, 6)
   );
   public static final Supplier<PoiType> BEACON = DEF_REG.register("am_beacon", () -> new PoiType(getBlockStates(Blocks.BEACON), 32, 6));
   public static final Supplier<PoiType> HUMMINGBIRD_FEEDER = DEF_REG.register(
      "hummingbird_feeder", () -> new PoiType(getBlockStates(AMBlockRegistry.HUMMINGBIRD_FEEDER.get()), 32, 6)
   );
   public static final ResourceKey<PoiType> END_PORTAL_FRAME_KEY = key("end_portal_frame");
   public static final ResourceKey<PoiType> LEAFCUTTER_ANT_HILL_KEY = key("leafcutter_anthill");
   public static final ResourceKey<PoiType> BEACON_KEY = key("am_beacon");
   public static final ResourceKey<PoiType> HUMMINGBIRD_FEEDER_KEY = key("hummingbird_feeder");

   private static ResourceKey<PoiType> key(String name) {
      return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, AMCompat.rl("alexsmobs", name));
   }

   private static Set<BlockState> getBlockStates(Block block) {
      return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
   }
}
