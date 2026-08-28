/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.semver.semver;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.MetadataVersion;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.NormalVersion;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Parser;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.VersionParser;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr.Expression;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr.ExpressionParser;
import java.util.Comparator;

public class Version
implements Comparable<Version> {
    private final NormalVersion normal;
    private final MetadataVersion preRelease;
    private final MetadataVersion build;
    private static final String PRE_RELEASE_PREFIX = "-";
    private static final String BUILD_PREFIX = "+";
    public static final Comparator<Version> BUILD_AWARE_ORDER = new BuildAwareOrder();

    Version(NormalVersion normal) {
        this(normal, MetadataVersion.NULL, MetadataVersion.NULL);
    }

    Version(NormalVersion normal, MetadataVersion preRelease) {
        this(normal, preRelease, MetadataVersion.NULL);
    }

    Version(NormalVersion normal, MetadataVersion preRelease, MetadataVersion build) {
        this.normal = normal;
        this.preRelease = preRelease;
        this.build = build;
    }

    public static Version valueOf(String version) {
        return VersionParser.parseValidSemVer(version);
    }

    public static Version forIntegers(int major) {
        return new Version(new NormalVersion(major, 0, 0));
    }

    public static Version forIntegers(int major, int minor) {
        return new Version(new NormalVersion(major, minor, 0));
    }

    public static Version forIntegers(int major, int minor, int patch) {
        return new Version(new NormalVersion(major, minor, patch));
    }

    public boolean satisfies(String expr) {
        Parser<Expression> parser = ExpressionParser.newInstance();
        return this.satisfies(parser.parse(expr));
    }

    public boolean satisfies(Expression expr) {
        return expr.interpret(this);
    }

    public Version incrementMajorVersion() {
        return new Version(this.normal.incrementMajor());
    }

    public Version incrementMajorVersion(String preRelease) {
        return new Version(this.normal.incrementMajor(), VersionParser.parsePreRelease(preRelease));
    }

    public Version incrementMinorVersion() {
        return new Version(this.normal.incrementMinor());
    }

    public Version incrementMinorVersion(String preRelease) {
        return new Version(this.normal.incrementMinor(), VersionParser.parsePreRelease(preRelease));
    }

    public Version incrementPatchVersion() {
        return new Version(this.normal.incrementPatch());
    }

    public Version incrementPatchVersion(String preRelease) {
        return new Version(this.normal.incrementPatch(), VersionParser.parsePreRelease(preRelease));
    }

    public Version incrementPreReleaseVersion() {
        return new Version(this.normal, this.preRelease.increment());
    }

    public Version incrementBuildMetadata() {
        return new Version(this.normal, this.preRelease, this.build.increment());
    }

    public Version setPreReleaseVersion(String preRelease) {
        return new Version(this.normal, VersionParser.parsePreRelease(preRelease));
    }

    public Version setBuildMetadata(String build) {
        return new Version(this.normal, this.preRelease, VersionParser.parseBuild(build));
    }

    public int getMajorVersion() {
        return this.normal.getMajor();
    }

    public int getMinorVersion() {
        return this.normal.getMinor();
    }

    public int getPatchVersion() {
        return this.normal.getPatch();
    }

    public String getNormalVersion() {
        return this.normal.toString();
    }

    public String getPreReleaseVersion() {
        return this.preRelease.toString();
    }

    public String getBuildMetadata() {
        return this.build.toString();
    }

    public boolean greaterThan(Version other) {
        return this.compareTo(other) > 0;
    }

    public boolean greaterThanOrEqualTo(Version other) {
        return this.compareTo(other) >= 0;
    }

    public boolean lessThan(Version other) {
        return this.compareTo(other) < 0;
    }

    public boolean lessThanOrEqualTo(Version other) {
        return this.compareTo(other) <= 0;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Version)) {
            return false;
        }
        return this.compareTo((Version)other) == 0;
    }

    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.normal.hashCode();
        hash = 97 * hash + this.preRelease.hashCode();
        return hash;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getNormalVersion());
        if (!this.getPreReleaseVersion().isEmpty()) {
            sb.append(PRE_RELEASE_PREFIX).append(this.getPreReleaseVersion());
        }
        if (!this.getBuildMetadata().isEmpty()) {
            sb.append(BUILD_PREFIX).append(this.getBuildMetadata());
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Version other) {
        int result = this.normal.compareTo(other.normal);
        if (result == 0) {
            result = this.preRelease.compareTo(other.preRelease);
        }
        return result;
    }

    public int compareWithBuildsTo(Version other) {
        return BUILD_AWARE_ORDER.compare(this, other);
    }

    private static class BuildAwareOrder
    implements Comparator<Version> {
        private BuildAwareOrder() {
        }

        @Override
        public int compare(Version v1, Version v2) {
            int result = v1.compareTo(v2);
            if (result == 0) {
                result = v1.build.compareTo(v2.build);
                if (v1.build == MetadataVersion.NULL || v2.build == MetadataVersion.NULL) {
                    result = -1 * result;
                }
            }
            return result;
        }
    }

    public static class Builder {
        private String normal;
        private String preRelease;
        private String build;

        public Builder() {
        }

        public Builder(String normal) {
            this.normal = normal;
        }

        public Builder setNormalVersion(String normal) {
            this.normal = normal;
            return this;
        }

        public Builder setPreReleaseVersion(String preRelease) {
            this.preRelease = preRelease;
            return this;
        }

        public Builder setBuildMetadata(String build) {
            this.build = build;
            return this;
        }

        public Version build() {
            StringBuilder sb = new StringBuilder();
            if (this.isFilled(this.normal)) {
                sb.append(this.normal);
            }
            if (this.isFilled(this.preRelease)) {
                sb.append(Version.PRE_RELEASE_PREFIX).append(this.preRelease);
            }
            if (this.isFilled(this.build)) {
                sb.append(Version.BUILD_PREFIX).append(this.build);
            }
            return VersionParser.parseValidSemVer(sb.toString());
        }

        private boolean isFilled(String str) {
            return str != null && !str.isEmpty();
        }
    }
}

