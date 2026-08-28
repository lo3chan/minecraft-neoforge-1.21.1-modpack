/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.dynamics;

import net.diebuddies.jbox2d.callbacks.ContactFilter;
import net.diebuddies.jbox2d.callbacks.ContactListener;
import net.diebuddies.jbox2d.callbacks.DebugDraw;
import net.diebuddies.jbox2d.callbacks.DestructionListener;
import net.diebuddies.jbox2d.callbacks.ParticleDestructionListener;
import net.diebuddies.jbox2d.callbacks.ParticleQueryCallback;
import net.diebuddies.jbox2d.callbacks.ParticleRaycastCallback;
import net.diebuddies.jbox2d.callbacks.QueryCallback;
import net.diebuddies.jbox2d.callbacks.RayCastCallback;
import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.RayCastInput;
import net.diebuddies.jbox2d.collision.TimeOfImpact;
import net.diebuddies.jbox2d.collision.broadphase.BroadPhase;
import net.diebuddies.jbox2d.collision.broadphase.BroadPhaseStrategy;
import net.diebuddies.jbox2d.collision.broadphase.DefaultBroadPhaseBuffer;
import net.diebuddies.jbox2d.collision.broadphase.DynamicTree;
import net.diebuddies.jbox2d.collision.shapes.ChainShape;
import net.diebuddies.jbox2d.collision.shapes.CircleShape;
import net.diebuddies.jbox2d.collision.shapes.EdgeShape;
import net.diebuddies.jbox2d.collision.shapes.PolygonShape;
import net.diebuddies.jbox2d.collision.shapes.Shape;
import net.diebuddies.jbox2d.collision.shapes.ShapeType;
import net.diebuddies.jbox2d.common.Color3f;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Sweep;
import net.diebuddies.jbox2d.common.Timer;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.BodyDef;
import net.diebuddies.jbox2d.dynamics.BodyType;
import net.diebuddies.jbox2d.dynamics.ContactManager;
import net.diebuddies.jbox2d.dynamics.Fixture;
import net.diebuddies.jbox2d.dynamics.FixtureProxy;
import net.diebuddies.jbox2d.dynamics.Island;
import net.diebuddies.jbox2d.dynamics.Profile;
import net.diebuddies.jbox2d.dynamics.TimeStep;
import net.diebuddies.jbox2d.dynamics.WorldQueryWrapper;
import net.diebuddies.jbox2d.dynamics.WorldRayCastWrapper;
import net.diebuddies.jbox2d.dynamics.contacts.Contact;
import net.diebuddies.jbox2d.dynamics.contacts.ContactEdge;
import net.diebuddies.jbox2d.dynamics.contacts.ContactRegister;
import net.diebuddies.jbox2d.dynamics.joints.Joint;
import net.diebuddies.jbox2d.dynamics.joints.JointDef;
import net.diebuddies.jbox2d.dynamics.joints.JointEdge;
import net.diebuddies.jbox2d.dynamics.joints.PulleyJoint;
import net.diebuddies.jbox2d.particle.ParticleBodyContact;
import net.diebuddies.jbox2d.particle.ParticleColor;
import net.diebuddies.jbox2d.particle.ParticleContact;
import net.diebuddies.jbox2d.particle.ParticleDef;
import net.diebuddies.jbox2d.particle.ParticleGroup;
import net.diebuddies.jbox2d.particle.ParticleGroupDef;
import net.diebuddies.jbox2d.particle.ParticleSystem;
import net.diebuddies.jbox2d.pooling.IDynamicStack;
import net.diebuddies.jbox2d.pooling.IWorldPool;
import net.diebuddies.jbox2d.pooling.arrays.Vec2Array;
import net.diebuddies.jbox2d.pooling.normal.DefaultWorldPool;

public class World {
    public static final int WORLD_POOL_SIZE = 5;
    public static final int WORLD_POOL_CONTAINER_SIZE = 10;
    public static final int NEW_FIXTURE = 1;
    public static final int LOCKED = 2;
    public static final int CLEAR_FORCES = 4;
    public int activeContacts = 0;
    public int contactPoolCount = 0;
    protected int m_flags;
    protected ContactManager m_contactManager;
    private Body m_bodyList;
    private Joint m_jointList;
    private int m_bodyCount;
    private int m_jointCount;
    private final Vec2 m_gravity = new Vec2();
    private boolean m_allowSleep;
    private DestructionListener m_destructionListener;
    private ParticleDestructionListener m_particleDestructionListener;
    private DebugDraw m_debugDraw;
    private final IWorldPool pool;
    private float m_inv_dt0;
    private boolean m_warmStarting;
    private boolean m_continuousPhysics;
    private boolean m_subStepping;
    private boolean m_stepComplete;
    private Profile m_profile;
    private ParticleSystem m_particleSystem;
    private ContactRegister[][] contactStacks = new ContactRegister[ShapeType.values().length][ShapeType.values().length];
    private final TimeStep step = new TimeStep();
    private final Timer stepTimer = new Timer();
    private final Timer tempTimer = new Timer();
    private final Color3f color = new Color3f();
    private final Transform xf = new Transform();
    private final Vec2 cA = new Vec2();
    private final Vec2 cB = new Vec2();
    private final Vec2Array avs = new Vec2Array();
    private final WorldQueryWrapper wqwrapper = new WorldQueryWrapper();
    private final WorldRayCastWrapper wrcwrapper = new WorldRayCastWrapper();
    private final RayCastInput input = new RayCastInput();
    private final Island island = new Island();
    private Body[] stack = new Body[10];
    private final Timer broadphaseTimer = new Timer();
    private final Island toiIsland = new Island();
    private final TimeOfImpact.TOIInput toiInput = new TimeOfImpact.TOIInput();
    private final TimeOfImpact.TOIOutput toiOutput = new TimeOfImpact.TOIOutput();
    private final TimeStep subStep = new TimeStep();
    private final Body[] tempBodies = new Body[2];
    private final Sweep backup1 = new Sweep();
    private final Sweep backup2 = new Sweep();
    private static Integer LIQUID_INT = 1234598372;
    private float liquidLength = 0.12f;
    private float averageLinearVel = -1.0f;
    private final Vec2 liquidOffset = new Vec2();
    private final Vec2 circCenterMoved = new Vec2();
    private final Color3f liquidColor = new Color3f(0.4f, 0.4f, 1.0f);
    private final Vec2 center = new Vec2();
    private final Vec2 axis = new Vec2();
    private final Vec2 v1 = new Vec2();
    private final Vec2 v2 = new Vec2();
    private final Vec2Array tlvertices = new Vec2Array();

