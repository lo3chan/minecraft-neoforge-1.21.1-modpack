package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.ScriptRuntime;
import dev.latvian.mods.rhino.type.EnumTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.RemappedEnumConstant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

public record EnumComponent<T extends Enum<T> & StringRepresentable>(@Nullable RecipeComponentType<?> typeOverride, EnumTypeInfo typeInfo, Codec<T> codec)
   implements RecipeComponent<T> {
   public static final RecipeComponentType<?> TYPE = RecipeComponentType.dynamic(
      KubeJS.id("enum"),
      RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               KubeJSCodecs.ENUM_TYPE_INFO
                  .fieldOf("enum")
                  .validate(
                     type -> StringRepresentable.class.isAssignableFrom(type.asClass())
                        ? DataResult.success(type)
                        : DataResult.error(() -> "Enum class " + type + " is not StringRepresentable!")
                  )
                  .forGetter(EnumComponent::typeInfo)
            )
            .apply(instance, EnumComponent::new)
      )
   );

   private EnumComponent(EnumTypeInfo typeInfo) {
      this(null, typeInfo, Codec.STRING.flatXmap(s -> {
         for (Object c : typeInfo.enumConstants()) {
            if (c instanceof RemappedEnumConstant r && r.getRemappedEnumConstantName().equalsIgnoreCase(s)) {
               return DataResult.success(c);
            }

            if (c instanceof Enum<?> e && e.name().equalsIgnoreCase(s)) {
               return DataResult.success(c);
            }
         }

         return DataResult.error(() -> "Enum value '" + s + "' of " + typeInfo.asClass().getName() + " not found");
      }, o -> DataResult.success(EnumTypeInfo.getName(o))));
   }

   public static <T extends Enum<T> & StringRepresentable> RecipeComponentType<T> of(ResourceLocation id, Class<T> enumClass, Codec<T> codec) {
      return RecipeComponentType.unit(id, type -> new EnumComponent<>(type, (EnumTypeInfo)TypeInfo.of(enumClass), codec));
   }

   public static <T extends Enum<T> & StringRepresentable> RecipeComponentType<T> of(ResourceLocation id, Class<T> enumClass) {
      return of(id, enumClass, StringRepresentable.fromEnum(enumClass::getEnumConstants));
   }

   @Override
   public RecipeComponentType<?> type() {
      return this.typeOverride == null ? TYPE : this.typeOverride;
   }

   public void buildUniqueId(UniqueIdBuilder builder, T value) {
      if (value instanceof RemappedEnumConstant r) {
         builder.append(r.getRemappedEnumConstantName());
      } else if (value instanceof Enum) {
         builder.append(value.name());
      } else {
         builder.append(value.toString());
      }
   }

   @Override
   public String toString() {
      return this.typeOverride != null ? this.typeOverride.toString() : "enum<" + this.typeInfo.asClass().getName() + ">";
   }

   public String toString(OpsContainer ops, T value) {
      return ScriptRuntime.escapeAndWrapString(value.getSerializedName());
   }
}
