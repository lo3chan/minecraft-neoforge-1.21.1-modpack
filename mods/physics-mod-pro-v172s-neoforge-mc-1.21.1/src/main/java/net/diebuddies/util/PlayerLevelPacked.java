/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 */
package net.diebuddies.util;

import net.minecraft.world.level.Level;

public class PlayerLevelPacked {
    public String e1;
    public Level e2;

    public PlayerLevelPacked(String e1, Level e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.e1 == null ? 0 : this.e1.hashCode());
        result = 31 * result + (this.e2 == null ? 0 : this.e2.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        PlayerLevelPacked other = (PlayerLevelPacked)obj;
        if (this.e1 == null ? other.e1 != null : !this.e1.equals(other.e1)) {
            return false;
        }
        return !(this.e2 == null ? other.e2 != null : !this.e2.equals(other.e2));
    }
}

