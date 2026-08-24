package pl.skidam.automodpack_core.modpack;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.config.ConfigTools;
import pl.skidam.automodpack_core.config.Jsons;
import pl.skidam.automodpack_core.loader.LoaderManagerService;
import pl.skidam.automodpack_core.utils.CustomFileUtils;
import pl.skidam.automodpack_core.utils.FileInspection;
import pl.skidam.automodpack_core.utils.FileTreeScanner;
import pl.skidam.automodpack_core.utils.ModpackContentTools;
import pl.skidam.automodpack_core.utils.ObservableMap;

public class ModpackContent {
   public final Set<Jsons.ModpackContentFields.ModpackContentItem> list = ConcurrentHashMap.newKeySet();
   public final ObservableMap<String, Path> pathsMap = new ObservableMap<>();
   private final String MODPACK_NAME;
   private final FileTreeScanner SYNCED_FILES_CARDS;
   private final FileTreeScanner EDITABLE_CARDS;
   private final FileTreeScanner FORCE_COPY_FILES_TO_STANDARD_LOCATION;
   private final Path MODPACK_DIR;
   private final ThreadPoolExecutor CREATION_EXECUTOR;
   private final Map<String, String> sha1MurmurMapPreviousContent = new HashMap<>();

   public ModpackContent(
      String modpackName,
      Path cwd,
      Path modpackDir,
      Set<String> syncedFiles,
      Set<String> allowEditsInFiles,
      Set<String> forceCopyFilesToStandardLocation,
      ThreadPoolExecutor CREATION_EXECUTOR
   ) {
      this.MODPACK_NAME = modpackName;
      this.MODPACK_DIR = modpackDir;
      Set<Path> directoriesToSearch = new HashSet<>(2);
      if (this.MODPACK_DIR != null) {
         directoriesToSearch.add(this.MODPACK_DIR);
      }

      if (cwd != null) {
         directoriesToSearch.add(cwd);
         this.SYNCED_FILES_CARDS = new FileTreeScanner(syncedFiles, Set.of(cwd));
      } else {
         this.SYNCED_FILES_CARDS = new FileTreeScanner(syncedFiles, Set.of());
      }

      this.EDITABLE_CARDS = new FileTreeScanner(allowEditsInFiles, directoriesToSearch);
      this.FORCE_COPY_FILES_TO_STANDARD_LOCATION = new FileTreeScanner(forceCopyFilesToStandardLocation, directoriesToSearch);
      this.CREATION_EXECUTOR = CREATION_EXECUTOR;
   }

   public String getModpackName() {
      return this.MODPACK_NAME;
   }

   public boolean create() {
      Set<Jsons.ModpackContentFields.FileToDelete> computedFilesToDelete = new HashSet<>();

      try {
         this.SYNCED_FILES_CARDS.scan();
         this.EDITABLE_CARDS.scan();
         this.FORCE_COPY_FILES_TO_STANDARD_LOCATION.scan();
         this.pathsMap.clear();
         this.sha1MurmurMapPreviousContent.clear();
         this.getPreviousContent()
            .ifPresent(
               previousContent -> {
                  Map<String, Jsons.ModpackContentFields.FileToDelete> oldFilesMap = previousContent.nonModpackFilesToDelete
                     .stream()
                     .collect(Collectors.toMap(f -> f.file, f -> (Jsons.ModpackContentFields.FileToDelete)f, (a, b) -> a));
                  if (GlobalVariables.serverConfig != null && GlobalVariables.serverConfig.nonModpackFilesToDelete != null) {
                     for (Entry<String, String> fileToDeleteEntry : GlobalVariables.serverConfig.nonModpackFilesToDelete.entrySet()) {
                        String file = fileToDeleteEntry.getKey();
                        String sha1 = fileToDeleteEntry.getValue();
                        if (oldFilesMap.containsKey(file) && oldFilesMap.get(file).sha1.equalsIgnoreCase(sha1)) {
                           computedFilesToDelete.add(oldFilesMap.get(file));
                        } else {
                           String currentTimestamp = String.valueOf(System.currentTimeMillis());
                           computedFilesToDelete.add(new Jsons.ModpackContentFields.FileToDelete(file, sha1, currentTimestamp));
                        }
                     }
                  }

                  previousContent.list.forEach(item -> this.sha1MurmurMapPreviousContent.put(item.sha1, item.murmur));
               }
            );
         List<CompletableFuture<Void>> creationFutures = Collections.synchronizedList(new ArrayList<>());
         if (this.MODPACK_DIR != null) {
            GlobalVariables.LOGGER.info("Syncing {}...", this.MODPACK_DIR.getFileName());

            try (Stream<Path> pathStream = Files.walk(this.MODPACK_DIR)) {
               creationFutures.addAll(this.generateAsync(pathStream.toList()));
               creationFutures.forEach(CompletableFuture::join);
               creationFutures.clear();
            }
         }

         creationFutures.addAll(this.generateAsync(this.SYNCED_FILES_CARDS.getMatchedPaths().values().stream().toList()));
         creationFutures.forEach(CompletableFuture::join);
         creationFutures.clear();
         if (this.list.isEmpty()) {
            GlobalVariables.LOGGER.warn("Modpack is empty!");
            return false;
         }
      } catch (Exception var8) {
         GlobalVariables.LOGGER.error("Error while generating modpack!", var8);
         return false;
      }

      this.saveModpackContent(computedFilesToDelete);
      if (GlobalVariables.hostServer != null) {
         GlobalVariables.hostServer.addPaths(this.pathsMap);
      }

      return true;
   }

