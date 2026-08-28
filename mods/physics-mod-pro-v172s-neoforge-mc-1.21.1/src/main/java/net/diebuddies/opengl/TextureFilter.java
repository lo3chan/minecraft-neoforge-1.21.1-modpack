/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.opengl;

public class TextureFilter {
    public boolean generateMipMaps;
    public boolean anisotropic;
    public int[] options;

    public TextureFilter(boolean generateMipMaps, boolean anisotropic, int ... options) {
        this.generateMipMaps = generateMipMaps;
        this.anisotropic = anisotropic;
        this.options = options;
    }

    public TextureFilter(boolean generateMipMaps, int ... options) {
        this(generateMipMaps, false, options);
    }

    public TextureFilter(int ... options) {
        this(false, options);
    }

    public String toString() {
        String s = "new TextureFilter(" + this.generateMipMaps + ", " + this.anisotropic;
        if (this.options != null && this.options.length > 0) {
            s = s + ", ";
            for (int i = 0; i < this.options.length - 1; ++i) {
                s = s + Integer.toString(this.options[i]) + ", ";
            }
            s = s + Integer.toString(this.options[this.options.length - 1]);
        }
        s = s + ")";
        return s;
    }
}

