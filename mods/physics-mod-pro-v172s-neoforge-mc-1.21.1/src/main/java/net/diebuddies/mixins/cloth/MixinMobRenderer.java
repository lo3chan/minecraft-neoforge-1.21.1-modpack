/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LightLayer
 *  org.joml.Vector3d
 *  org.joml.Vector3fc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.constraints.LeashConstraint;
import net.diebuddies.physics.verlet.constraints.WorldConstraint;
import net.diebuddies.util.ObjectPacked;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.joml.Vector3d;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class}, priority=900)
public abstract class MixinMobRenderer<T extends Entity> {
    @Shadow
    @Final
    private EntityRenderDispatcher entityRenderDispatcher;
    @Unique
    private Map<ObjectPacked, VerletSimulation> simulations = new Object2ObjectOpenHashMap();
    @Unique
    private ObjectPacked tmp = new ObjectPacked(null, null);
    @Unique
    private static double oceanOffset;

    @Inject(at={@At(value="HEAD")}, method={"renderLeash"}, cancellable=true)
    private void renderLeash(Entity mob, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, Entity entity, CallbackInfo info) {
        if (ConfigClient.areOceanPhysicsEnabled() && !ConfigClient.leashPhysics && entity != null && entity.level() instanceof ClientLevel) {
            OceanWorld oceanWorld = PhysicsMod.getInstance(entity.level()).getPhysicsWorld().getOceanWorld();
            oceanOffset = oceanWorld.computeYOffset(entity.level(), entity, tickDelta) - oceanWorld.computeYOffset(mob.level(), mob, 1.0f);
        }
        if (ConfigClient.leashPhysics && mob.getCommandSenderWorld() instanceof ClientLevel) {
            Iterator<Map.Entry<ObjectPacked, VerletSimulation>> it = this.simulations.entrySet().iterator();
            while (it.hasNext()) {
                if (!it.next().getValue().destroyed) continue;
                it.remove();
            }
            this.tmp.e1 = mob;
            this.tmp.e2 = entity;
            VerletSimulation simulation = this.simulations.get(this.tmp);
            BlockPos mob1Pos = BlockPos.containing((Position)mob.getEyePosition(tickDelta));
            int mob1Brightness = mob.level().getBrightness(LightLayer.BLOCK, mob1Pos);
            int mob1BrightnessSky = mob.level().getBrightness(LightLayer.SKY, mob1Pos);
            int brightness = LightTexture.pack((int)mob1Brightness, (int)mob1BrightnessSky);
            if (simulation == null) {
                simulation = new VerletSimulation(new Vector3d((Vector3fc)ConfigClient.getGravity(mob.getCommandSenderWorld().dimension().location())), 20, 0.855);
                simulation.addConstraint(new WorldConstraint(mob, 0.25f));
                simulation.addConstraint(new LeashConstraint(simulation, mob, entity, this.entityRenderDispatcher, tickDelta));
                simulation.brightness = brightness;
                simulation.textureID = PhysicsMod.whiteTexture.getID();
                simulation.alwaysFetchInstantly = false;
                this.simulations.put(new ObjectPacked(mob, entity), simulation);
                PhysicsMod.getInstance((Level)mob.getCommandSenderWorld()).physicsWorld.addVerletSimulation(simulation);
            } else if (!simulation.destroyed) {
                simulation.active = true;
                simulation.brightness = brightness;
            }
            if (StarterClient.optifabric) {
                PhysicsMod.optifineClothCompat.add(simulation);
            } else {
                simulation.renderSlow(mob.getCommandSenderWorld());
            }
            info.cancel();
        }
    }

    @ModifyVariable(at=@At(value="HEAD"), method={"addVertexPair"}, ordinal=1)
    private static float addVertexPair(float y) {
        return y + (float)oceanOffset;
    }
}

