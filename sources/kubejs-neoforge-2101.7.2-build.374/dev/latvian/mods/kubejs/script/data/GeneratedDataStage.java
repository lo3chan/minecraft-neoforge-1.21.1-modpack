package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.event.EventTargetType;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.StringRepresentable;

public enum GeneratedDataStage implements StringRepresentable {
   INTERNAL("internal", "Internal"),
   REGISTRIES("registries", "Registries"),
   BEFORE_MODS("before_mods", "Before Mods"),
   AFTER_MODS("after_mods", "After Mods"),
   LAST("last", "Last");

   public static final GeneratedDataStage[] FOR_SCRIPTS = new GeneratedDataStage[]{AFTER_MODS, BEFORE_MODS, LAST};
   public static final EventTargetType<GeneratedDataStage> TARGET = EventTargetType.fromEnum(GeneratedDataStage.class);
   public final String name;
   public final String displayName;

   public static <T> Map<GeneratedDataStage, T> forScripts(Function<GeneratedDataStage, T> factory) {
      Object2ObjectArrayMap<GeneratedDataStage, T> map = new Object2ObjectArrayMap(FOR_SCRIPTS.length);

      for (GeneratedDataStage stage : FOR_SCRIPTS) {
         map.put(stage, factory.apply(stage));
      }

      return map;
   }

   private GeneratedDataStage(String name, String displayName) {
      this.name = name;
      this.displayName = displayName;
   }

   public String getSerializedName() {
      return this.name;
   }
}
