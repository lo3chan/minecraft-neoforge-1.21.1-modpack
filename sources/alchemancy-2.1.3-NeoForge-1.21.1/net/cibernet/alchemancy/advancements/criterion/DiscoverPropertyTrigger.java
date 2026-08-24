package net.cibernet.alchemancy.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyCriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class DiscoverPropertyTrigger extends SimpleCriterionTrigger<DiscoverPropertyTrigger.TriggerInsance> {
   public Codec<DiscoverPropertyTrigger.TriggerInsance> codec() {
      return DiscoverPropertyTrigger.TriggerInsance.CODEC;
   }

   public void trigger(ServerPlayer player, ItemStack stack) {
      super.trigger(player, triggerInsance -> triggerInsance.matches(stack));
   }

   public record TriggerInsance(Optional<ContextAwarePredicate> player, Optional<Holder<Property>> property) implements SimpleInstance {
      public static final Codec<DiscoverPropertyTrigger.TriggerInsance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(DiscoverPropertyTrigger.TriggerInsance::player),
               Property.CODEC.optionalFieldOf("property").forGetter(DiscoverPropertyTrigger.TriggerInsance::property)
            )
            .apply(instance, DiscoverPropertyTrigger.TriggerInsance::new)
      );

      public boolean matches(ItemStack stack) {
         return this.property.isEmpty() || InfusedPropertiesHelper.hasInfusedProperty(stack, this.property.get());
      }

      public boolean matches(Holder<Property> property) {
         return this.property.isEmpty() || this.property.get().is(Objects.requireNonNull(property.getKey()));
      }

      public static Criterion<DiscoverPropertyTrigger.TriggerInsance> discoverProperty(Holder<Property> propertyHolder) {
         return ((DiscoverPropertyTrigger)AlchemancyCriteriaTriggers.DISCOVER_PROPERTY.get())
            .createCriterion(new DiscoverPropertyTrigger.TriggerInsance(Optional.empty(), Optional.of(propertyHolder)));
      }
   }
}
