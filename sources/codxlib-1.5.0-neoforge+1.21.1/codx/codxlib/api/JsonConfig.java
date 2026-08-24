package codx.codxlib.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.function.Supplier;

public final class JsonConfig<T> {
   private static final Gson DEFAULT_GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   private final Path path;
   private final Class<T> type;
   private final Supplier<T> defaults;
   private final Gson gson;
   private volatile T value;

   private JsonConfig(Path path, Class<T> type, Supplier<T> defaults, Gson gson) {
      this.path = path;
      this.type = type;
      this.defaults = defaults;
      this.gson = gson;
   }

   public static <T> JsonConfig<T> forMod(String modName, Class<T> type, Supplier<T> defaults) {
      return of("codxlib." + modName + ".json", type, defaults, DEFAULT_GSON);
   }

   public static <T> JsonConfig<T> forMod(String modName, Class<T> type, Supplier<T> defaults, Gson gson) {
      return of("codxlib." + modName + ".json", type, defaults, gson);
   }

   public static <T> JsonConfig<T> of(String fileName, Class<T> type, Supplier<T> defaults) {
      return of(fileName, type, defaults, DEFAULT_GSON);
   }

   public static <T> JsonConfig<T> of(String fileName, Class<T> type, Supplier<T> defaults, Gson gson) {
      Objects.requireNonNull(fileName, "fileName");
      Objects.requireNonNull(type, "type");
      Objects.requireNonNull(defaults, "defaults");
      Objects.requireNonNull(gson, "gson");
      Path path = CodxLib.configDir().resolve(fileName);
      JsonConfig<T> config = new JsonConfig<>(path, type, defaults, gson);
      config.load();
      return config;
   }

   public T get() {
      return this.value;
   }

   public synchronized void set(T newValue) {
      this.value = Objects.requireNonNull(newValue, "newValue");
   }

   public Path path() {
      return this.path;
   }

   public synchronized void load() {
      if (Files.exists(this.path)) {
         try (Reader reader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
            T loaded = (T)this.gson.fromJson(reader, this.type);
            if (loaded != null) {
               this.value = loaded;
               return;
            }
         } catch (JsonParseException | IOException var6) {
            this.backupCorrupt();
         }
      }

      this.value = this.defaults.get();
      this.save();
   }

   public synchronized void save() {
      if (this.value != null) {
         try {
            Path parent = this.path.getParent();
            if (parent != null) {
               Files.createDirectories(parent);
            }

            Path tmp = this.path.resolveSibling(this.path.getFileName() + ".tmp");

            try (Writer writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
               this.gson.toJson(this.value, writer);
            }

            try {
               Files.move(tmp, this.path, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException var7) {
               Files.move(tmp, this.path, StandardCopyOption.REPLACE_EXISTING);
            }
         } catch (IOException var9) {
            System.err.println("[CodxLib] Failed to save config " + this.path + ": " + var9.getMessage());
         }
      }
   }

   private void backupCorrupt() {
      try {
         Path backup = this.path.resolveSibling(this.path.getFileName() + ".corrupt-" + System.currentTimeMillis());
         Files.move(this.path, backup, StandardCopyOption.REPLACE_EXISTING);
         System.err.println("[CodxLib] Corrupt config " + this.path.getFileName() + " backed up to " + backup.getFileName() + "; using defaults.");
      } catch (IOException var2) {
      }
   }
}
