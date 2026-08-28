/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.language.LanguageManager
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.util;

import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageManager;
import org.jetbrains.annotations.Nullable;

public class MinecraftLocaleSupplier
implements Supplier<Locale> {
    @Nullable
    private String cachedLocaleCode;
    @Nullable
    private Locale cachedLocale;

    @Override
    public Locale get() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return Locale.getDefault();
        }
        LanguageManager languageManager = minecraft.getLanguageManager();
        String code = languageManager.getSelected();
        if (this.cachedLocale == null || !code.equals(this.cachedLocaleCode)) {
            this.cachedLocaleCode = code;
            String[] splitLangCode = code.split("_", 2);
            this.cachedLocale = splitLangCode.length == 1 ? Locale.of((String)code) : Locale.of((String)splitLangCode[0], (String)splitLangCode[1]);
        }
        return this.cachedLocale;
    }
}

