package net.mehvahdjukaar.moonlight.api.item.additional_placements;

import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.misc.IExtendedItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class AdditionalItemPlacementsAPI {
   @Deprecated(
      forRemoval = true
   )
   public static void addRegistration(Consumer<AdditionalItemPlacementsAPI.Event> eventConsumer) {
      Moonlight.assertInitPhase();
      PlatHelper.addCommonSetup(() -> eventConsumer.accept(AdditionalItemPlacementsAPI::registerPlacement));
   }

   public static void registerPlacement(Item target, AdditionalItemPlacement placement) {
      IExtendedItem ei = (IExtendedItem)target;
      AdditionalItemPlacement old = ei.moonlight$getAdditionalBehavior();
      if (old != null) {
         unregisterPlacement(target);
         Moonlight.LOGGER.warn("Overriding existing additional placement behavior for item {}, placement {}", target, old);
      }

      ei.moonlight$setAdditionalBehavior(placement);
      Block placedBlock = placement.getPlacedBlock();
      if (target != Items.AIR && placedBlock != Blocks.AIR) {
         Item.BY_BLOCK.put(placedBlock, target);
         placedBlock.item = null;
      } else {
         throw new AssertionError("Invalid item or block for additional placement: block = " + placedBlock + ", item = " + target);
      }
   }

   public static void registerSimplePlacement(Item target, Block toPlace) {
      registerPlacement(target, new AdditionalItemPlacement(toPlace));
   }

   public static void unregisterPlacement(Item target) {
      IExtendedItem ei = (IExtendedItem)target;
      AdditionalItemPlacement old = ei.moonlight$getAdditionalBehavior();
      if (old != null) {
         ei.moonlight$setAdditionalBehavior(null);
         Block placedBlock = old.getPlacedBlock();
         if (placedBlock.item == target) {
            Item.BY_BLOCK.remove(placedBlock);
            placedBlock.item = null;
         }
      }
   }

   @Nullable
   public static AdditionalItemPlacement getBehavior(Item item) {
      return ((IExtendedItem)item).moonlight$getAdditionalBehavior();
   }

   public static boolean hasBehavior(Item item) {
      return getBehavior(item) != null;
   }

   @Deprecated(
      forRemoval = true
   )
   public interface Event {
      void register(Item var1, AdditionalItemPlacement var2);

      default void registerSimple(Item target, Block toPlace) {
         this.register(target, new AdditionalItemPlacement(toPlace));
      }
   }
}
