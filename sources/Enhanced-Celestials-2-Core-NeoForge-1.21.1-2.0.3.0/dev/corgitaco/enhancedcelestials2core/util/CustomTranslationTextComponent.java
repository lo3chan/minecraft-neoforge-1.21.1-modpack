package dev.corgitaco.enhancedcelestials2core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class CustomTranslationTextComponent {
   public static final CustomTranslationTextComponent DEFAULT = new CustomTranslationTextComponent("", Style.EMPTY);
   public static final Codec<CustomTranslationTextComponent> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            Codec.STRING.fieldOf("key").forGetter(customTranslationTextComponent -> customTranslationTextComponent.translationKey),
            CodecUtil.STYLE_CODEC.optionalFieldOf("style", Style.EMPTY).forGetter(customTranslationTextComponent -> customTranslationTextComponent.style),
            new CodecUtil.LazyCodec(() -> CODEC)
               .listOf()
               .optionalFieldOf("args", new ArrayList())
               .forGetter(customTranslationTextComponent -> customTranslationTextComponent.args)
         )
         .apply(builder, CustomTranslationTextComponent::new)
   );
   private final String translationKey;
   private final Style style;
   private final List<CustomTranslationTextComponent> args;
   private final Component component;

   public CustomTranslationTextComponent(String translationKey, CustomTranslationTextComponent... args) {
      this(translationKey, Style.EMPTY, args);
   }

   public CustomTranslationTextComponent(String translationKey, Style style, CustomTranslationTextComponent... args) {
      this.translationKey = translationKey;
      this.style = style;
      this.args = Arrays.asList(args);
      this.component = Component.translatable(
            translationKey, Arrays.stream(args).map(customTranslationTextComponent -> customTranslationTextComponent.component).toArray()
         )
         .withStyle(style);
   }

   public CustomTranslationTextComponent(String translationKey, Style style, List<CustomTranslationTextComponent> args) {
      this(translationKey, style, args.toArray(new CustomTranslationTextComponent[0]));
   }

   public Component getComponent() {
      return this.component;
   }

   public String getKey() {
      return this.translationKey;
   }

   public Style getStyle() {
      return this.style;
   }
}
