package dev.latvian.mods.rhino.ast;

import dev.latvian.mods.rhino.Node;

public class Jump extends AstNode {
   public Node target;
   private Node target2;
   private Jump jumpNode;

   public Jump() {
      this.type = -1;
   }

   public Jump(int nodeType) {
      this.type = nodeType;
   }

   public Jump(int type, Node child) {
      this(type);
      this.addChildToBack(child);
   }

   public Jump getJumpStatement() {
      if (this.type != 122 && this.type != 123) {
         codeBug();
      }

      return this.jumpNode;
   }

   public void setJumpStatement(Jump jumpStatement) {
      if (this.type != 122 && this.type != 123) {
         codeBug();
      }

      if (jumpStatement == null) {
         codeBug();
      }

      if (this.jumpNode != null) {
         codeBug();
      }

      this.jumpNode = jumpStatement;
   }

   public Node getDefault() {
      if (this.type != 116) {
         codeBug();
      }

      return this.target2;
   }

   public void setDefault(Node defaultTarget) {
      if (this.type != 116) {
         codeBug();
      }

      if (defaultTarget.getType() != 133) {
         codeBug();
      }

      if (this.target2 != null) {
         codeBug();
      }

      this.target2 = defaultTarget;
   }

   public Node getFinally() {
      if (this.type != 82) {
         codeBug();
      }

      return this.target2;
   }

   public void setFinally(Node finallyTarget) {
      if (this.type != 82) {
         codeBug();
      }

      if (finallyTarget.getType() != 133) {
         codeBug();
      }

      if (this.target2 != null) {
         codeBug();
      }

      this.target2 = finallyTarget;
   }

   public Jump getLoop() {
      if (this.type != 132) {
         codeBug();
      }

      return this.jumpNode;
   }

   public void setLoop(Jump loop) {
      if (this.type != 132) {
         codeBug();
      }

      if (loop == null) {
         codeBug();
      }

      if (this.jumpNode != null) {
         codeBug();
      }

      this.jumpNode = loop;
   }

   public Node getContinue() {
      if (this.type != 134) {
         codeBug();
      }

      return this.target2;
   }

   public void setContinue(Node continueTarget) {
      if (this.type != 134) {
         codeBug();
      }

      if (continueTarget.getType() != 133) {
         codeBug();
      }

      if (this.target2 != null) {
         codeBug();
      }

      this.target2 = continueTarget;
   }
}
