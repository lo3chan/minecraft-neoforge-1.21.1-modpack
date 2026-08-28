/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer
 *  net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.compat.sodium.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.irisshaders.iris.platform.IrisPlatformHelpers;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.sodium.terrain.VertexEncoderInterface;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={AbstractBlockRenderContext.class})
public class MixinAbstractBlockRenderContext {
    @Shadow
    protected BlockPos pos;
    @Shadow
    protected BlockAndTintGetter level;

    @Inject(method={"bufferDefaultModel"}, at={@At(value="INVOKE", target="Lnet/caffeinemc/mods/sodium/client/services/PlatformModelAccess;getQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/minecraft/client/renderer/RenderType;Lnet/caffeinemc/mods/sodium/client/services/SodiumModelData;)Ljava/util/List;")})
    private void checkDirectionNeo(BakedModel model, BlockState state, CallbackInfo ci, @Local Direction cullFace) {
        MixinAbstractBlockRenderContext mixinAbstractBlockRenderContext = this;
        if (mixinAbstractBlockRenderContext instanceof BlockRenderer) {
            BlockRenderer r = (BlockRenderer)mixinAbstractBlockRenderContext;
            if (WorldRenderingSettings.INSTANCE.getBlockStateIds() != null && cullFace != null) {
                BlockState appearance = IrisPlatformHelpers.getInstance().getBlockAppearance(this.level, state, cullFace, this.pos);
                ((VertexEncoderInterface)r).overrideBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt((Object)appearance));
            }
        }
    }

    @Inject(method={"bufferDefaultModel"}, at={@At(value="TAIL")})
    private void checkDirectionNeo(BakedModel model, BlockState state, CallbackInfo ci) {
        MixinAbstractBlockRenderContext mixinAbstractBlockRenderContext = this;
        if (mixinAbstractBlockRenderContext instanceof BlockRenderer) {
            BlockRenderer r = (BlockRenderer)mixinAbstractBlockRenderContext;
            if (WorldRenderingSettings.INSTANCE.getBlockStateIds() != null) {
                ((VertexEncoderInterface)r).restoreBlock();
            }
        }
    }
}

