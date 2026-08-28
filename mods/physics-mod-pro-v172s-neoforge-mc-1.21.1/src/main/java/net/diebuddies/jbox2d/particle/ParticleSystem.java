/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.particle;

import java.lang.reflect.Array;
import java.util.Arrays;
import net.diebuddies.jbox2d.callbacks.ParticleDestructionListener;
import net.diebuddies.jbox2d.callbacks.ParticleQueryCallback;
import net.diebuddies.jbox2d.callbacks.ParticleRaycastCallback;
import net.diebuddies.jbox2d.callbacks.QueryCallback;
import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.RayCastInput;
import net.diebuddies.jbox2d.collision.RayCastOutput;
import net.diebuddies.jbox2d.collision.shapes.Shape;
import net.diebuddies.jbox2d.common.BufferUtils;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.Fixture;
import net.diebuddies.jbox2d.dynamics.TimeStep;
import net.diebuddies.jbox2d.dynamics.World;
import net.diebuddies.jbox2d.particle.ParticleBodyContact;
import net.diebuddies.jbox2d.particle.ParticleColor;
import net.diebuddies.jbox2d.particle.ParticleContact;
import net.diebuddies.jbox2d.particle.ParticleDef;
import net.diebuddies.jbox2d.particle.ParticleGroup;
import net.diebuddies.jbox2d.particle.ParticleGroupDef;
import net.diebuddies.jbox2d.particle.VoronoiDiagram;

public class ParticleSystem {
    private static final int k_pairFlags = 8;
    private static final int k_triadFlags = 16;
    private static final int k_noPressureFlags = 64;
    static final int xTruncBits = 12;
    static final int yTruncBits = 12;
    static final int tagBits = 31;
    static final long yOffset = 2048L;
    static final int yShift = 19;
    static final int xShift = 7;
    static final long xScale = 128L;
    static final long xOffset = 262144L;
    static final int xMask = 4095;
    static final int yMask = 4095;
    int m_timestamp;
    int m_allParticleFlags;
    int m_allGroupFlags;
    float m_density;
    float m_inverseDensity;
    float m_gravityScale;
    float m_particleDiameter;
    float m_inverseDiameter;
    float m_squaredDiameter;
    int m_count;
    int m_internalAllocatedCapacity;
    int m_maxCount;
    ParticleBufferInt m_flagsBuffer;
    ParticleBuffer<Vec2> m_positionBuffer;
    ParticleBuffer<Vec2> m_velocityBuffer;
    float[] m_accumulationBuffer;
    Vec2[] m_accumulation2Buffer;
    float[] m_depthBuffer;
    public ParticleBuffer<ParticleColor> m_colorBuffer;
    ParticleGroup[] m_groupBuffer;
    ParticleBuffer<Object> m_userDataBuffer;
    int m_proxyCount;
    int m_proxyCapacity;
    Proxy[] m_proxyBuffer;
    public int m_contactCount;
    int m_contactCapacity;
    public ParticleContact[] m_contactBuffer;
    public int m_bodyContactCount;
    int m_bodyContactCapacity;
    public ParticleBodyContact[] m_bodyContactBuffer;
    int m_pairCount;
    int m_pairCapacity;
    Pair[] m_pairBuffer;
    int m_triadCount;
    int m_triadCapacity;
    Triad[] m_triadBuffer;
    int m_groupCount;
    ParticleGroup m_groupList;
    float m_pressureStrength;
    float m_dampingStrength;
    float m_elasticStrength;
    float m_springStrength;
    float m_viscousStrength;
    float m_surfaceTensionStrengthA;
    float m_surfaceTensionStrengthB;
    float m_powderStrength;
    float m_ejectionStrength;
    float m_colorMixingStrength;
    World m_world;
    private final AABB temp = new AABB();
    private final DestroyParticlesInShapeCallback dpcallback = new DestroyParticlesInShapeCallback();
    private final AABB temp2 = new AABB();
    private final Vec2 tempVec = new Vec2();
    private final Transform tempTransform = new Transform();
    private final Transform tempTransform2 = new Transform();
    private CreateParticleGroupCallback createParticleGroupCallback = new CreateParticleGroupCallback();
    private final ParticleDef tempParticleDef = new ParticleDef();
    private final UpdateBodyContactsCallback ubccallback = new UpdateBodyContactsCallback();
    private SolveCollisionCallback sccallback = new SolveCollisionCallback();
    private final Vec2 tempVec2 = new Vec2();
    private final Rot tempRot = new Rot();
    private final Transform tempXf = new Transform();
    private final Transform tempXf2 = new Transform();
    private final NewIndices newIndices = new NewIndices();

    static long computeTag(float x, float y) {
        return ((long)(y + 2048.0f) << 19) + ((long)(128.0f * x) + 262144L);
    }

    static long computeRelativeTag(long tag, int x, int y) {
        return tag + (long)(y << 19) + (long)(x << 7);
    }

    static int limitCapacity(int capacity, int maxCount) {
        return maxCount != 0 && capacity > maxCount ? maxCount : capacity;
    }

    public ParticleSystem(World world) {
        this.m_world = world;
        this.m_timestamp = 0;
        this.m_allParticleFlags = 0;
        this.m_allGroupFlags = 0;
        this.m_density = 1.0f;
        this.m_inverseDensity = 1.0f;
        this.m_gravityScale = 1.0f;
        this.m_particleDiameter = 1.0f;
        this.m_inverseDiameter = 1.0f;
        this.m_squaredDiameter = 1.0f;
        this.m_count = 0;
        this.m_internalAllocatedCapacity = 0;
        this.m_maxCount = 0;
        this.m_proxyCount = 0;
        this.m_proxyCapacity = 0;
        this.m_contactCount = 0;
        this.m_contactCapacity = 0;
        this.m_bodyContactCount = 0;
        this.m_bodyContactCapacity = 0;
        this.m_pairCount = 0;
        this.m_pairCapacity = 0;
        this.m_triadCount = 0;
        this.m_triadCapacity = 0;
        this.m_groupCount = 0;
        this.m_pressureStrength = 0.05f;
        this.m_dampingStrength = 1.0f;
        this.m_elasticStrength = 0.25f;
        this.m_springStrength = 0.25f;
        this.m_viscousStrength = 0.25f;
        this.m_surfaceTensionStrengthA = 0.1f;
        this.m_surfaceTensionStrengthB = 0.2f;
        this.m_powderStrength = 0.5f;
        this.m_ejectionStrength = 0.5f;
        this.m_colorMixingStrength = 0.5f;
        this.m_flagsBuffer = new ParticleBufferInt();
        this.m_positionBuffer = new ParticleBuffer<Vec2>(Vec2.class);
        this.m_velocityBuffer = new ParticleBuffer<Vec2>(Vec2.class);
        this.m_colorBuffer = new ParticleBuffer<ParticleColor>(ParticleColor.class);
        this.m_userDataBuffer = new ParticleBuffer<Object>(Object.class);
    }

    public int createParticle(ParticleDef def) {
        if (this.m_count >= this.m_internalAllocatedCapacity) {
            int capacity = this.m_count != 0 ? 2 * this.m_count : 256;
            capacity = ParticleSystem.limitCapacity(capacity, this.m_maxCount);
            capacity = ParticleSystem.limitCapacity(capacity, this.m_flagsBuffer.userSuppliedCapacity);
            capacity = ParticleSystem.limitCapacity(capacity, this.m_positionBuffer.userSuppliedCapacity);
            capacity = ParticleSystem.limitCapacity(capacity, this.m_velocityBuffer.userSuppliedCapacity);
            capacity = ParticleSystem.limitCapacity(capacity, this.m_colorBuffer.userSuppliedCapacity);
            if (this.m_internalAllocatedCapacity < (capacity = ParticleSystem.limitCapacity(capacity, this.m_userDataBuffer.userSuppliedCapacity))) {
                this.m_flagsBuffer.data = ParticleSystem.reallocateBuffer(this.m_flagsBuffer, this.m_internalAllocatedCapacity, capacity, false);
                this.m_positionBuffer.data = ParticleSystem.reallocateBuffer(this.m_positionBuffer, this.m_internalAllocatedCapacity, capacity, false);
                this.m_velocityBuffer.data = ParticleSystem.reallocateBuffer(this.m_velocityBuffer, this.m_internalAllocatedCapacity, capacity, false);
                this.m_accumulationBuffer = BufferUtils.reallocateBuffer(this.m_accumulationBuffer, 0, this.m_internalAllocatedCapacity, capacity, false);
                this.m_accumulation2Buffer = BufferUtils.reallocateBuffer(Vec2.class, this.m_accumulation2Buffer, 0, this.m_internalAllocatedCapacity, capacity, true);
                this.m_depthBuffer = BufferUtils.reallocateBuffer(this.m_depthBuffer, 0, this.m_internalAllocatedCapacity, capacity, true);
                this.m_colorBuffer.data = ParticleSystem.reallocateBuffer(this.m_colorBuffer, this.m_internalAllocatedCapacity, capacity, true);
                this.m_groupBuffer = BufferUtils.reallocateBuffer(ParticleGroup.class, this.m_groupBuffer, 0, this.m_internalAllocatedCapacity, capacity, false);
                this.m_userDataBuffer.data = ParticleSystem.reallocateBuffer(this.m_userDataBuffer, this.m_internalAllocatedCapacity, capacity, true);
                this.m_internalAllocatedCapacity = capacity;
            }
        }
        if (this.m_count >= this.m_internalAllocatedCapacity) {
            return -1;
        }
        int index = this.m_count++;
        this.m_flagsBuffer.data[index] = def.flags;
        ((Vec2[])this.m_positionBuffer.data)[index].set(def.position);
        ((Vec2[])this.m_velocityBuffer.data)[index].set(def.velocity);
        this.m_groupBuffer[index] = null;
        if (this.m_depthBuffer != null) {
            this.m_depthBuffer[index] = 0.0f;
        }
        if (this.m_colorBuffer.data != null || def.color != null) {
            this.m_colorBuffer.data = this.requestParticleBuffer(this.m_colorBuffer.dataClass, (ParticleColor[])this.m_colorBuffer.data);
            ((ParticleColor[])this.m_colorBuffer.data)[index].set(def.color);
        }
        if (this.m_userDataBuffer.data != null || def.userData != null) {
            this.m_userDataBuffer.data = this.requestParticleBuffer(this.m_userDataBuffer.dataClass, this.m_userDataBuffer.data);
            this.m_userDataBuffer.data[index] = def.userData;
        }
        if (this.m_proxyCount >= this.m_proxyCapacity) {
            int oldCapacity = this.m_proxyCapacity;
            int newCapacity = this.m_proxyCount != 0 ? 2 * this.m_proxyCount : 256;
            this.m_proxyBuffer = BufferUtils.reallocateBuffer(Proxy.class, this.m_proxyBuffer, oldCapacity, newCapacity);
            this.m_proxyCapacity = newCapacity;
        }
        ++this.m_proxyCount;
        this.m_proxyBuffer[this.m_proxyCount].index = index;
        return index;
    }

