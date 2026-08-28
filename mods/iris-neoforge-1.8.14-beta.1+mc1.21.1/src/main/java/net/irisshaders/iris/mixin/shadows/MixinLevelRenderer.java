/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSection
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 */
package net.irisshaders.iris.mixin.shadows;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.irisshaders.iris.shadows.CullingDataCache;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer
implements CullingDataCache {
    @Shadow
    @Final
    @Mutable
    private ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections;
    @Unique
    private ObjectArrayList<SectionRenderDispatcher.RenderSection> savedRenderChunks = new ObjectArrayList(69696);
    @Shadow
    private double prevCamRotX;
    @Shadow
    private double prevCamRotY;
    @Unique
    private double savedLastCameraX;
    @Unique
    private double savedLastCameraY;
    @Unique
    private double savedLastCameraZ;
    @Unique
    private double savedLastCameraPitch;
    @Unique
    private double savedLastCameraYaw;

    @Override
    public void saveState() {
        this.swap();
    }

    @Override
    public void restoreState() {
        this.swap();
    }

    @Unique
    private void swap() {
        ObjectArrayList<SectionRenderDispatcher.RenderSection> tmpList = this.visibleSections;
        this.visibleSections = this.savedRenderChunks;
        this.savedRenderChunks = tmpList;
        double tmp = this.prevCamRotX;
        this.prevCamRotX = this.savedLastCameraPitch;
        this.savedLastCameraPitch = tmp;
        tmp = this.prevCamRotY;
        this.prevCamRotY = this.savedLastCameraYaw;
        this.savedLastCameraYaw = tmp;
    }
}

