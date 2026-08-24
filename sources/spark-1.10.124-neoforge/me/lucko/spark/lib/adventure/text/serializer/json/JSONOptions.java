package me.lucko.spark.lib.adventure.text.serializer.json;

import me.lucko.spark.lib.adventure.option.Option;
import me.lucko.spark.lib.adventure.option.OptionState;
import org.jetbrains.annotations.NotNull;

public final class JSONOptions {
   private static final int VERSION_INITIAL = 0;
   private static final int VERSION_1_16 = 2526;
   private static final int VERSION_1_20_3 = 3679;
   private static final int VERSION_1_20_5 = 3819;
   public static final Option<Boolean> EMIT_RGB = Option.booleanOption(key("emit/rgb"), true);
   public static final Option<JSONOptions.HoverEventValueMode> EMIT_HOVER_EVENT_TYPE = Option.enumOption(
      key("emit/hover_value_mode"), JSONOptions.HoverEventValueMode.class, JSONOptions.HoverEventValueMode.MODERN_ONLY
   );
   public static final Option<Boolean> EMIT_COMPACT_TEXT_COMPONENT = Option.booleanOption(key("emit/compact_text_component"), true);
   public static final Option<Boolean> EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY = Option.booleanOption(key("emit/hover_show_entity_id_as_int_array"), true);
   public static final Option<Boolean> VALIDATE_STRICT_EVENTS = Option.booleanOption(key("validate/strict_events"), true);
   public static final Option<Boolean> EMIT_DEFAULT_ITEM_HOVER_QUANTITY = Option.booleanOption(key("emit/default_item_hover_quantity"), true);
   public static final Option<JSONOptions.ShowItemHoverDataMode> SHOW_ITEM_HOVER_DATA_MODE = Option.enumOption(
      key("emit/show_item_hover_data"), JSONOptions.ShowItemHoverDataMode.class, JSONOptions.ShowItemHoverDataMode.EMIT_EITHER
   );
   private static final OptionState.Versioned BY_DATA_VERSION = OptionState.versionedOptionState()
      .version(
         0,
         b -> b.value(EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.LEGACY_ONLY)
            .value(EMIT_RGB, false)
            .value(EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, false)
            .value(VALIDATE_STRICT_EVENTS, false)
            .value(EMIT_DEFAULT_ITEM_HOVER_QUANTITY, false)
            .value(SHOW_ITEM_HOVER_DATA_MODE, JSONOptions.ShowItemHoverDataMode.EMIT_LEGACY_NBT)
      )
      .version(2526, b -> b.value(EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.MODERN_ONLY).value(EMIT_RGB, true))
      .version(3679, b -> b.value(EMIT_COMPACT_TEXT_COMPONENT, true).value(EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, true).value(VALIDATE_STRICT_EVENTS, true))
      .version(
         3819, b -> b.value(EMIT_DEFAULT_ITEM_HOVER_QUANTITY, true).value(SHOW_ITEM_HOVER_DATA_MODE, JSONOptions.ShowItemHoverDataMode.EMIT_DATA_COMPONENTS)
      )
      .build();
   private static final OptionState MOST_COMPATIBLE = OptionState.optionState()
      .value(EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.BOTH)
      .value(EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, false)
      .value(EMIT_COMPACT_TEXT_COMPONENT, false)
      .value(VALIDATE_STRICT_EVENTS, false)
      .value(SHOW_ITEM_HOVER_DATA_MODE, JSONOptions.ShowItemHoverDataMode.EMIT_EITHER)
      .build();

   private JSONOptions() {
   }

   private static String key(final String value) {
      return "adventure:json/" + value;
   }

   @NotNull
   public static OptionState.Versioned byDataVersion() {
      return BY_DATA_VERSION;
   }

   @NotNull
   public static OptionState compatibility() {
      return MOST_COMPATIBLE;
   }

   public static enum HoverEventValueMode {
      MODERN_ONLY,
      LEGACY_ONLY,
      BOTH;
   }

   public static enum ShowItemHoverDataMode {
      EMIT_LEGACY_NBT,
      EMIT_DATA_COMPONENTS,
      EMIT_EITHER;
   }
}
