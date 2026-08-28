/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.joml.Vector3d
 *  org.joml.Vector3i
 *  org.joml.Vector3ic
 */
package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import net.diebuddies.physics.snow.ChunkEntity;
import net.diebuddies.physics.snow.math.AABB3D;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class SnowBatch {
    public static final int BUCKET_SHIFTS = 3;
    private Map<Vector3i, SnowChunkBucket> buckets = new Object2ObjectOpenHashMap();
    private Vector3i tmp = new Vector3i();

    public void add(ChunkEntity entity) {
        SnowChunkBucket bucket;
        Vector3i bucketPos = this.getBucketPos(entity);
        if (this.buckets.containsKey(bucketPos)) {
            bucket = this.buckets.get(bucketPos);
        } else {
            bucket = new SnowChunkBucket(this);
            this.buckets.put(new Vector3i((Vector3ic)bucketPos), bucket);
        }
        bucket.add(entity);
    }

    public void remove(ChunkEntity entity) {
        SnowChunkBucket bucket = this.buckets.get(this.getBucketPos(entity));
        bucket.remove(entity);
        if (bucket.isEmpty()) {
            this.buckets.remove(this.tmp);
        }
    }

    public void update() {
        for (SnowChunkBucket bucket : this.buckets.values()) {
            bucket.update();
        }
    }

    public Map<Vector3i, SnowChunkBucket> getBuckets() {
        return this.buckets;
    }

    private Vector3i getBucketPos(ChunkEntity entity) {
        Vector3i position = entity.batchPosition;
        return this.tmp.set(position.x, position.y, position.z);
    }

    public static int shiftPos(int pos) {
        return pos >> 3 << 3;
    }

    public class SnowChunkBucket {
        private List<ChunkEntity> entities = new ObjectArrayList();
        private AABB3D aabb = new AABB3D(new Vector3d(Double.MAX_VALUE), new Vector3d(-1.7976931348623157E308));
        private boolean aabbNeedsUpdate;

        public SnowChunkBucket(SnowBatch this$0) {
        }

        public void update() {
            if (this.aabbNeedsUpdate) {
                this.aabb.start.set(Double.MAX_VALUE);
                this.aabb.end.set(-1.7976931348623157E308);
                for (int i = 0; i < this.entities.size(); ++i) {
                    this.aabb.include(this.entities.get((int)i).aabb);
                }
                this.aabbNeedsUpdate = false;
            }
        }

        public void add(ChunkEntity entity) {
            this.entities.add(entity);
            this.aabb.include(entity.aabb);
        }

        public void remove(ChunkEntity entity) {
            this.entities.remove(entity);
            this.aabbNeedsUpdate = true;
        }

        public boolean isEmpty() {
            return this.entities.isEmpty();
        }

        public AABB3D getAABB() {
            return this.aabb;
        }

        public List<ChunkEntity> getEntities() {
            return this.entities;
        }
    }
}

