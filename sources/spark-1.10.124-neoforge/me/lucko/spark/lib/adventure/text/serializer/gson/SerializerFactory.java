package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import java.util.UUID;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.option.OptionState;
import me.lucko.spark.lib.adventure.text.BlockNBTComponent;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.TranslationArgument;
import me.lucko.spark.lib.adventure.text.event.ClickEvent;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;
import me.lucko.spark.lib.adventure.text.format.Style;
import me.lucko.spark.lib.adventure.text.format.TextColor;
import me.lucko.spark.lib.adventure.text.format.TextDecoration;
import me.lucko.spark.lib.adventure.text.serializer.json.JSONOptions;
import org.jetbrains.annotations.Nullable;

final class SerializerFactory implements TypeAdapterFactory {
   static final Class<Key> KEY_TYPE = Key.class;
   static final Class<Component> COMPONENT_TYPE = Component.class;
   static final Class<Style> STYLE_TYPE = Style.class;
   static final Class<ClickEvent.Action> CLICK_ACTION_TYPE = ClickEvent.Action.class;
   static final Class<HoverEvent.Action> HOVER_ACTION_TYPE = HoverEvent.Action.class;
   static final Class<HoverEvent.ShowItem> SHOW_ITEM_TYPE = HoverEvent.ShowItem.class;
   static final Class<HoverEvent.ShowEntity> SHOW_ENTITY_TYPE = HoverEvent.ShowEntity.class;
   static final Class<String> STRING_TYPE = String.class;
   static final Class<TextColorWrapper> COLOR_WRAPPER_TYPE = TextColorWrapper.class;
   static final Class<TextColor> COLOR_TYPE = TextColor.class;
   static final Class<TextDecoration> TEXT_DECORATION_TYPE = TextDecoration.class;
   static final Class<BlockNBTComponent.Pos> BLOCK_NBT_POS_TYPE = BlockNBTComponent.Pos.class;
   static final Class<UUID> UUID_TYPE = UUID.class;
   static final Class<TranslationArgument> TRANSLATION_ARGUMENT_TYPE = TranslationArgument.class;
   private final OptionState features;
   private final me.lucko.spark.lib.adventure.text.serializer.json.LegacyHoverEventSerializer legacyHoverSerializer;

   SerializerFactory(
      final OptionState features, @Nullable final me.lucko.spark.lib.adventure.text.serializer.json.LegacyHoverEventSerializer legacyHoverSerializer
   ) {
      this.features = features;
      this.legacyHoverSerializer = legacyHoverSerializer;
   }

   public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
      Class<? super T> rawType = type.getRawType();
      if (COMPONENT_TYPE.isAssignableFrom(rawType)) {
         return (TypeAdapter<T>)ComponentSerializerImpl.create(this.features, gson);
      } else if (KEY_TYPE.isAssignableFrom(rawType)) {
         return (TypeAdapter<T>)KeySerializer.INSTANCE;
      } else if (STYLE_TYPE.isAssignableFrom(rawType)) {
         return (TypeAdapter<T>)StyleSerializer.create(this.legacyHoverSerializer, this.features, gson);
      } else if (CLICK_ACTION_TYPE.isAssignableFrom(rawType)) {
         return (TypeAdapter<T>)ClickEventActionSerializer.INSTANCE;
      } else if (HOVER_ACTION_TYPE.isAssignableFrom(rawType)) {
         return HoverEventActionSerializer.INSTANCE;
      } else if (SHOW_ITEM_TYPE.isAssignableFrom(rawType)) {
         return (TypeAdapter<T>)ShowItemSerializer.create(gson, this.features);
      } else if (SHOW_ENTITY_TYPE.isAssignableFrom(rawType)) {
         return (TypeAdapter<T>)ShowEntitySerializer.create(gson);
      } else if (COLOR_WRAPPER_TYPE.isAssignableFrom(rawType)) {
         return TextColorWrapper.Serializer.INSTANCE;
      } else if (COLOR_TYPE.isAssignableFrom(rawType)) {
         return (TypeAdapter<T>)(this.features.value(JSONOptions.EMIT_RGB) ? TextColorSerializer.INSTANCE : TextColorSerializer.DOWNSAMPLE_COLOR);
      } else if (TEXT_DECORATION_TYPE.isAssignableFrom(rawType)) {
         return (TypeAdapter<T>)TextDecorationSerializer.INSTANCE;
      } else if (BLOCK_NBT_POS_TYPE.isAssignableFrom(rawType)) {
         return (TypeAdapter<T>)BlockNBTComponentPosSerializer.INSTANCE;
      } else if (UUID_TYPE.isAssignableFrom(rawType)) {
         return (TypeAdapter<T>)UUIDSerializer.uuidSerializer(this.features);
      } else {
         return (TypeAdapter<T>)(TRANSLATION_ARGUMENT_TYPE.isAssignableFrom(rawType) ? TranslationArgumentSerializer.create(gson) : null);
      }
   }
}
