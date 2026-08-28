/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonSyntaxException
 *  com.google.gson.stream.JsonReader
 */
package de.markusbordihn.modsoptimizer.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import de.markusbordihn.modsoptimizer.Constants;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JsonFileParser {
    private JsonFileParser() {
    }

    public static JsonObject readJsonFile(JarFile jarFile, Path path) {
        ZipEntry modsFile = jarFile.getEntry(path.toString().replace("\\", "/"));
        if (modsFile != null && !modsFile.isDirectory()) {
            JsonObject jsonObject;
            block10: {
                InputStream inputStream = jarFile.getInputStream(modsFile);
                try {
                    jsonObject = JsonFileParser.parseJson(inputStream, path, jarFile);
                    if (inputStream == null) break block10;
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
                        Constants.LOG.error("Error reading json file {} from {}: {}", new Object[]{path, jarFile, e});
                    }
                }
                inputStream.close();
            }
            return jsonObject;
        } else {
            Constants.LOG.error("Json file {} not found in {}", (Object)path.toString().replace("\\", "/"), (Object)jarFile);
        }
        return new JsonObject();
    }

    private static JsonObject parseJson(InputStream inputStream, Path path, JarFile jarFile) {
        try {
            JsonElement jsonElement = JsonParser.parseReader((Reader)new InputStreamReader(inputStream));
            return jsonElement.getAsJsonObject();
        }
        catch (JsonSyntaxException e) {
            Constants.LOG.warn("Invalid json file {} from {}:", new Object[]{path, jarFile, e});
            return JsonFileParser.tryParsingWithLenient(inputStream, path, jarFile);
        }
        catch (Exception e) {
            Constants.LOG.error("Error parsing json file {} from {}: {}", new Object[]{path, jarFile, e});
            return new JsonObject();
        }
    }

    private static JsonObject tryParsingWithLenient(InputStream inputStream, Path path, JarFile jarFile) {
        try {
            JsonReader reader = new JsonReader((Reader)new InputStreamReader(inputStream));
            reader.setLenient(true);
            JsonElement jsonElement = JsonParser.parseReader((JsonReader)reader);
            return jsonElement.getAsJsonObject();
        }
        catch (Exception e) {
            Constants.LOG.error("Unable to parse invalid json file {} from {}: {}", new Object[]{path, jarFile, e});
            return new JsonObject();
        }
    }
}

