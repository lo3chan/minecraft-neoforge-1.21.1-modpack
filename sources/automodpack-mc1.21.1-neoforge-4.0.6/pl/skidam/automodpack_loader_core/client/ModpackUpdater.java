package pl.skidam.automodpack_loader_core.client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.auth.Secrets;
import pl.skidam.automodpack_core.auth.SecretsStore;
import pl.skidam.automodpack_core.config.ConfigTools;
import pl.skidam.automodpack_core.config.Jsons;
import pl.skidam.automodpack_core.protocol.DownloadClient;
import pl.skidam.automodpack_core.utils.ClientCacheUtils;
import pl.skidam.automodpack_core.utils.CustomFileUtils;
import pl.skidam.automodpack_core.utils.FileInspection;
import pl.skidam.automodpack_core.utils.WorkaroundUtil;
import pl.skidam.automodpack_core.utils.launchers.LauncherVersionSwapper;
import pl.skidam.automodpack_loader_core.ReLauncher;
import pl.skidam.automodpack_loader_core.screen.ScreenManager;
import pl.skidam.automodpack_loader_core.utils.DownloadManager;
import pl.skidam.automodpack_loader_core.utils.FetchManager;
import pl.skidam.automodpack_loader_core.utils.UpdateType;

public class ModpackUpdater {
   public Changelogs changelogs = new Changelogs();
   public DownloadManager downloadManager;
   public FetchManager fetchManager;
   public long totalBytesToDownload = 0L;
   public boolean fullDownload = false;
   private Jsons.ModpackContentFields serverModpackContent;
   private String serverModpackContentJson;
   public Map<Jsons.ModpackContentFields.ModpackContentItem, List<String>> failedDownloads = new HashMap<>();
   private final Set<String> newDownloadedFiles = new HashSet<>();
   private Set<String> preparedFiles = Set.of();
   private Set<Jsons.ModpackContentFields.ModpackContentItem> unverifiedJarFiles = Set.of();
   private final Jsons.ModpackAddresses modpackAddresses;
   private final Secrets.Secret modpackSecret;
   private Path modpackDir;
   private Path modpackContentFile;

   public String getModpackName() {
      return this.serverModpackContent.modpackName;
   }

   public Set<Jsons.ModpackContentFields.ModpackContentItem> getModpackFileList() {
      return this.serverModpackContent.list;
   }

   public Set<Jsons.ModpackContentFields.ModpackContentItem> getUnverifiedJarFiles() {
      return this.unverifiedJarFiles;
   }

   public ModpackUpdater(Jsons.ModpackContentFields modpackContent, Jsons.ModpackAddresses modpackAddresses, Secrets.Secret secret, Path modpackPath) {
      this.serverModpackContent = modpackContent;
      this.modpackAddresses = modpackAddresses;
      this.modpackSecret = secret;
      this.modpackDir = modpackPath;
      if (this.modpackAddresses == null || this.modpackAddresses.isAnyEmpty()) {
         throw new IllegalArgumentException("modpackAddresses is null or empty");
      }
   }

   public void processModpackUpdate(ModpackUtils.UpdateCheckResult result) {
      try {
         Object parentScreen = new ScreenManager().getScreen().orElse(null);
         this.modpackContentFile = this.modpackDir.resolve(GlobalVariables.hostModpackContentFile.getFileName());
         if (this.serverModpackContent == null) {
            this.CheckAndLoadModpack();
            return;
         }

         this.serverModpackContentJson = ConfigTools.GSON.toJson(this.serverModpackContent);
         if (!Files.exists(this.modpackDir)) {
            Files.createDirectories(this.modpackDir);
         }

         if (!Files.exists(this.modpackContentFile)) {
            this.prepareFetchData(this.serverModpackContent.list);
            if (GlobalVariables.preload) {
               if (this.hasUnverifiedJars()) {
                  this.abortPreloadUpdate();
               } else {
                  this.startUpdate(this.serverModpackContent.list);
               }
            } else {
               this.fullDownload = true;
               this.showDangerScreen(parentScreen, this.serverModpackContent.list);
            }
         } else {
            this.modpackDir = ModpackUtils.renameModpackDir(this.serverModpackContent, this.modpackDir);
            this.modpackContentFile = this.modpackDir.resolve(this.modpackContentFile.getFileName());
            if (result == null) {
               result = ModpackUtils.isUpdate(this.serverModpackContent, this.modpackDir);
            }

            if (result.requiresUpdate()) {
               Set<Jsons.ModpackContentFields.ModpackContentItem> filesToUpdate = result.filesToUpdate();
               this.prepareFetchData(filesToUpdate);
               if (GlobalVariables.preload && this.hasUnverifiedJars()) {
                  this.abortPreloadUpdate();
               } else if (this.hasUnverifiedJars()) {
                  this.showDangerScreen(parentScreen, filesToUpdate);
               } else {
                  this.startUpdate(filesToUpdate);
               }
            } else {
               Files.writeString(this.modpackContentFile, this.serverModpackContentJson);
               this.CheckAndLoadModpack();
            }
         }
      } catch (Exception var4) {
         GlobalVariables.LOGGER.error("Error while initializing modpack updater", var4);
      }
   }

