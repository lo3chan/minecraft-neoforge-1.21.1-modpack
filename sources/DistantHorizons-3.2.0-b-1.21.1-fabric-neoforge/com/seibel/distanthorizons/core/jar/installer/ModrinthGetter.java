package com.seibel.distanthorizons.core.jar.installer;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModrinthGetter {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final String ModrinthAPI = "https://api.modrinth.com/v2/project/";
   public static final String projectID = "distanthorizons";
   public static boolean initted = false;
   public static ArrayList<Config> projectRelease;
   public static Map<String, Config> idToJson = new HashMap<>();
   public static List<String> releaseID = new ArrayList<>();
   public static List<String> mcVersions = new ArrayList<>();
   public static Map<String, String> releaseNames = new HashMap<>();
   public static Map<String, List<String>> mcVerToReleaseID = new HashMap<>();
   public static Map<String, URL> downloadUrl = new HashMap<>();
   public static Map<String, String> changeLogs = new HashMap<>();

   public static boolean init() {
      try {
         initted = false;
         projectRelease = WebDownloader.parseWebJsonList("https://api.modrinth.com/v2/project/distanthorizons/version");

         for (Config currentRelease : projectRelease) {
            String workingID = currentRelease.get("id").toString();
            releaseID.add(workingID);
            idToJson.put(workingID, currentRelease);
            releaseNames.put(workingID, currentRelease.get("name").toString().replaceAll(" - 1\\..*", ""));
            changeLogs.put(workingID, currentRelease.get("changelog").toString());

            try {
               downloadUrl.put(workingID, new URL(currentRelease.<ArrayList<Config>>get("files").get(0).get("url").toString()));
            } catch (Exception var5) {
               LOGGER.error("Unable get modrinth version list, error: [" + var5.getMessage() + "]", var5);
            }

            for (String mcVer : (List)currentRelease.get("game_versions")) {
               if (!mcVersions.contains(mcVer)) {
                  mcVersions.add(mcVer);
                  mcVerToReleaseID.put(mcVer, new ArrayList<>());
               }

               mcVerToReleaseID.get(mcVer).add(workingID);
            }
         }

         Collections.sort(mcVersions);
         Collections.reverse(mcVersions);
         initted = true;
         return true;
      } catch (Exception var6) {
         LOGGER.error("Unable to set up Modrinth access, error: [" + var6.getMessage() + "]", var6);
         return false;
      }
   }

   public static String getLatestIDForVersion(String mcVer) {
      try {
         return mcVerToReleaseID.get(mcVer).get(0);
      } catch (Exception var2) {
         return null;
      }
   }

   public static String getLatestNameForVersion(String mcVer) {
      return releaseNames.get(mcVerToReleaseID.get(mcVer).get(0));
   }

   public static URL getLatestDownloadForVersion(String mcVer) {
      return downloadUrl.get(mcVerToReleaseID.get(mcVer).get(0));
   }

   public static String getLatestShaForVersion(String mcVer) {
      return idToJson.get(mcVerToReleaseID.get(mcVer).get(0)).<ArrayList<Config>>get("files").get(0).get("hashes.sha1").toString();
   }
}
