/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Version;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr.And;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr.Equal;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr.Expression;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr.Greater;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr.GreaterOrEqual;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr.Less;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr.LessOrEqual;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr.Not;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr.NotEqual;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr.Or;

public class CompositeExpression
implements Expression {
    private Expression exprTree;

    public CompositeExpression(Expression expr) {
        this.exprTree = expr;
    }

    public CompositeExpression and(Expression expr) {
        this.exprTree = new And(this.exprTree, expr);
        return this;
    }

    public CompositeExpression or(Expression expr) {
        this.exprTree = new Or(this.exprTree, expr);
        return this;
    }

    public boolean interpret(String version) {
        return this.interpret(Version.valueOf(version));
    }

    @Override
    public boolean interpret(Version version) {
        return this.exprTree.interpret(version);
    }

    public static class Helper {
        public static CompositeExpression not(Expression expr) {
            return new CompositeExpression(new Not(expr));
        }

        public static CompositeExpression eq(Version version) {
            return new CompositeExpression(new Equal(version));
        }

        public static CompositeExpression eq(String version) {
            return Helper.eq(Version.valueOf(version));
        }

        public static CompositeExpression neq(Version version) {
            return new CompositeExpression(new NotEqual(version));
        }

        public static CompositeExpression neq(String version) {
            return Helper.neq(Version.valueOf(version));
        }

        public static CompositeExpression gt(Version version) {
            return new CompositeExpression(new Greater(version));
        }

        public static CompositeExpression gt(String version) {
            return Helper.gt(Version.valueOf(version));
        }

        public static CompositeExpression gte(Version version) {
            return new CompositeExpression(new GreaterOrEqual(version));
        }

        public static CompositeExpression gte(String version) {
            return Helper.gte(Version.valueOf(version));
        }

        public static CompositeExpression lt(Version version) {
            return new CompositeExpression(new Less(version));
        }

        public static CompositeExpression lt(String version) {
            return Helper.lt(Version.valueOf(version));
        }

        public static CompositeExpression lte(Version version) {
            return new CompositeExpression(new LessOrEqual(version));
        }

        public static CompositeExpression lte(String version) {
            return Helper.lte(Version.valueOf(version));
        }
    }
}

