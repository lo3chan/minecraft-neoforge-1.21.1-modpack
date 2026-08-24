package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.DamageCondition;
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
public class SelfActionWhenHitPower extends HasCooldownPower {
   public static final MapCodec<SelfActionWhenHitPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            HasCooldownPower.CooldownSettings.CODEC.forGetter(HasCooldownPower::getCooldown),
            EntityAction.CODEC.fieldOf("entity_action").forGetter(SelfActionWhenHitPower::getEntityAction),
            DamageCondition.optionalCodec("damage_condition").forGetter(SelfActionWhenHitPower::getDamageCondition)
         )
         .apply(i, SelfActionWhenHitPower::new)
   );
   private final EntityAction entityAction;
   private final DamageCondition damageCondition;

   public SelfActionWhenHitPower(
      Power.BaseSettings settings, HasCooldownPower.CooldownSettings cooldown, EntityAction entityAction, DamageCondition damageCondition
   ) {
      super(settings, cooldown);
      this.entityAction = entityAction;
      this.damageCondition = damageCondition;
   }

   public EntityAction getEntityAction() {
      return this.entityAction;
   }

   public DamageCondition getDamageCondition() {
      return this.damageCondition;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @SubscribeEvent
   public static void onHit(Post event) {
      Entity self = event.getEntity();
      PowerHelper.get(self)
         .execute(
            SelfActionWhenHitPower.class,
            p -> p.damageCondition.test(event.getSource(), event.getNewDamage()),
            (h, p) -> p.getCooldownComponent(h).useIfReady(() -> p.entityAction.execute(self))
         );
   }
}
