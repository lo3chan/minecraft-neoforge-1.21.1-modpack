package codx.codxlib.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public record ModInfo(String modId, String modrinthSlug, String version, String chatPrefix, String curseforgeSlug, int curseforgeProjectId) {
   private static final Map<String, ModInfo.CurseForgeId> KNOWN_CURSEFORGE = Map.of(
      "codxlib",
      new ModInfo.CurseForgeId("codxlib", 1633207),
      "oneblock",
      new ModInfo.CurseForgeId("theoneblock", 1633271),
      "onedimension",
      new ModInfo.CurseForgeId("one-dimension", 1633224),
      "cleanhud",
      new ModInfo.CurseForgeId("cleanhud", 1644758),
      "alexsmobs",
      new ModInfo.CurseForgeId("alexs-mobs-continued", 1635121)
   );

   public ModInfo(String modId, String modrinthSlug, String version, String chatPrefix, String curseforgeSlug, int curseforgeProjectId) {
      Objects.requireNonNull(modId, "modId");
      Objects.requireNonNull(version, "version");
      if (modrinthSlug == null || modrinthSlug.isBlank()) {
         modrinthSlug = modId;
      }

      if (chatPrefix == null || chatPrefix.isBlank()) {
         chatPrefix = "[" + modId + "]";
      }

      if (curseforgeSlug != null && curseforgeSlug.isBlank()) {
         curseforgeSlug = null;
      }

      ModInfo.CurseForgeId known = KNOWN_CURSEFORGE.get(modId.toLowerCase(Locale.ROOT));
      if (known != null) {
         if (curseforgeSlug == null) {
            curseforgeSlug = known.slug();
         }

         if (curseforgeProjectId <= 0) {
            curseforgeProjectId = known.projectId();
         }
      }

      if (curseforgeProjectId < 0) {
         curseforgeProjectId = 0;
      }

      this.modId = modId;
      this.modrinthSlug = modrinthSlug;
      this.version = version;
      this.chatPrefix = chatPrefix;
      this.curseforgeSlug = curseforgeSlug;
      this.curseforgeProjectId = curseforgeProjectId;
   }

   public ModInfo(String modId, String modrinthSlug, String version, String chatPrefix) {
      this(modId, modrinthSlug, version, chatPrefix, null, 0);
   }

   public static ModInfo of(String modId, String modrinthSlug, String version) {
      return new ModInfo(modId, modrinthSlug, version, "[" + modId + "]", null, 0);
   }

   public static ModInfo of(String modId, String modrinthSlug, String curseforgeSlug, int curseforgeProjectId, String version) {
      return new ModInfo(modId, modrinthSlug, version, "[" + modId + "]", curseforgeSlug, curseforgeProjectId);
   }

   public ModInfo withCurseForge(String slug, int projectId) {
      return new ModInfo(this.modId, this.modrinthSlug, this.version, this.chatPrefix, slug, projectId);
   }

   public boolean hasCurseForge() {
      return this.curseforgeSlug != null;
   }

   public boolean canQueryCurseForge() {
      return this.curseforgeProjectId > 0;
   }

   public String modrinthUrl() {
      return "https://modrinth.com/mod/" + this.modrinthSlug;
   }

   public String curseforgeUrl() {
      return this.curseforgeSlug != null
         ? "https://www.curseforge.com/minecraft/mc-mods/" + this.curseforgeSlug
         : "https://www.curseforge.com/minecraft/search?class=mc-mods&search=" + URLEncoder.encode(this.modId, StandardCharsets.UTF_8);
   }

   private record CurseForgeId(String slug, int projectId) {
   }
}
