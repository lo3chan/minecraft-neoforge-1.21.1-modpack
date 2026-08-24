package com.iafenvoy.origins.content;

import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.event.GrantOriginEvent;
import com.iafenvoy.origins.registry.OriginsCriterionTriggers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ChoseOriginCriterionTrigger extends SimpleCriterionTrigger<ChoseOriginCriterionTrigger.TriggerInstance> {
   public void trigger(ServerPlayer player, Holder<Layer> layer, Holder<Origin> origin) {
      this.trigger(player, i -> i.matches(layer, origin));
   }

   @NotNull
   public Codec<ChoseOriginCriterionTrigger.TriggerInstance> codec() {
      return ChoseOriginCriterionTrigger.TriggerInstance.CODEC;
   }

   @SubscribeEvent
   public static void onChooseOrigin(GrantOriginEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         ((ChoseOriginCriterionTrigger)OriginsCriterionTriggers.CHOSE_ORIGIN.get()).trigger(player, event.getLayer(), event.getOrigin());
      }
   }

   public record TriggerInstance(@NotNull Optional<ContextAwarePredicate> player, Optional<Holder<Layer>> layer, Holder<Origin> origin)
      implements SimpleInstance {
      public static final Codec<ChoseOriginCriterionTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         inst -> inst.group(
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(ChoseOriginCriterionTrigger.TriggerInstance::player),
               Layer.CODEC.optionalFieldOf("layer").forGetter(ChoseOriginCriterionTrigger.TriggerInstance::layer),
               Origin.CODEC.fieldOf("origin").forGetter(ChoseOriginCriterionTrigger.TriggerInstance::origin)
            )
            .apply(inst, ChoseOriginCriterionTrigger.TriggerInstance::new)
      );

      public boolean matches(Holder<Layer> layer, Holder<Origin> origin) {
         return this.layer.<Boolean>map(l -> Objects.equals(l, layer)).orElse(true) && Objects.equals(this.origin, origin);
      }
   }
}