   private void showDangerScreen(Object parentScreen, Set<Jsons.ModpackContentFields.ModpackContentItem> filesToUpdate) {
      new ScreenManager().danger(parentScreen, this, filesToUpdate);
   }

   private boolean hasUnverifiedJars() {
      return !this.unverifiedJarFiles.isEmpty();
   }

   private void abortPreloadUpdate() {
      GlobalVariables.LOGGER
         .warn("Preload update stopped because it contains {} JAR file(s) without a public Modrinth or CurseForge match", this.unverifiedJarFiles.size());

      try {
         if (Files.exists(this.modpackContentFile)) {
            this.CheckAndLoadModpack();
         } else {
            GlobalVariables.LOGGER.warn("No existing modpack is available to load after stopping the preload update");
         }
      } catch (Exception var2) {
         GlobalVariables.LOGGER.error("Failed to load the current modpack after stopping the preload update", var2);
      }
   }

   private void prepareFetchData(Set<Jsons.ModpackContentFields.ModpackContentItem> filesToCheck) {
      long start = System.currentTimeMillis();
      Set<String> files = filesToCheck.stream().map(itemx -> itemx.file + "\u0000" + itemx.sha1).collect(Collectors.toUnmodifiableSet());
      if (!files.equals(this.preparedFiles) || this.fetchManager == null) {
         List<FetchManager.FetchData> fetchDatas = new LinkedList<>();

         for (Jsons.ModpackContentFields.ModpackContentItem item : filesToCheck) {
            String fileType = isJar(item.file) ? "mod" : item.type;
            if (isJar(item.file) || fileType.equals("mod") || fileType.equals("shader") || fileType.equals("resourcepack")) {
               fetchDatas.add(new FetchManager.FetchData(item.file, item.sha1, item.murmur, item.size, fileType));
            }
         }

         this.fetchManager = new FetchManager(fetchDatas);
         this.preparedFiles = files;
         this.unverifiedJarFiles = Set.of();
         if (!fetchDatas.isEmpty()) {
            new ScreenManager().fetch(this.fetchManager);
            this.fetchManager.fetch();
            this.unverifiedJarFiles = filesToCheck.stream()
               .filter(itemx -> isJar(itemx.file) && !this.fetchManager.hasPublicMatch(itemx.sha1))
               .collect(Collectors.toUnmodifiableSet());
         }

         GlobalVariables.LOGGER
            .info("Finished checking public file matches in {}ms ({} matches)", System.currentTimeMillis() - start, this.fetchManager.fetchesDone);
      }
   }

   private static boolean isJar(String file) {
      return file != null && file.toLowerCase(Locale.ROOT).endsWith(".jar");
   }

   private void CheckAndLoadModpack() throws Exception {
      if (Files.exists(this.modpackDir)) {
         boolean requiresRestart = this.applyModpack();
         if (requiresRestart) {
            GlobalVariables.LOGGER.info("Modpack is not loaded");
            ClientCacheUtils.saveMetadataCache();
            UpdateType updateType = this.fullDownload ? UpdateType.FULL : UpdateType.UPDATE;
            new ReLauncher(this.modpackDir, updateType, this.changelogs).restart(true);
         } else if (GlobalVariables.preload) {
            List<Path> modpackMods = List.of();

            List<String> standardModsHashes;
            try (Stream<Path> standardModsStream = Files.list(GlobalVariables.MODS_DIR)) {
               standardModsHashes = standardModsStream.map(ClientCacheUtils::computeHashIfNeeded).filter(Objects::nonNull).toList();
            }

            Path modpackModsDir = this.modpackDir.resolve("mods");
            if (Files.exists(modpackModsDir)) {
               try (Stream<Path> modpackModsStream = Files.list(modpackModsDir)) {
                  modpackMods = modpackModsStream.filter(mod -> {
                     String modHash = ClientCacheUtils.computeHashIfNeeded(mod);
                     boolean isUnique = standardModsHashes.stream().noneMatch(hash -> hash.equals(modHash));
                     boolean endsWithJar = mod.toString().endsWith(".jar");
                     boolean isFile = mod.toFile().isFile();
                     return isUnique && endsWithJar && isFile;
                  }).toList();
               }
            }

            GlobalVariables.MODPACK_LOADER.loadModpack(modpackMods);
         } else {
            ClientCacheUtils.saveMetadataCache();
            GlobalVariables.LOGGER.info("Modpack is already loaded");
         }
      }
   }

