package pl.skidam.automodpack_core.config;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import pl.skidam.automodpack_core.auth.Secrets;

public class Jsons {
   public static class ClientConfigFieldsV1 {
      public int DO_NOT_CHANGE_IT = 1;
      public String selectedModpack = "";
      public Map<String, String> installedModpacks;
      public boolean selfUpdater = false;
   }

   public static class ClientConfigFieldsV2 {
      public int DO_NOT_CHANGE_IT = 2;
      public String selectedModpack = "";
      public Map<String, Jsons.ModpackAddresses> installedModpacks;
      public boolean updateSelectedModpackOnLaunch = true;
      public boolean selfUpdater = false;
      public boolean syncAutoModpackVersion = true;
      public boolean syncLoaderVersion = true;
      public boolean playMusic = true;
      public boolean allowRemoteNonModpackDeletions = true;
   }

   public static class ClientDeletedNonModpackFilesTimestamps {
      public Set<String> timestamps = ConcurrentHashMap.newKeySet();
   }

   public static class ClientDummyFiles {
      public Set<String> files = ConcurrentHashMap.newKeySet();
   }

   public static class KnownHostsFields {
      public Map<String, String> hosts;
   }

   public static class LocalMetadata {
      public Map<String, Jsons.LocalMetadata.FileFingerprint> files = new ConcurrentHashMap<>();

      public static class FileFingerprint {
         public final String sha1;
         public final long lastSize;
         public final long lastModified;

         public FileFingerprint(String sha1, long lastSize, long lastModified) {
            this.sha1 = sha1;
            this.lastSize = lastSize;
            this.lastModified = lastModified;
         }
      }
   }

   public static class ModpackAddresses {
      public InetSocketAddress hostAddress;
      public InetSocketAddress serverAddress;
      public boolean requiresMagic;

      public ModpackAddresses() {
      }

      public ModpackAddresses(InetSocketAddress hostAddress, InetSocketAddress serverAddress, boolean requiresMagic) {
         this.hostAddress = hostAddress;
         this.serverAddress = serverAddress;
         this.requiresMagic = requiresMagic;
      }

      public boolean isAnyEmpty() {
         return this.hostAddress == null
            || this.serverAddress == null
            || this.hostAddress.getHostString().isBlank()
            || this.serverAddress.getHostString().isBlank();
      }
   }

   public static class ModpackContentFields {
      public String modpackName = "";
      public String automodpackVersion = "";
      public String loader = "";
      public String loaderVersion = "";
      public String mcVersion = "";
      public Set<Jsons.ModpackContentFields.ModpackContentItem> list;
      public Set<Jsons.ModpackContentFields.FileToDelete> nonModpackFilesToDelete = Set.of();

      public ModpackContentFields(Set<Jsons.ModpackContentFields.ModpackContentItem> list) {
         this.list = list;
      }

      public ModpackContentFields() {
         this.list = Set.of();
      }

      public static class FileToDelete {
         public final String file;
         public final String sha1;
         public final String timestamp;

         public FileToDelete(String file, String sha1, String timestamp) {
            this.file = file;
            this.sha1 = sha1;
            this.timestamp = timestamp;
         }
      }

      public static class ModpackContentItem {
         public final String file;
         public final String size;
         public final String type;
         public final boolean editable;
         public final boolean forceCopy;
         public final String sha1;
         public final String murmur;

         public ModpackContentItem(String file, String size, String type, boolean editable, boolean forceCopy, String sha1, String murmur) {
            this.file = file;
            this.size = size;
            this.type = type;
            this.editable = editable;
            this.forceCopy = forceCopy;
            this.sha1 = sha1;
            this.murmur = murmur;
         }

         @Override
         public String toString() {
            return String.format(
               "ModpackContentItems(file=%s, size=%s, type=%s, editable=%s, sha1=%s, murmur=%s)",
               this.file,
               this.size,
               this.type,
               this.editable,
               this.sha1,
               this.murmur
            );
         }