    public void destroyParticle(int index, boolean callDestructionListener) {
        int flags = 2;
        if (callDestructionListener) {
            flags |= 0x200;
        }
        int n = index;
        this.m_flagsBuffer.data[n] = this.m_flagsBuffer.data[n] | flags;
    }

    public int destroyParticlesInShape(Shape shape, Transform xf, boolean callDestructionListener) {
        this.dpcallback.init(this, shape, xf, callDestructionListener);
        shape.computeAABB(this.temp, xf, 0);
        this.m_world.queryAABB(this.dpcallback, this.temp);
        return this.dpcallback.destroyed;
    }

    public void destroyParticlesInGroup(ParticleGroup group, boolean callDestructionListener) {
        for (int i = group.m_firstIndex; i < group.m_lastIndex; ++i) {
            this.destroyParticle(i, callDestructionListener);
        }
    }

    public ParticleGroup createParticleGroup(ParticleGroupDef groupDef) {
        float stride = this.getParticleStride();
        Transform identity = this.tempTransform;
        identity.setIdentity();
        Transform transform = this.tempTransform2;
        transform.setIdentity();
        int firstIndex = this.m_count;
        if (groupDef.shape != null) {
            ParticleDef particleDef = this.tempParticleDef;
            particleDef.flags = groupDef.flags;
            particleDef.color = groupDef.color;
            particleDef.userData = groupDef.userData;
            Shape shape = groupDef.shape;
            transform.set(groupDef.position, groupDef.angle);
            AABB aabb = this.temp;
            int childCount = shape.getChildCount();
            for (int childIndex = 0; childIndex < childCount; ++childIndex) {
                if (childIndex == 0) {
                    shape.computeAABB(aabb, identity, childIndex);
                    continue;
                }
                AABB childAABB = this.temp2;
                shape.computeAABB(childAABB, identity, childIndex);
                aabb.combine(childAABB);
            }
            float upperBoundY = aabb.upperBound.y;
            float upperBoundX = aabb.upperBound.x;
            for (float y = (float)MathUtils.floor(aabb.lowerBound.y / stride) * stride; y < upperBoundY; y += stride) {
                for (float x = (float)MathUtils.floor(aabb.lowerBound.x / stride) * stride; x < upperBoundX; x += stride) {
                    Vec2 p = this.tempVec;
                    p.x = x;
                    p.y = y;
                    if (!shape.testPoint(identity, p)) continue;
                    Transform.mulToOut(transform, p, p);
                    particleDef.position.x = p.x;
                    particleDef.position.y = p.y;
                    p.subLocal(groupDef.position);
                    Vec2.crossToOutUnsafe(groupDef.angularVelocity, p, particleDef.velocity);
                    particleDef.velocity.addLocal(groupDef.linearVelocity);
                    this.createParticle(particleDef);
                }
            }
        }
        int lastIndex = this.m_count;
        ParticleGroup group = new ParticleGroup();
        group.m_system = this;
        group.m_firstIndex = firstIndex;
        group.m_lastIndex = lastIndex;
        group.m_groupFlags = groupDef.groupFlags;
        group.m_strength = groupDef.strength;
        group.m_userData = groupDef.userData;
        group.m_transform.set(transform);
        group.m_destroyAutomatically = groupDef.destroyAutomatically;
        group.m_prev = null;
        group.m_next = this.m_groupList;
        if (this.m_groupList != null) {
            this.m_groupList.m_prev = group;
        }
        this.m_groupList = group;
        ++this.m_groupCount;
        for (int i = firstIndex; i < lastIndex; ++i) {
            this.m_groupBuffer[i] = group;
        }
        this.updateContacts(true);
        if ((groupDef.flags & 8) != 0) {
            for (int k = 0; k < this.m_contactCount; ++k) {
                ParticleContact contact = this.m_contactBuffer[k];
                int a = contact.indexA;
                int b = contact.indexB;
                if (a > b) {
                    int temp = a;
                    a = b;
                    b = temp;
                }
                if (firstIndex > a || b >= lastIndex) continue;
                if (this.m_pairCount >= this.m_pairCapacity) {
                    int oldCapacity = this.m_pairCapacity;
                    int newCapacity = this.m_pairCount != 0 ? 2 * this.m_pairCount : 256;
                    this.m_pairBuffer = BufferUtils.reallocateBuffer(Pair.class, this.m_pairBuffer, oldCapacity, newCapacity);
                    this.m_pairCapacity = newCapacity;
                }
                Pair pair = this.m_pairBuffer[this.m_pairCount];
                pair.indexA = a;
                pair.indexB = b;
                pair.flags = contact.flags;
                pair.strength = groupDef.strength;
                pair.distance = MathUtils.distance(((Vec2[])this.m_positionBuffer.data)[a], ((Vec2[])this.m_positionBuffer.data)[b]);
                ++this.m_pairCount;
            }
        }
        if ((groupDef.flags & 0x10) != 0) {
            VoronoiDiagram diagram = new VoronoiDiagram(lastIndex - firstIndex);
            for (int i = firstIndex; i < lastIndex; ++i) {
                diagram.addGenerator(((Vec2[])this.m_positionBuffer.data)[i], i);
            }
            diagram.generate(stride / 2.0f);
            this.createParticleGroupCallback.system = this;
            this.createParticleGroupCallback.def = groupDef;
            this.createParticleGroupCallback.firstIndex = firstIndex;
            diagram.getNodes(this.createParticleGroupCallback);
        }
        if ((groupDef.groupFlags & 1) != 0) {
            this.computeDepthForGroup(group);
        }
        return group;
    }

    public void joinParticleGroups(ParticleGroup groupA, ParticleGroup groupB) {
        int groupFlags;
        assert (groupA != groupB);
        this.RotateBuffer(groupB.m_firstIndex, groupB.m_lastIndex, this.m_count);
        assert (groupB.m_lastIndex == this.m_count);
        this.RotateBuffer(groupA.m_firstIndex, groupA.m_lastIndex, groupB.m_firstIndex);
        assert (groupA.m_lastIndex == groupB.m_firstIndex);
        int particleFlags = 0;
        for (int i = groupA.m_firstIndex; i < groupB.m_lastIndex; ++i) {
            particleFlags |= this.m_flagsBuffer.data[i];
        }
        this.updateContacts(true);
        if ((particleFlags & 8) != 0) {
            for (int k = 0; k < this.m_contactCount; ++k) {
                ParticleContact contact = this.m_contactBuffer[k];
                int a = contact.indexA;
                int b = contact.indexB;
                if (a > b) {
                    int temp = a;
                    a = b;
                    b = temp;
                }
                if (groupA.m_firstIndex > a || a >= groupA.m_lastIndex || groupB.m_firstIndex > b || b >= groupB.m_lastIndex) continue;
                if (this.m_pairCount >= this.m_pairCapacity) {
                    int oldCapacity = this.m_pairCapacity;
                    int newCapacity = this.m_pairCount != 0 ? 2 * this.m_pairCount : 256;
                    this.m_pairBuffer = BufferUtils.reallocateBuffer(Pair.class, this.m_pairBuffer, oldCapacity, newCapacity);
                    this.m_pairCapacity = newCapacity;
                }
                Pair pair = this.m_pairBuffer[this.m_pairCount];
                pair.indexA = a;
                pair.indexB = b;
                pair.flags = contact.flags;
                pair.strength = MathUtils.min(groupA.m_strength, groupB.m_strength);
                pair.distance = MathUtils.distance(((Vec2[])this.m_positionBuffer.data)[a], ((Vec2[])this.m_positionBuffer.data)[b]);
                ++this.m_pairCount;
            }
        }
        if ((particleFlags & 0x10) != 0) {
            VoronoiDiagram diagram = new VoronoiDiagram(groupB.m_lastIndex - groupA.m_firstIndex);
            for (int i = groupA.m_firstIndex; i < groupB.m_lastIndex; ++i) {
                if ((this.m_flagsBuffer.data[i] & 2) != 0) continue;
                diagram.addGenerator(((Vec2[])this.m_positionBuffer.data)[i], i);
            }
            diagram.generate(this.getParticleStride() / 2.0f);
            JoinParticleGroupsCallback callback = new JoinParticleGroupsCallback();
            callback.system = this;
            callback.groupA = groupA;
            callback.groupB = groupB;
            diagram.getNodes(callback);
        }
        for (int i = groupB.m_firstIndex; i < groupB.m_lastIndex; ++i) {
            this.m_groupBuffer[i] = groupA;
        }
        groupA.m_groupFlags = groupFlags = groupA.m_groupFlags | groupB.m_groupFlags;
        groupA.m_lastIndex = groupB.m_lastIndex;
        groupB.m_firstIndex = groupB.m_lastIndex;
        this.destroyParticleGroup(groupB);
        if ((groupFlags & 1) != 0) {
            this.computeDepthForGroup(groupA);
        }
    }

    void destroyParticleGroup(ParticleGroup group) {
        assert (this.m_groupCount > 0);
        assert (group != null);
        if (this.m_world.getParticleDestructionListener() != null) {
            this.m_world.getParticleDestructionListener().sayGoodbye(group);
        }
        for (int i = group.m_firstIndex; i < group.m_lastIndex; ++i) {
            this.m_groupBuffer[i] = null;
        }
        if (group.m_prev != null) {
            group.m_prev.m_next = group.m_next;
        }
        if (group.m_next != null) {
            group.m_next.m_prev = group.m_prev;
        }
        if (group == this.m_groupList) {
            this.m_groupList = group.m_next;
        }
        --this.m_groupCount;
    }

    public void computeDepthForGroup(ParticleGroup group) {
        int i;
        for (i = group.m_firstIndex; i < group.m_lastIndex; ++i) {
            this.m_accumulationBuffer[i] = 0.0f;
        }
        for (int k = 0; k < this.m_contactCount; ++k) {
            ParticleContact contact = this.m_contactBuffer[k];
            int a = contact.indexA;
            int b = contact.indexB;
            if (a < group.m_firstIndex || a >= group.m_lastIndex || b < group.m_firstIndex || b >= group.m_lastIndex) continue;
            float w = contact.weight;
            int n = a;
            this.m_accumulationBuffer[n] = this.m_accumulationBuffer[n] + w;
            int n2 = b;
            this.m_accumulationBuffer[n2] = this.m_accumulationBuffer[n2] + w;
        }
        this.m_depthBuffer = this.requestParticleBuffer(this.m_depthBuffer);
        for (i = group.m_firstIndex; i < group.m_lastIndex; ++i) {
            float w = this.m_accumulationBuffer[i];
            this.m_depthBuffer[i] = w < 0.8f ? 0.0f : Float.MAX_VALUE;
        }
        int interationCount = group.getParticleCount();
        for (int t = 0; t < interationCount; ++t) {
            boolean updated = false;
            for (int k = 0; k < this.m_contactCount; ++k) {
                ParticleContact contact = this.m_contactBuffer[k];
                int a = contact.indexA;
                int b = contact.indexB;
                if (a < group.m_firstIndex || a >= group.m_lastIndex || b < group.m_firstIndex || b >= group.m_lastIndex) continue;
                float r = 1.0f - contact.weight;
                float ap0 = this.m_depthBuffer[a];
                float bp0 = this.m_depthBuffer[b];
                float ap1 = bp0 + r;
                float bp1 = ap0 + r;
                if (ap0 > ap1) {
                    this.m_depthBuffer[a] = ap1;
                    updated = true;
                }
                if (!(bp0 > bp1)) continue;
                this.m_depthBuffer[b] = bp1;
                updated = true;
            }
            if (!updated) break;
        }
        for (int i2 = group.m_firstIndex; i2 < group.m_lastIndex; ++i2) {
            float p = this.m_depthBuffer[i2];
            if (p < Float.MAX_VALUE) {
                int n = i2;
                this.m_depthBuffer[n] = this.m_depthBuffer[n] * this.m_particleDiameter;
                continue;
            }
            this.m_depthBuffer[i2] = 0.0f;
        }
    }

