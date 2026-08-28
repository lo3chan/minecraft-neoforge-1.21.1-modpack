/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.util.cpp;

import java.io.File;
import java.io.IOException;
import net.diebuddies.util.cpp.FileLexerSource;
import net.diebuddies.util.cpp.Source;
import net.diebuddies.util.cpp.VirtualFile;
import net.diebuddies.util.cpp.VirtualFileSystem;

public class JavaFileSystem
implements VirtualFileSystem {
    @Override
    public VirtualFile getFile(String path) {
        return new JavaFile(path);
    }

    @Override
    public VirtualFile getFile(String dir, String name) {
        return new JavaFile(dir, name);
    }

    private class JavaFile
    extends File
    implements VirtualFile {
        public JavaFile(String path) {
            super(path);
        }

        public JavaFile(String dir, String name) {
            super(dir, name);
        }

        public JavaFile(File dir, String name) {
            super(dir, name);
        }

        @Override
        public JavaFile getParentFile() {
            String parent = this.getParent();
            if (parent != null) {
                return new JavaFile(parent);
            }
            File absolute = this.getAbsoluteFile();
            parent = absolute.getParent();
            return new JavaFile(parent);
        }

        @Override
        public JavaFile getChildFile(String name) {
            return new JavaFile(this, name);
        }

        @Override
        public Source getSource() throws IOException {
            return new FileLexerSource(this);
        }
    }
}

