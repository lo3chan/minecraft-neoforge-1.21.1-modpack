package physx;

import physx.character.PxControllerManager;
import physx.common.PxDefaultAllocator;
import physx.common.PxDefaultCpuDispatcher;
import physx.common.PxErrorCallback;
import physx.common.PxFoundation;
import physx.common.PxTolerancesScale;
import physx.common.PxTransform;
import physx.cooking.PxConvexMeshDesc;
import physx.cooking.PxCookingParams;
import physx.cooking.PxTriangleMeshDesc;
import physx.extensions.PxD6Joint;
import physx.extensions.PxDistanceJoint;
import physx.extensions.PxFixedJoint;
import physx.extensions.PxPrismaticJoint;
import physx.extensions.PxRevoluteJoint;
import physx.extensions.PxSphericalJoint;
import physx.geometry.PxConvexMesh;
import physx.geometry.PxHeightField;
import physx.geometry.PxHeightFieldDesc;
import physx.geometry.PxTriangleMesh;
import physx.physics.PxPhysics;
import physx.physics.PxRigidActor;
import physx.physics.PxScene;
import physx.physics.PxSceneDesc;
import physx.physics.PxSimulationFilterShader;
import physx.support.PassThroughFilterShader;
import physx.support.PxOmniPvd;
import physx.support.PxPvd;
import physx.support.PxPvdTransport;

