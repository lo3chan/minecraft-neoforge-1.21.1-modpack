package pl.skidam.automodpack_core.auth;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.config.ConfigTools;
import pl.skidam.automodpack_core.config.Jsons;

public class SecretsStore {
   private static final SecretsStore.SecretsCache hostSecrets = new SecretsStore.SecretsCache(GlobalVariables.serverSecretsFile);
   private static final SecretsStore.SecretsCache clientSecrets = new SecretsStore.SecretsCache(GlobalVariables.clientSecretsFile);

   public static Entry<String, Secrets.Secret> getHostSecret(String secret) {
      hostSecrets.load();

      for (Entry<String, Secrets.Secret> entry : hostSecrets.cache.entrySet()) {
         String thisSecret = entry.getValue().secret();
         if (Objects.equals(thisSecret, secret)) {
            return entry;
         }
      }

      return null;
   }

   public static void saveHostSecret(String uuid, Secrets.Secret secret) {
      hostSecrets.save(uuid, secret);
   }

   public static Secrets.Secret getClientSecret(String modpack) {
      return clientSecrets.get(modpack);
   }

   public static void saveClientSecret(String modpack, Secrets.Secret secret) throws IllegalArgumentException {
      clientSecrets.save(modpack, secret);
   }

   private static class SecretsCache {
      private final ConcurrentMap<String, Secrets.Secret> cache;
      private Jsons.SecretsFields db;
      private final Path configFile;

      public SecretsCache(Path configFile) {
         this.configFile = configFile;
         this.cache = new ConcurrentHashMap<>();
      }

      public synchronized void load() {
         if (this.db == null) {
            this.db = ConfigTools.load(this.configFile, Jsons.SecretsFields.class);
            if (this.db != null && this.db.secrets != null && !this.db.secrets.isEmpty()) {
               this.cache.putAll(this.db.secrets);
            }
         }
      }

      public synchronized void save() {
         ConfigTools.save(this.configFile, this.db);
      }

      public Secrets.Secret get(String key) {
         this.load();
         return this.cache.get(key);
      }

      public void save(String key, Secrets.Secret secret) throws IllegalArgumentException {
         if (key != null && !key.isBlank() && secret != null && !secret.secret().isBlank()) {
            this.load();
            this.cache.put(key, secret);
            if (this.db == null) {
               this.db = new Jsons.SecretsFields();
            }

            this.db.secrets.put(key, secret);
            this.save();
         } else {
            throw new IllegalArgumentException("Key or secret cannot be null or blank");
         }
      }
   }
}
