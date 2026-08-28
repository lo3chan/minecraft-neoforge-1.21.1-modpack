/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.FishingHookRenderer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.FishingHook
 *  net.minecraft.world.level.Level
 *  org.joml.Vector3d
 *  org.joml.Vector3fc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.constraints.FishingHookConstraint;
import net.diebuddies.physics.verlet.constraints.WorldConstraint;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={FishingHookRenderer.class})
public abstract class MixinFishingHookRenderer
extends EntityRenderer<FishingHook> {
    @Unique
    private Map<FishingHook, VerletSimulation> simulations = new Object2ObjectOpenHashMap();
    @Unique
    private static double oceanOffset;

    protected MixinFishingHookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(at={@At(value="HEAD")}, method={"render"})
    public void render(FishingHook fishingHook, float f, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, CallbackInfo info) {
        if (ConfigClient.areOceanPhysicsEnabled() && !ConfigClient.fishingRodPhysics) {
            Player player = fishingHook.getPlayerOwner();
            if (player != null && player.level() instanceof ClientLevel) {
                OceanWorld oceanWorld = PhysicsMod.getInstance(player.level()).getPhysicsWorld().getOceanWorld();
                oceanOffset = oceanWorld.computeYOffset(player.level(), (Entity)player, tickDelta) - oceanWorld.computeYOffset(fishingHook.level(), (Entity)fishingHook, 1.0f);
            }
        } else {
            oceanOffset = 0.0;
        }
        if (ConfigClient.fishingRodPhysics) {
            Iterator<Map.Entry<FishingHook, VerletSimulation>> it = this.simulations.entrySet().iterator();
            while (it.hasNext()) {
                if (!it.next().getValue().destroyed) continue;
                it.remove();
            }
            VerletSimulation simulation = this.simulations.get(fishingHook);
            Player player = fishingHook.getPlayerOwner();
            if (player != null && player.level() instanceof ClientLevel) {
                if (simulation == null) {
                    simulation = new VerletSimulation(new Vector3d((Vector3fc)ConfigClient.getGravity(fishingHook.getCommandSenderWorld().dimension().location())), 20, 0.855);
                    simulation.addConstraint(new WorldConstraint((Entity)fishingHook, 0.0f));
                    simulation.addConstraint(new FishingHookConstraint(simulation, fishingHook, player, this.entityRenderDispatcher, tickDelta));
                    simulation.brightness = light;
                    simulation.textureID = PhysicsMod.whiteTexture.getID();
                    simulation.alwaysFetchInstantly = false;
                    this.simulations.put(fishingHook, simulation);
                    PhysicsMod.getInstance((Level)fishingHook.getCommandSenderWorld()).physicsWorld.addVerletSimulation(simulation);
                } else if (!simulation.destroyed) {
                    simulation.active = true;
                    simulation.brightness = light;
                }
                if (StarterClient.optifabric) {
                    PhysicsMod.optifineClothCompat.add(simulation);
                } else {
                    simulation.renderSlow(fishingHook.getCommandSenderWorld());
                }
            }
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"stringVertex"}, cancellable=true)
    private static void stringVertex(float x, float y, float z, VertexConsumer vertexConsumer, PoseStack.Pose pose, float startPerc, float endPerc, CallbackInfo info) {
        if (ConfigClient.fishingRodPhysics) {
            info.cancel();
        }
    }

    @ModifyVariable(at=@At(value="HEAD"), method={"stringVertex"}, ordinal=1)
    private static float stringVertex(float y) {
        return y + (float)oceanOffset;
    }
}

