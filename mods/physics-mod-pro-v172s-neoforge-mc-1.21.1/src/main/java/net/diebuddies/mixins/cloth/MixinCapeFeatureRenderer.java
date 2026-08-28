/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.client.renderer.entity.layers.CapeLayer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.Level
 *  org.joml.Matrix4d
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3fc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
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
import net.diebuddies.physics.verlet.constraints.WorldConstraint;
import net.diebuddies.util.PlayerLevelPacked;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={CapeLayer.class})
public class MixinCapeFeatureRenderer {
    @Unique
    private Map<PlayerLevelPacked, VerletSimulation> simulations = new Object2ObjectOpenHashMap();
    @Unique
    private Matrix4f localT = new Matrix4f();
    @Unique
    private AbstractClientPlayer player;
    @Unique
    private boolean renderedCape;
    @Unique
    private MultiBufferSource multiBufferSource;
    @Unique
    private PlayerLevelPacked tmp = new PlayerLevelPacked(null, null);

    @Inject(at={@At(value="HEAD")}, method={"render"})
    private void physicsmod$renderHead(PoseStack matrixStack, MultiBufferSource multiBufferSource, int light, AbstractClientPlayer player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo info) {
        this.player = player;
        this.renderedCape = false;
        this.multiBufferSource = multiBufferSource;
    }

    @Inject(at={@At(value="RETURN")}, method={"render"})
    private void physicsmod$renderTail(PoseStack matrixStack, MultiBufferSource multiBufferSource, int light, AbstractClientPlayer player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo info) {
        this.physicsmod$removeOldSimulations();
        boolean renderCloth = true;
        if (!renderCloth || !this.physicsmod$shouldRenderPhysicsCape(player) || this.renderedCape || !this.physicsmod$hasPhysicsCape(player)) {
            // empty if block
        }
    }

    @Redirect(method={"render"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/PlayerModel;renderCloak(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void physicsmod$renderCloak(PlayerModel playerModel, PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay) {
        this.physicsmod$removeOldSimulations();
        boolean renderCloth = true;
        if (renderCloth && this.physicsmod$shouldRenderPhysicsCape(this.player)) {
            if (!this.physicsmod$hasPhysicsCape(this.player)) {
                this.physicsmod$renderPhysicsCape(this.player, light);
            }
        } else {
            ((PlayerModel)((CapeLayer)this).getParentModel()).renderCloak(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
        this.renderedCape = true;
    }

    @Unique
    private boolean physicsmod$shouldRenderPhysicsCape(AbstractClientPlayer player) {
        return !PhysicsMod.hudRendering && ConfigClient.capePhysics && player != null && !player.isInvisible() && this.physicsmod$hasMojangCape(player);
    }

    @Unique
    private boolean physicsmod$hasMojangCape(AbstractClientPlayer player) {
        return player.getSkin().capeTexture() != null;
    }

    @Unique
    private boolean physicsmod$hasPhysicsCape(AbstractClientPlayer player) {
        return ConfigCloth.hasCategory((Entity)player, "Back");
    }

    @Unique
    private void physicsmod$removeOldSimulations() {
        Iterator<Map.Entry<PlayerLevelPacked, VerletSimulation>> it = this.simulations.entrySet().iterator();
        while (it.hasNext()) {
            if (!it.next().getValue().destroyed) continue;
            it.remove();
        }
    }

    @Unique
    private void physicsmod$renderPhysicsCape(AbstractClientPlayer player, int light) {
        ItemStack itemStack = player.getItemBySlot(EquipmentSlot.CHEST);
        Level level = player.getCommandSenderWorld();
        if (!(level instanceof ClientLevel)) {
            return;
        }
        this.tmp.e1 = player.getGameProfile().getName();
        this.tmp.e2 = level;
        VerletSimulation simulation = this.simulations.get(this.tmp);
        if (!itemStack.is(Items.ELYTRA)) {
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            boolean renderFast = false;
            if (camera.getBlockPosition().distSqr((Vec3i)player.blockPosition()) > (double)(ConfigClient.clothEntityRange * ConfigClient.clothEntityRange)) {
                renderFast = true;
            }
            Cloth cloth = PhysicsMod.defaultCape;
            LivingEntityRenderer renderer = (LivingEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((Entity)player);
            EntityModel model = renderer.getModel();
            if (renderFast || !cloth.rules.isDynamic()) {
                ModelPart modelPart = ModelPartConstraint.getPart((Model)model, "body");
                int textureID = Minecraft.getInstance().getTextureManager().getTexture(player.getSkin().capeTexture()).getId();
                if (cloth != null && modelPart != null) {
                    PhysicsMod.clothRenderFast.add(new ClothRenderCommand(cloth, textureID, (LivingEntity)player, modelPart, light));
                }
            } else {
                if (simulation == null) {
                    boolean alwaysInstantlyUpdate;
                    EntityModel entityModel;
                    if (model instanceof EntityModel && !ModelPartConstraint.exists((EntityModel<LivingEntity>)(entityModel = model), "body")) {
                        return;
                    }
                    int quality = player == Minecraft.getInstance().player ? 90 : 45;
                    simulation = new VerletSimulation(new Vector3d((Vector3fc)ConfigClient.getGravity(level.dimension().location())), quality, 0.855);
                    ModelPartConstraint modelPartConstraint = new ModelPartConstraint(simulation, cloth.rules.getIgnoreParts(), (LivingEntity)player, "body", (Model)model);
                    PoseStack modelMatrix = new PoseStack();
                    ModelPartConstraint.entityTransformation(modelMatrix, simulation, (LivingEntity)player, (Model)model, 1.0f);
                    modelPartConstraint.modelPartTransformation(modelMatrix.last().pose());
                    Matrix4d partTransformation = new Matrix4d();
                    partTransformation.set((Matrix4fc)modelMatrix.last().pose());
                    simulation.getConstraints().clear();
                    simulation.addConstraint(new OceanPhysicsDisplacementConstraint((Entity)player));
                    simulation.addConstraint(modelPartConstraint);
                    simulation.addConstraint(new WorldConstraint((Entity)player));
                    simulation.brightness = light;
                    int textureID = Minecraft.getInstance().getTextureManager().getTexture(player.getSkin().capeTexture()).getId();
                    simulation.addCloth(PhysicsMod.defaultCape, textureID, partTransformation, false);
                    simulation.setOffset(new Vector3d(player.getX(), player.getY(), player.getZ()).add((Vector3dc)simulation.getOffset()), false);
                    simulation.setTransformation(partTransformation);
                    simulation.setBufferTransformation(partTransformation);
                    simulation.updateOffsets();
                    PhysicsWorld physicsWorld = PhysicsMod.getInstance((Level)level).physicsWorld;
                    modelPartConstraint.initAsyncData(physicsWorld, simulation);
                    modelPartConstraint.changeInstantly = true;
                    modelPartConstraint.updateAfter(0.0, simulation);
                    simulation.downloadData();
                    simulation.alwaysFetchInstantly = alwaysInstantlyUpdate = player == Minecraft.getInstance().player;
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
            }
        }
    }
}

