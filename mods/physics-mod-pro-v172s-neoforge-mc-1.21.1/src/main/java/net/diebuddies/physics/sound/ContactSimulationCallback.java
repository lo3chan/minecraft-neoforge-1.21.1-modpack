/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Tuple
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.SoundType
 *  org.joml.Matrix4d
 *  org.joml.Vector3d
 *  org.lwjgl.system.MemoryUtil
 */
package net.diebuddies.physics.sound;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.ParticleSpawner;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.settings.mobs.MobPhysicsType;
import net.diebuddies.physics.settings.mobs.MobSetting;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.system.MemoryUtil;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxJoint;
import physx.extensions.PxJointActorIndexEnum;
import physx.physics.PxActor;
import physx.physics.PxConstraintInfo;
import physx.physics.PxContactPair;
import physx.physics.PxContactPairHeader;
import physx.physics.PxContactPairPoint;
import physx.physics.PxPairFlagEnum;
import physx.physics.PxPairFlags;
import physx.physics.PxRigidBody;
import physx.physics.PxSimulationEventCallbackImpl;
import physx.support.Vector_PxContactPairPoint;

public class ContactSimulationCallback
extends PxSimulationEventCallbackImpl {
    public static final float FORCE_THRESHOLD = 0.25f;
    public static final int REPORT_CONTACT_FLAGS = PxPairFlagEnum.eNOTIFY_THRESHOLD_FORCE_FOUND.value | PxPairFlagEnum.eNOTIFY_CONTACT_POINTS.value;
    public static final long OBJECT_SOUND_DELAY = 500000000L;
    public static final int MAX_SOUNDS_PER_TICK = 1;
    public static int RESET_SOUNDS_PER_TICK_EVERY_X_TICKS = 1;
    private PhysicsWorld physics;
    private Level level;
    private Vector_PxContactPairPoint contacts = new Vector_PxContactPairPoint(64);
    public int soundCount;
    public int soundTickCount;

    public ContactSimulationCallback(PhysicsWorld physics, Level level) {
        this.physics = physics;
        this.level = level;
    }

    @Override
    public void onConstraintBreak(PxConstraintInfo constraints, int count) {
        super.onConstraintBreak(constraints, count);
        if (ConfigClient.jointBlood <= 0.0f) {
            return;
        }
        for (int i = 0; i < count; ++i) {
            Object mobSetting;
            PxConstraintInfo info = PxConstraintInfo.arrayGet(constraints.getAddress(), i);
            PxJoint joint = PxJoint.wrapPointer(info.getExternalReference().getAddress());
            Tuple<IRigidBody, IRigidBody> parents = this.physics.getJointParents(joint);
            if (parents == null) continue;
            PxTransform transform = joint.getLocalPose(PxJointActorIndexEnum.eACTOR0);
            IRigidBody rigidBody = (IRigidBody)parents.getA();
            PhysicsEntity entity = rigidBody.getEntity();
            Object object = entity.info;
            if (object instanceof EntityType) {
                EntityType entityType = (EntityType)object;
                if (entity.type == PhysicsEntity.Type.MOB && ((MobSetting)(mobSetting = ConfigMobs.getMobSetting(entityType))).getType() != MobPhysicsType.RAGDOLL_BREAK_BLOOD) {
                    return;
                }
            }
            if (!((mobSetting = rigidBody.getRigidBody()) instanceof PxRigidBody)) continue;
            PxRigidBody body = (PxRigidBody)mobSetting;
            PxTransform globalTransform = body.getGlobalPose();
            PxVec3 globalPos = globalTransform.getP();
            PxQuat globalRot = globalTransform.getQ();
            Matrix4d globalMatrix = new Matrix4d().translationRotate((double)globalPos.getX(), (double)globalPos.getY(), (double)globalPos.getZ(), (double)globalRot.getX(), (double)globalRot.getY(), (double)globalRot.getZ(), (double)globalRot.getW());
            Vector3d offset = this.physics.getOffset();
            int amount = (int)((float)(8 + Math.randomInt(12)) * ConfigClient.jointBlood);
            PxVec3 pos = transform.getP();
            Vector3d finalPos = globalMatrix.transformPosition((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), new Vector3d());
            for (int j = 0; j < amount; ++j) {
                ParticleSpawner.spawnBloodPhysicsParticle(this.level, offset.x + finalPos.x, offset.y + finalPos.y, offset.z + finalPos.z);
            }
        }
    }

    @Override
    public void onContact(PxContactPairHeader pairHeader, PxContactPair pairs, int nbPairs) {
        super.onContact(pairHeader, pairs, nbPairs);
        if (this.soundCount >= 1 || ConfigClient.impactVolume < 0.01f) {
            return;
        }
        PxActor actor0 = pairHeader.getActors(0);
        PxActor actor1 = pairHeader.getActors(1);
        for (int i = 0; i < nbPairs; ++i) {
            IRigidBody body1;
            PxContactPair pair = PxContactPair.arrayGet(pairs.getAddress(), i);
            PxPairFlags events = pair.getEvents();
            if (!events.isSet(PxPairFlagEnum.eNOTIFY_THRESHOLD_FORCE_FOUND)) continue;
            double largestImpulse = 0.0;
            PxVec3 impulsePos = null;
            int contactPoints = pair.extractContacts(this.contacts.data(), 64);
            for (int j = 0; j < contactPoints; ++j) {
                float z;
                float y;
                PxContactPairPoint cp = this.contacts.at(j);
                PxVec3 impulse = cp.getImpulse();
                PxVec3 position = cp.getPosition();
                float x = MemoryUtil.memGetFloat((long)impulse.getAddress());
                double impulseMagnitude = x * x + (y = MemoryUtil.memGetFloat((long)(impulse.getAddress() + 4L))) * y + (z = MemoryUtil.memGetFloat((long)(impulse.getAddress() + 8L))) * z;
                if (!(largestImpulse < impulseMagnitude)) continue;
                largestImpulse = impulseMagnitude;
                impulsePos = position;
            }
            double volume = Math.remapClamp(largestImpulse, 2.0E-5, 0.0015, 0.0, 1.0) * (double)ConfigClient.impactVolume;
            if (volume <= 0.0) continue;
            PhysicsMod mod = PhysicsMod.getInstance(this.level);
            long time = mod.time;
            PhysicsWorld physics = mod.getPhysicsWorld();
            IRigidBody body0 = physics.getBody(actor0);
            SoundType sound = null;
            if (body0 != null && body0.getEntity().lastSoundTime > 500000000L) {
                sound = body0.getEntity().sound;
                body0.getEntity().lastSoundTime = time;
            }
            if ((body1 = physics.getBody(actor1)) != null && time - body1.getEntity().lastSoundTime > 500000000L) {
                if (sound == null) {
                    sound = body1.getEntity().sound;
                }
                body1.getEntity().lastSoundTime = time;
            }
            if (sound == null) continue;
            Vector3d offset = physics.getOffset();
            SoundEvent soundEvent = sound.getHitSound();
            if (soundEvent == null) continue;
            float pitch = 0.85f + Math.random() * 0.3f;
            float ix = MemoryUtil.memGetFloat((long)impulsePos.getAddress());
            float iy = MemoryUtil.memGetFloat((long)(impulsePos.getAddress() + 4L));
            float iz = MemoryUtil.memGetFloat((long)(impulsePos.getAddress() + 8L));
            this.level.playLocalSound((double)ix + offset.x, (double)iy + offset.y, (double)iz + offset.z, soundEvent, SoundSource.BLOCKS, (float)volume, pitch, true);
            ++this.soundCount;
        }
    }

    public void tick(float fixedTimeStep) {
        ++this.soundTickCount;
        if (this.soundTickCount >= RESET_SOUNDS_PER_TICK_EVERY_X_TICKS) {
            this.soundTickCount = 0;
            this.soundCount = 0;
        }
    }
}

