/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.DebugScreenOverlay
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.tr7zw.entityculling.mixin;

import dev.tr7zw.entityculling.EntityCullingModBase;
import java.text.DecimalFormat;
import java.util.List;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={DebugScreenOverlay.class})
public class DebugHudMixin {
    private int lastTickedEntities = 0;
    private int lastSkippedEntityTicks = 0;
    private final DecimalFormat entityCullingFormatter = new DecimalFormat("###.##");

    @Inject(method={"getGameInformation()Ljava/util/List;"}, at={@At(value="RETURN")})
    public List<String> getLeftText(CallbackInfoReturnable<List<String>> callback) {
        if (EntityCullingModBase.instance.tickedEntities != 0 || EntityCullingModBase.instance.skippedEntityTicks != 0) {
            this.lastTickedEntities = EntityCullingModBase.instance.tickedEntities;
            this.lastSkippedEntityTicks = EntityCullingModBase.instance.skippedEntityTicks;
            EntityCullingModBase.instance.tickedEntities = 0;
            EntityCullingModBase.instance.skippedEntityTicks = 0;
        }
        if (EntityCullingModBase.instance.config.disableF3) {
            return (List)callback.getReturnValue();
        }
        List list = (List)callback.getReturnValue();
        list.add("[Culling] Last pass: " + this.entityCullingFormatter.format(EntityCullingModBase.instance.cullTask.lastTime) + "ms/" + this.entityCullingFormatter.format(EntityCullingModBase.instance.lastTickTime) + "ms");
        if (!EntityCullingModBase.instance.config.skipBlockEntityCulling) {
            list.add("[Culling] Rendered Block Entities: " + EntityCullingModBase.instance.renderedBlockEntities + " Skipped: " + EntityCullingModBase.instance.skippedBlockEntities);
        }
        if (!EntityCullingModBase.instance.config.skipEntityCulling) {
            list.add("[Culling] Rendered Entities: " + EntityCullingModBase.instance.renderedEntities + " Skipped: " + EntityCullingModBase.instance.skippedEntities);
            list.add("[Culling] Ticked Entities: " + this.lastTickedEntities + " Skipped: " + this.lastSkippedEntityTicks);
        }
        EntityCullingModBase.instance.renderedBlockEntities = 0;
        EntityCullingModBase.instance.skippedBlockEntities = 0;
        EntityCullingModBase.instance.renderedEntities = 0;
        EntityCullingModBase.instance.skippedEntities = 0;
        return list;
    }
}

