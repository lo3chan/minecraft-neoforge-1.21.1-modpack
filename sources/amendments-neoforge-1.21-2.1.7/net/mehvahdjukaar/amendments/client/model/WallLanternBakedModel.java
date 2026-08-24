package net.mehvahdjukaar.amendments.client.model;

import java.util.ArrayList;
import java.util.List;
import net.mehvahdjukaar.amendments.client.WallLanternModelsManager;
import net.mehvahdjukaar.amendments.common.block.WallLanternBlock;
import net.mehvahdjukaar.amendments.common.tile.WallLanternBlockTile;
import net.mehvahdjukaar.amendments.reg.ModBlockProperties;
import net.mehvahdjukaar.moonlight.api.block.MimicBlock;
import net.mehvahdjukaar.moonlight.api.client.model.BakedQuadsTransformer;
import net.mehvahdjukaar.moonlight.api.client.model.CustomBakedModel;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData;
import net.mehvahdjukaar.moonlight.api.client.util.RotHlpr;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public class WallLanternBakedModel implements CustomBakedModel {
   private final BakedModel support;
   private final BlockModelShaper blockModelShaper;
   private final ModelState rotation;

   public WallLanternBakedModel(BakedModel support, ModelState state) {
      this.support = support;
      this.blockModelShaper = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
      this.rotation = state;
   }

   public List<BakedQuad> getBlockQuads(BlockState state, Direction side, RandomSource rand, RenderType renderType, ExtraModelData data) {
      List<BakedQuad> quads = new ArrayList<>();
      BlockState mimic = (BlockState)data.get(ModBlockProperties.MIMIC);
      List<BakedQuad> supportQuads = this.support.getQuads(state, side, rand);
      if (!supportQuads.isEmpty()) {
         quads.addAll(supportQuads);
      }

      boolean fancy = Boolean.TRUE.equals(data.get(WallLanternBlockTile.IS_FANCY));
      if (!fancy && mimic != null && !(mimic.getBlock() instanceof MimicBlock) && !mimic.isAir() && state != null) {
         BakedModel model = WallLanternModelsManager.getLanternModel(this.blockModelShaper, ((WallLanternBlock)state.getBlock()).type, mimic);
         List<BakedQuad> mimicQuads = model.getQuads(mimic, side, rand);
         if (!mimicQuads.isEmpty()) {
            Matrix4f mat = new Matrix4f();
            mat.mul(this.rotation.getRotation().getMatrix());
            mat.translate(0.0F, 0.125F, 0.125F);
            mat.rotate(RotHlpr.Y90);
            BakedQuadsTransformer transformer = BakedQuadsTransformer.create().applyingTransform(mat);
            quads.addAll(transformer.transformAll(mimicQuads));
         }
      }

      return quads;
   }

   public boolean useAmbientOcclusion() {
      return true;
   }

   public boolean isGui3d() {
      return false;
   }

   public boolean usesBlockLight() {
      return false;
   }

   public boolean isCustomRenderer() {
      return false;
   }

   public TextureAtlasSprite getBlockParticle(ExtraModelData data) {
      BlockState mimic = (BlockState)data.get(ModBlockProperties.MIMIC);
      if (mimic != null && !mimic.isAir()) {
         BakedModel model = this.blockModelShaper.getBlockModel(mimic);

         try {
            return model.getParticleIcon();
         } catch (Exception var5) {
         }
      }

      return this.support.getParticleIcon();
   }

   public ItemOverrides getOverrides() {
      return ItemOverrides.EMPTY;
   }

   public ItemTransforms getTransforms() {
      return ItemTransforms.NO_TRANSFORMS;
   }
}
