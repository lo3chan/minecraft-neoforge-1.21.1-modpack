/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 */
package mezz.jei.api.registration;

import mezz.jei.api.search.ISearchStorageBuilderFactory;
import mezz.jei.api.search.ISearchStorageFactory;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IAdvancedSearchRegistration {
    public ISearchStorageBuilderFactory getDefaultSearchStorageBuilderFactory();

    public void replaceSearchStorage(ISearchStorageFactory var1);

    public void replaceSearchStorage(ISearchStorageBuilderFactory var1);
}