    public void addContact(int a, int b) {
        assert (a != b);
        Vec2 pa = ((Vec2[])this.m_positionBuffer.data)[a];
        Vec2 pb = ((Vec2[])this.m_positionBuffer.data)[b];
        float dx = pb.x - pa.x;
        float dy = pb.y - pa.y;
        float d2 = dx * dx + dy * dy;
        if (d2 < this.m_squaredDiameter) {
            if (this.m_contactCount >= this.m_contactCapacity) {
                int oldCapacity = this.m_contactCapacity;
                int newCapacity = this.m_contactCount != 0 ? 2 * this.m_contactCount : 256;
                this.m_contactBuffer = BufferUtils.reallocateBuffer(ParticleContact.class, this.m_contactBuffer, oldCapacity, newCapacity);
                this.m_contactCapacity = newCapacity;
            }
            float invD = d2 != 0.0f ? MathUtils.sqrt(1.0f / d2) : Float.MAX_VALUE;
            ParticleContact contact = this.m_contactBuffer[this.m_contactCount];
            contact.indexA = a;
            contact.indexB = b;
            contact.flags = this.m_flagsBuffer.data[a] | this.m_flagsBuffer.data[b];
            contact.weight = 1.0f - d2 * invD * this.m_inverseDiameter;
            contact.normal.x = invD * dx;
            contact.normal.y = invD * dy;
            ++this.m_contactCount;
        }
    }

    public void updateContacts(boolean exceptZombie) {
        for (int p = 0; p < this.m_proxyCount; ++p) {
            Proxy proxy = this.m_proxyBuffer[p];
            int i = proxy.index;
            Vec2 pos = ((Vec2[])this.m_positionBuffer.data)[i];
            proxy.tag = ParticleSystem.computeTag(this.m_inverseDiameter * pos.x, this.m_inverseDiameter * pos.y);
        }
        Arrays.sort(this.m_proxyBuffer, 0, this.m_proxyCount);
        this.m_contactCount = 0;
        int c_index = 0;
        block1: for (int i = 0; i < this.m_proxyCount; ++i) {
            Proxy a = this.m_proxyBuffer[i];
            long rightTag = ParticleSystem.computeRelativeTag(a.tag, 1, 0);
            for (int j = i + 1; j < this.m_proxyCount; ++j) {
                Proxy b = this.m_proxyBuffer[j];
                if (rightTag < b.tag) break;
                this.addContact(a.index, b.index);
            }
            long bottomLeftTag = ParticleSystem.computeRelativeTag(a.tag, -1, 1);
            while (c_index < this.m_proxyCount) {
                Proxy c = this.m_proxyBuffer[c_index];
                if (bottomLeftTag <= c.tag) break;
                ++c_index;
            }
            long bottomRightTag = ParticleSystem.computeRelativeTag(a.tag, 1, 1);
            for (int b_index = c_index; b_index < this.m_proxyCount; ++b_index) {
                Proxy b = this.m_proxyBuffer[b_index];
                if (bottomRightTag < b.tag) continue block1;
                this.addContact(a.index, b.index);
            }
        }
        if (exceptZombie) {
            int j = this.m_contactCount;
            for (int i = 0; i < j; ++i) {
                if ((this.m_contactBuffer[i].flags & 2) == 0) continue;
                ParticleContact temp = this.m_contactBuffer[--j];
                this.m_contactBuffer[j] = this.m_contactBuffer[i];
                this.m_contactBuffer[i] = temp;
                --i;
            }
            this.m_contactCount = j;
        }
    }

    public void updateBodyContacts() {
        AABB aabb = this.temp;
        aabb.lowerBound.x = Float.MAX_VALUE;
        aabb.lowerBound.y = Float.MAX_VALUE;
        aabb.upperBound.x = -3.4028235E38f;
        aabb.upperBound.y = -3.4028235E38f;
        for (int i = 0; i < this.m_count; ++i) {
            Vec2 p = ((Vec2[])this.m_positionBuffer.data)[i];
            Vec2.minToOut(aabb.lowerBound, p, aabb.lowerBound);
            Vec2.maxToOut(aabb.upperBound, p, aabb.upperBound);
        }
        aabb.lowerBound.x -= this.m_particleDiameter;
        aabb.lowerBound.y -= this.m_particleDiameter;
        aabb.upperBound.x += this.m_particleDiameter;
        aabb.upperBound.y += this.m_particleDiameter;
        this.m_bodyContactCount = 0;
        this.ubccallback.system = this;
        this.m_world.queryAABB(this.ubccallback, aabb);
    }

    public void solveCollision(TimeStep step) {
        AABB aabb = this.temp;
        Vec2 lowerBound = aabb.lowerBound;
        Vec2 upperBound = aabb.upperBound;
        lowerBound.x = Float.MAX_VALUE;
        lowerBound.y = Float.MAX_VALUE;
        upperBound.x = -3.4028235E38f;
        upperBound.y = -3.4028235E38f;
        for (int i = 0; i < this.m_count; ++i) {
            Vec2 v = ((Vec2[])this.m_velocityBuffer.data)[i];
            Vec2 p1 = ((Vec2[])this.m_positionBuffer.data)[i];
            float p1x = p1.x;
            float p1y = p1.y;
            float p2x = p1x + step.dt * v.x;
            float p2y = p1y + step.dt * v.y;
            float bx = p1x < p2x ? p1x : p2x;
            float by = p1y < p2y ? p1y : p2y;
            lowerBound.x = lowerBound.x < bx ? lowerBound.x : bx;
            lowerBound.y = lowerBound.y < by ? lowerBound.y : by;
            float b1x = p1x > p2x ? p1x : p2x;
            float b1y = p1y > p2y ? p1y : p2y;
            upperBound.x = upperBound.x > b1x ? upperBound.x : b1x;
            upperBound.y = upperBound.y > b1y ? upperBound.y : b1y;
        }
        this.sccallback.step = step;
        this.sccallback.system = this;
        this.m_world.queryAABB(this.sccallback, aabb);
    }

    public void solve(TimeStep step) {
        int i;
        ++this.m_timestamp;
        if (this.m_count == 0) {
            return;
        }
        this.m_allParticleFlags = 0;
        for (int i2 = 0; i2 < this.m_count; ++i2) {
            this.m_allParticleFlags |= this.m_flagsBuffer.data[i2];
        }
        if ((this.m_allParticleFlags & 2) != 0) {
            this.solveZombie();
        }
        if (this.m_count == 0) {
            return;
        }
        this.m_allGroupFlags = 0;
        for (ParticleGroup group = this.m_groupList; group != null; group = group.getNext()) {
            this.m_allGroupFlags |= group.m_groupFlags;
        }
        float gravityx = step.dt * this.m_gravityScale * this.m_world.getGravity().x;
        float gravityy = step.dt * this.m_gravityScale * this.m_world.getGravity().y;
        float criticalVelocytySquared = this.getCriticalVelocitySquared(step);
        for (i = 0; i < this.m_count; ++i) {
            Vec2 v = ((Vec2[])this.m_velocityBuffer.data)[i];
            v.x += gravityx;
            v.y += gravityy;
            float v2 = v.x * v.x + v.y * v.y;
            if (!(v2 > criticalVelocytySquared)) continue;
            float a = v2 == 0.0f ? Float.MAX_VALUE : MathUtils.sqrt(criticalVelocytySquared / v2);
            v.x *= a;
            v.y *= a;
        }
        this.solveCollision(step);
        if ((this.m_allGroupFlags & 2) != 0) {
            this.solveRigid(step);
        }
        if ((this.m_allParticleFlags & 4) != 0) {
            this.solveWall(step);
        }
        for (i = 0; i < this.m_count; ++i) {
            Vec2 pos = ((Vec2[])this.m_positionBuffer.data)[i];
            Vec2 vel = ((Vec2[])this.m_velocityBuffer.data)[i];
            pos.x += step.dt * vel.x;
            pos.y += step.dt * vel.y;
        }
        this.updateBodyContacts();
        this.updateContacts(false);
        if ((this.m_allParticleFlags & 0x20) != 0) {
            this.solveViscous(step);
        }
        if ((this.m_allParticleFlags & 0x40) != 0) {
            this.solvePowder(step);
        }
        if ((this.m_allParticleFlags & 0x80) != 0) {
            this.solveTensile(step);
        }
        if ((this.m_allParticleFlags & 0x10) != 0) {
            this.solveElastic(step);
        }
        if ((this.m_allParticleFlags & 8) != 0) {
            this.solveSpring(step);
        }
        if ((this.m_allGroupFlags & 1) != 0) {
            this.solveSolid(step);
        }
        if ((this.m_allParticleFlags & 0x100) != 0) {
            this.solveColorMixing(step);
        }
        this.solvePressure(step);
        this.solveDamping(step);
    }

