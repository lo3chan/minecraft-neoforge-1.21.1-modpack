package pl.skidam.automodpack_loader_core.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.auth.Secrets;
import pl.skidam.automodpack_core.config.ConfigTools;
import pl.skidam.automodpack_core.config.Jsons;
import pl.skidam.automodpack_core.protocol.DownloadClient;
import pl.skidam.automodpack_core.protocol.NetUtils;
import pl.skidam.automodpack_core.utils.ClientCacheUtils;
import pl.skidam.automodpack_core.utils.CustomFileUtils;
import pl.skidam.automodpack_core.utils.FileInspection;
import pl.skidam.automodpack_core.utils.ModpackContentTools;
import pl.skidam.automodpack_loader_core.screen.ScreenManager;

public class ModpackUtils {
   public static ModpackUtils.UpdateCheckResult isUpdate(Jsons.ModpackContentFields serverModpackContent, Path modpackDir) {
      if (serverModpackContent != null && serverModpackContent.list != null) {
         Optional<Path> optionalClientModpackContentFile = ModpackContentTools.getModpackContentFile(modpackDir);
         if (!optionalClientModpackContentFile.isEmpty() && Files.exists(optionalClientModpackContentFile.get())) {
            Jsons.ModpackContentFields clientModpackContent = ConfigTools.loadModpackContent(optionalClientModpackContentFile.get());
            if (clientModpackContent == null) {
               return new ModpackUtils.UpdateCheckResult(true, serverModpackContent.list);
            } else {
               GlobalVariables.LOGGER.info("Indexing file system...");
               long start = System.currentTimeMillis();

               Set<Path> existingFileTree;
               try (Stream<Path> stream = Files.walk(modpackDir)) {
                  existingFileTree = stream.collect(Collectors.toSet());
               } catch (IOException var13) {
                  GlobalVariables.LOGGER.error("Failed to walk directory", var13);
                  return new ModpackUtils.UpdateCheckResult(true, serverModpackContent.list);
               }

               GlobalVariables.LOGGER.info("Verifying content against server list...");
               Set<Jsons.ModpackContentFields.ModpackContentItem> filesToUpdate = ConcurrentHashMap.newKeySet();
               serverModpackContent.list.parallelStream().forEach(serverItem -> {
                  Path serverItemPath = CustomFileUtils.getPath(modpackDir, serverItem.file);
                  if (!existingFileTree.contains(serverItemPath)) {
                     filesToUpdate.add(serverItem);
                  } else if (serverItem.editable) {
                     GlobalVariables.LOGGER.debug("Skipping editable file hash check: {}", serverItem.file);
                  } else {
                     String cachedHash = ClientCacheUtils.getVerifiedCacheHash(serverItemPath);
                     if (cachedHash == null || !cachedHash.equals(serverItem.sha1)) {
                        String diskHash = CustomFileUtils.getHash(serverItemPath);
                        if (diskHash != null && diskHash.equals(serverItem.sha1)) {
                           ClientCacheUtils.updateCache(serverItemPath, diskHash);
                        } else {
                           filesToUpdate.add(serverItem);
                        }
                     }
                  }
               });
               ClientCacheUtils.saveMetadataCache();
               if (!filesToUpdate.isEmpty()) {
                  GlobalVariables.LOGGER.info("Modpack {} requires update! Took {} ms", modpackDir, System.currentTimeMillis() - start);
                  return new ModpackUtils.UpdateCheckResult(true, filesToUpdate);
               } else {
                  GlobalVariables.LOGGER.info("Checking for deleted files...");
                  Set<String> serverFileSet = serverModpackContent.list.stream().map(item -> item.file).collect(Collectors.toSet());

                  for (Jsons.ModpackContentFields.ModpackContentItem clientItem : clientModpackContent.list) {
                     if (!serverFileSet.contains(clientItem.file)) {
                        GlobalVariables.LOGGER.info("Found file marked for deletion (its no longer on server): {}", clientItem.file);
                        return new ModpackUtils.UpdateCheckResult(true, Set.of());
                     }
                  }

                  GlobalVariables.LOGGER.info("Modpack {} is up to date! Took {} ms", modpackDir, System.currentTimeMillis() - start);
                  return new ModpackUtils.UpdateCheckResult(false, Set.of());
               }
            }
         } else {
            return new ModpackUtils.UpdateCheckResult(true, serverModpackContent.list);
         }
      } else {
         throw new IllegalArgumentException("Server modpack content list is null");
      }
   }

