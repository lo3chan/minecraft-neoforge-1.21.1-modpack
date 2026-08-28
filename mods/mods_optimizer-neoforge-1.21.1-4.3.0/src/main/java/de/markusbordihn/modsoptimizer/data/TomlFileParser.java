/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.data;

import de.markusbordihn.modsoptimizer.Constants;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Toml;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class TomlFileParser {
    private static final String LOG_PREFIX = "[TOMLFileParser]";

    private TomlFileParser() {
    }

    public static Toml readTomlFile(JarFile jarFile, Path path) {
        return TomlFileParser.readTomlFile(jarFile, path, false);
    }

    public static Toml tryReadTomlFile(JarFile jarFile, Path path) {
        return TomlFileParser.readTomlFile(jarFile, path, true);
    }

    public static Toml readTomlFile(JarFile jarFile, Path path, boolean ignoreErrors) {
        block12: {
            String normalizedPath = TomlFileParser.normalizePath(path);
            ZipEntry modsFile = jarFile.getEntry(normalizedPath);
            if (modsFile != null && !modsFile.isDirectory()) {
                Toml toml;
                block11: {
                    InputStream inputStream = jarFile.getInputStream(modsFile);
                    try {
                        toml = new Toml().read(TomlFileParser.preprocessToml(inputStream, jarFile.getName()));
                        if (inputStream == null) break block11;
                    }
                    catch (Throwable throwable) {
                        try {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        catch (Exception e) {
                            if (!ignoreErrors) {
                                Constants.LOG.error("{} \u26a0\ufe0f Error reading TOML file {} from {}: {}", new Object[]{LOG_PREFIX, path, jarFile, e});
                            }
                            break block12;
                        }
                    }
                    inputStream.close();
                }
                return toml;
            }
            if (!ignoreErrors) {
                Constants.LOG.error("{} \u26a0\ufe0f TOML file {} not found in {}", new Object[]{LOG_PREFIX, normalizedPath, jarFile});
            }
        }
        return new Toml();
    }

    private static String normalizePath(Path path) {
        return path.toString().replace("\\", "/");
    }

    public static String preprocessToml(InputStream inputStream, String jarFileName) {
        return new BufferedReader(new InputStreamReader(inputStream)).lines().map(line -> {
            String key;
            String trimmed = line.trim();
            if (trimmed.startsWith("[") || trimmed.startsWith("#") || trimmed.isEmpty()) {
                return line;
            }
            int equalsIndex = trimmed.indexOf(61);
            if (!(equalsIndex <= 0 || !(key = trimmed.substring(0, equalsIndex).trim()).contains(".") || key.startsWith("\"") && key.endsWith("\""))) {
                String newKey = key.replace(".", "_");
                Constants.LOG.warn("{} \u26a0\ufe0f Found invalid key '{}' in {}", new Object[]{LOG_PREFIX, key, jarFileName});
                return line.replaceFirst(key, newKey);
            }
            return line;
        }).collect(Collectors.joining("\n"));
    }
}

