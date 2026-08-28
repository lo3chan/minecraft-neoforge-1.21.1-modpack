/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.fml.loading.FMLLoader
 *  org.apache.maven.artifact.versioning.DefaultArtifactVersion
 */
package net.diebuddies.bridge;

import java.nio.file.Path;
import net.diebuddies.physics.Version;
import net.neoforged.fml.loading.FMLLoader;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class ModLoaderFunctions {
    public static Version getModID() {
        return new Version(FMLLoader.getLoadingModList().getModFileById("physicsmod").versionString());
    }

    public static boolean isModLoaded(String modID) {
        if (modID.equalsIgnoreCase("optifine") || modID.equalsIgnoreCase("optifabric")) {
            try {
                return Class.forName("net.optifine.VersionCheckThread") != null;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return FMLLoader.getLoadingModList().getModFileById(modID) != null;
    }

    public static boolean isModVersionOrNewer(String modID, String version) {
        try {
            DefaultArtifactVersion artifactVersion = new DefaultArtifactVersion(FMLLoader.getLoadingModList().getModFileById(modID).versionString());
            return artifactVersion.compareTo((Object)new DefaultArtifactVersion(version)) >= 0;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public static Path getGameDir() {
        return FMLLoader.getGamePath();
    }

    public static String getModloader() {
        return "neoforge";
    }
}

