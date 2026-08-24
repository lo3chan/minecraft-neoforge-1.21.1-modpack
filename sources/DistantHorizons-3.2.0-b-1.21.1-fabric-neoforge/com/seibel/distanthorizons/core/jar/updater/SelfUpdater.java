package com.seibel.distanthorizons.core.jar.updater;

import com.seibel.distanthorizons.api.enums.config.EDhApiUpdateBranch;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.jar.JarUtils;
import com.seibel.distanthorizons.core.jar.ModJarInfo;
import com.seibel.distanthorizons.core.jar.installer.GitlabGetter;
import com.seibel.distanthorizons.core.jar.installer.ModrinthGetter;
import com.seibel.distanthorizons.core.jar.installer.WebDownloader;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.IVersionConstants;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.coreapi.util.StringUtil;
import com.seibel.distanthorizons.coreapi.util.jar.DeleteOnUnlock;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SelfUpdater {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final IVersionConstants VERSION_CONSTANTS = SingletonInjector.INSTANCE.get(IVersionConstants.class);
   private static final String MC_VERSION = VERSION_CONSTANTS.getMinecraftVersion();
   public static boolean deleteOldJarOnJvmShutdown = false;
   public static File newFileLocation;

   public static boolean onStart() {
      if (!Config.Client.Advanced.AutoUpdater.enableAutoUpdater.get()) {
         LOGGER.info("Distant Horizons auto update disabled.");
         return false;
      } else {
         try {
            EDhApiUpdateBranch updateBranch = EDhApiUpdateBranch.convertAutoToStableOrNightly(Config.Client.Advanced.AutoUpdater.updateBranch.get());
            LOGGER.info("Checking for Distant Horizons [" + updateBranch + "] update for MC [" + MC_VERSION + "]...");
            return updateBranch == EDhApiUpdateBranch.STABLE ? onStableStart() : onNightlyStart();
         } catch (Exception var1) {
            LOGGER.warn("Unexpected updater startup error: [" + var1.getMessage() + "].", var1);
            return false;
         }
      }
   }

   private static boolean onStableStart() {
      if (!ModrinthGetter.init()) {
         LOGGER.warn("Unable to find any stable builds, auto update will be unavailable.");
         return false;
      } else if (!ModrinthGetter.mcVersions.contains(MC_VERSION)) {
         LOGGER.warn(
            "Minecraft version ["
               + MC_VERSION
               + "] is not findable on Modrinth, only findable versions are ["
               + StringUtil.join(", ", ModrinthGetter.mcVersions)
               + "]"
         );
         return false;
      } else {
         try {
            newFileLocation = JarUtils.jarFile
               .getParentFile()
               .toPath()
               .resolve("update")
               .resolve("DistantHorizons-" + ModrinthGetter.getLatestNameForVersion(MC_VERSION) + "-" + MC_VERSION + ".jar")
               .toFile();
         } catch (Exception var3) {
            LOGGER.warn("Unable to get file location to download auto updated file to.", var3);
            return false;
         }

         String currentJarSha;
         try {
            currentJarSha = JarUtils.getFileChecksum(MessageDigest.getInstance("SHA"), JarUtils.jarFile);
         } catch (Exception var2) {
            LOGGER.error("Unable to get existing jar checksum, error: [" + var2.getMessage() + "].", var2);
            return false;
         }

         if (currentJarSha.equals(ModrinthGetter.getLatestShaForVersion(MC_VERSION))) {
            LOGGER.info("Distant Horizons already up to date.");
            return false;
         } else if (JarUtils.jarFile == null) {
            LOGGER.warn("Unable to get the Distant Horizons jar file, self updating disabled.");
            return false;
         } else {
            LOGGER.info("New version (" + ModrinthGetter.getLatestNameForVersion(MC_VERSION) + ") of Distant Horizons is available");
            if (Config.Client.Advanced.AutoUpdater.enableSilentUpdates.get()) {
               updateMod(MC_VERSION, newFileLocation);
               return false;
            } else {
               LOGGER.info("Download link: " + ModrinthGetter.getLatestDownloadForVersion(MC_VERSION));
               return true;
            }
         }
      }
   }

   private static boolean onNightlyStart() {
      LOGGER.info("Checking for Distant Horizons Nightly update...");
      if (GitlabGetter.INSTANCE.projectPipelines.size() == 0) {
         LOGGER.info("Unable to find any nightly build pipelines, auto update will be unavailable.");
         return false;
      } else {
         DistantHorizons.libraries.electronwill.nightconfig.core.Config pipeline = GitlabGetter.INSTANCE.projectPipelines.get(0);
         if (!pipeline.get("ref").equals(ModJarInfo.Git_Branch)) {
            LOGGER.warn("Latest pipeline was found for branch [" + pipeline.get("ref") + "], but we are on branch [" + ModJarInfo.Git_Branch + "].");
            return false;
         } else if (!pipeline.get("status").equals("success")) {
            LOGGER.warn(
               "Pipeline for branch ["
                  + ModJarInfo.Git_Branch
                  + "], pipeline ID ["
                  + pipeline.get("id")
                  + "], has either failed to build, or is still building."
            );
            return false;
         } else if (!GitlabGetter.INSTANCE.getDownloads(pipeline.get("id")).containsKey(MC_VERSION)) {
            LOGGER.warn(
               "Minecraft version ["
                  + MC_VERSION
                  + "] is not findable on Gitlab, findable versions are ["
                  + StringUtil.join(", ", GitlabGetter.INSTANCE.getDownloads(pipeline.get("id")).keySet().toArray())
                  + "]."
            );
            return false;
         } else {
            String latestCommit = pipeline.get("sha");

            try {
               newFileLocation = JarUtils.jarFile.getParentFile().toPath().resolve("update").resolve("DistantHorizons-" + latestCommit + ".jar").toFile();
            } catch (Exception var3) {
               LOGGER.warn("Unable to get file location to download auto updated file to.", var3);
               return false;
            }

            if (ModJarInfo.Git_Commit.equals(latestCommit)) {
               LOGGER.info("Distant Horizons already up to date.");
               return false;
            } else {
               LOGGER.info("New version [" + latestCommit + "] of Distant Horizons is available");
               if (Config.Client.Advanced.AutoUpdater.enableSilentUpdates.get()) {
                  updateMod(MC_VERSION, newFileLocation);
                  return false;
               } else {
                  LOGGER.info("Download link: " + GitlabGetter.getLatestForVersion(MC_VERSION));
                  return true;
               }
            }
         }
      }
   }

   public static boolean updateMod() {
      String mcVer = SingletonInjector.INSTANCE.get(IVersionConstants.class).getMinecraftVersion();
      return updateMod(mcVer, newFileLocation);
   }

   public static boolean updateMod(String minecraftVersion, File file) {
      EDhApiUpdateBranch updateBranch = EDhApiUpdateBranch.convertAutoToStableOrNightly(Config.Client.Advanced.AutoUpdater.updateBranch.get());
      if (updateBranch == EDhApiUpdateBranch.STABLE) {
         return updateStableMod(minecraftVersion, file);
      } else if (updateBranch == EDhApiUpdateBranch.NIGHTLY) {
         return updateNightlyMod(minecraftVersion, file);
      } else {
         LOGGER.error("Unable to update due to unimplemented update branch [" + updateBranch + "].");
         return false;
      }
   }

   public static boolean updateStableMod(String minecraftVersion, File file) {
      try {
         LOGGER.info("Attempting to auto update Distant Horizons");
         Files.createDirectories(file.getParentFile().toPath());
         WebDownloader.downloadAsFile(ModrinthGetter.getLatestDownloadForVersion(minecraftVersion), file);
         if (!JarUtils.getFileChecksum(MessageDigest.getInstance("SHA"), file).equals(ModrinthGetter.getLatestShaForVersion(minecraftVersion))) {
            LOGGER.warn("Distant Horizons update checksum failed, aborting install");
            throw new Exception("Checksum failed");
         } else {
            deleteOldJarOnJvmShutdown = true;
            String successMessage = "Distant Horizons successfully updated. It will apply on game`s relaunch";
            LOGGER.info(successMessage);
            new Thread(() -> {
               try {
                  MC_CLIENT.showDialog("Distant Horizons", successMessage, "ok", "info");
               } catch (Exception var2x) {
               }
            }).start();
            return true;
         }
      } catch (Exception var7) {
         try {
            Files.deleteIfExists(file.toPath());
         } catch (Exception var6) {
            LOGGER.error("Unable to delete corrupted update file at [" + file.toPath() + "], error: [" + var6.getMessage() + "].", var6);
         }

         String failMessage = "Failed to update Distant Horizons to version ["
            + ModrinthGetter.getLatestNameForVersion(minecraftVersion)
            + "], error: ["
            + var7.getMessage()
            + "].";
         LOGGER.error(failMessage, var7);

         try {
            MC_CLIENT.showDialog("Distant Horizons", failMessage, "ok", "error");
         } catch (Exception var5) {
         }

         return false;
      }
   }

   public static boolean updateNightlyMod(String minecraftVersion, File file) {
      if (GitlabGetter.INSTANCE.projectPipelines.isEmpty()) {
         LOGGER.warn("Failed to find any nightly builds for the minecraft version [" + minecraftVersion + "] update canceled.");
         return false;
      } else {
         Path mergedZipPath = null;

         try {
            LOGGER.info("Attempting to auto update Distant Horizons.");
            Files.createDirectories(file.getParentFile().toPath());
            mergedZipPath = file.getParentFile().toPath().resolve("merged.zip");
            WebDownloader.downloadAsFile(
               GitlabGetter.INSTANCE.getDownloads(GitlabGetter.INSTANCE.projectPipelines.get(0).get("id")).get(minecraftVersion), mergedZipPath.toFile()
            );
            ZipFile zipFile = new ZipFile(mergedZipPath.toFile());

            try {
               ZipEntry zipEntry = Collections.list(zipFile.entries())
                  .stream()
                  .max(Comparator.comparingInt(entry -> entry.getName().length()))
                  .orElseThrow(() -> new Exception("Unable to find jar in zip. Is the downloaded zip empty?"));
               long expectedCheckSum = zipEntry.getCrc();
               int expectedSize = (int)zipEntry.getSize();
               byte[] buffer = new byte[expectedSize];
               CRC32 crcCheckSumGenerator = new CRC32();
               InputStream inputStream = zipFile.getInputStream(zipEntry);
               int byteReadIndex = 0;

               try {
                  NumberFormat outputFormat = NumberFormat.getNumberInstance();
                  int nextByte = inputStream.read();

                  while (nextByte != -1) {
                     buffer[byteReadIndex] = (byte)nextByte;
                     crcCheckSumGenerator.update(nextByte);
                     nextByte = inputStream.read();
                     if (++byteReadIndex % 100000 == 0) {
                        LOGGER.info("Decompressing [" + outputFormat.format((double)byteReadIndex / expectedSize * 100.0) + "]%");
                     }
                  }
               } catch (EOFException var18) {
               }

               if (byteReadIndex != expectedSize) {
                  LOGGER.warn("Distant Horizons update decompression failed, aborting install");
                  throw new Exception("Decompression failed");
               }

               long actualChecksum = crcCheckSumGenerator.getValue();
               if (actualChecksum != expectedCheckSum) {
                  LOGGER.warn("Distant Horizons checksum mismatch, aborting install");
                  throw new Exception("Checksum Mismatch");
               }

               Files.write(file.toPath(), buffer);
            } catch (Throwable var19) {
               try {
                  zipFile.close();
               } catch (Throwable var17) {
                  var19.addSuppressed(var17);
               }

               throw var19;
            }

            zipFile.close();
            Files.deleteIfExists(mergedZipPath);
            deleteOldJarOnJvmShutdown = true;
            String var21 = "Distant Horizons updated, this will be applied on game restart.";
            LOGGER.info(var21);
            new Thread(() -> {
               try {
                  MC_CLIENT.showDialog("Distant Horizons", var21, "ok", "info");
               } catch (Exception var2x) {
               }
            }).start();
            return true;
         } catch (Exception var20) {
            try {
               Files.deleteIfExists(file.toPath());
            } catch (Exception var16) {
               LOGGER.error("Unable to delete corrupted update jar file at [" + file.toPath() + "], error: [" + var16.getMessage() + "].", var16);
            }

            try {
               if (mergedZipPath != null) {
                  Files.deleteIfExists(mergedZipPath);
               }
            } catch (Exception var15) {
               LOGGER.error("Unable to delete corrupted update zip file at [" + mergedZipPath + "], error: [" + var15.getMessage() + "].", var15);
            }

            String versionHash = GitlabGetter.INSTANCE.projectPipelines.get(0).get("sha");
            String failMessage = "Failed to update [Distant Horizons] to version [" + versionHash + "], error: [" + var20.getMessage() + "].";
            LOGGER.error(failMessage, var20);

            try {
               MC_CLIENT.showDialog("Distant Horizons", failMessage, "ok", "error");
            } catch (Exception var14) {
            }

            return false;
         }
      }
   }

   public static void onClose() {
      if (deleteOldJarOnJvmShutdown) {
         if (JarUtils.jarFile != null) {
            Path newJarPath = newFileLocation.toPath();
            Path finalJarPath = JarUtils.jarFile.getParentFile().toPath().resolve(newFileLocation.getName());

            try {
               Files.deleteIfExists(finalJarPath);
               Files.move(newJarPath, finalJarPath);
               Files.delete(newFileLocation.getParentFile().toPath());
            } catch (Exception var11) {
               LOGGER.warn(
                  "Failed to move updated fire from ["
                     + newFileLocation.getAbsolutePath()
                     + "] to ["
                     + JarUtils.jarFile.getParentFile().getAbsolutePath()
                     + "], please move it manually",
                  var11
               );
            }

            try {
               String javaHome = System.getProperty("java.home");
               String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
               String execCommand = "\""
                  + javaBin
                  + "\" -cp \""
                  + finalJarPath.toAbsolutePath()
                  + "\" "
                  + DeleteOnUnlock.class.getCanonicalName()
                  + " "
                  + URLEncoder.encode(JarUtils.jarFile.getAbsolutePath(), "UTF-8");
               Process deleteProcess = Runtime.getRuntime().exec(execCommand);
               if (deleteProcess.isAlive()) {
                  LOGGER.info(DeleteOnUnlock.class.getSimpleName() + " process started...");
               } else {
                  LOGGER.error(DeleteOnUnlock.class.getSimpleName() + " process failed to start.");
               }

               Thread.sleep(250L);
               if (deleteProcess.isAlive()) {
                  LOGGER.info(
                     DeleteOnUnlock.class.getSimpleName()
                        + " running, old jar file at ["
                        + JarUtils.jarFile.getAbsolutePath()
                        + "] should be deleted after Minecraft's JVM shutdown has completed."
                  );
               } else {
                  int processExitCode = deleteProcess.exitValue();
                  if (processExitCode != DeleteOnUnlock.SUCCESS_EXIT_CODE) {
                     String failReason = processExitCode == DeleteOnUnlock.FAIL_EXIT_CODE
                        ? "Timed out and was unable to delete the file."
                        : "Ran into an unexpected error.";
                     LOGGER.error(DeleteOnUnlock.class.getSimpleName() + " " + failReason);
                     LOGGER.error(DeleteOnUnlock.class.getSimpleName() + " Logs are listed below:");
                     String normalOutput = convertInputStreamToString(deleteProcess.getInputStream());
                     LOGGER.info("process output: \n\n" + normalOutput);
                     String errorOutput = convertInputStreamToString(deleteProcess.getInputStream());
                     LOGGER.error("process error output: \n\n" + errorOutput);
                  } else {
                     LOGGER.info(DeleteOnUnlock.class.getSimpleName() + " completed before JVM shutdown.");
                  }
               }
            } catch (Exception var10) {
               LOGGER.warn("Failed to delete old jar using bootstrap method, doing backup 'Files.deleteOnExit()' method", var10);
               JarUtils.jarFile.deleteOnExit();
               LOGGER.warn("If the old Distant Horizons file didn't delete, delete it manually at [" + JarUtils.jarFile + "]");
            }
         }
      }
   }

   private static String convertInputStreamToString(InputStream inputStream) {
      try {
         byte[] bytes = new byte[inputStream.available()];
         DataInputStream dataInputStream = new DataInputStream(inputStream);
         dataInputStream.readFully(bytes);
         return new String(bytes);
      } catch (IOException var3) {
         return var3.getMessage();
      }
   }
}
