/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Identifier;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Results;
import java.util.concurrent.atomic.AtomicInteger;

class Context {
    final Identifier identifier;
    final AtomicInteger line;
    final Results.Errors errors;

    public Context(Identifier identifier, AtomicInteger line, Results.Errors errors) {
        this.identifier = identifier;
        this.line = line;
        this.errors = errors;
    }

    public Context with(Identifier identifier) {
        return new Context(identifier, this.line, this.errors);
    }
}

