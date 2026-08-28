/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.MeshData
 *  net.minecraft.client.renderer.RenderStateShard
 *  net.minecraft.client.renderer.RenderType
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.layer;

import com.mojang.blaze3d.vertex.MeshData;
import java.util.Objects;
import java.util.Optional;
import net.irisshaders.batchedentityrendering.impl.BlendingStateHolder;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.irisshaders.iris.mixin.rendertype.RenderTypeAccessor;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

public class InnerWrappedRenderType
extends RenderType
implements WrappableRenderType,
BlendingStateHolder {
    private final RenderStateShard extra;
    private final RenderType wrapped;

    public InnerWrappedRenderType(String name, RenderType wrapped, RenderStateShard extra) {
        super(name, wrapped.format(), wrapped.mode(), wrapped.bufferSize(), wrapped.affectsCrumbling(), InnerWrappedRenderType.shouldSortOnUpload(wrapped), () -> ((RenderType)wrapped).setupRenderState(), () -> ((RenderType)wrapped).clearRenderState());
        this.extra = extra;
        this.wrapped = wrapped;
    }

    public static InnerWrappedRenderType wrapExactlyOnce(String name, RenderType wrapped, RenderStateShard extra) {
        if (wrapped instanceof InnerWrappedRenderType) {
            wrapped = ((InnerWrappedRenderType)wrapped).unwrap();
        }
        return new InnerWrappedRenderType(name, wrapped, extra);
    }

    private static boolean shouldSortOnUpload(RenderType type) {
        return ((RenderTypeAccessor)type).shouldSortOnUpload();
    }

    public void setupRenderState() {
        super.setupRenderState();
        this.extra.setupRenderState();
    }

    public void clearRenderState() {
        this.extra.clearRenderState();
        super.clearRenderState();
    }

    @Override
    public RenderType unwrap() {
        return this.wrapped;
    }

    public Optional<RenderType> outline() {
        return this.wrapped.outline();
    }

    public boolean isOutline() {
        return this.wrapped.isOutline();
    }

    public boolean equals(@Nullable Object object) {
        if (object == null) {
            return false;
        }
        if (object.getClass() != this.getClass()) {
            return false;
        }
        InnerWrappedRenderType other = (InnerWrappedRenderType)object;
        return Objects.equals(this.wrapped, other.wrapped) && Objects.equals(this.extra, other.extra);
    }

    public int hashCode() {
        return this.wrapped.hashCode() + 2;
    }

    public String toString() {
        return "iris_wrapped:" + this.wrapped.toString();
    }

    public void draw(MeshData meshData) {
        this.wrapped.draw(meshData);
    }

    @Override
    public TransparencyType getTransparencyType() {
        return ((BlendingStateHolder)this.wrapped).getTransparencyType();
    }

    @Override
    public void setTransparencyType(TransparencyType transparencyType) {
        ((BlendingStateHolder)this.wrapped).setTransparencyType(transparencyType);
    }
}