   public void startUpdate(Set<Jsons.ModpackContentFields.ModpackContentItem> filesToUpdate) {
      if (this.modpackSecret == null) {
         GlobalVariables.LOGGER.error("Cannot update modpack, secret is null");
      } else {
         this.prepareFetchData(filesToUpdate);
         new ScreenManager().download(this.downloadManager, this.getModpackName());
         long start = System.currentTimeMillis();

         try {
            this.modpackDir = ModpackUtils.renameModpackDir(this.serverModpackContent, this.modpackDir);
            this.modpackContentFile = this.modpackDir.resolve(this.modpackContentFile.getFileName());
            long startFetching = System.currentTimeMillis();

            for (Jsons.ModpackContentFields.ModpackContentItem serverItem : filesToUpdate) {
               this.totalBytesToDownload = this.totalBytesToDownload + Long.parseLong(serverItem.size);
            }

            GlobalVariables.LOGGER.info("Finished preparing download urls in {}ms", System.currentTimeMillis() - startFetching);
            this.newDownloadedFiles.clear();
            int wholeQueue = filesToUpdate.size();
            if (wholeQueue > 0) {
               GlobalVariables.LOGGER.info("In queue left {} files to download ({}MB)", wholeQueue, this.totalBytesToDownload / 1024L / 1024L);
               DownloadClient downloadClient = DownloadClient.tryCreate(
                  this.modpackAddresses,
                  this.modpackSecret.secretBytes(),
                  Math.min(wholeQueue, 5),
                  ModpackUtils.userValidationCallback(this.modpackAddresses.hostAddress, false)
               );
               if (downloadClient == null) {
                  return;
               }

               this.downloadManager = new DownloadManager(this.totalBytesToDownload);
               new ScreenManager().download(this.downloadManager, this.getModpackName());
               this.downloadManager.attachDownloadClient(downloadClient);
               LinkedList<Jsons.ModpackContentFields.ModpackContentItem> randomizedList = new LinkedList<>(filesToUpdate);
               Collections.shuffle(randomizedList);

               for (Jsons.ModpackContentFields.ModpackContentItem serverItem : randomizedList) {
                  String serverFilePath = serverItem.file;
                  String serverHash = serverItem.sha1;
                  Path downloadFile = CustomFileUtils.getPath(this.modpackDir, serverFilePath);
                  if (!Files.exists(downloadFile)) {
                     this.newDownloadedFiles.add(serverFilePath);
                  }

                  List<String> urls = new ArrayList<>();
                  if (this.fetchManager.getFetchData(serverHash) != null) {
                     urls.addAll(this.fetchManager.getFetchData(serverHash).fetchedData().urls());
                  }

                  Runnable failureCallback = () -> this.failedDownloads.put(serverItem, urls);
                  Runnable successCallback = () -> {
                     List<String> mainPageUrls = new LinkedList<>();
                     if (this.fetchManager != null && this.fetchManager.getFetchData(serverHash) != null) {
                        mainPageUrls = this.fetchManager.getFetchData(serverHash).fetchedData().mainPageUrls();
                     }

                     this.changelogs.changesAddedList.put(downloadFile.getFileName().toString(), mainPageUrls);
                     ClientCacheUtils.updateCache(downloadFile, serverHash);
                  };
                  this.downloadManager.download(downloadFile, serverHash, urls, successCallback, failureCallback);
               }

               this.downloadManager.joinAll();
               GlobalVariables.LOGGER.info("Finished downloading files in {}ms", System.currentTimeMillis() - startFetching);
               if (this.downloadManager.isCanceled()) {
                  GlobalVariables.LOGGER.warn("Download canceled");
                  return;
               }

               this.downloadManager.cancelAllAndShutdown();
               this.totalBytesToDownload = 0L;
               Map<String, String> hashesToRefresh = new HashMap<>();
               HashMap<Jsons.ModpackContentFields.ModpackContentItem, List<String>> failedDownloadsSecMap = new HashMap<>(this.failedDownloads);
               failedDownloadsSecMap.forEach((k, v) -> {
                  hashesToRefresh.put(k.file, k.sha1);
                  this.failedDownloads.remove(k);
                  this.totalBytesToDownload = this.totalBytesToDownload + Long.parseLong(k.size);
               });
               if (!hashesToRefresh.isEmpty()) {
                  GlobalVariables.LOGGER.warn("Failed to download {} files", hashesToRefresh.size());
               }

               if (!hashesToRefresh.isEmpty()) {
                  byte[][] hashesArray = hashesToRefresh.values().stream().map(String::getBytes).toArray(byte[][]::new);
                  GlobalVariables.LOGGER.warn("Trying to refresh the modpack content");
                  GlobalVariables.LOGGER.info("Sending hashes to refresh: {}", hashesToRefresh.values());
                  Optional<Jsons.ModpackContentFields> refreshedContentOptional = ModpackUtils.refreshServerModpackContent(
                     this.modpackAddresses, this.modpackSecret, hashesArray, false
                  );
                  if (refreshedContentOptional.isEmpty()) {
                     GlobalVariables.LOGGER.error("Failed to refresh the modpack content");
                  } else {
                     GlobalVariables.LOGGER.info("Successfully refreshed the modpack content");
                     Jsons.ModpackContentFields refreshedContent = refreshedContentOptional.get();
                     this.serverModpackContent = refreshedContent;
                     this.serverModpackContentJson = ConfigTools.GSON.toJson(refreshedContent);
                     List<Jsons.ModpackContentFields.ModpackContentItem> refreshedFilteredList = refreshedContent.list
                        .stream()
                        .filter(itemx -> hashesToRefresh.containsKey(itemx.file))
                        .toList();
                     downloadClient = DownloadClient.tryCreate(
                        this.modpackAddresses,
                        this.modpackSecret.secretBytes(),
                        Math.min(refreshedFilteredList.size(), 5),
                        ModpackUtils.userValidationCallback(this.modpackAddresses.hostAddress, false)
                     );
                     if (downloadClient == null) {
                        return;
                     }

                     this.downloadManager = new DownloadManager(this.totalBytesToDownload);
                     new ScreenManager().download(this.downloadManager, this.getModpackName());
                     this.downloadManager.attachDownloadClient(downloadClient);
                     randomizedList = new LinkedList<>(refreshedFilteredList);
                     Collections.shuffle(randomizedList);

                     for (Jsons.ModpackContentFields.ModpackContentItem serverItem : randomizedList) {
                        String serverFilePathx = serverItem.file;
                        String serverHashx = serverItem.sha1;
                        Path downloadFilex = CustomFileUtils.getPath(this.modpackDir, serverFilePathx);
                        GlobalVariables.LOGGER.info("Retrying to download {} from {}", serverFilePathx, this.modpackAddresses.hostAddress.getHostName());
                        Runnable failureCallback = () -> this.failedDownloads.put(serverItem, List.of());
                        Runnable successCallback = () -> {
                           this.changelogs.changesAddedList.put(downloadFile.getFileName().toString(), null);
                           ClientCacheUtils.updateCache(downloadFile, serverHash);
                        };
                        this.downloadManager.download(downloadFilex, serverHashx, List.of(), successCallback, failureCallback);
                     }

                     this.downloadManager.joinAll();
                     if (this.downloadManager.isCanceled()) {
                        GlobalVariables.LOGGER.warn("Download canceled");
                        return;
                     }

                     this.downloadManager.cancelAllAndShutdown();
                     GlobalVariables.LOGGER.info("Finished refreshed downloading files in {}ms", System.currentTimeMillis() - startFetching);
                  }
               }
            }

            GlobalVariables.LOGGER.info("Done, saving {}", this.modpackContentFile);
            Files.writeString(this.modpackContentFile, this.serverModpackContentJson);
            ClientCacheUtils.saveMetadataCache();
            ClientCacheUtils.deleteDummyFiles();
            if (!this.failedDownloads.isEmpty()) {
               StringBuilder failedFiles = new StringBuilder();

               for (Entry<Jsons.ModpackContentFields.ModpackContentItem, List<String>> download : this.failedDownloads.entrySet()) {
                  Jsons.ModpackContentFields.ModpackContentItem item = download.getKey();
                  List<String> urls = download.getValue();
                  GlobalVariables.LOGGER.error("{}{}", "Failed to download: " + item.file + " from ", urls);
                  failedFiles.append(item.file);
               }

               new ScreenManager().error("automodpack.error.files", "Failed to download: " + failedFiles, "automodpack.error.logs");
               GlobalVariables.LOGGER.error("Update failed successfully! Try again! Took: {}ms", System.currentTimeMillis() - start);
            } else if (GlobalVariables.preload) {
               GlobalVariables.LOGGER.info("Update completed! Took: {}ms", System.currentTimeMillis() - start);
               this.CheckAndLoadModpack();
            } else {
               boolean requiredRestart = this.applyModpack();
               GlobalVariables.LOGGER.info("Update completed! Required restart: {} Took: {}ms", requiredRestart, System.currentTimeMillis() - start);
               UpdateType updateType = this.fullDownload ? UpdateType.FULL : UpdateType.UPDATE;
               new ReLauncher(this.modpackDir, updateType, this.changelogs).restart(false);
            }
         } catch (ConnectException | SocketTimeoutException var22) {
            GlobalVariables.LOGGER.error("{} is not responding", "Modpack host of " + this.modpackAddresses.hostAddress, var22);
         } catch (InterruptedException var23) {
            GlobalVariables.LOGGER.info("Interrupted the download");
         } catch (Exception var24) {
            new ScreenManager().error("automodpack.error.critical", "\"" + var24.getMessage() + "\"", "automodpack.error.logs");
            var24.printStackTrace();
         }
      }
   }

