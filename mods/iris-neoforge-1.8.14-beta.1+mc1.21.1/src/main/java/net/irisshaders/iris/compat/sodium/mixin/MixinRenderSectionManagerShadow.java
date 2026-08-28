/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  net.caffeinemc.mods.sodium.client.gl.device.CommandList
 *  net.caffeinemc.mods.sodium.client.render.chunk.RenderSection
 *  net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager
 *  net.caffeinemc.mods.sodium.client.render.chunk.TaskQueueType
 *  net.caffeinemc.mods.sodium.client.render.chunk.data.BuiltSectionInfo
 *  net.caffeinemc.mods.sodium.client.render.chunk.lists.SortedRenderLists
 *  net.caffeinemc.mods.sodium.client.render.chunk.region.RenderRegion
 *  net.caffeinemc.mods.sodium.client.render.chunk.region.RenderRegionManager
 *  net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.SortBehavior
 *  net.caffeinemc.mods.sodium.client.render.viewport.Viewport
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.SectionPos
 *  org.jetbrains.annotations.NotNull
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.compat.sodium.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.Map;
import net.caffeinemc.mods.sodium.client.gl.device.CommandList;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.TaskQueueType;
import net.caffeinemc.mods.sodium.client.render.chunk.data.BuiltSectionInfo;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import net.caffeinemc.mods.sodium.client.render.chunk.region.RenderRegion;
import net.caffeinemc.mods.sodium.client.render.chunk.region.RenderRegionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.SortBehavior;
import net.caffeinemc.mods.sodium.client.render.viewport.Viewport;
import net.irisshaders.iris.mixinterface.ShadowRenderRegion;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.SectionPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={RenderSectionManager.class})
public abstract class MixinRenderSectionManagerShadow {
    @Shadow(remap=false)
    @NotNull
    private SortedRenderLists renderLists;
    @Shadow(remap=false)
    @NotNull
    private Map<TaskQueueType, ArrayDeque<RenderSection>> taskLists;
    @Shadow
    private int lastUpdatedFrame;
    @Shadow
    @Final
    private RenderRegionManager regions;
    @Unique
    @NotNull
    private SortedRenderLists shadowRenderLists = SortedRenderLists.empty();
    @Unique
    @NotNull
    private Map<TaskQueueType, ArrayDeque<RenderSection>> shadowTaskLists = new EnumMap<TaskQueueType, ArrayDeque<RenderSection>>(TaskQueueType.class);
    private int lastUpdatedFrameShadow;
    @Unique
    private boolean shadowNeedsRenderListUpdate = true;
    @Unique
    private boolean renderListStateIsShadow = false;

    @Shadow
    protected abstract boolean isOutOfGraph(SectionPos var1);

    @Inject(method={"needsUpdate"}, at={@At(value="HEAD")}, remap=false)
    private void notifyChangedCamera(CallbackInfoReturnable<Boolean> cir) {
        this.shadowNeedsRenderListUpdate = true;
    }

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void create(ClientLevel level, int renderDistance, SortBehavior sortBehavior, CommandList commandList, CallbackInfo ci) {
        for (int var6 = 0; var6 < TaskQueueType.values().length; ++var6) {
            TaskQueueType type = TaskQueueType.values()[var6];
            this.shadowTaskLists.put(type, new ArrayDeque());
        }
    }

