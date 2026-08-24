package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class SprintingPower extends Power {
   public static final MapCodec<SprintingPower> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("requires_input", false).forGetter(SprintingPower::requiresInput)
         )
         .apply(instance, SprintingPower::new)
   );
   private final boolean requiresInput;

   public SprintingPower(Power.BaseSettings settings, boolean requiresInput) {
      super(settings);
      this.requiresInput = requiresInput;
   }

   public boolean requiresInput() {
      return this.requiresInput;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @SubscribeEvent
   public static void onTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         PowerHelper.get(player)
            .execute(SprintingPower.class, power -> !power.requiresInput || player.isSprinting(), (holder, power) -> player.setSprinting(true));
      }
   }
}
