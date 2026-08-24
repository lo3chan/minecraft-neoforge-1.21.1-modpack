package vectorwing.farmersdelight.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.Set;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import vectorwing.farmersdelight.common.registry.ModItems;

public class CommonSetup {
   public static void init(FMLCommonSetupEvent event) {
      event.enqueueWork(() -> {
         registerDispenserBehaviors();
         registerItemSetAdditions();
      });
   }

   public static void registerDispenserBehaviors() {
      DispenserBlock.registerProjectileBehavior((ItemLike)ModItems.ROTTEN_TOMATO.get());
   }

   public static void registerItemSetAdditions() {
      Set<Item> newWantedItems = Sets.newHashSet(
         new Item[]{
            ModItems.CABBAGE.get(),
            ModItems.TOMATO.get(),
            ModItems.ONION.get(),
            ModItems.RICE.get(),
            ModItems.CABBAGE_SEEDS.get(),
            ModItems.TOMATO_SEEDS.get(),
            ModItems.RICE_PANICLE.get()
         }
      );
      newWantedItems.addAll(Villager.WANTED_ITEMS);
      Villager.WANTED_ITEMS = ImmutableSet.copyOf(newWantedItems);
      HashMap<Item, Integer> newFoodPoints = new HashMap<>();
      newFoodPoints.put(ModItems.CABBAGE.get(), 1);
      newFoodPoints.put(ModItems.TOMATO.get(), 1);
      newFoodPoints.put(ModItems.ONION.get(), 1);
      newFoodPoints.put(ModItems.RICE.get(), 2);
      newFoodPoints.putAll(Villager.FOOD_POINTS);
      Villager.FOOD_POINTS = ImmutableMap.copyOf(newFoodPoints);
   }
}
