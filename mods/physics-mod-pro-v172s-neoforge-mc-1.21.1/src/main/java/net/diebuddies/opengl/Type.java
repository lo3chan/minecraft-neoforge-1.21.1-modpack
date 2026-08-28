/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.opengl;

public enum Type {
    DATA(34962),
    INDEX(34963),
    UNIFORM(35345),
    TEXTURE(35882),
    DOWNLOAD(35051),
    UPLOAD(35052);

    private int type;

    private Type(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}

