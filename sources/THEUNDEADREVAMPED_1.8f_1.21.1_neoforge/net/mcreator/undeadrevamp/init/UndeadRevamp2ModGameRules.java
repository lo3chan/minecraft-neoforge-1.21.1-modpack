package net.mcreator.undeadrevamp.init;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Category;
import net.minecraft.world.level.GameRules.Key;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class UndeadRevamp2ModGameRules {
   public static Key<BooleanValue> SUNRAY;
   public static Key<BooleanValue> HUNTERNIBLING;

   @SubscribeEvent
   public static void registerGameRules(FMLCommonSetupEvent event) {
      SUNRAY = GameRules.register("sunray", Category.MOBS, BooleanValue.create(true));
      HUNTERNIBLING = GameRules.register("hunternibling", Category.MOBS, BooleanValue.create(true));
   }
}
