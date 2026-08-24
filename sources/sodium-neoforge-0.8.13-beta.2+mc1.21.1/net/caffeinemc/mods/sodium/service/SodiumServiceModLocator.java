package net.caffeinemc.mods.sodium.service;

import cpw.mods.jarhandling.JarContents;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Map;
import java.util.stream.Stream;
import net.neoforged.neoforgespi.ILaunchContext;
import net.neoforged.neoforgespi.locating.IDiscoveryPipeline;
import net.neoforged.neoforgespi.locating.IModFileCandidateLocator;
import net.neoforged.neoforgespi.locating.IncompatibleFileReporting;
import net.neoforged.neoforgespi.locating.ModFileDiscoveryAttributes;

public class SodiumServiceModLocator implements IModFileCandidateLocator {
   private static final String JIJ_DIR = "META-INF/jarjar";

   public void findCandidates(ILaunchContext context, IDiscoveryPipeline pipeline) {
      Path jijDir = locateServiceRoot().resolve("META-INF/jarjar");

      try {
         try (Stream<Path> entries = Files.list(jijDir)) {
            entries.filter(SodiumServiceModLocator::isJar).forEach(entry -> mountAndAdd(entry, pipeline));
         }
      } catch (IOException var9) {
         throw new IllegalStateException("Failed to list JiJ entries in " + jijDir, var9);
      }
   }

   private static void mountAndAdd(Path innerJarInsideService, IDiscoveryPipeline pipeline) {
      try {
         String specific = innerJarInsideService.toAbsolutePath().toUri().getRawSchemeSpecificPart();
         URI jijUri = new URI("jij:" + specific).normalize();
         FileSystem innerFs = FileSystems.newFileSystem(jijUri, Map.of("packagePath", innerJarInsideService));
         JarContents contents = JarContents.of(innerFs.getPath("/"));
         pipeline.addJarContent(contents, ModFileDiscoveryAttributes.DEFAULT, IncompatibleFileReporting.WARN_ALWAYS);
      } catch (IOException | URISyntaxException var6) {
         throw new IllegalStateException("Failed to add JiJ entry " + innerJarInsideService.getFileName() + " to mod discovery", var6);
      }
   }

   private static boolean isJar(Path path) {
      return path.getFileName().toString().toLowerCase().endsWith(".jar");
   }

   private static Path locateServiceRoot() {
      CodeSource cs = SodiumServiceModLocator.class.getProtectionDomain().getCodeSource();
      if (cs != null && cs.getLocation() != null) {
         URI csUri;
         try {
            csUri = cs.getLocation().toURI();
         } catch (URISyntaxException var4) {
            throw new IllegalStateException("Could not parse CodeSource location URI " + cs.getLocation(), var4);
         }

         try {
            return Paths.get(csUri);
         } catch (Exception var3) {
            throw new IllegalStateException("Could not resolve sodium service jar from CodeSource URI " + csUri, var3);
         }
      } else {
         throw new IllegalStateException("CodeSource unavailable; cannot resolve sodium service jar.");
      }
   }
}
