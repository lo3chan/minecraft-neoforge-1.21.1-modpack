/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics;

import java.util.Arrays;

public final class Version {
    int[] versions;

    public Version(String version) {
        String[] split = version.split("\\.");
        this.versions = new int[split.length];
        for (int i = 0; i < split.length; ++i) {
            this.versions[i] = Integer.parseInt(split[i]);
        }
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.versions);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Version other = (Version)obj;
        return Arrays.equals(this.versions, other.versions);
    }

    public String toString() {
        return "Version [versions=" + Arrays.toString(this.versions) + "]";
    }
}

