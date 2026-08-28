/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package mezz.jei.api.runtime.config;

import java.util.function.Consumer;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;
import net.minecraft.network.chat.Component;

public interface IJeiConfigValue<T> {
    public String getName();

    @Deprecated(since="19.21.0", forRemoval=true)
    public String getDescription();

    public Component getLocalizedName();

    public Component getLocalizedDescription();

    public T getValue();

    public T getDefaultValue();

    public boolean set(T var1);

    default public void addListener(Consumer<T> listener) {
    }

    public IJeiConfigValueSerializer<T> getSerializer();
}

