/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.joml.Vector3i
 *  org.joml.Vector3ic
 */
package net.diebuddies.physics.smoke;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.diebuddies.math.Math;
import net.diebuddies.physics.smoke.SmokePanel;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class SmokeSimulation {
    public static final Vector3i[] OFFSETS = new Vector3i[]{new Vector3i(0, 0, 1), new Vector3i(0, 1, 0), new Vector3i(0, 1, 1), new Vector3i(1, 0, 0), new Vector3i(1, 0, 1), new Vector3i(1, 1, 0), new Vector3i(1, 1, 1), new Vector3i(-1, 0, 1), new Vector3i(-1, 1, 0), new Vector3i(-1, 1, 1), new Vector3i(0, -1, 1), new Vector3i(1, -1, 1), new Vector3i(1, 1, -1), new Vector3i(-1, -1, 1), new Vector3i(-1, 1, -1), new Vector3i(0, 1, -1), new Vector3i(1, -1, -1), new Vector3i(1, -1, 0), new Vector3i(1, 0, -1), new Vector3i(-1, -1, -1), new Vector3i(-1, -1, 0), new Vector3i(-1, 0, -1), new Vector3i(-1, 0, 0), new Vector3i(0, -1, -1), new Vector3i(0, -1, 0), new Vector3i(0, 0, -1)};
    private static double EFFECT_DISTANCE = 0.25;
    private static double EFFECT_DISTANCE_INV = 1.0 / EFFECT_DISTANCE;
    private static double EFFECT_DISTANCE_SQUARED = EFFECT_DISTANCE * EFFECT_DISTANCE;
    private static double EFFECT_STRENGTH = 0.25;
    private static double POWER = 0.5;
    private static final int CHUNK_SIZE = (int)java.lang.Math.round(java.lang.Math.ceil(EFFECT_DISTANCE));
    private final Map<Vector3i, List<SmokePanel.Particle>> chunks;
    private final Object2BooleanMap<Vector3i> masks;
    private final List<SmokePanel.Particle> allParticles;
    private final List<SmokePanel.Particle> changedParticles;
    private final Vector3i tmp = new Vector3i();

    public SmokeSimulation() {
        this.chunks = new Object2ObjectOpenHashMap();
        this.changedParticles = new ObjectArrayList();
        this.allParticles = new ObjectArrayList();
        this.masks = new Object2BooleanOpenHashMap();
        this.masks.defaultReturnValue(false);
    }

    public void update(double delta) {
        int i;
        Vector3i chunk;
        long time = System.nanoTime();
        Iterator<Map.Entry<Vector3i, List<SmokePanel.Particle>>> it = this.chunks.entrySet().iterator();
        this.changedParticles.clear();
        while (it.hasNext()) {
            Map.Entry<Vector3i, List<SmokePanel.Particle>> entry = it.next();
            chunk = entry.getKey();
            List<SmokePanel.Particle> particles = entry.getValue();
            for (i = 0; i < particles.size(); ++i) {
                SmokePanel.Particle particle = particles.get(i);
                particle.update(delta);
                int cx = Math.fastRound(particle.x) / CHUNK_SIZE;
                int cy = Math.fastRound(particle.y) / CHUNK_SIZE;
                if (cx == chunk.x && cy == chunk.y) continue;
                particles.remove(i--);
                this.changedParticles.add(particle);
            }
            if (particles.size() != 0) continue;
            it.remove();
        }
        for (int i2 = 0; i2 < this.changedParticles.size(); ++i2) {
            SmokePanel.Particle particle = this.changedParticles.get(i2);
            int cx = Math.fastRound(particle.x) / CHUNK_SIZE;
            int cy = Math.fastRound(particle.y) / CHUNK_SIZE;
            this.tmp.set(cx, cy, 0);
            ObjectArrayList particles = this.chunks.get(this.tmp);
            if (particles == null) {
                particles = new ObjectArrayList();
                this.chunks.put(new Vector3i((Vector3ic)this.tmp), (List<SmokePanel.Particle>)particles);
            }
            particles.add((SmokePanel.Particle)particle);
        }
        for (Map.Entry<Vector3i, List<SmokePanel.Particle>> entry : this.chunks.entrySet()) {
            chunk = entry.getKey();
            List<SmokePanel.Particle> particles = entry.getValue();
            this.repellParticles(particles, particles);
            for (i = 0; i < OFFSETS.length; ++i) {
                Vector3i offset = OFFSETS[i];
                this.tmp.set(chunk.x + offset.x, chunk.y + offset.y, chunk.z + offset.z);
                List<SmokePanel.Particle> otherParticles = this.chunks.get(this.tmp);
                if (otherParticles == null || this.masks.getBoolean((Object)this.tmp)) continue;
                this.repellParticles(particles, otherParticles);
            }
            this.masks.put((Object)chunk, true);
        }
        this.masks.clear();
        System.out.println("took (" + this.allParticles.size() + "): " + (double)(System.nanoTime() - time) / 1000000.0);
    }

    private void repellParticles(List<SmokePanel.Particle> particles, List<SmokePanel.Particle> otherParticles) {
        double EFFECT_STRENGTH = 4.25;
        double POWER = 1.0;
        double MAX_SPEED = 2.9;
        boolean same = particles == otherParticles;
        for (int i = 0; i < particles.size(); ++i) {
            SmokePanel.Particle particle1 = particles.get(i);
            for (int j = 0; j < otherParticles.size(); ++j) {
                double dy;
                double dx;
                double distanceSquared;
                SmokePanel.Particle particle2 = otherParticles.get(j);
                if (particle1 == particle2 || !((distanceSquared = (dx = particle1.x - particle2.x) * dx + (dy = particle1.y - particle2.y) * dy) < EFFECT_DISTANCE_SQUARED)) continue;
                double length = java.lang.Math.sqrt(distanceSquared);
                double effect = 1.0 - length * EFFECT_DISTANCE_INV;
                double invLength = 1.0 / length;
                boolean small = false;
                if (length <= 0.001) {
                    dy = 1.0;
                    effect = 1.0;
                    invLength = 1.0;
                    small = true;
                }
                double totalStrength = EFFECT_STRENGTH * effect;
                particle1.vx += (dx *= invLength) * totalStrength;
                particle1.vy += (dy *= invLength) * totalStrength;
                if (same) continue;
                particle2.vx -= dx * totalStrength;
                particle2.vy -= dy * totalStrength;
            }
        }
    }

    public void addParticle(SmokePanel.Particle particle) {
        int cx = Math.fastRound(particle.x) / CHUNK_SIZE;
        int cy = Math.fastRound(particle.y) / CHUNK_SIZE;
        this.tmp.set(cx, cy, 0);
        ObjectArrayList particles = this.chunks.get(this.tmp);
        if (particles == null) {
            particles = new ObjectArrayList();
            this.chunks.put(new Vector3i((Vector3ic)this.tmp), (List<SmokePanel.Particle>)particles);
        }
        particles.add((SmokePanel.Particle)particle);
        this.allParticles.add(particle);
    }

    public List<SmokePanel.Particle> getAllParticles() {
        return this.allParticles;
    }

    public void clear() {
        this.allParticles.clear();
        this.chunks.clear();
        this.changedParticles.clear();
    }
}

