package cn.foggyhillside.ends_delight.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class EDModDamageTypes {
   public static final ResourceKey<DamageType> ENDERMAN_GRISTLE_TELEPORT = ResourceKey.create(
      Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("ends_delight", "enderman_gristle_teleport")
   );
}
