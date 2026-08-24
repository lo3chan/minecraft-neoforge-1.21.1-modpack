package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.ast.ArrayComprehension;
import dev.latvian.mods.rhino.ast.ArrayComprehensionLoop;
import dev.latvian.mods.rhino.ast.ArrayLiteral;
import dev.latvian.mods.rhino.ast.Assignment;
import dev.latvian.mods.rhino.ast.AstNode;
import dev.latvian.mods.rhino.ast.AstRoot;
import dev.latvian.mods.rhino.ast.AstSymbol;
import dev.latvian.mods.rhino.ast.Block;
import dev.latvian.mods.rhino.ast.CatchClause;
import dev.latvian.mods.rhino.ast.ComputedPropertyKey;
import dev.latvian.mods.rhino.ast.ConditionalExpression;
import dev.latvian.mods.rhino.ast.ContinueStatement;
import dev.latvian.mods.rhino.ast.DestructuringForm;
import dev.latvian.mods.rhino.ast.DoLoop;
import dev.latvian.mods.rhino.ast.ElementGet;
import dev.latvian.mods.rhino.ast.EmptyExpression;
import dev.latvian.mods.rhino.ast.ExpressionStatement;
import dev.latvian.mods.rhino.ast.ForInLoop;
import dev.latvian.mods.rhino.ast.ForLoop;
import dev.latvian.mods.rhino.ast.FunctionCall;
import dev.latvian.mods.rhino.ast.FunctionNode;
import dev.latvian.mods.rhino.ast.GeneratorExpression;
import dev.latvian.mods.rhino.ast.GeneratorExpressionLoop;
import dev.latvian.mods.rhino.ast.GeneratorMethodDefinition;
import dev.latvian.mods.rhino.ast.IfStatement;
import dev.latvian.mods.rhino.ast.InfixExpression;
import dev.latvian.mods.rhino.ast.Jump;
import dev.latvian.mods.rhino.ast.Label;
import dev.latvian.mods.rhino.ast.LabeledStatement;
import dev.latvian.mods.rhino.ast.LetNode;
import dev.latvian.mods.rhino.ast.Name;
import dev.latvian.mods.rhino.ast.NewExpression;
import dev.latvian.mods.rhino.ast.ObjectLiteral;
import dev.latvian.mods.rhino.ast.ObjectProperty;
import dev.latvian.mods.rhino.ast.ParenthesizedExpression;
import dev.latvian.mods.rhino.ast.PropertyGet;
import dev.latvian.mods.rhino.ast.RegExpLiteral;
import dev.latvian.mods.rhino.ast.ReturnStatement;
import dev.latvian.mods.rhino.ast.Scope;
import dev.latvian.mods.rhino.ast.ScriptNode;
import dev.latvian.mods.rhino.ast.StringLiteral;
import dev.latvian.mods.rhino.ast.SwitchCase;
import dev.latvian.mods.rhino.ast.SwitchStatement;
import dev.latvian.mods.rhino.ast.TaggedTemplateLiteral;
import dev.latvian.mods.rhino.ast.TemplateCharacters;
import dev.latvian.mods.rhino.ast.TemplateLiteral;
import dev.latvian.mods.rhino.ast.ThrowStatement;
import dev.latvian.mods.rhino.ast.TryStatement;
import dev.latvian.mods.rhino.ast.UnaryExpression;
import dev.latvian.mods.rhino.ast.UpdateExpression;
import dev.latvian.mods.rhino.ast.VariableDeclaration;
import dev.latvian.mods.rhino.ast.VariableInitializer;
import dev.latvian.mods.rhino.ast.WhileLoop;
import dev.latvian.mods.rhino.ast.WithStatement;
import dev.latvian.mods.rhino.ast.Yield;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class IRFactory extends Parser {
   private static final int LOOP_DO_WHILE = 0;
   private static final int LOOP_WHILE = 1;
   private static final int LOOP_FOR = 2;
   private static final int ALWAYS_TRUE_BOOLEAN = 1;
   private static final int ALWAYS_FALSE_BOOLEAN = -1;

   private static void addSwitchCase(Node switchBlock, Node caseExpression, Node statements) {
      if (switchBlock.getType() != 131) {
         throw Kit.codeBug();
      } else {
         Jump switchNode = (Jump)switchBlock.getFirstChild();
         if (switchNode.getType() != 116) {
            throw Kit.codeBug();
         } else {
            Node gotoTarget = Node.newTarget();
            if (caseExpression != null) {
               Jump caseNode = new Jump(117, caseExpression);
               caseNode.target = gotoTarget;
               switchNode.addChildToBack(caseNode);
            } else {
               switchNode.setDefault(gotoTarget);
            }

            switchBlock.addChildToBack(gotoTarget);
            switchBlock.addChildToBack(statements);
         }
      }
   }

   private static void closeSwitch(Node switchBlock) {
      if (switchBlock.getType() != 131) {
         throw Kit.codeBug();
      } else {
         Jump switchNode = (Jump)switchBlock.getFirstChild();
         if (switchNode.getType() != 116) {
            throw Kit.codeBug();
         } else {
            Node switchBreakTarget = Node.newTarget();
            switchNode.target = switchBreakTarget;
            Node defaultTarget = switchNode.getDefault();
            if (defaultTarget == null) {
               defaultTarget = switchBreakTarget;
            }

            switchBlock.addChildAfter(makeJump(5, defaultTarget), switchNode);
            switchBlock.addChildToBack(switchBreakTarget);
         }
      }
   }

   private static Node createExprStatementNoReturn(Node expr, int lineno, int column) {
      return new Node(135, expr, lineno, column);
   }

   private static Node createString(String string) {
      return Node.newString(string);
   }

   private static Node initFunction(FunctionNode fnNode, int functionIndex, Node statements, int functionType) {
      fnNode.setFunctionType(functionType);
      fnNode.addChildToBack(statements);
      int functionCount = fnNode.getFunctionCount();
      if (functionCount != 0) {
         fnNode.setRequiresActivation();
      }

      if (functionType == 2) {
         Name name = fnNode.getFunctionName();
         if (name != null && name.length() != 0 && fnNode.getSymbol(name.getIdentifier()) == null) {
            fnNode.putSymbol(new AstSymbol(111, name.getIdentifier()));
            Node setFn = new Node(135, new Node(8, Node.newString(49, name.getIdentifier()), new Node(64)));
            statements.addChildrenToFront(setFn);
         }
      }

      Node lastStmt = statements.getLastChild();
      if (lastStmt == null || lastStmt.getType() != 4) {
         statements.addChildToBack(new Node(4));
      }

      Node result = Node.newString(111, fnNode.getName());
      result.putIntProp(1, functionIndex);
      return result;
   }

   private static Node createFor(Scope loop, Node init, Node test, Node incr, Node body) {
      if (init.getType() == 155) {
         Scope let = Scope.splitScope(loop);
         let.setType(155);
         let.addChildrenToBack(init);
         let.addChildToBack(createLoop(loop, 2, body, test, new Node(130), incr));
         return let;
      } else {
         return createLoop(loop, 2, body, test, init, incr);
      }
   }

   private static Node createLoop(Jump loop, int loopType, Node body, Node cond, Node init, Node incr) {
      Node bodyTarget = Node.newTarget();
      Node condTarget = Node.newTarget();
      if (loopType == 2 && cond.getType() == 130) {
         cond = new Node(45);
      }

      Jump IFEQ = new Jump(6, cond);
      IFEQ.target = bodyTarget;
      Node breakTarget = Node.newTarget();
      loop.addChildToBack(bodyTarget);
      loop.addChildrenToBack(body);
      if (loopType == 1 || loopType == 2) {
         loop.addChildrenToBack(new Node(130, loop.getLineno(), loop.getColumn()));
      }

      loop.addChildToBack(condTarget);
      loop.addChildToBack(IFEQ);
      loop.addChildToBack(breakTarget);
      loop.target = breakTarget;
      Node continueTarget = condTarget;
      if (loopType == 1 || loopType == 2) {
         loop.addChildToFront(makeJump(5, condTarget));
         if (loopType == 2) {
            int initType = init.getType();
            if (initType != 130) {
               if (initType != 124 && initType != 155) {
                  init = new Node(135, init);
               }

               loop.addChildToFront(init);
            }

            Node incrTarget = Node.newTarget();
            loop.addChildAfter(incrTarget, body);
            if (incr.getType() != 130) {
               incr = new Node(135, incr);
               loop.addChildAfter(incr, incrTarget);
            }

            continueTarget = incrTarget;
         }
      }

      loop.setContinue(continueTarget);
      return loop;
   }

   private static Node createIf(Node cond, Node ifTrue, Node ifFalse, int lineno, int column) {
      int condStatus = isAlwaysDefinedBoolean(cond);
      if (condStatus == 1) {
         return ifTrue;
      } else if (condStatus == -1) {
         return ifFalse != null ? ifFalse : new Node(131, lineno, column);
      } else {
         Node result = new Node(131, lineno, column);
         Node ifNotTarget = Node.newTarget();
         Jump IFNE = new Jump(7, cond);
         IFNE.target = ifNotTarget;
         result.addChildToBack(IFNE);
         result.addChildrenToBack(ifTrue);
         if (ifFalse != null) {
            Node endTarget = Node.newTarget();
            result.addChildToBack(makeJump(5, endTarget));
            result.addChildToBack(ifNotTarget);
            result.addChildrenToBack(ifFalse);
            result.addChildToBack(endTarget);
         } else {
            result.addChildToBack(ifNotTarget);
         }

         if (cond.getFirstChild() != null) {
            Node conditionalChild = cond.getFirstChild();
            result.setLineColumnNumber(conditionalChild.getLineno(), conditionalChild.getColumn());
         }

         return result;
      }
   }

   private static Node createCondExpr(Node cond, Node ifTrue, Node ifFalse) {
      int condStatus = isAlwaysDefinedBoolean(cond);
      if (condStatus == 1) {
         return ifTrue;
      } else {
         return condStatus == -1 ? ifFalse : new Node(104, cond, ifTrue, ifFalse);
      }
   }

   private static Node createUnary(int nodeType, Node child) {
      int childType = child.getType();
      switch (nodeType) {
         case 26:
            int status = isAlwaysDefinedBoolean(child);
            if (status != 0) {
               int type;
               if (status == 1) {
                  type = 44;
               } else {
                  type = 45;
               }

               if (childType != 45 && childType != 44) {
                  return new Node(type);
               }

               child.setType(type);
               return child;
            }
            break;
         case 27:
            if (childType == 40) {
               int value = ScriptRuntime.toInt32(child.getDouble());
               child.setDouble(~value);
               return child;
            }
         case 28:
         case 30:
         default:
            break;
         case 29:
            if (childType == 40) {
               child.setDouble(-child.getDouble());
               return child;
            }
            break;
         case 31:
            Node n;
            if (childType == 39) {
               child.setType(49);
               Node right = Node.newString(child.getString());
               n = new Node(nodeType, child, right);
            } else if (childType == 33 || childType == 36) {
               Node left = child.getFirstChild();
               Node right = child.getLastChild();
               child.removeChild(left);
               child.removeChild(right);
               n = new Node(nodeType, left, right);
            } else if (childType == 68) {
               Node ref = child.getFirstChild();
               child.removeChild(ref);
               n = new Node(70, ref);
            } else {
               n = new Node(nodeType, new Node(45), child);
            }

            return n;
         case 32:
            if (childType == 39) {
               child.setType(139);
               return child;
            }
      }

      return new Node(nodeType, child);
   }

   private static Node createIncDec(int nodeType, boolean post, Node child) {
      child = makeReference(child);
      int childType = child.getType();
      switch (childType) {
         case 33:
         case 36:
         case 39:
         case 68:
            Node n = new Node(nodeType, child);
            int incrDecrMask = 0;
            if (nodeType == 109) {
               incrDecrMask |= 1;
            }

            if (post) {
               incrDecrMask |= 2;
            }

            n.putIntProp(13, incrDecrMask);
            return n;
         default:
            throw Kit.codeBug();
      }
   }

   private static Node createBinary(int nodeType, Node left, Node right, Context cx) {
      String s2;
      label82: {
         switch (nodeType) {
            case 21:
               if (left.type == 41) {
                  if (right.type == 41) {
                     s2 = right.getString();
                     break label82;
                  }

                  if (right.type == 40) {
                     s2 = ScriptRuntime.numberToString(cx, right.getDouble(), 10);
                     break label82;
                  }
               } else if (left.type == 40) {
                  if (right.type == 40) {
                     left.setDouble(left.getDouble() + right.getDouble());
                     return left;
                  }

                  if (right.type == 41) {
                     s2 = ScriptRuntime.numberToString(cx, left.getDouble(), 10);
                     String s2x = right.getString();
                     right.setString(s2.concat(s2x));
                     return right;
                  }
               }
               break;
            case 22:
               if (left.type == 40) {
                  double ldx = left.getDouble();
                  if (right.type == 40) {
                     left.setDouble(ldx - right.getDouble());
                     return left;
                  }

                  if (ldx == 0.0) {
                     return new Node(29, right);
                  }
               } else if (right.type == 40 && right.getDouble() == 0.0) {
                  return new Node(28, left);
               }
               break;
            case 23:
               if (left.type == 40) {
                  double ld = left.getDouble();
                  if (right.type == 40) {
                     left.setDouble(ld * right.getDouble());
                     return left;
                  }

                  if (ld == 1.0) {
                     return new Node(28, right);
                  }
               } else if (right.type == 40 && right.getDouble() == 1.0) {
                  return new Node(28, left);
               }
               break;
            case 24:
               if (right.type == 40) {
                  double rd = right.getDouble();
                  if (left.type == 40) {
                     left.setDouble(left.getDouble() / rd);
                     return left;
                  }

                  if (rd == 1.0) {
                     return new Node(28, left);
                  }
               }
               break;
            case 106:
               int leftStatus = isAlwaysDefinedBoolean(left);
               if (leftStatus == 1) {
                  return left;
               }

               if (leftStatus == -1) {
                  return right;
               }
               break;
            case 107:
               int leftStatusx = isAlwaysDefinedBoolean(left);
               if (leftStatusx == -1) {
                  return left;
               }

               if (leftStatusx == 1) {
                  return right;
               }
         }

         return new Node(nodeType, left, right);
      }

      String s1 = left.getString();
      left.setString(s1.concat(s2));
      return left;
   }

   private static Node createUseLocal(Node localBlock) {
      if (143 != localBlock.getType()) {
         throw Kit.codeBug();
      } else {
         Node result = new Node(54);
         result.putProp(3, localBlock);
         return result;
      }
   }

   private static Jump makeJump(int type, Node target) {
      Jump n = new Jump(type);
      n.target = target;
      return n;
   }

   private static Node makeReference(Node node) {
      int type = node.getType();
      switch (type) {
         case 33:
         case 36:
         case 39:
         case 68:
            return node;
         case 38:
            node.setType(71);
            return new Node(68, node);
         default:
            return null;
      }
   }

   private static int isAlwaysDefinedBoolean(Node node) {
      switch (node.getType()) {
         case 40:
            double num = node.getDouble();
            if (!Double.isNaN(num) && num != 0.0) {
               return 1;
            }

            return -1;
         case 41:
         case 43:
         default:
            return 0;
         case 42:
         case 44:
            return -1;
         case 45:
            return 1;
      }
   }

   public IRFactory(Context cx) {
      super(cx);
   }

   public IRFactory(Context cx, CompilerEnvirons env) {
      this(cx, env, env.getErrorReporter());
   }

   public IRFactory(Context cx, CompilerEnvirons env, ErrorReporter errorReporter) {
      super(cx, env, errorReporter);
   }

   public ScriptNode transformTree(AstRoot root) {
      this.currentScriptOrFn = root;
      this.inUseStrictDirective = root.isInStrictMode();
      return (ScriptNode)this.transform(root);
   }

   public Node transform(AstNode node) {
      return (Node)(switch (node.getType()) {
         case 4 -> this.transformReturn((ReturnStatement)node);
         default -> {
            if (node instanceof ExpressionStatement n) {
               yield this.transformExprStmt(n);
            } else if (node instanceof Assignment n) {
               yield this.transformAssignment(n);
            } else if (node instanceof UnaryExpression n) {
               yield this.transformUnary(n);
            } else if (node instanceof UpdateExpression n) {
               yield this.transformUpdate(n);
            } else if (node instanceof InfixExpression n) {
               yield this.transformInfix(n);
            } else if (node instanceof VariableDeclaration n) {
               yield this.transformVariables(n);
            } else if (node instanceof ParenthesizedExpression n) {
               yield this.transformParenExpr(n);
            } else if (node instanceof ComputedPropertyKey n) {
               yield this.transformComputedPropertyKey(n);
            } else if (node instanceof LabeledStatement n) {
               yield this.transformLabeledStatement(n);
            } else if (node instanceof LetNode n) {
               yield this.transformLetNode(n);
            } else {
               if (!(node instanceof GeneratorMethodDefinition n)) {
                  throw new IllegalArgumentException("Can't transform: " + node + " (" + node.getClass().getName() + ")");
               }

               yield this.transformGeneratorMethodDefinition(n);
            }
         }
         case 30 -> this.transformNewExpr((NewExpression)node);
         case 33 -> this.transformPropertyGet((PropertyGet)node);
         case 36 -> this.transformElementGet((ElementGet)node);
         case 38 -> this.transformFunctionCall((FunctionCall)node);
         case 39, 40, 42, 43, 44, 45, 122, 130, 163 -> node;
         case 41 -> this.transformString((StringLiteral)node);
         case 48 -> this.transformRegExp((RegExpLiteral)node);
         case 50 -> this.transformThrow((ThrowStatement)node);
         case 66 -> this.transformArrayLiteral((ArrayLiteral)node);
         case 67 -> this.transformObjectLiteral((ObjectLiteral)node);
         case 73, 167 -> this.transformYield((Yield)node);
         case 77 -> node instanceof ElementGet ? this.transformElementGet((ElementGet)node) : this.transformPropertyGet((PropertyGet)node);
         case 82 -> this.transformTry((TryStatement)node);
         case 104 -> this.transformCondExpr((ConditionalExpression)node);
         case 111 -> this.transformFunction((FunctionNode)node);
         case 114 -> this.transformIf((IfStatement)node);
         case 116 -> this.transformSwitch((SwitchStatement)node);
         case 119 -> this.transformWhileLoop((WhileLoop)node);
         case 120 -> this.transformDoLoop((DoLoop)node);
         case 121 -> node instanceof ForInLoop ? this.transformForInLoop((ForInLoop)node) : this.transformForLoop((ForLoop)node);
         case 123 -> this.transformContinue((ContinueStatement)node);
         case 125 -> this.transformWith((WithStatement)node);
         case 131 -> this.transformBlock(node);
         case 138 -> this.transformScript((ScriptNode)node);
         case 159 -> this.transformArrayComp((ArrayComprehension)node);
         case 164 -> this.transformGenExpr((GeneratorExpression)node);
         case 168 -> this.transformTemplateLiteral((TemplateLiteral)node);
         case 171 -> this.transformTemplateLiteralCall((TaggedTemplateLiteral)node);
      });
   }

   private Node transformArrayComp(ArrayComprehension node) {
      int lineno = node.getLineno();
      Scope scopeNode = this.createScopeNode(159, lineno, 0);
      String arrayName = this.currentScriptOrFn.getNextTempName();
      this.pushScope(scopeNode);

      Scope var8;
      try {
         this.defineSymbol(155, arrayName);
         Node block = new Node(131, lineno, 0);
         Node newArray = this.createCallOrNew(30, this.createName("Array"));
         Node init = new Node(135, this.createAssignment(91, this.createName(arrayName), newArray), lineno, 0);
         block.addChildToBack(init);
         block.addChildToBack(this.arrayCompTransformHelper(node, arrayName));
         scopeNode.addChildToBack(block);
         scopeNode.addChildToBack(this.createName(arrayName));
         var8 = scopeNode;
      } finally {
         this.popScope();
      }

      return var8;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private Node arrayCompTransformHelper(ArrayComprehension node, String arrayName) {
      int lineno = node.getLineno();
      int column = node.getColumn();
      Node expr = this.transform(node.getResult());
      List<ArrayComprehensionLoop> loops = node.getLoops();
      int numLoops = loops.size();
      Node[] iterators = new Node[numLoops];
      Node[] iteratedObjs = new Node[numLoops];

      for (int i = 0; i < numLoops; i++) {
         ArrayComprehensionLoop acl = loops.get(i);
         AstNode iter = acl.getIterator();
         String name;
         if (iter.getType() == 39) {
            name = iter.getString();
         } else {
            name = this.currentScriptOrFn.getNextTempName();
            this.defineSymbol(88, name);
            expr = createBinary(90, this.createAssignment(91, iter, this.createName(name)), expr, this.cx);
         }

         Node init = this.createName(name);
         this.defineSymbol(155, name);
         iterators[i] = init;
         iteratedObjs[i] = this.transform(acl.getIteratedObject());
      }

      Node call = this.createCallOrNew(38, this.createPropertyGet(this.createName(arrayName), null, "push", 0, 33));
      Node body = new Node(135, call, lineno, 0);
      if (node.getFilter() != null) {
         body = createIf(this.transform(node.getFilter()), body, null, lineno, 0);
      }

      int pushed = 0;
      boolean var19 = false /* VF: Semaphore variable */;

      try {
         var19 = true;

         for (int var24 = numLoops - 1; var24 >= 0; var24--) {
            ArrayComprehensionLoop acl = loops.get(var24);
            Scope loop = this.createLoopNode(null, acl.getLineno());
            this.pushScope(loop);
            pushed++;
            body = this.createForIn(155, loop, iterators[var24], iteratedObjs[var24], body, acl.isForEach(), acl.isForOf());
         }

         var19 = false;
      } finally {
         if (var19) {
            for (int i = 0; i < pushed; i++) {
               this.popScope();
            }
         }
      }

      for (int i = 0; i < pushed; i++) {
         this.popScope();
      }

      call.addChildToBack(expr);
      return body;
   }

   private Node transformArrayLiteral(ArrayLiteral node) {
      if (node.isDestructuring()) {
         return node;
      } else {
         List<AstNode> elems = node.getElements();
         Node array = new Node(66);
         List<Integer> skipIndexes = null;

         for (int i = 0; i < elems.size(); i++) {
            AstNode elem = elems.get(i);
            if (elem.getType() != 130) {
               array.addChildToBack(this.transform(elem));
            } else {
               if (skipIndexes == null) {
                  skipIndexes = new ArrayList<>();
               }

               skipIndexes.add(i);
            }
         }

         array.putIntProp(21, node.getDestructuringLength());
         if (skipIndexes != null) {
            int[] skips = new int[skipIndexes.size()];

            for (int ix = 0; ix < skipIndexes.size(); ix++) {
               skips[ix] = skipIndexes.get(ix);
            }

            array.putProp(11, skips);
         }

         return array;
      }
   }

   private Node transformAssignment(Assignment node) {
      AstNode originalLeft = node.getLeft();
      AstNode left = this.removeParens(originalLeft);
      boolean shouldTryToInferName = originalLeft == left;
      Node target;
      if (this.isDestructuring(left)) {
         target = left;
         shouldTryToInferName = false;
      } else {
         target = this.transform(left);
      }

      Node transformedRight = this.transform(node.getRight());
      if (shouldTryToInferName) {
         this.inferNameIfMissing(node.getLeft(), transformedRight, null);
      }

      return this.createAssignment(node.getType(), target, transformedRight);
   }

   private Node transformBlock(AstNode node) {
      if (node instanceof Scope) {
         this.pushScope((Scope)node);
      }

      AstNode var9;
      try {
         List<Node> kids = new ArrayList<>();

         for (Node kid : node) {
            kids.add(this.transform((AstNode)kid));
         }

         node.removeChildren();

         for (Node kid : kids) {
            node.addChildToBack(kid);
         }

         var9 = node;
      } finally {
         if (node instanceof Scope) {
            this.popScope();
         }
      }

      return var9;
   }

   private Node transformCondExpr(ConditionalExpression node) {
      Node test = this.transform(node.getTestExpression());
      Node ifTrue = this.transform(node.getTrueExpression());
      Node ifFalse = this.transform(node.getFalseExpression());
      return createCondExpr(test, ifTrue, ifFalse);
   }

   private Node transformContinue(ContinueStatement node) {
      return node;
   }

   private Node transformDoLoop(DoLoop loop) {
      loop.setType(134);
      this.pushScope(loop);

      Node var4;
      try {
         Node body = this.transform(loop.getBody());
         Node cond = this.transform(loop.getCondition());
         var4 = createLoop(loop, 0, body, cond, null, null);
      } finally {
         this.popScope();
      }

      return var4;
   }

   private Node transformElementGet(ElementGet node) {
      Node target = this.transform(node.getTarget());
      Node element = this.transform(node.getElement());
      Node getElem = new Node(36, target, element);
      if (node.getType() == 77) {
         getElem.putIntProp(31, 1);
      }

      return getElem;
   }

   private Node transformExprStmt(ExpressionStatement node) {
      Node expr = this.transform(node.getExpression());
      return new Node(node.getType(), expr, node.getLineno(), node.getColumn());
   }

   private Node transformForInLoop(ForInLoop loop) {
      loop.setType(134);
      this.pushScope(loop);

      Node var7;
      try {
         int declType = -1;
         AstNode iter = loop.getIterator();
         if (iter instanceof VariableDeclaration) {
            declType = iter.getType();
         }

         Node lhs = this.transform(iter);
         Node obj = this.transform(loop.getIteratedObject());
         Node body = this.transform(loop.getBody());
         var7 = this.createForIn(declType, loop, lhs, obj, body, loop.isForEach(), loop.isForOf());
      } finally {
         this.popScope();
      }

      return var7;
   }

   private Node transformForLoop(ForLoop loop) {
      loop.setType(134);
      Scope savedScope = this.currentScope;
      this.currentScope = loop;

      Node var7;
      try {
         Node init = this.transform(loop.getInitializer());
         Node test = this.transform(loop.getCondition());
         Node incr = this.transform(loop.getIncrement());
         Node body = this.transform(loop.getBody());
         var7 = createFor(loop, init, test, incr, body);
      } finally {
         this.currentScope = savedScope;
      }

      return var7;
   }

   private Node transformFunction(FunctionNode fn) {
      int index = this.currentScriptOrFn.addFunction(fn);
      Parser.PerFunctionVariables savedVars = new Parser.PerFunctionVariables(fn);

      Node a;
      try {
         int lineno = fn.getBody().getLineno();
         this.nestingOfFunction++;
         Node body = this.transform(fn.getBody());
         List<Object> defaultParams = fn.getDefaultParams();
         int bodyLineno = body.getLineno();
         List<Node> prologue = new ArrayList<>();

         for (AstNode param : fn.getParams()) {
            if (param instanceof Name name) {
               String paramName = name.getIdentifier();
               if (defaultParams != null) {
                  for (int i = 0; i < defaultParams.size(); i += 2) {
                     if (defaultParams.get(i).equals(paramName) && defaultParams.get(i + 1) instanceof AstNode rhs) {
                        Node cond = new Node(46, this.createName(paramName), this.createName("undefined"));
                        Node assignNode = new Node(135, this.createAssignment(91, this.createName(paramName), this.transform(rhs)), bodyLineno, 0);
                        prologue.add(createIf(cond, assignNode, null, bodyLineno, 0));
                        break;
                     }
                  }
               }
            } else if (param.getProp(23) instanceof Node assign) {
               prologue.add(new Node(135, assign, lineno, 0));
            }
         }

         for (int ix = prologue.size() - 1; ix >= 0; ix--) {
            body.addChildToFront(prologue.get(ix));
         }

         List<Node[]> dfns = fn.getDestructuringRvalues();
         if (dfns != null) {
            for (Node[] ix : dfns) {
               a = ix[0];
               if (ix[1] instanceof AstNode b) {
                  a.replaceChild(b, this.transform(b));
               }
            }
         }

         int syntheticType = fn.getFunctionType();
         Node pn = initFunction(fn, index, body, syntheticType);
         a = pn;
      } finally {
         this.nestingOfFunction--;
         savedVars.restore();
      }

      return a;
   }

   private Node transformFunctionCall(FunctionCall node) {
      Node call = this.createCallOrNew(38, this.transform(node.getTarget()));
      call.setLineColumnNumber(node.getLineno(), node.getColumn());

      for (AstNode arg : node.getArguments()) {
         call.addChildToBack(this.transform(arg));
      }

      if (node.isOptionalCall()) {
         call.putIntProp(31, 1);
      }

      return call;
   }

   private Node transformGenExpr(GeneratorExpression node) {
      FunctionNode fn = new FunctionNode();
      fn.setSourceName(this.currentScriptOrFn.getNextTempName());
      fn.setIsGenerator();
      fn.setFunctionType(2);
      fn.setRequiresActivation();
      int index = this.currentScriptOrFn.addFunction(fn);
      Parser.PerFunctionVariables savedVars = new Parser.PerFunctionVariables(fn);

      Node pn;
      try {
         this.nestingOfFunction++;
         Node body = this.genExprTransformHelper(node);
         int syntheticType = fn.getFunctionType();
         pn = initFunction(fn, index, body, syntheticType);
      } finally {
         this.nestingOfFunction--;
         savedVars.restore();
      }

      Node call = this.createCallOrNew(38, pn);
      call.setLineColumnNumber(node.getLineno(), node.getColumn());
      return call;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private Node genExprTransformHelper(GeneratorExpression node) {
      int lineno = node.getLineno();
      int column = node.getColumn();
      Node expr = this.transform(node.getResult());
      List<GeneratorExpressionLoop> loops = node.getLoops();
      int numLoops = loops.size();
      Node[] iterators = new Node[numLoops];
      Node[] iteratedObjs = new Node[numLoops];

      for (int i = 0; i < numLoops; i++) {
         GeneratorExpressionLoop acl = loops.get(i);
         AstNode iter = acl.getIterator();
         String name;
         if (iter.getType() == 39) {
            name = iter.getString();
         } else {
            name = this.currentScriptOrFn.getNextTempName();
            this.defineSymbol(88, name);
            expr = createBinary(90, this.createAssignment(91, iter, this.createName(name)), expr, this.cx);
         }

         Node init = this.createName(name);
         this.defineSymbol(155, name);
         iterators[i] = init;
         iteratedObjs[i] = this.transform(acl.getIteratedObject());
      }

      Node yield = new Node(73, expr, node.getLineno(), node.getColumn());
      Node body = new Node(135, yield, lineno, column);
      if (node.getFilter() != null) {
         body = createIf(this.transform(node.getFilter()), body, null, lineno, column);
      }

      int pushed = 0;
      boolean var18 = false /* VF: Semaphore variable */;

      try {
         var18 = true;

         for (int var23 = numLoops - 1; var23 >= 0; var23--) {
            GeneratorExpressionLoop acl = loops.get(var23);
            Scope loop = this.createLoopNode(null, acl.getLineno());
            this.pushScope(loop);
            pushed++;
            body = this.createForIn(155, loop, iterators[var23], iteratedObjs[var23], body, acl.isForEach(), acl.isForOf());
         }

         var18 = false;
      } finally {
         if (var18) {
            for (int i = 0; i < pushed; i++) {
               this.popScope();
            }
         }
      }

      for (int i = 0; i < pushed; i++) {
         this.popScope();
      }

      return body;
   }

   private Node transformIf(IfStatement n) {
      Node cond = this.transform(n.getCondition());
      Node ifTrue = this.transform(n.getThenPart());
      Node ifFalse = null;
      if (n.getElsePart() != null) {
         ifFalse = this.transform(n.getElsePart());
      }

      return createIf(cond, ifTrue, ifFalse, n.getLineno(), 0);
   }

   private Node transformInfix(InfixExpression node) {
      Node left = this.transform(node.getLeft());
      Node right = this.transform(node.getRight());
      return createBinary(node.getType(), left, right, this.cx);
   }

   private Node transformLabeledStatement(LabeledStatement ls) {
      Label label = ls.getFirstLabel();
      Node statement = this.transform(ls.getStatement());
      Node breakTarget = Node.newTarget();
      Node block = new Node(131, label, statement, breakTarget);
      label.target = breakTarget;
      return block;
   }

   private Node transformLetNode(LetNode node) {
      this.pushScope(node);

      LetNode var3;
      try {
         Node vars = this.transformVariableInitializers(node.getVariables());
         node.addChildToBack(vars);
         if (node.getBody() != null) {
            node.addChildToBack(this.transform(node.getBody()));
         }

         var3 = node;
      } finally {
         this.popScope();
      }

      return var3;
   }

   private Node transformNewExpr(NewExpression node) {
      Node nx = this.createCallOrNew(30, this.transform(node.getTarget()));
      nx.setLineColumnNumber(node.getLineno(), node.getColumn());

      for (AstNode arg : node.getArguments()) {
         nx.addChildToBack(this.transform(arg));
      }

      if (node.getInitializer() != null) {
         nx.addChildToBack(this.transformObjectLiteral(node.getInitializer()));
      }

      return nx;
   }

   private Node transformObjectLiteral(ObjectLiteral node) {
      if (node.isDestructuring()) {
         return node;
      } else {
         List<ObjectProperty> elems = node.getElements();
         Node object = new Node(67);
         Object[] properties;
         if (elems.isEmpty()) {
            properties = ScriptRuntime.EMPTY_OBJECTS;
         } else {
            int size = elems.size();
            int i = 0;
            properties = new Object[size];

            for (ObjectProperty prop : elems) {
               Object propKey = getPropKey(prop.getLeft());
               if (propKey == null) {
                  Node theId = this.transform(prop.getLeft());
                  properties[i] = theId;
               } else {
                  properties[i] = propKey;
               }

               Name inferrableName = null;
               if (propKey instanceof String || propKey instanceof Integer) {
                  inferrableName = new Name(0, Objects.toString(propKey));
                  inferrableName.setLineColumnNumber(prop.getLeft().getLineno(), 0);
               }

               Node right = this.transform(prop.getRight());
               if (inferrableName != null) {
                  this.inferNameIfMissing(inferrableName, right, prop.isGetterMethod() ? "get " : (prop.isSetterMethod() ? "set " : null));
               }

               if (prop.isGetterMethod()) {
                  right = createUnary(153, right);
               } else if (prop.isSetterMethod()) {
                  right = createUnary(154, right);
               } else if (prop.isNormalMethod()) {
                  right = createUnary(165, right);
               }

               object.addChildToBack(right);
               i++;
            }
         }

         object.putProp(12, properties);
         return object;
      }
   }

   private Node transformParenExpr(ParenthesizedExpression node) {
      AstNode expr = node.getExpression();

      while (expr instanceof ParenthesizedExpression) {
         expr = ((ParenthesizedExpression)expr).getExpression();
      }

      Node result = this.transform(expr);
      result.putProp(19, Boolean.TRUE);
      return result;
   }

   private Node transformComputedPropertyKey(ComputedPropertyKey node) {
      Node transformedExpression = this.transform(node.getExpression());
      return new Node(node.type, transformedExpression);
   }

   private Node transformGeneratorMethodDefinition(GeneratorMethodDefinition node) {
      return this.transform(node.getMethodName());
   }

   private Node transformPropertyGet(PropertyGet node) {
      Node target = this.transform(node.getTarget());
      String name = node.getProperty().getIdentifier();
      return this.createPropertyGet(target, null, name, 0, node.getType());
   }

   private Node transformTemplateLiteral(TemplateLiteral node) {
      List<AstNode> elems = node.getElements();
      Node pn = Node.newString("");

      for (AstNode elem : elems) {
         if (elem.getType() != 169) {
            pn = createBinary(21, pn, this.transform(elem), this.cx);
         } else {
            TemplateCharacters chars = (TemplateCharacters)elem;
            String value = chars.getValue();
            if (value.length() > 0) {
               pn = createBinary(21, pn, Node.newString(value), this.cx);
            }
         }
      }

      return pn;
   }

   private Node transformTemplateLiteralCall(TaggedTemplateLiteral node) {
      Node call = this.createCallOrNew(38, this.transform(node.getTarget()));
      call.setLineColumnNumber(node.getLineno(), node.getColumn());
      TemplateLiteral templateLiteral = (TemplateLiteral)node.getTemplateLiteral();
      List<AstNode> elems = templateLiteral.getElements();
      call.addChildToBack(templateLiteral);

      for (AstNode elem : elems) {
         if (elem.getType() != 169) {
            call.addChildToBack(this.transform(elem));
         }
      }

      this.currentScriptOrFn.addTemplateLiteral(templateLiteral);
      return call;
   }

   private Node transformRegExp(RegExpLiteral node) {
      this.currentScriptOrFn.addRegExp(node);
      return node;
   }

   private Node transformReturn(ReturnStatement node) {
      AstNode rv = node.getReturnValue();
      Node value = rv == null ? null : this.transform(rv);
      return rv == null ? new Node(4, node.getLineno(), 0) : new Node(4, value, node.getLineno(), 0);
   }

   private Node transformScript(ScriptNode node) {
      if (this.currentScope != null) {
         Kit.codeBug();
      }

      this.currentScope = node;
      Node body = new Node(131);

      for (Node kid : node) {
         body.addChildToBack(this.transform((AstNode)kid));
      }

      node.removeChildren();
      Node children = body.getFirstChild();
      if (children != null) {
         node.addChildrenToBack(children);
      }

      return node;
   }

   private Node transformString(StringLiteral node) {
      Node stringNode = Node.newString(node.getValue());
      stringNode.setLineColumnNumber(node.getLineno(), node.getColumn());
      return stringNode;
   }

   private Node transformSwitch(SwitchStatement node) {
      Node switchExpr = this.transform(node.getExpression());
      node.addChildToBack(switchExpr);
      Node block = new Node(131, node, node.getLineno(), node.getColumn());

      for (SwitchCase sc : node.getCases()) {
         AstNode expr = sc.getExpression();
         Node caseExpr = null;
         if (expr != null) {
            caseExpr = this.transform(expr);
         }

         List<AstNode> stmts = sc.getStatements();
         Node body = new Block();
         if (stmts != null) {
            for (AstNode kid : stmts) {
               body.addChildToBack(this.transform(kid));
            }
         }

         addSwitchCase(block, caseExpr, body);
      }

      closeSwitch(block);
      return block;
   }

   private Node transformThrow(ThrowStatement node) {
      Node value = this.transform(node.getExpression());
      value.setLineColumnNumber(node.getLineno(), node.getColumn());
      Node nx = new Node(50, value);
      nx.setLineColumnNumber(node.getLineno(), node.getColumn());
      return nx;
   }

   private Node transformTry(TryStatement node) {
      Node tryBlock = this.transform(node.getTryBlock());
      Node catchBlocks = new Block();

      for (CatchClause cc : node.getCatchClauses()) {
         AstNode varName = cc.getVarName();
         Block catchBody = cc.getBody();
         String varNameStr;
         Node catchCond;
         if (varName == null) {
            varNameStr = this.currentScriptOrFn.getNextTempName();
            catchCond = new EmptyExpression();
         } else if (varName instanceof Name) {
            varNameStr = ((Name)varName).getIdentifier();
            AstNode ccc = cc.getCatchCondition();
            if (ccc != null) {
               catchCond = this.transform(ccc);
            } else {
               catchCond = new EmptyExpression();
            }
         } else {
            if (!(varName instanceof ArrayLiteral) && !(varName instanceof ObjectLiteral)) {
               throw new IllegalArgumentException("Unexpected catch parameter type: " + varName.getClass().getName());
            }

            String tempVarName = this.currentScriptOrFn.getNextTempName();
            varNameStr = tempVarName;
            VariableDeclaration letStatement = new VariableDeclaration();
            letStatement.setType(155);
            VariableInitializer letVar = new VariableInitializer();
            letStatement.addVariable(letVar);
            letVar.setTarget(varName);
            Name tempVarNameNode = new Name();
            tempVarNameNode.setIdentifier(tempVarName);
            letVar.setInitializer(tempVarNameNode);
            catchBody.addChildToFront(letStatement);
            catchCond = new EmptyExpression();
         }

         Node body = this.transform(catchBody);
         catchBlocks.addChildToBack(this.createCatch(varNameStr, catchCond, body, cc.getLineno()));
      }

      Node finallyBlock = null;
      if (node.getFinallyBlock() != null) {
         finallyBlock = this.transform(node.getFinallyBlock());
      }

      return this.createTryCatchFinally(tryBlock, catchBlocks, finallyBlock, node.getLineno(), node.getColumn());
   }

   private Node transformUnary(UnaryExpression node) {
      int type = node.getType();
      Node child = this.transform(node.getOperand());
      return createUnary(type, child);
   }

   private Node transformUpdate(UpdateExpression node) {
      int type = node.getType();
      Node child = this.transform(node.getOperand());
      return createIncDec(type, node.isPostfix(), child);
   }

   private Node transformVariables(VariableDeclaration node) {
      this.transformVariableInitializers(node);
      AstNode parent = node.getParent();
      return node;
   }

   private Node transformVariableInitializers(VariableDeclaration node) {
      for (VariableInitializer var : node.getVariables()) {
         AstNode target = var.getTarget();
         AstNode init = var.getInitializer();
         Node left;
         if (var.isDestructuring()) {
            left = target;
         } else {
            left = this.transform(target);
         }

         Node right = null;
         if (init != null) {
            right = this.transform(init);
         }

         if (var.isDestructuring()) {
            if (right == null) {
               node.addChildToBack(left);
            } else {
               Node d = this.createDestructuringAssignment(node.getType(), left, right, this::transform);
               node.addChildToBack(d);
            }
         } else {
            this.inferNameIfMissing(var.getTarget(), right, null);
            if (right != null) {
               left.addChildToBack(right);
            }

            node.addChildToBack(left);
         }
      }

      return node;
   }

   private Node transformWhileLoop(WhileLoop loop) {
      loop.setType(134);
      this.pushScope(loop);

      Node var4;
      try {
         Node cond = this.transform(loop.getCondition());
         Node body = this.transform(loop.getBody());
         var4 = createLoop(loop, 1, body, cond, null, null);
      } finally {
         this.popScope();
      }

      return var4;
   }

   private Node transformWith(WithStatement node) {
      Node expr = this.transform(node.getExpression());
      Node stmt = this.transform(node.getStatement());
      return this.createWith(expr, stmt, node.getLineno());
   }

   private Node transformYield(Yield node) {
      Node kid = node.getValue() == null ? null : this.transform(node.getValue());
      return kid != null ? new Node(node.getType(), kid, node.getLineno(), 0) : new Node(node.getType(), node.getLineno(), 0);
   }

   private Node createCatch(String varName, Node catchCond, Node stmts, int lineno) {
      if (catchCond == null) {
         catchCond = new Node(130);
      }

      return new Node(126, this.createName(varName), catchCond, stmts, lineno, 0);
   }

   private Scope createLoopNode(Node loopLabel, int lineno) {
      Scope result = this.createScopeNode(134, lineno, 0);
      if (loopLabel != null) {
         ((Jump)loopLabel).setLoop(result);
      }

      return result;
   }

   private Node createForIn(int declType, Node loop, Node lhs, Node obj, Node body, boolean isForEach, boolean isForOf) {
      int destructuring = -1;
      int destructuringLen = 0;
      int type = lhs.getType();
      Node lvalue;
      if (type == 124 || type == 155) {
         Node kid = lhs.getLastChild();
         int kidType = kid.getType();
         if (kidType != 66 && kidType != 67) {
            if (kidType != 39) {
               this.reportError("msg.bad.for.in.lhs");
               return null;
            }

            lvalue = Node.newString(39, kid.getString());
         } else {
            destructuring = kidType;
            type = kidType;
            lvalue = kid;
            if (kid instanceof ArrayLiteral) {
               destructuringLen = ((ArrayLiteral)kid).getDestructuringLength();
            }
         }
      } else if (type != 66 && type != 67) {
         lvalue = makeReference(lhs);
         if (lvalue == null) {
            this.reportError("msg.bad.for.in.lhs");
            return null;
         }
      } else {
         destructuring = type;
         lvalue = lhs;
         if (lhs instanceof ArrayLiteral) {
            destructuringLen = ((ArrayLiteral)lhs).getDestructuringLength();
         }
      }

      Node localBlock = new Node(143);
      int initType = isForEach ? 59 : (isForOf ? 61 : (destructuring != -1 ? 60 : 58));
      Node init = new Node(initType, obj);
      init.putProp(3, localBlock);
      Node cond = new Node(62);
      cond.putProp(3, localBlock);
      Node id = new Node(63);
      id.putProp(3, localBlock);
      Node newBody = new Node(131);
      Node assign;
      if (destructuring != -1) {
         assign = this.createDestructuringAssignment(declType, lvalue, id, this::transform);
         if (!isForEach && !isForOf && (destructuring == 67 || destructuringLen != 2)) {
            this.reportError("msg.bad.for.in.destruct");
         }
      } else {
         assign = this.simpleAssignment(lvalue, id);
      }

      newBody.addChildToBack(new Node(135, assign));
      newBody.addChildToBack(body);
      loop = createLoop((Jump)loop, 1, newBody, cond, null, null);
      loop.addChildToFront(init);
      if (type == 124 || type == 155) {
         loop.addChildToFront(lhs);
      }

      localBlock.addChildToBack(loop);
      return localBlock;
   }

   private Node createTryCatchFinally(Node tryBlock, Node catchBlocks, Node finallyBlock, int lineno, int column) {
      boolean hasFinally = finallyBlock != null && (finallyBlock.getType() != 131 || finallyBlock.hasChildren());
      if (tryBlock.getType() == 131 && !tryBlock.hasChildren() && !hasFinally) {
         return tryBlock;
      } else {
         boolean hasCatch = catchBlocks.hasChildren();
         if (!hasFinally && !hasCatch) {
            return tryBlock;
         } else {
            Node handlerBlock = new Node(143);
            Jump pn = new Jump(82, tryBlock);
            pn.setLineColumnNumber(lineno, column);
            pn.putProp(3, handlerBlock);
            if (hasCatch) {
               Node endCatch = Node.newTarget();
               pn.addChildToBack(makeJump(5, endCatch));
               Node catchTarget = Node.newTarget();
               pn.target = catchTarget;
               pn.addChildToBack(catchTarget);
               Node catchScopeBlock = new Node(143);
               Node cb = catchBlocks.getFirstChild();
               boolean hasDefault = false;

               for (int scopeIndex = 0; cb != null; scopeIndex++) {
                  int catchLineno = cb.getLineno();
                  int catchColumn = cb.getColumn();
                  Node name = cb.getFirstChild();
                  Node cond = name.getNext();
                  Node catchStatement = cond.getNext();
                  cb.removeChild(name);
                  cb.removeChild(cond);
                  cb.removeChild(catchStatement);
                  catchStatement.addChildToBack(new Node(3));
                  catchStatement.addChildToBack(makeJump(5, endCatch));
                  Node condStmt;
                  if (cond.getType() == 130) {
                     condStmt = catchStatement;
                     hasDefault = true;
                  } else {
                     condStmt = createIf(cond, catchStatement, null, catchLineno, catchColumn);
                  }

                  Node catchScope = new Node(57, name, createUseLocal(handlerBlock));
                  catchScope.putProp(3, catchScopeBlock);
                  catchScope.putIntProp(14, scopeIndex);
                  catchScopeBlock.addChildToBack(catchScope);
                  catchScopeBlock.addChildToBack(this.createWith(createUseLocal(catchScopeBlock), condStmt, catchLineno));
                  cb = cb.getNext();
               }

               pn.addChildToBack(catchScopeBlock);
               if (!hasDefault) {
                  Node rethrow = new Node(51);
                  rethrow.putProp(3, handlerBlock);
                  pn.addChildToBack(rethrow);
               }

               pn.addChildToBack(endCatch);
            }

            if (hasFinally) {
               Node finallyTarget = Node.newTarget();
               pn.setFinally(finallyTarget);
               pn.addChildToBack(makeJump(137, finallyTarget));
               Node finallyEnd = Node.newTarget();
               pn.addChildToBack(makeJump(5, finallyEnd));
               pn.addChildToBack(finallyTarget);
               Node fBlock = new Node(127, finallyBlock);
               fBlock.putProp(3, handlerBlock);
               pn.addChildToBack(fBlock);
               pn.addChildToBack(finallyEnd);
            }

            handlerBlock.addChildToBack(pn);
            return handlerBlock;
         }
      }
   }

   private Node createWith(Node obj, Node body, int lineno) {
      this.setRequiresActivation();
      Node result = new Node(131, lineno, 0);
      result.addChildToBack(new Node(2, obj));
      Node bodyNode = new Node(125, body, lineno, 0);
      result.addChildrenToBack(bodyNode);
      result.addChildToBack(new Node(3));
      return result;
   }

   private Node createCallOrNew(int nodeType, Node child) {
      int type = 0;
      if (child.getType() == 39) {
         String name = child.getString();
         if (name.equals("eval")) {
            type = 1;
         } else if (name.equals("With")) {
            type = 2;
         }
      }

      Node node = new Node(nodeType, child);
      if (type != 0) {
         this.setRequiresActivation();
         node.putIntProp(10, type);
      }

      return node;
   }

   private Node createPropertyGet(Node target, String namespace, String name, int memberTypeFlags, int type) {
      if (namespace != null || memberTypeFlags != 0) {
         Node elem = Node.newString(name);
         memberTypeFlags |= 1;
         return this.createMemberRefGet(target, namespace, elem, memberTypeFlags);
      } else if (target == null) {
         return this.createName(name);
      } else {
         this.checkActivationName(name, 33);
         if (ScriptRuntime.isSpecialProperty(name)) {
            Node ref = new Node(72, target);
            ref.putProp(17, name);
            Node getRef = new Node(68, ref);
            if (type == 77) {
               ref.putIntProp(31, 1);
               getRef.putIntProp(31, 1);
            }

            return getRef;
         } else {
            Node node = new Node(33, target, Node.newString(name));
            if (type == 77) {
               node.putIntProp(31, 1);
            }

            return node;
         }
      }
   }

   private Node createMemberRefGet(Node target, String namespace, Node elem, int memberTypeFlags) {
      return new Node(68, elem);
   }

   private Node createAssignment(int assignType, Node left, Node right) {
      Node ref = makeReference(left);
      if (ref == null) {
         if (left.getType() != 66 && left.getType() != 67) {
            this.reportError("msg.bad.assign.left");
            return right;
         } else if (assignType != 91) {
            this.reportError("msg.bad.destruct.op");
            return right;
         } else {
            return this.createDestructuringAssignment(-1, left, right, this::transform);
         }
      } else {
         int assignOp;
         switch (assignType) {
            case 91:
               return this.simpleAssignment(ref, right);
            case 92:
               assignOp = 9;
               break;
            case 93:
               assignOp = 10;
               break;
            case 94:
               assignOp = 11;
               break;
            case 95:
               assignOp = 18;
               break;
            case 96:
               assignOp = 19;
               break;
            case 97:
               assignOp = 20;
               break;
            case 98:
               assignOp = 21;
               break;
            case 99:
               assignOp = 22;
               break;
            case 100:
               assignOp = 23;
               break;
            case 101:
               assignOp = 24;
               break;
            case 102:
               assignOp = 25;
               break;
            case 103:
               assignOp = 75;
               break;
            case 174:
               assignOp = 76;
               break;
            default:
               throw Kit.codeBug();
         }

         int nodeType = ref.getType();
         switch (nodeType) {
            case 33:
            case 36: {
               Node obj = ref.getFirstChild();
               Node id = ref.getLastChild();
               int type = nodeType == 33 ? 141 : 142;
               Node opLeft = new Node(140);
               Node op = new Node(assignOp, opLeft, right);
               return new Node(type, obj, id, op);
            }
            case 39: {
               Node op = new Node(assignOp, ref, right);
               Node lvalueLeft = Node.newString(49, ref.getString());
               return new Node(8, lvalueLeft, op);
            }
            case 68: {
               ref = ref.getFirstChild();
               this.checkMutableReference(ref);
               Node opLeft = new Node(140);
               Node op = new Node(assignOp, opLeft, right);
               return new Node(144, ref, op);
            }
            default:
               throw Kit.codeBug();
         }
      }
   }

   boolean isDestructuring(Node n) {
      return n instanceof DestructuringForm && ((DestructuringForm)n).isDestructuring();
   }

   private void inferNameIfMissing(Object left, Node right, String prefix) {
      if (left instanceof Name name && right != null && right.type == 111) {
         if ("__proto__".equals(name.getIdentifier())) {
            return;
         }

         int fnIndex = right.getExistingIntProp(1);
         FunctionNode functionNode = this.currentScriptOrFn.getFunctionNode(fnIndex);
         if (functionNode.getType() != 0 && functionNode.getFunctionName() == null) {
            if (prefix != null) {
               functionNode.setFunctionName(name.withPrefix(prefix));
            } else {
               functionNode.setFunctionName(name);
            }
         }
      }
   }
}