   public static boolean deleteFilesMarkedForDeletionByTheServer(Set<Jsons.ModpackContentFields.FileToDelete> filesToDeleteOnClient) {
      if (!GlobalVariables.clientConfig.allowRemoteNonModpackDeletions) {
         if (!filesToDeleteOnClient.isEmpty()) {
            GlobalVariables.LOGGER
               .warn(
                  "Server requested deletion of {} files, but remote deletions are disabled in client config! Consider deleting them manually.",
                  filesToDeleteOnClient.size()
               );

            for (Jsons.ModpackContentFields.FileToDelete entry : filesToDeleteOnClient) {
               GlobalVariables.LOGGER.warn("File marked for deletion: {} (sha1: {})", entry.file, entry.sha1);
            }
         }

         return false;
      } else {
         AtomicBoolean deletedAnyModFile = new AtomicBoolean(false);

         for (Jsons.ModpackContentFields.FileToDelete entry : filesToDeleteOnClient) {
            if (ClientCacheUtils.wasThisTimestampEvaluatedBefore(entry.timestamp)) {
               GlobalVariables.LOGGER.info("Skipping deletion of {} - already evaluated", entry.file);
            } else {
               String filePath = entry.file;
               String expectedHash = entry.sha1;
               Path fileInCWD = CustomFileUtils.getPathFromCWD(filePath);
               if (Files.isRegularFile(fileInCWD)) {
                  GlobalVariables.LOGGER.info("Found exact file to delete: {}", filePath);
                  String diskHash = ClientCacheUtils.computeHashIfNeeded(fileInCWD);
                  if (diskHash.equalsIgnoreCase(expectedHash)) {
                     boolean isModFile = FileInspection.isMod(fileInCWD);
                     GlobalVariables.LOGGER.warn("Deleting file marked for deletion by server: {}", filePath);
                     CustomFileUtils.executeOrder66(fileInCWD);
                     if (isModFile) {
                        deletedAnyModFile.set(true);
                     }
                  }
               } else {
                  Path parentDir;
                  if (Files.isDirectory(fileInCWD)) {
                     parentDir = fileInCWD;
                  } else {
                     parentDir = fileInCWD.getParent();
                  }

                  GlobalVariables.LOGGER.info("Searching directory {} for files to delete matching: {}", parentDir, filePath);

                  try (Stream<Path> stream = Files.list(parentDir)) {
                     stream.forEach(path -> {
                        if (Files.isRegularFile(path)) {
                           String diskHash = ClientCacheUtils.computeHashIfNeeded(path);
                           if (diskHash.equalsIgnoreCase(expectedHash)) {
                              boolean isModFile = FileInspection.isMod(path);
                              GlobalVariables.LOGGER.warn("Deleting file marked for deletion by server: {}", path);
                              CustomFileUtils.executeOrder66(path);
                              if (isModFile) {
                                 deletedAnyModFile.set(true);
                              }
                           }
                        }
                     });
                  } catch (Exception var13) {
                     GlobalVariables.LOGGER.error("Error while searching for files to delete in directory: {}", parentDir, var13);
                  }
               }
            }
         }

         for (Jsons.ModpackContentFields.FileToDelete entryx : filesToDeleteOnClient) {
            ClientCacheUtils.markTimestampAsEvaluated(entryx.timestamp);
         }

         ClientCacheUtils.saveDeletedFilesTimestamps();
         return deletedAnyModFile.get();
      }
   }

