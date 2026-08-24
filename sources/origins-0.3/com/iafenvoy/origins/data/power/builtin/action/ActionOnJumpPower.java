package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ActionOnJumpPower extends HasCooldownPower {
   public static final MapCodec<ActionOnJumpPower> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            HasCooldownPower.CooldownSettings.CODEC.forGetter(HasCooldownPower::getCooldown),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnJumpPower::getEntityAction)
         )
         .apply(instance, ActionOnJumpPower::new)
   );
   private final EntityAction entityAction;

   public ActionOnJumpPower(Power.BaseSettings settings, HasCooldownPower.CooldownSettings cooldown, EntityAction entityAction) {
      super(settings, cooldown);
      this.entityAction = entityAction;
   }

   public EntityAction getEntityAction() {
      return this.entityAction;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @SubscribeEvent
   public static void onJump(LivingJumpEvent event) {
      LivingEntity entity = event.getEntity();
      PowerHelper.get(entity)
         .execute(ActionOnJumpPower.class, (holder, power) -> power.getCooldownComponent(holder).useIfReady(() -> power.entityAction.execute(entity)));
   }
}
