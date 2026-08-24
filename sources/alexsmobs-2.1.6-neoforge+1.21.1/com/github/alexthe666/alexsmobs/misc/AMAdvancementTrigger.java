package com.github.alexthe666.alexsmobs.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class AMAdvancementTrigger extends SimpleCriterionTrigger<AMAdvancementTrigger.Instance> {
   public final ResourceLocation resourceLocation;

   public AMAdvancementTrigger(ResourceLocation resourceLocation) {
      this.resourceLocation = resourceLocation;
   }

   public void trigger(ServerPlayer p_192180_1_) {
      this.trigger(p_192180_1_, p_226308_1_ -> true);
   }

   public Codec<AMAdvancementTrigger.Instance> codec() {
      return AMAdvancementTrigger.Instance.CODEC;
   }

   public record Instance(Optional<ContextAwarePredicate> player) implements SimpleInstance {
      public static final Codec<AMAdvancementTrigger.Instance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(AMAdvancementTrigger.Instance::player))
            .apply(instance, AMAdvancementTrigger.Instance::new)
      );
   }
}