   private boolean applyModpack() throws Exception {
      ModpackUtils.selectModpack(this.modpackDir, this.modpackAddresses, this.newDownloadedFiles);

      try {
         SecretsStore.saveClientSecret(GlobalVariables.clientConfig.selectedModpack, this.modpackSecret);
      } catch (IllegalArgumentException var22) {
         GlobalVariables.LOGGER.error("Failed to save client secret", var22);
      }

      Jsons.ModpackContentFields modpackContent = ConfigTools.loadModpackContent(this.modpackContentFile);
      if (modpackContent == null) {
         throw new IllegalStateException("Failed to load modpack content");
      } else {
         List<FileInspection.Mod> conflictingNestedMods = GlobalVariables.MODPACK_LOADER.getModpackNestedConflicts(this.modpackDir);
         boolean needsRestart0 = this.deleteNonModpackFiles(modpackContent);
         Set<String> workaroundMods = new WorkaroundUtil(this.modpackDir).getWorkaroundMods(modpackContent);
         Set<String> filesNotToCopy = this.getFilesNotToCopy(modpackContent.list, workaroundMods);
         boolean needsRestart1 = ModpackUtils.correctFilesLocations(this.modpackDir, modpackContent, filesNotToCopy);
         workaroundMods = new WorkaroundUtil(this.modpackDir).getWorkaroundMods(modpackContent);
         filesNotToCopy = this.getFilesNotToCopy(modpackContent.list, workaroundMods);
         Set<Path> modpackMods = new HashSet<>();
         Collection<FileInspection.Mod> modpackModList = new ArrayList<>();
         Path modpackModsDir = this.modpackDir.resolve("mods");
         if (Files.exists(modpackModsDir)) {
            try (Stream<Path> stream = Files.list(modpackModsDir)) {
               stream.forEach(path -> {
                  modpackMods.add(path);
                  FileInspection.Mod mod = FileInspection.getMod(path);
                  if (mod != null) {
                     modpackModList.add(mod);
                  }
               });
            }
         }

         Collection<FileInspection.Mod> standardModList = new ArrayList<>();
         Path standardModsDir = GlobalVariables.MODS_DIR;
         if (Files.exists(standardModsDir)) {
            try (Stream<Path> stream = Files.list(standardModsDir)) {
               stream.forEach(path -> {
                  FileInspection.Mod mod = FileInspection.getMod(path);
                  if (mod != null) {
                     standardModList.add(mod);
                  }
               });
            }
         }

         conflictingNestedMods = conflictingNestedMods.stream().filter(conflictingMod -> modpackMods.contains(conflictingMod.modPath())).toList();
         if (!conflictingNestedMods.isEmpty()) {
            GlobalVariables.LOGGER.warn("Found conflicting nested mods: {}", conflictingNestedMods);
         }

         boolean needsRestart2 = ModpackUtils.fixNestedMods(conflictingNestedMods, standardModList);
         Set<String> ignoredFiles = ModpackUtils.getIgnoredFiles(conflictingNestedMods, workaroundMods);
         Set<String> forceCopyFiles = modpackContent.list.stream().filter(item -> item.forceCopy).map(item -> item.file).collect(Collectors.toSet());
         ModpackUtils.RemoveDupeModsResult removeDupeModsResult = ModpackUtils.removeDupeMods(
            this.modpackDir, standardModList, modpackModList, ignoredFiles, workaroundMods, forceCopyFiles
         );
         boolean needsRestart3 = removeDupeModsResult.requiresRestart();
         boolean needsRestart4 = ModpackUtils.removeRestModsNotToCopy(modpackContent, filesNotToCopy, removeDupeModsResult.modsToKeep());
         boolean needsRestart5 = ModpackUtils.deleteFilesMarkedForDeletionByTheServer(modpackContent.nonModpackFilesToDelete);
         boolean needsRestart6 = LauncherVersionSwapper.swapLoaderVersion(modpackContent.loader, modpackContent.loaderVersion);
         return needsRestart0 || needsRestart1 || needsRestart2 || needsRestart3 || needsRestart4 || needsRestart5 || needsRestart6;
      }
   }

