/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.util;

public class ObjectPacked {
    public Object e1;
    public Object e2;

    public ObjectPacked(Object e1, Object e2) {
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
        ObjectPacked other = (ObjectPacked)obj;
        if (this.e1 == null ? other.e1 != null : !this.e1.equals(other.e1)) {
            return false;
        }
        return !(this.e2 == null ? other.e2 != null : !this.e2.equals(other.e2));
    }
}

