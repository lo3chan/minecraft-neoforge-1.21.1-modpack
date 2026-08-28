/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.shorts.ShortIterator
 *  it.unimi.dsi.fastutil.shorts.ShortOpenHashSet
 *  it.unimi.dsi.fastutil.shorts.ShortSet
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.vehicle.Boat
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.chunk.DataLayer
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.lighting.LayerLightEventListener
 *  net.minecraft.world.level.lighting.LayerLightEventListener$DummyLightLayerEventListener
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Quaternionfc
 *  org.joml.Vector2f
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.joml.Vector3i
 *  org.joml.Vector4f
 *  org.lwjgl.opengl.GL32C
 */
package net.diebuddies.physics.ocean;

import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nullable;
import net.diebuddies.bridge.WeatherParticlesRegistry;
import net.diebuddies.compat.SableCreate;
import net.diebuddies.compat.ValkyrienSkies;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.mixins.MixinParticleEngineAccessor;
import net.diebuddies.opengl.ArenaBuffer;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.StateTracker;
import net.diebuddies.opengl.VertexFormat;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.EntityOcean;
import net.diebuddies.physics.ocean.IChunk;
import net.diebuddies.physics.ocean.OceanBlockUpdate;
import net.diebuddies.physics.ocean.OceanMesh;
import net.diebuddies.physics.ocean.OceanProcessor;
import net.diebuddies.physics.ocean.OceanSurface;
import net.diebuddies.physics.ocean.ProxyOceanLayer;
import net.diebuddies.physics.ocean.RainParticle;
import net.diebuddies.physics.ocean.RippleParticle;
import net.diebuddies.physics.ocean.thread.OceanChunkCreator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL32C;

public class OceanWorld {
    private static final float BASE_OCEAN_HEIGHT = 13.0f;
    private PhysicsWorld world;
    private final Level level;
    private ConcurrentLinkedQueue<Runnable> queue;
    private OceanProcessor processor;
    private Set<OceanBlockUpdate> blockUpdates;
    public Long2ObjectMap<ShortSet> lightUpdates;
    private Set<Vector3i> oceanLayerLightUpdates;
    private double rippleTime;
    private ArenaBuffer oceanVertexData;
    private ArenaBuffer oceanIndexData;
    public VertexFormat format;
    public int oceanVAO = -1;
    private Short2ObjectMap<ProxyOceanLayer> oceanLayers;
    private float oceanTime;
    private float globalTime;
    private float oceanHeightMultiplier;
    private float weatherSpeedMultiplier;
    private Vector2f waterMidCoord;
    private Vector4f waterCoord;
    private Long2ObjectMap<OceanMesh> oceanMeshes;
    private BlockPos.MutableBlockPos tmp = new BlockPos.MutableBlockPos();

    public OceanWorld(PhysicsWorld world, Level level) {
        this.world = world;
        this.level = level;
        this.queue = new ConcurrentLinkedQueue();
        this.blockUpdates = new ObjectOpenHashSet();
        this.oceanMeshes = new Long2ObjectOpenHashMap();
        this.lightUpdates = new Long2ObjectOpenHashMap();
        this.oceanLayerLightUpdates = new ObjectOpenHashSet();
        this.oceanLayers = new Short2ObjectOpenHashMap();
        this.weatherSpeedMultiplier = 1.0f;
        this.oceanHeightMultiplier = 1.0f;
        TextureAtlasSprite waterTexture = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
        this.waterCoord = new Vector4f(waterTexture.getU0(), waterTexture.getU1(), waterTexture.getV0(), waterTexture.getV1());
        this.waterMidCoord = new Vector2f(this.waterCoord.x + this.waterCoord.y, this.waterCoord.z + this.waterCoord.w).mul(0.5f);
        this.processor = new OceanProcessor(this, level.getMinSection(), level.getMaxSection(), this.waterCoord);
        this.processor.start();
    }

