package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FishingHookRenderer.class})
public abstract class MixinFishingHookRenderer extends EntityRenderer<FishingHook> {
   @Unique
   private Map<FishingHook, VerletSimulation> simulations = new Object2ObjectOpenHashMap();
   @Unique
   private static double oceanOffset;

   protected MixinFishingHookRenderer(Context context) {
      super(context);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"render"}
   )
   public void render(FishingHook fishingHook, float f, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, CallbackInfo info) {
      if (ConfigClient.areOceanPhysicsEnabled() && !ConfigClient.fishingRodPhysics) {
         Player player = fishingHook.getPlayerOwner();
         if (player != null && player.level() instanceof ClientLevel) {
            OceanWorld oceanWorld = PhysicsMod.getInstance(player.level()).getPhysicsWorld().getOceanWorld();
            oceanOffset = oceanWorld.computeYOffset(player.level(), player, tickDelta) - oceanWorld.computeYOffset(fishingHook.level(), fishingHook, 1.0F);
         }
      } else {
         oceanOffset = 0.0;
      }

      if (ConfigClient.fishingRodPhysics) {
         Iterator<Entry<FishingHook, VerletSimulation>> it = this.simulations.entrySet().iterator();

         while (it.hasNext()) {
            if (it.next().getValue().destroyed) {
               it.remove();
            }
         }

         VerletSimulation simulation = this.simulations.get(fishingHook);
         Player player = fishingHook.getPlayerOwner();
         if (player != null && player.level() instanceof ClientLevel) {
            if (simulation == null) {
               simulation = new VerletSimulation(new Vector3d(ConfigClient.getGravity(fishingHook.getCommandSenderWorld().dimension().location())), 20, 0.855);
               simulation.addConstraint(new WorldConstraint(fishingHook, 0.0F));
               simulation.addConstraint(new FishingHookConstraint(simulation, fishingHook, player, this.entityRenderDispatcher, tickDelta));
               simulation.brightness = light;
               simulation.textureID = PhysicsMod.whiteTexture.getID();
               simulation.alwaysFetchInstantly = false;
               this.simulations.put(fishingHook, simulation);
               PhysicsMod.getInstance(fishingHook.getCommandSenderWorld()).physicsWorld.addVerletSimulation(simulation);
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

   @Inject(
      at = {@At("HEAD")},
      method = {"stringVertex"},
      cancellable = true
   )
   private static void stringVertex(float x, float y, float z, VertexConsumer vertexConsumer, Pose pose, float startPerc, float endPerc, CallbackInfo info) {
      if (ConfigClient.fishingRodPhysics) {
         info.cancel();
      }
   }

   @ModifyVariable(
      at = @At("HEAD"),
      method = {"stringVertex"},
      ordinal = 1
   )
   private static float stringVertex(float y) {
      return y + (float)oceanOffset;
   }
}
