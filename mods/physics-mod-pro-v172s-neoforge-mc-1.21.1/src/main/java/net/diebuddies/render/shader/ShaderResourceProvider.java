/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackLocationInfo
 *  net.minecraft.server.packs.PackResources
 *  net.minecraft.server.packs.PackResources$ResourceOutput
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.metadata.MetadataSectionSerializer
 *  net.minecraft.server.packs.resources.IoSupplier
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  org.jetbrains.annotations.Nullable
 */
package net.diebuddies.render.shader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

public class ShaderResourceProvider
implements ResourceProvider,
PackResources {
    private static final String NAMESPACE = "physicsmod";

    public Optional<Resource> getResource(ResourceLocation location) {
        return Optional.of(new Resource((PackResources)this, () -> this.getResourceStream(location)));
    }

    public InputStream getResourceStream(ResourceLocation resourceLocation) throws IOException {
        InputStream inputStream = this.processResourceAsStream(resourceLocation);
        if (inputStream != null) {
            return inputStream;
        }
        throw new FileNotFoundException(resourceLocation.getPath());
    }

    protected InputStream processResourceAsStream(ResourceLocation resourceLocation) {
        PackType packType = PackType.CLIENT_RESOURCES;
        String string = ShaderResourceProvider.createPath(packType, resourceLocation);
        try {
            URL url = ShaderResourceProvider.class.getResource(string);
            if (ShaderResourceProvider.isResourceUrlValid(string, url)) {
                return url.openStream();
            }
            return ShaderResourceProvider.class.getResourceAsStream(string);
        }
        catch (IOException iOException) {
            return ShaderResourceProvider.class.getResourceAsStream(string);
        }
    }

    private static String createPath(PackType packType, ResourceLocation resourceLocation) {
        return "/" + packType.getDirectory() + "/physicsmod/" + resourceLocation.getPath();
    }

    private static boolean isResourceUrlValid(String string, @Nullable URL url) throws IOException {
        return url != null && (url.getProtocol().equals("jar") || ShaderResourceProvider.validatePath(new File(url.getFile()), string));
    }

    public static boolean validatePath(File file, String string) throws IOException {
        String string2 = file.getCanonicalPath();
        return string2.endsWith(string);
    }

    public IoSupplier<InputStream> getRootResource(String ... var1) {
        return null;
    }

    public IoSupplier<InputStream> getResource(PackType var1, ResourceLocation var2) {
        return null;
    }

    public void listResources(PackType var1, String var2, String var3, PackResources.ResourceOutput var4) {
    }

    public Set<String> getNamespaces(PackType var1) {
        return null;
    }

    public <T> T getMetadataSection(MetadataSectionSerializer<T> var1) throws IOException {
        return null;
    }

    public String packId() {
        return NAMESPACE;
    }

    public void close() {
    }

    public PackLocationInfo location() {
        return null;
    }
}

