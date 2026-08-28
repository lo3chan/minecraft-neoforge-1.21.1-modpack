/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$Error
 *  com.mojang.serialization.DynamicOps
 *  javax.annotation.Nullable
 */
package mezz.jei.common.config.file;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import mezz.jei.common.config.file.JsonArrayWriter;
import mezz.jei.common.util.PathUtil;

public class JsonArrayFileHelper {
    private JsonArrayFileHelper() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> void write(Path path, int version, Collection<T> elements, Codec<T> codec, DynamicOps<JsonElement> registryOps, Consumer<? super DataResult.Error<JsonElement>> ifElementError, BiConsumer<T, RuntimeException> ifElementException) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent, new FileAttribute[0]);
        }
        Path tempFile = parent == null ? Files.createTempFile(null, null, new FileAttribute[0]) : Files.createTempFile(parent, null, null, new FileAttribute[0]);
        try {
            try (BufferedWriter out = Files.newBufferedWriter(tempFile, new OpenOption[0]);){
                JsonArrayFileHelper.writeToWriter(out, version, elements, codec, registryOps, ifElementError, ifElementException);
            }
            PathUtil.moveAtomicReplace(tempFile, path);
        }
        finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private static <T> void writeToWriter(BufferedWriter out, int version, Collection<T> elements, Codec<T> codec, DynamicOps<JsonElement> registryOps, Consumer<? super DataResult.Error<JsonElement>> ifElementError, BiConsumer<T, RuntimeException> ifElementException) throws IOException {
        JsonArrayWriter writer = JsonArrayWriter.start(out);
        JsonObject versionElement = new JsonObject();
        versionElement.addProperty("version", (Number)version);
        writer.add((JsonElement)versionElement);
        for (T element : elements) {
            try {
                DataResult dataResult = codec.encodeStart(registryOps, element);
                dataResult.ifError(ifElementError);
                Optional resultOpt = dataResult.result();
                if (!resultOpt.isPresent()) continue;
                JsonElement jsonElement = (JsonElement)resultOpt.get();
                writer.add(jsonElement);
            }
            catch (RuntimeException e) {
                ifElementException.accept(element, e);
            }
        }
        writer.end();
    }

    @Nullable
    private static Integer getVersion(JsonElement firstElement) {
        if (!firstElement.isJsonObject()) {
            return null;
        }
        JsonElement versionElement = firstElement.getAsJsonObject().get("version");
        if (versionElement.isJsonPrimitive()) {
            try {
                return versionElement.getAsInt();
            }
            catch (NumberFormatException | UnsupportedOperationException ignored) {
                return null;
            }
        }
        return null;
    }

    public static <T> List<T> read(BufferedReader reader, @Nullable Integer version, Codec<T> codec, DynamicOps<JsonElement> registryOps, BiConsumer<JsonElement, ? super DataResult.Error<Pair<T, JsonElement>>> ifElementError, BiConsumer<JsonElement, RuntimeException> ifElementException) throws JsonIOException, JsonSyntaxException {
        ArrayList<Object> results = new ArrayList<Object>();
        JsonElement jsonElement = JsonParser.parseReader((Reader)reader);
        if (!jsonElement.isJsonArray()) {
            throw new JsonSyntaxException("Expected an array but got :" + String.valueOf(jsonElement));
        }
        boolean versionFound = version == null;
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        for (JsonElement element : jsonArray) {
            if (!versionFound) {
                Integer foundVersion = JsonArrayFileHelper.getVersion(element);
                if (!version.equals(foundVersion)) {
                    return List.of();
                }
                versionFound = true;
                continue;
            }
            try {
                DataResult dataResult = codec.decode(registryOps, (Object)element);
                dataResult.ifError(error -> ifElementError.accept(element, (Object)error));
                Optional resultOpt = dataResult.result();
                if (!resultOpt.isPresent()) continue;
                Object value = ((Pair)resultOpt.get()).getFirst();
                results.add(value);
            }
            catch (RuntimeException e) {
                ifElementException.accept(element, e);
            }
        }
        return results;
    }
}

