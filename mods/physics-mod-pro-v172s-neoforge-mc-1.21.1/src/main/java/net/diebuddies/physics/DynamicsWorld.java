/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 *  org.joml.Vector3f
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.physics;

import java.util.ArrayDeque;
import java.util.Queue;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsUpdate;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.sound.ContactSimulationCallback;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import physx.PxTopLevelFunctions;
import physx.common.PxVec3;
import physx.physics.PxActor;
import physx.physics.PxBroadPhaseTypeEnum;
import physx.physics.PxPairFilteringModeEnum;
import physx.physics.PxPruningStructureTypeEnum;
import physx.physics.PxScene;
import physx.physics.PxSceneDesc;
import physx.physics.PxSceneFlagEnum;
import physx.physics.PxSceneQueryUpdateModeEnum;
import physx.physics.PxSolverTypeEnum;

public class DynamicsWorld {
    public static final Vector3f DEFAULT_GRAVITY = new Vector3f(0.0f, -9.81f, 0.0f);
    public static final Vector3f DEFAULT_BUOYANCY = new Vector3f(0.0f, 2.0f, 0.0f);
    private PxSceneDesc sceneDesc;
    private PxScene scene;
    private float fixedTimeStep;
    private boolean destroyed;
    private double time = 0.0;
    private boolean skipFirst;
    private PhysicsWorld physics;
    private Queue<Runnable> events = new ArrayDeque<Runnable>();
    private Level level;
    private Vector3f buoyancy;
    private Vector3f gravity;
    private ContactSimulationCallback contactCallback;
    private boolean simulating = true;

    public DynamicsWorld(PhysicsWorld physics, Level level, float fixedTimeStep) {
        this.physics = physics;
        this.level = level;
        this.destroyed = false;
        this.fixedTimeStep = fixedTimeStep;
        int numThreads = ConfigClient.cpuThreads;
        this.sceneDesc = new PxSceneDesc(StarterClient.tolerances);
        Vector3f gravity = ConfigClient.getGravity(level.dimension().location());
        this.buoyancy = ConfigClient.getBuoyancy(level.dimension().location());
        try (MemoryStack mem = MemoryStack.stackPush();){
            this.sceneDesc.setGravity(PxVec3.createAt(mem, MemoryStack::nmalloc, gravity.x, gravity.y, gravity.z));
            this.gravity = new Vector3f(gravity.x, gravity.y, gravity.z);
        }
        this.sceneDesc.setCpuDispatcher(PxTopLevelFunctions.DefaultCpuDispatcherCreate(numThreads));
        this.sceneDesc.setFilterShader(PxTopLevelFunctions.DefaultFilterShader());
        this.sceneDesc.setSolverType(PxSolverTypeEnum.ePGS);
        this.sceneDesc.setKineKineFilteringMode(PxPairFilteringModeEnum.eKILL);
        this.sceneDesc.setStaticKineFilteringMode(PxPairFilteringModeEnum.eKILL);
        this.sceneDesc.setSceneQueryUpdateMode(PxSceneQueryUpdateModeEnum.eBUILD_DISABLED_COMMIT_DISABLED);
        this.contactCallback = new ContactSimulationCallback(physics, level);
        this.sceneDesc.setSimulationEventCallback(this.contactCallback);
        this.sceneDesc.getFlags().raise(PxSceneFlagEnum.eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS);
        if (ConfigClient.cudaLiquids()) {
            this.sceneDesc.setCudaContextManager(StarterClient.cudaManager);
            this.sceneDesc.setStaticStructure(PxPruningStructureTypeEnum.eDYNAMIC_AABB_TREE);
            this.sceneDesc.getFlags().raise(PxSceneFlagEnum.eENABLE_PCM);
            this.sceneDesc.getFlags().raise(PxSceneFlagEnum.eENABLE_GPU_DYNAMICS);
            this.sceneDesc.setBroadPhaseType(PxBroadPhaseTypeEnum.eGPU);
            this.sceneDesc.setSolverType(PxSolverTypeEnum.eTGS);
        }
        this.scene = StarterClient.physics.createScene(this.sceneDesc);
    }

    public Vector3f getBuoyancy() {
        return this.buoyancy;
    }

    public Vector3f getGravity() {
        return this.gravity;
    }

    public int update(PhysicsUpdate physics, double diff) {
        int updateCount = 0;
        if (!this.destroyed) {
            boolean willUpdate;
            this.time += diff;
            boolean bl = willUpdate = this.time >= (double)this.fixedTimeStep;
            if (willUpdate) {
                PerformanceTracker.start("physics_tick_physx");
            }
            long physicsTime = System.nanoTime();
            while (this.time >= (double)this.fixedTimeStep) {
                this.time -= (double)this.fixedTimeStep;
                if (!this.skipFirst) {
                    this.skipFirst = true;
                } else {
                    this.scene.fetchResults(true);
                    if (ConfigClient.gravityChanged) {
                        ConfigClient.gravityChanged = false;
                        Vector3f gravity = ConfigClient.getGravity(this.level.dimension().location());
                        try (MemoryStack mem = MemoryStack.stackPush();){
                            this.scene.setGravity(PxVec3.createAt(mem, MemoryStack::nmalloc, gravity.x, gravity.y, gravity.z));
                            this.gravity.set(gravity.x, gravity.y, gravity.z);
                        }
                        this.buoyancy = ConfigClient.getBuoyancy(this.level.dimension().location());
                    }
                    this.simulating = false;
                    this.runQueue();
                    physics.physicsUpdate(this.fixedTimeStep);
                    this.simulating = true;
                }
                this.scene.simulate(this.fixedTimeStep);
                this.contactCallback.tick(this.fixedTimeStep);
                ++updateCount;
                long physicsDiff = System.nanoTime() - physicsTime;
                if (physicsDiff <= 25000000L) continue;
                break;
            }
            this.time %= (double)this.fixedTimeStep;
            if (willUpdate) {
                PerformanceTracker.end("physics_tick_physx");
            }
        }
        return updateCount;
    }

    public PxScene getScene() {
        return this.scene;
    }

    private void runQueue() {
        Runnable event = null;
        while ((event = this.events.poll()) != null) {
            event.run();
        }
        this.events.clear();
    }

    public void finish() {
        if (!this.destroyed) {
            if (this.skipFirst) {
                this.scene.fetchResults(true);
            }
            this.simulating = false;
            this.runQueue();
        }
    }

    public void destroy() {
        if (!this.destroyed) {
            this.scene.release();
            if (this.contactCallback != null) {
                this.contactCallback.destroy();
            }
        }
        this.destroyed = true;
    }

    public void addActor(PxActor actor) {
        if (this.simulating) {
            this.queue(() -> this.scene.addActor(actor));
        } else {
            this.scene.addActor(actor);
        }
    }

    public void removeActor(PxActor actor) {
        if (this.simulating) {
            this.queue(() -> this.scene.removeActor(actor));
        } else {
            this.scene.removeActor(actor);
        }
    }

    public boolean isDestroyed() {
        return this.destroyed;
    }

    public double getTime() {
        return this.time;
    }

    public float getFixedTimeStep() {
        return this.fixedTimeStep;
    }

    public void queue(Runnable runnable) {
        if (this.simulating) {
            this.events.add(runnable);
        } else {
            runnable.run();
        }
    }
}

