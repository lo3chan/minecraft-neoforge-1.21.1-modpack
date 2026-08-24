package vectorwing.farmersdelight.client.event;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.PossibleEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.FoodValues;
import vectorwing.farmersdelight.common.utility.TextUtils;

@EventBusSubscriber(
   modid = "farmersdelight",
   value = {Dist.CLIENT}
)
public class TooltipEvents {
   @SubscribeEvent
   public static void addTooltipToVanillaSoups(ItemTooltipEvent event) {
      Item food = event.getItemStack().getItem();
      if (food.equals(Items.PUMPKIN_PIE)) {
         event.getToolTip().add(Configuration.ENABLE_PUMPKIN_PIE_SNEAK_TO_PLACE.get() ? TextUtils.PLACEABLE_SNEAKING : TextUtils.PLACEABLE);
      }

      if (Configuration.ENABLE_VANILLA_SOUP_EXTRA_EFFECTS.get() && Configuration.ENABLE_FOOD_EFFECT_TOOLTIP.get()) {
         FoodProperties soupEffects = FoodValues.VANILLA_SOUP_EFFECTS.get(food);
         if (soupEffects != null) {
            List<Component> tooltip = event.getToolTip();

            for (PossibleEffect effect : soupEffects.effects()) {
               MobEffectInstance effectInstance = effect.effect();
               MutableComponent effectText = Component.translatable(effectInstance.getDescriptionId());
               Player player = event.getEntity();
               if (effectInstance.getDuration() > 20) {
                  effectText = Component.translatable(
                     "potion.withDuration",
                     new Object[]{
                        effectText, MobEffectUtil.formatDuration(effectInstance, 1.0F, player == null ? 20.0F : player.level().tickRateManager().tickrate())
                     }
                  );
               }

               tooltip.add(effectText.withStyle(((MobEffect)effectInstance.getEffect().value()).getCategory().getTooltipFormatting()));
            }
         }
      }
   }
}
