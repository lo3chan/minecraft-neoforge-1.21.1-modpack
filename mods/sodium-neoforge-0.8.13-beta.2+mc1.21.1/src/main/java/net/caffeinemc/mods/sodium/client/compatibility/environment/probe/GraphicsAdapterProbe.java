/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.caffeinemc.mods.sodium.client.compatibility.environment.probe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.caffeinemc.mods.sodium.client.compatibility.environment.OsUtils;
import net.caffeinemc.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterInfo;
import net.caffeinemc.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterVendor;
import net.caffeinemc.mods.sodium.client.platform.windows.api.d3dkmt.D3DKMT;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphicsAdapterProbe {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Sodium-GraphicsAdapterProbe");
    private static final Set<String> LINUX_PCI_CLASSES = Set.of("0x030000", "0x030001", "0x030200", "0x038000");
    private static List<? extends GraphicsAdapterInfo> ADAPTERS = List.of();

    public static void findAdapters() {
        List<? extends GraphicsAdapterInfo> adapters;
        LOGGER.info("Searching for graphics cards...");
        try {
            adapters = switch (OsUtils.getOs()) {
                case OsUtils.OperatingSystem.WIN -> GraphicsAdapterProbe.findAdapters$Windows();
                case OsUtils.OperatingSystem.LINUX -> GraphicsAdapterProbe.findAdapters$Linux();
                default -> null;
            };
        }
        catch (Exception e) {
            LOGGER.error("Failed to find graphics adapters!", (Throwable)e);
            return;
        }
        if (adapters == null) {
            return;
        }
        if (adapters.isEmpty()) {
            LOGGER.warn("Could not find any graphics adapters! Probably the device is not on a bus we can probe, or there are no devices supporting 3D acceleration.");
        } else {
            for (GraphicsAdapterInfo graphicsAdapterInfo : adapters) {
                LOGGER.info("Found graphics adapter: {}", (Object)graphicsAdapterInfo);
            }
        }
        ADAPTERS = adapters;
    }

    private static List<? extends GraphicsAdapterInfo> findAdapters$Windows() {
        return D3DKMT.findGraphicsAdapters();
    }

    private static List<? extends GraphicsAdapterInfo> findAdapters$Linux() {
        ArrayList<GraphicsAdapterInfo.LinuxPciAdapterInfo> results = new ArrayList<GraphicsAdapterInfo.LinuxPciAdapterInfo>();
        try (Stream<Path> devices = Files.list(Path.of("/sys/bus/pci/devices/", new String[0]));){
            Iterable devicesIter = devices::iterator;
            for (Path devicePath : devicesIter) {
                String deviceClass = Files.readString(devicePath.resolve("class")).trim();
                if (!LINUX_PCI_CLASSES.contains(deviceClass)) continue;
                String pciVendorId = Files.readString(devicePath.resolve("vendor")).trim();
                String pciDeviceId = Files.readString(devicePath.resolve("device")).trim();
                GraphicsAdapterVendor adapterVendor = GraphicsAdapterVendor.fromPciVendorId(pciVendorId);
                String adapterName = GraphicsAdapterProbe.getPciDeviceName$Linux(pciVendorId, pciDeviceId);
                if (adapterName == null) {
                    adapterName = "<unknown>";
                }
                GraphicsAdapterInfo.LinuxPciAdapterInfo info = new GraphicsAdapterInfo.LinuxPciAdapterInfo(adapterVendor, adapterName, pciVendorId, pciDeviceId);
                results.add(info);
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return results;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private static String getPciDeviceName$Linux(String vendorId, String deviceId) {
        String deviceFilter = vendorId.substring(2) + ":" + deviceId.substring(2);
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"lspci", "-vmm", "-d", deviceFilter});
            int result = process.waitFor();
            if (result != 0) {
                throw new IOException("lspci exited with error code: %s".formatted(result));
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));){
                String line;
                do {
                    if ((line = reader.readLine()) == null) throw new IOException("lspci did not return a device name");
                } while (!line.startsWith("Device:"));
                String string = line.substring("Device:".length()).trim();
                return string;
            }
        }
        catch (Throwable e) {
            LOGGER.warn("Failed to query PCI device name for %s:%s".formatted(vendorId, deviceId), e);
            return null;
        }
    }

    public static Collection<? extends GraphicsAdapterInfo> getAdapters() {
        if (ADAPTERS == null) {
            LOGGER.error("Graphics adapters not probed yet; returning an empty list.");
            return Collections.emptyList();
        }
        return ADAPTERS;
    }
}

