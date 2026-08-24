package fuzs.puzzleslib.api.entity.v1;

import fuzs.puzzleslib.api.util.v1.DamageHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

@Deprecated
public final class DamageSourcesHelper {
   private DamageSourcesHelper() {
   }

   public static DamageSource source(LevelReader level, ResourceKey<DamageType> damageType) {
      return DamageHelper.damageSource(level, damageType);
   }

   public static DamageSource source(LevelReader level, ResourceKey<DamageType> damageType, @Nullable Entity directEntity) {
      return DamageHelper.damageSource(level, damageType, directEntity);
   }

   public static DamageSource source(LevelReader level, ResourceKey<DamageType> damageType, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
      return DamageHelper.damageSource(level, damageType, directEntity, causingEntity);
   }

   public static DamageSource source(
      RegistryAccess registryAccess, ResourceKey<DamageType> damageType, @Nullable Entity directEntity, @Nullable Entity causingEntity
   ) {
      return DamageHelper.damageSource(registryAccess, damageType, directEntity, causingEntity);
   }
}
