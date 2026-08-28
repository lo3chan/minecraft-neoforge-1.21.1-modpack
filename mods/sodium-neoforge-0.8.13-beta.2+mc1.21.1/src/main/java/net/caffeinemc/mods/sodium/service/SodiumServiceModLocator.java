/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.jarhandling.JarContents
 *  net.neoforged.neoforgespi.ILaunchContext
 *  net.neoforged.neoforgespi.locating.IDiscoveryPipeline
 *  net.neoforged.neoforgespi.locating.IModFileCandidateLocator
 *  net.neoforged.neoforgespi.locating.IncompatibleFileReporting
 *  net.neoforged.neoforgespi.locating.ModFileDiscoveryAttributes
 */
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

public class SodiumServiceModLocator
implements IModFileCandidateLocator {
    private static final String JIJ_DIR = "META-INF/jarjar";

    public void findCandidates(ILaunchContext context, IDiscoveryPipeline pipeline) {
        Path jijDir = SodiumServiceModLocator.locateServiceRoot().resolve(JIJ_DIR);
        try (Stream<Path> entries = Files.list(jijDir);){
            entries.filter(SodiumServiceModLocator::isJar).forEach(entry -> SodiumServiceModLocator.mountAndAdd(entry, pipeline));
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to list JiJ entries in " + String.valueOf(jijDir), e);
        }
    }

    private static void mountAndAdd(Path innerJarInsideService, IDiscoveryPipeline pipeline) {
        try {
            String specific = innerJarInsideService.toAbsolutePath().toUri().getRawSchemeSpecificPart();
            URI jijUri = new URI("jij:" + specific).normalize();
            FileSystem innerFs = FileSystems.newFileSystem(jijUri, Map.of("packagePath", innerJarInsideService));
            JarContents contents = JarContents.of((Path)innerFs.getPath("/", new String[0]));
            pipeline.addJarContent(contents, ModFileDiscoveryAttributes.DEFAULT, IncompatibleFileReporting.WARN_ALWAYS);
        }
        catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Failed to add JiJ entry " + String.valueOf(innerJarInsideService.getFileName()) + " to mod discovery", e);
        }
    }

    private static boolean isJar(Path path) {
        return path.getFileName().toString().toLowerCase().endsWith(".jar");
    }

    private static Path locateServiceRoot() {
        URI csUri;
        CodeSource cs = SodiumServiceModLocator.class.getProtectionDomain().getCodeSource();
        if (cs == null || cs.getLocation() == null) {
            throw new IllegalStateException("CodeSource unavailable; cannot resolve sodium service jar.");
        }
        try {
            csUri = cs.getLocation().toURI();
        }
        catch (URISyntaxException e) {
            throw new IllegalStateException("Could not parse CodeSource location URI " + String.valueOf(cs.getLocation()), e);
        }
        try {
            return Paths.get(csUri);
        }
        catch (Exception e) {
            throw new IllegalStateException("Could not resolve sodium service jar from CodeSource URI " + String.valueOf(csUri), e);
        }
    }
}