    public World(Vec2 gravity) {
        this(gravity, new DefaultWorldPool(5, 10));
    }

    public World(Vec2 gravity, IWorldPool pool) {
        this(gravity, pool, new DynamicTree());
    }

    public World(Vec2 gravity, IWorldPool pool, BroadPhaseStrategy strategy) {
        this(gravity, pool, new DefaultBroadPhaseBuffer(strategy));
    }

    public World(Vec2 gravity, IWorldPool pool, BroadPhase broadPhase) {
        this.pool = pool;
        this.m_destructionListener = null;
        this.m_debugDraw = null;
        this.m_bodyList = null;
        this.m_jointList = null;
        this.m_bodyCount = 0;
        this.m_jointCount = 0;
        this.m_warmStarting = true;
        this.m_continuousPhysics = true;
        this.m_subStepping = false;
        this.m_stepComplete = true;
        this.m_allowSleep = true;
        this.m_gravity.set(gravity);
        this.m_flags = 4;
        this.m_inv_dt0 = 0.0f;
        this.m_contactManager = new ContactManager(this, broadPhase);
        this.m_profile = new Profile();
        this.m_particleSystem = new ParticleSystem(this);
        this.initializeRegisters();
    }

    public void setAllowSleep(boolean flag) {
        if (flag == this.m_allowSleep) {
            return;
        }
        this.m_allowSleep = flag;
        if (!this.m_allowSleep) {
            Body b = this.m_bodyList;
            while (b != null) {
                b.setAwake(true);
                b = b.m_next;
            }
        }
    }

    public void setSubStepping(boolean subStepping) {
        this.m_subStepping = subStepping;
    }

    public boolean isSubStepping() {
        return this.m_subStepping;
    }

    public boolean isAllowSleep() {
        return this.m_allowSleep;
    }

    private void addType(IDynamicStack<Contact> creator, ShapeType type1, ShapeType type2) {
        ContactRegister register = new ContactRegister();
        register.creator = creator;
        register.primary = true;
        this.contactStacks[type1.ordinal()][type2.ordinal()] = register;
        if (type1 != type2) {
            ContactRegister register2 = new ContactRegister();
            register2.creator = creator;
            register2.primary = false;
            this.contactStacks[type2.ordinal()][type1.ordinal()] = register2;
        }
    }

    private void initializeRegisters() {
        this.addType(this.pool.getCircleContactStack(), ShapeType.CIRCLE, ShapeType.CIRCLE);
        this.addType(this.pool.getPolyCircleContactStack(), ShapeType.POLYGON, ShapeType.CIRCLE);
        this.addType(this.pool.getPolyContactStack(), ShapeType.POLYGON, ShapeType.POLYGON);
        this.addType(this.pool.getEdgeCircleContactStack(), ShapeType.EDGE, ShapeType.CIRCLE);
        this.addType(this.pool.getEdgePolyContactStack(), ShapeType.EDGE, ShapeType.POLYGON);
        this.addType(this.pool.getChainCircleContactStack(), ShapeType.CHAIN, ShapeType.CIRCLE);
        this.addType(this.pool.getChainPolyContactStack(), ShapeType.CHAIN, ShapeType.POLYGON);
    }

    public DestructionListener getDestructionListener() {
        return this.m_destructionListener;
    }

    public ParticleDestructionListener getParticleDestructionListener() {
        return this.m_particleDestructionListener;
    }

    public void setParticleDestructionListener(ParticleDestructionListener listener) {
        this.m_particleDestructionListener = listener;
    }

    public Contact popContact(Fixture fixtureA, int indexA, Fixture fixtureB, int indexB) {
        ShapeType type1 = fixtureA.getType();
        ShapeType type2 = fixtureB.getType();
        ContactRegister reg = this.contactStacks[type1.ordinal()][type2.ordinal()];
        if (reg != null) {
            if (reg.primary) {
                Contact c = reg.creator.pop();
                c.init(fixtureA, indexA, fixtureB, indexB);
                return c;
            }
            Contact c = reg.creator.pop();
            c.init(fixtureB, indexB, fixtureA, indexA);
            return c;
        }
        return null;
    }

