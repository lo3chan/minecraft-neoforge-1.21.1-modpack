package net.diebuddies.dualcontouring;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Set;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.Mesh;
import net.diebuddies.opengl.Pack;
import net.diebuddies.physics.StarterClient;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

public class DualContouring3d {
   private static final int MATERIAL_SOLID = 0;
   private static final int MATERIAL_AIR = 1;
   public static final Vector3i[] CHILD_MIN_OFFSETS = new Vector3i[]{
      new Vector3i(0, 0, 0),
      new Vector3i(0, 0, 1),
      new Vector3i(0, 1, 0),
      new Vector3i(0, 1, 1),
      new Vector3i(1, 0, 0),
      new Vector3i(1, 0, 1),
      new Vector3i(1, 1, 0),
      new Vector3i(1, 1, 1)
   };
   public static final Vector3i[] EDGE_OFFSETS = new Vector3i[]{
      new Vector3i(1, 2, 0),
      new Vector3i(1, 0, 2),
      new Vector3i(2, 1, 0),
      new Vector3i(0, 1, 2),
      new Vector3i(2, 0, 1),
      new Vector3i(0, 2, 1),
      new Vector3i(1, 0, 0),
      new Vector3i(0, 1, 0),
      new Vector3i(0, 0, 1),
      new Vector3i(1, 2, 2),
      new Vector3i(2, 2, 1),
      new Vector3i(2, 1, 2)
   };
   private static final byte[][] EDGE_V_MAP = new byte[][]{{0, 4}, {1, 5}, {2, 6}, {3, 7}, {0, 2}, {1, 3}, {4, 6}, {5, 7}, {0, 1}, {2, 3}, {4, 5}, {6, 7}};
   private static final byte[][] CELL_PROC_FACE_MASK = new byte[][]{
      {0, 4, 0}, {1, 5, 0}, {2, 6, 0}, {3, 7, 0}, {0, 2, 1}, {4, 6, 1}, {1, 3, 1}, {5, 7, 1}, {0, 1, 2}, {2, 3, 2}, {4, 5, 2}, {6, 7, 2}
   };
   private static final byte[][] CELL_PROC_EDGE_MASK = new byte[][]{
      {0, 1, 2, 3, 0}, {4, 5, 6, 7, 0}, {0, 4, 1, 5, 1}, {2, 6, 3, 7, 1}, {0, 2, 4, 6, 2}, {1, 3, 5, 7, 2}
   };
   private static final byte[][][] FACE_PROC_FACE_MASK = new byte[][][]{
      {{4, 0, 0}, {5, 1, 0}, {6, 2, 0}, {7, 3, 0}}, {{2, 0, 1}, {6, 4, 1}, {3, 1, 1}, {7, 5, 1}}, {{1, 0, 2}, {3, 2, 2}, {5, 4, 2}, {7, 6, 2}}
   };
   private static final byte[][][] FACE_PROC_EDGE_MASK = new byte[][][]{
      {{1, 4, 0, 5, 1, 1}, {1, 6, 2, 7, 3, 1}, {0, 4, 6, 0, 2, 2}, {0, 5, 7, 1, 3, 2}},
      {{0, 2, 3, 0, 1, 0}, {0, 6, 7, 4, 5, 0}, {1, 2, 0, 6, 4, 2}, {1, 3, 1, 7, 5, 2}},
      {{1, 1, 0, 3, 2, 0}, {1, 5, 4, 7, 6, 0}, {0, 1, 5, 0, 4, 1}, {0, 3, 7, 2, 6, 1}}
   };
   private static final byte[][][] EDGE_PROC_EDGE_MASK = new byte[][][]{
      {{3, 2, 1, 0, 0}, {7, 6, 5, 4, 0}}, {{5, 1, 4, 0, 1}, {7, 3, 6, 2, 1}}, {{6, 4, 2, 0, 2}, {7, 5, 3, 1, 2}}
   };
   private static final byte[][] PROCESS_EDGE_MASK = new byte[][]{{3, 2, 1, 0}, {7, 5, 6, 4}, {11, 10, 9, 8}};
   private static final byte[][] ORDERS = new byte[][]{{0, 0, 1, 1}, {0, 1, 0, 1}};
   private static final byte[][] CORNER_EDGE_MAP = new byte[][]{
      new byte[0],
      {0, 4, 8},
      {1, 5, 8},
      {0, 1, 4, 5},
      {2, 4, 9},
      {0, 2, 8, 9},
      {1, 2, 4, 5, 8, 9},
      {0, 1, 2, 5, 9},
      {3, 5, 9},
      {0, 3, 4, 5, 8, 9},
      {1, 3, 8, 9},
      {0, 1, 3, 4, 9},
      {2, 3, 4, 5},
      {0, 2, 3, 5, 8},
      {1, 2, 3, 4, 8},
      {0, 1, 2, 3},
      {0, 6, 10},
      {4, 6, 8, 10},
      {0, 1, 5, 6, 8, 10},
      {1, 4, 5, 6, 10},
      {0, 2, 4, 6, 9, 10},
      {2, 6, 8, 9, 10},
      {0, 1, 2, 4, 5, 6, 8, 9, 10},
      {1, 2, 5, 6, 9, 10},
      {0, 3, 5, 6, 9, 10},
      {3, 4, 5, 6, 8, 9, 10},
      {0, 1, 3, 6, 8, 9, 10},
      {1, 3, 4, 6, 9, 10},
      {0, 2, 3, 4, 5, 6, 10},
      {2, 3, 5, 6, 8, 10},
      {0, 1, 2, 3, 4, 6, 8, 10},
      {1, 2, 3, 6, 10},
      {1, 7, 10},
      {0, 1, 4, 7, 8, 10},
      {5, 7, 8, 10},
      {0, 4, 5, 7, 10},
      {1, 2, 4, 7, 9, 10},
      {0, 1, 2, 7, 8, 9, 10},
      {2, 4, 5, 7, 8, 9, 10},
      {0, 2, 5, 7, 9, 10},
      {1, 3, 5, 7, 9, 10},
      {0, 1, 3, 4, 5, 7, 8, 9, 10},
      {3, 7, 8, 9, 10},
      {0, 3, 4, 7, 9, 10},
      {1, 2, 3, 4, 5, 7, 10},
      {0, 1, 2, 3, 5, 7, 8, 10},
      {2, 3, 4, 7, 8, 10},
      {0, 2, 3, 7, 10},
      {0, 1, 6, 7},
      {1, 4, 6, 7, 8},
      {0, 5, 6, 7, 8},
      {4, 5, 6, 7},
      {0, 1, 2, 4, 6, 7, 9},
      {1, 2, 6, 7, 8, 9},
      {0, 2, 4, 5, 6, 7, 8, 9},
      {2, 5, 6, 7, 9},
      {0, 1, 3, 5, 6, 7, 9},
      {1, 3, 4, 5, 6, 7, 8, 9},
      {0, 3, 6, 7, 8, 9},
      {3, 4, 6, 7, 9},
      {0, 1, 2, 3, 4, 5, 6, 7},
      {1, 2, 3, 5, 6, 7, 8},
      {0, 2, 3, 4, 6, 7, 8},
      {2, 3, 6, 7},
      {2, 6, 11},
      {0, 2, 4, 6, 8, 11},
      {1, 2, 5, 6, 8, 11},
      {0, 1, 2, 4, 5, 6, 11},
      {4, 6, 9, 11},
      {0, 6, 8, 9, 11},
      {1, 4, 5, 6, 8, 9, 11},
      {0, 1, 5, 6, 9, 11},
      {2, 3, 5, 6, 9, 11},
      {0, 2, 3, 4, 5, 6, 8, 9, 11},
      {1, 2, 3, 6, 8, 9, 11},
      {0, 1, 2, 3, 4, 6, 9, 11},
      {3, 4, 5, 6, 11},
      {0, 3, 5, 6, 8, 11},
      {1, 3, 4, 6, 8, 11},
      {0, 1, 3, 6, 11},
      {0, 2, 10, 11},
      {2, 4, 8, 10, 11},
      {0, 1, 2, 5, 8, 10, 11},
      {1, 2, 4, 5, 10, 11},
      {0, 4, 9, 10, 11},
      {8, 9, 10, 11},
      {0, 1, 4, 5, 8, 9, 10, 11},
      {1, 5, 9, 10, 11},
      {0, 2, 3, 5, 9, 10, 11},
      {2, 3, 4, 5, 8, 9, 10, 11},
      {0, 1, 2, 3, 8, 9, 10, 11},
      {1, 2, 3, 4, 9, 10, 11},
      {0, 3, 4, 5, 10, 11},
      {3, 5, 8, 10, 11},
      {0, 1, 3, 4, 8, 10, 11},
      {1, 3, 10, 11},
      {1, 2, 6, 7, 10, 11},
      {0, 1, 2, 4, 6, 7, 8, 10, 11},
      {2, 5, 6, 7, 8, 10, 11},
      {0, 2, 4, 5, 6, 7, 10, 11},
      {1, 4, 6, 7, 9, 10, 11},
      {0, 1, 6, 7, 8, 9, 10, 11},
      {4, 5, 6, 7, 8, 9, 10, 11},
      {0, 5, 6, 7, 9, 10, 11},
      {1, 2, 3, 5, 6, 7, 9, 10, 11},
      {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
      {2, 3, 6, 7, 8, 9, 10, 11},
      {0, 2, 3, 4, 6, 7, 9, 10, 11},
      {1, 3, 4, 5, 6, 7, 10, 11},
      {0, 1, 3, 5, 6, 7, 8, 10, 11},
      {3, 4, 6, 7, 8, 10, 11},
      {0, 3, 6, 7, 10, 11},
      {0, 1, 2, 7, 11},
      {1, 2, 4, 7, 8, 11},
      {0, 2, 5, 7, 8, 11},
      {2, 4, 5, 7, 11},
      {0, 1, 4, 7, 9, 11},
      {1, 7, 8, 9, 11},
      {0, 4, 5, 7, 8, 9, 11},
      {5, 7, 9, 11},
      {0, 1, 2, 3, 5, 7, 9, 11},
      {1, 2, 3, 4, 5, 7, 8, 9, 11},
      {0, 2, 3, 7, 8, 9, 11},
      {2, 3, 4, 7, 9, 11},
      {0, 1, 3, 4, 5, 7, 11},
      {1, 3, 5, 7, 8, 11},
      {0, 3, 4, 7, 8, 11},
      {3, 7, 11},
      {3, 7, 11},
      {0, 3, 4, 7, 8, 11},
      {1, 3, 5, 7, 8, 11},
      {0, 1, 3, 4, 5, 7, 11},
      {2, 3, 4, 7, 9, 11},
      {0, 2, 3, 7, 8, 9, 11},
      {1, 2, 3, 4, 5, 7, 8, 9, 11},
      {0, 1, 2, 3, 5, 7, 9, 11},
      {5, 7, 9, 11},
      {0, 4, 5, 7, 8, 9, 11},
      {1, 7, 8, 9, 11},
      {0, 1, 4, 7, 9, 11},
      {2, 4, 5, 7, 11},
      {0, 2, 5, 7, 8, 11},
      {1, 2, 4, 7, 8, 11},
      {0, 1, 2, 7, 11},
      {0, 3, 6, 7, 10, 11},
      {3, 4, 6, 7, 8, 10, 11},
      {0, 1, 3, 5, 6, 7, 8, 10, 11},
      {1, 3, 4, 5, 6, 7, 10, 11},
      {0, 2, 3, 4, 6, 7, 9, 10, 11},
      {2, 3, 6, 7, 8, 9, 10, 11},
      {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
      {1, 2, 3, 5, 6, 7, 9, 10, 11},
      {0, 5, 6, 7, 9, 10, 11},
      {4, 5, 6, 7, 8, 9, 10, 11},
      {0, 1, 6, 7, 8, 9, 10, 11},
      {1, 4, 6, 7, 9, 10, 11},
      {0, 2, 4, 5, 6, 7, 10, 11},
      {2, 5, 6, 7, 8, 10, 11},
      {0, 1, 2, 4, 6, 7, 8, 10, 11},
      {1, 2, 6, 7, 10, 11},
      {1, 3, 10, 11},
      {0, 1, 3, 4, 8, 10, 11},
      {3, 5, 8, 10, 11},
      {0, 3, 4, 5, 10, 11},
      {1, 2, 3, 4, 9, 10, 11},
      {0, 1, 2, 3, 8, 9, 10, 11},
      {2, 3, 4, 5, 8, 9, 10, 11},
      {0, 2, 3, 5, 9, 10, 11},
      {1, 5, 9, 10, 11},
      {0, 1, 4, 5, 8, 9, 10, 11},
      {8, 9, 10, 11},
      {0, 4, 9, 10, 11},
      {1, 2, 4, 5, 10, 11},
      {0, 1, 2, 5, 8, 10, 11},
      {2, 4, 8, 10, 11},
      {0, 2, 10, 11},
      {0, 1, 3, 6, 11},
      {1, 3, 4, 6, 8, 11},
      {0, 3, 5, 6, 8, 11},
      {3, 4, 5, 6, 11},
      {0, 1, 2, 3, 4, 6, 9, 11},
      {1, 2, 3, 6, 8, 9, 11},
      {0, 2, 3, 4, 5, 6, 8, 9, 11},
      {2, 3, 5, 6, 9, 11},
      {0, 1, 5, 6, 9, 11},
      {1, 4, 5, 6, 8, 9, 11},
      {0, 6, 8, 9, 11},
      {4, 6, 9, 11},
      {0, 1, 2, 4, 5, 6, 11},
      {1, 2, 5, 6, 8, 11},
      {0, 2, 4, 6, 8, 11},
      {2, 6, 11},
      {2, 3, 6, 7},
      {0, 2, 3, 4, 6, 7, 8},
      {1, 2, 3, 5, 6, 7, 8},
      {0, 1, 2, 3, 4, 5, 6, 7},
      {3, 4, 6, 7, 9},
      {0, 3, 6, 7, 8, 9},
      {1, 3, 4, 5, 6, 7, 8, 9},
      {0, 1, 3, 5, 6, 7, 9},
      {2, 5, 6, 7, 9},
      {0, 2, 4, 5, 6, 7, 8, 9},
      {1, 2, 6, 7, 8, 9},
      {0, 1, 2, 4, 6, 7, 9},
      {4, 5, 6, 7},
      {0, 5, 6, 7, 8},
      {1, 4, 6, 7, 8},
      {0, 1, 6, 7},
      {0, 2, 3, 7, 10},
      {2, 3, 4, 7, 8, 10},
      {0, 1, 2, 3, 5, 7, 8, 10},
      {1, 2, 3, 4, 5, 7, 10},
      {0, 3, 4, 7, 9, 10},
      {3, 7, 8, 9, 10},
      {0, 1, 3, 4, 5, 7, 8, 9, 10},
      {1, 3, 5, 7, 9, 10},
      {0, 2, 5, 7, 9, 10},
      {2, 4, 5, 7, 8, 9, 10},
      {0, 1, 2, 7, 8, 9, 10},
      {1, 2, 4, 7, 9, 10},
      {0, 4, 5, 7, 10},
      {5, 7, 8, 10},
      {0, 1, 4, 7, 8, 10},
      {1, 7, 10},
      {1, 2, 3, 6, 10},
      {0, 1, 2, 3, 4, 6, 8, 10},
      {2, 3, 5, 6, 8, 10},
      {0, 2, 3, 4, 5, 6, 10},
      {1, 3, 4, 6, 9, 10},
      {0, 1, 3, 6, 8, 9, 10},
      {3, 4, 5, 6, 8, 9, 10},
      {0, 3, 5, 6, 9, 10},
      {1, 2, 5, 6, 9, 10},
      {0, 1, 2, 4, 5, 6, 8, 9, 10},
      {2, 6, 8, 9, 10},
      {0, 2, 4, 6, 9, 10},
      {1, 4, 5, 6, 10},
      {0, 1, 5, 6, 8, 10},
      {4, 6, 8, 10},
      {0, 6, 10},
      {0, 1, 2, 3},
      {1, 2, 3, 4, 8},
      {0, 2, 3, 5, 8},
      {2, 3, 4, 5},
      {0, 1, 3, 4, 9},
      {1, 3, 8, 9},
      {0, 3, 4, 5, 8, 9},
      {3, 5, 9},
      {0, 1, 2, 5, 9},
      {1, 2, 4, 5, 8, 9},
      {0, 2, 8, 9},
      {2, 4, 9},
      {0, 1, 4, 5},
      {1, 5, 8},
      {0, 4, 8},
      new byte[0]
   };
   private Vector3f p = new Vector3f();
   private byte[] cornerDensities = new byte[8];
   private int[] indices = new int[]{-1, -1, -1, -1};
   private boolean[] signChange = new boolean[]{false, false, false, false};
   private ObjectArrayList<OctreeNode> stack = new ObjectArrayList(100);
   private SimplePoolOctreeNode poolOctreeNode = new SimplePoolOctreeNode(32000);
   private SimplePoolVertex poolVertex = new SimplePoolVertex(10000);
   private SimplePoolLongObjectHashMap<OctreeNode> poolHashMap = new SimplePoolLongObjectHashMap<>(10);
   private List<Vertex> triangles = new ObjectArrayList(1000);
   private Long2ObjectOpenHashMap<OctreeNode> octreeNodes = new Long2ObjectOpenHashMap(1000);

   public long hash(Vector3i min) {
      return (min.x & 2097151) << 40 | (min.y & 2097151) << 20 | min.z & 2097151;
   }

   public long hash(int x, int y, int z) {
      return (x & 2097151) << 40 | (y & 2097151) << 20 | z & 2097151;
   }

   public OctreeNode buildOctreeFromBottom(Long2ObjectOpenHashMap<OctreeNode> seams, int chunkX, int chunkY, int chunkZ, int size) {
      Vector3i childPos = new Vector3i();
      Long2ObjectOpenHashMap<OctreeNode> parents = this.poolHashMap.get();

      do {
         ObjectIterator<Entry<OctreeNode>> it = seams.long2ObjectEntrySet().iterator();
         parents = this.poolHashMap.get();

         while (it.hasNext()) {
            Entry<OctreeNode> entry = (Entry<OctreeNode>)it.next();
            OctreeNode node = (OctreeNode)entry.getValue();
            if (node.size == size) {
               int parentSize = node.size * 2;
               int parentSizeBitMask = parentSize - 1;
               int px = node.min.x - (node.min.x & parentSizeBitMask);
               int py = node.min.y - (node.min.y & parentSizeBitMask);
               int pz = node.min.z - (node.min.z & parentSizeBitMask);
               OctreeNode parent = null;
               parent = (OctreeNode)parents.get(this.hash(px, py, pz));
               if (parent == null) {
                  parent = this.poolOctreeNode.get();
                  parent.min.set(px, py, pz);
                  parent.size = parentSize;
                  parent.type = OctreeNodeType.INTERNAL;
                  parents.put(this.hash(parent.min), parent);
               }

               for (int j = 0; j < 8; j++) {
                  CHILD_MIN_OFFSETS[j].mul(parentSize / 2, childPos).add(px, py, pz);
                  if (childPos.equals(node.min)) {
                     parent.children[j] = node;
                     it.remove();
                     break;
                  }
               }
            }
         }

         parents.putAll(seams);
         seams = parents;
         size *= 2;
      } while (parents.size() > 1);

      this.poolHashMap.reset();
      return parents.size() == 1 ? (OctreeNode)parents.values().iterator().next() : null;
   }

   public void findNodes(OctreeNode node, int function, Long2ObjectOpenHashMap<OctreeNode> nodes, Vector3i seamValues) {
      if (node != null && node.min != null) {
         Vector3i max = node.min.add(node.size, node.size, node.size, new Vector3i());
         if (this.isOnEdge(node.min, max, function, seamValues)) {
            if (node.type == OctreeNodeType.LEAF) {
               nodes.put(this.hash(node.min), node);
            } else {
               for (int i = 0; i < 8; i++) {
                  this.findNodes(node.children[i], function, nodes, seamValues);
               }
            }
         }
      }
   }

   public boolean isOnEdge(Vector3i min, Vector3i max, int function, Vector3i seamValues) {
      switch (function) {
         case 0:
            return max.x == seamValues.x || max.y == seamValues.y || max.z == seamValues.z;
         case 1:
            return min.z == seamValues.z;
         case 2:
            return min.y == seamValues.y;
         case 3:
            return min.y == seamValues.y && min.z == seamValues.z;
         case 4:
            return min.x == seamValues.x;
         case 5:
            return min.x == seamValues.x && min.z == seamValues.z;
         case 6:
            return min.x == seamValues.x && min.y == seamValues.y;
         case 7:
            return min.x == seamValues.x && min.y == seamValues.y && min.z == seamValues.z;
         default:
            return false;
      }
   }

   public void constructOctreeLinear(OctreeNode node, Chunk chunk, Set<Vector3i> affectedCells) {
      this.constructOctreeLinear(node, chunk, affectedCells, 0, 0, 0, chunk.width - 1, chunk.height - 1, chunk.depth - 1);
   }

   public void constructOctreeLinear(OctreeNode node, Chunk chunk, Set<Vector3i> affectedCells, int sx, int sy, int sz, int ex, int ey, int ez) {
      this.octreeNodes.clear();

      for (Vector3i affectedCell : affectedCells) {
         if (affectedCell.x >= sx && affectedCell.y >= sy && affectedCell.z >= sz && affectedCell.x < ex && affectedCell.y < ey && affectedCell.z < ez) {
            OctreeNode leafNode = this.constructLeafNodeFast(affectedCell.x, affectedCell.y, affectedCell.z, chunk);
            if (leafNode != null) {
               this.octreeNodes.put(this.hash(leafNode.min), leafNode);
            }
         }
      }

      OctreeNode root = this.buildOctreeFromBottom(this.octreeNodes, 0, 0, 0, 1);
      if (root != null) {
         node.reset();
         node.type = root.type;
         node.size = root.size;
         node.min = root.min;
         node.children = root.children;
      }
   }

   public OctreeNode constructLeafNodeFast(int x, int y, int z, Chunk chunk) {
      byte corners = 0;

      for (int i = 0; i < 8; i++) {
         this.cornerDensities[i] = chunk.get(x + CHILD_MIN_OFFSETS[i].x, y + CHILD_MIN_OFFSETS[i].y, z + CHILD_MIN_OFFSETS[i].z).density;
         int material = this.cornerDensities[i] < 0 ? 1 : 0;
         corners = (byte)(corners | material << i);
      }

      if (corners != 0 && corners != 255) {
         OctreeNode node = this.poolOctreeNode.get();
         if (node.drawInfo == null) {
            node.drawInfo = new OctreeDrawInfo();
         }

         Vector3f massPoint = node.drawInfo.pos.set(0.0F);
         byte[] edges = CORNER_EDGE_MAP[corners & 255];

         for (int i = 0; i < edges.length; i++) {
            byte edge = edges[i];
            int c1 = EDGE_V_MAP[edge][0];
            int c2 = EDGE_V_MAP[edge][1];
            massPoint.add(
               this.positionInterpolation(
                  0.0F,
                  x + CHILD_MIN_OFFSETS[c1].x,
                  y + CHILD_MIN_OFFSETS[c1].y,
                  z + CHILD_MIN_OFFSETS[c1].z,
                  x + CHILD_MIN_OFFSETS[c2].x,
                  y + CHILD_MIN_OFFSETS[c2].y,
                  z + CHILD_MIN_OFFSETS[c2].z,
                  this.cornerDensities[c1],
                  this.cornerDensities[c2]
               )
            );
         }

         massPoint.mul(1.0F / edges.length);
         node.type = OctreeNodeType.LEAF;
         node.min.set(x, y, z);
         node.size = 1;
         node.drawInfo.corners = corners;
         node.drawInfo.ambient = this.calculateLight(x, y, z, chunk);
         return node;
      } else {
         return null;
      }
   }

   private int calculateLight(int x, int y, int z, Chunk chunk) {
      int ambient = chunk.get(x, y, z).ambient;
      ambient = Math.max(ambient, chunk.get(x + 1, y, z).ambient);
      ambient = Math.max(ambient, chunk.get(x, y + 1, z).ambient);
      ambient = Math.max(ambient, chunk.get(x, y, z + 1).ambient);
      ambient = Math.max(ambient, chunk.get(x, y + 1, z + 1).ambient);
      ambient = Math.max(ambient, chunk.get(x + 1, y, z + 1).ambient);
      return Math.max(ambient, chunk.get(x + 1, y + 1, z + 1).ambient);
   }

   private Vector3f positionInterpolation(float isolevel, float p1x, float p1y, float p1z, float p2x, float p2y, float p2z, float valp1, float valp2) {
      float mu = (isolevel - valp1) / (valp2 - valp1);
      this.p.x = p1x + mu * (p2x - p1x);
      this.p.y = p1y + mu * (p2y - p1y);
      this.p.z = p1z + mu * (p2z - p1z);
      return this.p;
   }

   public void generateMesh(OctreeNode node, List<Vertex> vertexBuffer, IntArrayList indexBuffer) {
      vertexBuffer.clear();
      indexBuffer.clear();
      this.poolVertex.reset();
      this.generateVertexIndicesIterative(node, vertexBuffer);
      this.contourCellProc(node, indexBuffer);
      this.poolOctreeNode.reset();
   }

   private void contourCellProc(OctreeNode node, IntList indexBuffer) {
      if (node != null && node.type != OctreeNodeType.LEAF) {
         for (int i = 0; i < 8; i++) {
            this.contourCellProc(node.children[i], indexBuffer);
         }

         for (int i = 0; i < 12; i++) {
            this.contourFaceProc(node.children[CELL_PROC_FACE_MASK[i][0]], node.children[CELL_PROC_FACE_MASK[i][1]], CELL_PROC_FACE_MASK[i][2], indexBuffer);
         }

         for (int i = 0; i < 6; i++) {
            this.contourEdgeProc(
               node.children[CELL_PROC_EDGE_MASK[i][0]],
               node.children[CELL_PROC_EDGE_MASK[i][1]],
               node.children[CELL_PROC_EDGE_MASK[i][2]],
               node.children[CELL_PROC_EDGE_MASK[i][3]],
               CELL_PROC_EDGE_MASK[i][4],
               indexBuffer
            );
         }
      }
   }

   private void contourFaceProc(OctreeNode node0, OctreeNode node1, int dir, IntList indexBuffer) {
      if (node0 != null && node1 != null) {
         if (node0.type == OctreeNodeType.INTERNAL || node1.type == OctreeNodeType.INTERNAL) {
            for (int i = 0; i < 4; i++) {
               this.contourFaceProc(
                  node0.type != OctreeNodeType.INTERNAL ? node0 : node0.children[FACE_PROC_FACE_MASK[dir][i][0]],
                  node1.type != OctreeNodeType.INTERNAL ? node1 : node1.children[FACE_PROC_FACE_MASK[dir][i][1]],
                  FACE_PROC_FACE_MASK[dir][i][2],
                  indexBuffer
               );
            }

            for (int i = 0; i < 4; i++) {
               OctreeNode n0 = ORDERS[FACE_PROC_EDGE_MASK[dir][i][0]][0] == 0 ? node0 : node1;
               OctreeNode n1 = ORDERS[FACE_PROC_EDGE_MASK[dir][i][0]][1] == 0 ? node0 : node1;
               OctreeNode n2 = ORDERS[FACE_PROC_EDGE_MASK[dir][i][0]][2] == 0 ? node0 : node1;
               OctreeNode n3 = ORDERS[FACE_PROC_EDGE_MASK[dir][i][0]][3] == 0 ? node0 : node1;
               this.contourEdgeProc(
                  n0.type == OctreeNodeType.LEAF ? n0 : n0.children[FACE_PROC_EDGE_MASK[dir][i][1]],
                  n1.type == OctreeNodeType.LEAF ? n1 : n1.children[FACE_PROC_EDGE_MASK[dir][i][2]],
                  n2.type == OctreeNodeType.LEAF ? n2 : n2.children[FACE_PROC_EDGE_MASK[dir][i][3]],
                  n3.type == OctreeNodeType.LEAF ? n3 : n3.children[FACE_PROC_EDGE_MASK[dir][i][4]],
                  FACE_PROC_EDGE_MASK[dir][i][5],
                  indexBuffer
               );
            }
         }
      }
   }

   private void contourEdgeProc(OctreeNode node0, OctreeNode node1, OctreeNode node2, OctreeNode node3, int dir, IntList indexBuffer) {
      if (node0 != null && node1 != null && node2 != null && node3 != null) {
         if (node0.type != OctreeNodeType.INTERNAL
            && node1.type != OctreeNodeType.INTERNAL
            && node2.type != OctreeNodeType.INTERNAL
            && node3.type != OctreeNodeType.INTERNAL) {
            this.contourProcessEdge(node0, node1, node2, node3, dir, indexBuffer);
         } else {
            for (int i = 0; i < 2; i++) {
               this.contourEdgeProc(
                  node0.type == OctreeNodeType.LEAF ? node0 : node0.children[EDGE_PROC_EDGE_MASK[dir][i][0]],
                  node1.type == OctreeNodeType.LEAF ? node1 : node1.children[EDGE_PROC_EDGE_MASK[dir][i][1]],
                  node2.type == OctreeNodeType.LEAF ? node2 : node2.children[EDGE_PROC_EDGE_MASK[dir][i][2]],
                  node3.type == OctreeNodeType.LEAF ? node3 : node3.children[EDGE_PROC_EDGE_MASK[dir][i][3]],
                  EDGE_PROC_EDGE_MASK[dir][i][4],
                  indexBuffer
               );
            }
         }
      }
   }

   private void contourProcessEdge(OctreeNode node0, OctreeNode node1, OctreeNode node2, OctreeNode node3, int dir, IntList indexBuffer) {
      int minSize = 2147483647;
      int minIndex = 0;
      boolean flip = false;

      for (int i = 0; i < 4; i++) {
         OctreeNode node = node3;
         if (i == 0) {
            node = node0;
         } else if (i == 1) {
            node = node1;
         } else if (i == 2) {
            node = node2;
         }

         int edge = PROCESS_EDGE_MASK[dir][i];
         int c1 = EDGE_V_MAP[edge][0];
         int c2 = EDGE_V_MAP[edge][1];
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

   private void generateVertexIndicesIterative(OctreeNode node, List<Vertex> vertexBuffer) {
      if (node != null) {
         this.stack.clear();
         this.stack.push(node);
         OctreeNode current = null;

         while (!this.stack.isEmpty()) {
            current = (OctreeNode)this.stack.pop();
            if (current.type == OctreeNodeType.LEAF) {
               OctreeDrawInfo d = current.drawInfo;
               d.index = vertexBuffer.size();
               vertexBuffer.add(this.poolVertex.get(d.pos, d.ambient));
            } else if (current.type == OctreeNodeType.INTERNAL) {
               for (int i = 0; i < 8; i++) {
                  if (current.children[i] != null) {
                     this.stack.push(current.children[i]);
                  }
               }
            }
         }
      }
   }

   public Mesh buildMeshFlat(
      List<Vertex> vertexBuffer, IntList indexBuffer, Mesh mesh, Vector3d uvPosOffset, double scale, Vector2f textureScale, int meshColor
   ) {
      if (mesh == null) {
         mesh = new Mesh();
      }

      Data lightAttribute = Data.LIGHT_SHADER;
      Data normalAttribute = Data.NORMAL_SHADER;
      Data midTexCoordAttribute = Data.MID_TEX_COORD_SHADER;
      Data tangentAttribute = Data.TANGENT_SHADER;
      if (StarterClient.optifabric) {
         lightAttribute = Data.LIGHT;
         normalAttribute = Data.NORMAL;
         midTexCoordAttribute = Data.MID_TEX_COORD_OPTIFINE;
         tangentAttribute = Data.TANGENT_OPTIFINE;
      }

      float[] position = mesh.getNative(Data.POSITION_SHADER) != null && ((float[])mesh.getNative(Data.POSITION_SHADER)).length > indexBuffer.size() * 3
         ? (float[])mesh.getNative(Data.POSITION_SHADER)
         : new float[indexBuffer.size() * 3 * 2];
      int[] color = mesh.getNative(Data.COLOR_SHADER) != null && ((int[])mesh.getNative(Data.COLOR_SHADER)).length > indexBuffer.size()
         ? (int[])mesh.getNative(Data.COLOR_SHADER)
         : new int[indexBuffer.size() * 2];
      int[] normal = mesh.getNative(normalAttribute) != null && ((int[])mesh.getNative(normalAttribute)).length > indexBuffer.size()
         ? (int[])mesh.getNative(normalAttribute)
         : new int[indexBuffer.size() * 2];
      int[] tangent = mesh.getNative(tangentAttribute) != null && ((int[])mesh.getNative(tangentAttribute)).length > indexBuffer.size()
         ? (int[])mesh.getNative(tangentAttribute)
         : new int[indexBuffer.size() * 2];
      float[] uv = mesh.getNative(Data.TEX_COORD_SHADER) != null && ((float[])mesh.getNative(Data.TEX_COORD_SHADER)).length > indexBuffer.size() * 2
         ? (float[])mesh.getNative(Data.TEX_COORD_SHADER)
         : new float[indexBuffer.size() * 2 * 2];
      float[] midTexCoord = mesh.getNative(midTexCoordAttribute) != null && ((float[])mesh.getNative(midTexCoordAttribute)).length > indexBuffer.size() * 2
         ? (float[])mesh.getNative(midTexCoordAttribute)
         : new float[indexBuffer.size() * 2 * 2];
      int[] light = mesh.getNative(lightAttribute) != null && ((int[])mesh.getNative(lightAttribute)).length >= indexBuffer.size()
         ? (int[])mesh.getNative(lightAttribute)
         : new int[indexBuffer.size() * 2];
      int[] index = mesh.getNative(Data.INDEX) != null && ((int[])mesh.getNative(Data.INDEX)).length > indexBuffer.size()
         ? (int[])mesh.getNative(Data.INDEX)
         : new int[indexBuffer.size() * 2];
      Vector3f tmp1 = new Vector3f();
      Vector3f tmp2 = new Vector3f();
      Vector3f tmp3 = new Vector3f();
      this.triangles.clear();

      for (int i = 0; i < indexBuffer.size() / 3; i++) {
         Vertex v0 = vertexBuffer.get(indexBuffer.getInt(i * 3));
         Vertex v1 = vertexBuffer.get(indexBuffer.getInt(i * 3 + 1));
         Vertex v2 = vertexBuffer.get(indexBuffer.getInt(i * 3 + 2));
         v0.uv.set((float)(v0.position.z * scale + uvPosOffset.z) * textureScale.x, (float)(v0.position.x * scale + uvPosOffset.x) * textureScale.y);
         v1.uv.set((float)(v1.position.z * scale + uvPosOffset.z) * textureScale.x, (float)(v1.position.x * scale + uvPosOffset.x) * textureScale.y);
         v2.uv.set((float)(v2.position.z * scale + uvPosOffset.z) * textureScale.x, (float)(v2.position.x * scale + uvPosOffset.x) * textureScale.y);
         Vector3f edge1 = v1.position.sub(v0.position, tmp1);
         Vector3f edge2 = v2.position.sub(v0.position, tmp2);
         Vector3f n = edge1.cross(edge2, tmp3);
         float nlength = rsqrt(n.x * n.x + n.y * n.y + n.z * n.z);
         n.x *= nlength;
         n.y *= nlength;
         n.z *= nlength;
         v0.normal.add(n);
         v1.normal.add(n);
         v2.normal.add(n);
         float deltaU1 = v1.uv.x - v0.uv.x;
         float deltaV1 = v1.uv.y - v0.uv.y;
         float deltaU2 = v2.uv.x - v0.uv.x;
         float deltaV2 = v2.uv.y - v0.uv.y;
         float fdenom = deltaU1 * deltaV2 - deltaU2 * deltaV1;
         float f;
         if (fdenom == 0.0F) {
            f = 1.0F;
         } else {
            f = 1.0F / fdenom;
         }

         float tangentx = f * (deltaV2 * edge1.x - deltaV1 * edge2.x);
         float tangenty = f * (deltaV2 * edge1.y - deltaV1 * edge2.y);
         float tangentz = f * (deltaV2 * edge1.z - deltaV1 * edge2.z);
         float tcoeff = rsqrt(tangentx * tangentx + tangenty * tangenty + tangentz * tangentz);
         tangentx *= tcoeff;
         tangenty *= tcoeff;
         tangentz *= tcoeff;
         v0.tangent.add(tangentx, tangenty, tangentz);
         v1.tangent.add(tangentx, tangenty, tangentz);
         v2.tangent.add(tangentx, tangenty, tangentz);
         this.triangles.add(v0);
         this.triangles.add(v1);
         this.triangles.add(v2);
      }

      float midTexCoordX = 0.0F;
      float midTexCoordY = 0.0F;

      for (int i = 0; i < this.triangles.size(); i++) {
         Vertex v = this.triangles.get(i);
         normalize(v.normal, 0.0F, 1.0F, 0.0F);
         normalize(v.tangent, 1.0F, 0.0F, 0.0F);
         index[i] = i;
         position[i * 3] = v.position.x;
         position[i * 3 + 1] = v.position.y;
         position[i * 3 + 2] = v.position.z;
         normal[i] = Pack.normal(v.normal.x, v.normal.y, v.normal.z);
         tangent[i] = Pack.normal(v.tangent.x, v.tangent.y, v.tangent.z, 1.0F);
         color[i] = meshColor;
         uv[i * 2] = v.uv.x;
         uv[i * 2 + 1] = v.uv.y;
         midTexCoordX += v.uv.x;
         midTexCoordY += v.uv.y;
         if ((i + 1) % 3 == 0) {
            midTexCoordX = (float)(midTexCoordX * 0.33333333333);
            midTexCoordY = (float)(midTexCoordY * 0.33333333333);
            midTexCoord[i * 2] = midTexCoordX;
            midTexCoord[i * 2 + 1] = midTexCoordY;
            midTexCoord[(i - 1) * 2] = midTexCoordX;
            midTexCoord[(i - 1) * 2 + 1] = midTexCoordY;
            midTexCoord[(i - 2) * 2] = midTexCoordX;
            midTexCoord[(i - 2) * 2 + 1] = midTexCoordY;
            midTexCoordX = 0.0F;
            midTexCoordY = 0.0F;
         }

         light[i] = v.ambient;
      }

      mesh.set(position, Data.POSITION_SHADER);
      mesh.set(color, Data.COLOR_SHADER);
      mesh.set(uv, Data.TEX_COORD_SHADER);
      mesh.set(light, lightAttribute);
      mesh.set(normal, normalAttribute);
      mesh.set(midTexCoord, midTexCoordAttribute);
      mesh.set(tangent, tangentAttribute);
      mesh.set(index, Data.INDEX);
      mesh.setSize(Data.POSITION_SHADER, indexBuffer.size() * 3);
      mesh.setSize(Data.COLOR_SHADER, indexBuffer.size());
      mesh.setSize(Data.TEX_COORD_SHADER, indexBuffer.size() * 2);
      mesh.setSize(lightAttribute, indexBuffer.size());
      mesh.setSize(normalAttribute, indexBuffer.size());
      mesh.setSize(midTexCoordAttribute, indexBuffer.size() * 2);
      mesh.setSize(tangentAttribute, indexBuffer.size());
      mesh.setSize(Data.INDEX, indexBuffer.size());
      return mesh;
   }

   public Mesh buildMeshFlatClassic(
      List<Vertex> vertexBuffer, IntList indexBuffer, Mesh mesh, Vector3d uvPosOffset, double scale, Vector2f textureScale, int meshColor
   ) {
      if (mesh == null) {
         mesh = new Mesh();
      }

      float[] position = mesh.getNative(Data.POSITION_SHADER) != null && ((float[])mesh.getNative(Data.POSITION_SHADER)).length >= indexBuffer.size() * 3
         ? (float[])mesh.getNative(Data.POSITION_SHADER)
         : new float[indexBuffer.size() * 3 * 2];
      int[] color = mesh.getNative(Data.COLOR_SHADER) != null && ((int[])mesh.getNative(Data.COLOR_SHADER)).length >= indexBuffer.size()
         ? (int[])mesh.getNative(Data.COLOR_SHADER)
         : new int[indexBuffer.size() * 2];
      int[] normal = mesh.getNative(Data.NORMAL_SHADER) != null && ((int[])mesh.getNative(Data.NORMAL_SHADER)).length >= indexBuffer.size()
         ? (int[])mesh.getNative(Data.NORMAL_SHADER)
         : new int[indexBuffer.size() * 2];
      float[] uv = mesh.getNative(Data.TEX_COORD_SHADER) != null && ((float[])mesh.getNative(Data.TEX_COORD_SHADER)).length >= indexBuffer.size() * 2
         ? (float[])mesh.getNative(Data.TEX_COORD_SHADER)
         : new float[indexBuffer.size() * 2 * 2];
      int[] light = mesh.getNative(Data.LIGHT_SHADER) != null && ((int[])mesh.getNative(Data.LIGHT_SHADER)).length >= indexBuffer.size()
         ? (int[])mesh.getNative(Data.LIGHT_SHADER)
         : new int[indexBuffer.size() * 2];
      int[] index = mesh.getNative(Data.INDEX) != null && ((int[])mesh.getNative(Data.INDEX)).length >= indexBuffer.size()
         ? (int[])mesh.getNative(Data.INDEX)
         : new int[indexBuffer.size() * 2];
      Vector3f tmp1 = new Vector3f();
      Vector3f tmp2 = new Vector3f();
      Vector3f tmp3 = new Vector3f();
      this.triangles.clear();

      for (int i = 0; i < indexBuffer.size() / 3; i++) {
         Vertex v0 = vertexBuffer.get(indexBuffer.getInt(i * 3));
         Vertex v1 = vertexBuffer.get(indexBuffer.getInt(i * 3 + 1));
         Vertex v2 = vertexBuffer.get(indexBuffer.getInt(i * 3 + 2));
         Vector3f u = v1.position.sub(v0.position, tmp1);
         Vector3f v = v2.position.sub(v0.position, tmp2);
         Vector3f n = u.cross(v, tmp3);
         float nlength = rsqrt(n.x * n.x + n.y * n.y + n.z * n.z);
         n.x *= nlength;
         n.y *= nlength;
         n.z *= nlength;
         v0.normal.add(n);
         v1.normal.add(n);
         v2.normal.add(n);
         this.triangles.add(v0);
         this.triangles.add(v1);
         this.triangles.add(v2);
      }

      for (int i = 0; i < this.triangles.size(); index[i] = i++) {
         Vertex v = this.triangles.get(i);
         normalize(v.normal, 0.0F, 1.0F, 0.0F);
         position[i * 3] = v.position.x;
         position[i * 3 + 1] = v.position.y;
         position[i * 3 + 2] = v.position.z;
         normal[i] = Pack.normal(v.normal.x, v.normal.y, v.normal.z);
         color[i] = meshColor;
         uv[i * 2] = (float)(v.position.z * scale + uvPosOffset.z) * textureScale.x;
         uv[i * 2 + 1] = (float)(v.position.x * scale + uvPosOffset.x) * textureScale.y;
         light[i] = v.ambient;
      }

      mesh.set(position, Data.POSITION_SHADER);
      mesh.set(color, Data.COLOR_SHADER);
      mesh.set(uv, Data.TEX_COORD_SHADER);
      mesh.set(light, Data.LIGHT_SHADER);
      mesh.set(normal, Data.NORMAL_SHADER);
      mesh.set(index, Data.INDEX);
      mesh.setSize(Data.POSITION_SHADER, indexBuffer.size() * 3);
      mesh.setSize(Data.COLOR_SHADER, indexBuffer.size());
      mesh.setSize(Data.TEX_COORD_SHADER, indexBuffer.size() * 2);
      mesh.setSize(Data.LIGHT_SHADER, indexBuffer.size());
      mesh.setSize(Data.NORMAL_SHADER, indexBuffer.size());
      mesh.setSize(Data.INDEX, indexBuffer.size());
      return mesh;
   }

   public static void normalize(Vector3f n, float defaultx, float defaulty, float defaultz) {
      float lengthSquared = n.x * n.x + n.y * n.y + n.z * n.z;
      if (lengthSquared == 0.0F) {
         n.x = defaultx;
         n.y = defaulty;
         n.z = defaultz;
      } else {
         lengthSquared = (float)(1.0 / Math.sqrt(lengthSquared));
         n.x *= lengthSquared;
         n.y *= lengthSquared;
         n.z *= lengthSquared;
      }
   }

   public static void normalize(Vector4f n, float defaultx, float defaulty, float defaultz) {
      float lengthSquared = n.x * n.x + n.y * n.y + n.z * n.z;
      if (lengthSquared == 0.0F) {
         n.x = defaultx;
         n.y = defaulty;
         n.z = defaultz;
      } else {
         lengthSquared = (float)(1.0 / Math.sqrt(lengthSquared));
         n.x *= lengthSquared;
         n.y *= lengthSquared;
         n.z *= lengthSquared;
      }

      n.w = Math.signum(n.w);
   }

   public static float rsqrt(float value) {
      return value == 0.0F ? 1.0F : (float)(1.0 / Math.sqrt(value));
   }
}
