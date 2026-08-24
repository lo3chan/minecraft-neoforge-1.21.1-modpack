package fuzs.eternalnether.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fuzs.puzzleslib.api.config.v3.json.GsonCodecHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;

public abstract class DataFixer<T> {
   public static final DataFixer<?> JSON = new DataFixer.JsonDataFixer();
   public static final DataFixer<?> NBT = new DataFixer.NbtDataFixer();
   static final Collection<DataFixer<?>> ALL = List.of(JSON, NBT);
   private final String fileExtension;

   DataFixer(String fileExtension) {
      this.fileExtension = fileExtension;
   }

   static void getAllFilesRecursive(File file, Consumer<File> fileOutput, Predicate<String> fileFilter) {
      File[] listedFiles = file.listFiles();
      if (listedFiles != null) {
         for (File listedFile : listedFiles) {
            if (listedFile.isDirectory()) {
               getAllFilesRecursive(listedFile, fileOutput, fileFilter);
            } else if (fileFilter.test(listedFile.getName())) {
               fileOutput.accept(listedFile);
            }
         }
      }
   }

   public static void updateAll(File inputDirectory, File outputDirectory, String targetValue, String replacementValue) {
      for (DataFixer<?> dataFixer : ALL) {
         dataFixer.update(inputDirectory, outputDirectory, targetValue, replacementValue);
      }
   }

   public final void update(File inputDirectory, File outputDirectory, String targetValue, String replacementValue) {
      List<File> files = new ArrayList<>();
      getAllFilesRecursive(inputDirectory, files::add, s -> s.endsWith("." + this.fileExtension));
      Iterator<File> iterator = files.iterator();

      while (iterator.hasNext()) {
         File inputFile = iterator.next();
         File outputFile = outputDirectory.toPath().resolve(inputDirectory.toPath().relativize(inputFile.toPath())).toFile();
         outputFile.getParentFile().mkdirs();

         try {
            T t = this.read(inputFile);
            this.write(outputFile, this.process(t, targetValue, replacementValue));
         } catch (IOException var10) {
            PuzzlesLib.LOGGER.warn("Failed handling {}", outputFile, var10);
            iterator.remove();
         }
      }
   }

   public abstract T process(T var1, String var2, String var3);

   abstract T read(File var1) throws IOException;

   abstract void write(File var1, T var2) throws IOException;

   private static class JsonDataFixer extends DataFixer<JsonElement> {
      JsonDataFixer() {
         super("json");
      }

      public JsonElement process(JsonElement jsonElement, String targetValue, String replacementValue) {
         if (jsonElement != null && !jsonElement.isJsonNull()) {
            if (jsonElement.isJsonPrimitive()) {
               JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
               if (jsonPrimitive.isString() && jsonPrimitive.getAsString().contains(targetValue)) {
                  return new JsonPrimitive(jsonPrimitive.getAsString().replace(targetValue, replacementValue));
               }

               return jsonElement;
            }

            if (jsonElement.isJsonArray()) {
               JsonArray jsonArray = jsonElement.getAsJsonArray();

               for (int i = 0; i < jsonArray.size(); i++) {
                  jsonArray.set(i, this.process(jsonArray.get(i), targetValue, replacementValue));
               }
            } else if (jsonElement.isJsonObject()) {
               JsonObject jsonObject = jsonElement.getAsJsonObject();

               for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                  entry.setValue(this.process(entry.getValue(), targetValue, replacementValue));
               }
            }
         }

         return jsonElement;
      }

      JsonElement read(File file) throws IOException {
         JsonElement var3;
         try (FileReader fileReader = new FileReader(file)) {
            var3 = (JsonElement)GsonHelper.fromJson(GsonCodecHelper.GSON, fileReader, JsonElement.class);
         }

         return var3;
      }

      void write(File file, JsonElement jsonElement) throws IOException {
         try (FileWriter fileWriter = new FileWriter(file)) {
            GsonCodecHelper.GSON.toJson(jsonElement, fileWriter);
         }
      }
   }

   private static class NbtDataFixer extends DataFixer<CompoundTag> {
      NbtDataFixer() {
         super("nbt");
      }

      public CompoundTag process(CompoundTag compoundTag, String targetValue, String replacementValue) {
         return this.processTag(compoundTag, targetValue, replacementValue);
      }

      private <T extends Tag> T processTag(T tag, String targetValue, String replacementValue) {
         if (tag != null) {
            if (tag instanceof StringTag stringTag) {
               if (stringTag.getAsString().contains(targetValue)) {
                  return (T)StringTag.valueOf(stringTag.getAsString().replace(targetValue, replacementValue));
               }
            } else if (tag instanceof ListTag listTag) {
               for (int i = 0; i < listTag.size(); i++) {
                  listTag.set(i, this.processTag(listTag.get(i), targetValue, replacementValue));
               }
            } else if (tag instanceof CompoundTag compoundTag) {
               for (String s : compoundTag.getAllKeys()) {
                  compoundTag.put(s, this.processTag(compoundTag.get(s), targetValue, replacementValue));
               }
            }
         }

         return tag;
      }

      CompoundTag read(File file) throws IOException {
         return NbtIo.readCompressed(file.toPath(), NbtAccounter.unlimitedHeap());
      }

      void write(File file, CompoundTag tag) throws IOException {
         NbtIo.writeCompressed(tag, file.toPath());
      }
   }
}
