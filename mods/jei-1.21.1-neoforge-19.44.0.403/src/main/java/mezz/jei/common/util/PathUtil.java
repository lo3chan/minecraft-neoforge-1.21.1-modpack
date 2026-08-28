/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.FileUtil
 */
package mezz.jei.common.util;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.FileUtil;

public class PathUtil {
    private static final String legacyUnsafeFileChars = "[^\\w-]";
    private static boolean atomicMoveSupported = true;

    public static String sanitizePathName(String filename) {
        String sanitized = FileUtil.sanitizeName((String)filename).trim();
        if (sanitized.isEmpty()) {
            return "_";
        }
        if (!FileUtil.isPathPortable((Path)Path.of(sanitized, new String[0]))) {
            return "_%s_".formatted(sanitized);
        }
        return sanitized;
    }

    public static String sanitizePathNameLegacy(String filename) {
        return String.join((CharSequence)"_", filename.split(legacyUnsafeFileChars));
    }

    public static void writeUsingTempFile(Path path, Iterable<? extends CharSequence> lines) throws IOException {
        Files.createDirectories(path.getParent(), new FileAttribute[0]);
        Path tempFile = Files.createTempFile(path.getParent(), null, null, new FileAttribute[0]);
        try {
            Files.write(tempFile, lines, new OpenOption[0]);
            PathUtil.moveAtomicReplace(tempFile, path);
        }
        finally {
            if (Files.exists(tempFile, new LinkOption[0])) {
                Files.delete(tempFile);
            }
        }
    }

    public static void moveAtomicReplace(Path source, Path target) throws IOException {
        if (atomicMoveSupported) {
            try {
                Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                return;
            }
            catch (AtomicMoveNotSupportedException ignored) {
                atomicMoveSupported = false;
            }
        }
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
}