    @Redirect(remap=false, method={"finalizeRenderLists"}, at=@At(value="FIELD", target="Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;renderLists:Lnet/caffeinemc/mods/sodium/client/render/chunk/lists/SortedRenderLists;"))
    private void useShadowRenderList(RenderSectionManager instance, SortedRenderLists value) {
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            this.shadowRenderLists = value;
        } else {
            this.renderLists = value;
        }
    }

    @WrapMethod(method={"createTerrainRenderList"})
    private boolean updateShadowRenderLists(Camera camera, Viewport viewport, int frame, boolean spectator, Operation<Boolean> original) {
        if (!ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            if (this.renderListStateIsShadow) {
                for (RenderRegion region : this.regions.getLoadedRegions()) {
                    ((ShadowRenderRegion)region).swapToRegularRenderList();
                }
                this.renderListStateIsShadow = false;
            }
        } else if (this.shadowNeedsRenderListUpdate && !this.renderListStateIsShadow) {
            for (RenderRegion region : this.regions.getLoadedRegions()) {
                ((ShadowRenderRegion)region).swapToShadowRenderList();
            }
            this.renderListStateIsShadow = true;
        }
        return (Boolean)original.call(new Object[]{camera, viewport, frame, spectator});
    }

    @Inject(method={"updateSectionInfo"}, at={@At(value="HEAD")}, remap=false)
    private void updateSectionInfo(RenderSection render, BuiltSectionInfo info, CallbackInfoReturnable<Boolean> cir) {
        this.shadowNeedsRenderListUpdate = true;
    }

    @Inject(method={"onSectionRemoved"}, at={@At(value="HEAD")}, remap=false)
    private void onSectionRemoved(int x, int y, int z, CallbackInfo ci) {
        this.shadowNeedsRenderListUpdate = true;
    }

    @Redirect(remap=false, method={"createTerrainRenderList"}, at=@At(value="FIELD", target="Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;taskLists:Ljava/util/Map;"))
    private void useShadowTaskrList(RenderSectionManager instance, @NotNull Map<TaskQueueType, ArrayDeque<RenderSection>> value) {
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            this.shadowTaskLists = value;
        } else {
            this.taskLists = value;
        }
    }

    @Redirect(method={"createTerrainRenderList"}, at=@At(value="INVOKE", target="Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;isOutOfGraph(Lnet/minecraft/core/SectionPos;)Z"))
    private boolean iris$setOutOfGraph(RenderSectionManager instance, SectionPos pos) {
        return ShadowRenderingState.areShadowsCurrentlyBeingRendered() || this.isOutOfGraph(pos);
    }

    @Redirect(method={"getRenderLists", "getVisibleChunkCount", "renderLayer"}, at=@At(value="FIELD", target="Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;renderLists:Lnet/caffeinemc/mods/sodium/client/render/chunk/lists/SortedRenderLists;"), remap=false)
    private SortedRenderLists useShadowRenderList2(RenderSectionManager instance) {
        return ShadowRenderingState.areShadowsCurrentlyBeingRendered() ? this.shadowRenderLists : this.renderLists;
    }

    @Inject(method={"updateChunks"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void doNotUpdateDuringShadow(boolean updateImmediately, CallbackInfo ci) {
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            ci.cancel();
        }
    }

    @Inject(method={"uploadChunks"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void doNotUploadDuringShadow(CallbackInfo ci) {
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            ci.cancel();
        }
    }

    @Redirect(method={"resetRenderLists", "submitSectionTasks(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/executor/ChunkJobCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/estimation/UploadResourceBudget;Lnet/caffeinemc/mods/sodium/client/render/chunk/TaskQueueType;)V"}, at=@At(value="FIELD", target="Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;taskLists:Ljava/util/Map;"), remap=false)
    @NotNull
    private Map<TaskQueueType, ArrayDeque<RenderSection>> useShadowTaskList3(RenderSectionManager instance) {
        return ShadowRenderingState.areShadowsCurrentlyBeingRendered() ? this.shadowTaskLists : this.taskLists;
    }

    @Redirect(method={"resetRenderLists"}, at=@At(value="FIELD", target="Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;renderLists:Lnet/caffeinemc/mods/sodium/client/render/chunk/lists/SortedRenderLists;"), remap=false)
    private void useShadowRenderList3(RenderSectionManager instance, SortedRenderLists value) {
        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            this.shadowRenderLists = value;
        } else {
            this.renderLists = value;
        }
    }
}

