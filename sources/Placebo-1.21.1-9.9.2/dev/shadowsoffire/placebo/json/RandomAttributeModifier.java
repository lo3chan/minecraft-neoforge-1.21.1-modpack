package dev.shadowsoffire.placebo.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.placebo.Placebo;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import dev.shadowsoffire.placebo.util.StepFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public record RandomAttributeModifier(Holder<Attribute> attribute, Operation operation, StepFunction value) {
   public static Codec<RandomAttributeModifier> CODEC = RecordCodecBuilder.create(
      inst -> inst.group(
            BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(a -> a.attribute),
            Operation.CODEC.fieldOf("operation").forGetter(a -> a.operation),
            StepFunction.CODEC.fieldOf("value").forGetter(a -> a.value)
         )
         .apply(inst, RandomAttributeModifier::new)
   );
   public static Codec<RandomAttributeModifier> CONSTANT_CODEC = RecordCodecBuilder.create(
      inst -> inst.group(
            BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(a -> a.attribute),
            PlaceboCodecs.enumCodec(Operation.class).fieldOf("operation").forGetter(a -> a.operation),
            StepFunction.CONSTANT_CODEC.fieldOf("value").forGetter(a -> a.value)
         )
         .apply(inst, RandomAttributeModifier::new)
   );

   public AttributeModifier create(ResourceLocation id, RandomSource rand) {
      return new AttributeModifier(id, this.value.get(rand.nextFloat()), this.operation);
   }

   public AttributeModifier createDeterministic(ResourceLocation id) {
      return new AttributeModifier(id, this.value.min(), this.operation);
   }

   public void apply(ResourceLocation id, RandomSource rand, LivingEntity entity) {
      if (entity == null) {
         throw new RuntimeException("Attempted to apply a random attribute modifier to a null entity!");
      } else {
         AttributeModifier modif = this.create(id, rand);
         AttributeInstance inst = entity.getAttribute(this.attribute);
         if (inst == null) {
            Placebo.LOGGER
               .trace(
                  String.format(
                     "Attempted to apply a random attribute modifier to an entity (%s) that does not have that attribute (%s)!",
                     EntityType.getKey(entity.getType()),
                     this.attribute.unwrapKey().get()
                  )
               );
         } else {
            inst.addPermanentModifier(modif);
         }
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public AttributeModifier create(RandomSource rand) {
      return new AttributeModifier(
         Placebo.loc("random_modifier_" + ((Attribute)this.attribute.value()).getDescriptionId() + rand.nextInt()),
         this.value.get(rand.nextFloat()),
         this.operation
      );
   }

   @Deprecated(
      forRemoval = true
   )
   public AttributeModifier createDeterministic() {
      return new AttributeModifier(Placebo.loc("random_modifier_" + ((Attribute)this.attribute.value()).getDescriptionId()), this.value.min(), this.operation);
   }

   @Deprecated(
      forRemoval = true
   )
   public void apply(RandomSource rand, LivingEntity entity) {
      if (entity == null) {
         throw new RuntimeException("Attempted to apply a random attribute modifier to a null entity!");
      } else {
         AttributeModifier modif = this.create(rand);
         AttributeInstance inst = entity.getAttribute(this.attribute);
         if (inst == null) {
            Placebo.LOGGER
               .trace(
                  String.format(
                     "Attempted to apply a random attribute modifier to an entity (%s) that does not have that attribute (%s)!",
                     EntityType.getKey(entity.getType()),
                     this.attribute.unwrapKey().get()
                  )
               );
         } else {
            inst.addPermanentModifier(modif);
         }
      }
   }
}
