/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.world.entity.EntityType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package net.diebuddies.mixins;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.diebuddies.minecraft.EggItemRenderer;
import net.diebuddies.minecraft.EnderpearItemRenderer;
import net.diebuddies.minecraft.SnowballItemRenderer;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={EntityRenderDispatcher.class})
public class MixinEntityRenderDispatcher {
    @Shadow
    private Map<EntityType<?>, EntityRenderer<?>> renderers;

    @Inject(at={@At(value="TAIL")}, method={"onResourceManagerReload"}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void onResourceManagerReload(ResourceManager manager, CallbackInfo ci, EntityRendererProvider.Context context) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Map.Entry<EntityType<?>, EntityRenderer<?>> entry : this.renderers.entrySet()) {
            if (entry.getKey() == EntityType.SNOWBALL || entry.getKey() == EntityType.ENDER_PEARL || entry.getKey() == EntityType.EGG) continue;
            builder.put(entry.getKey(), entry.getValue());
        }
        builder.put((Object)EntityType.SNOWBALL, new SnowballItemRenderer(context));
        builder.put((Object)EntityType.ENDER_PEARL, new EnderpearItemRenderer(context));
        builder.put((Object)EntityType.EGG, new EggItemRenderer(context));
        this.renderers = builder.build();
        for (Map.Entry<EntityType<?>, EntityRenderer<?>> entry : this.renderers.entrySet()) {
            PhysicsMod.renderers.put(entry.getKey(), entry.getValue());
        }
        PhysicsMod.renderers.put(EntityType.PLAYER, null);
    }
}

