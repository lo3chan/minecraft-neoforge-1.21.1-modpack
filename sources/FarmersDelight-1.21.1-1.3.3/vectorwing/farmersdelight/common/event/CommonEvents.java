package vectorwing.farmersdelight.common.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.PossibleEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.FoodValues;

@EventBusSubscriber(
   modid = "farmersdelight"
)
public class CommonEvents {
   @SubscribeEvent
   public static void handleVanillaSoupEffects(Finish event) {
      Item food = event.getItem().getItem();
      LivingEntity entity = event.getEntity();
      if (!Configuration.ENABLE_RABBIT_STEW_BUFF.get() || !food.equals(Items.RABBIT_STEW)) {
         if (Configuration.ENABLE_VANILLA_SOUP_EXTRA_EFFECTS.get()) {
            FoodProperties soupEffects = FoodValues.VANILLA_SOUP_EFFECTS.get(food);
            if (soupEffects != null) {
               for (PossibleEffect effect : soupEffects.effects()) {
                  entity.addEffect(effect.effect());
               }
            }
         }
      }
   }
}