    void solvePressure(TimeStep step) {
        float w;
        int k;
        int a;
        Object contact;
        int k2;
        int i;
        for (i = 0; i < this.m_count; ++i) {
            this.m_accumulationBuffer[i] = 0.0f;
        }
        for (k2 = 0; k2 < this.m_bodyContactCount; ++k2) {
            contact = this.m_bodyContactBuffer[k2];
            a = ((ParticleBodyContact)contact).index;
            float w2 = ((ParticleBodyContact)contact).weight;
            int n = a;
            this.m_accumulationBuffer[n] = this.m_accumulationBuffer[n] + w2;
        }
        for (k2 = 0; k2 < this.m_contactCount; ++k2) {
            contact = this.m_contactBuffer[k2];
            a = ((ParticleContact)contact).indexA;
            int b = ((ParticleContact)contact).indexB;
            float w3 = ((ParticleContact)contact).weight;
            int n = a;
            this.m_accumulationBuffer[n] = this.m_accumulationBuffer[n] + w3;
            int n2 = b;
            this.m_accumulationBuffer[n2] = this.m_accumulationBuffer[n2] + w3;
        }
        if ((this.m_allParticleFlags & 0x40) != 0) {
            for (i = 0; i < this.m_count; ++i) {
                if ((this.m_flagsBuffer.data[i] & 0x40) == 0) continue;
                this.m_accumulationBuffer[i] = 0.0f;
            }
        }
        float pressurePerWeight = this.m_pressureStrength * this.getCriticalPressure(step);
        for (int i2 = 0; i2 < this.m_count; ++i2) {
            float h;
            float w4 = this.m_accumulationBuffer[i2];
            this.m_accumulationBuffer[i2] = h = pressurePerWeight * MathUtils.max(0.0f, MathUtils.min(w4, 5.0f) - 1.0f);
        }
        float velocityPerPressure = step.dt / (this.m_density * this.m_particleDiameter);
        for (k = 0; k < this.m_bodyContactCount; ++k) {
            ParticleBodyContact contact2 = this.m_bodyContactBuffer[k];
            int a2 = contact2.index;
            Body b = contact2.body;
            w = contact2.weight;
            float m = contact2.mass;
            Vec2 n = contact2.normal;
            Vec2 p = ((Vec2[])this.m_positionBuffer.data)[a2];
            float h = this.m_accumulationBuffer[a2] + pressurePerWeight * w;
            Vec2 f = this.tempVec;
            float coef = velocityPerPressure * w * m * h;
            f.x = coef * n.x;
            f.y = coef * n.y;
            Vec2 velData = ((Vec2[])this.m_velocityBuffer.data)[a2];
            float particleInvMass = this.getParticleInvMass();
            velData.x -= particleInvMass * f.x;
            velData.y -= particleInvMass * f.y;
            b.applyLinearImpulse(f, p, true);
        }
        for (k = 0; k < this.m_contactCount; ++k) {
            ParticleContact contact3 = this.m_contactBuffer[k];
            int a3 = contact3.indexA;
            int b = contact3.indexB;
            w = contact3.weight;
            Vec2 n = contact3.normal;
            float h = this.m_accumulationBuffer[a3] + this.m_accumulationBuffer[b];
            float fx = velocityPerPressure * w * h * n.x;
            float fy = velocityPerPressure * w * h * n.y;
            Vec2 velDataA = ((Vec2[])this.m_velocityBuffer.data)[a3];
            Vec2 velDataB = ((Vec2[])this.m_velocityBuffer.data)[b];
            velDataA.x -= fx;
            velDataA.y -= fy;
            velDataB.x += fx;
            velDataB.y += fy;
        }
    }

    void solveDamping(TimeStep step) {
        float w;
        int a;
        Object contact;
        int k;
        float damping = this.m_dampingStrength;
        for (k = 0; k < this.m_bodyContactCount; ++k) {
            contact = this.m_bodyContactBuffer[k];
            a = ((ParticleBodyContact)contact).index;
            Body b = ((ParticleBodyContact)contact).body;
            w = ((ParticleBodyContact)contact).weight;
            float m = ((ParticleBodyContact)contact).mass;
            Vec2 n = ((ParticleBodyContact)contact).normal;
            Vec2 p = ((Vec2[])this.m_positionBuffer.data)[a];
            float tempX = p.x - b.m_sweep.c.x;
            float tempY = p.y - b.m_sweep.c.y;
            Vec2 velA = ((Vec2[])this.m_velocityBuffer.data)[a];
            float vx = -b.m_angularVelocity * tempY + b.m_linearVelocity.x - velA.x;
            float vy = b.m_angularVelocity * tempX + b.m_linearVelocity.y - velA.y;
            float vn = vx * n.x + vy * n.y;
            if (!(vn < 0.0f)) continue;
            Vec2 f = this.tempVec;
            f.x = damping * w * m * vn * n.x;
            f.y = damping * w * m * vn * n.y;
            float invMass = this.getParticleInvMass();
            velA.x += invMass * f.x;
            velA.y += invMass * f.y;
            f.x = -f.x;
            f.y = -f.y;
            b.applyLinearImpulse(f, p, true);
        }
        for (k = 0; k < this.m_contactCount; ++k) {
            contact = this.m_contactBuffer[k];
            a = ((ParticleContact)contact).indexA;
            int b = ((ParticleContact)contact).indexB;
            w = ((ParticleContact)contact).weight;
            Vec2 n = ((ParticleContact)contact).normal;
            Vec2 velA = ((Vec2[])this.m_velocityBuffer.data)[a];
            Vec2 velB = ((Vec2[])this.m_velocityBuffer.data)[b];
            float vx = velB.x - velA.x;
            float vy = velB.y - velA.y;
            float vn = vx * n.x + vy * n.y;
            if (!(vn < 0.0f)) continue;
            float fx = damping * w * vn * n.x;
            float fy = damping * w * vn * n.y;
            velA.x += fx;
            velA.y += fy;
            velB.x -= fx;
            velB.y -= fy;
        }
    }

    public void solveWall(TimeStep step) {
        for (int i = 0; i < this.m_count; ++i) {
            if ((this.m_flagsBuffer.data[i] & 4) == 0) continue;
            Vec2 r = ((Vec2[])this.m_velocityBuffer.data)[i];
            r.x = 0.0f;
            r.y = 0.0f;
        }
    }

    void solveRigid(TimeStep step) {
        for (ParticleGroup group = this.m_groupList; group != null; group = group.getNext()) {
            if ((group.m_groupFlags & 2) == 0) continue;
            group.updateStatistics();
            Vec2 temp = this.tempVec;
            Vec2 cross = this.tempVec2;
            Rot rotation = this.tempRot;
            rotation.set(step.dt * group.m_angularVelocity);
            Rot.mulToOutUnsafe(rotation, group.m_center, cross);
            temp.set(group.m_linearVelocity).mulLocal(step.dt).addLocal(group.m_center).subLocal(cross);
            this.tempXf.p.set(temp);
            this.tempXf.q.set(rotation);
            Transform.mulToOut(this.tempXf, group.m_transform, group.m_transform);
            Transform velocityTransform = this.tempXf2;
            velocityTransform.p.x = step.inv_dt * this.tempXf.p.x;
            velocityTransform.p.y = step.inv_dt * this.tempXf.p.y;
            velocityTransform.q.s = step.inv_dt * this.tempXf.q.s;
            velocityTransform.q.c = step.inv_dt * (this.tempXf.q.c - 1.0f);
            for (int i = group.m_firstIndex; i < group.m_lastIndex; ++i) {
                Transform.mulToOutUnsafe(velocityTransform, ((Vec2[])this.m_positionBuffer.data)[i], ((Vec2[])this.m_velocityBuffer.data)[i]);
            }
        }
    }

    void solveElastic(TimeStep step) {
        float elasticStrength = step.inv_dt * this.m_elasticStrength;
        for (int k = 0; k < this.m_triadCount; ++k) {
            float rc;
            Triad triad = this.m_triadBuffer[k];
            if ((triad.flags & 0x10) == 0) continue;
            int a = triad.indexA;
            int b = triad.indexB;
            int c = triad.indexC;
            Vec2 oa = triad.pa;
            Vec2 ob = triad.pb;
            Vec2 oc = triad.pc;
            Vec2 pa = ((Vec2[])this.m_positionBuffer.data)[a];
            Vec2 pb = ((Vec2[])this.m_positionBuffer.data)[b];
            Vec2 pc = ((Vec2[])this.m_positionBuffer.data)[c];
            float px = 0.33333334f * (pa.x + pb.x + pc.x);
            float py = 0.33333334f * (pa.y + pb.y + pc.y);
            float rs = Vec2.cross(oa, pa) + Vec2.cross(ob, pb) + Vec2.cross(oc, pc);
            float r2 = rs * rs + (rc = Vec2.dot(oa, pa) + Vec2.dot(ob, pb) + Vec2.dot(oc, pc)) * rc;
            float invR = r2 == 0.0f ? Float.MAX_VALUE : MathUtils.sqrt(1.0f / r2);
            float strength = elasticStrength * triad.strength;
            float roax = (rc *= invR) * oa.x - (rs *= invR) * oa.y;
            float roay = rs * oa.x + rc * oa.y;
            float robx = rc * ob.x - rs * ob.y;
            float roby = rs * ob.x + rc * ob.y;
            float rocx = rc * oc.x - rs * oc.y;
            float rocy = rs * oc.x + rc * oc.y;
            Vec2 va = ((Vec2[])this.m_velocityBuffer.data)[a];
            Vec2 vb = ((Vec2[])this.m_velocityBuffer.data)[b];
            Vec2 vc = ((Vec2[])this.m_velocityBuffer.data)[c];
            va.x += strength * (roax - (pa.x - px));
            va.y += strength * (roay - (pa.y - py));
            vb.x += strength * (robx - (pb.x - px));
            vb.y += strength * (roby - (pb.y - py));
            vc.x += strength * (rocx - (pc.x - px));
            vc.y += strength * (rocy - (pc.y - py));
        }
    }

    void solveSpring(TimeStep step) {
        float springStrength = step.inv_dt * this.m_springStrength;
        for (int k = 0; k < this.m_pairCount; ++k) {
            Pair pair = this.m_pairBuffer[k];
            if ((pair.flags & 8) == 0) continue;
            int a = pair.indexA;
            int b = pair.indexB;
            Vec2 pa = ((Vec2[])this.m_positionBuffer.data)[a];
            Vec2 pb = ((Vec2[])this.m_positionBuffer.data)[b];
            float dx = pb.x - pa.x;
            float dy = pb.y - pa.y;
            float r0 = pair.distance;
            float r1 = MathUtils.sqrt(dx * dx + dy * dy);
            if (r1 == 0.0f) {
                r1 = Float.MAX_VALUE;
            }
            float strength = springStrength * pair.strength;
            float fx = strength * (r0 - r1) / r1 * dx;
            float fy = strength * (r0 - r1) / r1 * dy;
            Vec2 va = ((Vec2[])this.m_velocityBuffer.data)[a];
            Vec2 vb = ((Vec2[])this.m_velocityBuffer.data)[b];
            va.x -= fx;
            va.y -= fy;
            vb.x += fx;
            vb.y += fy;
        }
    }

