/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.diebuddies.util.cpp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import net.diebuddies.util.cpp.InputLexerSource;
import net.diebuddies.util.cpp.Source;
import net.diebuddies.util.cpp.VirtualFile;
import net.diebuddies.util.cpp.VirtualFileSystem;

public class ResourceFileSystem
implements VirtualFileSystem {
    private final ClassLoader loader;
    private final Charset charset;

    public ResourceFileSystem(@Nonnull ClassLoader loader, @Nonnull Charset charset) {
        this.loader = loader;
        this.charset = charset;
    }

    @Override
    public VirtualFile getFile(String path) {
        return new ResourceFile(this.loader, path);
    }

    @Override
    public VirtualFile getFile(String dir, String name) {
        return this.getFile(dir + "/" + name);
    }

    private class ResourceFile
    implements VirtualFile {
        private final ClassLoader loader;
        private final String path;

        public ResourceFile(ClassLoader loader, String path) {
            this.loader = loader;
            this.path = path;
        }

        @Override
        public boolean isFile() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getPath() {
            return this.path;
        }

        @Override
        public String getName() {
            return this.path.substring(this.path.lastIndexOf(47) + 1);
        }

        @Override
        public ResourceFile getParentFile() {
            int idx = this.path.lastIndexOf(47);
            if (idx < 1) {
                return null;
            }
            return new ResourceFile(this.loader, this.path.substring(0, idx));
        }

        @Override
        public ResourceFile getChildFile(String name) {
            return new ResourceFile(this.loader, this.path + "/" + name);
        }

        @Override
        public Source getSource() throws IOException {
            InputStream stream = this.loader.getResourceAsStream(this.path);
            return new InputLexerSource(stream, ResourceFileSystem.this.charset);
        }
    }
}

