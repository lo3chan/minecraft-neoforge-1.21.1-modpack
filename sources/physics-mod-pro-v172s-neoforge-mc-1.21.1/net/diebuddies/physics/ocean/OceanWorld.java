package net.diebuddies.physics.ocean;

import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
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
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.mixins.MixinParticleEngineAccessor;
import net.diebuddies.opengl.ArenaBuffer;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.StateTracker;
import net.diebuddies.opengl.VertexFormat;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.thread.OceanChunkCreator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.LayerLightEventListener.DummyLightLayerEventListener;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL32C;

public class OceanWorld {
   private static final float BASE_OCEAN_HEIGHT = 13.0F;
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
   private MutableBlockPos tmp = new MutableBlockPos();

   public OceanWorld(PhysicsWorld world, Level level) {
      this.world = world;
      this.level = level;
      this.queue = new ConcurrentLinkedQueue<>();
      this.blockUpdates = new ObjectOpenHashSet();
      this.oceanMeshes = new Long2ObjectOpenHashMap();
      this.lightUpdates = new Long2ObjectOpenHashMap();
      this.oceanLayerLightUpdates = new ObjectOpenHashSet();
      this.oceanLayers = new Short2ObjectOpenHashMap();
      this.weatherSpeedMultiplier = 1.0F;
      this.oceanHeightMultiplier = 1.0F;
      TextureAtlasSprite waterTexture = Minecraft.getInstance()
         .getModelManager()
         .getBlockModelShaper()
         .getBlockModel(Blocks.WATER.defaultBlockState())
         .getParticleIcon();
      this.waterCoord = new Vector4f(waterTexture.getU0(), waterTexture.getU1(), waterTexture.getV0(), waterTexture.getV1());
      this.waterMidCoord = new Vector2f(this.waterCoord.x + this.waterCoord.y, this.waterCoord.z + this.waterCoord.w).mul(0.5F);
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

      if (ConfigClient.areOceanPhysicsEnabled()) {
         List<Runnable> events = new ObjectArrayList();
         this.applyBlockUpdates(events);
         this.applyLightUpdates(events);
         this.applyOceanLayerLightUpdates(events);
         if (!events.isEmpty()) {
            this.processor.queueEvent(() -> {
               for (Runnable task : events) {
                  task.run();
               }
            });
         }

         Minecraft minecraft = Minecraft.getInstance();
         float renderPercent = minecraft.getTimer().getGameTimeDeltaPartialTick(true);
         float storminess = ConfigClient.oceanWeatherClear;
         storminess += this.level.getRainLevel(renderPercent) * ConfigClient.oceanWeatherRain;
         storminess += this.level.getThunderLevel(renderPercent) * ConfigClient.oceanWeatherThunder;
         this.weatherSpeedMultiplier = 0.9F + storminess * 0.2F;
         this.oceanHeightMultiplier = 0.7F + storminess * 0.4F;
         this.oceanTime = (float)(this.oceanTime + diff * this.weatherSpeedMultiplier * ConfigClient.oceanBaseSpeed);
         this.globalTime = (float)(this.globalTime + diff);
         if (ConfigClient.oceanRipples) {
            this.updateRipple(diff);
         }
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
      ObjectIterator var3 = this.oceanLayers.values().iterator();

      while (var3.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var3.next();
         layer.update(diff);
      }
   }

   public void spawnRainRipple(int lifetime, float scale, double x, double y, double z) {
      ProxyOceanLayer layer = this.getOceanLayer(x, y, z);
      if (layer != null) {
         RainParticle particle = new RainParticle(lifetime, scale, x, y, z);
         particle.baseAlpha = 0.35F;
         layer.addRippleParticle(particle);
      }
   }

   public void spawnRipple(int amount, int lifetime, float scale, double x, double y, double z, double speed) {
      ProxyOceanLayer layer = this.getOceanLayer(x, y, z);
      if (layer != null) {
         double slice = 6.283185307179586 / amount;

         for (int i = 0; i < amount; i++) {
            double angle = slice * i;
            RippleParticle particle = new RippleParticle(lifetime, x, y, z, Math.cos(angle) * speed, 0.0, Math.sin(angle) * speed);
            particle.scale = scale;
            particle.baseAlpha = 0.35F;
            layer.addRippleParticle(particle);
         }
      }
   }

   public void spawnAngularRipple(
      int amount, int lifetime, double x, double y, double z, double dirx, double dirz, double angleRange, double speed, double delay
   ) {
      ProxyOceanLayer layer = this.getOceanLayer(x, y, z);
      if (layer != null) {
         double slice = 6.283185307179586 / amount;
         double movementAngle = Math.atan2(dirz, dirx);

         for (int i = 0; i < amount; i++) {
            double angle = slice * i;
            double px = Math.cos(angle);
            double pz = Math.sin(angle);
            double pointAngle = Math.atan2(pz, px);
            double diff = Math.abs(pointAngle - movementAngle);
            if (diff <= angleRange || diff >= 6.283185307179586 - angleRange) {
               if (diff >= 6.283185307179586 - angleRange) {
                  diff -= 6.283185307179586;
               }

               RippleParticle particle = new RippleParticle(lifetime, x - px * delay * speed, y, z - pz * delay * speed, px * speed, 0.0, pz * speed);
               particle.baseAlpha = net.diebuddies.math.Math.clamp(1.0F - (float)Math.abs(diff / angleRange), 0.0F, 1.0F);
               layer.addRippleParticle(particle);
            }
         }
      }
   }

   public float getOceanTime() {
      return this.oceanTime;
   }

   public float getGlobalTime() {
      return this.globalTime;
   }

   private void applyBlockUpdates(List<Runnable> events) {
      if (!this.blockUpdates.isEmpty()) {
         List<OceanBlockUpdate> updates = new ObjectArrayList(this.blockUpdates);
         events.add(() -> {
            for (OceanBlockUpdate update : updates) {
               BlockPos pos = update.pos;
               byte state = update.state;
               int rx = pos.getX();
               int ry = pos.getY();
               int rz = pos.getZ();
               IChunk<?> chunk = this.processor.getChunkWorldPos(rx, ry, rz);
               if (chunk != null) {
                  int lx = rx & 15;
                  int ly = ry & 15;
                  int lz = rz & 15;
                  byte data = chunk.getData(lx, ly, lz);
                  if (data != state) {
                     chunk.setData(lx, ly, lz, state);
                     this.processor.blockChanged(rx, ry, rz, data, state);
                  }
               }
            }
         });
         this.blockUpdates.clear();
      }
   }

   private void applyLightUpdates(List<Runnable> events) {
      if (!this.lightUpdates.isEmpty()) {
         List<OceanWorld.LightUpdate> asyncUpdates = new ObjectArrayList();
         Iterator<Entry<ShortSet>> it = this.lightUpdates.long2ObjectEntrySet().iterator();
         LevelLightEngine levelLightEngine = this.level.getLightEngine();

         while (it.hasNext()) {
            Entry<ShortSet> entry = it.next();
            long chunkIndex = entry.getLongKey();
            int x = SectionPos.x(chunkIndex);
            int y = SectionPos.y(chunkIndex);
            int z = SectionPos.z(chunkIndex);
            ShortSet positions = (ShortSet)entry.getValue();
            LevelChunk chunk = this.level.getChunk(x, z);
            if (!positions.isEmpty() && chunk != null) {
               SectionPos sectionPos = SectionPos.of(chunk.getPos(), 0);
               if (levelLightEngine.lightOnInSection(sectionPos)) {
                  ShortIterator blockIt = positions.iterator();

                  while (blockIt.hasNext()) {
                     short localPos = blockIt.nextShort();
                     byte lx = (byte)(localPos >> 8 & 15);
                     byte ly = (byte)(localPos >> 4 & 15);
                     byte lz = (byte)(localPos & 15);
                     int wx = x * 16 + lx;
                     int wy = y * 16 + ly;
                     int wz = z * 16 + lz;
                     if (wy >= this.level.getMinBuildHeight() && wy < this.level.getMaxBuildHeight()) {
                        this.tmp.set(wx, wy, wz);
                        int sky = net.diebuddies.math.Math.clamp(this.level.getBrightness(LightLayer.SKY, this.tmp), 0, 15);
                        int block = net.diebuddies.math.Math.clamp(this.level.getBrightness(LightLayer.BLOCK, this.tmp), 0, 15);
                        OceanWorld.LightUpdate update = new OceanWorld.LightUpdate();
                        update.posX = wx;
                        update.posY = wy;
                        update.posZ = wz;
                        update.lightData = (byte)(sky << 4 | block);
                        asyncUpdates.add(update);
                        blockIt.remove();
                     } else {
                        blockIt.remove();
                     }
                  }

                  if (positions.isEmpty()) {
                     it.remove();
                  }
               }
            } else {
               it.remove();
            }
         }

         if (!asyncUpdates.isEmpty()) {
            events.add(() -> {
               for (OceanWorld.LightUpdate updatex : asyncUpdates) {
                  this.processor.updateLight(updatex.posX, updatex.posY, updatex.posZ, updatex.lightData);
               }
            });
         }
      }
   }

   public double calculateYOffset(double x, double y, double z) {
      double maxOffset = 0.0;
      double maxMagnitude = 0.0;
      ObjectIterator var11 = this.oceanLayers.values().iterator();

      while (var11.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var11.next();
         double offset = layer.calculateYOffset(this, x, y, z);
         double magnitude = Math.abs(offset);
         if (magnitude > maxMagnitude) {
            maxMagnitude = magnitude;
            maxOffset = offset;
         }
      }

      return maxOffset;
   }

   public Vector3d calculateWaveForce(double x, double y, double z) {
      Vector3d maxOffset = null;
      double maxMagnitude = 0.0;
      ObjectIterator var10 = this.oceanLayers.values().iterator();

      while (var10.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var10.next();
         Vector3d offset = layer.calculateWaveNormal(this, x, y, z);
         if (offset != null) {
            double magnitude = offset.lengthSquared();
            if (magnitude > maxMagnitude) {
               maxMagnitude = magnitude;
               maxOffset = offset;
            }
         }
      }

      return maxOffset;
   }

   public boolean isInsideOceanWater(double x, double y, double z) {
      ObjectIterator var7 = this.oceanLayers.values().iterator();

      while (var7.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var7.next();
         if (layer.isInsideOceanWater(this, x, y, z)) {
            return true;
         }
      }

      return false;
   }

   public boolean isInsideOceanRange(double x, double y, double z) {
      ObjectIterator var7 = this.oceanLayers.values().iterator();

      while (var7.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var7.next();
         if (layer.isInsideTextureOceanRange(this, x, y, z)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public ProxyOceanLayer getOceanLayer(double x, double y, double z) {
      ObjectIterator var7 = this.oceanLayers.values().iterator();

      while (var7.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var7.next();
         if (layer.isInsideTextureOceanRangeNoWarp(this, x, y, z)) {
            return layer;
         }
      }

      return null;
   }

   private void applyOceanLayerLightUpdates(List<Runnable> events) {
      if (!this.oceanLayerLightUpdates.isEmpty()) {
         Iterator<Vector3i> it = this.oceanLayerLightUpdates.iterator();
         List<OceanWorld.LayerLightUpdate> asyncUpdates = new ObjectArrayList();
         LevelLightEngine lightEngine = this.level.getLightEngine();
         LayerLightEventListener blockLightListener = lightEngine.getLayerListener(LightLayer.BLOCK);
         LayerLightEventListener skyLightListener = lightEngine.getLayerListener(LightLayer.SKY);
         LevelLightEngine levelLightEngine = this.level.getLightEngine();

         while (it.hasNext()) {
            Vector3i chunkPosExceptYWorldPos = it.next();
            LevelChunk chunk = this.level.getChunk(chunkPosExceptYWorldPos.x, chunkPosExceptYWorldPos.z);
            if (chunk == null) {
               it.remove();
            } else {
               SectionPos sectionPos = SectionPos.of(chunk.getPos(), 0);
               if (levelLightEngine.lightOnInSection(sectionPos)) {
                  int worldX = chunkPosExceptYWorldPos.x * 16;
                  int worldZ = chunkPosExceptYWorldPos.z * 16;
                  SectionPos pos = SectionPos.of(
                     SectionPos.blockToSectionCoord(worldX),
                     SectionPos.blockToSectionCoord(chunkPosExceptYWorldPos.y + 1),
                     SectionPos.blockToSectionCoord(worldZ)
                  );
                  DataLayer layerBlock = blockLightListener.getDataLayerData(pos);
                  DataLayer layerSky = skyLightListener.getDataLayerData(pos);
                  int topSectionY = this.level.getMaxSection() - 1;
                  if (layerBlock != null) {
                     while (layerSky == null && pos.y() + 1 < topSectionY) {
                        pos = SectionPos.of(pos.x(), pos.y() + 1, pos.z());
                        layerSky = skyLightListener.getDataLayerData(pos);
                     }

                     if (layerSky != null || skyLightListener instanceof DummyLightLayerEventListener) {
                        int relativeY = SectionPos.sectionRelative(chunkPosExceptYWorldPos.y + 1);
                        OceanWorld.LayerLightUpdate layerUpdate = new OceanWorld.LayerLightUpdate();
                        layerUpdate.chunkX = chunkPosExceptYWorldPos.x;
                        layerUpdate.chunkZ = chunkPosExceptYWorldPos.z;
                        layerUpdate.layerY = (short)chunkPosExceptYWorldPos.y;
                        if (layerSky == null) {
                           int sky = 0;

                           for (int zo = 0; zo < 16; zo++) {
                              int zindex = zo << 4;

                              for (int xo = 0; xo < 16; xo++) {
                                 int block = Math.min(15, layerBlock.get(xo, relativeY, zo));
                                 layerUpdate.lightData[zindex + xo] = (byte)(sky << 4 | block);
                              }
                           }
                        } else {
                           for (int zo = 0; zo < 16; zo++) {
                              int zindex = zo << 4;

                              for (int xo = 0; xo < 16; xo++) {
                                 int sky = Math.min(15, layerSky.get(xo, relativeY, zo));
                                 int block = Math.min(15, layerBlock.get(xo, relativeY, zo));
                                 layerUpdate.lightData[zindex + xo] = (byte)(sky << 4 | block);
                              }
                           }
                        }

                        asyncUpdates.add(layerUpdate);
                        it.remove();
                     }
                  }
               }
            }
         }

         if (!asyncUpdates.isEmpty()) {
            events.add(() -> {
               for (OceanWorld.LayerLightUpdate update : asyncUpdates) {
                  this.processor.updateLayerLight(update.chunkX, update.layerY, update.chunkZ, update.lightData);
               }
            });
         }
      }
   }

   public void computeEntityOffset(
      Matrix4f transformation,
      @Nullable Matrix3f normal,
      Level level,
      Entity entity,
      double x,
      double y,
      double z,
      double offsetX,
      double offsetY,
      double offsetZ,
      float yRot,
      float renderPercent
   ) {
      Entity vehicle = entity.getVehicle();
      if (StarterClient.valkyrienSkies && vehicle == null && ValkyrienSkies.hasShipMount(entity) != null) {
         ValkyrienSkies.doEntityOnShipTransformation(transformation, entity, renderPercent);
      } else if (!StarterClient.sable || vehicle != null || SableCreate.hasShipMount(entity) == null) {
         EntityOcean entityOcean = (EntityOcean)entity;
         double yOffset = ((EntityOcean)entity).getPhysicsYOffset(renderPercent);
         transformation.translate(0.0F, (float)yOffset, 0.0F);
         if (entity instanceof Boat || vehicle != null && vehicle instanceof Boat) {
            float actualYRot = 0.0F;
            if (entity instanceof LivingEntity living) {
               actualYRot = Mth.rotLerp(renderPercent, living.yBodyRotO, living.yBodyRot);
            } else {
               actualYRot = entity.getViewYRot(renderPercent);
            }

            float currentYRot = (float)(-Math.toRadians(actualYRot - 3.1415927F));
            double forwardZ = Math.cos(currentYRot);
            double forwardX = Math.sin(currentYRot);
            double leftZ = -forwardX;
            double roll = entityOcean.getPhysicsRoll(renderPercent);
            double pitch = entityOcean.getPhysicsPitch(renderPercent);
            float diffX = 0.0F;
            float diffY = 0.375F;
            float diffZ = 0.0F;
            float diffRot = 0.0F;
            if (!(entity instanceof Boat)) {
               double ex = Mth.lerp(renderPercent, entity.xo, entity.getX());
               double ey = Mth.lerp(renderPercent, entity.yo, entity.getY());
               double ez = Mth.lerp(renderPercent, entity.zo, entity.getZ());
               double bx = Mth.lerp(renderPercent, vehicle.xo, vehicle.getX());
               double by = Mth.lerp(renderPercent, vehicle.yo, vehicle.getY());
               double bz = Mth.lerp(renderPercent, vehicle.zo, vehicle.getZ());
               diffX = (float)(diffX + (bx - ex));
               diffY = (float)(diffY + (by - ey));
               diffZ = (float)(diffZ + (bz - ez));
            }

            if (vehicle != null && vehicle instanceof Boat) {
               diffRot = vehicle.getViewYRot(renderPercent) - actualYRot;
            }

            float ox = (float)(x - offsetX) + diffX;
            float oy = (float)(y - offsetY) + diffY;
            float oz = (float)(z - offsetZ) + diffZ;
            transformation.translate(ox, oy, oz);
            transformation.rotate(Axis.YP.rotationDegrees(-diffRot));
            transformation.rotate(Axis.of(new Vector3f((float)forwardX, 0.0F, (float)forwardZ)).rotationDegrees((float)(-Math.toDegrees(roll))));
            transformation.rotate(Axis.of(new Vector3f((float)forwardZ, 0.0F, (float)leftZ)).rotationDegrees((float)Math.toDegrees(pitch)));
            if (normal != null) {
               normal.rotate(Axis.of(new Vector3f((float)forwardX, 0.0F, (float)forwardZ)).rotationDegrees((float)(-Math.toDegrees(roll))));
               normal.rotate(Axis.of(new Vector3f((float)forwardZ, 0.0F, (float)leftZ)).rotationDegrees((float)Math.toDegrees(pitch)));
            }

            transformation.rotate(Axis.YP.rotationDegrees(diffRot));
            transformation.translate(-ox, -oy, -oz);
         }
      }
   }

   public double computeYOffset(Level level, Entity entity, float renderPercent) {
      Entity vehicle = entity.getVehicle();
      double wx;
      double wy;
      double wz;
      if (vehicle != null) {
         wx = Mth.lerp(renderPercent, vehicle.xOld, vehicle.getX());
         wy = Mth.lerp(renderPercent, vehicle.yOld, vehicle.getY());
         wz = Mth.lerp(renderPercent, vehicle.zOld, vehicle.getZ());
      } else {
         wx = Mth.lerp(renderPercent, entity.xOld, entity.getX());
         wy = Mth.lerp(renderPercent, entity.yOld, entity.getY());
         wz = Mth.lerp(renderPercent, entity.zOld, entity.getZ());
      }

      return this.calculateYOffset(wx, wy, wz);
   }

   public double computeYOffset(Level level, Entity entity) {
      return this.calculateYOffset(entity.getX(), entity.getY(), entity.getZ());
   }

   public void loadOceanLayerLights(int x, short layerPosY, int z) {
      this.oceanLayerLightUpdates.add(new Vector3i(x, layerPosY, z));
   }

   public void replaceOceanMeshes(List<OceanSurface> generatedMeshes) {
      for (OceanSurface oceanSurface : generatedMeshes) {
         OceanSurface oldSurface = oceanSurface.oceanLayer.getOceanSurface();
         if (oldSurface == null) {
            oceanSurface.oceanLayer.setOceanSurface(oceanSurface);
         }

         OceanSurface usedSurface = oceanSurface.oceanLayer.getOceanSurface();
         if (oceanSurface.removeAllMeshes) {
            ObjectIterator var16 = usedSurface.meshes.long2ObjectEntrySet().iterator();

            while (var16.hasNext()) {
               Entry<OceanMesh> entry = (Entry<OceanMesh>)var16.next();
               long index = entry.getLongKey();
               this.oceanMeshes.remove(index);
               OceanMesh mesh = (OceanMesh)entry.getValue();
               if (mesh != null) {
                  mesh.destroy(this);
               }
            }

            usedSurface.meshes.clear();
         } else {
            if (usedSurface != oceanSurface) {
               if (oceanSurface.singleMesh) {
                  ObjectIterator it = usedSurface.meshes.long2ObjectEntrySet().iterator();

                  while (it.hasNext()) {
                     Entry<OceanMesh> entry = (Entry<OceanMesh>)it.next();
                     long index = entry.getLongKey();
                     this.oceanMeshes.remove(index);
                     OceanMesh mesh = (OceanMesh)entry.getValue();
                     if (mesh != null) {
                        mesh.destroy(this);
                     }
                  }

                  usedSurface.meshes.clear();
               }

               if (oldSurface != null && oldSurface.singleMesh) {
                  LongSet removeLater = new LongOpenHashSet();
                  ObjectIterator var17 = oldSurface.meshes.long2ObjectEntrySet().iterator();

                  while (var17.hasNext()) {
                     Entry<OceanMesh> entry = (Entry<OceanMesh>)var17.next();
                     long index = entry.getLongKey();
                     this.oceanMeshes.remove(index);
                     OceanMesh mesh = (OceanMesh)entry.getValue();
                     removeLater.add(index);
                     if (mesh != null) {
                        mesh.destroy(this);
                     }
                  }

                  oldSurface.meshes.clear();
                  LongIterator it = removeLater.longIterator();

                  while (it.hasNext()) {
                     usedSurface.getMeshes().remove(it.nextLong());
                  }
               }
            }

            Iterator<Entry<OceanMesh>> it = oceanSurface.meshes.long2ObjectEntrySet().iterator();
            Long2ObjectMap<OceanMesh> addLater = new Long2ObjectOpenHashMap();
            LongSet removeLater = new LongOpenHashSet();

            while (it.hasNext()) {
               Entry<OceanMesh> entry = it.next();
               long index = entry.getLongKey();
               OceanMesh newMesh = (OceanMesh)entry.getValue();
               OceanMesh oldMesh = null;
               if (newMesh.mesh == null) {
                  oldMesh = (OceanMesh)this.oceanMeshes.remove(index);
                  removeLater.add(index);
                  if (oldMesh != null) {
                     oldMesh.destroy(this);
                  }
               } else {
                  oldMesh = (OceanMesh)this.oceanMeshes.put(index, newMesh);
                  addLater.put(index, newMesh);
                  newMesh.createGLObjects(this, oldMesh);
               }
            }

            ObjectIterator var25 = addLater.long2ObjectEntrySet().iterator();

            while (var25.hasNext()) {
               Entry<OceanMesh> entry = (Entry<OceanMesh>)var25.next();
               usedSurface.getMeshes().put(entry.getLongKey(), (OceanMesh)entry.getValue());
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
         this.lightUpdates.put(chunkIndex, lightUpdates);
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
      return 13.0F * ConfigClient.oceanWaveHeightMultiplier * this.oceanHeightMultiplier;
   }

   public static float getMaxOceanHeight() {
      float storminess = ConfigClient.oceanWeatherClear;
      storminess += ConfigClient.oceanWeatherRain;
      storminess += ConfigClient.oceanWeatherThunder;
      float oceanHeightMultiplier = 0.7F + storminess * 0.4F;
      return 13.0F * ConfigClient.oceanWaveHeightMultiplier * oceanHeightMultiplier;
   }

   public void addChunkColumn(List<OceanChunkCreator> asyncChunkCreation, int chunkX, int chunkZ) {
      this.processor.queueEvent(() -> {
         List<OceanChunk> chunkColumn = new ObjectArrayList();

         for (int i = 0; i < asyncChunkCreation.size(); i++) {
            chunkColumn.add(asyncChunkCreation.get(i).create());
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
      ObjectIterator var1 = this.oceanLayers.values().iterator();

      while (var1.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var1.next();
         layer.destroy();
      }

      this.oceanLayers.clear();
   }

   public static void createWaterSplash(
      Level level,
      ResourceLocation resource,
      SimpleParticleType type,
      double wx,
      double wy,
      double wz,
      double vx,
      double vy,
      double vz,
      double randomOffset,
      double intensity,
      int amount
   ) {
      ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
      Map<ResourceLocation, ParticleProvider<?>> provider = ((MixinParticleEngineAccessor)particleEngine).getParticleProviders();
      ParticleProvider<ParticleOptions> particleProvider = (ParticleProvider<ParticleOptions>)provider.get(resource);

      for (int i = 0; i < amount; i++) {
         double angle = 6.283185307179586 * net.diebuddies.math.Math.random();
         double radius = 1.0;
         double x = radius * Math.cos(angle);
         double z = radius * Math.sin(angle);
         particleEngine.add(
            particleProvider.createParticle(
               type,
               (ClientLevel)level,
               wx + x * randomOffset + (net.diebuddies.math.Math.random() - 0.5) * 0.3,
               wy + net.diebuddies.math.Math.random() * 0.4,
               wz + z * randomOffset + (net.diebuddies.math.Math.random() - 0.5) * 0.3,
               x * 0.2 * intensity + (net.diebuddies.math.Math.random() - 0.5) * 0.3 + vx,
               0.181 + vy,
               z * 0.2 * intensity + (net.diebuddies.math.Math.random() - 0.5) * 0.3 + vz
            )
         );
      }
   }

   public static void createWaterSplash(
      Level level, double wx, double wy, double wz, double vx, double vy, double vz, double randomOffset, double intensity, int amount
   ) {
      createWaterSplash(level, WeatherParticlesRegistry.SPLASH_RESOURCE, WeatherEffects.PHYSICS_SPLASH, wx, wy, wz, vx, vy, vz, randomOffset, intensity, amount);
   }

   public static void createExplosionWaterSplash(
      Level level, double wx, double wy, double wz, double vx, double vy, double vz, double randomOffset, double intensity, int amount
   ) {
      createWaterSplash(
         level,
         WeatherParticlesRegistry.SPLASH_EXPLOSION_RESOURCE,
         WeatherEffects.PHYSICS_SPLASH_EXPLOSION,
         wx,
         wy,
         wz,
         vx,
         vy,
         vz,
         randomOffset,
         intensity,
         amount
      );
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
      return this.oceanVertexData == null ? 0 : this.oceanVertexData.getTotalSize() + this.oceanIndexData.getTotalSize();
   }

   private void createGLObjects() {
      this.oceanVAO = GL32C.glGenVertexArrays();
      if (StarterClient.optifabric) {
         this.format = new VertexFormat(Data.POSITION_SHADER, Data.TEX_COORD_SHADER, Data.LIGHT, Data.COLOR_SHADER);
      } else {
         this.format = new VertexFormat(Data.POSITION_SHADER, Data.TEX_COORD_SHADER, Data.LIGHT_SHADER, Data.COLOR_SHADER);
      }

      this.oceanVertexData = new ArenaBuffer(1048576 * this.format.getStride());
      this.oceanIndexData = new ArenaBuffer(4194304, 34963);
      StateTracker.bindVertexArray(this.oceanVAO);
   }

   public void bindForRendering() {
      if (this.oceanVAO != -1) {
         StateTracker.bindVertexArray(this.oceanVAO);
         this.oceanVertexData.bind();
         this.format.bindAttributeFormat();
         this.oceanIndexData.bind();
      }
   }

   public void destroy() {
      this.processor.shutdown();
      this.processor.join();
      Runnable event = null;

      while ((event = this.queue.poll()) != null) {
         event.run();
      }

      ObjectIterator var2 = this.oceanLayers.values().iterator();

      while (var2.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var2.next();
         layer.destroy();
      }

      var2 = this.oceanMeshes.values().iterator();

      while (var2.hasNext()) {
         OceanMesh oceanMesh = (OceanMesh)var2.next();
         oceanMesh.destroy(this);
      }

      if (this.oceanVertexData != null) {
         this.oceanVertexData.destroy();
      }

      if (this.oceanIndexData != null) {
         this.oceanIndexData.destroy();
      }

      if (this.oceanVAO != -1) {
         GL32C.glDeleteVertexArrays(this.oceanVAO);
      }

      this.oceanMeshes.clear();
   }

   private class LayerLightUpdate {
      int chunkX;
      int chunkZ;
      short layerY;
      byte[] lightData = new byte[256];

      public LayerLightUpdate() {
      }
   }

   private class LightUpdate {
      int posX;
      int posY;
      int posZ;
      byte lightData;
   }
}
