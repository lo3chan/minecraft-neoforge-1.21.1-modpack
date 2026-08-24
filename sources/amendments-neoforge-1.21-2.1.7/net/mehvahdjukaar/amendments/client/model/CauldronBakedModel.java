package net.mehvahdjukaar.amendments.client.model;

import java.util.ArrayList;
import java.util.List;
import net.mehvahdjukaar.amendments.AmendmentsClient;
import net.mehvahdjukaar.amendments.common.block.ModCauldronBlock;
import net.mehvahdjukaar.amendments.common.tile.LiquidCauldronBlockTile;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.moonlight.api.client.model.BakedQuadsTransformer;
import net.mehvahdjukaar.moonlight.api.client.model.CustomBakedModel;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData;
import net.mehvahdjukaar.moonlight.api.fluids.MLBuiltinSoftFluids;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidRegistry;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class CauldronBakedModel implements CustomBakedModel {
   private static final boolean SINGLE_PASS = PlatHelper.getPlatform().isFabric();
   private final BakedModel cauldron;
   private final BakedModel fluid;
   private final ModelState transform;
   private final boolean hasTranslucent;

   public CauldronBakedModel(BakedModel c, BakedModel fluid, ModelState transform, boolean translucent) {
      this.cauldron = c;
      this.fluid = fluid;
      this.transform = transform;
      this.hasTranslucent = translucent && !SINGLE_PASS;
   }

   public List<BakedQuad> getBlockQuads(BlockState state, Direction direction, RandomSource randomSource, RenderType renderType, ExtraModelData extraModelData) {
      List<BakedQuad> quads = new ArrayList<>();
      boolean isTranslucentLayer = renderType == RenderType.translucent();
      if (!this.hasTranslucent || !isTranslucentLayer) {
         quads.addAll(this.cauldron.getQuads(state, direction, randomSource));
      }

      if (!this.hasTranslucent || isTranslucentLayer) {
         BakedQuadsTransformer transformer = BakedQuadsTransformer.create();
         List<BakedQuad> liquidQuads = this.fluid.getQuads(state, direction, randomSource);
         ClientLevel level = Minecraft.getInstance().level;
         if (liquidQuads.isEmpty() || level == null) {
            return quads;
         }

         RegistryAccess access = level.registryAccess();
         ResourceKey<SoftFluid> fluidKey = (ResourceKey<SoftFluid>)extraModelData.get(LiquidCauldronBlockTile.FLUID);
         if (fluidKey != null) {
            SoftFluid fluid = (SoftFluid)SoftFluidRegistry.get(access).get(fluidKey);
            Boolean glowing = (Boolean)extraModelData.get(LiquidCauldronBlockTile.GLOWING);
            if (glowing == null) {
               glowing = false;
            }

            if (fluid != null && !MLBuiltinSoftFluids.EMPTY.is(fluidKey)) {
               ResourceLocation stillTexture = fluid.getStillTexture();
               if (ClientConfigs.POTION_TEXTURE.get() && MLBuiltinSoftFluids.POTION.is(fluidKey)) {
                  stillTexture = AmendmentsClient.POTION_TEXTURE;
               } else if (MLBuiltinSoftFluids.MUSHROOM_STEW.is(fluidKey)) {
                  stillTexture = AmendmentsClient.MUSHROOM_STEW;
               } else if (MLBuiltinSoftFluids.BEETROOT_SOUP.is(fluidKey)) {
                  stillTexture = AmendmentsClient.BEETROOT_SOUP;
               } else if (MLBuiltinSoftFluids.RABBIT_STEW.is(fluidKey)) {
                  stillTexture = AmendmentsClient.RABBIT_STEW;
               } else if (MLBuiltinSoftFluids.SUS_STEW.is(fluidKey)) {
                  stillTexture = AmendmentsClient.SUS_STEW;
               }

               TextureAtlasSprite sprite = ClientHelper.getBlockMaterial(stillTexture).sprite();
               transformer.applyingAmbientOcclusion(false).applyingEmissivity(Math.max(glowing ? 14 : 0, fluid.getEmissivity())).applyingSprite(sprite);
               quads.addAll(transformer.transformAll(liquidQuads));
            }
         } else if (!(state.getBlock() instanceof ModCauldronBlock)) {
            transformer.applyingAmbientOcclusion(false);
            quads.addAll(transformer.transformAll(liquidQuads));
         }
      }

      return quads;
   }

   public TextureAtlasSprite getBlockParticle(ExtraModelData extraModelData) {
      return this.cauldron.getParticleIcon();
   }

   public boolean useAmbientOcclusion() {
      return true;
   }

   public boolean isGui3d() {
      return true;
   }

   public boolean usesBlockLight() {
      return true;
   }

   public boolean isCustomRenderer() {
      return false;
   }

   public ItemTransforms getTransforms() {
      return ItemTransforms.NO_TRANSFORMS;
   }

   public ItemOverrides getOverrides() {
      return ItemOverrides.EMPTY;
   }
}
