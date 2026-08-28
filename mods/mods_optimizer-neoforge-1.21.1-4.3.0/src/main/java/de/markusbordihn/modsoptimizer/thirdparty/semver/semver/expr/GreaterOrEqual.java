/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Version;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr.Expression;

class GreaterOrEqual
implements Expression {
    private final Version parsedVersion;

    GreaterOrEqual(Version parsedVersion) {
        this.parsedVersion = parsedVersion;
    }

    @Override
    public boolean interpret(Version version) {
        return version.greaterThanOrEqualTo(this.parsedVersion);
    }
}