public class PxTopLevelFunctions extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxTopLevelFunctions() {
   }

   private static native int __sizeOf();

   public static PxTopLevelFunctions wrapPointer(long address) {
      return address != 0L ? new PxTopLevelFunctions(address) : null;
   }

   public static PxTopLevelFunctions arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * index);
      }
   }

   protected PxTopLevelFunctions(long address) {
      super(address);
   }

   public static int getPHYSICS_VERSION() {
      return _getPHYSICS_VERSION();
   }

   private static native int _getPHYSICS_VERSION();

   public static PxSimulationFilterShader DefaultFilterShader() {
      return PxSimulationFilterShader.wrapPointer(_DefaultFilterShader());
   }

   private static native long _DefaultFilterShader();

   public static void setupPassThroughFilterShader(PxSceneDesc sceneDesc, PassThroughFilterShader filterShader) {
      _setupPassThroughFilterShader(sceneDesc.getAddress(), filterShader.getAddress());
   }

   private static native void _setupPassThroughFilterShader(long var0, long var2);

   public static PxControllerManager CreateControllerManager(PxScene scene) {
      return PxControllerManager.wrapPointer(_CreateControllerManager(scene.getAddress()));
   }

   private static native long _CreateControllerManager(long var0);

   public static PxControllerManager CreateControllerManager(PxScene scene, boolean lockingEnabled) {
      return PxControllerManager.wrapPointer(_CreateControllerManager(scene.getAddress(), lockingEnabled));
   }

   private static native long _CreateControllerManager(long var0, boolean var2);

   public static PxFoundation CreateFoundation(int version, PxDefaultAllocator allocator, PxErrorCallback errorCallback) {
      return PxFoundation.wrapPointer(_CreateFoundation(version, allocator.getAddress(), errorCallback.getAddress()));
   }

   private static native long _CreateFoundation(int var0, long var1, long var3);

   public static PxPhysics CreatePhysics(int version, PxFoundation foundation, PxTolerancesScale params) {
      return PxPhysics.wrapPointer(_CreatePhysics(version, foundation.getAddress(), params.getAddress()));
   }

   private static native long _CreatePhysics(int var0, long var1, long var3);

   public static PxPhysics CreatePhysics(int version, PxFoundation foundation, PxTolerancesScale params, PxPvd pvd) {
      return PxPhysics.wrapPointer(_CreatePhysics(version, foundation.getAddress(), params.getAddress(), pvd != null ? pvd.getAddress() : 0L));
   }

   private static native long _CreatePhysics(int var0, long var1, long var3, long var5);

   public static PxPhysics CreatePhysics(int version, PxFoundation foundation, PxTolerancesScale params, PxPvd pvd, PxOmniPvd omniPvd) {
      return PxPhysics.wrapPointer(
         _CreatePhysics(version, foundation.getAddress(), params.getAddress(), pvd != null ? pvd.getAddress() : 0L, omniPvd != null ? omniPvd.getAddress() : 0L)
      );
   }

   private static native long _CreatePhysics(int var0, long var1, long var3, long var5, long var7);

   public static PxDefaultCpuDispatcher DefaultCpuDispatcherCreate(int numThreads) {
      return PxDefaultCpuDispatcher.wrapPointer(_DefaultCpuDispatcherCreate(numThreads));
   }

   private static native long _DefaultCpuDispatcherCreate(int var0);

   public static boolean InitExtensions(PxPhysics physics) {
      return _InitExtensions(physics.getAddress());
   }

   private static native boolean _InitExtensions(long var0);

   public static void CloseExtensions() {
      _CloseExtensions();
   }

   private static native void _CloseExtensions();

   public static PxPvd CreatePvd(PxFoundation foundation) {
      return PxPvd.wrapPointer(_CreatePvd(foundation.getAddress()));
   }

   private static native long _CreatePvd(long var0);

   public static PxPvdTransport DefaultPvdSocketTransportCreate(String host, int port, int timeoutInMilliseconds) {
      PlatformChecks.requirePlatform(7, "physx.PxTopLevelFunctions");
      return PxPvdTransport.wrapPointer(_DefaultPvdSocketTransportCreate(host, port, timeoutInMilliseconds));
   }

   private static native long _DefaultPvdSocketTransportCreate(String var0, int var1, int var2);

   public static PxOmniPvd CreateOmniPvd(PxFoundation foundation) {
      PlatformChecks.requirePlatform(7, "physx.PxTopLevelFunctions");
      return PxOmniPvd.wrapPointer(_CreateOmniPvd(foundation.getAddress()));
   }

   private static native long _CreateOmniPvd(long var0);

   public static PxD6Joint D6JointCreate(PxPhysics physics, PxRigidActor actor0, PxTransform localFrame0, PxRigidActor actor1, PxTransform localFrame1) {
      return PxD6Joint.wrapPointer(
         _D6JointCreate(
            physics.getAddress(),
            actor0 != null ? actor0.getAddress() : 0L,
            localFrame0.getAddress(),
            actor1 != null ? actor1.getAddress() : 0L,
            localFrame1.getAddress()
         )
      );
   }

   private static native long _D6JointCreate(long var0, long var2, long var4, long var6, long var8);

   public static PxDistanceJoint DistanceJointCreate(
      PxPhysics physics, PxRigidActor actor0, PxTransform localFrame0, PxRigidActor actor1, PxTransform localFrame1
   ) {
      return PxDistanceJoint.wrapPointer(
         _DistanceJointCreate(
            physics.getAddress(),
            actor0 != null ? actor0.getAddress() : 0L,
            localFrame0.getAddress(),
            actor1 != null ? actor1.getAddress() : 0L,
            localFrame1.getAddress()
         )
      );
   }

   private static native long _DistanceJointCreate(long var0, long var2, long var4, long var6, long var8);

   public static PxFixedJoint FixedJointCreate(PxPhysics physics, PxRigidActor actor0, PxTransform localFrame0, PxRigidActor actor1, PxTransform localFrame1) {
      return PxFixedJoint.wrapPointer(
         _FixedJointCreate(
            physics.getAddress(),
            actor0 != null ? actor0.getAddress() : 0L,
            localFrame0.getAddress(),
            actor1 != null ? actor1.getAddress() : 0L,
            localFrame1.getAddress()
         )
      );
   }

   private static native long _FixedJointCreate(long var0, long var2, long var4, long var6, long var8);

   public static PxPrismaticJoint PrismaticJointCreate(
      PxPhysics physics, PxRigidActor actor0, PxTransform localFrame0, PxRigidActor actor1, PxTransform localFrame1
   ) {
      return PxPrismaticJoint.wrapPointer(
         _PrismaticJointCreate(
            physics.getAddress(),
            actor0 != null ? actor0.getAddress() : 0L,
            localFrame0.getAddress(),
            actor1 != null ? actor1.getAddress() : 0L,
            localFrame1.getAddress()
         )
      );
   }

   private static native long _PrismaticJointCreate(long var0, long var2, long var4, long var6, long var8);

   public static PxRevoluteJoint RevoluteJointCreate(
      PxPhysics physics, PxRigidActor actor0, PxTransform localFrame0, PxRigidActor actor1, PxTransform localFrame1
   ) {
      return PxRevoluteJoint.wrapPointer(
         _RevoluteJointCreate(
            physics.getAddress(),
            actor0 != null ? actor0.getAddress() : 0L,
            localFrame0.getAddress(),
            actor1 != null ? actor1.getAddress() : 0L,
            localFrame1.getAddress()
         )
      );
   }

   private static native long _RevoluteJointCreate(long var0, long var2, long var4, long var6, long var8);

   public static PxSphericalJoint SphericalJointCreate(
      PxPhysics physics, PxRigidActor actor0, PxTransform localFrame0, PxRigidActor actor1, PxTransform localFrame1
   ) {
      return PxSphericalJoint.wrapPointer(
         _SphericalJointCreate(
            physics.getAddress(),
            actor0 != null ? actor0.getAddress() : 0L,
            localFrame0.getAddress(),
            actor1 != null ? actor1.getAddress() : 0L,
            localFrame1.getAddress()
         )
      );
   }

   private static native long _SphericalJointCreate(long var0, long var2, long var4, long var6, long var8);

   public static PxConvexMesh CreateConvexMesh(PxCookingParams params, PxConvexMeshDesc desc) {
      return PxConvexMesh.wrapPointer(_CreateConvexMesh(params.getAddress(), desc.getAddress()));
   }

   private static native long _CreateConvexMesh(long var0, long var2);

   public static PxTriangleMesh CreateTriangleMesh(PxCookingParams params, PxTriangleMeshDesc desc) {
      return PxTriangleMesh.wrapPointer(_CreateTriangleMesh(params.getAddress(), desc.getAddress()));
   }

   private static native long _CreateTriangleMesh(long var0, long var2);

   public static PxHeightField CreateHeightField(PxHeightFieldDesc desc) {
      return PxHeightField.wrapPointer(_CreateHeightField(desc.getAddress()));
   }

   private static native long _CreateHeightField(long var0);
}