   public static boolean correctFilesLocations(Path modpackDir, Jsons.ModpackContentFields serverModpackContent, Set<String> filesNotToCopy) throws IOException {
      boolean needsRestart = false;

      for (Jsons.ModpackContentFields.ModpackContentItem contentItem : serverModpackContent.list) {
         String formattedFile = contentItem.file;
         Path modpackFile = CustomFileUtils.getPath(modpackDir, formattedFile);
         Path runFile = CustomFileUtils.getPathFromCWD(formattedFile);
         boolean isMod = "mod".equals(contentItem.type);
         if (isMod) {
            runFile = CustomFileUtils.getPath(GlobalVariables.MODS_DIR, formattedFile.replaceFirst("/mods/", ""));
         }

         boolean modpackFileExists = Files.exists(modpackFile);
         boolean runFileExists = Files.exists(runFile);
         boolean runFileHashMatch = false;
         if (runFileExists) {
            runFileHashMatch = Objects.equals(contentItem.sha1, ClientCacheUtils.computeHashIfNeeded(runFile));
         }

         if (runFileHashMatch && !modpackFileExists) {
            GlobalVariables.LOGGER.debug("Copying {} file to the modpack directory", formattedFile);
            CustomFileUtils.copyFile(runFile, modpackFile);
            modpackFileExists = true;
         }

         if (!filesNotToCopy.contains(formattedFile)) {
            if (modpackFileExists && !runFileExists) {
               CustomFileUtils.copyFile(modpackFile, runFile);
               if (isMod) {
                  needsRestart = true;
                  GlobalVariables.LOGGER.warn("Applying workaround for {} mod", formattedFile);
               }
            } else if (!modpackFileExists) {
               GlobalVariables.LOGGER
                  .error(
                     "File {} doesn't exist!? If you see this please report this to the automodpack repo and attach this log https://github.com/Skidamek/AutoModpack/issues",
                     formattedFile
                  );
               Thread.dumpStack();
            } else if (!runFileHashMatch) {
               CustomFileUtils.copyFile(modpackFile, runFile);
               if (isMod) {
                  needsRestart = true;
                  GlobalVariables.LOGGER.warn("Overwriting mod {} file to modpack version", formattedFile);
               } else {
                  GlobalVariables.LOGGER.info("Overwriting {} file to the modpack version", formattedFile);
               }
            }
         }
      }

      return needsRestart;
   }

   public static boolean removeRestModsNotToCopy(Jsons.ModpackContentFields serverModpackContent, Set<String> filesNotToCopy, Set<Path> modsToKeep) {
      boolean needsRestart = false;

      for (Jsons.ModpackContentFields.ModpackContentItem contentItem : serverModpackContent.list) {
         String formattedFile = contentItem.file;
         Path runFile = CustomFileUtils.getPathFromCWD(formattedFile);
         boolean isMod = "mod".equals(contentItem.type);
         if (isMod) {
            runFile = CustomFileUtils.getPath(GlobalVariables.MODS_DIR, formattedFile.replaceFirst("/mods/", ""));
         }

         if (modsToKeep.contains(runFile)) {
            GlobalVariables.LOGGER.info("Keeping {} file in the standard mods directory", formattedFile);
         } else {
            boolean runFileExists = Files.exists(runFile);
            boolean runFileHashMatch = false;
            if (runFileExists) {
               runFileHashMatch = Objects.equals(contentItem.sha1, ClientCacheUtils.computeHashIfNeeded(runFile));
            }

            if (runFileHashMatch && isMod && filesNotToCopy.contains(formattedFile)) {
               GlobalVariables.LOGGER.info("Deleting {} file from standard mods directory", formattedFile);
               CustomFileUtils.executeOrder66(runFile);
               needsRestart = true;
            }
         }
      }

      return needsRestart;
   }

