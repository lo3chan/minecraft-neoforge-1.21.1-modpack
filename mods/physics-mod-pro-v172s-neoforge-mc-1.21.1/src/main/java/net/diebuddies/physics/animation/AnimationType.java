/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.animation;

public enum AnimationType {
    Shrink("physicsmod.enum.animation.shrink"),
    Vanish("physicsmod.enum.animation.vanish"),
    Shrink_and_Vanish("physicsmod.enum.animation.shrinkandvanish");

    private String translationId;

    private AnimationType(String translationId) {
        this.translationId = translationId;
    }

    public String toString() {
        return this.translationId;
    }
}

