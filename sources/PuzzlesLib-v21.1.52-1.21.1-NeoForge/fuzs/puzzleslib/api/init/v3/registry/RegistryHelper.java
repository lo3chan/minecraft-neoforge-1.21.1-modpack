package fuzs.puzzleslib.api.init.v3.registry;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

@Deprecated
public final class RegistryHelper {
   private RegistryHelper() {
   }

   public static <T> Registry<T> findBuiltInRegistry(ResourceKey<? extends Registry<? super T>> registryKey) {
      Registry<T> registry = findNullableBuiltInRegistry(registryKey);
      Objects.requireNonNull(registry, () -> "registry for %s is null".formatted(registryKey));
      return registry;
   }

   @Nullable
   public static <T> Registry<T> findNullableBuiltInRegistry(ResourceKey<? extends Registry<? super T>> registryKey) {
      Objects.requireNonNull(registryKey, "registry key is null");
      return (Registry<T>)BuiltInRegistries.REGISTRY.get(registryKey);
   }

   public static <T> Registry<T> findGameRegistry(ResourceKey<? extends Registry<? super T>> registryKey) {
      Registry<T> registry = findNullableGameRegistry(registryKey);
      Objects.requireNonNull(registry, () -> "registry for %s is null".formatted(registryKey));
      return registry;
   }

   public static <T> Registry<T> findNullableGameRegistry(ResourceKey<? extends Registry<? super T>> registryKey) {
      Objects.requireNonNull(registryKey, "registry key is null");
      Optional<Registry<T>> registry = Optional.empty();
      MinecraftServer minecraftServer = CommonAbstractions.INSTANCE.getMinecraftServer();
      if (minecraftServer != null) {
         registry = minecraftServer.registryAccess().registry(registryKey);
      }

      if (registry.isEmpty()) {
         registry = Optional.ofNullable(findNullableBuiltInRegistry(registryKey));
      }

      return registry.orElse(null);
   }

   public static <T> Optional<ResourceKey<T>> getResourceKey(ResourceKey<? extends Registry<? super T>> registryKey, T object) {
      return getHolderReference(registryKey, object).map(Reference::key);
   }

   public static <T> Optional<ResourceKey<T>> getResourceKey(Registry<T> registry, T object) {
      return getHolderReference(registry, object).map(Reference::key);
   }

   public static <T> ResourceKey<T> getResourceKeyOrThrow(ResourceKey<? extends Registry<? super T>> registryKey, T object) {
      return getResourceKey(registryKey, object).orElseThrow(() -> new IllegalStateException("Missing object in " + registryKey + ": " + object));
   }

   public static <T> ResourceKey<T> getResourceKeyOrThrow(Registry<T> registry, T object) {
      return getResourceKey(registry, object).orElseThrow(() -> new IllegalStateException("Missing object in " + registry.key() + ": " + object));
   }

   public static <T> Optional<Reference<T>> getHolderReference(ResourceKey<? extends Registry<? super T>> registryKey, T object) {
      return Optional.ofNullable(getBuiltInRegistryHolder(object)).or(() -> {
         Registry<T> registry = findGameRegistry(registryKey);
         return registry.getResourceKey(object).flatMap(registry::getHolder);
      });
   }

   public static <T> Optional<Reference<T>> getHolderReference(Registry<T> registry, T object) {
      return Optional.ofNullable(getBuiltInRegistryHolder(object)).or(() -> registry.getResourceKey(object).flatMap(registry::getHolder));
   }

   public static <T> Reference<T> getHolderOrThrow(ResourceKey<? extends Registry<? super T>> registryKey, T object) {
      return getHolderReference(registryKey, object).orElseThrow(() -> new IllegalStateException("Missing object in " + registryKey + ": " + object));
   }

   public static <T> Reference<T> getHolderOrThrow(Registry<T> registry, T object) {
      return getHolderReference(registry, object).orElseThrow(() -> new IllegalStateException("Missing object in " + registry.key() + ": " + object));
   }

   public static <T> Holder<T> wrapAsHolder(ResourceKey<? extends Registry<? super T>> registryKey, T object) {
      return findGameRegistry(registryKey).wrapAsHolder(object);
   }

   public static <T> boolean is(TagKey<T> tagKey, T object) {
      Reference<T> holder = getBuiltInRegistryHolder(object);
      if (holder != null) {
         return holder.is(tagKey);
      } else {
         Registry<T> registry = findGameRegistry(tagKey.registry());
         return tagKey.isFor(registry.key()) && registry.wrapAsHolder(object).is(tagKey);
      }
   }

   @Nullable
   public static <T> Reference<T> getBuiltInRegistryHolder(T object) {
      return switch (object) {
         case Block block -> block.builtInRegistryHolder();
         case Item item -> item.builtInRegistryHolder();
         case EntityType<?> entityType -> entityType.builtInRegistryHolder();
         case Fluid fluid -> fluid.builtInRegistryHolder();
         case BlockEntityType<?> blockEntityType -> blockEntityType.builtInRegistryHolder();
         default -> null;
      };
   }
}