   public static boolean fixNestedMods(List<FileInspection.Mod> conflictingNestedMods, Collection<FileInspection.Mod> standardModList) throws IOException {
      if (conflictingNestedMods.isEmpty()) {
         return false;
      } else {
         List<String> standardModIDs = standardModList.stream().map(FileInspection.Mod::modID).toList();
         boolean needsRestart = false;

         for (FileInspection.Mod mod : conflictingNestedMods) {
            if (!standardModIDs.stream().anyMatch(mod.providesIDs()::contains)) {
               Path modPath = mod.modPath();
               Path standardModPath = GlobalVariables.MODS_DIR.resolve(modPath.getFileName());
               if (!Files.exists(standardModPath) || !Objects.equals(ClientCacheUtils.computeHashIfNeeded(standardModPath), mod.hash())) {
                  needsRestart = true;
                  GlobalVariables.LOGGER.info("Copying nested mod {} to standard mods folder", standardModPath.getFileName());
                  CustomFileUtils.copyFile(modPath, standardModPath);
                  FileInspection.Mod newMod = FileInspection.getMod(standardModPath);
                  if (newMod != null) {
                     standardModList.add(newMod);
                  }
               }
            }
         }

         return needsRestart;
      }
   }

   public static Set<String> getIgnoredFiles(List<FileInspection.Mod> conflictingNestedMods, Set<String> workarounds) {
      Set<String> newIgnoredFiles = new HashSet<>(workarounds);

      for (FileInspection.Mod mod : conflictingNestedMods) {
         newIgnoredFiles.add(CustomFileUtils.formatPath(mod.modPath(), GlobalVariables.modpacksDir));
      }

      return newIgnoredFiles;
   }

   public static Map<FileInspection.Mod, FileInspection.Mod> getDupeMods(
      Path modpackDir,
      Set<String> ignoredMods,
      Collection<FileInspection.Mod> standardModList,
      Collection<FileInspection.Mod> modpackModList,
      Set<String> forceCopyFiles
   ) {
      Map<FileInspection.Mod, FileInspection.Mod> duplicates = new HashMap<>();

      for (FileInspection.Mod modpackMod : modpackModList) {
         FileInspection.Mod standardMod = standardModList.stream().filter(mod -> mod.modID().equals(modpackMod.modID())).findFirst().orElse(null);
         if (standardMod != null) {
            String formattedFile = CustomFileUtils.formatPath(modpackMod.modPath(), modpackDir);
            if (!ignoredMods.contains(formattedFile) && !forceCopyFiles.contains(formattedFile)) {
               duplicates.put(modpackMod, standardMod);
            }
         }
      }

      return duplicates;
   }

