/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  org.joml.Vector3i
 */
package net.diebuddies.physics.liquid;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.diebuddies.dualcontouring.Chunk;
import net.diebuddies.dualcontouring.DualContouring3d;
import net.diebuddies.dualcontouring.SimplePoolVector3i;
import net.diebuddies.dualcontouring.Vertex;
import net.diebuddies.physics.liquid.Event;
import org.joml.Vector3i;

public class LiquidContouringThread
extends Thread {
    private volatile boolean running;
    private volatile boolean done;
    private ConcurrentLinkedQueue<Event> queue;
    public DualContouring3d dualContouringLiquid = new DualContouring3d();
    public Chunk chunk = new Chunk(40, 40, 40, 0, 0, 0);
    public Set<Vector3i> tmpCells = new ObjectOpenHashSet(500);
    public Set<Vector3i> affectedCells = new ObjectOpenHashSet(500);
    public SimplePoolVector3i vectorPool = new SimplePoolVector3i(1000);
    public List<Vertex> vertexBuffer = new ObjectArrayList(400);
    public IntArrayList indexBuffer = new IntArrayList(400);
    private Long2ObjectMap<Event> lastEvents = new Long2ObjectOpenHashMap();

    public LiquidContouringThread() {
        super("Liquid Contouring Thread");
        this.queue = new ConcurrentLinkedQueue();
    }

    @Override
    public void run() {
        super.run();
        this.running = true;
        while (this.running) {
            this.runOnce();
            try {
                Thread.sleep(1L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void runOnce() {
        Event event = null;
        this.done = this.queue.isEmpty();
        if (!this.done) {
            while ((event = this.queue.poll()) != null) {
                this.lastEvents.put(event.id, (Object)event);
            }
            if (this.lastEvents.size() > 0) {
                for (Event lastEvent : this.lastEvents.values()) {
                    lastEvent.run();
                }
                this.lastEvents.clear();
            }
        }
        this.done = true;
    }

    public void queueEvent(Event event) {
        this.done = false;
        event.thread = this;
        this.queue.add(event);
    }

    public boolean isDone() {
        return this.done && this.queue.isEmpty();
    }

    public void cancel() {
        this.running = false;
    }
}

