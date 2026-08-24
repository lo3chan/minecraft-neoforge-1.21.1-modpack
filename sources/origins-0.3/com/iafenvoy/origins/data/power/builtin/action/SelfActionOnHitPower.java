package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class SelfActionOnHitPower extends HasCooldownPower {
   public static final MapCodec<SelfActionOnHitPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            HasCooldownPower.CooldownSettings.CODEC.forGetter(HasCooldownPower::getCooldown),
            EntityAction.CODEC.fieldOf("entity_action").forGetter(SelfActionOnHitPower::getEntityAction),
            DamageCondition.optionalCodec("damage_condition").forGetter(SelfActionOnHitPower::getDamageCondition),
            EntityCondition.optionalCodec("target_condition").forGetter(SelfActionOnHitPower::getTargetCondition)
         )
         .apply(i, SelfActionOnHitPower::new)
   );
   private final EntityAction entityAction;
   private final DamageCondition damageCondition;
   private final EntityCondition targetCondition;

   public SelfActionOnHitPower(
      Power.BaseSettings settings,
      HasCooldownPower.CooldownSettings cooldown,
      EntityAction entityAction,
      DamageCondition damageCondition,
      EntityCondition targetCondition
   ) {
      super(settings, cooldown);
      this.entityAction = entityAction;
      this.damageCondition = damageCondition;
      this.targetCondition = targetCondition;
   }

   public EntityAction getEntityAction() {
      return this.entityAction;
   }

   public DamageCondition getDamageCondition() {
      return this.damageCondition;
   }

   public EntityCondition getTargetCondition() {
      return this.targetCondition;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @SubscribeEvent
   public static void onHit(Post event) {
      Entity self = event.getSource().getEntity();
      Entity target = event.getEntity();
      if (self != null) {
         PowerHelper.get(self)
            .execute(
               SelfActionOnHitPower.class,
               p -> p.damageCondition.test(event.getSource(), event.getNewDamage()) && p.targetCondition.test(target),
               (h, p) -> p.getCooldownComponent(h).useIfReady(() -> p.entityAction.execute(self))
            );
      }
   }
}
