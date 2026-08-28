/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.v2.WrapWithCondition
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSection
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 */
package net.irisshaders.iris.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.Set;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer_SkipRendering {
    @Unique
    private static final ObjectArrayList<SectionRenderDispatcher.RenderSection> EMPTY_LIST = new ObjectArrayList();

    @WrapWithCondition(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;setupRender(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;ZZ)V")})
    private boolean skipSetupRender(LevelRenderer instance, Camera camera, Frustum frustum, boolean bl, boolean bl2) {
        WorldRenderingPipeline worldRenderingPipeline = Iris.getPipelineManager().getPipelineNullable();
        if (worldRenderingPipeline instanceof IrisRenderingPipeline) {
            IrisRenderingPipeline pipeline = (IrisRenderingPipeline)worldRenderingPipeline;
            return !pipeline.skipAllRendering();
        }
        return true;
    }

    @WrapWithCondition(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderSectionLayer(Lnet/minecraft/client/renderer/RenderType;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V")})
    private boolean skipRenderChunks(LevelRenderer instance, RenderType renderType, double d, double e, double f, Matrix4f matrix4f, Matrix4f matrix4f2) {
        WorldRenderingPipeline worldRenderingPipeline = Iris.getPipelineManager().getPipelineNullable();
        if (worldRenderingPipeline instanceof IrisRenderingPipeline) {
            IrisRenderingPipeline pipeline = (IrisRenderingPipeline)worldRenderingPipeline;
            return !pipeline.skipAllRendering();
        }
        return true;
    }

    @WrapOperation(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;")})
    private Iterable<Entity> skipRenderEntities(ClientLevel instance, Operation<Iterable<Entity>> original) {
        IrisRenderingPipeline pipeline;
        WorldRenderingPipeline worldRenderingPipeline = Iris.getPipelineManager().getPipelineNullable();
        if (worldRenderingPipeline instanceof IrisRenderingPipeline && (pipeline = (IrisRenderingPipeline)worldRenderingPipeline).skipAllRendering()) {
            return Collections.emptyList();
        }
        return (Iterable)original.call(new Object[]{instance});
    }

    @WrapOperation(method={"renderLevel"}, at={@At(value="FIELD", target="Lnet/minecraft/client/renderer/LevelRenderer;visibleSections:Lit/unimi/dsi/fastutil/objects/ObjectArrayList;")})
    private ObjectArrayList<SectionRenderDispatcher.RenderSection> skipLocalBlockEntities(LevelRenderer instance, Operation<ObjectArrayList<SectionRenderDispatcher.RenderSection>> original) {
        IrisRenderingPipeline pipeline;
        WorldRenderingPipeline worldRenderingPipeline = Iris.getPipelineManager().getPipelineNullable();
        if (worldRenderingPipeline instanceof IrisRenderingPipeline && (pipeline = (IrisRenderingPipeline)worldRenderingPipeline).skipAllRendering()) {
            return EMPTY_LIST;
        }
        return (ObjectArrayList)original.call(new Object[]{instance});
    }

    @WrapOperation(method={"renderLevel"}, at={@At(value="FIELD", target="Lnet/minecraft/client/renderer/LevelRenderer;globalBlockEntities:Ljava/util/Set;")})
    private Set<BlockEntity> skipGlobalBlockEntities(LevelRenderer instance, Operation<Set<BlockEntity>> original) {
        IrisRenderingPipeline pipeline;
        WorldRenderingPipeline worldRenderingPipeline = Iris.getPipelineManager().getPipelineNullable();
        if (worldRenderingPipeline instanceof IrisRenderingPipeline && (pipeline = (IrisRenderingPipeline)worldRenderingPipeline).skipAllRendering()) {
            return Collections.emptySet();
        }
        return (Set)original.call(new Object[]{instance});
    }
}

