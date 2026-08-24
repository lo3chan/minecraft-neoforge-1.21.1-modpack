package com.iafenvoy.origins.data.power.component.builtin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.ResourcePower;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.function.IntBinaryOperator;
import org.jetbrains.annotations.NotNull;

public class ResourceComponent extends PowerComponent {
   public static final MapCodec<ResourceComponent> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Codec.INT.fieldOf("value").forGetter(ResourceComponent::getValue),
            Codec.INT.optionalFieldOf("min", -2147483648).forGetter(ResourceComponent::getMin),
            Codec.INT.optionalFieldOf("max", 2147483647).forGetter(ResourceComponent::getMax)
         )
         .apply(i, ResourceComponent::new)
   );
   private int value;
   private int min;
   private int max;
   private boolean checkCallback = false;

   public ResourceComponent(int value, int min, int max) {
      this.value = Math.clamp(value, min, max);
      this.min = min;
      this.max = max;
   }

   public int getValue() {
      return this.value;
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }

   @NotNull
   @Override
   public MapCodec<? extends PowerComponent> codec() {
      return CODEC;
   }

   public void setValue(int value) {
      this.value = Math.clamp(value, this.min, this.max);
      this.markDirty();
   }

   public void updateResource(Int2IntFunction operation) {
      this.value = Math.clamp(operation.applyAsInt(this.value), this.min, this.max);
      this.markDirty();
   }

   public void updateResource(IntBinaryOperator operation, int value) {
      this.value = Math.clamp(operation.applyAsInt(this.value, value), this.min, this.max);
      this.markDirty();
   }

   @Override
   public void tick(OriginDataHolder holder, PowerHolder parent) {
      if (this.checkCallback && parent.power() instanceof ResourcePower power) {
         this.checkCallback = false;
         this.min = power.getMinValue();
         this.max = power.getMaxValue();
         if (this.value == this.min) {
            power.getMinAction().execute(holder.getEntity());
         }

         if (this.value == this.max) {
            power.getMaxAction().execute(holder.getEntity());
         }
      }
   }

   @Override
   public void markDirty() {
      super.markDirty();
      this.checkCallback = true;
   }
}
