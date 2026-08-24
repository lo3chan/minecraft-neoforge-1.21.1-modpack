package com.iafenvoy.origins.util.math;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.component.builtin.ResourceComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.DoubleBinaryOperator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

public record Modifier(double value, Modifier.ModifierOperation operation, Optional<ResourceLocation> resource, Optional<Modifier> modifier) {
   public static final Codec<Modifier> CODEC = Codec.recursive(
      Modifier.class.getSimpleName(),
      codec -> RecordCodecBuilder.create(
         i -> i.group(
               Codec.DOUBLE.fieldOf("value").forGetter(Modifier::value),
               Modifier.ModifierOperation.CODEC.optionalFieldOf("operation", Modifier.ModifierOperation.ADD_BASE_EARLY).forGetter(Modifier::operation),
               ResourceLocation.CODEC.optionalFieldOf("resource").forGetter(Modifier::resource),
               codec.optionalFieldOf("modifier").forGetter(Modifier::modifier)
            )
            .apply(i, Modifier::new)
      )
   );

   public double getValue(OriginDataHolder holder) {
      return this.resource
         .<ResourceComponent>flatMap(x -> holder.getComponent(x, ResourceComponent.class))
         .map(ResourceComponent::getValue)
         .map(Double.class::cast)
         .map(x -> this.modifier.<Double>map(m -> applyModifiers(holder, List.of(m), x)).orElse(x))
         .orElse(this.value);
   }

   public static int applyModifiers(OriginDataHolder holder, List<Modifier> modifiers, int value) {
      return (int)applyModifiers(holder, modifiers, (double)value);
   }

   public static float applyModifiers(OriginDataHolder holder, List<Modifier> modifiers, float value) {
      return (float)applyModifiers(holder, modifiers, (double)value);
   }

   public static double applyModifiers(OriginDataHolder holder, List<Modifier> modifiers, double value) {
      Map<Modifier.ModifierOperation, DoubleList> modifierMap = new EnumMap<>(Modifier.ModifierOperation.class);
      modifiers.forEach(m -> modifierMap.computeIfAbsent(m.operation(), op -> new DoubleArrayList()).add(m.getValue(holder)));

      for (Modifier.ModifierOperation operation : Modifier.ModifierOperation.values()) {
         if (modifierMap.containsKey(operation)) {
            value = operation.getOperator().applyAsDouble(value, modifierMap.get(operation));
         }
      }

      return value;
   }

   public static Modifier fromAttributeModifier(AttributeModifier attributeModifier) {
      return new Modifier(attributeModifier.amount(), switch (attributeModifier.operation()) {
         case ADD_VALUE -> Modifier.ModifierOperation.ADD_BASE_EARLY;
         case ADD_MULTIPLIED_BASE -> Modifier.ModifierOperation.MULTIPLY_BASE_MULTIPLICATIVE;
         case ADD_MULTIPLIED_TOTAL -> Modifier.ModifierOperation.MULTIPLY_TOTAL_MULTIPLICATIVE;
         default -> throw new MatchException(null, null);
      }, Optional.empty(), Optional.empty());
   }

   public static enum ModifierOperation implements StringRepresentable {
      ADD_BASE_EARLY(Double::sum, false),
      MULTIPLY_BASE_ADDITIVE((cur, val) -> cur * (1.0 + val), true),
      MULTIPLY_BASE_MULTIPLICATIVE((cur, val) -> cur * (1.0 + val), false),
      ADD_BASE_LATE(Double::sum, false),
      MULTIPLY_TOTAL_ADDITIVE((cur, val) -> cur * (1.0 + val), true),
      MULTIPLY_TOTAL_MULTIPLICATIVE((cur, val) -> cur * (1.0 + val), false),
      SET_TOTAL((cur, val) -> val, false),
      MIN_TOTAL(Math::min, false),
      MAX_TOTAL(Math::max, false);

      public static final Codec<Modifier.ModifierOperation> CODEC = StringRepresentable.fromEnum(Modifier.ModifierOperation::values);
      private final Modifier.ModifierOperation.MultiDoubleBinaryOperator operator;

      private ModifierOperation(DoubleBinaryOperator operator, boolean sum) {
         this.operator = sum ? (v, l) -> operator.applyAsDouble(v, l.doubleStream().sum()) : (v, l) -> l.doubleStream().reduce(v, operator);
      }

      public Modifier.ModifierOperation.MultiDoubleBinaryOperator getOperator() {
         return this.operator;
      }

      @NotNull
      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }

      @FunctionalInterface
      public interface MultiDoubleBinaryOperator {
         double applyAsDouble(double var1, DoubleList var3);
      }
   }
}
