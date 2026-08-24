package pl.skidam.automodpack_loader_core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_loader_core.platforms.CurseForgeAPI;
import pl.skidam.automodpack_loader_core.platforms.ModrinthAPI;

public class FetchManager {
   private final Map<String, FetchManager.Datas> fetchDatas = new HashMap<>();
   private final Set<String> publiclyMatchedHashes = new HashSet<>();
   public int fetchesDone = 0;
   private CompletableFuture<Void> completableFuture;

   public FetchManager(List<FetchManager.FetchData> fetchDatas) {
      for (FetchManager.FetchData fetchData : fetchDatas) {
         this.fetchDatas.put(key(fetchData.sha1), new FetchManager.Datas(fetchData, new FetchManager.FetchedData(new ArrayList<>(), new ArrayList<>())));
      }
   }

   public void cancel() {
      this.completableFuture.cancel(true);
   }

   public void fetch() {
      Map<String, String> cf = new HashMap<>();
      List<String> mo = new ArrayList<>();

      for (Entry<String, FetchManager.Datas> entry : this.fetchDatas.entrySet()) {
         FetchManager.FetchData fetchData = entry.getValue().fetchData();
         if (fetchData.murmur != null && !fetchData.murmur.isBlank()) {
            cf.put(fetchData.sha1, fetchData.murmur);
         }

         mo.add(fetchData.sha1);
      }

      try {
         this.completableFuture = CompletableFuture.runAsync(() -> {
            this.fetchByMurmur(cf);
            this.fetchBySha1(mo);
         });
         this.completableFuture.join();
      } catch (CancellationException var6) {
         GlobalVariables.LOGGER.warn("Fetch canceled");
      } catch (Exception var7) {
         var7.printStackTrace();
      }
   }

   private void fetchBySha1(List<String> sha1s) {
      List<ModrinthAPI> modrinthFileInfos = ModrinthAPI.getModsInfosFromListOfSHA1(sha1s);
      if (modrinthFileInfos != null) {
         for (ModrinthAPI modrinthFileInfo : modrinthFileInfos) {
            String sha1 = modrinthFileInfo.SHA1Hash();
            FetchManager.Datas datas = this.fetchDatas.get(key(sha1));
            if (datas != null) {
               this.publiclyMatchedHashes.add(key(sha1));
               String mainPageUrl = ModrinthAPI.getMainPageUrl(modrinthFileInfo.modrinthID(), datas.fetchData.fileType);
               if (modrinthFileInfo.downloadUrl() != null && !modrinthFileInfo.downloadUrl().isBlank()) {
                  datas.fetchedData().urls().add(modrinthFileInfo.downloadUrl());
               }

               datas.fetchedData().mainPageUrls().add(mainPageUrl);
               this.fetchesDone++;
            }
         }
      }
   }

   private void fetchByMurmur(Map<String, String> hashes) {
      List<CurseForgeAPI> cfFileInfos = CurseForgeAPI.getModInfosFromFingerPrints(hashes);
      if (cfFileInfos != null) {
         for (CurseForgeAPI cfFileInfo : cfFileInfos) {
            String sha1 = cfFileInfo.sha1Hash();
            FetchManager.Datas datas = this.fetchDatas.get(key(sha1));
            if (datas != null) {
               this.publiclyMatchedHashes.add(key(sha1));
               if (cfFileInfo.downloadUrl() != null && !cfFileInfo.downloadUrl().isBlank()) {
                  datas.fetchedData().urls().add(cfFileInfo.downloadUrl());
               }

               this.fetchesDone++;
            }
         }
      }
   }

   public Map<String, FetchManager.Datas> getFetchDatas() {
      return this.fetchDatas;
   }

   public FetchManager.Datas getFetchData(String sha1) {
      return this.fetchDatas.get(key(sha1));
   }

   public boolean hasPublicMatch(String sha1) {
      return this.publiclyMatchedHashes.contains(key(sha1));
   }

   private static String key(String sha1) {
      return sha1 == null ? "" : sha1.toLowerCase(Locale.ROOT);
   }

   public record Datas(FetchManager.FetchData fetchData, FetchManager.FetchedData fetchedData) {
   }

   public record FetchData(String file, String sha1, String murmur, String fileSize, String fileType) {
   }

   public record FetchedData(List<String> urls, List<String> mainPageUrls) {
   }
}
