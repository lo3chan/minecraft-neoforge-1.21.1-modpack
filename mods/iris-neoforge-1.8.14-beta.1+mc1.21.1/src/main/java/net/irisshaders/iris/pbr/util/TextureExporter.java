/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  org.apache.commons.io.FilenameUtils
 */
package net.irisshaders.iris.pbr.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FilenameUtils;

public class TextureExporter {
    public static void exportTextures(String directory, String filename, int textureId, int mipLevel, int width, int height) {
        String extension = FilenameUtils.getExtension((String)filename);
        String baseName = filename.substring(0, filename.length() - extension.length() - 1);
        for (int level = 0; level <= mipLevel; ++level) {
            TextureExporter.exportTexture(directory, baseName + "_" + level + "." + extension, textureId, level, width >> level, height >> level);
        }
    }

    public static void exportTexture(String directory, String filename, int textureId, int level, int width, int height) {
        NativeImage nativeImage = new NativeImage(width, height, false);
        RenderSystem.bindTexture((int)textureId);
        nativeImage.downloadTexture(level, false);
        File dir = new File(Minecraft.getInstance().gameDirectory, directory);
        dir.mkdirs();
        File file = new File(dir, filename);
        Util.ioPool().execute(() -> {
            try {
                nativeImage.writeToFile(file);
            }
            catch (Exception exception) {
            }
            finally {
                nativeImage.close();
            }
        });
    }
}

