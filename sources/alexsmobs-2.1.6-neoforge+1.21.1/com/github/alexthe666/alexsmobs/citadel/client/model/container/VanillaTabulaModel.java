package com.github.alexthe666.alexsmobs.citadel.client.model.container;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.math.Transformation;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class VanillaTabulaModel implements UnbakedModel {
   private final TabulaModelContainer model;
   private final Material particle;
   private final Collection<Material> textures;
   private final ImmutableMap<ItemDisplayContext, Transformation> transforms;

   public VanillaTabulaModel(
      TabulaModelContainer model, Material particle, ImmutableList<Material> textures, ImmutableMap<ItemDisplayContext, Transformation> transforms
   ) {
      this.model = model;
      this.particle = particle;
      this.textures = textures;
      this.transforms = transforms;
   }

   public Collection<ResourceLocation> getDependencies() {
      return ImmutableList.of();
   }

   public void resolveParents(Function<ResourceLocation, UnbakedModel> p_119538_) {
   }

   @Nullable
   public BakedModel bake(ModelBaker p_250133_, Function<Material, TextureAtlasSprite> p_119535_, ModelState p_119536_) {
      return null;
   }
}
