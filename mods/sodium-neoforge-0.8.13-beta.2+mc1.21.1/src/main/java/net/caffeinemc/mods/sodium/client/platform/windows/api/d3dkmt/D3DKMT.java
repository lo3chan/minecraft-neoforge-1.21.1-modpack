/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.Struct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.caffeinemc.mods.sodium.client.platform.windows.api.d3dkmt;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.caffeinemc.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterInfo;
import net.caffeinemc.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterVendor;
import net.caffeinemc.mods.sodium.client.platform.windows.WindowsFileVersion;
import net.caffeinemc.mods.sodium.client.platform.windows.api.Gdi32;
import net.caffeinemc.mods.sodium.client.platform.windows.api.d3dkmt.D3DKMTAdapterInfoStruct;
import net.caffeinemc.mods.sodium.client.platform.windows.api.d3dkmt.D3DKMTAdapterRegistryInfoStruct;
import net.caffeinemc.mods.sodium.client.platform.windows.api.d3dkmt.D3DKMTEnumAdaptersStruct;
import net.caffeinemc.mods.sodium.client.platform.windows.api.d3dkmt.D3DKMTOpenGLInfoStruct;
import net.caffeinemc.mods.sodium.client.platform.windows.api.d3dkmt.D3DKMTQueryAdapterInfoStruct;
import net.caffeinemc.mods.sodium.client.platform.windows.api.version.Version;
import net.caffeinemc.mods.sodium.client.platform.windows.api.version.VersionFixedFileInfoStruct;
import net.caffeinemc.mods.sodium.client.platform.windows.api.version.VersionInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class D3DKMT {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Sodium-D3DKMT");

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<WDDMAdapterInfo> findGraphicsAdapters() {
        if (!Gdi32.isD3DKMTSupported()) {
            LOGGER.warn("Unable to query graphics adapters when the operating system is older than Windows 8.0.");
            return List.of();
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            ArrayList<WDDMAdapterInfo> arrayList;
            D3DKMTEnumAdaptersStruct adapters = D3DKMTEnumAdaptersStruct.calloc(stack);
            D3DKMT.apiCheckError("D3DKMTEnumAdapters", Gdi32.nD3DKMTEnumAdapters(adapters.address()));
            D3DKMTAdapterInfoStruct.Buffer adapterInfoBuffer = adapters.getAdapters();
            try {
                arrayList = D3DKMT.queryAdapters(adapterInfoBuffer);
            }
            catch (Throwable throwable) {
                D3DKMT.freeAdapters(adapterInfoBuffer);
                throw throwable;
            }
            D3DKMT.freeAdapters(adapterInfoBuffer);
            return arrayList;
        }
    }

    @NotNull
    private static ArrayList<WDDMAdapterInfo> queryAdapters(@NotNull D3DKMTAdapterInfoStruct.Buffer adapterInfoBuffer) {
        ArrayList<WDDMAdapterInfo> results = new ArrayList<WDDMAdapterInfo>();
        for (int adapterIndex = adapterInfoBuffer.position(); adapterIndex < adapterInfoBuffer.limit(); ++adapterIndex) {
            D3DKMTAdapterInfoStruct pAdapterInfo = (D3DKMTAdapterInfoStruct)adapterInfoBuffer.get(adapterIndex);
            int pAdapter = pAdapterInfo.getAdapterHandle();
            WDDMAdapterInfo parsed = D3DKMT.getAdapterInfo(pAdapter);
            if (parsed == null) continue;
            results.add(parsed);
        }
        return results;
    }

    private static void freeAdapters(@NotNull D3DKMTAdapterInfoStruct.Buffer adapterInfoBuffer) {
        for (int adapterIndex = adapterInfoBuffer.position(); adapterIndex < adapterInfoBuffer.limit(); ++adapterIndex) {
            D3DKMTAdapterInfoStruct adapterInfo = (D3DKMTAdapterInfoStruct)adapterInfoBuffer.get(adapterIndex);
            D3DKMT.apiCheckError("D3DKMTCloseAdapter", D3DKMT.d3dkmtCloseAdapter(adapterInfo.getAdapterHandle()));
        }
    }

    private static @Nullable WDDMAdapterInfo getAdapterInfo(int adapter) {
        int adapterType = D3DKMT.queryAdapterType(adapter);
        if (!D3DKMT.isSupportedAdapterType(adapterType)) {
            return null;
        }
        String adapterName = D3DKMT.queryFriendlyName(adapter);
        String driverFileName = D3DKMT.queryDriverFileName(adapter);
        WindowsFileVersion driverVersion = null;
        GraphicsAdapterVendor driverVendor = GraphicsAdapterVendor.UNKNOWN;
        if (driverFileName != null) {
            driverVersion = D3DKMT.queryDriverVersion(driverFileName);
            driverVendor = GraphicsAdapterVendor.fromIcdName(D3DKMT.getOpenGlIcdName(driverFileName));
        }
        return new WDDMAdapterInfo(driverVendor, adapterName, adapterType, driverFileName, driverVersion);
    }

    private static boolean isSupportedAdapterType(int adapterType) {
        if ((adapterType & 1) == 0) {
            return false;
        }
        return (adapterType & 4) == 0;
    }

    @Nullable
    private static String queryDriverFileName(int adapter) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            D3DKMTOpenGLInfoStruct info = D3DKMTOpenGLInfoStruct.calloc(stack);
            D3DKMT.d3dkmtQueryAdapterInfo(adapter, 2, MemoryUtil.memByteBuffer((Struct)info));
            String string = info.getUserModeDriverFileName();
            return string;
        }
    }

    @Nullable
    private static WindowsFileVersion queryDriverVersion(String file) {
        VersionInfo version = Version.getModuleFileVersion(file);
        if (version == null) {
            return null;
        }
        VersionFixedFileInfoStruct fileVersion = version.queryFixedFileInfo();
        if (fileVersion == null) {
            return null;
        }
        return WindowsFileVersion.fromFileVersion(fileVersion);
    }

    @NotNull
    private static String queryFriendlyName(int adapter) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            D3DKMTAdapterRegistryInfoStruct registryInfo = D3DKMTAdapterRegistryInfoStruct.calloc(stack);
            D3DKMT.d3dkmtQueryAdapterInfo(adapter, 8, MemoryUtil.memByteBuffer((Struct)registryInfo));
            String name = registryInfo.getAdapterString();
            if (name == null) {
                name = "<unknown>";
            }
            String string = name;
            return string;
        }
    }

    private static int queryAdapterType(int adapter) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            IntBuffer info = stack.callocInt(1);
            D3DKMT.d3dkmtQueryAdapterInfo(adapter, 15, MemoryUtil.memByteBuffer((IntBuffer)info));
            int n = info.get(0);
            return n;
        }
    }

    private static void d3dkmtQueryAdapterInfo(int adapter, int type, ByteBuffer holder) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            D3DKMTQueryAdapterInfoStruct info = D3DKMTQueryAdapterInfoStruct.malloc(stack);
            info.setAdapterHandle(adapter);
            info.setType(type);
            info.setDataPointer(MemoryUtil.memAddress((ByteBuffer)holder));
            info.setDataLength(holder.remaining());
            D3DKMT.apiCheckError("D3DKMTQueryAdapterInfo", Gdi32.nd3dKmtQueryAdapterInfo(info.address()));
        }
    }

    private static int d3dkmtCloseAdapter(int handle) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            IntBuffer info = stack.ints(handle);
            int n = Gdi32.nD3DKMTCloseAdapter(MemoryUtil.memAddress((IntBuffer)info));
            return n;
        }
    }

    private static void apiCheckError(String name, int error) {
        if (error != 0) {
            throw new RuntimeException("%s returned non-zero result (error=%s)".formatted(name, Integer.toHexString(error)));
        }
    }

    private static String getOpenGlIcdName(@Nullable String filePath) {
        if (filePath == null) {
            return null;
        }
        Path fileName = Paths.get(filePath, new String[0]).getFileName();
        if (fileName == null) {
            return null;
        }
        return fileName.toString();
    }

    public record WDDMAdapterInfo(@NotNull GraphicsAdapterVendor vendor, @NotNull String name, int adapterType, @Nullable String openglIcdFilePath, @Nullable WindowsFileVersion openglIcdVersion) implements GraphicsAdapterInfo
    {
        @Nullable
        public String getOpenGlIcdName() {
            return D3DKMT.getOpenGlIcdName(this.openglIcdFilePath);
        }

        @Override
        public String toString() {
            return "AdapterInfo{vendor=%s, description='%s', adapterType=0x%08X, openglIcdFilePath='%s', openglIcdVersion=%s}".formatted(new Object[]{this.vendor, this.name, this.adapterType, this.openglIcdFilePath, this.openglIcdVersion});
        }
    }
}

