/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.ingredients;

import mezz.jei.api.ingredients.ITypedIngredient;

public interface IListElement<V> {
    public ITypedIngredient<V> getTypedIngredient();

    public int getSortedIndex();

    public void setSortedIndex(int var1);

    public int getCreatedIndex();

    public boolean isVisible();

    public void setVisible(boolean var1);
}

