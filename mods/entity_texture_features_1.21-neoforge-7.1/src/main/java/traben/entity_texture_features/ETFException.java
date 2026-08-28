/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package traben.entity_texture_features;

import net.minecraft.world.level.block.entity.BlockEntity;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class ETFException
extends RuntimeException {
    public ETFException(String message) {
        super(ETFException.amendMessage(message));
    }

    private static String amendMessage(String message) {
        ETFEntityRenderState entity = ETFRenderContext.getCurrentEntityState();
        return message + "\n----------------------\nETF context:\n - Entity = %s\n - EMF installed = %s\n----------------------\n".formatted(entity == null ? "null" : (entity.isBlockEntity() ? ((BlockEntity)entity.entity()).getType() : entity.entityType()), ETF.isThisModLoaded("entity_model_features"));
    }
}