   public static ModpackUtils.RemoveDupeModsResult removeDupeMods(
      Path modpackDir,
      Collection<FileInspection.Mod> standardModList,
      Collection<FileInspection.Mod> modpackModList,
      Set<String> ignoredMods,
      Set<String> workaroundMods,
      Set<String> forceCopyFiles
   ) throws IOException {
      Map<FileInspection.Mod, FileInspection.Mod> dupeMods = getDupeMods(modpackDir, ignoredMods, standardModList, modpackModList, forceCopyFiles);
      if (dupeMods.isEmpty()) {
         return new ModpackUtils.RemoveDupeModsResult(false, Set.of());
      } else {
         Set<FileInspection.Mod> modsToKeep = new HashSet<>();

         for (FileInspection.Mod standardMod : standardModList) {
            if (!dupeMods.containsValue(standardMod)) {
               modsToKeep.add(standardMod);
               addDependenciesRecursively(standardMod, standardModList, modsToKeep);
            }
         }

         Set<String> idsToKeep = new HashSet<>();
         modsToKeep.forEach(mod -> {
            idsToKeep.add(mod.modID());
            idsToKeep.addAll(mod.providesIDs());
         });
         boolean requiresRestart = false;
         Set<Path> dependentMods = new HashSet<>();

         for (Entry<FileInspection.Mod, FileInspection.Mod> dupeMod : dupeMods.entrySet()) {
            FileInspection.Mod modpackMod = dupeMod.getKey();
            FileInspection.Mod standardModx = dupeMod.getValue();
            Path modpackModPath = modpackMod.modPath();
            Path standardModPath = standardModx.modPath();
            String modId = modpackMod.modID();
            String formatedPath = CustomFileUtils.formatPath(standardModPath, GlobalVariables.MODS_DIR.getParent());
            Collection<String> providesIDs = modpackMod.providesIDs();
            List<String> IDs = new ArrayList<>(providesIDs);
            IDs.add(modId);
            boolean isDependent = IDs.stream().anyMatch(idsToKeep::contains);
            boolean isWorkaround = workaroundMods.contains(formatedPath);
            boolean isForceCopy = forceCopyFiles.contains(formatedPath);
            if (isDependent) {
               Path newStandardModPath = standardModPath.getParent().resolve(modpackModPath.getFileName());
               dependentMods.add(newStandardModPath);
               if (!Objects.equals(modpackMod.hash(), standardModx.hash())) {
                  GlobalVariables.LOGGER
                     .warn("Changing duplicated mod {} - {} to modpack version - {}", modId, standardModx.modVersion(), modpackMod.modVersion());
                  CustomFileUtils.executeOrder66(standardModPath, false);
                  CustomFileUtils.copyFile(modpackModPath, newStandardModPath);
                  requiresRestart = true;
               }
            } else if (!isWorkaround && !isForceCopy) {
               GlobalVariables.LOGGER.warn("Removing {} mod. It is duplicated modpack mod and no other mods are dependent on it!", modId);
               CustomFileUtils.executeOrder66(standardModPath, false);
               requiresRestart = true;
            }
         }

         ClientCacheUtils.saveDummyFiles();
         return new ModpackUtils.RemoveDupeModsResult(requiresRestart, dependentMods);
      }
   }

   private static void addDependenciesRecursively(FileInspection.Mod mod, Collection<FileInspection.Mod> modList, Set<FileInspection.Mod> modsToKeep) {
      for (String depId : mod.dependencies()) {
         for (FileInspection.Mod modItem : modList) {
            if ((modItem.modID().equals(depId) || modItem.providesIDs().contains(depId)) && modsToKeep.add(modItem)) {
               addDependenciesRecursively(modItem, modList, modsToKeep);
            }
         }
      }
   }

   public static Path renameModpackDir(Jsons.ModpackContentFields serverModpackContent, Path modpackDir) {
      String currentName = GlobalVariables.clientConfig.selectedModpack;
      String newName = serverModpackContent.modpackName;
      if (GlobalVariables.clientConfig.installedModpacks == null
         || GlobalVariables.clientConfig.selectedModpack == null
         || GlobalVariables.clientConfig.selectedModpack.isBlank()) {
         return modpackDir;
      } else if (!newName.isEmpty() && !newName.equals(currentName)) {
         Jsons.ModpackAddresses installedAddresses = GlobalVariables.clientConfig.installedModpacks.get(currentName);
         if (installedAddresses == null) {
            return modpackDir;
         } else {
            Path newModpackDir = modpackDir.getParent().resolve(newName);

            try {
               GlobalVariables.LOGGER.info("Renaming modpack directory: {} -> {}", modpackDir.getFileName(), newName);
               Files.move(modpackDir, newModpackDir, StandardCopyOption.REPLACE_EXISTING);
               removeModpackFromList(currentName);
               selectModpack(newModpackDir, installedAddresses, Set.of());
               GlobalVariables.LOGGER.info("Successfully renamed and reselected modpack: {}", newName);
               return newModpackDir;
            } catch (DirectoryNotEmptyException var7) {
               GlobalVariables.LOGGER.warn("Could not rename: Target directory {} not empty", newName);
            } catch (IOException var8) {
               GlobalVariables.LOGGER.error("Failed to rename modpack directory", var8);
            }

            return modpackDir;
         }
      } else {
         return modpackDir;
      }
   }