    void solveTensile(TimeStep step) {
        this.m_accumulation2Buffer = this.requestParticleBuffer(Vec2.class, this.m_accumulation2Buffer);
        for (int i = 0; i < this.m_count; ++i) {
            this.m_accumulationBuffer[i] = 0.0f;
            this.m_accumulation2Buffer[i].setZero();
        }
        for (int k = 0; k < this.m_contactCount; ++k) {
            ParticleContact contact = this.m_contactBuffer[k];
            if ((contact.flags & 0x80) == 0) continue;
            int a = contact.indexA;
            int b = contact.indexB;
            float w = contact.weight;
            Vec2 n = contact.normal;
            int n2 = a;
            this.m_accumulationBuffer[n2] = this.m_accumulationBuffer[n2] + w;
            int n3 = b;
            this.m_accumulationBuffer[n3] = this.m_accumulationBuffer[n3] + w;
            Vec2 a2A = this.m_accumulation2Buffer[a];
            Vec2 a2B = this.m_accumulation2Buffer[b];
            float inter = (1.0f - w) * w;
            a2A.x -= inter * n.x;
            a2A.y -= inter * n.y;
            a2B.x += inter * n.x;
            a2B.y += inter * n.y;
        }
        float strengthA = this.m_surfaceTensionStrengthA * this.getCriticalVelocity(step);
        float strengthB = this.m_surfaceTensionStrengthB * this.getCriticalVelocity(step);
        for (int k = 0; k < this.m_contactCount; ++k) {
            ParticleContact contact = this.m_contactBuffer[k];
            if ((contact.flags & 0x80) == 0) continue;
            int a = contact.indexA;
            int b = contact.indexB;
            float w = contact.weight;
            Vec2 n = contact.normal;
            Vec2 a2A = this.m_accumulation2Buffer[a];
            Vec2 a2B = this.m_accumulation2Buffer[b];
            float h = this.m_accumulationBuffer[a] + this.m_accumulationBuffer[b];
            float sx = a2B.x - a2A.x;
            float sy = a2B.y - a2A.y;
            float fn = (strengthA * (h - 2.0f) + strengthB * (sx * n.x + sy * n.y)) * w;
            float fx = fn * n.x;
            float fy = fn * n.y;
            Vec2 va = ((Vec2[])this.m_velocityBuffer.data)[a];
            Vec2 vb = ((Vec2[])this.m_velocityBuffer.data)[b];
            va.x -= fx;
            va.y -= fy;
            vb.x += fx;
            vb.y += fy;
        }
    }

    void solveViscous(TimeStep step) {
        float w;
        int a;
        Object contact;
        int k;
        float viscousStrength = this.m_viscousStrength;
        for (k = 0; k < this.m_bodyContactCount; ++k) {
            contact = this.m_bodyContactBuffer[k];
            a = ((ParticleBodyContact)contact).index;
            if ((this.m_flagsBuffer.data[a] & 0x20) == 0) continue;
            Body b = ((ParticleBodyContact)contact).body;
            w = ((ParticleBodyContact)contact).weight;
            float m = ((ParticleBodyContact)contact).mass;
            Vec2 p = ((Vec2[])this.m_positionBuffer.data)[a];
            Vec2 va = ((Vec2[])this.m_velocityBuffer.data)[a];
            float tempX = p.x - b.m_sweep.c.x;
            float tempY = p.y - b.m_sweep.c.y;
            float vx = -b.m_angularVelocity * tempY + b.m_linearVelocity.x - va.x;
            float vy = b.m_angularVelocity * tempX + b.m_linearVelocity.y - va.y;
            Vec2 f = this.tempVec;
            float pInvMass = this.getParticleInvMass();
            f.x = viscousStrength * m * w * vx;
            f.y = viscousStrength * m * w * vy;
            va.x += pInvMass * f.x;
            va.y += pInvMass * f.y;
            f.x = -f.x;
            f.y = -f.y;
            b.applyLinearImpulse(f, p, true);
        }
        for (k = 0; k < this.m_contactCount; ++k) {
            contact = this.m_contactBuffer[k];
            if ((((ParticleContact)contact).flags & 0x20) == 0) continue;
            a = ((ParticleContact)contact).indexA;
            int b = ((ParticleContact)contact).indexB;
            w = ((ParticleContact)contact).weight;
            Vec2 va = ((Vec2[])this.m_velocityBuffer.data)[a];
            Vec2 vb = ((Vec2[])this.m_velocityBuffer.data)[b];
            float vx = vb.x - va.x;
            float vy = vb.y - va.y;
            float fx = viscousStrength * w * vx;
            float fy = viscousStrength * w * vy;
            va.x += fx;
            va.y += fy;
            vb.x -= fx;
            vb.y -= fy;
        }
    }

    void solvePowder(TimeStep step) {
        Object contact;
        int k;
        float powderStrength = this.m_powderStrength * this.getCriticalVelocity(step);
        float minWeight = 0.25f;
        for (k = 0; k < this.m_bodyContactCount; ++k) {
            float w;
            contact = this.m_bodyContactBuffer[k];
            int a = ((ParticleBodyContact)contact).index;
            if ((this.m_flagsBuffer.data[a] & 0x40) == 0 || !((w = ((ParticleBodyContact)contact).weight) > minWeight)) continue;
            Body b = ((ParticleBodyContact)contact).body;
            float m = ((ParticleBodyContact)contact).mass;
            Vec2 p = ((Vec2[])this.m_positionBuffer.data)[a];
            Vec2 n = ((ParticleBodyContact)contact).normal;
            Vec2 f = this.tempVec;
            Vec2 va = ((Vec2[])this.m_velocityBuffer.data)[a];
            float inter = powderStrength * m * (w - minWeight);
            float pInvMass = this.getParticleInvMass();
            f.x = inter * n.x;
            f.y = inter * n.y;
            va.x -= pInvMass * f.x;
            va.y -= pInvMass * f.y;
            b.applyLinearImpulse(f, p, true);
        }
        for (k = 0; k < this.m_contactCount; ++k) {
            float w;
            contact = this.m_contactBuffer[k];
            if ((((ParticleContact)contact).flags & 0x40) == 0 || !((w = ((ParticleContact)contact).weight) > minWeight)) continue;
            int a = ((ParticleContact)contact).indexA;
            int b = ((ParticleContact)contact).indexB;
            Vec2 n = ((ParticleContact)contact).normal;
            Vec2 va = ((Vec2[])this.m_velocityBuffer.data)[a];
            Vec2 vb = ((Vec2[])this.m_velocityBuffer.data)[b];
            float inter = powderStrength * (w - minWeight);
            float fx = inter * n.x;
            float fy = inter * n.y;
            va.x -= fx;
            va.y -= fy;
            vb.x += fx;
            vb.y += fy;
        }
    }

    void solveSolid(TimeStep step) {
        this.m_depthBuffer = this.requestParticleBuffer(this.m_depthBuffer);
        float ejectionStrength = step.inv_dt * this.m_ejectionStrength;
        for (int k = 0; k < this.m_contactCount; ++k) {
            ParticleContact contact = this.m_contactBuffer[k];
            int a = contact.indexA;
            int b = contact.indexB;
            if (this.m_groupBuffer[a] == this.m_groupBuffer[b]) continue;
            float w = contact.weight;
            Vec2 n = contact.normal;
            float h = this.m_depthBuffer[a] + this.m_depthBuffer[b];
            Vec2 va = ((Vec2[])this.m_velocityBuffer.data)[a];
            Vec2 vb = ((Vec2[])this.m_velocityBuffer.data)[b];
            float inter = ejectionStrength * h * w;
            float fx = inter * n.x;
            float fy = inter * n.y;
            va.x -= fx;
            va.y -= fy;
            vb.x += fx;
            vb.y += fy;
        }
    }

    void solveColorMixing(TimeStep step) {
        this.m_colorBuffer.data = this.requestParticleBuffer(ParticleColor.class, (ParticleColor[])this.m_colorBuffer.data);
        int colorMixing256 = (int)(256.0f * this.m_colorMixingStrength);
        for (int k = 0; k < this.m_contactCount; ++k) {
            ParticleContact contact = this.m_contactBuffer[k];
            int a = contact.indexA;
            int b = contact.indexB;
            if ((this.m_flagsBuffer.data[a] & this.m_flagsBuffer.data[b] & 0x100) == 0) continue;
            ParticleColor colorA = ((ParticleColor[])this.m_colorBuffer.data)[a];
            ParticleColor colorB = ((ParticleColor[])this.m_colorBuffer.data)[b];
            int dr = colorMixing256 * ((colorB.r & 0xFF) - (colorA.r & 0xFF)) >> 8;
            int dg = colorMixing256 * ((colorB.g & 0xFF) - (colorA.g & 0xFF)) >> 8;
            int db = colorMixing256 * ((colorB.b & 0xFF) - (colorA.b & 0xFF)) >> 8;
            int da = colorMixing256 * ((colorB.a & 0xFF) - (colorA.a & 0xFF)) >> 8;
            colorA.r = (byte)(colorA.r + dr);
            colorA.g = (byte)(colorA.g + dg);
            colorA.b = (byte)(colorA.b + db);
            colorA.a = (byte)(colorA.a + da);
            colorB.r = (byte)(colorB.r - dr);
            colorB.g = (byte)(colorB.g - dg);
            colorB.b = (byte)(colorB.b - db);
            colorB.a = (byte)(colorB.a - da);
        }
    }

