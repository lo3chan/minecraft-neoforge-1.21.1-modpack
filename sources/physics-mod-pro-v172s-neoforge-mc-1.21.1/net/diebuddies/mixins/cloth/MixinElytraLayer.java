package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigCloth;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.physics.verlet.ClothRenderCommand;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.constraints.ModelPartConstraint;
import net.diebuddies.physics.verlet.constraints.OceanPhysicsDisplacementConstraint;
import net.diebuddies.util.PlayerLevelPacked;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ElytraLayer.class})
public class MixinElytraLayer {
   @Unique
   private Map<PlayerLevelPacked, VerletSimulation> simulations = new Object2ObjectOpenHashMap();
   @Unique
   private Matrix4f localT = new Matrix4f();
   @Unique
   private MultiBufferSource multiBufferSource;
   @Unique
   private PlayerLevelPacked tmp = new PlayerLevelPacked(null, null);

   @Inject(
      at = {@At("HEAD")},
      method = {"render"},
      cancellable = true
   )
   private void physicsmod$replaceElytra(
      PoseStack matrixStack,
      MultiBufferSource multiBufferSource,
      int light,
      LivingEntity entity,
      float limbAngle,
      float limbDistance,
      float tickDelta,
      float animationProgress,
      float headYaw,
      float headPitch,
      CallbackInfo info
   ) {
      if (entity instanceof AbstractClientPlayer player) {
         this.physicsmod$removeOldSimulations();
         ItemStack itemStack = entity.getItemBySlot(EquipmentSlot.CHEST);
         if (!itemStack.is(Items.ELYTRA)) {
            return;
         }

         boolean renderCloth = true;
         if (renderCloth && this.physicsmod$shouldRenderPhysicsElytra(player) && this.physicsmod$hasPhysicsElytra(player)) {
            this.physicsmod$renderPhysicsElytra(player, light);
            info.cancel();
         }
      }
   }

   @Unique
   private void physicsmod$removeOldSimulations() {
      Iterator<Entry<PlayerLevelPacked, VerletSimulation>> it = this.simulations.entrySet().iterator();

      while (it.hasNext()) {
         if (it.next().getValue().destroyed) {
            it.remove();
         }
      }
   }

   @Unique
   private void physicsmod$renderPhysicsElytra(AbstractClientPlayer player, int light) {
      Level level = player.getCommandSenderWorld();
      if (level instanceof ClientLevel) {
         this.tmp.e1 = player.getGameProfile().getName();
         this.tmp.e2 = level;
         VerletSimulation simulation = this.simulations.get(this.tmp);
         Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
         boolean renderFast = false;
         if (camera.getBlockPosition().distSqr(player.blockPosition()) > ConfigClient.clothEntityRange * ConfigClient.clothEntityRange) {
            renderFast = true;
         }

         Cloth cloth = ConfigCloth.getCategory(player, "Elytra");
         LivingEntityRenderer<?, ?> renderer = (LivingEntityRenderer<?, ?>)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
         Model model = renderer.getModel();
         if (!renderFast && cloth.rules.isDynamic()) {
            if (simulation == null) {
               if (model instanceof EntityModel entityModel && !ModelPartConstraint.exists(entityModel, "body")) {
                  return;
               }

               int quality = player == Minecraft.getInstance().player ? 90 : 45;
               simulation = new VerletSimulation(new Vector3d(ConfigClient.getGravity(level.dimension().location())), quality, 0.855);
               ModelPartConstraint modelPartConstraint = new ModelPartConstraint(simulation, cloth.rules.getIgnoreParts(), player, "body", model);
               PoseStack modelMatrix = new PoseStack();
               ModelPartConstraint.entityTransformation(modelMatrix, simulation, player, model, 1.0F);
               modelPartConstraint.modelPartTransformation(modelMatrix.last().pose());
               Matrix4d partTransformation = new Matrix4d();
               partTransformation.set(modelMatrix.last().pose());
               simulation.getConstraints().clear();
               simulation.addConstraint(new OceanPhysicsDisplacementConstraint(player));
               simulation.addConstraint(modelPartConstraint);
               simulation.brightness = light;
               simulation.addCloth(cloth, cloth.getTexture(player), partTransformation, false);
               simulation.setOffset(new Vector3d(player.getX(), player.getY(), player.getZ()).add(simulation.getOffset()), false);
               simulation.setTransformation(partTransformation);
               simulation.setBufferTransformation(partTransformation);
               simulation.updateOffsets();
               PhysicsWorld physicsWorld = PhysicsMod.getInstance(level).physicsWorld;
               modelPartConstraint.initAsyncData(physicsWorld, simulation);
               modelPartConstraint.changeInstantly = true;
               modelPartConstraint.updateAfter(0.0, simulation);
               simulation.downloadData();
               boolean alwaysInstantlyUpdate = player == Minecraft.getInstance().player;
               simulation.alwaysFetchInstantly = alwaysInstantlyUpdate;
               this.simulations.put(new PlayerLevelPacked(player.getGameProfile().getName(), player.getCommandSenderWorld()), simulation);
               if (alwaysInstantlyUpdate) {
                  physicsWorld.addVerletSimulation(0, simulation);
               } else {
                  physicsWorld.addVerletSimulation(simulation);
               }
            } else if (!simulation.destroyed) {
               simulation.active = true;
               simulation.brightness = light;
            }

            if (cloth != simulation.cloth) {
               simulation.destroyed = true;
            }

            if (StarterClient.optifabric) {
               PhysicsMod.optifineClothCompat.add(simulation);
            } else {
               simulation.renderSlow(player.getCommandSenderWorld());
            }
         } else {
            ModelPart modelPart = ModelPartConstraint.getPart(model, "body");
            if (cloth != null && modelPart != null) {
               PhysicsMod.clothRenderFast.add(new ClothRenderCommand(cloth, player, modelPart, light));
            }
         }
      }
   }

   @Unique
   private boolean physicsmod$shouldRenderPhysicsElytra(AbstractClientPlayer player) {
      return !PhysicsMod.hudRendering && ConfigClient.capePhysics && player != null && !player.isInvisible() && !ConfigClient.clothForceArmor;
   }

   @Unique
   private boolean physicsmod$hasPhysicsElytra(AbstractClientPlayer player) {
      return ConfigCloth.hasCategory(player, "Elytra");
   }
}
