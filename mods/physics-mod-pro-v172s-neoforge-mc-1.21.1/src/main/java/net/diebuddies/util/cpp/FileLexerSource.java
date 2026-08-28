/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.diebuddies.util.cpp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import net.diebuddies.util.cpp.InputLexerSource;

public class FileLexerSource
extends InputLexerSource {
    private final String path;
    private final File file;

    public FileLexerSource(@Nonnull File file, @Nonnull Charset charset, @Nonnull String path) throws IOException {
        super(new FileInputStream(file), charset);
        this.file = file;
        this.path = path;
    }

    public FileLexerSource(@Nonnull File file, @Nonnull String path) throws IOException {
        this(file, Charset.defaultCharset(), path);
    }

    public FileLexerSource(@Nonnull File file, @Nonnull Charset charset) throws IOException {
        this(file, charset, file.getPath());
    }

    @Deprecated
    public FileLexerSource(@Nonnull File file) throws IOException {
        this(file, Charset.defaultCharset());
    }

    public FileLexerSource(@Nonnull String path, @Nonnull Charset charset) throws IOException {
        this(new File(path), charset, path);
    }

    @Deprecated
    public FileLexerSource(@Nonnull String path) throws IOException {
        this(path, Charset.defaultCharset());
    }

    @Nonnull
    public File getFile() {
        return this.file;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getName() {
        return this.getPath();
    }

    @Override
    public String toString() {
        return "file " + this.getPath();
    }
}

