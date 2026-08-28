/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package mezz.jei.gui.bookmarks;

import java.util.Objects;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.gui.bookmarks.BookmarkType;
import mezz.jei.gui.bookmarks.IBookmark;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.overlay.elements.IngredientBookmarkElement;
import net.minecraft.world.item.ItemStack;

public class IngredientBookmark<T>
implements IBookmark {
    private final IElement<T> element;
    private final Object uid;
    private final ITypedIngredient<T> typedIngredient;
    private boolean visible = true;

    IngredientBookmark(ITypedIngredient<T> typedIngredient, Object uid) {
        this.typedIngredient = typedIngredient;
        this.uid = uid;
        this.element = new IngredientBookmarkElement(this);
    }

    @Override
    public BookmarkType getType() {
        return BookmarkType.INGREDIENT;
    }

    public ITypedIngredient<T> getIngredient() {
        return this.typedIngredient;
    }

    @Override
    public IElement<?> getElement() {
        return this.element;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int hashCode() {
        return Objects.hash(this.uid, this.typedIngredient.getType());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof IngredientBookmark) {
            IngredientBookmark ingredientBookmark = (IngredientBookmark)obj;
            T t = this.typedIngredient.getIngredient();
            if (t instanceof ItemStack) {
                ItemStack stackA = (ItemStack)t;
                t = ingredientBookmark.typedIngredient.getIngredient();
                if (t instanceof ItemStack) {
                    ItemStack stackB = (ItemStack)t;
                    return ItemStack.matches((ItemStack)stackA, (ItemStack)stackB);
                }
            }
            return ingredientBookmark.uid.equals(this.uid) && ingredientBookmark.typedIngredient.getType().equals(this.typedIngredient.getType());
        }
        return false;
    }

    public String toString() {
        return "IngredientBookmark{uid=" + String.valueOf(this.uid) + ", typedIngredient=" + String.valueOf(this.typedIngredient) + ", visible=" + this.visible + "}";
    }
}

