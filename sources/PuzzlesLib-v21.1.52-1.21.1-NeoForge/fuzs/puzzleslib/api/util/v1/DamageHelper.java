package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

public final class DamageHelper {
   private DamageHelper() {
   }

   public static Holder<DamageType> lookup(Entity entity, ResourceKey<DamageType> resourceKey) {
      return LookupHelper.lookup(entity, Registries.DAMAGE_TYPE, resourceKey);
   }

   public static Holder<DamageType> lookup(LevelReader levelReader, ResourceKey<DamageType> resourceKey) {
      return LookupHelper.lookup(levelReader, Registries.DAMAGE_TYPE, resourceKey);
   }

   public static Holder<DamageType> lookup(Provider registries, ResourceKey<DamageType> resourceKey) {
      return LookupHelper.lookup(registries, Registries.DAMAGE_TYPE, resourceKey);
   }

   public static DamageSource damageSource(LevelReader level, ResourceKey<DamageType> damageType) {
      return damageSource(level, damageType, null, null);
   }

   public static DamageSource damageSource(LevelReader level, ResourceKey<DamageType> damageType, @Nullable Entity directEntity) {
      return damageSource(level, damageType, directEntity, directEntity);
   }

   public static DamageSource damageSource(LevelReader level, ResourceKey<DamageType> damageType, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
      return damageSource(level.registryAccess(), damageType, directEntity, causingEntity);
   }

   public static DamageSource damageSource(
      RegistryAccess registries, ResourceKey<DamageType> damageType, @Nullable Entity directEntity, @Nullable Entity causingEntity
   ) {
      return new DamageSource(lookup(registries, damageType), directEntity, causingEntity);
   }
}