   public static boolean selectModpack(Path modpackDirToSelect, Jsons.ModpackAddresses modpackAddresses, Set<String> newDownloadedFiles) {
      String newName = modpackDirToSelect.getFileName().toString();
      String oldName = GlobalVariables.clientConfig.selectedModpack;
      if (Objects.equals(newName, oldName)) {
         addModpackToList(newName, modpackAddresses);
         return false;
      } else {
         GlobalVariables.LOGGER.info("Preserving editable files from old modpack and copying to new modpack...");
         if (oldName != null && !oldName.isBlank()) {
            processEditableFiles(GlobalVariables.modpacksDir.resolve(oldName), (dir, files) -> preserveEditableFiles(dir, files, newDownloadedFiles));
         }

         processEditableFiles(modpackDirToSelect, (dir, files) -> copyPreviousEditableFiles(dir, files, newDownloadedFiles));
         GlobalVariables.clientConfig.selectedModpack = newName;
         ConfigTools.save(GlobalVariables.clientConfigFile, GlobalVariables.clientConfig);
         addModpackToList(newName, modpackAddresses);
         GlobalVariables.LOGGER.info("Selected modpack: {}", newName);
         return true;
      }
   }

   private static void processEditableFiles(Path modpackDir, BiConsumer<Path, Set<String>> action) {
      Path contentFile = modpackDir.resolve(GlobalVariables.hostModpackContentFile.getFileName());
      Jsons.ModpackContentFields content = ConfigTools.loadModpackContent(contentFile);
      if (content != null) {
         Set<String> editableFiles = getEditableFiles(content.list);
         action.accept(modpackDir, editableFiles);
      }
   }

   public static void removeModpackFromList(String modpackName) {
      if (modpackName != null && !modpackName.isEmpty()) {
         if (GlobalVariables.clientConfig.installedModpacks != null && GlobalVariables.clientConfig.installedModpacks.containsKey(modpackName)) {
            Map<String, Jsons.ModpackAddresses> modpacks = new HashMap<>(GlobalVariables.clientConfig.installedModpacks);
            modpacks.remove(modpackName);
            GlobalVariables.clientConfig.installedModpacks = modpacks;
            ConfigTools.save(GlobalVariables.clientConfigFile, GlobalVariables.clientConfig);
         }
      }
   }

   public static void addModpackToList(String modpackName, Jsons.ModpackAddresses modpackAddresses) {
      if (modpackName != null && !modpackName.isEmpty() && !modpackAddresses.isAnyEmpty()) {
         Map<String, Jsons.ModpackAddresses> modpacks = new HashMap<>(GlobalVariables.clientConfig.installedModpacks);
         modpacks.put(modpackName, modpackAddresses);
         GlobalVariables.clientConfig.installedModpacks = modpacks;
         ConfigTools.save(GlobalVariables.clientConfigFile, GlobalVariables.clientConfig);
      }
   }

   public static Path getModpackPath(InetSocketAddress address, String modpackName) {
      String strAddress = address.getHostString() + ":" + address.getPort();
      String correctedName = strAddress;
      if (FileInspection.isInValidFileName(strAddress)) {
         correctedName = FileInspection.fixFileName(strAddress);
      }

      Path modpackDir = CustomFileUtils.getPath(GlobalVariables.modpacksDir, correctedName);
      if (!modpackName.isEmpty()) {
         String nameFromName = modpackName;
         if (FileInspection.isInValidFileName(modpackName)) {
            nameFromName = FileInspection.fixFileName(modpackName);
         }

         modpackDir = CustomFileUtils.getPath(GlobalVariables.modpacksDir, nameFromName);
      }

      return modpackDir;
   }

   public static Optional<Jsons.ModpackContentFields> requestServerModpackContent(
      Jsons.ModpackAddresses modpackAddresses, Secrets.Secret secret, boolean allowAskingUser
   ) {
      return fetchModpackContent(
         modpackAddresses, secret, client -> client.downloadFile(new byte[0], GlobalVariables.modpackContentTempFile, null), "Fetched", allowAskingUser
      );
   }

