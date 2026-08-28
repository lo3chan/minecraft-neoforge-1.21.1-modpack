/*
 * Decompiled with CFR 0.152.
 */
package me.flashyreese.mods.sodiumextra.client.config;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;

public final class ConfigFileIO {
    private ConfigFileIO() {
    }

    public static void writeStringAtomically(Path path, String contents) throws IOException {
        ConfigFileIO.writeBytesAtomically(path, contents.getBytes(StandardCharsets.UTF_8));
    }

    public static void writeLinesAtomically(Path path, Iterable<String> lines) throws IOException {
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            builder.append(line).append(System.lineSeparator());
        }
        ConfigFileIO.writeStringAtomically(path, builder.toString());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void writeBytesAtomically(Path path, byte[] bytes) throws IOException {
        Path targetPath = path.toAbsolutePath();
        Path directory = targetPath.getParent();
        Path fileName = targetPath.getFileName();
        if (directory == null) throw new IOException("Path has no parent directory: " + String.valueOf(path));
        if (fileName == null) {
            throw new IOException("Path has no parent directory: " + String.valueOf(path));
        }
        Path tempPath = null;
        IOException failure = null;
        try {
            Files.createDirectories(directory, new FileAttribute[0]);
            tempPath = Files.createTempFile(directory, fileName.toString(), ".tmp", new FileAttribute[0]);
            ConfigFileIO.writeBytesToTempFile(tempPath, bytes);
            Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            tempPath = null;
            ConfigFileIO.forceDirectory(directory);
            if (tempPath == null) return;
        }
        catch (IOException e) {
            try {
                failure = e;
                throw e;
            }
            catch (Throwable throwable) {
                if (tempPath == null) throw throwable;
                try {
                    Files.deleteIfExists(tempPath);
                    throw throwable;
                }
                catch (IOException e2) {
                    if (failure == null) throw e2;
                    failure.addSuppressed(e2);
                    throw throwable;
                }
            }
        }
        try {
            Files.deleteIfExists(tempPath);
            return;
        }
        catch (IOException e) {
            if (failure == null) throw e;
            failure.addSuppressed(e);
            return;
        }
    }

    public static Path moveCorruptFile(Path path) throws IOException {
        Path corruptPath = ConfigFileIO.nextCorruptFilePath(path);
        Files.move(path, corruptPath, new CopyOption[0]);
        return corruptPath;
    }

    private static void writeBytesToTempFile(Path tempPath, byte[] bytes) throws IOException {
        try (FileChannel channel = FileChannel.open(tempPath, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);){
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            channel.force(true);
        }
    }

    private static Path nextCorruptFilePath(Path path) {
        Path basePath = path.resolveSibling(String.valueOf(path.getFileName()) + ".corrupt");
        if (!Files.exists(basePath, new LinkOption[0])) {
            return basePath;
        }
        int i = 1;
        Path candidate;
        while (Files.exists(candidate = path.resolveSibling(String.valueOf(path.getFileName()) + ".corrupt." + i), new LinkOption[0])) {
            ++i;
        }
        return candidate;
    }

    private static void forceDirectory(Path directory) {
        try (FileChannel channel = FileChannel.open(directory, StandardOpenOption.READ);){
            channel.force(true);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

