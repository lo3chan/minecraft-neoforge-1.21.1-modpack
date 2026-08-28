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

public class ChrootFileSystem
implements VirtualFileSystem {
    private File root;

    public ChrootFileSystem(File root) {
        this.root = root;
    }

    @Override
    public VirtualFile getFile(String path) {
        return new ChrootFile(path);
    }

    @Override
    public VirtualFile getFile(String dir, String name) {
        return new ChrootFile(dir, name);
    }

    private class ChrootFile
    extends File
    implements VirtualFile {
        private File rfile;

        public ChrootFile(String path) {
            super(path);
        }

        public ChrootFile(String dir, String name) {
            super(dir, name);
        }

        public ChrootFile(File dir, String name) {
            super(dir, name);
        }

        @Override
        public ChrootFile getParentFile() {
            return new ChrootFile(this.getParent());
        }

        @Override
        public ChrootFile getChildFile(String name) {
            return new ChrootFile(this, name);
        }

        @Override
        public boolean isFile() {
            File real = new File(ChrootFileSystem.this.root, this.getPath());
            return real.isFile();
        }

        @Override
        public Source getSource() throws IOException {
            return new FileLexerSource(new File(ChrootFileSystem.this.root, this.getPath()), this.getPath());
        }
    }
}

