package top.theillusivec4.curios.common.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotAttribute;
import top.theillusivec4.curios.common.CuriosRegistry;

public class SetCurioAttributesFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetCurioAttributesFunction> CODEC = RecordCodecBuilder.mapCodec(
      instance -> commonFields(instance)
         .and(
            instance.group(
               ExtraCodecs.nonEmptyList(SetCurioAttributesFunction.Modifier.MODIFIER_CODEC.listOf())
                  .fieldOf("modifiers")
                  .forGetter(function -> function.modifiers),
               Codec.BOOL.optionalFieldOf("replace", Boolean.TRUE).forGetter(function -> function.replace)
            )
         )
         .apply(instance, SetCurioAttributesFunction::new)
   );
   final List<SetCurioAttributesFunction.Modifier> modifiers;
   final boolean replace;

   SetCurioAttributesFunction(List<LootItemCondition> conditions, List<SetCurioAttributesFunction.Modifier> modifiers, boolean replace) {
      super(conditions);
      this.modifiers = ImmutableList.copyOf(modifiers);
      this.replace = replace;
   }

   @Nonnull
   public LootItemFunctionType<SetCurioAttributesFunction> getType() {
      return CuriosRegistry.CURIO_ATTRIBUTES.get();
   }

   @Nonnull
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.modifiers.stream().flatMap(mod -> mod.amount.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
   }

   @Nonnull
   public ItemStack run(@Nonnull ItemStack stack, LootContext context) {
      RandomSource random = context.getRandom();

      for (SetCurioAttributesFunction.Modifier modifier : this.modifiers) {
         String slot = (String)Util.getRandom(modifier.slots, random);
         if (modifier.attribute.value() instanceof SlotAttribute wrapper) {
            CuriosApi.addSlotModifier(stack, wrapper.getIdentifier(), modifier.id, modifier.amount.getFloat(context), modifier.operation, slot);
         } else {
            CuriosApi.addModifier(stack, modifier.attribute, modifier.id, modifier.amount.getFloat(context), modifier.operation, slot);
         }
      }

      return stack;
   }

   record Modifier(Holder<Attribute> attribute, Operation operation, NumberProvider amount, ResourceLocation id, List<String> slots) {
      private static final Codec<List<String>> SLOTS_CODEC = ExtraCodecs.nonEmptyList(
         Codec.either(Codec.STRING, Codec.list(Codec.STRING))
            .xmap(
               either -> (List)either.map(List::of, Function.identity()), list -> list.size() == 1 ? Either.left((String)list.getFirst()) : Either.right(list)
            )
      );
      private static final Codec<Holder<Attribute>> ATTRIBUTE_CODEC = new PrimitiveCodec<Holder<Attribute>>() {
         public <T> DataResult<Holder<Attribute>> read(DynamicOps<T> ops, T input) {
            return ops.getStringValue(input).map(name -> {
               ResourceLocation rl = ResourceLocation.tryParse(name);
               if (rl == null) {
                  return null;
               } else {
                  Holder<Attribute> attribute;
                  if (rl.getNamespace().equals("curios")) {
                     String identifier = rl.getPath();
                     if (CuriosApi.getSlot(identifier, false).isEmpty()) {
                        throw new JsonSyntaxException("Unknown curios slot type: " + identifier);
                     }

                     attribute = SlotAttribute.getOrCreate(identifier);
                  } else {
                     attribute = (Holder<Attribute>)BuiltInRegistries.ATTRIBUTE.getHolder(rl).orElse(null);
                  }

                  return attribute;
               }
            });
         }

         public <T> T write(DynamicOps<T> ops, Holder<Attribute> value) {
            ResourceLocation rl;
            if (value.value() instanceof SlotAttribute wrapper) {
               rl = ResourceLocation.fromNamespaceAndPath("curios", wrapper.getIdentifier());
            } else {
               rl = BuiltInRegistries.ATTRIBUTE.getKey((Attribute)value.value());
            }

            return (T)(rl != null ? ops.createString(rl.toString()) : ops.empty());
         }
      };
      public static final Codec<SetCurioAttributesFunction.Modifier> MODIFIER_CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               ATTRIBUTE_CODEC.fieldOf("attribute").forGetter(SetCurioAttributesFunction.Modifier::attribute),
               Operation.CODEC.fieldOf("operation").forGetter(SetCurioAttributesFunction.Modifier::operation),
               NumberProviders.CODEC.fieldOf("amount").forGetter(SetCurioAttributesFunction.Modifier::amount),
               ResourceLocation.CODEC.fieldOf("id").forGetter(SetCurioAttributesFunction.Modifier::id),
               SLOTS_CODEC.fieldOf("slot").forGetter(SetCurioAttributesFunction.Modifier::slots)
            )
            .apply(instance, SetCurioAttributesFunction.Modifier::new)
      );
   }
}
