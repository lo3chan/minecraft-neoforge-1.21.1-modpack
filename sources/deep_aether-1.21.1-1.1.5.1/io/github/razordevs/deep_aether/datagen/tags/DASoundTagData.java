package io.github.razordevs.deep_aether.datagen.tags;

import com.aetherteam.aether.AetherTags.SoundEvents;
import io.github.razordevs.deep_aether.init.DASounds;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class DASoundTagData extends TagsProvider<SoundEvent> {
   public DASoundTagData(PackOutput output, CompletableFuture<Provider> registries, ExistingFileHelper existingFileHelper) {
      super(output, Registries.SOUND_EVENT, registries, "deep_aether", existingFileHelper);
   }

   public void addTags(Provider provider) {
      this.tag(SoundEvents.BOSS_MUSIC).add(DASounds.MUSIC_BOSS_EOTS.getKey());
   }
}
