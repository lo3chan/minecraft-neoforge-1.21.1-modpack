package net.mehvahdjukaar.moonlight.api.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.jetbrains.annotations.Nullable;

public class SearchTrie<K, O> {
   protected final SearchTrie.TrieNode<K, O> root = new SearchTrie.TrieNode<>();

   public void insert(List<K> paths, O object) {
      SearchTrie.TrieNode<K, O> current = this.root;

      for (K folder : paths) {
         current.children.putIfAbsent(folder, new SearchTrie.TrieNode<>());
         current = current.children.get(folder);
      }

      current.objects.add(object);
   }

   public Collection<O> search(List<K> paths) {
      SearchTrie.TrieNode<K, O> current = this.getNode(paths);
      return current == null ? Collections.emptyList() : current.collectObjects();
   }

   public boolean remove(List<K> path) {
      SearchTrie.TrieNode<K, O> current = this.getNode(path);
      if (current == null) {
         return false;
      } else {
         current.children.clear();
         current.objects.clear();
         return true;
      }
   }

   @Nullable
   protected SearchTrie.TrieNode<K, O> getNode(List<K> path) {
      SearchTrie.TrieNode<K, O> current = this.root;

      for (K key : path) {
         current = current.children.getOrDefault(key, current.children.get(null));
         if (current == null) {
            return null;
         }
      }

      return current;
   }

   public void clear() {
      this.root.children.clear();
      this.root.objects.clear();
   }

   public Collection<K> listKeys(List<K> path) {
      SearchTrie.TrieNode<K, O> startNode = this.getNode(path);
      return (Collection<K>)(startNode != null ? startNode.children.keySet() : Collections.emptyList());
   }

   public void printTrie() {
      this.printTrie(Logger.getGlobal());
   }

   public void printTrie(Logger logger) {
      this.printNode(logger, this.root, "", "root", true);
   }

   private void printNode(Logger logger, SearchTrie.TrieNode<K, O> node, String prefix, String nodeName, boolean isTail) {
      if (!node.objects.isEmpty()) {
         logger.info(prefix + (isTail ? "\\--- " : "|--- ") + nodeName + " " + node.objects);
      } else {
         logger.info(prefix + (isTail ? "\\--- " : "|--- ") + nodeName + " (empty)");
      }

      List<K> childrenKeys = new ArrayList<>(node.children.keySet());

      for (int i = 0; i < childrenKeys.size(); i++) {
         K key = childrenKeys.get(i);
         SearchTrie.TrieNode<K, O> childNode = node.children.get(key);
         boolean isLastChild = i == childrenKeys.size() - 1;
         String newPrefix = prefix + (isTail ? "    " : "|   ");
         this.printNode(logger, childNode, newPrefix, key.toString(), isLastChild);
      }
   }

   protected static class TrieNode<K, O> {
      final Map<K, SearchTrie.TrieNode<K, O>> children = new HashMap<>();
      final List<O> objects = new ArrayList<>();

      public TrieNode() {
      }

      public List<O> collectObjects() {
         List<O> result = new ArrayList<>(this.objects);

         for (SearchTrie.TrieNode<K, O> child : this.children.values()) {
            result.addAll(child.collectObjects());
         }

         return result;
      }
   }
}
