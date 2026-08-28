/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.semver.semver;

class NormalVersion
implements Comparable<NormalVersion> {
    private final int major;
    private final int minor;
    private final int patch;

    NormalVersion(int major, int minor, int patch) {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Major, minor and patch versions MUST be non-negative integers.");
        }
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    int getMajor() {
        return this.major;
    }

    int getMinor() {
        return this.minor;
    }

    int getPatch() {
        return this.patch;
    }

    NormalVersion incrementMajor() {
        return new NormalVersion(this.major + 1, 0, 0);
    }

    NormalVersion incrementMinor() {
        return new NormalVersion(this.major, this.minor + 1, 0);
    }

    NormalVersion incrementPatch() {
        return new NormalVersion(this.major, this.minor, this.patch + 1);
    }

    @Override
    public int compareTo(NormalVersion other) {
        int result = this.major - other.major;
        if (result == 0 && (result = this.minor - other.minor) == 0) {
            result = this.patch - other.patch;
        }
        return result;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NormalVersion)) {
            return false;
        }
        return this.compareTo((NormalVersion)other) == 0;
    }

    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + this.major;
        hash = 31 * hash + this.minor;
        hash = 31 * hash + this.patch;
        return hash;
    }

    public String toString() {
        return String.format("%d.%d.%d", this.major, this.minor, this.patch);
    }
}

