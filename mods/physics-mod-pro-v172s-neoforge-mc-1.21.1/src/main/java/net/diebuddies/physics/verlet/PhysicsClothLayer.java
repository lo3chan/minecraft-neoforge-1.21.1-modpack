/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  org.joml.Matrix4d
 *  org.joml.Matrix4fc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3fc
 */
package net.diebuddies.physics.verlet;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigCloth;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.settings.cloth.ClothConstants;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.physics.verlet.ClothRenderCommand;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.constraints.ModelPartConstraint;
import net.diebuddies.physics.verlet.constraints.OceanPhysicsDisplacementConstraint;
import net.diebuddies.physics.verlet.constraints.WorldConstraint;
import net.diebuddies.util.EntityLevelPacked;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.joml.Matrix4d;
import org.joml.Matrix4fc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3fc;

public class PhysicsClothLayer<T extends LivingEntity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    private Map<EntityLevelPacked, VerletSimulation> simulations = new Object2ObjectOpenHashMap();
    private EntityLevelPacked tmp = new EntityLevelPacked();

    public PhysicsClothLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        boolean renderCloth = true;
        if (renderCloth && !PhysicsMod.hudRendering && ConfigClient.capePhysics) {
            Level level;
            Iterator<Map.Entry<EntityLevelPacked, VerletSimulation>> it = this.simulations.entrySet().iterator();
            while (it.hasNext()) {
                if (!it.next().getValue().destroyed) continue;
                it.remove();
            }
            Map<String, ConfigCloth.ClothList> customizationParts = ConfigCloth.getCustomizationParts((Entity)entity);
            if (customizationParts == null || entity.isInvisible()) {
                return;
            }
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            boolean renderFast = false;
            if (camera.getBlockPosition().distSqr((Vec3i)entity.blockPosition()) > (double)(ConfigClient.clothEntityRange * ConfigClient.clothEntityRange)) {
                renderFast = true;
            }
            if (!((level = entity.getCommandSenderWorld()) instanceof ClientLevel)) {
                return;
            }
            for (Map.Entry<String, ConfigCloth.ClothList> customizationPart : customizationParts.entrySet()) {
                String part = customizationPart.getKey();
                ConfigCloth.ClothList clothList = customizationPart.getValue();
                for (String clothPiece : clothList.getClothPieces()) {
                    ModelPart modelPart;
                    EntityModel model;
                    Cloth cloth = PhysicsClothLayer.getCloth(clothPiece);
                    if (cloth == null || ClothConstants.doesArmorHideCloth(cloth, entity) || ClothConstants.isElytraHidingCloth(cloth, part, entity)) continue;
                    if (renderFast || !cloth.rules.isDynamic()) {
                        model = this.getParentModel();
                        modelPart = ModelPartConstraint.getPart((Model)model, part);
                        if (cloth == null || modelPart == null) continue;
                        PhysicsMod.clothRenderFast.add(new ClothRenderCommand(cloth, entity, modelPart, light));
                        continue;
                    }
                    this.renderAndCreateClothSimulation(level, entity, part, cloth, clothPiece, light);
                    if (cloth.playerMesh == null) continue;
                    model = this.getParentModel();
                    modelPart = ModelPartConstraint.getPart((Model)model, part);
                    if (cloth == null || modelPart == null) continue;
                    PhysicsMod.clothRenderFast.add(new ClothRenderCommand(cloth, entity, modelPart, light).setOnlyRenderPlayer(true));
                }
            }
        }
    }

    private void renderAndCreateClothSimulation(Level level, LivingEntity entity, String attachedTo, Cloth cloth, String clothPiece, int light) {
        this.tmp.set((Entity)entity, attachedTo, clothPiece, level);
        VerletSimulation simulation = this.simulations.get(this.tmp);
        if (simulation == null) {
            boolean alwaysInstantlyUpdate;
            EntityModel model = this.getParentModel();
            if (!ModelPartConstraint.exists((EntityModel<LivingEntity>)model, attachedTo)) {
                return;
            }
            int quality = entity == Minecraft.getInstance().player ? 90 : 45;
            simulation = new VerletSimulation(new Vector3d((Vector3fc)ConfigClient.getGravity(level.dimension().location())), quality, 0.855);
            ModelPartConstraint modelPartConstraint = new ModelPartConstraint(simulation, cloth.rules.getIgnoreParts(), entity, attachedTo, (Model)model);
            PoseStack modelMatrix = new PoseStack();
            ModelPartConstraint.entityTransformation(modelMatrix, simulation, entity, (Model)model, 1.0f);
            modelPartConstraint.modelPartTransformation(modelMatrix.last().pose());
            Matrix4d partTransformation = new Matrix4d();
            partTransformation.set((Matrix4fc)modelMatrix.last().pose());
            simulation.getConstraints().clear();
            simulation.addConstraint(new OceanPhysicsDisplacementConstraint((Entity)entity));
            simulation.addConstraint(modelPartConstraint);
            simulation.addConstraint(new WorldConstraint((Entity)entity));
            simulation.brightness = light;
            simulation.addCloth(cloth, cloth.getTexture((Entity)entity), partTransformation, false);
            simulation.setOffset(new Vector3d(entity.getX(), entity.getY(), entity.getZ()).add((Vector3dc)simulation.getOffset()), false);
            simulation.setTransformation(partTransformation);
            simulation.setBufferTransformation(partTransformation);
            simulation.updateOffsets();
            PhysicsWorld physicsWorld = PhysicsMod.getInstance((Level)level).physicsWorld;
            modelPartConstraint.initAsyncData(physicsWorld, simulation);
            modelPartConstraint.changeInstantly = true;
            modelPartConstraint.updateAfter(0.0, simulation);
            simulation.downloadData();
            simulation.alwaysFetchInstantly = alwaysInstantlyUpdate = entity == Minecraft.getInstance().player;
            this.simulations.put(new EntityLevelPacked((Entity)entity, attachedTo, clothPiece, level), simulation);
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

