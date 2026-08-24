package top.theillusivec4.curios.api;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record CurioAttributeModifiers(List<CurioAttributeModifiers.Entry> modifiers, boolean showInTooltip) {
   public static final CurioAttributeModifiers EMPTY = new CurioAttributeModifiers(List.of(), true);
   private static final Codec<CurioAttributeModifiers> FULL_CODEC = RecordCodecBuilder.create(
      p_337947_ -> p_337947_.group(
            CurioAttributeModifiers.Entry.CODEC.listOf().fieldOf("modifiers").forGetter(CurioAttributeModifiers::modifiers),
            Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.TRUE).forGetter(CurioAttributeModifiers::showInTooltip)
         )
         .apply(p_337947_, CurioAttributeModifiers::new)
   );
   public static final Codec<CurioAttributeModifiers> CODEC = Codec.withAlternative(
      FULL_CODEC, CurioAttributeModifiers.Entry.CODEC.listOf(), p_332621_ -> new CurioAttributeModifiers(p_332621_, true)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, CurioAttributeModifiers> STREAM_CODEC = StreamCodec.composite(
      CurioAttributeModifiers.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()),
      CurioAttributeModifiers::modifiers,
      ByteBufCodecs.BOOL,
      CurioAttributeModifiers::showInTooltip,
      CurioAttributeModifiers::new
   );

   public CurioAttributeModifiers withTooltip(boolean showInTooltip) {
      return new CurioAttributeModifiers(this.modifiers, showInTooltip);
   }

   public static CurioAttributeModifiers.Builder builder() {
      return new CurioAttributeModifiers.Builder();
   }

   public CurioAttributeModifiers withModifierAdded(ResourceLocation attribute, AttributeModifier attributeModifier, String slot) {
      com.google.common.collect.ImmutableList.Builder<CurioAttributeModifiers.Entry> builder = ImmutableList.builderWithExpectedSize(this.modifiers.size() + 1);

      for (CurioAttributeModifiers.Entry attributemodifiers$entry : this.modifiers) {
         if (!attributemodifiers$entry.modifier.id().equals(attributeModifier.id())) {
            builder.add(attributemodifiers$entry);
         }
      }

      builder.add(new CurioAttributeModifiers.Entry(attribute, attributeModifier, slot));
      return new CurioAttributeModifiers(builder.build(), this.showInTooltip);
   }

   public void forEach(String slot, BiConsumer<ResourceLocation, AttributeModifier> consumer) {
      for (CurioAttributeModifiers.Entry attributemodifiers$entry : this.modifiers) {
         if (attributemodifiers$entry.slot.equals(slot)) {
            consumer.accept(attributemodifiers$entry.attribute, attributemodifiers$entry.modifier);
         }
      }
   }

   public static class Builder {
      private final com.google.common.collect.ImmutableList.Builder<CurioAttributeModifiers.Entry> entries = ImmutableList.builder();

      Builder() {
      }

      public CurioAttributeModifiers.Builder add(Holder<Attribute> attribute, AttributeModifier attributeModifier, String slot) {
         ResourceLocation rl;
         if (attribute.value() instanceof SlotAttribute wrapper) {
            rl = ResourceLocation.fromNamespaceAndPath("curios", wrapper.getIdentifier());
         } else {
            rl = ResourceLocation.parse(attribute.getRegisteredName());
         }

         this.entries.add(new CurioAttributeModifiers.Entry(rl, attributeModifier, slot));
         return this;
      }

      public CurioAttributeModifiers build() {
         return new CurioAttributeModifiers(this.entries.build(), true);
      }
   }

   public record Entry(ResourceLocation attribute, AttributeModifier modifier, String slot) {
      public static final Codec<CurioAttributeModifiers.Entry> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               ResourceLocation.CODEC.fieldOf("type").forGetter(CurioAttributeModifiers.Entry::attribute),
               AttributeModifier.MAP_CODEC.forGetter(CurioAttributeModifiers.Entry::modifier),
               Codec.STRING.optionalFieldOf("slot", "").forGetter(CurioAttributeModifiers.Entry::slot)
            )
            .apply(instance, CurioAttributeModifiers.Entry::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, CurioAttributeModifiers.Entry> STREAM_CODEC = StreamCodec.composite(
         ResourceLocation.STREAM_CODEC,
         CurioAttributeModifiers.Entry::attribute,
         AttributeModifier.STREAM_CODEC,
         CurioAttributeModifiers.Entry::modifier,
         ByteBufCodecs.STRING_UTF8,
         CurioAttributeModifiers.Entry::slot,
         CurioAttributeModifiers.Entry::new
      );
   }
}
