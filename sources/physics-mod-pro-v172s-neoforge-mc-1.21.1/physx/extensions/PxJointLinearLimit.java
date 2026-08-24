package physx.extensions;

public class PxJointLinearLimit extends PxJointLimitParameters {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxJointLinearLimit() {
   }

   private static native int __sizeOf();

   public static PxJointLinearLimit wrapPointer(long address) {
      return address != 0L ? new PxJointLinearLimit(address) : null;
   }

   public static PxJointLinearLimit arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * index);
      }
   }

   protected PxJointLinearLimit(long address) {
      super(address);
   }

   public PxJointLinearLimit(float extent, PxSpring spring) {
      this.address = _PxJointLinearLimit(extent, spring.getAddress());
   }

   private static native long _PxJointLinearLimit(float var0, long var1);

   public void destroy() {
      if (this.address == 0L) {
         throw new IllegalStateException(this + " is already deleted");
      } else if (this.isExternallyAllocated) {
         throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
      } else {
         _delete_native_instance(this.address);
         this.address = 0L;
      }
   }

   private static native long _delete_native_instance(long var0);

   public float getValue() {
      this.checkNotNull();
      return _getValue(this.address);
   }

   private static native float _getValue(long var0);

   public void setValue(float value) {
      this.checkNotNull();
      _setValue(this.address, value);
   }

   private static native void _setValue(long var0, float var2);
}
