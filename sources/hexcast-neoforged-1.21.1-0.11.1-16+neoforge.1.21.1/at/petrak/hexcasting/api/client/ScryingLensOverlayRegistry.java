package at.petrak.hexcasting.api.client;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public final class ScryingLensOverlayRegistry {
   private static final ConcurrentMap<ResourceLocation, ScryingLensOverlayRegistry.OverlayBuilder> ID_LOOKUP = new ConcurrentHashMap<>();
   private static final List<Pair<ScryingLensOverlayRegistry.OverlayPredicate, ScryingLensOverlayRegistry.OverlayBuilder>> PREDICATE_LOOKUP = new Vector<>();

   public static void addDisplayer(Block block, ScryingLensOverlayRegistry.OverlayBuilder displayer) {
      addDisplayer(BuiltInRegistries.BLOCK.getKey(block), displayer);
   }

   public static void addDisplayer(ResourceLocation blockID, ScryingLensOverlayRegistry.OverlayBuilder displayer) {
      if (ID_LOOKUP.containsKey(blockID)) {
         throw new IllegalArgumentException("Already have a displayer for " + blockID);
      } else {
         ID_LOOKUP.put(blockID, displayer);
      }
   }

   public static void addPredicateDisplayer(ScryingLensOverlayRegistry.OverlayPredicate predicate, ScryingLensOverlayRegistry.OverlayBuilder displayer) {
      PREDICATE_LOOKUP.add(new Pair(predicate, displayer));
   }

   @NotNull
   public static List<Pair<ItemStack, Component>> getLines(BlockState state, BlockPos pos, Player observer, Level world, Direction hitFace) {
      List<Pair<ItemStack, Component>> lines = Lists.newArrayList();
      ScryingLensOverlayRegistry.OverlayBuilder idLookedup = ID_LOOKUP.get(BuiltInRegistries.BLOCK.getKey(state.getBlock()));
      if (idLookedup != null) {
         idLookedup.addLines(lines, state, pos, observer, world, hitFace);
      }

      for (Pair<ScryingLensOverlayRegistry.OverlayPredicate, ScryingLensOverlayRegistry.OverlayBuilder> pair : PREDICATE_LOOKUP) {
         if (((ScryingLensOverlayRegistry.OverlayPredicate)pair.getFirst()).test(state, pos, observer, world, hitFace)) {
            ((ScryingLensOverlayRegistry.OverlayBuilder)pair.getSecond()).addLines(lines, state, pos, observer, world, hitFace);
         }
      }

      return lines;
   }

   @FunctionalInterface
   public interface OverlayBuilder {
      void addLines(List<Pair<ItemStack, Component>> var1, BlockState var2, BlockPos var3, Player var4, Level var5, Direction var6);
   }

   @FunctionalInterface
   public interface OverlayPredicate {
      boolean test(BlockState var1, BlockPos var2, Player var3, Level var4, Direction var5);
   }
}
