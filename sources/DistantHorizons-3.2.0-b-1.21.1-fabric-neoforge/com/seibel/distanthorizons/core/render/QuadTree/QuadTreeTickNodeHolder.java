package com.seibel.distanthorizons.core.render.QuadTree;

import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.util.objects.quadTree.QuadNode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class QuadTreeTickNodeHolder {
   private final HashSet<LodRenderSection> sectionsToLoad = new HashSet<>();
   private final HashSet<QuadNode<LodRenderSection>> presentNodes = new HashSet<>();
   private final HashSet<QuadNode<LodRenderSection>> nodesToEnable = new HashSet<>();
   private final HashSet<QuadNode<LodRenderSection>> nodesToDisable = new HashSet<>();
   private final ArrayList<QuadNode<LodRenderSection>> nodesToEnableDeleteChildrenList = new ArrayList<>();
   private final ArrayList<QuadNode<LodRenderSection>> nodesForWorldGen = new ArrayList<>();
   private final QuadTreeTickNodeHolder.QuadNodeNearComparator quadNodeNearComparator = new QuadTreeTickNodeHolder.QuadNodeNearComparator();

   public void clear() {
      this.sectionsToLoad.clear();
      this.presentNodes.clear();
      this.nodesToEnable.clear();
      this.nodesToDisable.clear();
      this.nodesToEnableDeleteChildrenList.clear();
   }

   public void addLoadSection(LodRenderSection section) {
      this.sectionsToLoad.add(section);
   }

   public HashSet<LodRenderSection> getLoadSections() {
      return this.sectionsToLoad;
   }

   public void addEnableNode(QuadNode<LodRenderSection> node) {
      if (this.presentNodes.add(node)) {
         this.nodesToEnable.add(node);
      }
   }

   public void removeEnableAndDisableNode(QuadNode<LodRenderSection> node) {
      this.nodesToEnable.remove(node);
      this.nodesToEnableDeleteChildrenList.remove(node);
      this.presentNodes.add(node);
      this.nodesToDisable.add(node);
   }

   public HashSet<QuadNode<LodRenderSection>> getEnabledNodes() {
      return this.nodesToEnable;
   }

   public void addDisableNode(QuadNode<LodRenderSection> node) {
      if (this.presentNodes.add(node)) {
         this.nodesToDisable.add(node);
      }
   }

   public HashSet<QuadNode<LodRenderSection>> getDisableNodes() {
      return this.nodesToDisable;
   }

   public void addEnableDeleteChildrenNode(QuadNode<LodRenderSection> node) {
      if (this.presentNodes.add(node)) {
         this.nodesToEnableDeleteChildrenList.add(node);
      }
   }

   public ArrayList<QuadNode<LodRenderSection>> getEnableDeleteChildrenNodes() {
      return this.nodesToEnableDeleteChildrenList;
   }

   public ArrayList<QuadNode<LodRenderSection>> getWorldGenNodesNearToFar(DhBlockPos2D centerPos) {
      this.quadNodeNearComparator.centerPos = centerPos;
      this.nodesToEnableDeleteChildrenList.sort(this.quadNodeNearComparator);
      this.nodesForWorldGen.clear();
      this.nodesForWorldGen.addAll(this.nodesToEnableDeleteChildrenList);
      return this.nodesForWorldGen;
   }

   private static class QuadNodeNearComparator implements Comparator<QuadNode<LodRenderSection>> {
      public DhBlockPos2D centerPos = DhBlockPos2D.ZERO;

      private QuadNodeNearComparator() {
      }

      public int compare(QuadNode<LodRenderSection> nodeA, QuadNode<LodRenderSection> nodeB) {
         int aDist = DhSectionPos.getManhattanBlockDistance(nodeA.sectionPos, this.centerPos);
         int bDist = DhSectionPos.getManhattanBlockDistance(nodeB.sectionPos, this.centerPos);
         return Integer.compare(aDist, bDist);
      }
   }
}
