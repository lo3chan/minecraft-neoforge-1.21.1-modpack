package dev.corgitaco.enhancedcelestials2defaultlunarevents.neoforge.datagen.providers;

import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.EnhancedCelestialsDefaultLunarEvents;
import dev.corgitaco.enhancedcelestials2defaultlunarevents.core.lunarevent.StandardLunarEvents;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ECLunarEventTagsProvider extends TagsProvider<LunarEvent> {
   public ECLunarEventTagsProvider(
      PackOutput pOutput,
      ResourceKey<? extends Registry<LunarEvent>> pRegistryKey,
      CompletableFuture<Provider> pLookupProvider,
      String modId,
      @Nullable ExistingFileHelper existingFileHelper
   ) {
      super(pOutput, pRegistryKey, pLookupProvider, modId, existingFileHelper);
   }

   protected void addTags(Provider pProvider) {
      this.tag(this.createTagKey("blood_moon")).add(new ResourceKey[]{StandardLunarEvents.BLOOD_MOON, StandardLunarEvents.SUPER_BLOOD_MOON});
      this.tag(this.createTagKey("blue_moon")).add(new ResourceKey[]{StandardLunarEvents.BLUE_MOON, StandardLunarEvents.SUPER_BLUE_MOON});
      this.tag(this.createTagKey("harvest_moon")).add(new ResourceKey[]{StandardLunarEvents.HARVEST_MOON, StandardLunarEvents.SUPER_HARVEST_MOON});
      this.tag(this.createTagKey("super_moon"))
         .add(
            new ResourceKey[]{
               StandardLunarEvents.SUPER_BLOOD_MOON,
               StandardLunarEvents.SUPER_BLUE_MOON,
               StandardLunarEvents.SUPER_HARVEST_MOON,
               StandardLunarEvents.SUPER_MOON
            }
         );
      this.tag(this.createTagKey("moon"))
         .add(new ResourceKey[]{StandardLunarEvents.BLOOD_MOON, StandardLunarEvents.BLUE_MOON, StandardLunarEvents.HARVEST_MOON});
   }

   public TagKey<LunarEvent> createTagKey(String path) {
      return TagKey.create(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY, EnhancedCelestialsDefaultLunarEvents.createLocation(path));
   }
}