   public Optional<Jsons.ModpackContentFields> getPreviousContent() {
      Optional<Path> optionalModpackContentFile = ModpackContentTools.getModpackContentFile(this.MODPACK_DIR);
      return optionalModpackContentFile.map(ConfigTools::loadModpackContent);
   }

   public boolean loadPreviousContent() {
      Optional<Jsons.ModpackContentFields> optionalPreviousModpackContent = this.getPreviousContent();
      if (optionalPreviousModpackContent.isEmpty()) {
         return false;
      } else {
         Jsons.ModpackContentFields previousModpackContent = optionalPreviousModpackContent.get();
         synchronized (this.list) {
            this.list.addAll(previousModpackContent.list);

            for (Jsons.ModpackContentFields.ModpackContentItem modpackContentItem : this.list) {
               Path file = CustomFileUtils.getPath(this.MODPACK_DIR, modpackContentItem.file);
               if (!Files.exists(file)) {
                  file = CustomFileUtils.getPathFromCWD(modpackContentItem.file);
               }

               if (!Files.exists(file)) {
                  GlobalVariables.LOGGER.warn("File {} does not exist!", file);
               } else {
                  this.pathsMap.put(modpackContentItem.sha1, file);
               }
            }
         }

         if (GlobalVariables.hostServer != null) {
            GlobalVariables.hostServer.addPaths(this.pathsMap);
         }

         this.saveModpackContent(previousModpackContent.nonModpackFilesToDelete);
         return true;
      }
   }

   public synchronized void saveModpackContent(Set<Jsons.ModpackContentFields.FileToDelete> nonModpackFilesToDelete) {
      if (nonModpackFilesToDelete == null) {
         throw new IllegalArgumentException("filesToDelete is null");
      } else {
         synchronized (this.list) {
            Jsons.ModpackContentFields modpackContent = new Jsons.ModpackContentFields(this.list);
            modpackContent.automodpackVersion = GlobalVariables.AM_VERSION;
            modpackContent.mcVersion = GlobalVariables.MC_VERSION;
            modpackContent.loaderVersion = GlobalVariables.LOADER_VERSION;
            modpackContent.loader = GlobalVariables.LOADER;
            modpackContent.modpackName = this.MODPACK_NAME;
            modpackContent.nonModpackFilesToDelete = nonModpackFilesToDelete;
            ConfigTools.saveModpackContent(GlobalVariables.hostModpackContentFile, modpackContent);
         }
      }
   }

   private List<CompletableFuture<Void>> generateAsync(List<Path> files) {
      List<CompletableFuture<Void>> futures = new ArrayList<>();

      for (int i = 0; i < files.size(); i += 6) {
         List<Path> subList = files.subList(i, Math.min(files.size(), i + 6));
         futures.add(CompletableFuture.runAsync(() -> subList.forEach(this::generate), this.CREATION_EXECUTOR));
      }

      return futures;
   }

   private void generate(Path file) {
      try {
         Jsons.ModpackContentFields.ModpackContentItem item = this.generateContent(file);
         if (item != null) {
            GlobalVariables.LOGGER.info("generated content for {}", item.file);
            synchronized (this.list) {
               this.list.add(item);
            }

            this.pathsMap.put(item.sha1, file);
         }
      } catch (Exception var6) {
         GlobalVariables.LOGGER.error("Error while generating content for: " + file + " generated from: " + this.MODPACK_DIR, var6);
      }
   }

   public CompletableFuture<Void> replaceAsync(Path file) {
      return CompletableFuture.runAsync(() -> this.replace(file), this.CREATION_EXECUTOR);
   }

   public void replace(Path file) {
      this.remove(file);
      this.generate(file);
   }

