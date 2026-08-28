/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3i
 *  org.joml.Vector3ic
 */
package net.diebuddies.physics.wind;

import java.util.ArrayDeque;
import java.util.Queue;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class WindSimulation {
    public static final Vector3i[] NEIGHBOUR_VOXEL_OFFSETS = new Vector3i[]{new Vector3i(1, 0, 0), new Vector3i(-1, 0, 0), new Vector3i(0, 1, 0), new Vector3i(0, -1, 0)};
    public static final int SKY_LIGHT = 60;
    public static final int SOLID = 1;
    public int[][] light = new int[70][40];
    public int[][] map = new int[70][40];
    private Queue<Vector3i> lightAddQueue = new ArrayDeque<Vector3i>(0);
    private Queue<Vector3i> lightRemoveQueue = new ArrayDeque<Vector3i>(0);

    public void update(double delta) {
        this.updateLightValues();
    }

    public void updateLightValues() {
        int lightValue;
        Vector3i pos;
        Vector3i currentLight = null;
        Vector3i tmp = new Vector3i();
        while ((currentLight = this.lightRemoveQueue.poll()) != null) {
            pos = currentLight;
            lightValue = this.getLightData(pos.x, pos.y);
            this.setLightData(pos.x, pos.y, 0);
            for (int i = 0; i < NEIGHBOUR_VOXEL_OFFSETS.length; ++i) {
                Vector3i offset = tmp.set(pos.x + WindSimulation.NEIGHBOUR_VOXEL_OFFSETS[i].x, pos.y + WindSimulation.NEIGHBOUR_VOXEL_OFFSETS[i].y, pos.z + WindSimulation.NEIGHBOUR_VOXEL_OFFSETS[i].z);
                int lightValueOff = this.getLightData(offset.x, offset.y);
                if (lightValue == 60 && i == 2 && lightValueOff == lightValue) {
                    this.lightRemoveQueue.add(new Vector3i((Vector3ic)offset));
                    continue;
                }
                if (lightValueOff != 0 && lightValueOff < lightValue) {
                    this.lightRemoveQueue.add(new Vector3i((Vector3ic)offset));
                    continue;
                }
                if (lightValueOff < lightValue) continue;
                this.lightAddQueue.add(new Vector3i((Vector3ic)offset));
            }
        }
        while ((currentLight = this.lightAddQueue.poll()) != null) {
            pos = currentLight;
            lightValue = this.getLightData(pos.x, pos.y);
            int lightValueDec = lightValue - 1;
            for (int i = 0; i < NEIGHBOUR_VOXEL_OFFSETS.length; ++i) {
                Vector3i offset = tmp.set(pos.x + WindSimulation.NEIGHBOUR_VOXEL_OFFSETS[i].x, pos.y + WindSimulation.NEIGHBOUR_VOXEL_OFFSETS[i].y, pos.z + WindSimulation.NEIGHBOUR_VOXEL_OFFSETS[i].z);
                int lightValueOff = this.getLightData(offset.x, offset.y);
                boolean solid = this.isSolid(offset.x, offset.y);
                if (solid) continue;
                if (lightValue == 60 && i == 2 && lightValueOff < lightValue) {
                    this.setLightData(offset.x, offset.y, 60);
                    this.lightAddQueue.add(new Vector3i((Vector3ic)offset));
                    continue;
                }
                if (lightValueOff >= lightValueDec) continue;
                this.setLightData(offset.x, offset.y, lightValueDec);
                this.lightAddQueue.add(new Vector3i((Vector3ic)offset));
            }
        }
    }

    private void setLightData(int x, int y, int lightData) {
        if (x < 0 || y < 0 || x >= this.light.length || y >= this.light[0].length) {
            return;
        }
        this.light[x][y] = lightData;
    }

    public int getLightData(int x, int y) {
        if (x < 0 || y < 0 || x >= this.light.length || y >= this.light[0].length) {
            return 0;
        }
        int val = this.light[x][y];
        return val;
    }

    public void setSkyLight(int x, int y) {
        if (x < 0 || y < 0 || x >= this.light.length || y >= this.light[0].length) {
            return;
        }
        this.light[x][y] = 60;
        this.lightAddQueue.add(new Vector3i(x, y, 0));
    }

    public void removeSkyLight(int x, int y) {
        if (x < 0 || y < 0 || x >= this.light.length || y >= this.light[0].length) {
            return;
        }
        if (this.light[x][y] == 60) {
            this.lightRemoveQueue.add(new Vector3i(x, y, 0));
        }
    }

    public boolean isSolid(int x, int y) {
        if (x < 0 || y < 0 || x >= this.map.length || y >= this.map[0].length) {
            return true;
        }
        return this.map[x][y] == 1;
    }

    public void setSolid(int x, int y) {
        if (x < 0 || y < 0 || x >= this.map.length || y >= this.map[0].length) {
            return;
        }
        if (this.map[x][y] == 0) {
            this.lightRemoveQueue.add(new Vector3i(x, y, 0));
            this.map[x][y] = 1;
        }
    }

    public void removeSolid(int x, int y) {
        if (x < 0 || y < 0 || x >= this.map.length || y >= this.map[0].length) {
            return;
        }
        if (this.map[x][y] == 1) {
            this.map[x][y] = 0;
            int lightData = this.getLightData(x, y);
            for (int i = 0; i < NEIGHBOUR_VOXEL_OFFSETS.length; ++i) {
                Vector3i offset = NEIGHBOUR_VOXEL_OFFSETS[i];
                int offLightData = this.getLightData(x + offset.x, y + offset.y);
                if (offLightData <= lightData) continue;
                this.lightAddQueue.add(new Vector3i(x + offset.x, y + offset.y, 0));
            }
        }
    }
}

