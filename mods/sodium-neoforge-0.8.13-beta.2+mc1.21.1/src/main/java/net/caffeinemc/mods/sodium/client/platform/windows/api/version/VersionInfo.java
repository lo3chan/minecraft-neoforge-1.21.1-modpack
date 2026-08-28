/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.system.MemoryUtil
 */
package net.caffeinemc.mods.sodium.client.platform.windows.api.version;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Locale;
import net.caffeinemc.mods.sodium.client.platform.windows.api.version.LanguageCodePage;
import net.caffeinemc.mods.sodium.client.platform.windows.api.version.QueryResult;
import net.caffeinemc.mods.sodium.client.platform.windows.api.version.Version;
import net.caffeinemc.mods.sodium.client.platform.windows.api.version.VersionFixedFileInfoStruct;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public class VersionInfo
implements Closeable {
    private final ByteBuffer pBlock;

    VersionInfo(ByteBuffer buffer) {
        this.pBlock = buffer;
    }

    public static VersionInfo allocate(int len) {
        return new VersionInfo(MemoryUtil.memAlignedAlloc((int)16, (int)len));
    }

    @Nullable
    public String queryValue(String key, LanguageCodePage translation) {
        QueryResult result = Version.query(this.pBlock, VersionInfo.getStringFileInfoPath(key, translation));
        if (result == null) {
            return null;
        }
        return MemoryUtil.memUTF16((long)result.address());
    }

    @Nullable
    public VersionFixedFileInfoStruct queryFixedFileInfo() {
        QueryResult result = Version.query(this.pBlock, "\\");
        if (result == null) {
            return null;
        }
        return VersionFixedFileInfoStruct.from(result.address());
    }

    @Nullable
    public LanguageCodePage queryEnglishTranslation() {
        QueryResult result = Version.query(this.pBlock, "\\VarFileInfo\\Translation");
        if (result == null) {
            return null;
        }
        return VersionInfo.findEnglishTranslationEntry(result);
    }

    @Nullable
    private static LanguageCodePage findEnglishTranslationEntry(QueryResult result) {
        LanguageCodePage translation = null;
        for (int offset = 0; offset < result.length(); offset += 4) {
            translation = LanguageCodePage.decode(result.address() + (long)offset);
            if (translation.codePage() != 1200 || translation.languageId() != 1033) continue;
            return translation;
        }
        return translation;
    }

    private static String getStringFileInfoPath(String key, LanguageCodePage translation) {
        return String.format(Locale.ROOT, "\\StringFileInfo\\%04x%04x\\%s", translation.languageId(), translation.codePage(), key);
    }

    @Override
    public void close() {
        MemoryUtil.memAlignedFree((ByteBuffer)this.pBlock);
    }

    long address() {
        return MemoryUtil.memAddress((ByteBuffer)this.pBlock);
    }
}