    public void pushContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (contact.m_manifold.pointCount > 0 && !fixtureA.isSensor() && !fixtureB.isSensor()) {
            fixtureA.getBody().setAwake(true);
            fixtureB.getBody().setAwake(true);
        }
        ShapeType type1 = fixtureA.getType();
        ShapeType type2 = fixtureB.getType();
        IDynamicStack<Contact> creator = this.contactStacks[type1.ordinal()][type2.ordinal()].creator;
        creator.push(contact);
    }

    public IWorldPool getPool() {
        return this.pool;
    }

    public void setDestructionListener(DestructionListener listener) {
        this.m_destructionListener = listener;
    }

    public void setContactFilter(ContactFilter filter) {
        this.m_contactManager.m_contactFilter = filter;
    }

    public void setContactListener(ContactListener listener) {
        this.m_contactManager.m_contactListener = listener;
    }

    public void setDebugDraw(DebugDraw debugDraw) {
        this.m_debugDraw = debugDraw;
    }

    public Body createBody(BodyDef def) {
        assert (!this.isLocked());
        if (this.isLocked()) {
            return null;
        }
        Body b = new Body(def, this);
        b.m_prev = null;
        b.m_next = this.m_bodyList;
        if (this.m_bodyList != null) {
            this.m_bodyList.m_prev = b;
        }
        this.m_bodyList = b;
        ++this.m_bodyCount;
        return b;
    }

    public void destroyBody(Body body) {
        assert (this.m_bodyCount > 0);
        assert (!this.isLocked());
        if (this.isLocked()) {
            return;
        }
        JointEdge je = body.m_jointList;
        while (je != null) {
            JointEdge je0 = je;
            je = je.next;
            if (this.m_destructionListener != null) {
                this.m_destructionListener.sayGoodbye(je0.joint);
            }
            this.destroyJoint(je0.joint);
            body.m_jointList = je;
        }
        body.m_jointList = null;
        ContactEdge ce = body.m_contactList;
        while (ce != null) {
            ContactEdge ce0 = ce;
            ce = ce.next;
            this.m_contactManager.destroy(ce0.contact);
        }
        body.m_contactList = null;
        Fixture f = body.m_fixtureList;
        while (f != null) {
            Fixture f0 = f;
            f = f.m_next;
            if (this.m_destructionListener != null) {
                this.m_destructionListener.sayGoodbye(f0);
            }
            f0.destroyProxies(this.m_contactManager.m_broadPhase);
            f0.destroy();
            body.m_fixtureList = f;
            --body.m_fixtureCount;
        }
        body.m_fixtureList = null;
        body.m_fixtureCount = 0;
        if (body.m_prev != null) {
            body.m_prev.m_next = body.m_next;
        }
        if (body.m_next != null) {
            body.m_next.m_prev = body.m_prev;
        }
        if (body == this.m_bodyList) {
            this.m_bodyList = body.m_next;
        }
        --this.m_bodyCount;
    }

    public Joint createJoint(JointDef def) {
        assert (!this.isLocked());
        if (this.isLocked()) {
            return null;
        }
        Joint j = Joint.create(this, def);
        j.m_prev = null;
        j.m_next = this.m_jointList;
        if (this.m_jointList != null) {
            this.m_jointList.m_prev = j;
        }
        this.m_jointList = j;
        ++this.m_jointCount;
        j.m_edgeA.joint = j;
        j.m_edgeA.other = j.getBodyB();
        j.m_edgeA.prev = null;
        j.m_edgeA.next = j.getBodyA().m_jointList;
        if (j.getBodyA().m_jointList != null) {
            j.getBodyA().m_jointList.prev = j.m_edgeA;
        }
        j.getBodyA().m_jointList = j.m_edgeA;
        j.m_edgeB.joint = j;
        j.m_edgeB.other = j.getBodyA();
        j.m_edgeB.prev = null;
        j.m_edgeB.next = j.getBodyB().m_jointList;
        if (j.getBodyB().m_jointList != null) {
            j.getBodyB().m_jointList.prev = j.m_edgeB;
        }
        j.getBodyB().m_jointList = j.m_edgeB;
        Body bodyA = def.bodyA;
        Body bodyB = def.bodyB;
        if (!def.collideConnected) {
            ContactEdge edge = bodyB.getContactList();
            while (edge != null) {
                if (edge.other == bodyA) {
                    edge.contact.flagForFiltering();
                }
                edge = edge.next;
            }
        }
        return j;
    }

    public void destroyJoint(Joint j) {
        assert (!this.isLocked());
        if (this.isLocked()) {
            return;
        }
        boolean collideConnected = j.getCollideConnected();
        if (j.m_prev != null) {
            j.m_prev.m_next = j.m_next;
        }
        if (j.m_next != null) {
            j.m_next.m_prev = j.m_prev;
        }
        if (j == this.m_jointList) {
            this.m_jointList = j.m_next;
        }
        Body bodyA = j.getBodyA();
        Body bodyB = j.getBodyB();
        bodyA.setAwake(true);
        bodyB.setAwake(true);
        if (j.m_edgeA.prev != null) {
            j.m_edgeA.prev.next = j.m_edgeA.next;
        }
        if (j.m_edgeA.next != null) {
            j.m_edgeA.next.prev = j.m_edgeA.prev;
        }
        if (j.m_edgeA == bodyA.m_jointList) {
            bodyA.m_jointList = j.m_edgeA.next;
        }
        j.m_edgeA.prev = null;
        j.m_edgeA.next = null;
        if (j.m_edgeB.prev != null) {
            j.m_edgeB.prev.next = j.m_edgeB.next;
        }
        if (j.m_edgeB.next != null) {
            j.m_edgeB.next.prev = j.m_edgeB.prev;
        }
        if (j.m_edgeB == bodyB.m_jointList) {
            bodyB.m_jointList = j.m_edgeB.next;
        }
        j.m_edgeB.prev = null;
        j.m_edgeB.next = null;
        Joint.destroy(j);
        assert (this.m_jointCount > 0);
        --this.m_jointCount;
        if (!collideConnected) {
            ContactEdge edge = bodyB.getContactList();
            while (edge != null) {
                if (edge.other == bodyA) {
                    edge.contact.flagForFiltering();
                }
                edge = edge.next;
            }
        }
    }

    public void step(float dt, int velocityIterations, int positionIterations) {
        this.stepTimer.reset();
        this.tempTimer.reset();
        if ((this.m_flags & 1) == 1) {
            this.m_contactManager.findNewContacts();
            this.m_flags &= 0xFFFFFFFE;
        }
        this.m_flags |= 2;
        this.step.dt = dt;
        this.step.velocityIterations = velocityIterations;
        this.step.positionIterations = positionIterations;
        this.step.inv_dt = dt > 0.0f ? 1.0f / dt : 0.0f;
        this.step.dtRatio = this.m_inv_dt0 * dt;
        this.step.warmStarting = this.m_warmStarting;
        this.m_profile.stepInit.record(this.tempTimer.getMilliseconds());
        this.tempTimer.reset();
        this.m_contactManager.collide();
        this.m_profile.collide.record(this.tempTimer.getMilliseconds());
        if (this.m_stepComplete && this.step.dt > 0.0f) {
            this.tempTimer.reset();
            this.m_particleSystem.solve(this.step);
            this.m_profile.solveParticleSystem.record(this.tempTimer.getMilliseconds());
            this.tempTimer.reset();
            this.solve(this.step);
            this.m_profile.solve.record(this.tempTimer.getMilliseconds());
        }
        if (this.m_continuousPhysics && this.step.dt > 0.0f) {
            this.tempTimer.reset();
            this.solveTOI(this.step);
            this.m_profile.solveTOI.record(this.tempTimer.getMilliseconds());
        }
        if (this.step.dt > 0.0f) {
            this.m_inv_dt0 = this.step.inv_dt;
        }
        if ((this.m_flags & 4) == 4) {
            this.clearForces();
        }
        this.m_flags &= 0xFFFFFFFD;
        this.m_profile.step.record(this.stepTimer.getMilliseconds());
    }

    public void clearForces() {
        for (Body body = this.m_bodyList; body != null; body = body.getNext()) {
            body.m_force.setZero();
            body.m_torque = 0.0f;
        }
    }

    public void drawDebugData() {
        Fixture f;
        Body b;
        boolean wireframe;
        if (this.m_debugDraw == null) {
            return;
        }
        int flags = this.m_debugDraw.getFlags();
        boolean bl = wireframe = (flags & 0x80) != 0;
        if ((flags & 2) != 0) {
            for (b = this.m_bodyList; b != null; b = b.getNext()) {
                this.xf.set(b.getTransform());
                for (f = b.getFixtureList(); f != null; f = f.getNext()) {
                    if (!b.isActive()) {
                        this.color.set(0.5f, 0.5f, 0.3f);
                        this.drawShape(f, this.xf, this.color, wireframe);
                        continue;
                    }
                    if (b.getType() == BodyType.STATIC) {
                        this.color.set(0.5f, 0.9f, 0.3f);
                        this.drawShape(f, this.xf, this.color, wireframe);
                        continue;
                    }
                    if (b.getType() == BodyType.KINEMATIC) {
                        this.color.set(0.5f, 0.5f, 0.9f);
                        this.drawShape(f, this.xf, this.color, wireframe);
                        continue;
                    }
                    if (!b.isAwake()) {
                        this.color.set(0.5f, 0.5f, 0.5f);
                        this.drawShape(f, this.xf, this.color, wireframe);
                        continue;
                    }
                    this.color.set(0.9f, 0.7f, 0.7f);
                    this.drawShape(f, this.xf, this.color, wireframe);
                }
            }
            this.drawParticleSystem(this.m_particleSystem);
        }
        if ((flags & 4) != 0) {
            for (Joint j = this.m_jointList; j != null; j = j.getNext()) {
                this.drawJoint(j);
            }
        }
        if ((flags & 0x10) != 0) {
            this.color.set(0.3f, 0.9f, 0.9f);
            for (Contact c = this.m_contactManager.m_contactList; c != null; c = c.getNext()) {
                Fixture fixtureA = c.getFixtureA();
                Fixture fixtureB = c.getFixtureB();
                fixtureA.getAABB(c.getChildIndexA()).getCenterToOut(this.cA);
                fixtureB.getAABB(c.getChildIndexB()).getCenterToOut(this.cB);
                this.m_debugDraw.drawSegment(this.cA, this.cB, this.color);
            }
        }
        if ((flags & 8) != 0) {
            this.color.set(0.9f, 0.3f, 0.9f);
            for (b = this.m_bodyList; b != null; b = b.getNext()) {
                if (!b.isActive()) continue;
                for (f = b.getFixtureList(); f != null; f = f.getNext()) {
                    for (int i = 0; i < f.m_proxyCount; ++i) {
                        FixtureProxy proxy = f.m_proxies[i];
                        AABB aabb = this.m_contactManager.m_broadPhase.getFatAABB(proxy.proxyId);
                        if (aabb == null) continue;
                        Vec2[] vs = this.avs.get(4);
                        vs[0].set(aabb.lowerBound.x, aabb.lowerBound.y);
                        vs[1].set(aabb.upperBound.x, aabb.lowerBound.y);
                        vs[2].set(aabb.upperBound.x, aabb.upperBound.y);
                        vs[3].set(aabb.lowerBound.x, aabb.upperBound.y);
                        this.m_debugDraw.drawPolygon(vs, 4, this.color);
                    }
                }
            }
        }
        if ((flags & 0x20) != 0) {
            for (b = this.m_bodyList; b != null; b = b.getNext()) {
                this.xf.set(b.getTransform());
                this.xf.p.set(b.getWorldCenter());
                this.m_debugDraw.drawTransform(this.xf);
            }
        }
        if ((flags & 0x40) != 0) {
            this.m_contactManager.m_broadPhase.drawTree(this.m_debugDraw);
        }
        this.m_debugDraw.flush();
    }

    public void queryAABB(QueryCallback callback, AABB aabb) {
        this.wqwrapper.broadPhase = this.m_contactManager.m_broadPhase;
        this.wqwrapper.callback = callback;
        this.m_contactManager.m_broadPhase.query(this.wqwrapper, aabb);
    }

    public void queryAABB(QueryCallback callback, ParticleQueryCallback particleCallback, AABB aabb) {
        this.wqwrapper.broadPhase = this.m_contactManager.m_broadPhase;
        this.wqwrapper.callback = callback;
        this.m_contactManager.m_broadPhase.query(this.wqwrapper, aabb);
        this.m_particleSystem.queryAABB(particleCallback, aabb);
    }

    public void queryAABB(ParticleQueryCallback particleCallback, AABB aabb) {
        this.m_particleSystem.queryAABB(particleCallback, aabb);
    }

    public void raycast(RayCastCallback callback, Vec2 point1, Vec2 point2) {
        this.wrcwrapper.broadPhase = this.m_contactManager.m_broadPhase;
        this.wrcwrapper.callback = callback;
        this.input.maxFraction = 1.0f;
        this.input.p1.set(point1);
        this.input.p2.set(point2);
        this.m_contactManager.m_broadPhase.raycast(this.wrcwrapper, this.input);
    }

    public void raycast(RayCastCallback callback, ParticleRaycastCallback particleCallback, Vec2 point1, Vec2 point2) {
        this.wrcwrapper.broadPhase = this.m_contactManager.m_broadPhase;
        this.wrcwrapper.callback = callback;
        this.input.maxFraction = 1.0f;
        this.input.p1.set(point1);
        this.input.p2.set(point2);
        this.m_contactManager.m_broadPhase.raycast(this.wrcwrapper, this.input);
        this.m_particleSystem.raycast(particleCallback, point1, point2);
    }

    public void raycast(ParticleRaycastCallback particleCallback, Vec2 point1, Vec2 point2) {
        this.m_particleSystem.raycast(particleCallback, point1, point2);
    }

    public Body getBodyList() {
        return this.m_bodyList;
    }

    public Joint getJointList() {
        return this.m_jointList;
    }

    public Contact getContactList() {
        return this.m_contactManager.m_contactList;
    }

    public boolean isSleepingAllowed() {
        return this.m_allowSleep;
    }

    public void setSleepingAllowed(boolean sleepingAllowed) {
        this.m_allowSleep = sleepingAllowed;
    }

    public void setWarmStarting(boolean flag) {
        this.m_warmStarting = flag;
    }

    public boolean isWarmStarting() {
        return this.m_warmStarting;
    }

    public void setContinuousPhysics(boolean flag) {
        this.m_continuousPhysics = flag;
    }

    public boolean isContinuousPhysics() {
        return this.m_continuousPhysics;
    }

    public int getProxyCount() {
        return this.m_contactManager.m_broadPhase.getProxyCount();
    }

    public int getBodyCount() {
        return this.m_bodyCount;
    }

    public int getJointCount() {
        return this.m_jointCount;
    }

    public int getContactCount() {
        return this.m_contactManager.m_contactCount;
    }

    public int getTreeHeight() {
        return this.m_contactManager.m_broadPhase.getTreeHeight();
    }

    public int getTreeBalance() {
        return this.m_contactManager.m_broadPhase.getTreeBalance();
    }

    public float getTreeQuality() {
        return this.m_contactManager.m_broadPhase.getTreeQuality();
    }

    public void setGravity(Vec2 gravity) {
        this.m_gravity.set(gravity);
    }

    public Vec2 getGravity() {
        return this.m_gravity;
    }

    public boolean isLocked() {
        return (this.m_flags & 2) == 2;
    }

    public void setAutoClearForces(boolean flag) {
        this.m_flags = flag ? (this.m_flags |= 4) : (this.m_flags &= 0xFFFFFFFB);
    }

    public boolean getAutoClearForces() {
        return (this.m_flags & 4) == 4;
    }

    public ContactManager getContactManager() {
        return this.m_contactManager;
    }

    public Profile getProfile() {
        return this.m_profile;
    }

    private void solve(TimeStep step) {
        this.m_profile.solveInit.startAccum();
        this.m_profile.solveVelocity.startAccum();
        this.m_profile.solvePosition.startAccum();
        Body b = this.m_bodyList;
        while (b != null) {
            b.m_xf0.set(b.m_xf);
            b = b.m_next;
        }
        this.island.init(this.m_bodyCount, this.m_contactManager.m_contactCount, this.m_jointCount, this.m_contactManager.m_contactListener);
        b = this.m_bodyList;
        while (b != null) {
            b.m_flags &= 0xFFFFFFFE;
            b = b.m_next;
        }
        Contact c = this.m_contactManager.m_contactList;
        while (c != null) {
            c.m_flags &= 0xFFFFFFFE;
            c = c.m_next;
        }
        Joint j = this.m_jointList;
        while (j != null) {
            j.m_islandFlag = false;
            j = j.m_next;
        }
        int stackSize = this.m_bodyCount;
        if (this.stack.length < stackSize) {
            this.stack = new Body[stackSize];
        }
        Body seed = this.m_bodyList;
        while (seed != null) {
            if ((seed.m_flags & 1) != 1 && seed.isAwake() && seed.isActive() && seed.getType() != BodyType.STATIC) {
                this.island.clear();
                int stackCount = 0;
                this.stack[stackCount++] = seed;
                seed.m_flags |= 1;
                while (stackCount > 0) {
                    Body b2 = this.stack[--stackCount];
                    assert (b2.isActive());
                    this.island.add(b2);
                    b2.setAwake(true);
                    if (b2.getType() == BodyType.STATIC) continue;
                    ContactEdge ce = b2.m_contactList;
                    while (ce != null) {
                        Contact contact = ce.contact;
                        if ((contact.m_flags & 1) != 1 && contact.isEnabled() && contact.isTouching()) {
                            boolean sensorA = contact.m_fixtureA.m_isSensor;
                            boolean sensorB = contact.m_fixtureB.m_isSensor;
                            if (!sensorA && !sensorB) {
                                this.island.add(contact);
                                contact.m_flags |= 1;
                                Body other = ce.other;
                                if ((other.m_flags & 1) != 1) {
                                    assert (stackCount < stackSize);
                                    this.stack[stackCount++] = other;
                                    other.m_flags |= 1;
                                }
                            }
                        }
                        ce = ce.next;
                    }
                    JointEdge je = b2.m_jointList;
                    while (je != null) {
                        Body other;
                        if (!je.joint.m_islandFlag && (other = je.other).isActive()) {
                            this.island.add(je.joint);
                            je.joint.m_islandFlag = true;
                            if ((other.m_flags & 1) != 1) {
                                assert (stackCount < stackSize);
                                this.stack[stackCount++] = other;
                                other.m_flags |= 1;
                            }
                        }
                        je = je.next;
                    }
                }
                this.island.solve(this.m_profile, step, this.m_gravity, this.m_allowSleep);
                for (int i = 0; i < this.island.m_bodyCount; ++i) {
                    Body b3 = this.island.m_bodies[i];
                    if (b3.getType() != BodyType.STATIC) continue;
                    b3.m_flags &= 0xFFFFFFFE;
                }
            }
            seed = seed.m_next;
        }
        this.m_profile.solveInit.endAccum();
        this.m_profile.solveVelocity.endAccum();
        this.m_profile.solvePosition.endAccum();
        this.broadphaseTimer.reset();
        for (Body b4 = this.m_bodyList; b4 != null; b4 = b4.getNext()) {
            if ((b4.m_flags & 1) == 0 || b4.getType() == BodyType.STATIC) continue;
            b4.synchronizeFixtures();
        }
        this.m_contactManager.findNewContacts();
        this.m_profile.broadphase.record(this.broadphaseTimer.getMilliseconds());
    }

    private void solveTOI(TimeStep step) {
        block31: {
            Island island = this.toiIsland;
            island.init(2 * Settings.maxTOIContacts, Settings.maxTOIContacts, 0, this.m_contactManager.m_contactListener);
            if (this.m_stepComplete) {
                Body b = this.m_bodyList;
                while (b != null) {
                    b.m_flags &= 0xFFFFFFFE;
                    b.m_sweep.alpha0 = 0.0f;
                    b = b.m_next;
                }
                Contact c = this.m_contactManager.m_contactList;
                while (c != null) {
                    c.m_flags &= 0xFFFFFFDE;
                    c.m_toiCount = 0.0f;
                    c.m_toi = 1.0f;
                    c = c.m_next;
                }
            }
            while (true) {
                ContactEdge ce;
                Body body;
                int i;
                Contact minContact = null;
                float minAlpha = 1.0f;
                Contact c = this.m_contactManager.m_contactList;
                while (c != null) {
                    block32: {
                        float alpha;
                        block34: {
                            boolean collideB;
                            boolean activeB;
                            block33: {
                                if (!c.isEnabled() || c.m_toiCount > (float)Settings.maxSubSteps) break block32;
                                alpha = 1.0f;
                                if ((c.m_flags & 0x20) == 0) break block33;
                                alpha = c.m_toi;
                                break block34;
                            }
                            Fixture fA = c.getFixtureA();
                            Fixture fB = c.getFixtureB();
                            if (fA.isSensor() || fB.isSensor()) break block32;
                            Body bA = fA.getBody();
                            Body bB = fB.getBody();
                            BodyType typeA = bA.m_type;
                            BodyType typeB = bB.m_type;
                            assert (typeA == BodyType.DYNAMIC || typeB == BodyType.DYNAMIC);
                            boolean activeA = bA.isAwake() && typeA != BodyType.STATIC;
                            boolean bl = activeB = bB.isAwake() && typeB != BodyType.STATIC;
                            if (!activeA && !activeB) break block32;
                            boolean collideA = bA.isBullet() || typeA != BodyType.DYNAMIC;
                            boolean bl2 = collideB = bB.isBullet() || typeB != BodyType.DYNAMIC;
                            if (!collideA && !collideB) break block32;
                            float alpha0 = bA.m_sweep.alpha0;
                            if (bA.m_sweep.alpha0 < bB.m_sweep.alpha0) {
                                alpha0 = bB.m_sweep.alpha0;
                                bA.m_sweep.advance(alpha0);
                            } else if (bB.m_sweep.alpha0 < bA.m_sweep.alpha0) {
                                alpha0 = bA.m_sweep.alpha0;
                                bB.m_sweep.advance(alpha0);
                            }
                            assert (alpha0 < 1.0f);
                            int indexA = c.getChildIndexA();
                            int indexB = c.getChildIndexB();
                            TimeOfImpact.TOIInput input = this.toiInput;
                            input.proxyA.set(fA.getShape(), indexA);
                            input.proxyB.set(fB.getShape(), indexB);
                            input.sweepA.set(bA.m_sweep);
                            input.sweepB.set(bB.m_sweep);
                            input.tMax = 1.0f;
                            this.pool.getTimeOfImpact().timeOfImpact(this.toiOutput, input);
                            float beta = this.toiOutput.t;
                            alpha = this.toiOutput.state == TimeOfImpact.TOIOutputState.TOUCHING ? MathUtils.min(alpha0 + (1.0f - alpha0) * beta, 1.0f) : 1.0f;
                            c.m_toi = alpha;
                            c.m_flags |= 0x20;
                        }
                        if (alpha < minAlpha) {
                            minContact = c;
                            minAlpha = alpha;
                        }
                    }
                    c = c.m_next;
                }
                if (minContact == null || 0.9999988f < minAlpha) {
                    this.m_stepComplete = true;
                    break block31;
                }
                Fixture fA = minContact.getFixtureA();
                Fixture fB = minContact.getFixtureB();
                Body bA = fA.getBody();
                Body bB = fB.getBody();
                this.backup1.set(bA.m_sweep);
                this.backup2.set(bB.m_sweep);
                bA.advance(minAlpha);
                bB.advance(minAlpha);
                minContact.update(this.m_contactManager.m_contactListener);
                minContact.m_flags &= 0xFFFFFFDF;
                minContact.m_toiCount += 1.0f;
                if (!minContact.isEnabled() || !minContact.isTouching()) {
                    minContact.setEnabled(false);
                    bA.m_sweep.set(this.backup1);
                    bB.m_sweep.set(this.backup2);
                    bA.synchronizeTransform();
                    bB.synchronizeTransform();
                    continue;
                }
                bA.setAwake(true);
                bB.setAwake(true);
                island.clear();
                island.add(bA);
                island.add(bB);
                island.add(minContact);
                bA.m_flags |= 1;
                bB.m_flags |= 1;
                minContact.m_flags |= 1;
                this.tempBodies[0] = bA;
                this.tempBodies[1] = bB;
                for (i = 0; i < 2; ++i) {
                    body = this.tempBodies[i];
                    if (body.m_type != BodyType.DYNAMIC) continue;
                    ce = body.m_contactList;
                    while (ce != null && island.m_bodyCount != island.m_bodyCapacity && island.m_contactCount != island.m_contactCapacity) {
                        Contact contact = ce.contact;
                        if ((contact.m_flags & 1) == 0) {
                            Body other = ce.other;
                            if (other.m_type != BodyType.DYNAMIC || body.isBullet() || other.isBullet()) {
                                boolean sensorA = contact.m_fixtureA.m_isSensor;
                                boolean sensorB = contact.m_fixtureB.m_isSensor;
                                if (!sensorA && !sensorB) {
                                    this.backup1.set(other.m_sweep);
                                    if ((other.m_flags & 1) == 0) {
                                        other.advance(minAlpha);
                                    }
                                    contact.update(this.m_contactManager.m_contactListener);
                                    if (!contact.isEnabled()) {
                                        other.m_sweep.set(this.backup1);
                                        other.synchronizeTransform();
                                    } else if (!contact.isTouching()) {
                                        other.m_sweep.set(this.backup1);
                                        other.synchronizeTransform();
                                    } else {
                                        contact.m_flags |= 1;
                                        island.add(contact);
                                        if ((other.m_flags & 1) == 0) {
                                            other.m_flags |= 1;
                                            if (other.m_type != BodyType.STATIC) {
                                                other.setAwake(true);
                                            }
                                            island.add(other);
                                        }
                                    }
                                }
                            }
                        }
                        ce = ce.next;
                    }
                }
                this.subStep.dt = (1.0f - minAlpha) * step.dt;
                this.subStep.inv_dt = 1.0f / this.subStep.dt;
                this.subStep.dtRatio = 1.0f;
                this.subStep.positionIterations = 20;
                this.subStep.velocityIterations = step.velocityIterations;
                this.subStep.warmStarting = false;
                island.solveTOI(this.subStep, bA.m_islandIndex, bB.m_islandIndex);
                for (i = 0; i < island.m_bodyCount; ++i) {
                    body = island.m_bodies[i];
                    body.m_flags &= 0xFFFFFFFE;
                    if (body.m_type != BodyType.DYNAMIC) continue;
                    body.synchronizeFixtures();
                    ce = body.m_contactList;
                    while (ce != null) {
                        ce.contact.m_flags &= 0xFFFFFFDE;
                        ce = ce.next;
                    }
                }
                this.m_contactManager.findNewContacts();
                if (this.m_subStepping) break;
            }
            this.m_stepComplete = false;
        }
    }

    private void drawJoint(Joint joint) {
        Body bodyA = joint.getBodyA();
        Body bodyB = joint.getBodyB();
        Transform xf1 = bodyA.getTransform();
        Transform xf2 = bodyB.getTransform();
        Vec2 x1 = xf1.p;
        Vec2 x2 = xf2.p;
        Vec2 p1 = this.pool.popVec2();
        Vec2 p2 = this.pool.popVec2();
        joint.getAnchorA(p1);
        joint.getAnchorB(p2);
        this.color.set(0.5f, 0.8f, 0.8f);
        switch (joint.getType()) {
            case DISTANCE: {
                this.m_debugDraw.drawSegment(p1, p2, this.color);
                break;
            }
            case PULLEY: {
                PulleyJoint pulley = (PulleyJoint)joint;
                Vec2 s1 = pulley.getGroundAnchorA();
                Vec2 s2 = pulley.getGroundAnchorB();
                this.m_debugDraw.drawSegment(s1, p1, this.color);
                this.m_debugDraw.drawSegment(s2, p2, this.color);
                this.m_debugDraw.drawSegment(s1, s2, this.color);
                break;
            }
            case CONSTANT_VOLUME: 
            case MOUSE: {
                break;
            }
            default: {
                this.m_debugDraw.drawSegment(x1, p1, this.color);
                this.m_debugDraw.drawSegment(p1, p2, this.color);
                this.m_debugDraw.drawSegment(x2, p2, this.color);
            }
        }
        this.pool.pushVec2(2);
    }

    private void drawShape(Fixture fixture, Transform xf, Color3f color, boolean wireframe) {
        switch (fixture.getType()) {
            case CIRCLE: {
                CircleShape circle = (CircleShape)fixture.getShape();
                Transform.mulToOutUnsafe(xf, circle.m_p, this.center);
                float radius = circle.m_radius;
                xf.q.getXAxis(this.axis);
                if (fixture.getUserData() != null && fixture.getUserData().equals(LIQUID_INT)) {
                    Body b = fixture.getBody();
                    this.liquidOffset.set(b.m_linearVelocity);
                    float linVelLength = b.m_linearVelocity.length();
                    this.averageLinearVel = this.averageLinearVel == -1.0f ? linVelLength : 0.98f * this.averageLinearVel + 0.02f * linVelLength;
                    this.liquidOffset.mulLocal(this.liquidLength / this.averageLinearVel / 2.0f);
                    this.circCenterMoved.set(this.center).addLocal(this.liquidOffset);
                    this.center.subLocal(this.liquidOffset);
                    this.m_debugDraw.drawSegment(this.center, this.circCenterMoved, this.liquidColor);
                    return;
                }
                if (wireframe) {
                    this.m_debugDraw.drawCircle(this.center, radius, this.axis, color);
                    break;
                }
                this.m_debugDraw.drawSolidCircle(this.center, radius, this.axis, color);
                break;
            }
            case POLYGON: {
                PolygonShape poly = (PolygonShape)fixture.getShape();
                int vertexCount = poly.m_count;
                assert (vertexCount <= Settings.maxPolygonVertices);
                Vec2[] vertices = this.tlvertices.get(Settings.maxPolygonVertices);
                for (int i = 0; i < vertexCount; ++i) {
                    Transform.mulToOutUnsafe(xf, poly.m_vertices[i], vertices[i]);
                }
                if (wireframe) {
                    this.m_debugDraw.drawPolygon(vertices, vertexCount, color);
                    break;
                }
                this.m_debugDraw.drawSolidPolygon(vertices, vertexCount, color);
                break;
            }
            case EDGE: {
                EdgeShape edge = (EdgeShape)fixture.getShape();
                Transform.mulToOutUnsafe(xf, edge.m_vertex1, this.v1);
                Transform.mulToOutUnsafe(xf, edge.m_vertex2, this.v2);
                this.m_debugDraw.drawSegment(this.v1, this.v2, color);
                break;
            }
            case CHAIN: {
                ChainShape chain = (ChainShape)fixture.getShape();
                int count = chain.m_count;
                Vec2[] vertices = chain.m_vertices;
                Transform.mulToOutUnsafe(xf, vertices[0], this.v1);
                for (int i = 1; i < count; ++i) {
                    Transform.mulToOutUnsafe(xf, vertices[i], this.v2);
                    this.m_debugDraw.drawSegment(this.v1, this.v2, color);
                    this.m_debugDraw.drawCircle(this.v1, 0.05f, color);
                    this.v1.set(this.v2);
                }
                break;
            }
        }
    }

    private void drawParticleSystem(ParticleSystem system) {
        boolean wireframe = (this.m_debugDraw.getFlags() & 0x80) != 0;
        int particleCount = system.getParticleCount();
        if (particleCount != 0) {
            float particleRadius = system.getParticleRadius();
            Vec2[] positionBuffer = system.getParticlePositionBuffer();
            ParticleColor[] colorBuffer = null;
            if (system.m_colorBuffer.data != null) {
                colorBuffer = system.getParticleColorBuffer();
            }
            if (wireframe) {
                this.m_debugDraw.drawParticlesWireframe(positionBuffer, particleRadius, colorBuffer, particleCount);
            } else {
                this.m_debugDraw.drawParticles(positionBuffer, particleRadius, colorBuffer, particleCount);
            }
        }
    }

    public int createParticle(ParticleDef def) {
        assert (!this.isLocked());
        if (this.isLocked()) {
            return 0;
        }
        int p = this.m_particleSystem.createParticle(def);
        return p;
    }

    public void destroyParticle(int index) {
        this.destroyParticle(index, false);
    }

    public void destroyParticle(int index, boolean callDestructionListener) {
        this.m_particleSystem.destroyParticle(index, callDestructionListener);
    }

    public int destroyParticlesInShape(Shape shape, Transform xf) {
        return this.destroyParticlesInShape(shape, xf, false);
    }

    public int destroyParticlesInShape(Shape shape, Transform xf, boolean callDestructionListener) {
        assert (!this.isLocked());
        if (this.isLocked()) {
            return 0;
        }
        return this.m_particleSystem.destroyParticlesInShape(shape, xf, callDestructionListener);
    }

    public ParticleGroup createParticleGroup(ParticleGroupDef def) {
        assert (!this.isLocked());
        if (this.isLocked()) {
            return null;
        }
        ParticleGroup g = this.m_particleSystem.createParticleGroup(def);
        return g;
    }

    public void joinParticleGroups(ParticleGroup groupA, ParticleGroup groupB) {
        assert (!this.isLocked());
        if (this.isLocked()) {
            return;
        }
        this.m_particleSystem.joinParticleGroups(groupA, groupB);
    }

    public void destroyParticlesInGroup(ParticleGroup group, boolean callDestructionListener) {
        assert (!this.isLocked());
        if (this.isLocked()) {
            return;
        }
        this.m_particleSystem.destroyParticlesInGroup(group, callDestructionListener);
    }

    public void destroyParticlesInGroup(ParticleGroup group) {
        this.destroyParticlesInGroup(group, false);
    }

    public ParticleGroup[] getParticleGroupList() {
        return this.m_particleSystem.getParticleGroupList();
    }

    public int getParticleGroupCount() {
        return this.m_particleSystem.getParticleGroupCount();
    }

    public int getParticleCount() {
        return this.m_particleSystem.getParticleCount();
    }

    public int getParticleMaxCount() {
        return this.m_particleSystem.getParticleMaxCount();
    }

    public void setParticleMaxCount(int count) {
        this.m_particleSystem.setParticleMaxCount(count);
    }

    public void setParticleDensity(float density) {
        this.m_particleSystem.setParticleDensity(density);
    }

    public float getParticleDensity() {
        return this.m_particleSystem.getParticleDensity();
    }

    public void setParticleGravityScale(float gravityScale) {
        this.m_particleSystem.setParticleGravityScale(gravityScale);
    }

    public float getParticleGravityScale() {
        return this.m_particleSystem.getParticleGravityScale();
    }

    public void setParticleDamping(float damping) {
        this.m_particleSystem.setParticleDamping(damping);
    }

    public float getParticleDamping() {
        return this.m_particleSystem.getParticleDamping();
    }

    public void setParticleRadius(float radius) {
        this.m_particleSystem.setParticleRadius(radius);
    }

    public float getParticleRadius() {
        return this.m_particleSystem.getParticleRadius();
    }

    public int[] getParticleFlagsBuffer() {
        return this.m_particleSystem.getParticleFlagsBuffer();
    }

    public Vec2[] getParticlePositionBuffer() {
        return this.m_particleSystem.getParticlePositionBuffer();
    }

    public Vec2[] getParticleVelocityBuffer() {
        return this.m_particleSystem.getParticleVelocityBuffer();
    }

    public ParticleColor[] getParticleColorBuffer() {
        return this.m_particleSystem.getParticleColorBuffer();
    }

    public ParticleGroup[] getParticleGroupBuffer() {
        return this.m_particleSystem.getParticleGroupBuffer();
    }

    public Object[] getParticleUserDataBuffer() {
        return this.m_particleSystem.getParticleUserDataBuffer();
    }

    public void setParticleFlagsBuffer(int[] buffer, int capacity) {
        this.m_particleSystem.setParticleFlagsBuffer(buffer, capacity);
    }

    public void setParticlePositionBuffer(Vec2[] buffer, int capacity) {
        this.m_particleSystem.setParticlePositionBuffer(buffer, capacity);
    }

    public void setParticleVelocityBuffer(Vec2[] buffer, int capacity) {
        this.m_particleSystem.setParticleVelocityBuffer(buffer, capacity);
    }

    public void setParticleColorBuffer(ParticleColor[] buffer, int capacity) {
        this.m_particleSystem.setParticleColorBuffer(buffer, capacity);
    }

    public void setParticleUserDataBuffer(Object[] buffer, int capacity) {
        this.m_particleSystem.setParticleUserDataBuffer(buffer, capacity);
    }

    public ParticleContact[] getParticleContacts() {
        return this.m_particleSystem.m_contactBuffer;
    }

    public int getParticleContactCount() {
        return this.m_particleSystem.m_contactCount;
    }

    public ParticleBodyContact[] getParticleBodyContacts() {
        return this.m_particleSystem.m_bodyContactBuffer;
    }

    public int getParticleBodyContactCount() {
        return this.m_particleSystem.m_bodyContactCount;
    }

    public float computeParticleCollisionEnergy() {
        return this.m_particleSystem.computeParticleCollisionEnergy();
    }
}

