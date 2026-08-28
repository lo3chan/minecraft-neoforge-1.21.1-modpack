/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Context;
import java.util.concurrent.atomic.AtomicInteger;

interface ValueReader {
    public boolean canRead(String var1);

    public Object read(String var1, AtomicInteger var2, Context var3);
}

