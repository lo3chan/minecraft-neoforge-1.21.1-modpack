package mezz.jei.modshade.net.mezzdev.suffixtree;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.function.Consumer;

public class GeneralizedSuffixTree<T> implements ISuffixTree<T> {
   private final RootNode<T> root = new RootNode<>();
   private Node<T> activeLeaf = this.root;

   @Override
   public void getSearchResults(String word, Consumer<Collection<T>> resultsConsumer) {
      Node<T> currentNode = this.root;
      SubString wordSubString = new SubString(word);

      while (!wordSubString.isEmpty()) {
         Node<T> currentEdge = currentNode.getEdge(wordSubString);
         if (currentEdge == null) {
            return;
         }

         int lenToMatch = Math.min(wordSubString.length(), currentEdge.length());
         if (!currentEdge.startsWith(wordSubString, lenToMatch)) {
            return;
         }

         currentNode = currentEdge;
         if (lenToMatch == wordSubString.length()) {
            currentEdge.getData(resultsConsumer);
            return;
         }

         wordSubString = wordSubString.subSequence(lenToMatch);
      }
   }

   @Override
   public void getAllElements(Consumer<Collection<T>> resultsConsumer) {
      this.root.getData(resultsConsumer);
   }

   @Override
   public void put(String key, T value) {
      this.activeLeaf = this.root;
      Node<T> s = this.root;
      SubString keyString = new SubString(key);
      SubString text = keyString.shorten(keyString.length());

      for (int i = 0; i < keyString.length(); i++) {
         SubString rest = keyString.subSequence(i);
         Pair<Node<T>, SubString> active = this.update(s, text, keyString.charAt(i), rest, value);
         s = active.first();
         text = active.second();
      }

      if (null == this.activeLeaf.getSuffix() && this.activeLeaf != this.root && this.activeLeaf != s) {
         this.activeLeaf.setSuffix(s);
      }
   }

   private static <T> Pair<Boolean, Node<T>> testAndSplit(Node<T> startNode, SubString searchString, char t, SubString remainder, T value) {
      assert !remainder.isEmpty();

      assert remainder.charAt(0) == t;

      Pair<Node<T>, SubString> canonizeResult = canonize(startNode, searchString);
      startNode = canonizeResult.first();
      searchString = canonizeResult.second();
      if (!searchString.isEmpty()) {
         Node<T> g = startNode.getEdge(searchString);

         assert g != null;

         if (g.length() > searchString.length() && g.charAt(searchString.length()) == t) {
            return new Pair<>(true, startNode);
         } else {
            Node<T> newNode = splitNode(startNode, g, searchString);
            return new Pair<>(false, newNode);
         }
      } else {
         Node<T> e = startNode.getEdge(remainder);
         if (e == null) {
            return new Pair<>(false, startNode);
         } else if (e.startsWith(remainder)) {
            if (e.length() == remainder.length()) {
               e.addRef(value);
               return new Pair<>(true, startNode);
            } else {
               Node<T> newNode = splitNode(startNode, e, remainder);
               newNode.addRef(value);
               return new Pair<>(false, startNode);
            }
         } else {
            return new Pair<>(true, startNode);
         }
      }
   }

   private static <T> Node<T> splitNode(Node<T> s, Node<T> e, SubString splitFirstPart) {
      assert e == s.getEdge(splitFirstPart);

      assert e.startsWith(splitFirstPart);

      assert e.length() > splitFirstPart.length();

      SubString splitSecondPart = e.subSequence(splitFirstPart.length());
      Node<T> r = new Node<>(splitFirstPart);
      s.addEdge(r);
      e.set(splitSecondPart);
      r.addEdge(e);
      return r;
   }

   private static <T> Pair<Node<T>, SubString> canonize(Node<T> s, SubString input) {
      Node<T> currentNode = s;
      SubString remainder = input;

      while (!remainder.isEmpty()) {
         Node<T> nextEdge = currentNode.getEdge(remainder);
         if (nextEdge == null || !remainder.startsWith(nextEdge)) {
            break;
         }

         currentNode = nextEdge;
         remainder = remainder.subSequence(nextEdge.length());
      }

      return new Pair<>(currentNode, remainder);
   }

   private Pair<Node<T>, SubString> update(Node<T> s, SubString stringPart, char newChar, SubString rest, T value) {
      assert !rest.isEmpty();

      assert rest.charAt(0) == newChar;

      SubString k = stringPart.extend(newChar);
      Node<T> oldRoot = this.root;
      Pair<Boolean, Node<T>> ret = testAndSplit(s, stringPart, newChar, rest, value);
      Node<T> r = ret.second();
      boolean endpoint = ret.first();

      while (!endpoint) {
         Node<T> tempEdge = r.getEdge(newChar);
         Node<T> leaf;
         if (tempEdge != null) {
            leaf = tempEdge;
         } else {
            leaf = new Node<>(rest);
            leaf.addRef(value);
            r.addEdge(leaf);
         }

         if (this.activeLeaf != this.root) {
            this.activeLeaf.setSuffix(leaf);
         }

         this.activeLeaf = leaf;
         if (oldRoot != this.root) {
            oldRoot.setSuffix(r);
         }

         oldRoot = r;
         if (null == s.getSuffix()) {
            assert this.root == s;

            k = k.subSequence(1);
         } else {
            Pair<Node<T>, SubString> canonized = canonize(s.getSuffix(), k.shorten(1));
            char nextChar = k.charAt(k.length() - 1);
            s = canonized.first();
            k = canonized.second().extend(nextChar);
         }

         ret = testAndSplit(s, k.shorten(1), newChar, rest, value);
         endpoint = ret.first();
         r = ret.second();
      }

      if (oldRoot != this.root) {
         oldRoot.setSuffix(r);
      }

      return canonize(s, k);
   }

   @Override
   public String statistics() {
      return "GeneralizedSuffixTree:\nNode size stats: \n" + this.root.nodeSizeStats() + "\nNode edge stats: \n" + this.root.nodeEdgeStats();
   }

   public void printTree(PrintWriter out, boolean includeSuffixLinks) {
      this.root.printTree(out, includeSuffixLinks);
   }
}
