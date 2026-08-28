/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3i
 */
package net.diebuddies.dualcontouring;

import net.diebuddies.dualcontouring.OctreeDrawInfo;
import net.diebuddies.dualcontouring.OctreeNodeType;
import org.joml.Vector3i;

public class OctreeNode {
    public OctreeNode[] children = new OctreeNode[8];
    public OctreeNodeType type = OctreeNodeType.NONE;
    public OctreeDrawInfo drawInfo;
    public Vector3i min = new Vector3i();
    public int size;

    public String toString() {
        return this.generateString(0);
    }

    public OctreeNode reset() {
        for (int i = 0; i < 8; ++i) {
            this.children[i] = null;
        }
        this.size = 0;
        this.type = OctreeNodeType.NONE;
        return this;
    }

    public String generateString(int spaces) {
        int i;
        Object spacesString = "";
        for (int i2 = 0; i2 < spaces; ++i2) {
            spacesString = (String)spacesString + " ";
        }
        int childrenCount = 0;
        for (i = 0; i < this.children.length; ++i) {
            if (this.children[i] == null) continue;
            ++childrenCount;
        }
        spacesString = (String)spacesString + String.valueOf(this.min) + ", size: " + this.size + ", draw info: " + String.valueOf(this.drawInfo) + ", children: " + childrenCount + ", type: " + String.valueOf((Object)this.type) + "\n";
        for (i = 0; i < this.children.length; ++i) {
            if (this.children[i] == null) continue;
            spacesString = (String)spacesString + this.children[i].generateString(spaces + 1);
        }
        return spacesString;
    }

    public int getChildrenSize() {
        int childrenSize = 0;
        for (int i = 0; this.children != null && i < this.children.length; ++i) {
            if (this.children[i] == null) continue;
            ++childrenSize;
            childrenSize += this.children[i].getChildrenSize();
        }
        return childrenSize;
    }

    public int getChildrenSize(OctreeNodeType type) {
        int childrenSize = 0;
        for (int i = 0; this.children != null && i < this.children.length; ++i) {
            if (this.children[i] == null) continue;
            if (this.children[i].type == type) {
                ++childrenSize;
            }
            childrenSize += this.children[i].getChildrenSize(type);
        }
        return childrenSize;
    }

    public OctreeNode copy() {
        OctreeNode node = new OctreeNode();
        node.type = this.type;
        node.children = this.children;
        node.type = this.type;
        node.drawInfo = this.drawInfo;
        node.min = this.min;
        node.size = this.size;
        return node;
    }
}

