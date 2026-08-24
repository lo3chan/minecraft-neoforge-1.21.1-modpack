package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class HoverPower extends Power {
   public static final MapCodec<HoverPower> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings), Codec.FLOAT.optionalFieldOf("step_assist", 0.0F).forGetter(HoverPower::stepAssist)
         )
         .apply(instance, HoverPower::new)
   );
   private final float stepAssist;

   public HoverPower(Power.BaseSettings settings, float stepAssist) {
      super(settings);
      this.stepAssist = stepAssist;
   }

   public float stepAssist() {
      return this.stepAssist;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @SubscribeEvent
   public static void onTick(Post event) {
      Entity entity = event.getEntity();
      if (PowerHelper.get(entity).anyActive(HoverPower.class)) {
         Vec3 velocity = entity.getDeltaMovement();
         entity.setDeltaMovement(velocity.x, 0.0, velocity.z);
         entity.hurtMarked = true;
      }
   }
}
