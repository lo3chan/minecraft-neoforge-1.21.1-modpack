package dev.corgitaco.enhancedcelestials2core.neoforge.datagen.providers;

import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.core.lunarevent.DefaultLunarEvents;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ECLunarEventTagsProvider extends TagsProvider<LunarEvent> {
   private final boolean useMinecraftNameSpace;

   public ECLunarEventTagsProvider(
      PackOutput pOutput,
      boolean useMinecraftNameSpace,
      ResourceKey<? extends Registry<LunarEvent>> pRegistryKey,
      CompletableFuture<Provider> pLookupProvider,
      String modId,
      @Nullable ExistingFileHelper existingFileHelper
   ) {
      super(pOutput, pRegistryKey, pLookupProvider, modId, existingFileHelper);
      this.useMinecraftNameSpace = useMinecraftNameSpace;
   }

   protected void addTags(Provider pProvider) {
      this.tag(this.createTagKey("moon")).add(DefaultLunarEvents.DEFAULT);
   }

   public TagKey<LunarEvent> createTagKey(String path) {
      ResourceKey<Registry<LunarEvent>> lunarEventKey = this.useMinecraftNameSpace
         ? ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("lunar/event"))
         : EnhancedCelestialsRegistry.LUNAR_EVENT_KEY;
      return TagKey.create(lunarEventKey, EnhancedCelestials.createLocation(path));
   }

   public String getName() {
      return this.useMinecraftNameSpace ? "Fabric " + super.getName() : "Forge " + super.getName();
   }
}
