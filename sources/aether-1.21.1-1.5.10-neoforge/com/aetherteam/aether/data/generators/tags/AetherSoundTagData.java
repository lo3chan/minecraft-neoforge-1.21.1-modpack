package com.aetherteam.aether.data.generators.tags;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.client.AetherSoundEvents;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class AetherSoundTagData extends TagsProvider<SoundEvent> {
   public AetherSoundTagData(PackOutput output, CompletableFuture<Provider> registries, ExistingFileHelper existingFileHelper) {
      super(output, Registries.SOUND_EVENT, registries, "aether", existingFileHelper);
   }

   protected void addTags(Provider provider) {
      this.tag(AetherTags.SoundEvents.PORTAL_SOUNDS)
         .add(
            new ResourceKey[]{
               AetherSoundEvents.BLOCK_AETHER_PORTAL_AMBIENT.getKey(),
               AetherSoundEvents.BLOCK_AETHER_PORTAL_TRIGGER.getKey(),
               AetherSoundEvents.BLOCK_AETHER_PORTAL_TRAVEL.getKey()
            }
         );
      this.tag(AetherTags.SoundEvents.AMBIENT_PORTAL_SOUNDS).add(AetherSoundEvents.BLOCK_AETHER_PORTAL_AMBIENT.getKey());
      this.tag(AetherTags.SoundEvents.ACTIVATED_PORTAL_SOUNDS)
         .add(new ResourceKey[]{AetherSoundEvents.BLOCK_AETHER_PORTAL_TRIGGER.getKey(), AetherSoundEvents.BLOCK_AETHER_PORTAL_TRAVEL.getKey()});
      this.tag(AetherTags.SoundEvents.ACHIEVEMENT_SOUNDS)
         .add(
            new ResourceKey[]{
               AetherSoundEvents.UI_TOAST_AETHER_GENERAL.getKey(),
               AetherSoundEvents.UI_TOAST_AETHER_BRONZE.getKey(),
               AetherSoundEvents.UI_TOAST_AETHER_SILVER.getKey(),
               AetherSoundEvents.UI_TOAST_AETHER_GOLD.getKey()
            }
         );
      this.tag(AetherTags.SoundEvents.BOSS_MUSIC)
         .add(
            new ResourceKey[]{
               AetherSoundEvents.MUSIC_BOSS_SLIDER.getKey(),
               AetherSoundEvents.MUSIC_BOSS_VALKYRIE_QUEEN.getKey(),
               AetherSoundEvents.MUSIC_BOSS_SUN_SPIRIT.getKey()
            }
         );
   }
}
