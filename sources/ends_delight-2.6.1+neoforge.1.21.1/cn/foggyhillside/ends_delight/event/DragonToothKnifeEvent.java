package cn.foggyhillside.ends_delight.event;

import cn.foggyhillside.ends_delight.EDCommonConfigs;
import cn.foggyhillside.ends_delight.registry.ModItems;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

public class DragonToothKnifeEvent {
   @EventBusSubscriber(
      modid = "ends_delight"
   )
   public static class KnifeEvents {
      @SubscribeEvent
      public static void onAttackEndMobs(Pre event) {
         LivingEntity target = event.getEntity();
         String[] endMobs = ((List)EDCommonConfigs.END_MOBS.get()).toArray(new String[0]);

         for (String endMob : endMobs) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
            if (id.equals(ResourceLocation.tryParse(endMob)) && event.getSource().getEntity() instanceof LivingEntity attacker) {
               ItemStack toolStack = attacker.getItemInHand(InteractionHand.MAIN_HAND);
               if (toolStack.is((Item)ModItems.DRAGON_TOOTH_KNIFE.get())) {
                  event.setNewDamage(event.getNewDamage() * 3.5F);
                  break;
               }
            }
         }
      }
   }
}
