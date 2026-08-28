/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.NativeObject;
import physx.common.PxCollection;
import physx.common.PxInputData;
import physx.common.PxOutputStream;
import physx.cooking.PxCookingParams;
import physx.extensions.PxSerializationRegistry;
import physx.physics.PxPhysics;

public class PxSerialization
extends NativeObject {
    public static final int SIZEOF = PxSerialization.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxSerialization() {
    }

    private static native int __sizeOf();

    public static PxSerialization wrapPointer(long address) {
        return address != 0L ? new PxSerialization(address) : null;
    }

    public static PxSerialization arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxSerialization.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxSerialization(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxSerialization._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public static boolean isSerializable(PxCollection collection, PxSerializationRegistry sr) {
        return PxSerialization._isSerializable(collection.getAddress(), sr.getAddress());
    }

    private static native boolean _isSerializable(long var0, long var2);

    public static boolean isSerializable(PxCollection collection, PxSerializationRegistry sr, PxCollection externalReferences) {
        return PxSerialization._isSerializable(collection.getAddress(), sr.getAddress(), externalReferences.getAddress());
    }

    private static native boolean _isSerializable(long var0, long var2, long var4);

    public static void complete(PxCollection collection, PxSerializationRegistry sr) {
        PxSerialization._complete(collection.getAddress(), sr.getAddress());
    }

    private static native void _complete(long var0, long var2);

    public static void complete(PxCollection collection, PxSerializationRegistry sr, PxCollection exceptFor) {
        PxSerialization._complete(collection.getAddress(), sr.getAddress(), exceptFor.getAddress());
    }

    private static native void _complete(long var0, long var2, long var4);

    public static void complete(PxCollection collection, PxSerializationRegistry sr, PxCollection exceptFor, boolean followJoints) {
        PxSerialization._complete(collection.getAddress(), sr.getAddress(), exceptFor.getAddress(), followJoints);
    }

    private static native void _complete(long var0, long var2, long var4, boolean var6);

    public static void createSerialObjectIds(PxCollection collection, long base) {
        PxSerialization._createSerialObjectIds(collection.getAddress(), base);
    }

    private static native void _createSerialObjectIds(long var0, long var2);

    public static PxCollection createCollectionFromXml(PxInputData inputData, PxCookingParams params, PxSerializationRegistry sr) {
        return PxCollection.wrapPointer(PxSerialization._createCollectionFromXml(inputData.getAddress(), params.getAddress(), sr.getAddress()));
    }

    private static native long _createCollectionFromXml(long var0, long var2, long var4);

    public static PxCollection createCollectionFromXml(PxInputData inputData, PxCookingParams params, PxSerializationRegistry sr, PxCollection externalRefs) {
        return PxCollection.wrapPointer(PxSerialization._createCollectionFromXml(inputData.getAddress(), params.getAddress(), sr.getAddress(), externalRefs.getAddress()));
    }

    private static native long _createCollectionFromXml(long var0, long var2, long var4, long var6);

    public static PxCollection createCollectionFromBinary(NativeObject memBlock, PxSerializationRegistry sr) {
        return PxCollection.wrapPointer(PxSerialization._createCollectionFromBinary(memBlock.getAddress(), sr.getAddress()));
    }

    private static native long _createCollectionFromBinary(long var0, long var2);

    public static PxCollection createCollectionFromBinary(NativeObject memBlock, PxSerializationRegistry sr, PxCollection externalRefs) {
        return PxCollection.wrapPointer(PxSerialization._createCollectionFromBinary(memBlock.getAddress(), sr.getAddress(), externalRefs.getAddress()));
    }

    private static native long _createCollectionFromBinary(long var0, long var2, long var4);

    public static boolean serializeCollectionToXml(PxOutputStream outputStream, PxCollection collection, PxSerializationRegistry sr) {
        return PxSerialization._serializeCollectionToXml(outputStream.getAddress(), collection.getAddress(), sr.getAddress());
    }

    private static native boolean _serializeCollectionToXml(long var0, long var2, long var4);

    public static boolean serializeCollectionToXml(PxOutputStream outputStream, PxCollection collection, PxSerializationRegistry sr, PxCookingParams params) {
        return PxSerialization._serializeCollectionToXml(outputStream.getAddress(), collection.getAddress(), sr.getAddress(), params.getAddress());
    }

    private static native boolean _serializeCollectionToXml(long var0, long var2, long var4, long var6);

    public static boolean serializeCollectionToXml(PxOutputStream outputStream, PxCollection collection, PxSerializationRegistry sr, PxCookingParams params, PxCollection externalRefs) {
        return PxSerialization._serializeCollectionToXml(outputStream.getAddress(), collection.getAddress(), sr.getAddress(), params.getAddress(), externalRefs.getAddress());
    }

    private static native boolean _serializeCollectionToXml(long var0, long var2, long var4, long var6, long var8);

    public static boolean serializeCollectionToBinary(PxOutputStream outputStream, PxCollection collection, PxSerializationRegistry sr) {
        return PxSerialization._serializeCollectionToBinary(outputStream.getAddress(), collection.getAddress(), sr.getAddress());
    }

    private static native boolean _serializeCollectionToBinary(long var0, long var2, long var4);

    public static boolean serializeCollectionToBinary(PxOutputStream outputStream, PxCollection collection, PxSerializationRegistry sr, PxCollection externalRefs) {
        return PxSerialization._serializeCollectionToBinary(outputStream.getAddress(), collection.getAddress(), sr.getAddress(), externalRefs.getAddress());
    }

    private static native boolean _serializeCollectionToBinary(long var0, long var2, long var4, long var6);

    public static boolean serializeCollectionToBinary(PxOutputStream outputStream, PxCollection collection, PxSerializationRegistry sr, PxCollection externalRefs, boolean exportNames) {
        return PxSerialization._serializeCollectionToBinary(outputStream.getAddress(), collection.getAddress(), sr.getAddress(), externalRefs.getAddress(), exportNames);
    }

    private static native boolean _serializeCollectionToBinary(long var0, long var2, long var4, long var6, boolean var8);

    public static PxSerializationRegistry createSerializationRegistry(PxPhysics physics) {
        return PxSerializationRegistry.wrapPointer(PxSerialization._createSerializationRegistry(physics.getAddress()));
    }

    private static native long _createSerializationRegistry(long var0);
}

