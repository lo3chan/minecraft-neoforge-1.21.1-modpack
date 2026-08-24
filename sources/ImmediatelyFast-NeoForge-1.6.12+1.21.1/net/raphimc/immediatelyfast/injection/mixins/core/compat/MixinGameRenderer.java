package net.raphimc.immediatelyfast.injection.mixins.core.compat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.raphimc.immediatelyfast.ImmediatelyFast;
import net.raphimc.immediatelyfast.compat.CoreShaderBlacklist;
import net.raphimc.immediatelyfast.feature.core.ImmediatelyFastResourcePackMetadata;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public abstract class MixinGameRenderer {
   @Shadow
   @Final
   private Map<String, ShaderInstance> shaders;

   @Inject(
      method = {"reloadShaders"},
      at = {@At("RETURN")}
   )
   private void checkForCoreShaderModifications(ResourceProvider factory, CallbackInfo ci) {
      if (!ImmediatelyFast.config.experimental_disable_resource_pack_conflict_handling) {
         PackResources resourcePackWhichBreaksFontAtlasResizing = null;
         PackResources resourcePackWhichBreaksHudBatching = null;
         PackResources resourcePackWhichBreaksScreenBatching = null;

         try {
            Set<PackResources> breakingResourcePacks = new HashSet<>();

            for (Entry<String, ShaderInstance> shaderProgramEntry : this.shaders.entrySet()) {
               if (CoreShaderBlacklist.isBlacklisted(shaderProgramEntry.getKey())) {
                  ResourceLocation vertexShaderIdentifier = ResourceLocation.parse(
                     "shaders/core/" + shaderProgramEntry.getValue().getVertexProgram().getName() + ".vsh"
                  );
                  PackResources vertexShaderResourcePack = factory.getResource(vertexShaderIdentifier).<PackResources>map(Resource::source).orElse(null);
                  if (vertexShaderResourcePack != null && !vertexShaderResourcePack.equals(Minecraft.getInstance().getVanillaPackResources())) {
                     breakingResourcePacks.add(vertexShaderResourcePack);
                  }

                  ResourceLocation fragmentShaderIdentifier = ResourceLocation.parse(
                     "shaders/core/" + shaderProgramEntry.getValue().getFragmentProgram().getName() + ".fsh"
                  );
                  PackResources fragmentShaderResourcePack = factory.getResource(fragmentShaderIdentifier).<PackResources>map(Resource::source).orElse(null);
                  if (fragmentShaderResourcePack != null && !fragmentShaderResourcePack.equals(Minecraft.getInstance().getVanillaPackResources())) {
                     breakingResourcePacks.add(fragmentShaderResourcePack);
                  }
               }
            }

            for (PackResources resourcePack : breakingResourcePacks) {
               ImmediatelyFastResourcePackMetadata metadata = (ImmediatelyFastResourcePackMetadata)resourcePack.getMetadataSection(
                  ImmediatelyFastResourcePackMetadata.SERIALIZER
               );
               if (metadata == null) {
                  metadata = ImmediatelyFastResourcePackMetadata.DEFAULT;
               }

               if (!metadata.compatibleFeatures().contains("font_atlas_resizing")) {
                  resourcePackWhichBreaksFontAtlasResizing = resourcePack;
               }

               if (!metadata.compatibleFeatures().contains("hud_batching")) {
                  resourcePackWhichBreaksHudBatching = resourcePack;
               }

               if (!metadata.compatibleFeatures().contains("experimental_screen_batching")) {
                  resourcePackWhichBreaksScreenBatching = resourcePack;
               }
            }
         } catch (IOException var13) {
            ImmediatelyFast.LOGGER.error("Failed to check for core shader modifications", var13);
         }

         if (ImmediatelyFast.config.font_atlas_resizing) {
            if (resourcePackWhichBreaksFontAtlasResizing != null) {
               ImmediatelyFast.LOGGER
                  .warn(
                     "Resource pack "
                        + resourcePackWhichBreaksFontAtlasResizing.packId()
                        + " is not compatible with font atlas resizing. Temporarily disabling font atlas resizing."
                  );
               if (ImmediatelyFast.runtimeConfig.font_atlas_resizing) {
                  ImmediatelyFast.runtimeConfig.font_atlas_resizing = false;
                  this.immediatelyFast$reloadFontStorages();
               }
            } else if (!ImmediatelyFast.runtimeConfig.font_atlas_resizing) {
               ImmediatelyFast.LOGGER.info("Re-enabling font atlas resizing because no incompatible resource packs are loaded.");
               ImmediatelyFast.runtimeConfig.font_atlas_resizing = true;
               this.immediatelyFast$reloadFontStorages();
            }
         }

         if (ImmediatelyFast.config.hud_batching) {
            if (resourcePackWhichBreaksHudBatching != null) {
               ImmediatelyFast.LOGGER
                  .warn(
                     "Resource pack "
                        + resourcePackWhichBreaksHudBatching.packId()
                        + " is not compatible with HUD batching. Temporarily disabling HUD batching."
                  );
               ImmediatelyFast.runtimeConfig.hud_batching = false;
            } else if (!ImmediatelyFast.runtimeConfig.hud_batching) {
               ImmediatelyFast.LOGGER.info("Re-enabling HUD batching because no incompatible resource packs are loaded.");
               ImmediatelyFast.runtimeConfig.hud_batching = true;
            }
         }

         if (ImmediatelyFast.config.experimental_screen_batching) {
            if (resourcePackWhichBreaksScreenBatching != null) {
               ImmediatelyFast.LOGGER
                  .warn(
                     "Resource pack "
                        + resourcePackWhichBreaksScreenBatching.packId()
                        + " is not compatible with experimental screen batching. Temporarily disabling experimental screen batching."
                  );
               ImmediatelyFast.runtimeConfig.experimental_screen_batching = false;
            } else if (!ImmediatelyFast.runtimeConfig.experimental_screen_batching) {
               ImmediatelyFast.LOGGER.info("Re-enabling experimental screen batching because no incompatible resource packs are loaded.");
               ImmediatelyFast.runtimeConfig.experimental_screen_batching = true;
            }
         }
      }
   }

   @Unique
   private void immediatelyFast$reloadFontStorages() {
      Minecraft.getInstance().fontManager.updateOptions(Minecraft.getInstance().options);
   }
}
