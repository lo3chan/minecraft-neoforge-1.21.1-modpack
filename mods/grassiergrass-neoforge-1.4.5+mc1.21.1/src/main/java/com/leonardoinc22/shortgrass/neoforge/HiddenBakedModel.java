/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.ItemOverrides
 *  net.minecraft.client.renderer.block.model.ItemTransforms
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.client.ChunkRenderTypeSet
 *  net.neoforged.neoforge.client.event.ModelEvent$ModifyBakingResult
 *  net.neoforged.neoforge.client.model.data.ModelData
 */
package com.leonardoinc22.shortgrass.neoforge;

import com.leonardoinc22.shortgrass.client.render.HiddenGrass;
import com.leonardoinc22.shortgrass.client.render.HiddenModelScope;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.data.ModelData;

public final class HiddenBakedModel
implements BakedModel {
    private final ResourceLocation blockId;
    private final BakedModel wrapped;

    public HiddenBakedModel(ResourceLocation blockId, BakedModel wrapped) {
        this.blockId = blockId;
        this.wrapped = wrapped;
    }

    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        HiddenGrass.clearOriginalModels();
        event.getModels().replaceAll((location, model) -> {
            if (HiddenGrass.canBeHidden(location) && !(model instanceof HiddenBakedModel)) {
                HiddenGrass.rememberOriginalModel(location, model);
                return new HiddenBakedModel(location.id(), (BakedModel)model);
            }
            return model;
        });
    }

    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
        if (HiddenModelScope.shouldExposeHiddenModels() || !HiddenGrass.shouldHide(this.blockId)) {
            return this.wrapped.getQuads(state, side, random);
        }
        return List.of();
    }

    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData modelData, RenderType renderType) {
        if (HiddenModelScope.shouldExposeHiddenModels() || !HiddenGrass.shouldHide(this.blockId)) {
            return this.wrapped.getQuads(state, side, random, modelData, renderType);
        }
        return List.of();
    }

    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData modelData) {
        if (HiddenModelScope.shouldExposeHiddenModels() || !HiddenGrass.shouldHide(this.blockId)) {
            return this.wrapped.getRenderTypes(state, random, modelData);
        }
        return ChunkRenderTypeSet.none();
    }

    public boolean useAmbientOcclusion() {
        return this.wrapped.useAmbientOcclusion();
    }

    public boolean isGui3d() {
        return this.wrapped.isGui3d();
    }

    public boolean usesBlockLight() {
        return this.wrapped.usesBlockLight();
    }

    public boolean isCustomRenderer() {
        return this.wrapped.isCustomRenderer();
    }

    public TextureAtlasSprite getParticleIcon() {
        return this.wrapped.getParticleIcon();
    }

    public TextureAtlasSprite getParticleIcon(ModelData modelData) {
        return this.wrapped.getParticleIcon(modelData);
    }

    public ItemTransforms getTransforms() {
        return this.wrapped.getTransforms();
    }

    public ItemOverrides getOverrides() {
        return this.wrapped.getOverrides();
    }
}

