/*
 * Decompiled with CFR 0.152.
 */
package de.fabmax.physxjni;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public abstract class NativeLib {
    public abstract String getVersion();

    protected abstract List<String> getLibResourceNames();

    public void load() throws IOException {
        String defaultLibPath = new File(System.getProperty("java.io.tmpdir"), "de.fabmax.physx-jni" + File.separator + this.getVersion()).getAbsolutePath();
        String libTmpPath = System.getProperty("physxjni.nativeLibLocation", defaultLibPath);
        boolean loadFromResources = Boolean.parseBoolean(System.getProperty("physxjni.loadFromResources", "true"));
        File tempLibDir = new File(libTmpPath);
        if (tempLibDir.exists() && !tempLibDir.isDirectory() || !tempLibDir.exists() && !tempLibDir.mkdirs()) {
            throw new IllegalStateException("Failed creating native lib dir " + tempLibDir);
        }
        ArrayList<String> libFiles = new ArrayList<String>();
        for (String libResource : this.getLibResourceNames()) {
            File libTmpFile = new File(tempLibDir, new File(libResource).getName());
            if (loadFromResources) {
                InputStream libIn = this.getClass().getResourceAsStream(libResource);
                if (libIn == null) {
                    throw new IllegalStateException("Failed loading " + libResource + " from resources");
                }
                if (libTmpFile.exists() && !this.checkHash(libResource, libTmpFile) && !libTmpFile.delete()) {
                    throw new IllegalStateException("Failed deleting existing native lib file " + libTmpFile);
                }
                if (!libTmpFile.exists()) {
                    Files.copy(libIn, libTmpFile.toPath(), new CopyOption[0]);
                }
            }
            libFiles.add(libTmpFile.getAbsolutePath());
        }
        for (String libFile : libFiles) {
            System.load(libFile);
        }
    }

    private boolean checkHash(String resourceName, File file) {
        boolean isSameHash;
        block9: {
            isSameHash = false;
            try {
                InputStream hashIn = this.getClass().getResourceAsStream(resourceName + ".sha1");
                if (hashIn != null) {
                    String resourceHash;
                    String fileHash = this.makeFileHash(file);
                    try (BufferedReader r = new BufferedReader(new InputStreamReader(hashIn));){
                        resourceHash = r.readLine();
                    }
                    isSameHash = fileHash.equals(resourceHash);
                    break block9;
                }
                System.err.println("Failed to get signature for " + resourceName + " from resources");
            }
            catch (Exception e) {
                System.err.println("Error on signature check for " + resourceName + " / " + file);
                e.printStackTrace();
            }
        }
        return isSameHash;
    }

    private String makeFileHash(File file) throws Exception {
        try (FileInputStream inStream = new FileInputStream(file);){
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] buf = new byte[4096];
            int len = inStream.read(buf);
            while (len > 0) {
                md.update(buf, 0, len);
                len = inStream.read(buf);
            }
            byte[] hash = md.digest();
            StringBuilder hashStr = new StringBuilder();
            for (byte b : hash) {
                hashStr.append(String.format("%02x", b & 0xFF));
            }
            String string = hashStr.toString();
            return string;
        }
    }
}

