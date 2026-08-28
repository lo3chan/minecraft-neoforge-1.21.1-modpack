/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  net.minecraft.util.Mth
 *  org.joml.Math
 *  org.joml.Vector2d
 *  org.joml.Vector3d
 */
package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import net.diebuddies.math.Math;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.ocean.OceanLayer;
import net.diebuddies.physics.ocean.OceanSurface;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.ocean.ProxyOceanStorage;
import net.diebuddies.physics.ocean.RippleParticle;
import net.diebuddies.physics.ocean.WaveFunction;
import net.minecraft.util.Mth;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class ProxyOceanLayer {
    private final short layerPosY;
    private int waveOffsetX = Integer.MAX_VALUE;
    private int waveOffsetZ = Integer.MAX_VALUE;
    private Long2ObjectMap<ProxyOceanStorage> oceanStorage;
    private OceanSurface oceanSurface;
    private LinkedList<RippleParticle> rippleParticles;
    private boolean needsRippleUpdate;
    private VAO rippleVAO;
    private int rippleCount;
    private Vector3d rippleOffset = new Vector3d();
    private Vector2d tmpVec2 = new Vector2d();
    private Vector3d tmpVec3 = new Vector3d();

    public ProxyOceanLayer(short layerPosY) {
        this.oceanStorage = new Long2ObjectOpenHashMap();
        this.rippleParticles = new LinkedList();
        this.layerPosY = layerPosY;
    }

    public void update(double diff) {
        Iterator it = this.rippleParticles.iterator();
        while (it.hasNext()) {
            RippleParticle particle = (RippleParticle)it.next();
            particle.update();
            if (particle.lifetime >= 0) continue;
            it.remove();
        }
        this.needsRippleUpdate = true;
    }

    public double calculateYOffset(OceanWorld oceanWorld, double x, double y, double z) {
        if (!this.isInsideOceanRange(oceanWorld, x, y, z)) {
            return 0.0;
        }
        Vector2d position = this.tmpVec2.set(x - (double)this.waveOffsetX, z - (double)this.waveOffsetZ);
        float factor = this.bilinearInterpolationTexture(x, y, z);
        double waveHeight = WaveFunction.waveHeight(position, 13, factor, oceanWorld.getOceanHeight(), oceanWorld.getOceanTime());
        double waveOffset = this.warpFunction(oceanWorld, waveHeight, y);
        return waveOffset;
    }

    public boolean isInsideOceanWater(OceanWorld oceanWorld, double x, double y, double z) {
        if (!this.isInsideOceanRange(oceanWorld, x, y, z)) {
            return false;
        }
        Vector2d position = this.tmpVec2.set(x - (double)this.waveOffsetX, z - (double)this.waveOffsetZ);
        float factor = this.bilinearInterpolationTexture(x, y, z);
        double waveHeight = WaveFunction.waveHeight(position, 13, factor, oceanWorld.getOceanHeight(), oceanWorld.getOceanTime());
        double vanillaSurface = (double)this.layerPosY + 0.8888888;
        double surface = vanillaSurface + waveHeight;
        int ix = Mth.floor((double)x);
        int iz = Mth.floor((double)z);
        double distance = y - (double)this.layerPosY;
        if (distance >= 0.0 ? (double)this.getOceanHeight(ix, iz) <= distance : (double)this.getOceanDepth(ix, iz) <= -distance) {
            return false;
        }
        return y < surface;
    }

    public boolean isInsideTextureOceanRange(OceanWorld oceanWorld, double x, double y, double z) {
        if (!this.isInsideOceanRange(oceanWorld, x, y, z)) {
            return false;
        }
        int ix = Mth.floor((double)x);
        int iz = Mth.floor((double)z);
        double distance = y - (double)this.layerPosY;
        return !(distance >= 0.0 ? (double)this.getOceanHeight(ix, iz) <= distance : (double)this.getOceanDepth(ix, iz) <= -distance);
    }

    public boolean isInsideTextureOceanRangeNoWarp(OceanWorld oceanWorld, double x, double y, double z) {
        if (!this.isInsideOceanRange(oceanWorld, x, y, z, 2.0 + (double)oceanWorld.getOceanHeight() * 0.5)) {
            return false;
        }
        int ix = Mth.floor((double)x);
        int iz = Mth.floor((double)z);
        double distance = y - (double)this.layerPosY;
        return !(distance >= 0.0 ? (double)this.getOceanHeight(ix, iz) <= distance : (double)this.getOceanDepth(ix, iz) <= -distance);
    }

    public Vector3d calculateWaveNormal(OceanWorld oceanWorld, double x, double y, double z) {
        float factor;
        if (!this.isInsideOceanRange(oceanWorld, x, y, z)) {
            return null;
        }
        double vanillaSurface = (double)this.layerPosY + 0.8888888;
        Vector2d position = this.tmpVec2.set(x - (double)this.waveOffsetX, z - (double)this.waveOffsetZ);
        double waveHeight = WaveFunction.waveHeight(position, 13, factor = this.bilinearInterpolationTexture(x, y, z), oceanWorld.getOceanHeight(), oceanWorld.getOceanTime());
        double surface = vanillaSurface + waveHeight;
        if (y > surface && y > vanillaSurface) {
            return null;
        }
        Vector3d waveNormal = WaveFunction.waveNormal(position, factor, oceanWorld.getOceanHeight(), oceanWorld.getOceanTime(), this.tmpVec3);
        waveNormal.y = 1.0;
        if (y < vanillaSurface) {
            waveNormal.y = y > surface ? -waveNormal.y : 0.0;
        }
        double warpFunction = this.warpFunction(oceanWorld, 1.0, y);
        waveNormal.x *= warpFunction;
        waveNormal.z *= warpFunction;
        return waveNormal;
    }

    public boolean isInsideOceanRange(OceanWorld oceanWorld, double x, double y, double z) {
        return this.isInsideOceanRange(oceanWorld, x, y, z, (double)oceanWorld.getOceanHeight() * 0.5);
    }

    public boolean isInsideOceanRange(OceanWorld oceanWorld, double x, double y, double z, double oceanHeight) {
        if (this.waveOffsetX == Integer.MAX_VALUE || this.oceanSurface == null || this.oceanSurface.textureData == null) {
            return false;
        }
        double distance = y - (double)this.layerPosY;
        int ix = Mth.floor((double)x);
        int iz = Mth.floor((double)z);
        return !(java.lang.Math.abs(distance) > oceanHeight) && this.getOcean(ix, iz) != 0;
    }

    private double warpFunction(OceanWorld oceanWorld, double waveHeight, double y) {
        double distance = y - (double)this.layerPosY;
        double halfHeight = (double)oceanWorld.getOceanHeight() * 0.5 - 1.0;
        double warp = 1.0 - Math.clamp(java.lang.Math.abs(distance) - 1.0, 0.0, halfHeight) / halfHeight;
        return waveHeight * warp;
    }

    public float bilinearInterpolationTexture(double worldX, double worldY, double worldZ) {
        double distance = worldY - (double)this.layerPosY;
        int ix = Mth.floor((double)worldX);
        int iz = Mth.floor((double)worldZ);
        int properOffsetX = this.waveOffsetX - this.oceanSurface.offsetX;
        int properOffsetZ = this.waveOffsetZ - this.oceanSurface.offsetZ;
        int uvX = ix - properOffsetX;
        int uvY = iz - properOffsetZ;
        float fractUVX = (float)(worldX - (double)properOffsetX - (double)uvX);
        float fractUVY = (float)(worldZ - (double)properOffsetZ - (double)uvY);
        float data0 = this.textureData(uvX, uvY);
        float data1 = this.textureData(uvX + 1, uvY);
        float data2 = this.textureData(uvX, uvY + 1);
        float data3 = this.textureData(uvX + 1, uvY + 1);
        if (distance >= 0.0) {
            double height = distance;
            if ((double)this.getOceanHeight(ix, iz) <= height) {
                data0 = 0.0f;
                data1 = 0.0f;
                data2 = 0.0f;
                data3 = 0.0f;
            }
            if ((double)this.getOceanHeight(ix + 1, iz) <= height) {
                data1 = 0.0f;
                data3 = 0.0f;
            }
            if ((double)this.getOceanHeight(ix, iz + 1) <= height) {
                data2 = 0.0f;
                data3 = 0.0f;
            }
            if ((double)this.getOceanHeight(ix + 1, iz + 1) <= height) {
                data3 = 0.0f;
            }
            if ((double)this.getOceanHeight(ix - 1, iz) <= height) {
                data0 = 0.0f;
                data2 = 0.0f;
            }
            if ((double)this.getOceanHeight(ix, iz - 1) <= height) {
                data0 = 0.0f;
                data1 = 0.0f;
            }
            if ((double)this.getOceanHeight(ix - 1, iz - 1) <= height) {
                data0 = 0.0f;
            }
            if ((double)this.getOceanHeight(ix - 1, iz + 1) <= height) {
                data2 = 0.0f;
            }
            if ((double)this.getOceanHeight(ix + 1, iz - 1) <= height) {
                data1 = 0.0f;
            }
        } else {
            double depth = -distance;
            if ((double)this.getOceanDepth(ix, iz) <= depth) {
                data0 = 0.0f;
                data1 = 0.0f;
                data2 = 0.0f;
                data3 = 0.0f;
            }
            if ((double)this.getOceanDepth(ix + 1, iz) <= depth) {
                data1 = 0.0f;
                data3 = 0.0f;
            }
            if ((double)this.getOceanDepth(ix, iz + 1) <= depth) {
                data2 = 0.0f;
                data3 = 0.0f;
            }
            if ((double)this.getOceanDepth(ix + 1, iz + 1) <= depth) {
                data3 = 0.0f;
            }
            if ((double)this.getOceanDepth(ix - 1, iz) <= depth) {
                data0 = 0.0f;
                data2 = 0.0f;
            }
            if ((double)this.getOceanDepth(ix, iz - 1) <= depth) {
                data0 = 0.0f;
                data1 = 0.0f;
            }
            if ((double)this.getOceanDepth(ix - 1, iz - 1) <= depth) {
                data0 = 0.0f;
            }
            if ((double)this.getOceanDepth(ix - 1, iz + 1) <= depth) {
                data2 = 0.0f;
            }
            if ((double)this.getOceanDepth(ix + 1, iz - 1) <= depth) {
                data1 = 0.0f;
            }
        }
        float data4 = org.joml.Math.lerp((float)data0, (float)data1, (float)fractUVX);
        float data5 = org.joml.Math.lerp((float)data2, (float)data3, (float)fractUVX);
        return org.joml.Math.lerp((float)data4, (float)data5, (float)fractUVY);
    }

    public float textureData(int x, int y) {
        if (this.oceanSurface.textureData.length == 1) {
            return (float)(this.oceanSurface.textureData[0] & 0xFF) / 255.0f;
        }
        x = Math.clamp(x, 0, this.oceanSurface.width - 1);
        y = Math.clamp(y, 0, this.oceanSurface.height - 1);
        return (float)(this.oceanSurface.textureData[y * this.oceanSurface.width + x] & 0xFF) / 255.0f;
    }

    public Long2ObjectMap<ProxyOceanStorage> getOceanStorage() {
        return this.oceanStorage;
    }

    public int getOceanDepth(int x, int z) {
        ProxyOceanStorage storage = (ProxyOceanStorage)this.oceanStorage.get(OceanLayer.oceanLayerChunkFromWorldPos(x, z));
        if (storage == null) {
            return 0;
        }
        return storage.depths.getData(x & 0xF, z & 0xF) & 0xFF;
    }

    public int getOceanHeight(int x, int z) {
        ProxyOceanStorage storage = (ProxyOceanStorage)this.oceanStorage.get(OceanLayer.oceanLayerChunkFromWorldPos(x, z));
        if (storage == null) {
            return 0;
        }
        return storage.depths.getData(x & 0xF, z & 0xF) >> 8 & 0xFF;
    }

    public int getOcean(int x, int z) {
        ProxyOceanStorage storage = (ProxyOceanStorage)this.oceanStorage.get(OceanLayer.oceanLayerChunkFromWorldPos(x, z));
        if (storage == null) {
            return 0;
        }
        return storage.blocks.getData(x & 0xF, z & 0xF);
    }

    public short getLayerPosY() {
        return this.layerPosY;
    }

    public void setWaveOffset(int offsetX, int offsetZ) {
        this.waveOffsetX = offsetX;
        this.waveOffsetZ = offsetZ;
    }

    public void setOceanSurface(OceanSurface oceanSurface) {
        this.oceanSurface = oceanSurface;
    }

    public OceanSurface getOceanSurface() {
        return this.oceanSurface;
    }

    public void addRippleParticle(RippleParticle particle) {
        if (this.rippleParticles.size() == 0) {
            this.rippleOffset.set(particle.x, particle.y, particle.z);
        }
        particle.x -= this.rippleOffset.x;
        particle.y -= this.rippleOffset.y;
        particle.z -= this.rippleOffset.z;
        particle.xo -= this.rippleOffset.x;
        particle.yo -= this.rippleOffset.y;
        particle.zo -= this.rippleOffset.z;
        this.rippleParticles.addFirst(particle);
    }

    public Vector3d getRippleOffset() {
        return this.rippleOffset;
    }

    public LinkedList<RippleParticle> getRippleParticles() {
        return this.rippleParticles;
    }

    public VAO getRippleVAO() {
        return this.rippleVAO;
    }

    public void setRippleVAO(VAO rippleVAO) {
        this.rippleVAO = rippleVAO;
    }

    public void setRippleCount(int rippleCount) {
        this.rippleCount = rippleCount;
    }

    public int getRippleCount() {
        return this.rippleCount;
    }

    public boolean needsRippleUpdate() {
        return this.needsRippleUpdate;
    }

    public void setNeedsRippleUpdate(boolean needsRippleUpdate) {
        this.needsRippleUpdate = needsRippleUpdate;
    }

    public void destroy() {
        if (this.rippleVAO != null) {
            this.rippleVAO.destroy();
        }
    }
}

