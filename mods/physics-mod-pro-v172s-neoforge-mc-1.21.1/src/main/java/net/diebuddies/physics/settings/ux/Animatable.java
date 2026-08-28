/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.settings.ux;

import java.util.List;
import net.diebuddies.physics.settings.ux.Animator;

public interface Animatable {
    public Animatable addAnimator(Animator var1);

    public Animatable addAnimator(int var1, Animator var2);

    public Animatable addAnimator(Animator ... var1);

    public Animatable addAnimator(List<Animator> var1);

    public Animatable removeAnimator(Animator var1);

    public List<Animator> getAnimators();

    public <T extends Animator> T getAnimator(Class<T> var1);

    public float getAnimX();

    public Animatable setAnimX(float var1);

    public float getAnimY();

    public Animatable setAnimY(float var1);

    public float getAnimWidth();

    public Animatable setAnimWidth(float var1);

    public float getAnimHeight();

    public Animatable setAnimHeight(float var1);

    public float getAnimRed();

    public Animatable setAnimRed(float var1);

    public float getAnimGreen();

    public Animatable setAnimGreen(float var1);

    public float getAnimBlue();

    public Animatable setAnimBlue(float var1);

    public float getAnimAlpha();

    public Animatable setAnimAlpha(float var1);

    public Animatable setAnimColor(float var1, float var2, float var3, float var4);

    public float getAnimDepth();

    public Animatable setAnimDepth(float var1);

    public boolean isInside(double var1, double var3);
}

