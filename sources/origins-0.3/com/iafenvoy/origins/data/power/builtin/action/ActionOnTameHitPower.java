package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ActionOnTameHitPower extends TameHitPower {
   public static final MapCodec<ActionOnTameHitPower> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            HasCooldownPower.CooldownSettings.CODEC.forGetter(HasCooldownPower::getCooldown),
            DamageCondition.optionalCodec("damage_condition").forGetter(power -> power.damageCondition),
            BiEntityAction.optionalCodec("bientity_action").forGetter(power -> power.bientityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(power -> power.bientityCondition),
            BiEntityAction.optionalCodec("owner_bientity_action").forGetter(power -> power.ownerBientityAction),
            BiEntityCondition.optionalCodec("owner_bientity_condition").forGetter(power -> power.ownerBientityCondition)
         )
         .apply(instance, ActionOnTameHitPower::new)
   );

   public ActionOnTameHitPower(
      Power.BaseSettings settings,
      HasCooldownPower.CooldownSettings cooldown,
      DamageCondition damageCondition,
      BiEntityAction bientityAction,
      BiEntityCondition bientityCondition,
      BiEntityAction ownerBientityAction,
      BiEntityCondition ownerBientityCondition
   ) {
      super(settings, cooldown, damageCondition, bientityAction, bientityCondition, ownerBientityAction, ownerBientityCondition);
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @SubscribeEvent
   public static void onDamage(Post event) {
      if (event.getSource().getEntity() instanceof TamableAnimal tame) {
         LivingEntity owner = tame.getOwner();
         if (owner != null) {
            PowerHelper.get(owner)
               .execute(
                  ActionOnTameHitPower.class,
                  (holder, power) -> execute(power, holder, tame, event.getEntity(), owner, event.getEntity(), event.getSource(), event.getNewDamage())
               );
         }
      }
   }
}
