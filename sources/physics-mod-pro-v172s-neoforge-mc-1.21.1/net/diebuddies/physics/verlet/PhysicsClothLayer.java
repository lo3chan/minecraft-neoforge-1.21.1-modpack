package net.diebuddies.physics.verlet;

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
import net.diebuddies.physics.settings.cloth.ClothConstants;
import net.diebuddies.physics.verlet.constraints.ModelPartConstraint;
import net.diebuddies.physics.verlet.constraints.OceanPhysicsDisplacementConstraint;
import net.diebuddies.physics.verlet.constraints.WorldConstraint;
import net.diebuddies.util.EntityLevelPacked;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.joml.Matrix4d;
import org.joml.Vector3d;

public class PhysicsClothLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
   private Map<EntityLevelPacked, VerletSimulation> simulations = new Object2ObjectOpenHashMap();
   private EntityLevelPacked tmp = new EntityLevelPacked();

   public PhysicsClothLayer(RenderLayerParent<T, M> renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      int light,
      LivingEntity entity,
      float limbAngle,
      float limbDistance,
      float tickDelta,
      float animationProgress,
      float headYaw,
      float headPitch
   ) {
      boolean renderCloth = true;
      if (renderCloth && !PhysicsMod.hudRendering && ConfigClient.capePhysics) {
         Iterator<Entry<EntityLevelPacked, VerletSimulation>> it = this.simulations.entrySet().iterator();

         while (it.hasNext()) {
            if (it.next().getValue().destroyed) {
               it.remove();
            }
         }

         Map<String, ConfigCloth.ClothList> customizationParts = ConfigCloth.getCustomizationParts(entity);
         if (customizationParts == null || entity.isInvisible()) {
            return;
         }

         Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
         boolean renderFast = false;
         if (camera.getBlockPosition().distSqr(entity.blockPosition()) > ConfigClient.clothEntityRange * ConfigClient.clothEntityRange) {
            renderFast = true;
         }

         Level level = entity.getCommandSenderWorld();
         if (!(level instanceof ClientLevel)) {
            return;
         }

         for (Entry<String, ConfigCloth.ClothList> customizationPart : customizationParts.entrySet()) {
            String part = customizationPart.getKey();
            ConfigCloth.ClothList clothList = customizationPart.getValue();

            for (String clothPiece : clothList.getClothPieces()) {
               Cloth cloth = getCloth(clothPiece);
               if (cloth != null && !ClothConstants.doesArmorHideCloth(cloth, entity) && !ClothConstants.isElytraHidingCloth(cloth, part, entity)) {
                  if (!renderFast && cloth.rules.isDynamic()) {
                     this.renderAndCreateClothSimulation(level, entity, part, cloth, clothPiece, light);
                     if (cloth.playerMesh != null) {
                        EntityModel<LivingEntity> model = this.getParentModel();
                        ModelPart modelPart = ModelPartConstraint.getPart(model, part);
                        if (cloth != null && modelPart != null) {
                           PhysicsMod.clothRenderFast.add(new ClothRenderCommand(cloth, entity, modelPart, light).setOnlyRenderPlayer(true));
                        }
                     }
                  } else {
                     EntityModel<LivingEntity> model = this.getParentModel();
                     ModelPart modelPart = ModelPartConstraint.getPart(model, part);
                     if (cloth != null && modelPart != null) {
                        PhysicsMod.clothRenderFast.add(new ClothRenderCommand(cloth, entity, modelPart, light));
                     }
                  }
               }
            }
         }
      }
   }

   private void renderAndCreateClothSimulation(Level level, LivingEntity entity, String attachedTo, Cloth cloth, String clothPiece, int light) {
      this.tmp.set(entity, attachedTo, clothPiece, level);
      VerletSimulation simulation = this.simulations.get(this.tmp);
      if (simulation == null) {
         EntityModel<LivingEntity> model = this.getParentModel();
         if (!ModelPartConstraint.exists(model, attachedTo)) {
            return;
         }

         int quality = entity == Minecraft.getInstance().player ? 90 : 45;
         simulation = new VerletSimulation(new Vector3d(ConfigClient.getGravity(level.dimension().location())), quality, 0.855);
         ModelPartConstraint modelPartConstraint = new ModelPartConstraint(simulation, cloth.rules.getIgnoreParts(), entity, attachedTo, model);
         PoseStack modelMatrix = new PoseStack();
         ModelPartConstraint.entityTransformation(modelMatrix, simulation, entity, model, 1.0F);
         modelPartConstraint.modelPartTransformation(modelMatrix.last().pose());
         Matrix4d partTransformation = new Matrix4d();
         partTransformation.set(modelMatrix.last().pose());
         simulation.getConstraints().clear();
         simulation.addConstraint(new OceanPhysicsDisplacementConstraint(entity));
         simulation.addConstraint(modelPartConstraint);
         simulation.addConstraint(new WorldConstraint(entity));
         simulation.brightness = light;
         simulation.addCloth(cloth, cloth.getTexture(entity), partTransformation, false);
         simulation.setOffset(new Vector3d(entity.getX(), entity.getY(), entity.getZ()).add(simulation.getOffset()), false);
         simulation.setTransformation(partTransformation);
         simulation.setBufferTransformation(partTransformation);
         simulation.updateOffsets();
         PhysicsWorld physicsWorld = PhysicsMod.getInstance(level).physicsWorld;
         modelPartConstraint.initAsyncData(physicsWorld, simulation);
         modelPartConstraint.changeInstantly = true;
         modelPartConstraint.updateAfter(0.0, simulation);
         simulation.downloadData();
         boolean alwaysInstantlyUpdate = entity == Minecraft.getInstance().player;
         simulation.alwaysFetchInstantly = alwaysInstantlyUpdate;
         this.simulations.put(new EntityLevelPacked(entity, attachedTo, clothPiece, level), simulation);
         if (alwaysInstantlyUpdate) {
            physicsWorld.addVerletSimulation(0, simulation);
         } else {
            physicsWorld.addVerletSimulation(simulation);
         }
      } else {
         if (!simulation.destroyed) {
            simulation.active = true;
            simulation.brightness = light;
         }

         if (cloth != simulation.cloth) {
            simulation.destroyed = true;
         }
      }

      if (StarterClient.optifabric) {
         PhysicsMod.optifineClothCompat.add(simulation);
      } else {
         simulation.renderSlow(level);
      }
   }

   public static Cloth getCloth(String cloth) {
      return PhysicsMod.cloth.get(cloth);
   }
}
