/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.particle;

import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.particle.StackQueue;
import net.diebuddies.jbox2d.pooling.normal.MutableStack;

public class VoronoiDiagram {
    private Generator[] m_generatorBuffer;
    private int m_generatorCount;
    private int m_countX;
    private int m_countY;
    private Generator[] m_diagram;
    private final Vec2 lower = new Vec2();
    private final Vec2 upper = new Vec2();
    private MutableStack<VoronoiDiagramTask> taskPool = new MutableStack<VoronoiDiagramTask>(this, 50){

        @Override
        protected VoronoiDiagramTask newInstance() {
            return new VoronoiDiagramTask();
        }

        protected VoronoiDiagramTask[] newArray(int size) {
            return new VoronoiDiagramTask[size];
        }
    };
    private final StackQueue<VoronoiDiagramTask> queue = new StackQueue();

    public VoronoiDiagram(int generatorCapacity) {
        this.m_generatorBuffer = new Generator[generatorCapacity];
        for (int i = 0; i < generatorCapacity; ++i) {
            this.m_generatorBuffer[i] = new Generator();
        }
        this.m_generatorCount = 0;
        this.m_countX = 0;
        this.m_countY = 0;
        this.m_diagram = null;
    }

    public void getNodes(VoronoiDiagramCallback callback) {
        for (int y = 0; y < this.m_countY - 1; ++y) {
            for (int x = 0; x < this.m_countX - 1; ++x) {
                int i = x + y * this.m_countX;
                Generator a = this.m_diagram[i];
                Generator b = this.m_diagram[i + 1];
                Generator c = this.m_diagram[i + this.m_countX];
                Generator d = this.m_diagram[i + 1 + this.m_countX];
                if (b == c) continue;
                if (a != b && a != c) {
                    callback.callback(a.tag, b.tag, c.tag);
                }
                if (d == b || d == c) continue;
                callback.callback(b.tag, d.tag, c.tag);
            }
        }
    }

    public void addGenerator(Vec2 center, int tag) {
        Generator g = this.m_generatorBuffer[this.m_generatorCount++];
        g.center.x = center.x;
        g.center.y = center.y;
        g.tag = tag;
    }

