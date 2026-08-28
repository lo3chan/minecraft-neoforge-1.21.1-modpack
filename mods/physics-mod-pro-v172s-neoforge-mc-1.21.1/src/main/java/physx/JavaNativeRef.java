/*
 * Decompiled with CFR 0.152.
 */
package physx;

import de.fabmax.physxjni.Loader;
import physx.NativeObject;

public class JavaNativeRef<T>
extends NativeObject {
    private static native long _new_instance(Object var0);

    private static native void _delete_instance(long var0);

    private static native Object _get_java_ref(long var0);

    public static <T> JavaNativeRef<T> fromNativeObject(NativeObject nativeObj) {
        return new JavaNativeRef<T>(nativeObj != null ? nativeObj.address : 0L);
    }

    protected JavaNativeRef(long address) {
        super(address);
    }

    public JavaNativeRef(Object javaRef) {
        this.address = JavaNativeRef._new_instance(javaRef);
    }

    public T get() {
        this.checkNotNull();
        return (T)JavaNativeRef._get_java_ref(this.address);
    }

    public void destroy() {
        this.checkNotNull();
        JavaNativeRef._delete_instance(this.address);
    }

    static {
        Loader.load();
    }
}

