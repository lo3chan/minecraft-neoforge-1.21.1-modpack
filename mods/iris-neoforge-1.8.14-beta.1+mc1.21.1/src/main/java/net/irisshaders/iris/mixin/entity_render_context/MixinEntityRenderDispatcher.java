/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.objects.Object2IntFunction
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.monster.ZombieVillager
 *  net.minecraft.world.entity.player.Player
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.entity_render_context;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.irisshaders.iris.layer.BufferSourceWrapper;
import net.irisshaders.iris.layer.EntityRenderStateShard;
import net.irisshaders.iris.layer.OuterWrappedRenderType;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderDispatcher.class})
public class MixinEntityRenderDispatcher {
    @Unique
    private static final NamespacedId CURRENT_PLAYER = new NamespacedId("minecraft", "current_player");
    @Unique
    private static final NamespacedId CONVERTING_VILLAGER = new NamespacedId("minecraft", "zombie_villager_converting");
    @Unique
    private static final Object2ObjectMap<EntityType<?>, NamespacedId> ENTITY_IDS = new Object2ObjectOpenHashMap();

    @ModifyVariable(method={"render"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift=At.Shift.AFTER), allow=1, require=1, argsOnly=true)
    private MultiBufferSource iris$beginEntityRender(MultiBufferSource bufferSource, Entity entity) {
        ZombieVillager zombie;
        Object2IntFunction<NamespacedId> entityIds = WorldRenderingSettings.INSTANCE.getEntityIds();
        if (entityIds == null || !ImmediateState.isRenderingLevel) {
            return bufferSource;
        }
        int intId = entity instanceof ZombieVillager && (zombie = (ZombieVillager)entity).isConverting() && WorldRenderingSettings.INSTANCE.hasVillagerConversionId() ? entityIds.applyAsInt((Object)CONVERTING_VILLAGER) : (entity instanceof Player && Minecraft.getInstance().getCameraEntity() == entity ? (entityIds.containsKey((Object)CURRENT_PLAYER) ? entityIds.getInt((Object)CURRENT_PLAYER) : entityIds.applyAsInt((Object)((NamespacedId)ENTITY_IDS.computeIfAbsent((Object)entity.getType(), k -> {
            ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey((Object)entity.getType());
            return new NamespacedId(entityId.getNamespace(), entityId.getPath());
        })))) : entityIds.applyAsInt((Object)((NamespacedId)ENTITY_IDS.computeIfAbsent((Object)entity.getType(), k -> {
            ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey((Object)entity.getType());
            return new NamespacedId(entityId.getNamespace(), entityId.getPath());
        }))));
        CapturedRenderingState.INSTANCE.setCurrentEntity(intId);
        return new BufferSourceWrapper(bufferSource, renderType -> OuterWrappedRenderType.wrapExactlyOnce("iris:entity", renderType, EntityRenderStateShard.INSTANCE));
    }

    @Inject(method={"render"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V")})
    private void iris$endEntityRender(Entity entity, double x, double y, double z, float yaw, float tickDelta, PoseStack poseStack, MultiBufferSource bufferSource, int light, CallbackInfo ci) {
        CapturedRenderingState.INSTANCE.setCurrentEntity(0);
        CapturedRenderingState.INSTANCE.setCurrentRenderedItem(0);
    }
}