    public void update(double diff) {
        if (this.oceanVAO != -1) {
            StateTracker.bindVertexArray(this.oceanVAO);
        }
        Runnable event = null;
        while ((event = this.queue.poll()) != null) {
            event.run();
        }
        if (!ConfigClient.areOceanPhysicsEnabled()) {
            return;
        }
        ObjectArrayList events = new ObjectArrayList();
        this.applyBlockUpdates((List<Runnable>)events);
        this.applyLightUpdates((List<Runnable>)events);
        this.applyOceanLayerLightUpdates((List<Runnable>)events);
        if (!events.isEmpty()) {
            this.processor.queueEvent(() -> OceanWorld.lambda$update$0((List)events));
        }
        Minecraft minecraft = Minecraft.getInstance();
        float renderPercent = minecraft.getTimer().getGameTimeDeltaPartialTick(true);
        float storminess = ConfigClient.oceanWeatherClear;
        storminess += this.level.getRainLevel(renderPercent) * ConfigClient.oceanWeatherRain;
        this.weatherSpeedMultiplier = 0.9f + (storminess += this.level.getThunderLevel(renderPercent) * ConfigClient.oceanWeatherThunder) * 0.2f;
        this.oceanHeightMultiplier = 0.7f + storminess * 0.4f;
        this.oceanTime = (float)((double)this.oceanTime + diff * (double)this.weatherSpeedMultiplier * (double)ConfigClient.oceanBaseSpeed);
        this.globalTime = (float)((double)this.globalTime + diff);
        if (ConfigClient.oceanRipples) {
            this.updateRipple(diff);
        }
    }

    public void updateRipple(double diff) {
        this.rippleTime += diff;
        double tick = 0.025;
        if (this.rippleTime >= tick * 5.0) {
            this.rippleTime = tick * 5.0;
        }
        while (this.rippleTime >= tick) {
            this.rippleTime -= tick;
            this.updateParticles(tick);
        }
    }

    private void updateParticles(double diff) {
        for (ProxyOceanLayer layer : this.oceanLayers.values()) {
            layer.update(diff);
        }
    }

    public void spawnRainRipple(int lifetime, float scale, double x, double y, double z) {
        ProxyOceanLayer layer = this.getOceanLayer(x, y, z);
        if (layer == null) {
            return;
        }
        RainParticle particle = new RainParticle(lifetime, scale, x, y, z);
        particle.baseAlpha = 0.35f;
        layer.addRippleParticle(particle);
    }

    public void spawnRipple(int amount, int lifetime, float scale, double x, double y, double z, double speed) {
        ProxyOceanLayer layer = this.getOceanLayer(x, y, z);
        if (layer == null) {
            return;
        }
        double slice = java.lang.Math.PI * 2 / (double)amount;
        for (int i = 0; i < amount; ++i) {
            double angle = slice * (double)i;
            RippleParticle particle = new RippleParticle(lifetime, x, y, z, java.lang.Math.cos(angle) * speed, 0.0, java.lang.Math.sin(angle) * speed);
            particle.scale = scale;
            particle.baseAlpha = 0.35f;
            layer.addRippleParticle(particle);
        }
    }

    public void spawnAngularRipple(int amount, int lifetime, double x, double y, double z, double dirx, double dirz, double angleRange, double speed, double delay) {
        ProxyOceanLayer layer = this.getOceanLayer(x, y, z);
        if (layer == null) {
            return;
        }
        double slice = java.lang.Math.PI * 2 / (double)amount;
        double movementAngle = java.lang.Math.atan2(dirz, dirx);
        for (int i = 0; i < amount; ++i) {
            double angle = slice * (double)i;
            double px = java.lang.Math.cos(angle);
            double pz = java.lang.Math.sin(angle);
            double pointAngle = java.lang.Math.atan2(pz, px);
            double diff = java.lang.Math.abs(pointAngle - movementAngle);
            if (!(diff <= angleRange) && !(diff >= java.lang.Math.PI * 2 - angleRange)) continue;
            if (diff >= java.lang.Math.PI * 2 - angleRange) {
                diff -= java.lang.Math.PI * 2;
            }
            RippleParticle particle = new RippleParticle(lifetime, x - px * delay * speed, y, z - pz * delay * speed, px * speed, 0.0, pz * speed);
            particle.baseAlpha = Math.clamp(1.0f - (float)java.lang.Math.abs(diff / angleRange), 0.0f, 1.0f);
            layer.addRippleParticle(particle);
        }
    }

    public float getOceanTime() {
        return this.oceanTime;
    }

    public float getGlobalTime() {
        return this.globalTime;
    }

    private void applyBlockUpdates(List<Runnable> events) {
        if (this.blockUpdates.isEmpty()) {
            return;
        }
        ObjectArrayList updates = new ObjectArrayList(this.blockUpdates);
        events.add(() -> this.lambda$applyBlockUpdates$1((List)updates));
        this.blockUpdates.clear();
    }