   public void remove(Path file) {
      String modpackFile = CustomFileUtils.formatPath(file, this.MODPACK_DIR);
      synchronized (this.list) {
         for (Jsons.ModpackContentFields.ModpackContentItem item : this.list) {
            if (item.file.equals(modpackFile)) {
               this.pathsMap.remove(item.sha1);
               this.list.remove(item);
               GlobalVariables.LOGGER.info("Removed content for {}", modpackFile);
               break;
            }
         }
      }
   }

   public static boolean isInnerFile(Path file) {
      Path normalizedFilePath = file.toAbsolutePath().normalize();
      boolean isInner = normalizedFilePath.startsWith(GlobalVariables.automodpackDir.toAbsolutePath().normalize())
         && !normalizedFilePath.startsWith(GlobalVariables.hostModpackDir.toAbsolutePath().normalize());
      return !isInner && normalizedFilePath.equals(GlobalVariables.hostModpackContentFile.toAbsolutePath().normalize()) ? true : isInner;
   }

   private Jsons.ModpackContentFields.ModpackContentItem generateContent(Path file) throws Exception {
      if (!Files.isRegularFile(file)) {
         return null;
      } else if (GlobalVariables.serverConfig == null) {
         GlobalVariables.LOGGER.error("Server config is null!");
         return null;
      } else if (isInnerFile(file)) {
         return null;
      } else {
         String formattedFile = CustomFileUtils.formatPath(file, this.MODPACK_DIR);
         if (formattedFile.startsWith("/automodpack/")) {
            return null;
         } else {
            String size = String.valueOf(Files.size(file));
            if (GlobalVariables.serverConfig.autoExcludeUnnecessaryFiles) {
               if (size.equals("0")) {
                  GlobalVariables.LOGGER.info("Skipping file {} because it is empty", formattedFile);
                  return null;
               }

               if (file.getFileName().toString().startsWith(".")) {
                  GlobalVariables.LOGGER.info("Skipping file {} is hidden", formattedFile);
                  return null;
               }

               if (formattedFile.endsWith(".tmp")) {
                  GlobalVariables.LOGGER.info("File {} is temporary! Skipping...", formattedFile);
                  return null;
               }

               if (formattedFile.endsWith(".disabled")) {
                  GlobalVariables.LOGGER.info("File {} is disabled! Skipping...", formattedFile);
                  return null;
               }

               if (formattedFile.endsWith(".bak")) {
                  GlobalVariables.LOGGER.info("File {} is backup file, unnecessary on client! Skipping...", formattedFile);
                  return null;
               }
            }

            String type;
            if (FileInspection.isMod(file)) {
               type = "mod";
               if (GlobalVariables.serverConfig.autoExcludeServerSideMods
                  && Objects.equals(FileInspection.getModEnvironment(file), LoaderManagerService.EnvironmentType.SERVER)) {
                  GlobalVariables.LOGGER.info("File {} is server mod! Skipping...", formattedFile);
                  return null;
               }

               String modId = FileInspection.getModID(file);
               if ("automodpack_bootstrap".equals(modId)
                  || "automodpack-bootstrap".equals(modId)
                  || "automodpack_mod".equals(modId)
                  || "automodpack".equals(modId)) {
                  return null;
               }
            } else if (formattedFile.contains("/config/")) {
               type = "config";
            } else if (formattedFile.contains("/shaderpacks/")) {
               type = "shader";
            } else if (formattedFile.contains("/resourcepacks/")) {
               type = "resourcepack";
            } else if (formattedFile.endsWith("/options.txt")) {
               type = "mc_options";
            } else {
               type = "other";
            }

            String sha1 = CustomFileUtils.getHash(file);
            String murmur = null;
            if (file.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".jar")) {
               murmur = this.sha1MurmurMapPreviousContent.get(sha1);
               if (murmur == null) {
                  murmur = CustomFileUtils.getCurseforgeMurmurHash(file);
               }
            }

            boolean isEditable = false;
            if (this.EDITABLE_CARDS.hasMatch(formattedFile)) {
               isEditable = true;
               GlobalVariables.LOGGER.info("File {} is editable!", formattedFile);
            }

            boolean forcedToCopy = false;
            if (this.FORCE_COPY_FILES_TO_STANDARD_LOCATION.hasMatch(formattedFile)) {
               forcedToCopy = true;
               GlobalVariables.LOGGER.info("File {} is forced to copy to standard location!", formattedFile);
            }

            return new Jsons.ModpackContentFields.ModpackContentItem(formattedFile, size, type, isEditable, forcedToCopy, sha1, murmur);
         }
      }
   }
}