    void solveZombie() {
        ParticleGroup group;
        Object contact;
        int k;
        Object temp;
        int i;
        int newCount = 0;
        int[] newIndices = new int[this.m_count];
        for (int i2 = 0; i2 < this.m_count; ++i2) {
            int flags = this.m_flagsBuffer.data[i2];
            if ((flags & 2) != 0) {
                ParticleDestructionListener destructionListener = this.m_world.getParticleDestructionListener();
                if ((flags & 0x200) != 0 && destructionListener != null) {
                    destructionListener.sayGoodbye(i2);
                }
                newIndices[i2] = -1;
                continue;
            }
            newIndices[i2] = newCount;
            if (i2 != newCount) {
                this.m_flagsBuffer.data[newCount] = this.m_flagsBuffer.data[i2];
                ((Vec2[])this.m_positionBuffer.data)[newCount].set(((Vec2[])this.m_positionBuffer.data)[i2]);
                ((Vec2[])this.m_velocityBuffer.data)[newCount].set(((Vec2[])this.m_velocityBuffer.data)[i2]);
                this.m_groupBuffer[newCount] = this.m_groupBuffer[i2];
                if (this.m_depthBuffer != null) {
                    this.m_depthBuffer[newCount] = this.m_depthBuffer[i2];
                }
                if (this.m_colorBuffer.data != null) {
                    ((ParticleColor[])this.m_colorBuffer.data)[newCount].set(((ParticleColor[])this.m_colorBuffer.data)[i2]);
                }
                if (this.m_userDataBuffer.data != null) {
                    this.m_userDataBuffer.data[newCount] = this.m_userDataBuffer.data[i2];
                }
            }
            ++newCount;
        }
        for (int k2 = 0; k2 < this.m_proxyCount; ++k2) {
            Proxy proxy = this.m_proxyBuffer[k2];
            proxy.index = newIndices[proxy.index];
        }
        int j = this.m_proxyCount;
        for (i = 0; i < j; ++i) {
            if (!Test.IsProxyInvalid(this.m_proxyBuffer[i])) continue;
            temp = this.m_proxyBuffer[--j];
            this.m_proxyBuffer[j] = this.m_proxyBuffer[i];
            this.m_proxyBuffer[i] = temp;
            --i;
        }
        this.m_proxyCount = j;
        for (k = 0; k < this.m_contactCount; ++k) {
            contact = this.m_contactBuffer[k];
            ((ParticleContact)contact).indexA = newIndices[((ParticleContact)contact).indexA];
            ((ParticleContact)contact).indexB = newIndices[((ParticleContact)contact).indexB];
        }
        j = this.m_contactCount;
        for (i = 0; i < j; ++i) {
            if (!Test.IsContactInvalid(this.m_contactBuffer[i])) continue;
            temp = this.m_contactBuffer[--j];
            this.m_contactBuffer[j] = this.m_contactBuffer[i];
            this.m_contactBuffer[i] = temp;
            --i;
        }
        this.m_contactCount = j;
        for (k = 0; k < this.m_bodyContactCount; ++k) {
            contact = this.m_bodyContactBuffer[k];
            ((ParticleBodyContact)contact).index = newIndices[((ParticleBodyContact)contact).index];
        }
        j = this.m_bodyContactCount;
        for (i = 0; i < j; ++i) {
            if (!Test.IsBodyContactInvalid(this.m_bodyContactBuffer[i])) continue;
            temp = this.m_bodyContactBuffer[--j];
            this.m_bodyContactBuffer[j] = this.m_bodyContactBuffer[i];
            this.m_bodyContactBuffer[i] = temp;
            --i;
        }
        this.m_bodyContactCount = j;
        for (k = 0; k < this.m_pairCount; ++k) {
            Pair pair = this.m_pairBuffer[k];
            pair.indexA = newIndices[pair.indexA];
            pair.indexB = newIndices[pair.indexB];
        }
        j = this.m_pairCount;
        for (i = 0; i < j; ++i) {
            if (!Test.IsPairInvalid(this.m_pairBuffer[i])) continue;
            temp = this.m_pairBuffer[--j];
            this.m_pairBuffer[j] = this.m_pairBuffer[i];
            this.m_pairBuffer[i] = temp;
            --i;
        }
        this.m_pairCount = j;
        for (k = 0; k < this.m_triadCount; ++k) {
            Triad triad = this.m_triadBuffer[k];
            triad.indexA = newIndices[triad.indexA];
            triad.indexB = newIndices[triad.indexB];
            triad.indexC = newIndices[triad.indexC];
        }
        j = this.m_triadCount;
        for (i = 0; i < j; ++i) {
            if (!Test.IsTriadInvalid(this.m_triadBuffer[i])) continue;
            temp = this.m_triadBuffer[--j];
            this.m_triadBuffer[j] = this.m_triadBuffer[i];
            this.m_triadBuffer[i] = temp;
            --i;
        }
        this.m_triadCount = j;
        for (group = this.m_groupList; group != null; group = group.getNext()) {
            int firstIndex = newCount;
            int lastIndex = 0;
            boolean modified = false;
            for (int i3 = group.m_firstIndex; i3 < group.m_lastIndex; ++i3) {
                j = newIndices[i3];
                if (j >= 0) {
                    firstIndex = MathUtils.min(firstIndex, j);
                    lastIndex = MathUtils.max(lastIndex, j + 1);
                    continue;
                }
                modified = true;
            }
            if (firstIndex < lastIndex) {
                group.m_firstIndex = firstIndex;
                group.m_lastIndex = lastIndex;
                if (!modified || (group.m_groupFlags & 2) == 0) continue;
                group.m_toBeSplit = true;
                continue;
            }
            group.m_firstIndex = 0;
            group.m_lastIndex = 0;
            if (!group.m_destroyAutomatically) continue;
            group.m_toBeDestroyed = true;
        }
        this.m_count = newCount;
        group = this.m_groupList;
        while (group != null) {
            ParticleGroup next = group.getNext();
            if (group.m_toBeDestroyed) {
                this.destroyParticleGroup(group);
            } else if (group.m_toBeSplit) {
                // empty if block
            }
            group = next;
        }
    }

    void RotateBuffer(int start, int mid, int end) {
        Object contact;
        int k;
        if (start == mid || mid == end) {
            return;
        }
        this.newIndices.start = start;
        this.newIndices.mid = mid;
        this.newIndices.end = end;
        BufferUtils.rotate(this.m_flagsBuffer.data, start, mid, end);
        BufferUtils.rotate((Vec2[])this.m_positionBuffer.data, start, mid, end);
        BufferUtils.rotate((Vec2[])this.m_velocityBuffer.data, start, mid, end);
        BufferUtils.rotate(this.m_groupBuffer, start, mid, end);
        if (this.m_depthBuffer != null) {
            BufferUtils.rotate(this.m_depthBuffer, start, mid, end);
        }
        if (this.m_colorBuffer.data != null) {
            BufferUtils.rotate((ParticleColor[])this.m_colorBuffer.data, start, mid, end);
        }
        if (this.m_userDataBuffer.data != null) {
            BufferUtils.rotate(this.m_userDataBuffer.data, start, mid, end);
        }
        for (k = 0; k < this.m_proxyCount; ++k) {
            Proxy proxy = this.m_proxyBuffer[k];
            proxy.index = this.newIndices.getIndex(proxy.index);
        }
        for (k = 0; k < this.m_contactCount; ++k) {
            contact = this.m_contactBuffer[k];
            ((ParticleContact)contact).indexA = this.newIndices.getIndex(((ParticleContact)contact).indexA);
            ((ParticleContact)contact).indexB = this.newIndices.getIndex(((ParticleContact)contact).indexB);
        }
        for (k = 0; k < this.m_bodyContactCount; ++k) {
            contact = this.m_bodyContactBuffer[k];
            ((ParticleBodyContact)contact).index = this.newIndices.getIndex(((ParticleBodyContact)contact).index);
        }
        for (k = 0; k < this.m_pairCount; ++k) {
            Pair pair = this.m_pairBuffer[k];
            pair.indexA = this.newIndices.getIndex(pair.indexA);
            pair.indexB = this.newIndices.getIndex(pair.indexB);
        }
        for (k = 0; k < this.m_triadCount; ++k) {
            Triad triad = this.m_triadBuffer[k];
            triad.indexA = this.newIndices.getIndex(triad.indexA);
            triad.indexB = this.newIndices.getIndex(triad.indexB);
            triad.indexC = this.newIndices.getIndex(triad.indexC);
        }
        for (ParticleGroup group = this.m_groupList; group != null; group = group.getNext()) {
            group.m_firstIndex = this.newIndices.getIndex(group.m_firstIndex);
            group.m_lastIndex = this.newIndices.getIndex(group.m_lastIndex - 1) + 1;
        }
    }

    public void setParticleRadius(float radius) {
        this.m_particleDiameter = 2.0f * radius;
        this.m_squaredDiameter = this.m_particleDiameter * this.m_particleDiameter;
        this.m_inverseDiameter = 1.0f / this.m_particleDiameter;
    }

    public void setParticleDensity(float density) {
        this.m_density = density;
        this.m_inverseDensity = 1.0f / this.m_density;
    }

    public float getParticleDensity() {
        return this.m_density;
    }

    public void setParticleGravityScale(float gravityScale) {
        this.m_gravityScale = gravityScale;
    }

    public float getParticleGravityScale() {
        return this.m_gravityScale;
    }

    public void setParticleDamping(float damping) {
        this.m_dampingStrength = damping;
    }

    public float getParticleDamping() {
        return this.m_dampingStrength;
    }

    public float getParticleRadius() {
        return this.m_particleDiameter / 2.0f;
    }

    float getCriticalVelocity(TimeStep step) {
        return this.m_particleDiameter * step.inv_dt;
    }

    float getCriticalVelocitySquared(TimeStep step) {
        float velocity = this.getCriticalVelocity(step);
        return velocity * velocity;
    }

    float getCriticalPressure(TimeStep step) {
        return this.m_density * this.getCriticalVelocitySquared(step);
    }

    float getParticleStride() {
        return 0.75f * this.m_particleDiameter;
    }

    float getParticleMass() {
        float stride = this.getParticleStride();
        return this.m_density * stride * stride;
    }

    float getParticleInvMass() {
        return 1.777777f * this.m_inverseDensity * this.m_inverseDiameter * this.m_inverseDiameter;
    }

    public int[] getParticleFlagsBuffer() {
        return this.m_flagsBuffer.data;
    }

    public Vec2[] getParticlePositionBuffer() {
        return (Vec2[])this.m_positionBuffer.data;
    }

    public Vec2[] getParticleVelocityBuffer() {
        return (Vec2[])this.m_velocityBuffer.data;
    }

    public ParticleColor[] getParticleColorBuffer() {
        this.m_colorBuffer.data = this.requestParticleBuffer(ParticleColor.class, (ParticleColor[])this.m_colorBuffer.data);
        return (ParticleColor[])this.m_colorBuffer.data;
    }

    public Object[] getParticleUserDataBuffer() {
        this.m_userDataBuffer.data = this.requestParticleBuffer(Object.class, this.m_userDataBuffer.data);
        return this.m_userDataBuffer.data;
    }

    public int getParticleMaxCount() {
        return this.m_maxCount;
    }

    public void setParticleMaxCount(int count) {
        assert (this.m_count <= count);
        this.m_maxCount = count;
    }

    void setParticleBuffer(ParticleBufferInt buffer, int[] newData, int newCapacity) {
        assert (newData != null && newCapacity != 0 || newData == null && newCapacity == 0);
        if (buffer.userSuppliedCapacity != 0) {
            // empty if block
        }
        buffer.data = newData;
        buffer.userSuppliedCapacity = newCapacity;
    }

    <T> void setParticleBuffer(ParticleBuffer<T> buffer, T[] newData, int newCapacity) {
        assert (newData != null && newCapacity != 0 || newData == null && newCapacity == 0);
        if (buffer.userSuppliedCapacity != 0) {
            // empty if block
        }
        buffer.data = newData;
        buffer.userSuppliedCapacity = newCapacity;
    }

    public void setParticleFlagsBuffer(int[] buffer, int capacity) {
        this.setParticleBuffer(this.m_flagsBuffer, buffer, capacity);
    }

    public void setParticlePositionBuffer(Vec2[] buffer, int capacity) {
        this.setParticleBuffer(this.m_positionBuffer, buffer, capacity);
    }

    public void setParticleVelocityBuffer(Vec2[] buffer, int capacity) {
        this.setParticleBuffer(this.m_velocityBuffer, buffer, capacity);
    }

    public void setParticleColorBuffer(ParticleColor[] buffer, int capacity) {
        this.setParticleBuffer(this.m_colorBuffer, buffer, capacity);
    }

    public ParticleGroup[] getParticleGroupBuffer() {
        return this.m_groupBuffer;
    }

    public int getParticleGroupCount() {
        return this.m_groupCount;
    }

    public ParticleGroup[] getParticleGroupList() {
        return this.m_groupBuffer;
    }

    public int getParticleCount() {
        return this.m_count;
    }

    public void setParticleUserDataBuffer(Object[] buffer, int capacity) {
        this.setParticleBuffer(this.m_userDataBuffer, buffer, capacity);
    }

    private static final int lowerBound(Proxy[] ray, int length, long tag) {
        int left = 0;
        while (length > 0) {
            int step = length / 2;
            int curr = left + step;
            if (ray[curr].tag < tag) {
                left = curr + 1;
                length -= step + 1;
                continue;
            }
            length = step;
        }
        return left;
    }

