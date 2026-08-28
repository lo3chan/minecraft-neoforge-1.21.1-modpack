/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.modshade.net.mezzdev.suffixtree;

import mezz.jei.modshade.net.mezzdev.suffixtree.Node;
import mezz.jei.modshade.net.mezzdev.suffixtree.SubString;

public class RootNode<T>
extends Node<T> {
    public RootNode() {
        super(new SubString(""));
    }

    @Override
    protected boolean contains(T value) {
        return true;
    }

    @Override
    protected void addValue(T value) {
    }
}

