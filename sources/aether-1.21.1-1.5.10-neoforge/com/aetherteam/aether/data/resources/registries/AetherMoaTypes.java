package com.aetherteam.aether.data.resources.registries;

import com.aetherteam.aether.api.registers.MoaType;
import com.aetherteam.aether.item.AetherItems;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.SimpleWeightedRandomList.Builder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class AetherMoaTypes {
   public static final ResourceKey<Registry<MoaType>> MOA_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(
      ResourceLocation.fromNamespaceAndPath("aether", "moa_type")
   );
   public static final ResourceKey<MoaType> BLUE = createKey("blue");
   public static final ResourceKey<MoaType> WHITE = createKey("white");
   public static final ResourceKey<MoaType> BLACK = createKey("black");

   private static ResourceKey<MoaType> createKey(String name) {
      return ResourceKey.create(MOA_TYPE_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath("aether", name));
   }

   public static void bootstrap(BootstrapContext<MoaType> context) {
      context.register(
         BLUE,
         new MoaType(
            new ItemStack((ItemLike)AetherItems.BLUE_MOA_EGG.get()),
            3,
            0.155F,
            100,
            ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/blue_moa.png"),
            ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/moa_saddle.png"),
            Optional.empty()
         )
      );
      context.register(
         WHITE,
         new MoaType(
            new ItemStack((ItemLike)AetherItems.WHITE_MOA_EGG.get()),
            4,
            0.155F,
            50,
            ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/white_moa.png"),
            ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/moa_saddle.png"),
            Optional.empty()
         )
      );
      context.register(
         BLACK,
         new MoaType(
            new ItemStack((ItemLike)AetherItems.BLACK_MOA_EGG.get()),
            8,
            0.155F,
            25,
            ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/black_moa.png"),
            ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/black_moa_saddle.png"),
            Optional.empty()
         )
      );
   }

   @Nullable
   public static ResourceKey<MoaType> getResourceKey(RegistryAccess registryAccess, String location) {
      return getResourceKey(registryAccess, ResourceLocation.parse(location));
   }

   @Nullable
   public static ResourceKey<MoaType> getResourceKey(RegistryAccess registryAccess, ResourceLocation location) {
      MoaType moaType = getMoaType(registryAccess, location);
      return moaType != null ? (ResourceKey)registryAccess.registryOrThrow(MOA_TYPE_REGISTRY_KEY).getResourceKey(moaType).orElse(null) : null;
   }

   @Nullable
   public static ResourceKey<MoaType> getResourceKey(RegistryAccess registryAccess, MoaType moaType) {
      return (ResourceKey<MoaType>)registryAccess.registryOrThrow(MOA_TYPE_REGISTRY_KEY).getResourceKey(moaType).orElse(null);
   }

   @Nullable
   public static MoaType getMoaType(RegistryAccess registryAccess, String location) {
      return getMoaType(registryAccess, ResourceLocation.parse(location));
   }

   @Nullable
   public static MoaType getMoaType(RegistryAccess registryAccess, ResourceLocation location) {
      return (MoaType)registryAccess.registryOrThrow(MOA_TYPE_REGISTRY_KEY).get(location);
   }

   public static MoaType getWeightedChance(RegistryAccess registryAccess, RandomSource random) {
      Registry<MoaType> moaTypeRegistry = registryAccess.registryOrThrow(MOA_TYPE_REGISTRY_KEY);
      Builder<MoaType> weightedListBuilder = SimpleWeightedRandomList.builder();
      moaTypeRegistry.holders().forEach(moaTypex -> weightedListBuilder.add((MoaType)moaTypex.value(), ((MoaType)moaTypex.value()).spawnChance()));
      SimpleWeightedRandomList<MoaType> weightedList = weightedListBuilder.build();
      Optional<MoaType> moaType = weightedList.getRandomValue(random);
      return moaType.orElse((MoaType)moaTypeRegistry.get(BLUE));
   }
}
