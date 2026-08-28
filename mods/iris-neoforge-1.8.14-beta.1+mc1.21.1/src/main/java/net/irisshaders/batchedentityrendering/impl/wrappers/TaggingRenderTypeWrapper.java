/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.batchedentityrendering.impl.wrappers;

import java.util.Objects;
import java.util.Optional;
import net.irisshaders.batchedentityrendering.impl.BlendingStateHolder;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.irisshaders.batchedentityrendering.mixin.RenderTypeAccessor;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

public class TaggingRenderTypeWrapper
extends RenderType
implements WrappableRenderType,
BlendingStateHolder {
    private final int tag;
    private final RenderType wrapped;

    public TaggingRenderTypeWrapper(String name, RenderType wrapped, int tag) {
        super(name, wrapped.format(), wrapped.mode(), wrapped.bufferSize(), wrapped.affectsCrumbling(), TaggingRenderTypeWrapper.shouldSortOnUpload(wrapped), () -> ((RenderType)wrapped).setupRenderState(), () -> ((RenderType)wrapped).clearRenderState());
        this.tag = tag;
        this.wrapped = wrapped;
    }

    private static boolean shouldSortOnUpload(RenderType type) {
        return ((RenderTypeAccessor)type).shouldSortOnUpload();
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
        TaggingRenderTypeWrapper other = (TaggingRenderTypeWrapper)object;
        return this.tag == other.tag && Objects.equals(this.wrapped, other.wrapped);
    }

    public int hashCode() {
        return this.wrapped.hashCode() + this.tag + 1;
    }

    public String toString() {
        return "tagged(" + this.tag + "):" + this.wrapped.toString();
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

