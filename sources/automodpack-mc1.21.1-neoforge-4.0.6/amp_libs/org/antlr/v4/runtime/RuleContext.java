package amp_libs.org.antlr.v4.runtime;

import amp_libs.org.antlr.v4.runtime.misc.Interval;
import amp_libs.org.antlr.v4.runtime.tree.ParseTree;
import amp_libs.org.antlr.v4.runtime.tree.ParseTreeVisitor;
import amp_libs.org.antlr.v4.runtime.tree.RuleNode;
import amp_libs.org.antlr.v4.runtime.tree.Trees;
import java.util.Arrays;
import java.util.List;

public class RuleContext implements RuleNode {
   public RuleContext parent;
   public int invokingState = -1;

   public RuleContext() {
   }

   public RuleContext(RuleContext parent, int invokingState) {
      this.parent = parent;
      this.invokingState = invokingState;
   }

   public int depth() {
      int n = 0;

      for (RuleContext p = this; p != null; n++) {
         p = p.parent;
      }

      return n;
   }

   public boolean isEmpty() {
      return this.invokingState == -1;
   }

   @Override
   public Interval getSourceInterval() {
      return Interval.INVALID;
   }

   @Override
   public RuleContext getRuleContext() {
      return this;
   }

   public RuleContext getParent() {
      return this.parent;
   }

   public RuleContext getPayload() {
      return this;
   }

   @Override
   public String getText() {
      if (this.getChildCount() == 0) {
         return "";
      } else {
         StringBuilder builder = new StringBuilder();

         for (int i = 0; i < this.getChildCount(); i++) {
            builder.append(this.getChild(i).getText());
         }

         return builder.toString();
      }
   }

   public int getRuleIndex() {
      return -1;
   }

   public int getAltNumber() {
      return 0;
   }

   public void setAltNumber(int altNumber) {
   }

   @Override
   public void setParent(RuleContext parent) {
      this.parent = parent;
   }

   @Override
   public ParseTree getChild(int i) {
      return null;
   }

   @Override
   public int getChildCount() {
      return 0;
   }

   @Override
   public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      return (T)visitor.visitChildren(this);
   }

   @Override
   public String toStringTree(Parser recog) {
      return Trees.toStringTree(this, recog);
   }

   public String toStringTree(List<String> ruleNames) {
      return Trees.toStringTree(this, ruleNames);
   }

   @Override
   public String toStringTree() {
      return this.toStringTree((List<String>)null);
   }

   @Override
   public String toString() {
      return this.toString((List<String>)null, (RuleContext)null);
   }

   public final String toString(Recognizer<?, ?> recog) {
      return this.toString(recog, ParserRuleContext.EMPTY);
   }

   public final String toString(List<String> ruleNames) {
      return this.toString(ruleNames, null);
   }

   public String toString(Recognizer<?, ?> recog, RuleContext stop) {
      String[] ruleNames = recog != null ? recog.getRuleNames() : null;
      List<String> ruleNamesList = ruleNames != null ? Arrays.asList(ruleNames) : null;
      return this.toString(ruleNamesList, stop);
   }

   public String toString(List<String> ruleNames, RuleContext stop) {
      StringBuilder buf = new StringBuilder();
      RuleContext p = this;
      buf.append("[");

      for (; p != null && p != stop; p = p.parent) {
         if (ruleNames == null) {
            if (!p.isEmpty()) {
               buf.append(p.invokingState);
            }
         } else {
            int ruleIndex = p.getRuleIndex();
            String ruleName = ruleIndex >= 0 && ruleIndex < ruleNames.size() ? ruleNames.get(ruleIndex) : Integer.toString(ruleIndex);
            buf.append(ruleName);
         }

         if (p.parent != null && (ruleNames != null || !p.parent.isEmpty())) {
            buf.append(" ");
         }
      }

      buf.append("]");
      return buf.toString();
   }
}