    private static final int upperBound(Proxy[] ray, int length, long tag) {
        int left = 0;
        while (length > 0) {
            int step = length / 2;
            int curr = left + step;
            if (ray[curr].tag <= tag) {
                left = curr + 1;
                length -= step + 1;
                continue;
            }
            length = step;
        }
        return left;
    }

    public void queryAABB(ParticleQueryCallback callback, AABB aabb) {
        if (this.m_proxyCount == 0) {
            return;
        }
        float lowerBoundX = aabb.lowerBound.x;
        float lowerBoundY = aabb.lowerBound.y;
        float upperBoundX = aabb.upperBound.x;
        float upperBoundY = aabb.upperBound.y;
        int firstProxy = ParticleSystem.lowerBound(this.m_proxyBuffer, this.m_proxyCount, ParticleSystem.computeTag(this.m_inverseDiameter * lowerBoundX, this.m_inverseDiameter * lowerBoundY));
        int lastProxy = ParticleSystem.upperBound(this.m_proxyBuffer, this.m_proxyCount, ParticleSystem.computeTag(this.m_inverseDiameter * upperBoundX, this.m_inverseDiameter * upperBoundY));
        for (int proxy = firstProxy; proxy < lastProxy; ++proxy) {
            int i = this.m_proxyBuffer[proxy].index;
            Vec2 p = ((Vec2[])this.m_positionBuffer.data)[i];
            if (lowerBoundX < p.x && p.x < upperBoundX && lowerBoundY < p.y && p.y < upperBoundY && !callback.reportParticle(i)) break;
        }
    }

    public void raycast(ParticleRaycastCallback callback, Vec2 point1, Vec2 point2) {
        if (this.m_proxyCount == 0) {
            return;
        }
        int firstProxy = ParticleSystem.lowerBound(this.m_proxyBuffer, this.m_proxyCount, ParticleSystem.computeTag(this.m_inverseDiameter * MathUtils.min(point1.x, point2.x) - 1.0f, this.m_inverseDiameter * MathUtils.min(point1.y, point2.y) - 1.0f));
        int lastProxy = ParticleSystem.upperBound(this.m_proxyBuffer, this.m_proxyCount, ParticleSystem.computeTag(this.m_inverseDiameter * MathUtils.max(point1.x, point2.x) + 1.0f, this.m_inverseDiameter * MathUtils.max(point1.y, point2.y) + 1.0f));
        float fraction = 1.0f;
        float vx = point2.x - point1.x;
        float vy = point2.y - point1.y;
        float v2 = vx * vx + vy * vy;
        if (v2 == 0.0f) {
            v2 = Float.MAX_VALUE;
        }
        for (int proxy = firstProxy; proxy < lastProxy; ++proxy) {
            float sqrtDeterminant;
            float t;
            int i = this.m_proxyBuffer[proxy].index;
            Vec2 posI = ((Vec2[])this.m_positionBuffer.data)[i];
            float px = point1.x - posI.x;
            float py = point1.y - posI.y;
            float pv = px * vx + py * vy;
            float p2 = px * px + py * py;
            float determinant = pv * pv - v2 * (p2 - this.m_squaredDiameter);
            if (!(determinant >= 0.0f) || (t = (-pv - (sqrtDeterminant = MathUtils.sqrt(determinant))) / v2) > fraction || t < 0.0f && ((t = (-pv + sqrtDeterminant) / v2) < 0.0f || t > fraction)) continue;
            Vec2 n = this.tempVec;
            this.tempVec.x = px + t * vx;
            this.tempVec.y = py + t * vy;
            n.normalize();
            Vec2 point = this.tempVec2;
            point.x = point1.x + t * vx;
            point.y = point1.y + t * vy;
            float f = callback.reportParticle(i, point, n, t);
            fraction = MathUtils.min(fraction, f);
            if (fraction <= 0.0f) break;
        }
    }

    public float computeParticleCollisionEnergy() {
        float sum_v2 = 0.0f;
        for (int k = 0; k < this.m_contactCount; ++k) {
            ParticleContact contact = this.m_contactBuffer[k];
            int a = contact.indexA;
            int b = contact.indexB;
            Vec2 n = contact.normal;
            Vec2 va = ((Vec2[])this.m_velocityBuffer.data)[a];
            Vec2 vb = ((Vec2[])this.m_velocityBuffer.data)[b];
            float vx = vb.x - va.x;
            float vy = vb.y - va.y;
            float vn = vx * n.x + vy * n.y;
            if (!(vn < 0.0f)) continue;
            sum_v2 += vn * vn;
        }
        return 0.5f * this.getParticleMass() * sum_v2;
    }

    static <T> T[] reallocateBuffer(ParticleBuffer<T> buffer, int oldCapacity, int newCapacity, boolean deferred) {
        assert (newCapacity > oldCapacity);
        return BufferUtils.reallocateBuffer(buffer.dataClass, buffer.data, buffer.userSuppliedCapacity, oldCapacity, newCapacity, deferred);
    }

    static int[] reallocateBuffer(ParticleBufferInt buffer, int oldCapacity, int newCapacity, boolean deferred) {
        assert (newCapacity > oldCapacity);
        return BufferUtils.reallocateBuffer(buffer.data, buffer.userSuppliedCapacity, oldCapacity, newCapacity, deferred);
    }

