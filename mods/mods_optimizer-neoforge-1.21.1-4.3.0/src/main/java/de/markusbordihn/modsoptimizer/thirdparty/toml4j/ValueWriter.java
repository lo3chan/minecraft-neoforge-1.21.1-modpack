/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.WriterContext;

interface ValueWriter {
    public boolean canWrite(Object var1);

    public void write(Object var1, WriterContext var2);

    public boolean isPrimitiveType();
}

