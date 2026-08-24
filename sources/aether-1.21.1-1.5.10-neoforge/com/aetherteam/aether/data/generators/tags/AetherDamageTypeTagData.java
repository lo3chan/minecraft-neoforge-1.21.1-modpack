package com.aetherteam.aether.data.generators.tags;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.data.resources.registries.AetherDamageTypes;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class AetherDamageTypeTagData extends TagsProvider<DamageType> {
   public AetherDamageTypeTagData(PackOutput output, CompletableFuture<Provider> registries, ExistingFileHelper existingFileHelper) {
      super(output, Registries.DAMAGE_TYPE, registries, "aether", existingFileHelper);
   }

   protected void addTags(Provider provider) {
      this.tag(DamageTypeTags.BYPASSES_ARMOR).add(AetherDamageTypes.INEBRIATION);
      this.tag(DamageTypeTags.DAMAGES_HELMET).add(AetherDamageTypes.FLOATING_BLOCK);
      this.tag(DamageTypeTags.IS_FIRE).add(AetherDamageTypes.INCINERATION);
      this.tag(DamageTypeTags.IS_PROJECTILE)
         .add(
            new ResourceKey[]{
               AetherDamageTypes.CLOUD_CRYSTAL, AetherDamageTypes.FIRE_CRYSTAL, AetherDamageTypes.ICE_CRYSTAL, AetherDamageTypes.THUNDER_CRYSTAL
            }
         );
      this.tag(DamageTypeTags.NO_KNOCKBACK).add(AetherDamageTypes.INEBRIATION);
      this.tag(DamageTypeTags.PANIC_CAUSES)
         .add(
            new ResourceKey[]{
               AetherDamageTypes.CLOUD_CRYSTAL,
               AetherDamageTypes.CRUSH,
               AetherDamageTypes.FIRE_CRYSTAL,
               AetherDamageTypes.ICE_CRYSTAL,
               AetherDamageTypes.INCINERATION,
               AetherDamageTypes.THUNDER_CRYSTAL
            }
         );
      this.tag(AetherTags.DamageTypes.IS_COLD).add(AetherDamageTypes.ICE_CRYSTAL);
   }
}
