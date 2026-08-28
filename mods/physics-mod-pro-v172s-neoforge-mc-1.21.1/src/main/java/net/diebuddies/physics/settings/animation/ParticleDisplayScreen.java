/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Lighting
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  org.joml.Matrix4fStack
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package net.diebuddies.physics.settings.animation;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.diebuddies.math.Math;
import net.diebuddies.mixins.MixinParticleEngineAccessor;
import net.diebuddies.opengl.Box;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.animation.AnimationType;
import net.diebuddies.physics.animation.CurveType;
import net.diebuddies.physics.animation.ParticleSpawn;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.animation.ParticleExtension;
import net.diebuddies.physics.settings.ux.Animatable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class ParticleDisplayScreen
extends Screen {
    public static final ResourceLocation STONE_TEXTURE = ResourceLocation.withDefaultNamespace((String)"textures/block/stone.png");
    public Screen parent;
    private double xPosition = 70.0;
    private double animationStart;
    private boolean createdParticles;
    public Animation animation;
    private List<Particle> particles;
    private Camera camera = new Camera();
    private long lastTick = System.currentTimeMillis();

    protected ParticleDisplayScreen(Screen parent, Component component) {
        super(component);
        this.parent = parent;
        this.particles = new ObjectArrayList();
        this.animation = new Animation("default", CurveType.Linear, 0.5f);
    }

    protected void init() {
        super.init();
        this.startAnimation();
        this.addRenderableWidget((GuiEventListener)((Button)((Animatable)ButtonSettings.builder((int)this.xPosition - 30, this.height - 57, 60, 20, (Component)Component.translatable((String)"physicsmod.menu.animation.replay"), button -> this.startAnimation())).setAnimDepth(200.0f)));
    }

    public void startAnimation() {
        this.particles.clear();
        this.animationStart = (double)this.animation.speed + 0.5;
        this.createdParticles = false;
    }

    private void createParticles() {
        if (this.createdParticles) {
            return;
        }
        this.createdParticles = true;
        this.particles.clear();
        for (ParticleSpawn spawn : this.animation.particleSpawns) {
            ParticleOptions particleOptions = spawn.particle;
            if (particleOptions == null) continue;
            Map<ResourceLocation, ParticleProvider<?>> provider = ((MixinParticleEngineAccessor)this.minecraft.particleEngine).getParticleProviders();
            ParticleProvider<?> particleProvider = provider.get(BuiltInRegistries.PARTICLE_TYPE.getKey((Object)particleOptions.getType()));
            if (!((double)Math.random() < spawn.spawnChance)) continue;
            for (int i = 0; i < spawn.amount; ++i) {
                double halfSpread = spawn.spread * 0.5;
                double px = (double)Math.random() * spawn.spread - halfSpread;
                double py = (double)Math.random() * spawn.spread - halfSpread;
                double pz = (double)Math.random() * spawn.spread - halfSpread;
                try {
                    Particle particle = particleProvider.createParticle(particleOptions, null, px, py, pz, spawn.vx, spawn.vy, spawn.vz);
                    ((ParticleExtension)particle).setPhysics(false);
                    ((ParticleExtension)particle).setFakeLight(true);
                    this.particles.add(particle);
                    continue;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            if (spawn.sound == null) continue;
            Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)spawn.sound, (float)1.0f));
        }
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        this.renderAnimation(delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    public void tick() {
        super.tick();
        this.lastTick = System.currentTimeMillis();
        Iterator<Particle> it = this.particles.iterator();
        while (it.hasNext()) {
            Particle particle = it.next();
            if (!particle.isAlive()) continue;
            try {
                particle.tick();
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (particle.isAlive()) continue;
            it.remove();
        }
    }

    private void renderAnimation(float delta) {
        float scale = 30.0f;
        Matrix4fStack matrices = RenderSystem.getModelViewStack();
        float realDelta = java.lang.Math.min(1.0f, (float)(System.currentTimeMillis() - this.lastTick) / 50.0f);
        double startX = -1.0;
        double endX = 1.0;
        double startY = -1.0;
        double endY = 1.0;
        double startZ = -1.0;
        double endZ = 1.0;
        double capeWidth = endX - startX;
        double capeHeight = endY - startY;
        double capeDepth = endZ - startZ;
        matrices.pushMatrix();
        matrices.translate((float)this.xPosition, (float)this.height / 2.0f, 100.0f);
        matrices.scale(scale, -scale, scale);
        matrices.translate((float)(-capeWidth * 0.5 - startX), (float)(-capeHeight * 0.5 - startY), (float)(-capeDepth * 0.5 - startZ));
        matrices.pushMatrix();
        matrices.rotate((Quaternionfc)new Quaternionf().rotationXYZ((float)java.lang.Math.toRadians(25.0), (float)java.lang.Math.toRadians(-25.0), 0.0f));
        RenderSystem.applyModelViewMatrix();
        ShaderInstance shader = RenderSystem.getShader();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShader(GameRenderer::getRendertypeArmorCutoutNoCullShader);
        RenderSystem.enableDepthTest();
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        Lighting.setupForEntityInInventory();
        RenderSystem.activeTexture((int)33984);
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)STONE_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        int brightness = 0xF00000;
        Tesselator tesselator = Tesselator.getInstance();
        this.animationStart -= (double)delta * 0.05;
        float despawnScale = this.animation.getCurve().get((float)(this.animationStart / (double)this.animation.speed));
        if (this.animationStart > (double)this.animation.speed) {
            despawnScale = 1.0f;
        } else if (this.animationStart <= 0.0) {
            despawnScale = 0.0f;
        }
        float alpha = 1.0f;
        if (this.animation.despawnType == AnimationType.Vanish) {
            alpha = java.lang.Math.min(1.0f, despawnScale);
            despawnScale = 1.0f;
        } else if (this.animation.despawnType == AnimationType.Shrink_and_Vanish) {
            alpha = java.lang.Math.min(1.0f, despawnScale);
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
        int[] indices = Box.INDICES;
        float[] positions = Box.POSITIONS;
        float[] normals = Box.NORMALS;
        float[] uvs = Box.UVS;
        for (int i = 0; i < indices.length; ++i) {
            int index = indices[i];
            float x = positions[index * 3] * 0.5f * despawnScale;
            float y = positions[index * 3 + 1] * 0.5f * despawnScale;
            float z = positions[index * 3 + 2] * 0.5f * despawnScale;
            float nx = -normals[index * 3];
            float ny = -normals[index * 3 + 1];
            float nz = -normals[index * 3 + 2];
            float uvx = uvs[index * 2];
            float uvy = uvs[index * 2 + 1];
            bufferbuilder.addVertex(x, y, z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(uvx, uvy).setOverlay(OverlayTexture.NO_OVERLAY).setLight(brightness).setNormal(nx, ny, nz);
        }
        BufferUploader.drawWithShader((MeshData)bufferbuilder.build());
        if (this.animationStart <= 0.0) {
            this.createParticles();
        }
        matrices.popMatrix();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        for (Particle particle : this.particles) {
            if (!particle.isAlive()) continue;
            ParticleRenderType particleRenderType = particle.getRenderType();
            RenderSystem.setShader(GameRenderer::getParticleShader);
            bufferbuilder = particleRenderType.begin(tesselator, this.minecraft.getTextureManager());
            if (bufferbuilder == null) continue;
            particle.render((VertexConsumer)bufferbuilder, this.camera, realDelta);
            MeshData meshData = bufferbuilder.build();
            if (meshData == null) continue;
            BufferUploader.drawWithShader((MeshData)meshData);
        }
        matrices.popMatrix();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
        RenderSystem.setShader(() -> shader);
    }

    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }
}

