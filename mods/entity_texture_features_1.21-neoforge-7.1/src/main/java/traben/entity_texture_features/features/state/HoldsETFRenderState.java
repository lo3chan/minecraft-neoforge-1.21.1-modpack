/*
 * Decompiled with CFR 0.152.
 */
package traben.entity_texture_features.features.state;

import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public interface HoldsETFRenderState {
    public ETFEntityRenderState etf$getState();

    public void etf$initState(ETFEntity var1);
}