    <T> T[] requestParticleBuffer(Class<T> klass, T[] buffer) {
        if (buffer == null) {
            buffer = (Object[])Array.newInstance(klass, this.m_internalAllocatedCapacity);
            for (int i = 0; i < this.m_internalAllocatedCapacity; ++i) {
                try {
                    buffer[i] = klass.newInstance();
                    continue;
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return buffer;
    }

    float[] requestParticleBuffer(float[] buffer) {
        if (buffer == null) {
            buffer = new float[this.m_internalAllocatedCapacity];
        }
        return buffer;
    }

    static class DestroyParticlesInShapeCallback
    implements ParticleQueryCallback {
        ParticleSystem system;
        Shape shape;
        Transform xf;
        boolean callDestructionListener;
        int destroyed;

        public void init(ParticleSystem system, Shape shape, Transform xf, boolean callDestructionListener) {
            this.system = system;
            this.shape = shape;
            this.xf = xf;
            this.destroyed = 0;
            this.callDestructionListener = callDestructionListener;
        }

        @Override
        public boolean reportParticle(int index) {
            assert (index >= 0 && index < this.system.m_count);
            if (this.shape.testPoint(this.xf, ((Vec2[])this.system.m_positionBuffer.data)[index])) {
                this.system.destroyParticle(index, this.callDestructionListener);
                ++this.destroyed;
            }
            return true;
        }
    }

    static class CreateParticleGroupCallback
    implements VoronoiDiagram.VoronoiDiagramCallback {
        ParticleSystem system;
        ParticleGroupDef def;
        int firstIndex;

        CreateParticleGroupCallback() {
        }

        @Override
        public void callback(int a, int b, int c) {
            Vec2 pa = ((Vec2[])this.system.m_positionBuffer.data)[a];
            Vec2 pb = ((Vec2[])this.system.m_positionBuffer.data)[b];
            Vec2 pc = ((Vec2[])this.system.m_positionBuffer.data)[c];
            float dabx = pa.x - pb.x;
            float daby = pa.y - pb.y;
            float dbcx = pb.x - pc.x;
            float dbcy = pb.y - pc.y;
            float dcax = pc.x - pa.x;
            float dcay = pc.y - pa.y;
            float maxDistanceSquared = 4.0f * this.system.m_squaredDiameter;
            if (dabx * dabx + daby * daby < maxDistanceSquared && dbcx * dbcx + dbcy * dbcy < maxDistanceSquared && dcax * dcax + dcay * dcay < maxDistanceSquared) {
                if (this.system.m_triadCount >= this.system.m_triadCapacity) {
                    int oldCapacity = this.system.m_triadCapacity;
                    int newCapacity = this.system.m_triadCount != 0 ? 2 * this.system.m_triadCount : 256;
                    this.system.m_triadBuffer = BufferUtils.reallocateBuffer(Triad.class, this.system.m_triadBuffer, oldCapacity, newCapacity);
                    this.system.m_triadCapacity = newCapacity;
                }
                Triad triad = this.system.m_triadBuffer[this.system.m_triadCount];
                triad.indexA = a;
                triad.indexB = b;
                triad.indexC = c;
                triad.flags = this.system.m_flagsBuffer.data[a] | this.system.m_flagsBuffer.data[b] | this.system.m_flagsBuffer.data[c];
                triad.strength = this.def.strength;
                float midPointx = 0.33333334f * (pa.x + pb.x + pc.x);
                float midPointy = 0.33333334f * (pa.y + pb.y + pc.y);
                triad.pa.x = pa.x - midPointx;
                triad.pa.y = pa.y - midPointy;
                triad.pb.x = pb.x - midPointx;
                triad.pb.y = pb.y - midPointy;
                triad.pc.x = pc.x - midPointx;
                triad.pc.y = pc.y - midPointy;
                triad.ka = -(dcax * dabx + dcay * daby);
                triad.kb = -(dabx * dbcx + daby * dbcy);
                triad.kc = -(dbcx * dcax + dbcy * dcay);
                triad.s = Vec2.cross(pa, pb) + Vec2.cross(pb, pc) + Vec2.cross(pc, pa);
                ++this.system.m_triadCount;
            }
        }
    }

    static class UpdateBodyContactsCallback
    implements QueryCallback {
        ParticleSystem system;
        private final Vec2 tempVec = new Vec2();

        UpdateBodyContactsCallback() {
        }

        @Override
        public boolean reportFixture(Fixture fixture) {
            if (fixture.isSensor()) {
                return true;
            }
            Shape shape = fixture.getShape();
            Body b = fixture.getBody();
            Vec2 bp = b.getWorldCenter();
            float bm = b.getMass();
            float bI = b.getInertia() - bm * b.getLocalCenter().lengthSquared();
            float invBm = bm > 0.0f ? 1.0f / bm : 0.0f;
            float invBI = bI > 0.0f ? 1.0f / bI : 0.0f;
            int childCount = shape.getChildCount();
            for (int childIndex = 0; childIndex < childCount; ++childIndex) {
                AABB aabb = fixture.getAABB(childIndex);
                float aabblowerBoundx = aabb.lowerBound.x - this.system.m_particleDiameter;
                float aabblowerBoundy = aabb.lowerBound.y - this.system.m_particleDiameter;
                float aabbupperBoundx = aabb.upperBound.x + this.system.m_particleDiameter;
                float aabbupperBoundy = aabb.upperBound.y + this.system.m_particleDiameter;
                int firstProxy = ParticleSystem.lowerBound(this.system.m_proxyBuffer, this.system.m_proxyCount, ParticleSystem.computeTag(this.system.m_inverseDiameter * aabblowerBoundx, this.system.m_inverseDiameter * aabblowerBoundy));
                int lastProxy = ParticleSystem.upperBound(this.system.m_proxyBuffer, this.system.m_proxyCount, ParticleSystem.computeTag(this.system.m_inverseDiameter * aabbupperBoundx, this.system.m_inverseDiameter * aabbupperBoundy));
                for (int proxy = firstProxy; proxy != lastProxy; ++proxy) {
                    Vec2 n;
                    float d;
                    int a = this.system.m_proxyBuffer[proxy].index;
                    Vec2 ap = ((Vec2[])this.system.m_positionBuffer.data)[a];
                    if (!(aabblowerBoundx <= ap.x) || !(ap.x <= aabbupperBoundx) || !(aabblowerBoundy <= ap.y) || !(ap.y <= aabbupperBoundy) || !((d = fixture.computeDistance(ap, childIndex, n = this.tempVec)) < this.system.m_particleDiameter)) continue;
                    float invAm = (this.system.m_flagsBuffer.data[a] & 4) != 0 ? 0.0f : this.system.getParticleInvMass();
                    float rpx = ap.x - bp.x;
                    float rpy = ap.y - bp.y;
                    float rpn = rpx * n.y - rpy * n.x;
                    if (this.system.m_bodyContactCount >= this.system.m_bodyContactCapacity) {
                        int oldCapacity = this.system.m_bodyContactCapacity;
                        int newCapacity = this.system.m_bodyContactCount != 0 ? 2 * this.system.m_bodyContactCount : 256;
                        this.system.m_bodyContactBuffer = BufferUtils.reallocateBuffer(ParticleBodyContact.class, this.system.m_bodyContactBuffer, oldCapacity, newCapacity);
                        this.system.m_bodyContactCapacity = newCapacity;
                    }
                    ParticleBodyContact contact = this.system.m_bodyContactBuffer[this.system.m_bodyContactCount];
                    contact.index = a;
                    contact.body = b;
                    contact.weight = 1.0f - d * this.system.m_inverseDiameter;
                    contact.normal.x = -n.x;
                    contact.normal.y = -n.y;
                    contact.mass = 1.0f / (invAm + invBm + invBI * rpn * rpn);
                    ++this.system.m_bodyContactCount;
                }
            }
            return true;
        }
    }

    static class SolveCollisionCallback
    implements QueryCallback {
        ParticleSystem system;
        TimeStep step;
        private final RayCastInput input = new RayCastInput();
        private final RayCastOutput output = new RayCastOutput();
        private final Vec2 tempVec = new Vec2();
        private final Vec2 tempVec2 = new Vec2();

        SolveCollisionCallback() {
        }

        @Override
        public boolean reportFixture(Fixture fixture) {
            if (fixture.isSensor()) {
                return true;
            }
            Shape shape = fixture.getShape();
            Body body = fixture.getBody();
            int childCount = shape.getChildCount();
            for (int childIndex = 0; childIndex < childCount; ++childIndex) {
                AABB aabb = fixture.getAABB(childIndex);
                float aabblowerBoundx = aabb.lowerBound.x - this.system.m_particleDiameter;
                float aabblowerBoundy = aabb.lowerBound.y - this.system.m_particleDiameter;
                float aabbupperBoundx = aabb.upperBound.x + this.system.m_particleDiameter;
                float aabbupperBoundy = aabb.upperBound.y + this.system.m_particleDiameter;
                int firstProxy = ParticleSystem.lowerBound(this.system.m_proxyBuffer, this.system.m_proxyCount, ParticleSystem.computeTag(this.system.m_inverseDiameter * aabblowerBoundx, this.system.m_inverseDiameter * aabblowerBoundy));
                int lastProxy = ParticleSystem.upperBound(this.system.m_proxyBuffer, this.system.m_proxyCount, ParticleSystem.computeTag(this.system.m_inverseDiameter * aabbupperBoundx, this.system.m_inverseDiameter * aabbupperBoundy));
                for (int proxy = firstProxy; proxy != lastProxy; ++proxy) {
                    int a = this.system.m_proxyBuffer[proxy].index;
                    Vec2 ap = ((Vec2[])this.system.m_positionBuffer.data)[a];
                    if (!(aabblowerBoundx <= ap.x) || !(ap.x <= aabbupperBoundx) || !(aabblowerBoundy <= ap.y) || !(ap.y <= aabbupperBoundy)) continue;
                    Vec2 av = ((Vec2[])this.system.m_velocityBuffer.data)[a];
                    Vec2 temp = this.tempVec;
                    Transform.mulTransToOutUnsafe(body.m_xf0, ap, temp);
                    Transform.mulToOutUnsafe(body.m_xf, temp, this.input.p1);
                    this.input.p2.x = ap.x + this.step.dt * av.x;
                    this.input.p2.y = ap.y + this.step.dt * av.y;
                    this.input.maxFraction = 1.0f;
                    if (!fixture.raycast(this.output, this.input, childIndex)) continue;
                    Vec2 p = this.tempVec;
                    p.x = (1.0f - this.output.fraction) * this.input.p1.x + this.output.fraction * this.input.p2.x + Settings.linearSlop * this.output.normal.x;
                    p.y = (1.0f - this.output.fraction) * this.input.p1.y + this.output.fraction * this.input.p2.y + Settings.linearSlop * this.output.normal.y;
                    float vx = this.step.inv_dt * (p.x - ap.x);
                    float vy = this.step.inv_dt * (p.y - ap.y);
                    av.x = vx;
                    av.y = vy;
                    float particleMass = this.system.getParticleMass();
                    float ax = particleMass * (av.x - vx);
                    float ay = particleMass * (av.y - vy);
                    Vec2 b = this.output.normal;
                    float fdn = ax * b.x + ay * b.y;
                    Vec2 f = this.tempVec2;
                    f.x = fdn * b.x;
                    f.y = fdn * b.y;
                    body.applyLinearImpulse(f, p, true);
                }
            }
            return true;
        }
    }

    private static class NewIndices {
        int start;
        int mid;
        int end;

        private NewIndices() {
        }

        final int getIndex(int i) {
            if (i < this.start) {
                return i;
            }
            if (i < this.mid) {
                return i + this.end - this.mid;
            }
            if (i < this.end) {
                return i + this.start - this.mid;
            }
            return i;
        }
    }

    static class ParticleBufferInt {
        int[] data;
        int userSuppliedCapacity;

        ParticleBufferInt() {
        }
    }

    public static class ParticleBuffer<T> {
        public T[] data;
        final Class<T> dataClass;
        int userSuppliedCapacity;

        public ParticleBuffer(Class<T> dataClass) {
            this.dataClass = dataClass;
        }
    }

    public static class Proxy
    implements Comparable<Proxy> {
        int index;
        long tag;

        @Override
        public int compareTo(Proxy o) {
            return this.tag - o.tag < 0L ? -1 : (o.tag == this.tag ? 0 : 1);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            Proxy other = (Proxy)obj;
            return this.tag == other.tag;
        }
    }

    public static class Pair {
        int indexA;
        int indexB;
        int flags;
        float strength;
        float distance;
    }

    static class JoinParticleGroupsCallback
    implements VoronoiDiagram.VoronoiDiagramCallback {
        ParticleSystem system;
        ParticleGroup groupA;
        ParticleGroup groupB;

        JoinParticleGroupsCallback() {
        }

        @Override
        public void callback(int a, int b, int c) {
            int cf;
            int bf;
            int af;
            int countA = (a < this.groupB.m_firstIndex ? 1 : 0) + (b < this.groupB.m_firstIndex ? 1 : 0) + (c < this.groupB.m_firstIndex ? 1 : 0);
            if (countA > 0 && countA < 3 && ((af = this.system.m_flagsBuffer.data[a]) & (bf = this.system.m_flagsBuffer.data[b]) & (cf = this.system.m_flagsBuffer.data[c]) & 0x10) != 0) {
                Vec2 pa = ((Vec2[])this.system.m_positionBuffer.data)[a];
                Vec2 pb = ((Vec2[])this.system.m_positionBuffer.data)[b];
                Vec2 pc = ((Vec2[])this.system.m_positionBuffer.data)[c];
                float dabx = pa.x - pb.x;
                float daby = pa.y - pb.y;
                float dbcx = pb.x - pc.x;
                float dbcy = pb.y - pc.y;
                float dcax = pc.x - pa.x;
                float dcay = pc.y - pa.y;
                float maxDistanceSquared = 4.0f * this.system.m_squaredDiameter;
                if (dabx * dabx + daby * daby < maxDistanceSquared && dbcx * dbcx + dbcy * dbcy < maxDistanceSquared && dcax * dcax + dcay * dcay < maxDistanceSquared) {
                    if (this.system.m_triadCount >= this.system.m_triadCapacity) {
                        int oldCapacity = this.system.m_triadCapacity;
                        int newCapacity = this.system.m_triadCount != 0 ? 2 * this.system.m_triadCount : 256;
                        this.system.m_triadBuffer = BufferUtils.reallocateBuffer(Triad.class, this.system.m_triadBuffer, oldCapacity, newCapacity);
                        this.system.m_triadCapacity = newCapacity;
                    }
                    Triad triad = this.system.m_triadBuffer[this.system.m_triadCount];
                    triad.indexA = a;
                    triad.indexB = b;
                    triad.indexC = c;
                    triad.flags = af | bf | cf;
                    triad.strength = MathUtils.min(this.groupA.m_strength, this.groupB.m_strength);
                    float midPointx = 0.33333334f * (pa.x + pb.x + pc.x);
                    float midPointy = 0.33333334f * (pa.y + pb.y + pc.y);
                    triad.pa.x = pa.x - midPointx;
                    triad.pa.y = pa.y - midPointy;
                    triad.pb.x = pb.x - midPointx;
                    triad.pb.y = pb.y - midPointy;
                    triad.pc.x = pc.x - midPointx;
                    triad.pc.y = pc.y - midPointy;
                    triad.ka = -(dcax * dabx + dcay * daby);
                    triad.kb = -(dabx * dbcx + daby * dbcy);
                    triad.kc = -(dbcx * dcax + dbcy * dcay);
                    triad.s = Vec2.cross(pa, pb) + Vec2.cross(pb, pc) + Vec2.cross(pc, pa);
                    ++this.system.m_triadCount;
                }
            }
        }
    }

    public static class Triad {
        int indexA;
        int indexB;
        int indexC;
        int flags;
        float strength;
        final Vec2 pa = new Vec2();
        final Vec2 pb = new Vec2();
        final Vec2 pc = new Vec2();
        float ka;
        float kb;
        float kc;
        float s;
    }

    static class Test {
        Test() {
        }

        static boolean IsProxyInvalid(Proxy proxy) {
            return proxy.index < 0;
        }

        static boolean IsContactInvalid(ParticleContact contact) {
            return contact.indexA < 0 || contact.indexB < 0;
        }

        static boolean IsBodyContactInvalid(ParticleBodyContact contact) {
            return contact.index < 0;
        }

        static boolean IsPairInvalid(Pair pair) {
            return pair.indexA < 0 || pair.indexB < 0;
        }

        static boolean IsTriadInvalid(Triad triad) {
            return triad.indexA < 0 || triad.indexB < 0 || triad.indexC < 0;
        }
    }
}