   public static Optional<Jsons.ModpackContentFields> refreshServerModpackContent(
      Jsons.ModpackAddresses modpackAddresses, Secrets.Secret secret, byte[][] fileHashes, boolean allowAskingUser
   ) {
      return fetchModpackContent(
         modpackAddresses, secret, client -> client.requestRefresh(fileHashes, GlobalVariables.modpackContentTempFile), "Re-fetched", allowAskingUser
      );
   }

   private static Optional<Jsons.ModpackContentFields> fetchModpackContent(
      Jsons.ModpackAddresses modpackAddresses,
      Secrets.Secret secret,
      Function<DownloadClient, Future<Path>> operation,
      String fetchType,
      boolean allowAskingUser
   ) {
      if (secret == null) {
         return Optional.empty();
      } else if (modpackAddresses.isAnyEmpty()) {
         throw new IllegalArgumentException("Modpack addresses are empty!");
      } else {
         try {
            Optional var9;
            try (DownloadClient client = DownloadClient.tryCreate(
                  modpackAddresses, secret.secretBytes(), 1, userValidationCallback(modpackAddresses.hostAddress, allowAskingUser)
               )) {
               if (client == null) {
                  return Optional.empty();
               }

               Future<Path> future = operation.apply(client);
               Path path = future.get();
               Optional<Jsons.ModpackContentFields> content = Optional.ofNullable(ConfigTools.loadModpackContent(path));
               Files.deleteIfExists(GlobalVariables.modpackContentTempFile);
               if (content.isPresent() && potentiallyMalicious(content.get())) {
                  return Optional.empty();
               }

               var9 = content;
            }

            return var9;
         } catch (Exception var12) {
            GlobalVariables.LOGGER.error("Error while getting server modpack content", var12);
            return Optional.empty();
         }
      }
   }

   public static boolean canConnectModpackHost(Jsons.ModpackAddresses modpackAddresses) {
      if (modpackAddresses.isAnyEmpty()) {
         throw new IllegalArgumentException("Modpack addresses are empty!");
      } else {
         try {
            boolean var2;
            try (DownloadClient client = DownloadClient.tryCreate(modpackAddresses, null, 1, null)) {
               var2 = client != null;
            }

            return var2;
         } catch (Exception var6) {
            GlobalVariables.LOGGER.error("Error while pinging AutoModpack host server", var6);
            return false;
         }
      }
   }

   public static Function<X509Certificate, Boolean> userValidationCallback(InetSocketAddress address, boolean allowAskingUser) {
      return certificate -> {
         String fingerprint;
         try {
            fingerprint = NetUtils.getFingerprint(certificate);
         } catch (CertificateEncodingException var5) {
            return false;
         }

         if (Objects.equals(GlobalVariables.knownHosts.hosts.get(address.getHostString()), fingerprint)) {
            return true;
         } else {
            GlobalVariables.LOGGER.warn("Received untrusted certificate from server {}!", address.getHostString());
            if (allowAskingUser) {
               boolean trusted = askUserAboutCertificate(address, fingerprint);
               if (trusted) {
                  GlobalVariables.knownHosts.hosts.put(address.getHostString(), fingerprint);
                  ConfigTools.save(GlobalVariables.knownHostsFile, GlobalVariables.knownHosts);
               }

               return trusted;
            } else {
               return false;
            }
         }
      };
   }

   private static Boolean askUserAboutCertificate(InetSocketAddress address, String fingerprint) {
      GlobalVariables.LOGGER.info("Asking user for {}", address.getHostString());
      Optional<Object> screen = new ScreenManager().getScreen();
      if (screen.isEmpty()) {
         GlobalVariables.LOGGER.warn("No screen available, cannot ask user");
         return false;
      } else {
         CountDownLatch latch = new CountDownLatch(1);
         AtomicBoolean accepted = new AtomicBoolean(false);
         Runnable trustCallback = () -> {
            accepted.set(true);
            latch.countDown();
         };
         Runnable cancelCallback = latch::countDown;
         new ScreenManager().validation(screen.get(), fingerprint, trustCallback, cancelCallback);

         try {
            latch.await();
         } catch (InterruptedException var8) {
            return false;
         }

         return accepted.get();
      }
   }