    public void generate(float radius) {
        int y;
        Generator g;
        int k;
        assert (this.m_diagram == null);
        float inverseRadius = 1.0f / radius;
        this.lower.x = Float.MAX_VALUE;
        this.lower.y = Float.MAX_VALUE;
        this.upper.x = -3.4028235E38f;
        this.upper.y = -3.4028235E38f;
        for (k = 0; k < this.m_generatorCount; ++k) {
            g = this.m_generatorBuffer[k];
            Vec2.minToOut(this.lower, g.center, this.lower);
            Vec2.maxToOut(this.upper, g.center, this.upper);
        }
        this.m_countX = 1 + (int)(inverseRadius * (this.upper.x - this.lower.x));
        this.m_countY = 1 + (int)(inverseRadius * (this.upper.y - this.lower.y));
        this.m_diagram = new Generator[this.m_countX * this.m_countY];
        this.queue.reset((VoronoiDiagramTask[])new VoronoiDiagramTask[4 * this.m_countX * this.m_countX]);
        for (k = 0; k < this.m_generatorCount; ++k) {
            g = this.m_generatorBuffer[k];
            g.center.x = inverseRadius * (g.center.x - this.lower.x);
            g.center.y = inverseRadius * (g.center.y - this.lower.y);
            int x = MathUtils.max(0, MathUtils.min((int)g.center.x, this.m_countX - 1));
            int y2 = MathUtils.max(0, MathUtils.min((int)g.center.y, this.m_countY - 1));
            this.queue.push(this.taskPool.pop().set(x, y2, x + y2 * this.m_countX, g));
        }
        while (!this.queue.empty()) {
            VoronoiDiagramTask front = this.queue.pop();
            int x = front.m_x;
            y = front.m_y;
            int i = front.m_i;
            Generator g2 = front.m_generator;
            if (this.m_diagram[i] == null) {
                this.m_diagram[i] = g2;
                if (x > 0) {
                    this.queue.push(this.taskPool.pop().set(x - 1, y, i - 1, g2));
                }
                if (y > 0) {
                    this.queue.push(this.taskPool.pop().set(x, y - 1, i - this.m_countX, g2));
                }
                if (x < this.m_countX - 1) {
                    this.queue.push(this.taskPool.pop().set(x + 1, y, i + 1, g2));
                }
                if (y < this.m_countY - 1) {
                    this.queue.push(this.taskPool.pop().set(x, y + 1, i + this.m_countX, g2));
                }
            }
            this.taskPool.push(front);
        }
        int maxIteration = this.m_countX + this.m_countY;
        for (int iteration = 0; iteration < maxIteration; ++iteration) {
            Generator b;
            Generator a;
            for (y = 0; y < this.m_countY; ++y) {
                for (int x = 0; x < this.m_countX - 1; ++x) {
                    int i = x + y * this.m_countX;
                    a = this.m_diagram[i];
                    b = this.m_diagram[i + 1];
                    if (a == b) continue;
                    this.queue.push(this.taskPool.pop().set(x, y, i, b));
                    this.queue.push(this.taskPool.pop().set(x + 1, y, i + 1, a));
                }
            }
            for (y = 0; y < this.m_countY - 1; ++y) {
                for (int x = 0; x < this.m_countX; ++x) {
                    int i = x + y * this.m_countX;
                    a = this.m_diagram[i];
                    b = this.m_diagram[i + this.m_countX];
                    if (a == b) continue;
                    this.queue.push(this.taskPool.pop().set(x, y, i, b));
                    this.queue.push(this.taskPool.pop().set(x, y + 1, i + this.m_countX, a));
                }
            }
            boolean updated = false;
            while (!this.queue.empty()) {
                float by;
                float bx;
                float b2;
                float ay;
                float ax;
                float a2;
                VoronoiDiagramTask front = this.queue.pop();
                int x = front.m_x;
                int y3 = front.m_y;
                int i = front.m_i;
                Generator a3 = this.m_diagram[i];
                Generator k2 = front.m_generator;
                Generator b3 = k2;
                if (a3 != b3 && (a2 = (ax = a3.center.x - (float)x) * ax + (ay = a3.center.y - (float)y3) * ay) > (b2 = (bx = b3.center.x - (float)x) * bx + (by = b3.center.y - (float)y3) * by)) {
                    this.m_diagram[i] = b3;
                    if (x > 0) {
                        this.queue.push(this.taskPool.pop().set(x - 1, y3, i - 1, b3));
                    }
                    if (y3 > 0) {
                        this.queue.push(this.taskPool.pop().set(x, y3 - 1, i - this.m_countX, b3));
                    }
                    if (x < this.m_countX - 1) {
                        this.queue.push(this.taskPool.pop().set(x + 1, y3, i + 1, b3));
                    }
                    if (y3 < this.m_countY - 1) {
                        this.queue.push(this.taskPool.pop().set(x, y3 + 1, i + this.m_countX, b3));
                    }
                    updated = true;
                }
                this.taskPool.push(front);
            }
            if (!updated) break;
        }
    }

    public static class Generator {
        final Vec2 center = new Vec2();
        int tag;
    }

    public static interface VoronoiDiagramCallback {
        public void callback(int var1, int var2, int var3);
    }

    public static class VoronoiDiagramTask {
        int m_x;
        int m_y;
        int m_i;
        Generator m_generator;

        public VoronoiDiagramTask() {
        }

        public VoronoiDiagramTask(int x, int y, int i, Generator g) {
            this.m_x = x;
            this.m_y = y;
            this.m_i = i;
            this.m_generator = g;
        }

        public VoronoiDiagramTask set(int x, int y, int i, Generator g) {
            this.m_x = x;
            this.m_y = y;
            this.m_i = i;
            this.m_generator = g;
            return this;
        }
    }
}