         @Override
         public boolean equals(Object obj) {
            if (this == obj) {
               return true;
            } else if (obj != null && this.getClass() == obj.getClass()) {
               Jsons.ModpackContentFields.ModpackContentItem that = (Jsons.ModpackContentFields.ModpackContentItem)obj;
               return Objects.equals(this.file, that.file);
            } else {
               return false;
            }
         }

         @Override
         public int hashCode() {
            return Objects.hash(this.file);
         }
      }
   }

   public static class SecretsFields {
      public Map<String, Secrets.Secret> secrets = new HashMap<>();
   }

   public static class ServerConfigFieldsV1 {
      public int DO_NOT_CHANGE_IT = 1;
      public String modpackName = "";
      public boolean modpackHost = true;
      public boolean generateModpackOnStart = true;
      public List<String> syncedFiles = List.of("/mods/*.jar", "/kubejs/**", "!/kubejs/server_scripts/**", "/emotes/*");
      public List<String> allowEditsInFiles = List.of("/options.txt", "/config/**");
      public boolean autoExcludeUnnecessaryFiles = true;
      public boolean requireAutoModpackOnClient = true;
      public boolean nagUnModdedClients = true;
      public String nagMessage = "This server provides dedicated modpack through AutoModpack!";
      public String nagClickableMessage = "Click here to get the AutoModpack!";
      public String nagClickableLink = "https://modrinth.com/project/automodpack";
      public boolean autoExcludeServerSideMods = true;
      public boolean hostModpackOnMinecraftPort = true;
      public String hostIp = "";
      public String hostLocalIp = "";
      public boolean updateIpsOnEveryStart = false;
      public int hostPort = -1;
      public boolean reverseProxy = false;
      public int bandwidthLimit = 0;
      public long secretLifetime = 336L;
      public boolean validateSecrets = true;
      public boolean selfUpdater = false;
      public List<String> acceptedLoaders;
   }

   public static class ServerConfigFieldsV2 {
      public int DO_NOT_CHANGE_IT = 2;
      public String modpackName = "";
      public boolean modpackHost = true;
      public boolean generateModpackOnStart = true;
      public Set<String> syncedFiles = Set.of("/mods/*.jar", "/kubejs/**", "!/kubejs/server_scripts/**", "/emotes/*");
      public Set<String> allowEditsInFiles = Set.of("/options.txt", "/config/**");
      public Set<String> forceCopyFilesToStandardLocation = Set.of();
      public Map<String, String> nonModpackFilesToDelete = Map.of();
      public boolean autoExcludeServerSideMods = true;
      public boolean autoExcludeUnnecessaryFiles = true;
      public boolean requireAutoModpackOnClient = true;
      public boolean nagUnModdedClients = true;
      public String nagMessage = "This server provides dedicated modpack through AutoModpack!";
      public String nagClickableMessage = "Click here to get the AutoModpack!";
      public String nagClickableLink = "https://modrinth.com/project/automodpack";
      public String bindAddress = "";
      public int bindPort = -1;
      public String addressToSend = "";
      public int portToSend = -1;
      public boolean disableInternalTLS = false;
      public boolean requireMagicPackets = false;
      public boolean updateIpsOnEveryStart = false;
      public int bandwidthLimit = 0;
      public boolean validateSecrets = true;
      public long secretLifetime = 336L;
      public boolean selfUpdater = false;
      public Set<String> acceptedLoaders = new HashSet<>();

      public static class FileToDelete {
         public final String file;
         public final String sha1;

         public FileToDelete(String file, String sha1) {
            this.file = file;
            this.sha1 = sha1;
         }
      }
   }

   public static class ServerCoreConfigFields {
      public String automodpackVersion = "";
      public String loader = "";
      public String loaderVersion = "";
      public String mcVersion = "";
   }

   public static class VersionConfigField {
      public int DO_NOT_CHANGE_IT = 0;
   }
}
