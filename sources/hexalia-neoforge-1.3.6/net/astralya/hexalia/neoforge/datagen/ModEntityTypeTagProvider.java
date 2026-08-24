package net.astralya.hexalia.neoforge.datagen;

import java.util.concurrent.CompletableFuture;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ModEntityTypeTagProvider extends EntityTypeTagsProvider {
   public ModEntityTypeTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
      super(output, lookupProvider, "hexalia", existingFileHelper);
   }

   protected void addTags(Provider lookupProvider) {
      this.tag(ModTags.EntityTypes.SPIRITROOT_UNCAPTURABLE)
         .add(EntityType.ENDER_DRAGON)
         .add(EntityType.GHAST)
         .add(EntityType.WITHER)
         .add(EntityType.PLAYER)
         .add(EntityType.VILLAGER)
         .add(EntityType.WARDEN);
      this.tag(ModTags.EntityTypes.RABBAGE_IMMUNE).add(EntityType.BEE).add((EntityType)ModEntities.CACOFEY.get());
   }
}
