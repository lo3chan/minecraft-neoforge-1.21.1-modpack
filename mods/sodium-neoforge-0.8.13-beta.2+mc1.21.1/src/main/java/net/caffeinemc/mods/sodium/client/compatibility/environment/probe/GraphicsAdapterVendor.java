/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package net.caffeinemc.mods.sodium.client.compatibility.environment.probe;

import java.util.regex.Pattern;
import net.caffeinemc.mods.sodium.client.compatibility.environment.GlContextInfo;
import org.jetbrains.annotations.NotNull;

public enum GraphicsAdapterVendor {
    NVIDIA,
    AMD,
    INTEL,
    UNKNOWN;

    private static final Pattern INTEL_ICD_PATTERN;
    private static final Pattern NVIDIA_ICD_PATTERN;
    private static final Pattern AMD_ICD_PATTERN;

    @NotNull
    static GraphicsAdapterVendor fromPciVendorId(String vendor) {
        if (vendor.contains("0x1002")) {
            return AMD;
        }
        if (vendor.contains("0x10de")) {
            return NVIDIA;
        }
        if (vendor.contains("0x8086")) {
            return INTEL;
        }
        return UNKNOWN;
    }

    @NotNull
    public static GraphicsAdapterVendor fromIcdName(String name) {
        if (GraphicsAdapterVendor.matchesPattern(INTEL_ICD_PATTERN, name)) {
            return INTEL;
        }
        if (GraphicsAdapterVendor.matchesPattern(NVIDIA_ICD_PATTERN, name)) {
            return NVIDIA;
        }
        if (GraphicsAdapterVendor.matchesPattern(AMD_ICD_PATTERN, name)) {
            return AMD;
        }
        return UNKNOWN;
    }

    @NotNull
    public static GraphicsAdapterVendor fromContext(GlContextInfo context) {
        String vendor;
        return switch (vendor = context.vendor()) {
            case "NVIDIA Corporation" -> NVIDIA;
            case "Intel", "Intel Open Source Technology Center" -> INTEL;
            case "AMD", "ATI Technologies Inc." -> AMD;
            default -> UNKNOWN;
        };
    }

    private static boolean matchesPattern(Pattern pattern, String name) {
        return pattern.matcher(name).matches();
    }

    static {
        INTEL_ICD_PATTERN = Pattern.compile("ig(4|7|75|8|9|11|12|(xe2?(hpg?|lpg?)))icd(32|64)\\.dll", 2);
        NVIDIA_ICD_PATTERN = Pattern.compile("nvoglv(32|64)\\.dll", 2);
        AMD_ICD_PATTERN = Pattern.compile("(atiglpxx|atig6pxx)\\.dll", 2);
    }
}

