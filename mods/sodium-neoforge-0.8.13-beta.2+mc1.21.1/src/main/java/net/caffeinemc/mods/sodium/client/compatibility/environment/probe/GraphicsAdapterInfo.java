/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package net.caffeinemc.mods.sodium.client.compatibility.environment.probe;

import net.caffeinemc.mods.sodium.client.compatibility.environment.probe.GraphicsAdapterVendor;
import org.jetbrains.annotations.NotNull;

public interface GraphicsAdapterInfo {
    @NotNull
    public GraphicsAdapterVendor vendor();

    @NotNull
    public String name();

    public record LinuxPciAdapterInfo(@NotNull GraphicsAdapterVendor vendor, @NotNull String name, String pciVendorId, String pciDeviceId) implements GraphicsAdapterInfo
    {
    }
}

