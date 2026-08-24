package net.cibernet.alchemancy.registries;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AlchemancyPoiTypes {
   public static final DeferredRegister<PoiType> REGISTRY = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, "alchemancy");
   public static final DeferredHolder<PoiType, PoiType> TICKING_BLOCK = REGISTRY.register(
      "ticking_block",
      () -> new PoiType(
         Streams.concat(
               new Stream[]{
                  getBlockStates((Block)AlchemancyBlocks.GUST_BASKET.get()).stream(), getBlockStates((Block)AlchemancyBlocks.FLAT_HOPPER.get()).stream()
               }
            )
            .collect(Collectors.toSet()),
         0,
         1
      )
   );
   public static final DeferredHolder<PoiType, PoiType> ROOTED_ITEM = REGISTRY.register(
      "rooted_item", () -> new PoiType(getBlockStates((Block)AlchemancyBlocks.ROOTED_ITEM.get()), 0, 1)
   );

   private static Set<BlockState> getBlockStates(Block block) {
      return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
   }
}