   public static boolean potentiallyMalicious(Jsons.ModpackContentFields serverModpackContent) {
      if (isUnsafePath(serverModpackContent.modpackName, true)) {
         GlobalVariables.LOGGER.error("Modpack content is invalid: modpack name '{}' is unsafe/malicious", serverModpackContent.modpackName);
         return true;
      } else if (serverModpackContent.list != null && !serverModpackContent.list.isEmpty()) {
         boolean listInvalid = serverModpackContent.list.stream().anyMatch(item -> {
            if (isUnsafePath(item.file, false)) {
               GlobalVariables.LOGGER.error("Modpack content is invalid: file path '{}' is unsafe/malicious", item.file);
               return true;
            } else {
               return false;
            }
         });
         boolean nonModpackFilesToDeleteInvalid = serverModpackContent.nonModpackFilesToDelete.stream().anyMatch(item -> {
            if (isUnsafePath(item.file, false)) {
               GlobalVariables.LOGGER.error("Modpack content is invalid: file to delete path '{}' is unsafe/malicious", item.file);
               return true;
            } else {
               return false;
            }
         });
         return listInvalid || nonModpackFilesToDeleteInvalid;
      } else {
         return false;
      }
   }

   private static boolean isUnsafePath(String rawPath, boolean blankIsFine) {
      if (rawPath == null) {
         return true;
      } else if (!blankIsFine && rawPath.isBlank()) {
         return true;
      } else if (rawPath.indexOf(0) != -1) {
         return true;
      } else if (!rawPath.contains("..")) {
         return false;
      } else {
         String normalized = rawPath.replace('\\', '/');
         if (!normalized.equals("..") && !normalized.equals(".")) {
            String[] segments = normalized.split("/");

            for (String segment : segments) {
               if (segment.equals("..")) {
                  return true;
               }
            }

            return normalized.startsWith("automodpack/") || normalized.startsWith("/automodpack/");
         } else {
            return true;
         }
      }
   }

   public static void preserveEditableFiles(Path modpackDir, Set<String> editableFiles, Set<String> newDownloadedFiles) {
      for (String file : editableFiles) {
         if (!newDownloadedFiles.contains(file)) {
            Path path = CustomFileUtils.getPathFromCWD(file);
            if (Files.exists(path)) {
               try {
                  CustomFileUtils.copyFile(path, CustomFileUtils.getPath(modpackDir, file));
               } catch (IOException var7) {
                  var7.printStackTrace();
               }
            }
         }
      }
   }

   public static void copyPreviousEditableFiles(Path modpackDir, Set<String> editableFiles, Set<String> newDownloadedFiles) {
      for (String file : editableFiles) {
         if (!newDownloadedFiles.contains(file) && (!file.contains("/mods/") || !file.endsWith(".jar"))) {
            Path path = CustomFileUtils.getPath(modpackDir, file);
            if (Files.exists(path)) {
               try {
                  CustomFileUtils.copyFile(path, CustomFileUtils.getPathFromCWD(file));
               } catch (IOException var7) {
                  var7.printStackTrace();
               }
            }
         }
      }
   }

   static Set<String> getEditableFiles(Set<Jsons.ModpackContentFields.ModpackContentItem> modpackContentItems) {
      Set<String> editableFiles = new HashSet<>();

      for (Jsons.ModpackContentFields.ModpackContentItem modpackContentItem : modpackContentItems) {
         if (modpackContentItem.editable) {
            editableFiles.add(modpackContentItem.file);
         }
      }

      return editableFiles;
   }

   public record RemoveDupeModsResult(boolean requiresRestart, Set<Path> modsToKeep) {
   }

   public record UpdateCheckResult(boolean requiresUpdate, Set<Jsons.ModpackContentFields.ModpackContentItem> filesToUpdate) {
   }
}