   private Set<String> getFilesNotToCopy(Set<Jsons.ModpackContentFields.ModpackContentItem> modpackContentItems, Set<String> workaroundMods) {
      Set<String> filesNotToCopy = new HashSet<>();

      for (Jsons.ModpackContentFields.ModpackContentItem item : modpackContentItems) {
         if (!item.forceCopy) {
            if (item.editable && !this.newDownloadedFiles.contains(item.file)) {
               filesNotToCopy.add(item.file);
            }

            if (item.type.equals("mod") && !workaroundMods.contains(item.file)) {
               filesNotToCopy.add(item.file);
            }
         }
      }

      return filesNotToCopy;
   }

   private boolean deleteNonModpackFiles(Jsons.ModpackContentFields modpackContent) throws IOException {
      Set<String> modpackFiles = modpackContent.list.stream().map(modpackContentField -> modpackContentField.file).collect(Collectors.toSet());

      List<Path> pathList;
      try (Stream<Path> pathStream = Files.walk(this.modpackDir)) {
         pathList = pathStream.toList();
      }

      Set<Path> parentPaths = new HashSet<>();
      boolean needsRestart = false;

      for (Path path : pathList) {
         if (!Files.isDirectory(path) && !path.equals(this.modpackContentFile)) {
            String formattedFile = CustomFileUtils.formatPath(path, this.modpackDir);
            if (!modpackFiles.contains(formattedFile)) {
               Path runPath = CustomFileUtils.getPathFromCWD(formattedFile);
               if (ClientCacheUtils.fastHashCompare(path, runPath)) {
                  GlobalVariables.LOGGER.info("Deleting {} and {}", path, runPath);
                  parentPaths.add(runPath.getParent());
                  CustomFileUtils.executeOrder66(runPath, false);
                  needsRestart = true;
               } else {
                  GlobalVariables.LOGGER.info("Deleting {}", path);
               }

               parentPaths.add(path.getParent());
               CustomFileUtils.executeOrder66(path, false);
               this.changelogs.changesDeletedList.put(path.getFileName().toString(), null);
            }
         }
      }

      ClientCacheUtils.saveDummyFiles();

      for (Path parentPath : parentPaths) {
         this.deleteEmptyParentDirectoriesRecursively(parentPath);
      }

      return needsRestart;
   }

   private void deleteEmptyParentDirectoriesRecursively(Path directory) throws IOException {
      if (directory != null && CustomFileUtils.isEmptyDirectory(directory)) {
         GlobalVariables.LOGGER.info("Deleting empty directory {}", directory);
         CustomFileUtils.executeOrder66(directory);
         this.deleteEmptyParentDirectoriesRecursively(directory.getParent());
      }
   }
}