    private void applyLightUpdates(List<Runnable> events) {
        if (this.lightUpdates.isEmpty()) {
            return;
        }
        ObjectArrayList asyncUpdates = new ObjectArrayList();
        ObjectIterator it = this.lightUpdates.long2ObjectEntrySet().iterator();
        LevelLightEngine levelLightEngine = this.level.getLightEngine();
        while (it.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)it.next();
            long chunkIndex = entry.getLongKey();
            int x = SectionPos.x((long)chunkIndex);
            int y = SectionPos.y((long)chunkIndex);
            int z = SectionPos.z((long)chunkIndex);
            ShortSet positions = (ShortSet)entry.getValue();
            LevelChunk chunk = this.level.getChunk(x, z);
            if (positions.isEmpty() || chunk == null) {
                it.remove();
                continue;
            }
            SectionPos sectionPos = SectionPos.of((ChunkPos)chunk.getPos(), (int)0);
            if (!levelLightEngine.lightOnInSection(sectionPos)) continue;
            ShortIterator blockIt = positions.iterator();
            while (blockIt.hasNext()) {
                short localPos = blockIt.nextShort();
                byte lx = (byte)(localPos >> 8 & 0xF);
                byte ly = (byte)(localPos >> 4 & 0xF);
                byte lz = (byte)(localPos & 0xF);
                int wx = x * 16 + lx;
                int wy = y * 16 + ly;
                int wz = z * 16 + lz;
                if (wy < this.level.getMinBuildHeight() || wy >= this.level.getMaxBuildHeight()) {
                    blockIt.remove();
                    continue;
                }
                this.tmp.set(wx, wy, wz);
                int sky = Math.clamp(this.level.getBrightness(LightLayer.SKY, (BlockPos)this.tmp), 0, 15);
                int block = Math.clamp(this.level.getBrightness(LightLayer.BLOCK, (BlockPos)this.tmp), 0, 15);
                LightUpdate update = new LightUpdate(this);
                update.posX = wx;
                update.posY = wy;
                update.posZ = wz;
                update.lightData = (byte)(sky << 4 | block);
                asyncUpdates.add(update);
                blockIt.remove();
            }
            if (!positions.isEmpty()) continue;
            it.remove();
        }
        if (!asyncUpdates.isEmpty()) {
            events.add(() -> this.lambda$applyLightUpdates$2((List)asyncUpdates));
        }
    }

    public double calculateYOffset(double x, double y, double z) {
        double maxOffset = 0.0;
        double maxMagnitude = 0.0;
        for (ProxyOceanLayer layer : this.oceanLayers.values()) {
            double offset = layer.calculateYOffset(this, x, y, z);
            double magnitude = java.lang.Math.abs(offset);
            if (!(magnitude > maxMagnitude)) continue;
            maxMagnitude = magnitude;
            maxOffset = offset;
        }
        return maxOffset;
    }

    public Vector3d calculateWaveForce(double x, double y, double z) {
        Vector3d maxOffset = null;
        double maxMagnitude = 0.0;
        for (ProxyOceanLayer layer : this.oceanLayers.values()) {
            double magnitude;
            Vector3d offset = layer.calculateWaveNormal(this, x, y, z);
            if (offset == null || !((magnitude = offset.lengthSquared()) > maxMagnitude)) continue;
            maxMagnitude = magnitude;
            maxOffset = offset;
        }
        return maxOffset;
    }

    public boolean isInsideOceanWater(double x, double y, double z) {
        for (ProxyOceanLayer layer : this.oceanLayers.values()) {
            if (!layer.isInsideOceanWater(this, x, y, z)) continue;
            return true;
        }
        return false;
    }

    public boolean isInsideOceanRange(double x, double y, double z) {
        for (ProxyOceanLayer layer : this.oceanLayers.values()) {
            if (!layer.isInsideTextureOceanRange(this, x, y, z)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    public ProxyOceanLayer getOceanLayer(double x, double y, double z) {
        for (ProxyOceanLayer layer : this.oceanLayers.values()) {
            if (!layer.isInsideTextureOceanRangeNoWarp(this, x, y, z)) continue;
            return layer;
        }
        return null;
    }

    private void applyOceanLayerLightUpdates(List<Runnable> events) {
        if (this.oceanLayerLightUpdates.isEmpty()) {
            return;
        }
        Iterator<Vector3i> it = this.oceanLayerLightUpdates.iterator();
        ObjectArrayList asyncUpdates = new ObjectArrayList();
        LevelLightEngine lightEngine = this.level.getLightEngine();
        LayerLightEventListener blockLightListener = lightEngine.getLayerListener(LightLayer.BLOCK);
        LayerLightEventListener skyLightListener = lightEngine.getLayerListener(LightLayer.SKY);
        LevelLightEngine levelLightEngine = this.level.getLightEngine();
        while (it.hasNext()) {
            Vector3i chunkPosExceptYWorldPos = it.next();
            LevelChunk chunk = this.level.getChunk(chunkPosExceptYWorldPos.x, chunkPosExceptYWorldPos.z);
            if (chunk == null) {
                it.remove();
                continue;
            }
            SectionPos sectionPos = SectionPos.of((ChunkPos)chunk.getPos(), (int)0);
            if (!levelLightEngine.lightOnInSection(sectionPos)) continue;
            int worldX = chunkPosExceptYWorldPos.x * 16;
            int worldZ = chunkPosExceptYWorldPos.z * 16;
            SectionPos pos = SectionPos.of((int)SectionPos.blockToSectionCoord((int)worldX), (int)SectionPos.blockToSectionCoord((int)(chunkPosExceptYWorldPos.y + 1)), (int)SectionPos.blockToSectionCoord((int)worldZ));
            DataLayer layerBlock = blockLightListener.getDataLayerData(pos);
            DataLayer layerSky = skyLightListener.getDataLayerData(pos);
            int topSectionY = this.level.getMaxSection() - 1;
            if (layerBlock == null) continue;
            while (layerSky == null && pos.y() + 1 < topSectionY) {
                pos = SectionPos.of((int)pos.x(), (int)(pos.y() + 1), (int)pos.z());
                layerSky = skyLightListener.getDataLayerData(pos);
            }
            if (layerSky == null && !(skyLightListener instanceof LayerLightEventListener.DummyLightLayerEventListener)) continue;
            int relativeY = SectionPos.sectionRelative((int)(chunkPosExceptYWorldPos.y + 1));
            LayerLightUpdate layerUpdate = new LayerLightUpdate(this);
            layerUpdate.chunkX = chunkPosExceptYWorldPos.x;
            layerUpdate.chunkZ = chunkPosExceptYWorldPos.z;
            layerUpdate.layerY = (short)chunkPosExceptYWorldPos.y;
            if (layerSky == null) {
                int sky = 0;
                for (int zo = 0; zo < 16; ++zo) {
                    int zindex = zo << 4;
                    for (int xo = 0; xo < 16; ++xo) {
                        block = java.lang.Math.min(15, layerBlock.get(xo, relativeY, zo));
                        layerUpdate.lightData[zindex + xo] = (byte)(sky << 4 | block);
                    }
                }
            } else {
                for (int zo = 0; zo < 16; ++zo) {
                    int zindex = zo << 4;
                    for (int xo = 0; xo < 16; ++xo) {
                        int sky = java.lang.Math.min(15, layerSky.get(xo, relativeY, zo));
                        block = java.lang.Math.min(15, layerBlock.get(xo, relativeY, zo));
                        layerUpdate.lightData[zindex + xo] = (byte)(sky << 4 | block);
                    }
                }
            }
            asyncUpdates.add(layerUpdate);
            it.remove();
        }
        if (!asyncUpdates.isEmpty()) {
            events.add(() -> this.lambda$applyOceanLayerLightUpdates$3((List)asyncUpdates));
        }
    }

    public void computeEntityOffset(Matrix4f transformation, @Nullable Matrix3f normal, Level level, Entity entity, double x, double y, double z, double offsetX, double offsetY, double offsetZ, float yRot, float renderPercent) {
        Entity vehicle = entity.getVehicle();
        if (StarterClient.valkyrienSkies && vehicle == null && ValkyrienSkies.hasShipMount(entity) != null) {
            ValkyrienSkies.doEntityOnShipTransformation(transformation, entity, renderPercent);
            return;
        }
        if (StarterClient.sable && vehicle == null && SableCreate.hasShipMount(entity) != null) {
            return;
        }
        EntityOcean entityOcean = (EntityOcean)entity;
        double yOffset = ((EntityOcean)entity).getPhysicsYOffset(renderPercent);
        transformation.translate(0.0f, (float)yOffset, 0.0f);
        if (entity instanceof Boat || vehicle != null && vehicle instanceof Boat) {
            float actualYRot = 0.0f;
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity)entity;
                actualYRot = Mth.rotLerp((float)renderPercent, (float)living.yBodyRotO, (float)living.yBodyRot);
            } else {
                actualYRot = entity.getViewYRot(renderPercent);
            }
            float currentYRot = (float)(-java.lang.Math.toRadians(actualYRot - (float)java.lang.Math.PI));
            double forwardZ = java.lang.Math.cos(currentYRot);
            double forwardX = java.lang.Math.sin(currentYRot);
            double leftX = forwardZ;
            double leftZ = -forwardX;
            double roll = entityOcean.getPhysicsRoll(renderPercent);
            double pitch = entityOcean.getPhysicsPitch(renderPercent);
            float diffX = 0.0f;
            float diffY = 0.375f;
            float diffZ = 0.0f;
            float diffRot = 0.0f;
            if (!(entity instanceof Boat)) {
                double ex = Mth.lerp((double)renderPercent, (double)entity.xo, (double)entity.getX());
                double ey = Mth.lerp((double)renderPercent, (double)entity.yo, (double)entity.getY());
                double ez = Mth.lerp((double)renderPercent, (double)entity.zo, (double)entity.getZ());
                double bx = Mth.lerp((double)renderPercent, (double)vehicle.xo, (double)vehicle.getX());
                double by = Mth.lerp((double)renderPercent, (double)vehicle.yo, (double)vehicle.getY());
                double bz = Mth.lerp((double)renderPercent, (double)vehicle.zo, (double)vehicle.getZ());
                diffX = (float)((double)diffX + (bx - ex));
                diffY = (float)((double)diffY + (by - ey));
                diffZ = (float)((double)diffZ + (bz - ez));
            }
            if (vehicle != null && vehicle instanceof Boat) {
                diffRot = vehicle.getViewYRot(renderPercent) - actualYRot;
            }
            float ox = (float)(x - offsetX) + diffX;
            float oy = (float)(y - offsetY) + diffY;
            float oz = (float)(z - offsetZ) + diffZ;
            transformation.translate(ox, oy, oz);
            transformation.rotate((Quaternionfc)Axis.YP.rotationDegrees(-diffRot));
            transformation.rotate((Quaternionfc)Axis.of((Vector3f)new Vector3f((float)forwardX, 0.0f, (float)forwardZ)).rotationDegrees((float)(-java.lang.Math.toDegrees(roll))));
            transformation.rotate((Quaternionfc)Axis.of((Vector3f)new Vector3f((float)leftX, 0.0f, (float)leftZ)).rotationDegrees((float)java.lang.Math.toDegrees(pitch)));
            if (normal != null) {
                normal.rotate((Quaternionfc)Axis.of((Vector3f)new Vector3f((float)forwardX, 0.0f, (float)forwardZ)).rotationDegrees((float)(-java.lang.Math.toDegrees(roll))));
                normal.rotate((Quaternionfc)Axis.of((Vector3f)new Vector3f((float)leftX, 0.0f, (float)leftZ)).rotationDegrees((float)java.lang.Math.toDegrees(pitch)));
            }
            transformation.rotate((Quaternionfc)Axis.YP.rotationDegrees(diffRot));
            transformation.translate(-ox, -oy, -oz);
        }
    }

    public double computeYOffset(Level level, Entity entity, float renderPercent) {
        double wz;
        double wy;
        double wx;
        Entity vehicle = entity.getVehicle();
        if (vehicle != null) {
            wx = Mth.lerp((double)renderPercent, (double)vehicle.xOld, (double)vehicle.getX());
            wy = Mth.lerp((double)renderPercent, (double)vehicle.yOld, (double)vehicle.getY());
            wz = Mth.lerp((double)renderPercent, (double)vehicle.zOld, (double)vehicle.getZ());
        } else {
            wx = Mth.lerp((double)renderPercent, (double)entity.xOld, (double)entity.getX());
            wy = Mth.lerp((double)renderPercent, (double)entity.yOld, (double)entity.getY());
            wz = Mth.lerp((double)renderPercent, (double)entity.zOld, (double)entity.getZ());
        }
        return this.calculateYOffset(wx, wy, wz);
    }

    public double computeYOffset(Level level, Entity entity) {
        return this.calculateYOffset(entity.getX(), entity.getY(), entity.getZ());
    }

    public void loadOceanLayerLights(int x, short layerPosY, int z) {
        this.oceanLayerLightUpdates.add(new Vector3i(x, (int)layerPosY, z));
    }

    public void replaceOceanMeshes(List<OceanSurface> generatedMeshes) {
        for (OceanSurface oceanSurface : generatedMeshes) {
            OceanSurface oldSurface = oceanSurface.oceanLayer.getOceanSurface();
            if (oldSurface == null) {
                oceanSurface.oceanLayer.setOceanSurface(oceanSurface);
            }
            OceanSurface usedSurface = oceanSurface.oceanLayer.getOceanSurface();
            if (oceanSurface.removeAllMeshes) {
                for (Long2ObjectMap.Entry entry : usedSurface.meshes.long2ObjectEntrySet()) {
                    long index = entry.getLongKey();
                    this.oceanMeshes.remove(index);
                    mesh = (OceanMesh)entry.getValue();
                    if (mesh == null) continue;
                    mesh.destroy(this);
                }
                usedSurface.meshes.clear();
            } else {
                if (usedSurface != oceanSurface) {
                    if (oceanSurface.singleMesh) {
                        for (Long2ObjectMap.Entry entry : usedSurface.meshes.long2ObjectEntrySet()) {
                            long index = entry.getLongKey();
                            this.oceanMeshes.remove(index);
                            mesh = (OceanMesh)entry.getValue();
                            if (mesh == null) continue;
                            mesh.destroy(this);
                        }
                        usedSurface.meshes.clear();
                    }
                    if (oldSurface != null && oldSurface.singleMesh) {
                        Long2ObjectMap.Entry entry;
                        LongOpenHashSet removeLater = new LongOpenHashSet();
                        entry = oldSurface.meshes.long2ObjectEntrySet().iterator();
                        while (entry.hasNext()) {
                            Long2ObjectMap.Entry entry2 = (Long2ObjectMap.Entry)entry.next();
                            long index = entry2.getLongKey();
                            this.oceanMeshes.remove(index);
                            OceanMesh mesh = (OceanMesh)entry2.getValue();
                            removeLater.add(index);
                            if (mesh == null) continue;
                            mesh.destroy(this);
                        }
                        oldSurface.meshes.clear();
                        LongIterator it = removeLater.longIterator();
                        while (it.hasNext()) {
                            usedSurface.getMeshes().remove(it.nextLong());
                        }
                    }
                }
                ObjectIterator it = oceanSurface.meshes.long2ObjectEntrySet().iterator();
                Long2ObjectOpenHashMap addLater = new Long2ObjectOpenHashMap();
                LongOpenHashSet removeLater = new LongOpenHashSet();
                while (it.hasNext()) {
                    Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)it.next();
                    long index = entry.getLongKey();
                    OceanMesh newMesh = (OceanMesh)entry.getValue();
                    OceanMesh oldMesh = null;
                    if (newMesh.mesh == null) {
                        oldMesh = (OceanMesh)this.oceanMeshes.remove(index);
                        removeLater.add(index);
                        if (oldMesh == null) continue;
                        oldMesh.destroy(this);
                        continue;
                    }
                    oldMesh = (OceanMesh)this.oceanMeshes.put(index, (Object)newMesh);
                    addLater.put(index, (Object)newMesh);
                    newMesh.createGLObjects(this, oldMesh);
                }
                for (Long2ObjectMap.Entry entry : addLater.long2ObjectEntrySet()) {
                    usedSurface.getMeshes().put(entry.getLongKey(), (Object)((OceanMesh)entry.getValue()));
                }
                LongIterator longIt = removeLater.longIterator();
                while (longIt.hasNext()) {
                    usedSurface.getMeshes().remove(longIt.nextLong());
                }
            }
            usedSurface.set(oceanSurface);
        }
    }

    public ShortSet getLightUpdates(long chunkIndex) {
        ShortSet lightUpdates = (ShortSet)this.lightUpdates.get(chunkIndex);
        if (lightUpdates == null) {
            lightUpdates = new ShortOpenHashSet();
            this.lightUpdates.put(chunkIndex, (Object)lightUpdates);
        }
        return lightUpdates;
    }

    public void queueEvent(Runnable runnable) {
        this.queue.add(runnable);
    }

    public Short2ObjectMap<ProxyOceanLayer> getOceanLayers() {
        return this.oceanLayers;
    }

    public Long2ObjectMap<OceanMesh> getOceanMeshes() {
        return this.oceanMeshes;
    }

    public Set<OceanBlockUpdate> getBlockUpdates() {
        return this.blockUpdates;
    }

    public float getOceanHeight() {
        return 13.0f * ConfigClient.oceanWaveHeightMultiplier * this.oceanHeightMultiplier;
    }

    public static float getMaxOceanHeight() {
        float storminess = ConfigClient.oceanWeatherClear;
        storminess += ConfigClient.oceanWeatherRain;
        float oceanHeightMultiplier = 0.7f + (storminess += ConfigClient.oceanWeatherThunder) * 0.4f;
        return 13.0f * ConfigClient.oceanWaveHeightMultiplier * oceanHeightMultiplier;
    }

    public void addChunkColumn(List<OceanChunkCreator> asyncChunkCreation, int chunkX, int chunkZ) {
        this.processor.queueEvent(() -> {
            ObjectArrayList chunkColumn = new ObjectArrayList();
            for (int i = 0; i < asyncChunkCreation.size(); ++i) {
                chunkColumn.add(((OceanChunkCreator)asyncChunkCreation.get(i)).create());
            }
            this.processor.addChunkColumn(chunkColumn);
        });
    }

    public void removeChunkColumn(int chunkX, int chunkZ) {
        this.processor.queueEvent(() -> this.processor.removeChunkColumn(chunkX, chunkZ));
    }

    public void removeAll() {
        this.processor.queueEvent(() -> this.processor.removeAll());
    }

    public Vector2f getWaterMidCoord() {
        return this.waterMidCoord;
    }

    public Vector4f getWaterUVCoord() {
        return this.waterCoord;
    }

    public Level getLevel() {
        return this.level;
    }

    public void removeOceanLayer(short layerPosY) {
        ProxyOceanLayer layer = (ProxyOceanLayer)this.oceanLayers.remove(layerPosY);
        if (layer != null) {
            layer.destroy();
        }
    }

    public void clearOceanLayers() {
        for (ProxyOceanLayer layer : this.oceanLayers.values()) {
            layer.destroy();
        }
        this.oceanLayers.clear();
    }

    public static void createWaterSplash(Level level, ResourceLocation resource, SimpleParticleType type, double wx, double wy, double wz, double vx, double vy, double vz, double randomOffset, double intensity, int amount) {
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        Map<ResourceLocation, ParticleProvider<?>> provider = ((MixinParticleEngineAccessor)particleEngine).getParticleProviders();
        ParticleProvider<?> particleProvider = provider.get(resource);
        for (int i = 0; i < amount; ++i) {
            double angle = java.lang.Math.PI * 2 * (double)Math.random();
            double radius = 1.0;
            double x = radius * java.lang.Math.cos(angle);
            double z = radius * java.lang.Math.sin(angle);
            particleEngine.add(particleProvider.createParticle((ParticleOptions)type, (ClientLevel)level, wx + x * randomOffset + ((double)Math.random() - 0.5) * 0.3, wy + (double)Math.random() * 0.4, wz + z * randomOffset + ((double)Math.random() - 0.5) * 0.3, x * 0.2 * intensity + ((double)Math.random() - 0.5) * 0.3 + vx, 0.181 + vy, z * 0.2 * intensity + ((double)Math.random() - 0.5) * 0.3 + vz));
        }
    }

    public static void createWaterSplash(Level level, double wx, double wy, double wz, double vx, double vy, double vz, double randomOffset, double intensity, int amount) {
        OceanWorld.createWaterSplash(level, WeatherParticlesRegistry.SPLASH_RESOURCE, WeatherEffects.PHYSICS_SPLASH, wx, wy, wz, vx, vy, vz, randomOffset, intensity, amount);
    }

    public static void createExplosionWaterSplash(Level level, double wx, double wy, double wz, double vx, double vy, double vz, double randomOffset, double intensity, int amount) {
        OceanWorld.createWaterSplash(level, WeatherParticlesRegistry.SPLASH_EXPLOSION_RESOURCE, WeatherEffects.PHYSICS_SPLASH_EXPLOSION, wx, wy, wz, vx, vy, vz, randomOffset, intensity, amount);
    }

    public ArenaBuffer getOceanVertexData() {
        if (this.oceanVertexData == null) {
            this.createGLObjects();
        }
        return this.oceanVertexData;
    }

    public ArenaBuffer getOceanIndexData() {
        if (this.oceanIndexData == null) {
            this.createGLObjects();
        }
        return this.oceanIndexData;
    }

    public int getGPUMemoryUsage() {
        if (this.oceanVertexData == null) {
            return 0;
        }
        return this.oceanVertexData.getTotalSize() + this.oceanIndexData.getTotalSize();
    }

    private void createGLObjects() {
        this.oceanVAO = GL32C.glGenVertexArrays();
        this.format = StarterClient.optifabric ? new VertexFormat(Data.POSITION_SHADER, Data.TEX_COORD_SHADER, Data.LIGHT, Data.COLOR_SHADER) : new VertexFormat(Data.POSITION_SHADER, Data.TEX_COORD_SHADER, Data.LIGHT_SHADER, Data.COLOR_SHADER);
        this.oceanVertexData = new ArenaBuffer(0x100000 * this.format.getStride());
        this.oceanIndexData = new ArenaBuffer(0x400000, 34963);
        StateTracker.bindVertexArray(this.oceanVAO);
    }

    public void bindForRendering() {
        if (this.oceanVAO == -1) {
            return;
        }
        StateTracker.bindVertexArray(this.oceanVAO);
        this.oceanVertexData.bind();
        this.format.bindAttributeFormat();
        this.oceanIndexData.bind();
    }

    public void destroy() {
        this.processor.shutdown();
        this.processor.join();
        Runnable event = null;
        while ((event = this.queue.poll()) != null) {
            event.run();
        }
        for (ProxyOceanLayer layer : this.oceanLayers.values()) {
            layer.destroy();
        }
        for (OceanMesh oceanMesh : this.oceanMeshes.values()) {
            oceanMesh.destroy(this);
        }
        if (this.oceanVertexData != null) {
            this.oceanVertexData.destroy();
        }
        if (this.oceanIndexData != null) {
            this.oceanIndexData.destroy();
        }
        if (this.oceanVAO != -1) {
            GL32C.glDeleteVertexArrays((int)this.oceanVAO);
        }
        this.oceanMeshes.clear();
    }

    private /* synthetic */ void lambda$applyOceanLayerLightUpdates$3(List asyncUpdates) {
        for (LayerLightUpdate update : asyncUpdates) {
            this.processor.updateLayerLight(update.chunkX, update.layerY, update.chunkZ, update.lightData);
        }
    }

    private /* synthetic */ void lambda$applyLightUpdates$2(List asyncUpdates) {
        for (LightUpdate update : asyncUpdates) {
            this.processor.updateLight(update.posX, update.posY, update.posZ, update.lightData);
        }
    }

    private /* synthetic */ void lambda$applyBlockUpdates$1(List updates) {
        for (OceanBlockUpdate update : updates) {
            int lz;
            int ly;
            int lx;
            byte data;
            int rz;
            int ry;
            BlockPos pos = update.pos;
            byte state = update.state;
            int rx = pos.getX();
            Object chunk = this.processor.getChunkWorldPos(rx, ry = pos.getY(), rz = pos.getZ());
            if (chunk == null || (data = ((IChunk)chunk).getData(lx = rx & 0xF, ly = ry & 0xF, lz = rz & 0xF)) == state) continue;
            ((IChunk)chunk).setData(lx, ly, lz, state);
            this.processor.blockChanged(rx, ry, rz, data, state);
        }
    }

    private static /* synthetic */ void lambda$update$0(List events) {
        for (Runnable task : events) {
            task.run();
        }
    }

    private class LightUpdate {
        int posX;
        int posY;
        int posZ;
        byte lightData;

        private LightUpdate(OceanWorld oceanWorld) {
        }
    }

    private class LayerLightUpdate {
        int chunkX;
        int chunkZ;
        short layerY;
        byte[] lightData = new byte[256];

        public LayerLightUpdate(OceanWorld oceanWorld) {
        }
    }
}

