package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
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
public class ActionWhenHitPower extends HasCooldownPower {
   public static final MapCodec<ActionWhenHitPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            HasCooldownPower.CooldownSettings.CODEC.forGetter(HasCooldownPower::getCooldown),
            BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(ActionWhenHitPower::getBiEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionWhenHitPower::getBiEntityCondition),
            DamageCondition.optionalCodec("damage_condition").forGetter(ActionWhenHitPower::getDamageCondition)
         )
         .apply(i, ActionWhenHitPower::new)
   );
   private final BiEntityAction biEntityAction;
   private final BiEntityCondition biEntityCondition;
   private final DamageCondition damageCondition;

   public ActionWhenHitPower(
      Power.BaseSettings settings,
      HasCooldownPower.CooldownSettings cooldown,
      BiEntityAction biEntityAction,
      BiEntityCondition biEntityCondition,
      DamageCondition damageCondition
   ) {
      super(settings, cooldown);
      this.biEntityAction = biEntityAction;
      this.biEntityCondition = biEntityCondition;
      this.damageCondition = damageCondition;
   }

   public BiEntityAction getBiEntityAction() {
      return this.biEntityAction;
   }

   public BiEntityCondition getBiEntityCondition() {
      return this.biEntityCondition;
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
   public static void onDamage(Post event) {
      Entity source = event.getSource().getEntity();
      Entity target = event.getEntity();
      if (source != null) {
         PowerHelper.get(target)
            .execute(
               ActionWhenHitPower.class,
               p -> p.biEntityCondition.test(source, target) && p.damageCondition.test(event.getSource(), event.getNewDamage()),
               (h, p) -> p.getCooldownComponent(h).useIfReady(() -> p.biEntityAction.execute(source, target))
            );
      }
   }
}
