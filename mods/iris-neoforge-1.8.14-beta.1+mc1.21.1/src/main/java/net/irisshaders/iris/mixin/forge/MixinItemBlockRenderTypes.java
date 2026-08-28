/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.client.ChunkRenderTypeSet
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin.forge;

import net.irisshaders.iris.shaderpack.materialmap.BlockMaterialMapping;
import net.irisshaders.iris.shaderpack.materialmap.BlockRenderType;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ItemBlockRenderTypes.class})
public class MixinItemBlockRenderTypes {
    @Unique
    private static final ChunkRenderTypeSet[] LAYER_SET = new ChunkRenderTypeSet[BlockRenderType.values().length];

    @Inject(method={"getRenderLayers"}, at={@At(value="HEAD")}, cancellable=true)
    private static void iris$setCustomRenderType(BlockState arg, CallbackInfoReturnable<ChunkRenderTypeSet> cir) {
        BlockRenderType type = WorldRenderingSettings.INSTANCE.getBlockTypeIds().get(arg.getBlock());
        if (type != null) {
            cir.setReturnValue((Object)LAYER_SET[type.ordinal()]);
        }
    }

    static {
        for (int i = 0; i < BlockRenderType.values().length; ++i) {
            MixinItemBlockRenderTypes.LAYER_SET[i] = ChunkRenderTypeSet.of((RenderType[])new RenderType[]{BlockMaterialMapping.convertBlockToRenderType(BlockRenderType.values()[i])});
        }
    }
}

