/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntIterator
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3i
 *  org.joml.Vector3ic
 *  org.lwjgl.system.MemoryUtil
 */
package net.diebuddies.physics.snow.contouring;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.ByteBuffer;
import java.util.List;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.opengl.Pack;
import net.diebuddies.opengl.RawMesh;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.snow.ChunkContouring;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.IWorld;
import net.diebuddies.physics.snow.WorldUtil;
import net.diebuddies.physics.snow.contouring.OctreeDrawInfo;
import net.diebuddies.physics.snow.contouring.OctreeNode;
import net.diebuddies.physics.snow.contouring.SimplePoolVertex;
import net.diebuddies.physics.snow.contouring.Vertex;
import net.diebuddies.physics.snow.math.AABB3D;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.lwjgl.system.MemoryUtil;

public class DualContouring {
    private final float NORMAL_SMOOTHNESS = 2.0f;
    private static final int MATERIAL_SOLID = 0;
    private static final int MATERIAL_AIR = 1;
    private static final byte[] CELL_PROC_FACE_MASK = new byte[]{0, 4, 1, 5, 2, 6, 3, 7, 0, 2, 4, 6, 1, 3, 5, 7, 0, 1, 2, 3, 4, 5, 6, 7};
    private static final byte[] CELL_PROC_EDGE_MASK = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 0, 4, 1, 5, 2, 6, 3, 7, 0, 2, 4, 6, 1, 3, 5, 7};
    private static final byte[] FACE_PROC_FACE_MASK = new byte[]{4, 0, 5, 1, 6, 2, 7, 3, 2, 0, 6, 4, 3, 1, 7, 5, 1, 0, 3, 2, 5, 4, 7, 6};
    private static final byte[] FACE_PROC_EDGE_MASK = new byte[]{1, 4, 0, 5, 1, 1, 1, 6, 2, 7, 3, 1, 0, 4, 6, 0, 2, 2, 0, 5, 7, 1, 3, 2, 0, 2, 3, 0, 1, 0, 0, 6, 7, 4, 5, 0, 1, 2, 0, 6, 4, 2, 1, 3, 1, 7, 5, 2, 1, 1, 0, 3, 2, 0, 1, 5, 4, 7, 6, 0, 0, 1, 5, 0, 4, 1, 0, 3, 7, 2, 6, 1};
    private static final byte[] EDGE_PROC_EDGE_MASK = new byte[]{3, 2, 1, 0, 7, 6, 5, 4, 5, 1, 4, 0, 7, 3, 6, 2, 6, 4, 2, 0, 7, 5, 3, 1};
    private static final byte[] PROCESS_EDGE_MASK = new byte[]{6, 4, 2, 0, 14, 10, 12, 8, 22, 20, 18, 16};
    private static final byte[] EDGE_V_MAP = new byte[]{0, 4, 1, 5, 2, 6, 3, 7, 0, 2, 1, 3, 4, 6, 5, 7, 0, 1, 2, 3, 4, 5, 6, 7};
    private static final byte[][] UNIQUE_CORNERS = new byte[][]{new byte[0], {0, 1, 2, 4}, {0, 1, 3, 5}, {0, 1, 2, 3, 4, 5}, {0, 2, 3, 6}, {0, 1, 2, 3, 4, 6}, {0, 1, 2, 3, 5, 6}, {0, 1, 2, 3, 4, 5, 6}, {1, 2, 3, 7}, {0, 1, 2, 3, 4, 7}, {0, 1, 2, 3, 5, 7}, {0, 1, 2, 3, 4, 5, 7}, {0, 1, 2, 3, 6, 7}, {0, 1, 2, 3, 4, 6, 7}, {0, 1, 2, 3, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 4, 5, 6}, {0, 1, 2, 4, 5, 6}, {0, 1, 3, 4, 5, 6}, {0, 1, 2, 3, 4, 5, 6}, {0, 2, 3, 4, 5, 6}, {0, 1, 2, 3, 4, 5, 6}, {0, 1, 2, 3, 4, 5, 6}, {1, 2, 3, 4, 5, 6}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {1, 2, 3, 4, 5, 6, 7}, {1, 4, 5, 7}, {0, 1, 2, 4, 5, 7}, {0, 1, 3, 4, 5, 7}, {0, 1, 2, 3, 4, 5, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {1, 2, 3, 4, 5, 7}, {0, 1, 2, 3, 4, 5, 7}, {0, 1, 2, 3, 4, 5, 7}, {0, 2, 3, 4, 5, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 2, 3, 4, 5, 6, 7}, {0, 1, 4, 5, 6, 7}, {0, 1, 2, 4, 5, 6, 7}, {0, 1, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {2, 3, 4, 5, 6, 7}, {2, 4, 6, 7}, {0, 1, 2, 4, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 2, 3, 4, 6, 7}, {0, 1, 2, 3, 4, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {1, 2, 3, 4, 6, 7}, {0, 1, 2, 3, 4, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 6, 7}, {0, 1, 3, 4, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 3, 4, 5, 6, 7}, {0, 2, 4, 5, 6, 7}, {0, 1, 2, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {1, 3, 4, 5, 6, 7}, {1, 2, 4, 5, 6, 7}, {0, 1, 2, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 3, 4, 5, 6, 7}, {0, 1, 2, 4, 5, 6, 7}, {0, 1, 2, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {1, 2, 3, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 2, 3, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 3, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {3, 5, 6, 7}, {3, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 3, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 2, 3, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {1, 2, 3, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 5, 6, 7}, {0, 1, 2, 4, 5, 6, 7}, {0, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 4, 5, 6, 7}, {1, 2, 4, 5, 6, 7}, {1, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 4, 5, 6, 7}, {0, 2, 4, 5, 6, 7}, {0, 1, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 3, 4, 6, 7}, {0, 1, 2, 3, 4, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 6, 7}, {1, 2, 3, 4, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 6, 7}, {0, 2, 3, 4, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 4, 6, 7}, {2, 4, 6, 7}, {2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 3, 4, 5, 6, 7}, {0, 1, 2, 4, 5, 6, 7}, {0, 1, 4, 5, 6, 7}, {0, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 2, 3, 4, 5, 7}, {0, 1, 2, 3, 4, 5, 7}, {0, 1, 2, 3, 4, 5, 7}, {1, 2, 3, 4, 5, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 7}, {0, 1, 3, 4, 5, 7}, {0, 1, 2, 4, 5, 7}, {1, 4, 5, 7}, {1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {1, 2, 3, 4, 5, 6}, {0, 1, 2, 3, 4, 5, 6}, {0, 1, 2, 3, 4, 5, 6}, {0, 2, 3, 4, 5, 6}, {0, 1, 2, 3, 4, 5, 6}, {0, 1, 3, 4, 5, 6}, {0, 1, 2, 4, 5, 6}, {0, 4, 5, 6}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 5, 6, 7}, {0, 1, 2, 3, 4, 6, 7}, {0, 1, 2, 3, 6, 7}, {0, 1, 2, 3, 4, 5, 7}, {0, 1, 2, 3, 5, 7}, {0, 1, 2, 3, 4, 7}, {1, 2, 3, 7}, {0, 1, 2, 3, 4, 5, 6}, {0, 1, 2, 3, 5, 6}, {0, 1, 2, 3, 4, 6}, {0, 2, 3, 6}, {0, 1, 2, 3, 4, 5}, {0, 1, 3, 5}, {0, 1, 2, 4}, new byte[0]};
    private static final byte[][] CORNER_EDGE_MAP = new byte[][]{new byte[0], {0, 4, 8}, {1, 5, 8}, {0, 1, 4, 5}, {2, 4, 9}, {0, 2, 8, 9}, {1, 2, 4, 5, 8, 9}, {0, 1, 2, 5, 9}, {3, 5, 9}, {0, 3, 4, 5, 8, 9}, {1, 3, 8, 9}, {0, 1, 3, 4, 9}, {2, 3, 4, 5}, {0, 2, 3, 5, 8}, {1, 2, 3, 4, 8}, {0, 1, 2, 3}, {0, 6, 10}, {4, 6, 8, 10}, {0, 1, 5, 6, 8, 10}, {1, 4, 5, 6, 10}, {0, 2, 4, 6, 9, 10}, {2, 6, 8, 9, 10}, {0, 1, 2, 4, 5, 6, 8, 9, 10}, {1, 2, 5, 6, 9, 10}, {0, 3, 5, 6, 9, 10}, {3, 4, 5, 6, 8, 9, 10}, {0, 1, 3, 6, 8, 9, 10}, {1, 3, 4, 6, 9, 10}, {0, 2, 3, 4, 5, 6, 10}, {2, 3, 5, 6, 8, 10}, {0, 1, 2, 3, 4, 6, 8, 10}, {1, 2, 3, 6, 10}, {1, 7, 10}, {0, 1, 4, 7, 8, 10}, {5, 7, 8, 10}, {0, 4, 5, 7, 10}, {1, 2, 4, 7, 9, 10}, {0, 1, 2, 7, 8, 9, 10}, {2, 4, 5, 7, 8, 9, 10}, {0, 2, 5, 7, 9, 10}, {1, 3, 5, 7, 9, 10}, {0, 1, 3, 4, 5, 7, 8, 9, 10}, {3, 7, 8, 9, 10}, {0, 3, 4, 7, 9, 10}, {1, 2, 3, 4, 5, 7, 10}, {0, 1, 2, 3, 5, 7, 8, 10}, {2, 3, 4, 7, 8, 10}, {0, 2, 3, 7, 10}, {0, 1, 6, 7}, {1, 4, 6, 7, 8}, {0, 5, 6, 7, 8}, {4, 5, 6, 7}, {0, 1, 2, 4, 6, 7, 9}, {1, 2, 6, 7, 8, 9}, {0, 2, 4, 5, 6, 7, 8, 9}, {2, 5, 6, 7, 9}, {0, 1, 3, 5, 6, 7, 9}, {1, 3, 4, 5, 6, 7, 8, 9}, {0, 3, 6, 7, 8, 9}, {3, 4, 6, 7, 9}, {0, 1, 2, 3, 4, 5, 6, 7}, {1, 2, 3, 5, 6, 7, 8}, {0, 2, 3, 4, 6, 7, 8}, {2, 3, 6, 7}, {2, 6, 11}, {0, 2, 4, 6, 8, 11}, {1, 2, 5, 6, 8, 11}, {0, 1, 2, 4, 5, 6, 11}, {4, 6, 9, 11}, {0, 6, 8, 9, 11}, {1, 4, 5, 6, 8, 9, 11}, {0, 1, 5, 6, 9, 11}, {2, 3, 5, 6, 9, 11}, {0, 2, 3, 4, 5, 6, 8, 9, 11}, {1, 2, 3, 6, 8, 9, 11}, {0, 1, 2, 3, 4, 6, 9, 11}, {3, 4, 5, 6, 11}, {0, 3, 5, 6, 8, 11}, {1, 3, 4, 6, 8, 11}, {0, 1, 3, 6, 11}, {0, 2, 10, 11}, {2, 4, 8, 10, 11}, {0, 1, 2, 5, 8, 10, 11}, {1, 2, 4, 5, 10, 11}, {0, 4, 9, 10, 11}, {8, 9, 10, 11}, {0, 1, 4, 5, 8, 9, 10, 11}, {1, 5, 9, 10, 11}, {0, 2, 3, 5, 9, 10, 11}, {2, 3, 4, 5, 8, 9, 10, 11}, {0, 1, 2, 3, 8, 9, 10, 11}, {1, 2, 3, 4, 9, 10, 11}, {0, 3, 4, 5, 10, 11}, {3, 5, 8, 10, 11}, {0, 1, 3, 4, 8, 10, 11}, {1, 3, 10, 11}, {1, 2, 6, 7, 10, 11}, {0, 1, 2, 4, 6, 7, 8, 10, 11}, {2, 5, 6, 7, 8, 10, 11}, {0, 2, 4, 5, 6, 7, 10, 11}, {1, 4, 6, 7, 9, 10, 11}, {0, 1, 6, 7, 8, 9, 10, 11}, {4, 5, 6, 7, 8, 9, 10, 11}, {0, 5, 6, 7, 9, 10, 11}, {1, 2, 3, 5, 6, 7, 9, 10, 11}, {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, {2, 3, 6, 7, 8, 9, 10, 11}, {0, 2, 3, 4, 6, 7, 9, 10, 11}, {1, 3, 4, 5, 6, 7, 10, 11}, {0, 1, 3, 5, 6, 7, 8, 10, 11}, {3, 4, 6, 7, 8, 10, 11}, {0, 3, 6, 7, 10, 11}, {0, 1, 2, 7, 11}, {1, 2, 4, 7, 8, 11}, {0, 2, 5, 7, 8, 11}, {2, 4, 5, 7, 11}, {0, 1, 4, 7, 9, 11}, {1, 7, 8, 9, 11}, {0, 4, 5, 7, 8, 9, 11}, {5, 7, 9, 11}, {0, 1, 2, 3, 5, 7, 9, 11}, {1, 2, 3, 4, 5, 7, 8, 9, 11}, {0, 2, 3, 7, 8, 9, 11}, {2, 3, 4, 7, 9, 11}, {0, 1, 3, 4, 5, 7, 11}, {1, 3, 5, 7, 8, 11}, {0, 3, 4, 7, 8, 11}, {3, 7, 11}, {3, 7, 11}, {0, 3, 4, 7, 8, 11}, {1, 3, 5, 7, 8, 11}, {0, 1, 3, 4, 5, 7, 11}, {2, 3, 4, 7, 9, 11}, {0, 2, 3, 7, 8, 9, 11}, {1, 2, 3, 4, 5, 7, 8, 9, 11}, {0, 1, 2, 3, 5, 7, 9, 11}, {5, 7, 9, 11}, {0, 4, 5, 7, 8, 9, 11}, {1, 7, 8, 9, 11}, {0, 1, 4, 7, 9, 11}, {2, 4, 5, 7, 11}, {0, 2, 5, 7, 8, 11}, {1, 2, 4, 7, 8, 11}, {0, 1, 2, 7, 11}, {0, 3, 6, 7, 10, 11}, {3, 4, 6, 7, 8, 10, 11}, {0, 1, 3, 5, 6, 7, 8, 10, 11}, {1, 3, 4, 5, 6, 7, 10, 11}, {0, 2, 3, 4, 6, 7, 9, 10, 11}, {2, 3, 6, 7, 8, 9, 10, 11}, {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, {1, 2, 3, 5, 6, 7, 9, 10, 11}, {0, 5, 6, 7, 9, 10, 11}, {4, 5, 6, 7, 8, 9, 10, 11}, {0, 1, 6, 7, 8, 9, 10, 11}, {1, 4, 6, 7, 9, 10, 11}, {0, 2, 4, 5, 6, 7, 10, 11}, {2, 5, 6, 7, 8, 10, 11}, {0, 1, 2, 4, 6, 7, 8, 10, 11}, {1, 2, 6, 7, 10, 11}, {1, 3, 10, 11}, {0, 1, 3, 4, 8, 10, 11}, {3, 5, 8, 10, 11}, {0, 3, 4, 5, 10, 11}, {1, 2, 3, 4, 9, 10, 11}, {0, 1, 2, 3, 8, 9, 10, 11}, {2, 3, 4, 5, 8, 9, 10, 11}, {0, 2, 3, 5, 9, 10, 11}, {1, 5, 9, 10, 11}, {0, 1, 4, 5, 8, 9, 10, 11}, {8, 9, 10, 11}, {0, 4, 9, 10, 11}, {1, 2, 4, 5, 10, 11}, {0, 1, 2, 5, 8, 10, 11}, {2, 4, 8, 10, 11}, {0, 2, 10, 11}, {0, 1, 3, 6, 11}, {1, 3, 4, 6, 8, 11}, {0, 3, 5, 6, 8, 11}, {3, 4, 5, 6, 11}, {0, 1, 2, 3, 4, 6, 9, 11}, {1, 2, 3, 6, 8, 9, 11}, {0, 2, 3, 4, 5, 6, 8, 9, 11}, {2, 3, 5, 6, 9, 11}, {0, 1, 5, 6, 9, 11}, {1, 4, 5, 6, 8, 9, 11}, {0, 6, 8, 9, 11}, {4, 6, 9, 11}, {0, 1, 2, 4, 5, 6, 11}, {1, 2, 5, 6, 8, 11}, {0, 2, 4, 6, 8, 11}, {2, 6, 11}, {2, 3, 6, 7}, {0, 2, 3, 4, 6, 7, 8}, {1, 2, 3, 5, 6, 7, 8}, {0, 1, 2, 3, 4, 5, 6, 7}, {3, 4, 6, 7, 9}, {0, 3, 6, 7, 8, 9}, {1, 3, 4, 5, 6, 7, 8, 9}, {0, 1, 3, 5, 6, 7, 9}, {2, 5, 6, 7, 9}, {0, 2, 4, 5, 6, 7, 8, 9}, {1, 2, 6, 7, 8, 9}, {0, 1, 2, 4, 6, 7, 9}, {4, 5, 6, 7}, {0, 5, 6, 7, 8}, {1, 4, 6, 7, 8}, {0, 1, 6, 7}, {0, 2, 3, 7, 10}, {2, 3, 4, 7, 8, 10}, {0, 1, 2, 3, 5, 7, 8, 10}, {1, 2, 3, 4, 5, 7, 10}, {0, 3, 4, 7, 9, 10}, {3, 7, 8, 9, 10}, {0, 1, 3, 4, 5, 7, 8, 9, 10}, {1, 3, 5, 7, 9, 10}, {0, 2, 5, 7, 9, 10}, {2, 4, 5, 7, 8, 9, 10}, {0, 1, 2, 7, 8, 9, 10}, {1, 2, 4, 7, 9, 10}, {0, 4, 5, 7, 10}, {5, 7, 8, 10}, {0, 1, 4, 7, 8, 10}, {1, 7, 10}, {1, 2, 3, 6, 10}, {0, 1, 2, 3, 4, 6, 8, 10}, {2, 3, 5, 6, 8, 10}, {0, 2, 3, 4, 5, 6, 10}, {1, 3, 4, 6, 9, 10}, {0, 1, 3, 6, 8, 9, 10}, {3, 4, 5, 6, 8, 9, 10}, {0, 3, 5, 6, 9, 10}, {1, 2, 5, 6, 9, 10}, {0, 1, 2, 4, 5, 6, 8, 9, 10}, {2, 6, 8, 9, 10}, {0, 2, 4, 6, 9, 10}, {1, 4, 5, 6, 10}, {0, 1, 5, 6, 8, 10}, {4, 6, 8, 10}, {0, 6, 10}, {0, 1, 2, 3}, {1, 2, 3, 4, 8}, {0, 2, 3, 5, 8}, {2, 3, 4, 5}, {0, 1, 3, 4, 9}, {1, 3, 8, 9}, {0, 3, 4, 5, 8, 9}, {3, 5, 9}, {0, 1, 2, 5, 9}, {1, 2, 4, 5, 8, 9}, {0, 2, 8, 9}, {2, 4, 9}, {0, 1, 4, 5}, {1, 5, 8}, {0, 4, 8}, new byte[0]};
    private Vector3d p = new Vector3d();
    private Vector3i p1 = new Vector3i();
    private Vector3i p2 = new Vector3i();
    private Vector3i tmp = new Vector3i();
    private Vector3i faces = new Vector3i();
    private SimplePoolVertex vertexPool = new SimplePoolVertex(1000);
    private int[] indices = new int[]{-1, -1, -1, -1};
    private boolean[] signChange = new boolean[]{false, false, false, false};
    private ObjectArrayList<OctreeNode> stack = new ObjectArrayList();

    public int hash(Vector3i min) {
        return (min.x * 337 + min.y) * 103 + min.z;
    }

    public int hash(int x, int y, int z) {
        return (x * 337 + y) * 103 + z;
    }

    public Int2ObjectMap<OctreeNode> buildOctreeFromBottom(Int2ObjectMap<OctreeNode> seams, int chunkX, int chunkY, int chunkZ, int size) {
        int chunkMinX = IChunk.CHUNK_SIZE * chunkX;
        int chunkMinY = IChunk.CHUNK_SIZE * chunkY;
        int chunkMinZ = IChunk.CHUNK_SIZE * chunkZ;
        Int2ObjectOpenHashMap parents = new Int2ObjectOpenHashMap();
        do {
            ObjectIterator it = seams.int2ObjectEntrySet().iterator();
            parents = new Int2ObjectOpenHashMap();
            while (it.hasNext()) {
                Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)it.next();
                OctreeNode node = (OctreeNode)entry.getValue();
                if (node.size != size) continue;
                int parentSize = node.size << 1;
                int parentSizeBitMask = parentSize - 1;
                int px = node.min.x - (node.min.x - chunkMinX & parentSizeBitMask);
                int py = node.min.y - (node.min.y - chunkMinY & parentSizeBitMask);
                int pz = node.min.z - (node.min.z - chunkMinZ & parentSizeBitMask);
                OctreeNode parent = null;
                int hash = this.hash(px, py, pz);
                parent = (OctreeNode)parents.get(hash);
                if (parent == null) {
                    parent = new OctreeNode();
                    parent.min = new Vector3i(px, py, pz);
                    parent.size = parentSize;
                    parent.leaf = false;
                    parents.put(hash, (Object)parent);
                }
                int childX = node.min.x - px != 0 ? 4 : 0;
                int childY = node.min.y - py != 0 ? 2 : 0;
                int childZ = node.min.z - pz != 0 ? 1 : 0;
                parent.children[childX | childY | childZ] = node;
                parent.edge |= node.edge;
                it.remove();
            }
            parents.putAll(seams);
            seams = parents;
            if ((size <<= 1) <= 1024) continue;
            int biggestSize = 0;
            OctreeNode biggest = null;
            for (OctreeNode node : parents.values()) {
                if (node.size <= biggestSize) continue;
                biggestSize = node.size;
                biggest = node;
            }
            parents.clear();
            parents.put(this.hash(biggest.min), biggest);
        } while (parents.size() > 1);
        return parents;
    }

    public Int2ObjectMap<OctreeNode> findSeamNodes(IWorld world, IChunk chunk) {
        int seamValuesX = IChunk.CHUNK_SIZE * chunk.x + IChunk.CHUNK_SIZE;
        int seamValuesY = IChunk.CHUNK_SIZE * chunk.y + IChunk.CHUNK_SIZE;
        int seamValuesZ = IChunk.CHUNK_SIZE * chunk.z + IChunk.CHUNK_SIZE;
        Int2ObjectOpenHashMap seamNodes = new Int2ObjectOpenHashMap();
        for (int i = 0; i < 8; ++i) {
            Object c = world.getChunk(chunk.x + (i >> 2 & 1), chunk.y + (i >> 1 & 1), chunk.z + (i & 1));
            if (c == null) continue;
            ChunkContouring contouring = (ChunkContouring)c;
            List<OctreeNode> edgeNodes = contouring.getEdgeNodes();
            for (int j = 0; j < edgeNodes.size(); ++j) {
                this.findNodes(edgeNodes.get(j), i, (Int2ObjectMap<OctreeNode>)seamNodes, seamValuesX, seamValuesY, seamValuesZ);
            }
        }
        return seamNodes;
    }

    public void findNodes(OctreeNode node, int function, Int2ObjectMap<OctreeNode> nodes, int seamValuesX, int seamValuesY, int seamValuesZ) {
        if (node == null || node.min == null || !node.edge) {
            return;
        }
        int maxX = node.min.x + node.size;
        int maxY = node.min.y + node.size;
        int maxZ = node.min.z + node.size;
        if (!this.isOnEdge(node.min, maxX, maxY, maxZ, function, seamValuesX, seamValuesY, seamValuesZ)) {
            return;
        }
        nodes.put(this.hash(node.min), (Object)node);
    }

    public boolean isOnEdge(Vector3i min, int maxX, int maxY, int maxZ, int function, int seamValuesX, int seamValuesY, int seamValuesZ) {
        switch (function) {
            case 0: {
                return maxX == seamValuesX || maxY == seamValuesY || maxZ == seamValuesZ;
            }
            case 1: {
                return min.z == seamValuesZ;
            }
            case 2: {
                return min.y == seamValuesY;
            }
            case 3: {
                return min.y == seamValuesY && min.z == seamValuesZ;
            }
            case 4: {
                return min.x == seamValuesX;
            }
            case 5: {
                return min.x == seamValuesX && min.z == seamValuesZ;
            }
            case 6: {
                return min.x == seamValuesX && min.y == seamValuesY;
            }
            case 7: {
                return min.x == seamValuesX && min.y == seamValuesY && min.z == seamValuesZ;
            }
        }
        return false;
    }

    public void constructOctreeLinear(OctreeNode node, IChunk chunk, int offset, int size, int lod) {
        int mul = (int)Math.pow(2.0, lod);
        Int2ObjectOpenHashMap octreeNodes = new Int2ObjectOpenHashMap();
        IntSet activeNodes = (IntSet)chunk.activeNodes.get(lod);
        if (activeNodes != null) {
            for (int x = offset; x < size + offset; x += mul) {
                for (y = offset; y < size + offset; y += mul) {
                    this.createNode((Int2ObjectMap<OctreeNode>)octreeNodes, chunk, x, y, size - mul, mul, lod, size, node);
                    this.createNode((Int2ObjectMap<OctreeNode>)octreeNodes, chunk, x, size - mul, y, mul, lod, size, node);
                    this.createNode((Int2ObjectMap<OctreeNode>)octreeNodes, chunk, size - mul, x, y, mul, lod, size, node);
                }
            }
            IntIterator it = activeNodes.iterator();
            while (it.hasNext()) {
                int pos = it.nextInt();
                byte x = (byte)(pos >> 16 & 0xFF);
                byte y = (byte)(pos >> 8 & 0xFF);
                byte z = (byte)(pos & 0xFF);
                if (x >= 0 && y >= 0 && z >= 0) {
                    if (this.createNode((Int2ObjectMap<OctreeNode>)octreeNodes, chunk, x, y, z, mul, lod, size, node)) continue;
                    it.remove();
                    continue;
                }
                it.remove();
            }
        } else {
            activeNodes = new IntOpenHashSet();
            for (int x = offset; x < size + offset; x += mul) {
                for (y = offset; y < size + offset; y += mul) {
                    for (int z = offset; z < size + offset; z += mul) {
                        if (!this.createNode((Int2ObjectMap<OctreeNode>)octreeNodes, chunk, x, y, z, mul, lod, size, node)) continue;
                        activeNodes.add(x << 16 | y << 8 | z);
                    }
                }
            }
            chunk.activeNodes.put(lod, (Object)activeNodes);
        }
        Int2ObjectMap<OctreeNode> parents = this.buildOctreeFromBottom((Int2ObjectMap<OctreeNode>)octreeNodes, chunk.x, chunk.y, chunk.z, mul);
        if (parents.size() == 1) {
            OctreeNode root = (OctreeNode)parents.values().iterator().next();
            node.reset(root.leaf);
            node.size = root.size;
            node.edge = true;
            node.min = root.min;
            node.children = root.children;
        }
    }

    private boolean createNode(Int2ObjectMap<OctreeNode> octreeNodes, IChunk chunk, int x, int y, int z, int mul, int lod, int size, OctreeNode node) {
        int minX = node.min.x + x;
        int minY = node.min.y + y;
        int minZ = node.min.z + z;
        boolean fast = x + mul < IChunk.CHUNK_SIZE && y + mul < IChunk.CHUNK_SIZE && z + mul < IChunk.CHUNK_SIZE;
        OctreeNode leafNode = this.constructLeafNode(minX, minY, minZ, mul, lod, chunk, fast);
        if (leafNode != null) {
            leafNode.edge = x == 0 || x + mul >= size || y == 0 || y + mul >= size || z == 0 || z + mul >= size;
            octreeNodes.put(this.hash(leafNode.min), (Object)leafNode);
            return true;
        }
        return false;
    }

    public void constructOctreeLinear(OctreeNode node, IChunk chunk, int size, int lod) {
        this.constructOctreeLinear(node, chunk, 0, size, lod);
    }

    public void constructOctreeLinear(OctreeNode node, IChunk chunk, int lod) {
        this.constructOctreeLinear(node, chunk, IChunk.CHUNK_SIZE, lod);
    }

    public OctreeNode constructLeafNode(int x, int y, int z, int size, int lod, IChunk chunk, boolean fast) {
        int corners = 0;
        int xo = x - chunk.xVoxel;
        int yo = y - chunk.yVoxel;
        int zo = z - chunk.zVoxel;
        long cornerDensitiesFast = 0L;
        for (int i = 0; i < 8; ++i) {
            int xt = xo + ((i >> 2 & 1) << lod);
            int yt = yo + ((i >> 1 & 1) << lod);
            int zt = zo + ((i & 1) << lod);
            byte val = fast ? chunk.getDataByteFast(xt, yt, zt) : chunk.getDataByte(xt, yt, zt);
            cornerDensitiesFast |= (long)(val & 0xFF) << (i << 3);
            int material = val < 0 ? 1 : 0;
            corners |= material << i;
        }
        if (corners == 0 || corners == 255) {
            return null;
        }
        Vector3d massPoint = new Vector3d();
        OctreeNode node = new OctreeNode(true);
        node.drawInfo = new OctreeDrawInfo();
        byte[] edges = CORNER_EDGE_MAP[corners];
        boolean round = ConfigClient.snowType == 0;
        for (int i = 0; i < edges.length; ++i) {
            int edge = edges[i] << 1;
            byte c1 = EDGE_V_MAP[edge];
            byte c2 = EDGE_V_MAP[edge + 1];
            int p1x = xo + ((c1 >> 2 & 1) << lod);
            int p1y = yo + ((c1 >> 1 & 1) << lod);
            int p1z = zo + ((c1 & 1) << lod);
            int p2x = xo + ((c2 >> 2 & 1) << lod);
            int p2y = yo + ((c2 >> 1 & 1) << lod);
            int p2z = zo + ((c2 & 1) << lod);
            int light = fast ? Math.max(chunk.getLightDataByteFast(p1x, p1y, p1z) & 0xFF, chunk.getLightDataByteFast(p2x, p2y, p2z) & 0xFF) : Math.max(chunk.getLightDataByte(p1x, p1y, p1z) & 0xFF, chunk.getLightDataByte(p2x, p2y, p2z) & 0xFF);
            node.drawInfo.light = Math.max(node.drawInfo.light, light);
            if (!round) continue;
            massPoint.add((Vector3dc)this.vertexInterpolation(0.0, p1x, p1y, p1z, p2x, p2y, p2z, (byte)(cornerDensitiesFast >>> (c1 << 3)), (byte)(cornerDensitiesFast >>> (c2 << 3))));
        }
        if (node.drawInfo.light == 0) {
            node.drawInfo.light = this.getLight(chunk, xo, yo, zo);
        }
        node.min = new Vector3i(x, y, z);
        if (round) {
            massPoint.mul(1.0 / (double)edges.length).add((double)chunk.xVoxel, (double)chunk.yVoxel, (double)chunk.zVoxel);
        } else {
            massPoint.set((Vector3ic)node.min).add(0.5, 0.5, 0.5);
        }
        node.size = size;
        node.drawInfo.pos = massPoint;
        if (ConfigClient.snowSmoothShading) {
            byte b0 = (byte)cornerDensitiesFast;
            byte b1 = (byte)(cornerDensitiesFast >>> 8);
            byte b2 = (byte)(cornerDensitiesFast >>> 16);
            byte b3 = (byte)(cornerDensitiesFast >>> 24);
            byte b4 = (byte)(cornerDensitiesFast >>> 32);
            byte b5 = (byte)(cornerDensitiesFast >>> 40);
            byte b6 = (byte)(cornerDensitiesFast >>> 48);
            byte b7 = (byte)(cornerDensitiesFast >>> 56);
            node.drawInfo.normal = new Vector3f((float)(b0 + b1 + b2 + b3 - (b4 + b5 + b6 + b7)), (float)(b0 + b1 + b4 + b5 - (b2 + b3 + b6 + b7)), (float)(b0 + b2 + b4 + b6 - (b1 + b3 + b5 + b7)));
            float length = node.drawInfo.normal.lengthSquared();
            if (length > 0.0f) {
                float inv = 1.0f / (float)Math.sqrt(length);
                node.drawInfo.normal.mul(inv);
            } else {
                node.drawInfo.normal = chunk.calculateNormal((float)xo + 0.5f, (float)yo + 0.5f, (float)zo + 0.5f, 2.0f * (float)size);
                if (!node.drawInfo.normal.isFinite() || node.drawInfo.normal.lengthSquared() == 0.0f) {
                    node.drawInfo.normal.set(0.0f, 1.0f, 0.0f);
                }
            }
        } else {
            node.drawInfo.normal = new Vector3f(0.0f, 1.0f, 0.0f);
        }
        node.drawInfo.corners = corners;
        return node;
    }

    public void updateLighting(OctreeNode node, IChunk chunk) {
        if (node == null) {
            return;
        }
        if (!node.leaf) {
            for (int i = 0; i < node.children.length; ++i) {
                this.updateLighting(node.children[i], chunk);
            }
        } else {
            int corners = node.drawInfo.corners;
            byte[] uniqueCorners = UNIQUE_CORNERS[corners];
            int x = node.min.x - chunk.xVoxel;
            int y = node.min.y - chunk.yVoxel;
            int z = node.min.z - chunk.zVoxel;
            int size = node.size;
            node.drawInfo.light = 0;
            for (int i = 0; i < uniqueCorners.length; ++i) {
                byte corner = uniqueCorners[i];
                node.drawInfo.light = Math.max(node.drawInfo.light, chunk.getLightDataByte(x + (corner & 1) * size, y + (corner >> 1 & 1) * size, z + (corner >> 2 & 1) * size) & 0xFF);
            }
            if (node.drawInfo.light == 0) {
                node.drawInfo.light = this.getLight(chunk, x, y, z);
            }
        }
    }

    private int getLight(IChunk chunk, int x, int y, int z) {
        int day = 0;
        int night = 0;
        int count = 0;
        for (int xo = -1; xo <= 1; ++xo) {
            for (int yo = -1; yo <= 1; ++yo) {
                for (int zo = -1; zo <= 1; ++zo) {
                    int light = chunk.getLightDataByte(x + xo * IChunk.CHUNK_MULTIPLE, y + yo * IChunk.CHUNK_MULTIPLE, z + zo * IChunk.CHUNK_MULTIPLE) & 0xFF;
                    if (light == 0) continue;
                    day += light & 0xF;
                    night += light >> 4 & 0xF;
                    ++count;
                }
            }
        }
        if (count == 0) {
            return 0;
        }
        return day / count & 0xF | (night / count & 0xF) << 4;
    }

    private Vector3d vertexInterpolation(double isolevel, int p1x, int p1y, int p1z, int p2x, int p2y, int p2z, double valp1, double valp2) {
        double mu = (isolevel - valp1) / (valp2 - valp1);
        this.p.x = (double)p1x + mu * (double)(p2x - p1x);
        this.p.y = (double)p1y + mu * (double)(p2y - p1y);
        this.p.z = (double)p1z + mu * (double)(p2z - p1z);
        return this.p;
    }

    public void contourMesh(OctreeNode node, List<Vertex> vertexBuffer, IntList indexBuffer, boolean seam) {
        vertexBuffer.clear();
        indexBuffer.clear();
        this.generateVertexIndicesIterative(node, vertexBuffer);
        this.contourCellProc(node, indexBuffer, seam);
    }

    public void resetMemoryPool() {
        this.vertexPool.reset();
    }

    private void generateVertexIndicesIterative(OctreeNode node, List<Vertex> vertexBuffer) {
        if (node == null) {
            return;
        }
        this.stack.clear();
        this.stack.push((Object)node);
        OctreeNode current = null;
        while (!this.stack.isEmpty()) {
            current = (OctreeNode)this.stack.pop();
            if (current.leaf) {
                OctreeDrawInfo d = current.drawInfo;
                d.index = vertexBuffer.size();
                vertexBuffer.add(this.vertexPool.get(d.pos, d.normal, this.transformLight(d.light)));
                continue;
            }
            if (current.leaf) continue;
            for (int i = 0; i < 8; ++i) {
                if (current.children[i] == null) continue;
                this.stack.push((Object)current.children[i]);
            }
        }
    }

    private void contourCellProc(OctreeNode node, IntList indexBuffer, boolean seam) {
        int lookup;
        int i;
        if (node == null || node.leaf) {
            return;
        }
        for (i = 0; i < 8; ++i) {
            this.contourCellProc(node.children[i], indexBuffer, seam);
        }
        for (i = 0; i < 12; ++i) {
            lookup = i << 1;
            this.contourFaceProc(node.children[CELL_PROC_FACE_MASK[lookup]], node.children[CELL_PROC_FACE_MASK[lookup + 1]], i >> 2, indexBuffer, seam);
        }
        for (i = 0; i < 6; ++i) {
            lookup = i << 2;
            this.contourEdgeProc(node.children[CELL_PROC_EDGE_MASK[lookup]], node.children[CELL_PROC_EDGE_MASK[lookup + 1]], node.children[CELL_PROC_EDGE_MASK[lookup + 2]], node.children[CELL_PROC_EDGE_MASK[lookup + 3]], i >> 1, indexBuffer, seam);
        }
    }

    private void contourFaceProc(OctreeNode node0, OctreeNode node1, int dir, IntList indexBuffer, boolean seam) {
        if (node0 == null || node1 == null) {
            return;
        }
        if (seam && this.chunkMinForPosition(node0.min) == this.chunkMinForPosition(node1.min)) {
            return;
        }
        if (!node0.leaf || !node1.leaf) {
            int lookup;
            int i;
            int dirLookup = dir << 3;
            for (i = 0; i < 4; ++i) {
                lookup = dirLookup + (i << 1);
                this.contourFaceProc(node0.leaf ? node0 : node0.children[FACE_PROC_FACE_MASK[lookup]], node1.leaf ? node1 : node1.children[FACE_PROC_FACE_MASK[lookup + 1]], dir, indexBuffer, seam);
            }
            dirLookup = dir * 24;
            for (i = 0; i < 4; ++i) {
                lookup = dirLookup + i * 6;
                byte mask = FACE_PROC_EDGE_MASK[lookup];
                OctreeNode n1 = mask == 0 ? node0 : node1;
                OctreeNode n2 = mask == 0 ? node1 : node0;
                this.contourEdgeProc(node0.leaf ? node0 : node0.children[FACE_PROC_EDGE_MASK[lookup + 1]], n1.leaf ? n1 : n1.children[FACE_PROC_EDGE_MASK[lookup + 2]], n2.leaf ? n2 : n2.children[FACE_PROC_EDGE_MASK[lookup + 3]], node1.leaf ? node1 : node1.children[FACE_PROC_EDGE_MASK[lookup + 4]], FACE_PROC_EDGE_MASK[lookup + 5], indexBuffer, seam);
            }
        }
    }

    private void contourEdgeProc(OctreeNode node0, OctreeNode node1, OctreeNode node2, OctreeNode node3, int dir, IntList indexBuffer, boolean seam) {
        if (node0 == null || node1 == null || node2 == null || node3 == null) {
            return;
        }
        if (seam) {
            int chunkPos1 = this.chunkMinForPosition(node0.min);
            int chunkPos2 = this.chunkMinForPosition(node1.min);
            int chunkPos3 = this.chunkMinForPosition(node2.min);
            int chunkPos4 = this.chunkMinForPosition(node3.min);
            if (chunkPos1 == chunkPos2 && chunkPos2 == chunkPos3 && chunkPos3 == chunkPos4) {
                return;
            }
        }
        if (node0.leaf && node1.leaf && node2.leaf && node3.leaf) {
            this.contourProcessEdge(node0, node1, node2, node3, dir, indexBuffer);
        } else {
            int dirLookup = dir << 3;
            for (int i = 0; i < 2; ++i) {
                int lookup = dirLookup + (i << 2);
                this.contourEdgeProc(node0.leaf ? node0 : node0.children[EDGE_PROC_EDGE_MASK[lookup]], node1.leaf ? node1 : node1.children[EDGE_PROC_EDGE_MASK[lookup + 1]], node2.leaf ? node2 : node2.children[EDGE_PROC_EDGE_MASK[lookup + 2]], node3.leaf ? node3 : node3.children[EDGE_PROC_EDGE_MASK[lookup + 3]], dir, indexBuffer, seam);
            }
        }
    }

    private void contourProcessEdge(OctreeNode node0, OctreeNode node1, OctreeNode node2, OctreeNode node3, int dir, IntList indexBuffer) {
        int minSize = Integer.MAX_VALUE;
        int minIndex = 0;
        boolean flip = false;
        int lookup = dir << 2;
        for (int i = 0; i < 4; ++i) {
            OctreeNode node = switch (i) {
                case 0 -> node0;
                case 1 -> node1;
                case 2 -> node2;
                default -> node3;
            };
            byte edge = PROCESS_EDGE_MASK[lookup + i];
            byte c1 = EDGE_V_MAP[edge];
            byte c2 = EDGE_V_MAP[edge + 1];
            int m1 = node.drawInfo.corners >> c1 & 1;
            int m2 = node.drawInfo.corners >> c2 & 1;
            if (node.size < minSize) {
                minSize = node.size;
                minIndex = i;
                flip = m1 != 1;
            }
            this.indices[i] = node.drawInfo.index;
            this.signChange[i] = m1 != m2;
        }
        if (this.signChange[minIndex]) {
            if (!flip) {
                indexBuffer.add(this.indices[0]);
                indexBuffer.add(this.indices[1]);
                indexBuffer.add(this.indices[3]);
                indexBuffer.add(this.indices[0]);
                indexBuffer.add(this.indices[3]);
                indexBuffer.add(this.indices[2]);
            } else {
                indexBuffer.add(this.indices[0]);
                indexBuffer.add(this.indices[3]);
                indexBuffer.add(this.indices[1]);
                indexBuffer.add(this.indices[0]);
                indexBuffer.add(this.indices[2]);
                indexBuffer.add(this.indices[3]);
            }
        }
    }

    private int chunkMinForPosition(Vector3i position) {
        return this.hash(WorldUtil.calculateChunkPosX(position.x), WorldUtil.calculateChunkPosX(position.y), WorldUtil.calculateChunkPosX(position.z));
    }

    private int transformLight(int light) {
        int sky = light >> 4 & 0xF;
        int block = light & 0xF;
        return sky << 20 | block << 4;
    }

    public static RawMesh generateMesh(Vector3d offset, List<Vertex> vertices, IntList indices) {
        boolean useTangents;
        if (indices.size() == 0) {
            return null;
        }
        AABB3D boundingBox = new AABB3D(new Vector3d(Double.MAX_VALUE), new Vector3d(-1.7976931348623157E308));
        ByteBuffer data = null;
        RawMesh mesh = new RawMesh();
        boolean bl = useTangents = StarterClient.iris || StarterClient.optifabric;
        if (ConfigClient.snowSmoothShading) {
            boolean thickerSnow;
            float thickness = ConfigClient.snowThickness;
            boolean bl2 = thickerSnow = ConfigClient.snowType == 0 && thickness != 0.0f;
            if (!thickerSnow) {
                thickness = 0.0f;
            }
            if (ConfigClient.snowQuality == 1) {
                thickness *= 2.0f;
            }
            int positionSize = vertices.size() * 3 * 4;
            int normalSize = vertices.size() * 4;
            int lightSize = vertices.size() * 4;
            int size = positionSize + normalSize + lightSize;
            if (useTangents) {
                int tangentSize = vertices.size() * 4;
                size += tangentSize;
            }
            data = MemoryUtil.memAlloc((int)size);
            long pointer = MemoryUtil.memAddress((ByteBuffer)data);
            for (int i = 0; i < vertices.size(); ++i) {
                Vertex v = vertices.get(i);
                float px = (float)(v.position.x - offset.x + (double)(v.normal.x * thickness));
                float py = (float)(v.position.y - offset.y + (double)(v.normal.y * thickness));
                float pz = (float)(v.position.z - offset.z + (double)(v.normal.z * thickness));
                boundingBox.include(px, py, pz);
                MemoryUtil.memPutFloat((long)pointer, (float)px);
                MemoryUtil.memPutFloat((long)(pointer + 4L), (float)py);
                MemoryUtil.memPutFloat((long)(pointer + 8L), (float)pz);
                MemoryUtil.memPutInt((long)(pointer + 12L), (int)Pack.normal(v.normal.x, v.normal.y, v.normal.z));
                MemoryUtil.memPutInt((long)(pointer + 16L), (int)v.light);
                if (useTangents) {
                    MemoryUtil.memPutInt((long)(pointer + 20L), (int)Pack.normal(v.normal.z, -v.normal.x, v.normal.y, 1.0f));
                    pointer += 4L;
                }
                pointer += 20L;
            }
            ByteBuffer indexData = MemoryUtil.memAlloc((int)(indices.size() * 4));
            long indexPointer = MemoryUtil.memAddress((ByteBuffer)indexData);
            for (int i = 0; i < indices.size(); ++i) {
                MemoryUtil.memPutInt((long)(indexPointer + (long)(i * 4)), (int)indices.getInt(i));
            }
            mesh.indexData = indexData;
        } else {
            int positionSize = indices.size() * 3 * 4;
            int normalSize = indices.size() * 4;
            int lightSize = indices.size() * 4;
            int size = positionSize + normalSize + lightSize;
            if (useTangents) {
                int tangentSize = indices.size() * 4;
                size += tangentSize;
            }
            data = MemoryUtil.memAlloc((int)size);
            long pointer = MemoryUtil.memAddress((ByteBuffer)data);
            int flatNormal = 0;
            int flatTangent = 0;
            Vector3d tmp1 = new Vector3d();
            Vector3d tmp2 = new Vector3d();
            Vector3d tmpNormal = new Vector3d();
            for (int i = 0; i < indices.size(); ++i) {
                int vertexIndex = indices.getInt(i);
                Vertex v = vertices.get(vertexIndex);
                if (i % 3 == 0) {
                    Vertex v1 = vertices.get(indices.getInt(i + 1));
                    Vertex v2 = vertices.get(indices.getInt(i + 2));
                    Vector3d d1 = v1.position.sub((Vector3dc)v.position, tmp1);
                    Vector3d d2 = v2.position.sub((Vector3dc)v.position, tmp2);
                    tmpNormal = d1.cross((Vector3dc)d2, tmpNormal).normalize();
                    flatNormal = Pack.normal((float)tmpNormal.x, (float)tmpNormal.y, (float)tmpNormal.z);
                    if (useTangents) {
                        flatTangent = Pack.normal((float)tmpNormal.z, (float)(-tmpNormal.x), (float)tmpNormal.y, 1.0f);
                    }
                }
                float px = (float)(v.position.x - offset.x);
                float py = (float)(v.position.y - offset.y);
                float pz = (float)(v.position.z - offset.z);
                boundingBox.include(px, py, pz);
                MemoryUtil.memPutFloat((long)pointer, (float)px);
                MemoryUtil.memPutFloat((long)(pointer + 4L), (float)py);
                MemoryUtil.memPutFloat((long)(pointer + 8L), (float)pz);
                MemoryUtil.memPutInt((long)(pointer + 12L), (int)flatNormal);
                MemoryUtil.memPutInt((long)(pointer + 16L), (int)v.light);
                if (useTangents) {
                    MemoryUtil.memPutInt((long)(pointer + 20L), (int)flatTangent);
                    pointer += 4L;
                }
                pointer += 20L;
            }
        }
        mesh.boundingBox = boundingBox;
        mesh.data = data;
        return mesh;
    }
}

