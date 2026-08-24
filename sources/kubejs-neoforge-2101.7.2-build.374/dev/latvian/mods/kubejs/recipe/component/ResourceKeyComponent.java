package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

public record ResourceKeyComponent<T>(
   @Nullable RecipeComponentType<?> typeOverride, ResourceKey<? extends Registry<T>> registryKey, Codec<ResourceKey<T>> codec, TypeInfo typeInfo
) implements RecipeComponent<ResourceKey<T>> {
   public static final RecipeComponentType<ResourceKey<Level>> DIMENSION = RecipeComponentType.unit(
      KubeJS.id("dimension_resource_key"), type -> create(type, Registries.DIMENSION)
   );
   public static final RecipeComponentType<ResourceKey<LootTable>> LOOT_TABLE = RecipeComponentType.unit(
      KubeJS.id("loot_table_resource_key"), type -> create(type, Registries.LOOT_TABLE)
   );
   public static final RecipeComponentType<?> TYPE = RecipeComponentType.dynamic(
      KubeJS.id("resource_key"),
      RecordCodecBuilder.mapCodec(
         instance -> instance.group(KubeJSCodecs.REGISTRY_KEY_CODEC.fieldOf("registry").forGetter(ResourceKeyComponent::registryKey))
            .apply(instance, ResourceKeyComponent::of)
      )
   );

   private static <T> ResourceKeyComponent<T> create(@Nullable RecipeComponentType<?> typeOverride, ResourceKey<? extends Registry<T>> registryKey) {
      RegistryType<T> reg = RegistryType.ofKey(registryKey);
      return new ResourceKeyComponent<>(
         typeOverride,
         registryKey,
         ResourceKey.codec(registryKey),
         reg == null ? TypeInfo.of(ResourceKey.class) : TypeInfo.of(ResourceKey.class).withParams(new TypeInfo[]{reg.type()})
      );
   }

   private static ResourceKeyComponent<?> of(ResourceKey key) {
      if (key == Registries.DIMENSION) {
         return (ResourceKeyComponent<?>)DIMENSION.instance();
      } else {
         return key == Registries.LOOT_TABLE ? (ResourceKeyComponent)LOOT_TABLE.instance() : create(null, key);
      }
   }

   @Override
   public RecipeComponentType<?> type() {
      return this.typeOverride == null ? TYPE : this.typeOverride;
   }

   public ResourceKey<T> wrap(RecipeScriptContext cx, Object from) {
      return ResourceKey.create(this.registryKey, ID.mc(from));
   }

   @Override
   public String toString() {
      return this.typeOverride != null ? this.typeOverride.toString() : "resource_key<" + ID.reduce(this.registryKey.location()) + ">";
   }

   public String toString(OpsContainer ops, ResourceKey<T> value) {
      return value.location().toString();
   }
}
