package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.ast.FunctionNode;
import dev.latvian.mods.rhino.ast.Jump;
import dev.latvian.mods.rhino.ast.Scope;
import dev.latvian.mods.rhino.ast.ScriptNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NodeTransformer {
   private ObjArray loops;
   private ObjArray loopEnds;
   private boolean hasFinally;

   private static Node addBeforeCurrent(Node parent, Node previous, Node current, Node toAdd) {
      if (previous == null) {
         if (current != parent.getFirstChild()) {
            Kit.codeBug();
         }

         parent.addChildToFront(toAdd);
      } else {
         if (current != previous.getNext()) {
            Kit.codeBug();
         }

         parent.addChildAfter(toAdd, previous);
      }

      return toAdd;
   }

   private static Node replaceCurrent(Node parent, Node previous, Node current, Node replacement) {
      if (previous == null) {
         if (current != parent.getFirstChild()) {
            Kit.codeBug();
         }

         parent.replaceChild(current, replacement);
      } else if (previous.next == current) {
         parent.replaceChildAfter(previous, replacement);
      } else {
         parent.replaceChild(current, replacement);
      }

      return replacement;
   }

   public final void transform(ScriptNode tree, CompilerEnvirons env) {
      this.transform(tree, false, env);
   }

   public final void transform(ScriptNode tree, boolean inStrictMode, CompilerEnvirons env) {
      boolean useStrictMode = inStrictMode;
      if (tree.isInStrictMode()) {
         useStrictMode = true;
      }

      this.transformCompilationUnit(tree, useStrictMode);

      for (int i = 0; i != tree.getFunctionCount(); i++) {
         FunctionNode fn = tree.getFunctionNode(i);
         this.transform(fn, useStrictMode, env);
      }
   }

   private void transformCompilationUnit(ScriptNode tree, boolean inStrictMode) {
      this.loops = new ObjArray();
      this.loopEnds = new ObjArray();
      this.hasFinally = false;
      boolean createScopeObjects = tree.getType() != 111 || ((FunctionNode)tree).requiresActivation();
      tree.flattenSymbolTable(!createScopeObjects);
      this.transformCompilationUnit_r(tree, tree, tree, createScopeObjects, inStrictMode);
   }

   private void transformCompilationUnit_r(ScriptNode tree, Node parent, Scope scope, boolean createScopeObjects, boolean inStrictMode) {
      Node node = null;

      while (true) {
         Node previous = null;
         if (node == null) {
            node = parent.getFirstChild();
         } else {
            previous = node;
            node = node.getNext();
         }

         if (node == null) {
            return;
         }

         int type = node.getType();
         if (createScopeObjects && (type == 131 || type == 134 || type == 159) && node instanceof Scope newScope && newScope.getSymbolTable() != null) {
            Node let = new Node(type == 159 ? 160 : 155);
            Node innerLet = new Node(155);
            let.addChildToBack(innerLet);

            for (String name : newScope.getSymbolTable().keySet()) {
               innerLet.addChildToBack(Node.newString(39, name));
            }

            newScope.setSymbolTable(null);
            Node oldNode = node;
            node = replaceCurrent(parent, previous, node, let);
            type = node.getType();
            let.addChildToBack(oldNode);
         }

         label286:
         switch (type) {
            case 3:
            case 133:
               if (!this.loopEnds.isEmpty() && this.loopEnds.peek() == node) {
                  this.loopEnds.pop();
                  this.loops.pop();
               }
               break;
            case 4:
               boolean isGenerator = tree.getType() == 111 && ((FunctionNode)tree).isGenerator();
               if (isGenerator) {
                  node.putIntProp(20, 1);
               }

               if (this.hasFinally) {
                  Node unwindBlock = null;

                  for (int i = this.loops.size() - 1; i >= 0; i--) {
                     Node n = (Node)this.loops.get(i);
                     int elemtype = n.getType();
                     if (elemtype == 82 || elemtype == 125) {
                        Node unwind;
                        if (elemtype == 82) {
                           Jump jsrnode = new Jump(137);
                           Node jsrtarget = ((Jump)n).getFinally();
                           jsrnode.target = jsrtarget;
                           unwind = jsrnode;
                        } else {
                           unwind = new Node(3);
                        }

                        if (unwindBlock == null) {
                           unwindBlock = new Node(131);
                           unwind.setLineColumnNumber(node.getLineno(), node.getColumn());
                        }

                        unwindBlock.addChildToBack(unwind);
                     }
                  }

                  if (unwindBlock != null) {
                     Node returnNode = node;
                     Node returnExpr = node.getFirstChild();
                     node = replaceCurrent(parent, previous, node, unwindBlock);
                     if (returnExpr != null && !isGenerator) {
                        Node store = new Node(136, returnExpr);
                        unwindBlock.addChildToFront(store);
                        returnNode = new Node(65);
                        unwindBlock.addChildToBack(returnNode);
                        this.transformCompilationUnit_r(tree, store, scope, createScopeObjects, inStrictMode);
                     } else {
                        unwindBlock.addChildToBack(returnNode);
                     }
                     continue;
                  }
               }
               break;
            case 7:
            case 32:
               Node child = node.getFirstChild();
               if (type == 7) {
                  while (child.getType() == 26) {
                     child = child.getFirstChild();
                  }

                  if (child.getType() == 12 || child.getType() == 13) {
                     Node first = child.getFirstChild();
                     Node last = child.getLastChild();
                     if (first.getType() == 39 && first.getString().equals("undefined")) {
                        child = last;
                     } else if (last.getType() == 39 && last.getString().equals("undefined")) {
                        child = first;
                     }
                  }
               }

               if (child.getType() == 33) {
                  child.setType(34);
               }
               break;
            case 8:
               if (inStrictMode) {
                  node.setType(74);
               }
            case 31:
            case 39:
            case 157:
               label267:
               if (!createScopeObjects) {
                  Node nameSource;
                  if (type == 39) {
                     nameSource = node;
                  } else {
                     nameSource = node.getFirstChild();
                     if (nameSource.getType() != 49) {
                        if (type != 31) {
                           throw Kit.codeBug();
                        }
                        break label267;
                     }
                  }

                  if (nameSource.getScope() == null) {
                     String name = nameSource.getString();
                     Scope definingx = scope.getDefiningScope(name);
                     if (definingx != null) {
                        nameSource.setScope(definingx);
                        if (type == 39) {
                           node.setType(55);
                        } else if (type == 8 || type == 74) {
                           node.setType(56);
                           nameSource.setType(41);
                        } else if (type == 157) {
                           node.setType(158);
                           nameSource.setType(41);
                        } else {
                           if (type != 31) {
                              throw Kit.codeBug();
                           }

                           Node nx = new Node(44);
                           node = replaceCurrent(parent, previous, node, nx);
                        }
                     }
                  }
               }
               break;
            case 30:
               this.visitNew(node, tree);
               break;
            case 38:
               this.visitCall(node, tree);
               break;
            case 67:
               Object[] propertyIds = (Object[])node.getProp(12);
               if (propertyIds != null) {
                  for (Object propertyId : propertyIds) {
                     if (propertyId instanceof Node) {
                        this.transformCompilationUnit_r(tree, (Node)propertyId, node instanceof Scope ? (Scope)node : scope, createScopeObjects, inStrictMode);
                     }
                  }
               }
               break;
            case 73:
            case 167:
               ((FunctionNode)tree).addResumptionPoint(node);
               break;
            case 82:
               Jump jump = (Jump)node;
               Node finallytarget = jump.getFinally();
               if (finallytarget != null) {
                  this.hasFinally = true;
                  this.loops.push(node);
                  this.loopEnds.push(finallytarget);
               }
               break;
            case 116:
            case 132:
            case 134:
               this.loops.push(node);
               this.loopEnds.push(((Jump)node).target);
               break;
            case 122:
            case 123:
               Jump jumpx = (Jump)node;
               Jump jumpStatement = jumpx.getJumpStatement();
               if (jumpStatement == null) {
                  Kit.codeBug();
               }

               int ix = this.loops.size();

               while (ix != 0) {
                  Node n = (Node)this.loops.get(--ix);
                  if (n == jumpStatement) {
                     if (type == 122) {
                        jumpx.target = jumpStatement.target;
                     } else {
                        jumpx.target = jumpStatement.getContinue();
                     }

                     jumpx.setType(5);
                     break label286;
                  }

                  int elemtype = n.getType();
                  if (elemtype == 125) {
                     Node leave = new Node(3);
                     previous = addBeforeCurrent(parent, previous, node, leave);
                  } else if (elemtype == 82) {
                     Jump tryNode = (Jump)n;
                     Jump jsrFinally = new Jump(137);
                     jsrFinally.target = tryNode.getFinally();
                     previous = addBeforeCurrent(parent, previous, node, jsrFinally);
                  }
               }

               throw Kit.codeBug();
            case 125:
               this.loops.push(node);
               Node leave = node.getNext();
               if (leave.getType() != 3) {
                  Kit.codeBug();
               }

               this.loopEnds.push(leave);
               break;
            case 139:
               Scope defining = scope.getDefiningScope(node.getString());
               if (defining != null) {
                  node.setScope(defining);
               }
               break;
            case 155:
            case 160:
               Node childx = node.getFirstChild();
               if (childx.getType() == 155) {
                  boolean createWith = tree.getType() != 111 || ((FunctionNode)tree).requiresActivation();
                  node = this.visitLet(createWith, parent, previous, node);
                  break;
               }
            case 124:
            case 156:
               Node result = new Node(131);
               Node cursor = node.getFirstChild();

               while (cursor != null) {
                  Node nx = cursor;
                  cursor = cursor.getNext();
                  if (nx.getType() == 39) {
                     if (!nx.hasChildren()) {
                        continue;
                     }

                     Node init = nx.getFirstChild();
                     nx.removeChild(init);
                     nx.setType(49);
                     nx = new Node(type == 156 ? 157 : 8, nx, init);
                  } else if (nx.getType() != 160) {
                     throw Kit.codeBug();
                  }

                  Node pop = new Node(135, nx, node.getLineno(), 0);
                  result.addChildToBack(pop);
               }

               node = replaceCurrent(parent, previous, node, result);
         }

         this.transformCompilationUnit_r(tree, node, node instanceof Scope ? (Scope)node : scope, createScopeObjects, inStrictMode);
      }
   }

   protected void visitNew(Node node, ScriptNode tree) {
   }

   protected void visitCall(Node node, ScriptNode tree) {
   }

   protected Node visitLet(boolean createWith, Node parent, Node previous, Node scopeNode) {
      Node vars = scopeNode.getFirstChild();
      Node body = vars.getNext();
      scopeNode.removeChild(vars);
      scopeNode.removeChild(body);
      boolean isExpression = scopeNode.getType() == 160;
      Node result;
      if (!createWith) {
         result = new Node(isExpression ? 90 : 131);
         result = replaceCurrent(parent, previous, scopeNode, result);
         Node newVars = new Node(90);

         for (Node v = vars.getFirstChild(); v != null; v = v.getNext()) {
            Node current = v;
            if (v.getType() == 160) {
               Node c = v.getFirstChild();
               if (c.getType() != 155) {
                  throw Kit.codeBug();
               }

               if (isExpression) {
                  body = new Node(90, c.getNext(), body);
               } else {
                  body = new Node(131, new Node(135, c.getNext()), body);
               }

               Scope.joinScopes((Scope)v, (Scope)scopeNode);
               current = c.getFirstChild();
            }

            if (current.getType() != 39) {
               throw Kit.codeBug();
            }

            Node stringNode = Node.newString(current.getString());
            stringNode.setScope((Scope)scopeNode);
            Node init = current.getFirstChild();
            if (init == null) {
               init = new Node(128, Node.newNumber(0.0));
            }

            newVars.addChildToBack(new Node(56, stringNode, init));
         }

         if (isExpression) {
            result.addChildToBack(newVars);
            scopeNode.setType(90);
            result.addChildToBack(scopeNode);
            scopeNode.addChildToBack(body);
            if (body instanceof Scope) {
               Scope scopeParent = ((Scope)body).getParentScope();
               ((Scope)body).setParentScope((Scope)scopeNode);
               ((Scope)scopeNode).setParentScope(scopeParent);
            }
         } else {
            result.addChildToBack(new Node(135, newVars));
            scopeNode.setType(131);
            result.addChildToBack(scopeNode);
            scopeNode.addChildrenToBack(body);
            if (body instanceof Scope) {
               Scope scopeParent = ((Scope)body).getParentScope();
               ((Scope)body).setParentScope((Scope)scopeNode);
               ((Scope)scopeNode).setParentScope(scopeParent);
            }
         }
      } else {
         result = new Node(isExpression ? 161 : 131);
         result = replaceCurrent(parent, previous, scopeNode, result);
         ArrayList<Object> list = new ArrayList<>();
         Node objectLiteral = new Node(67);

         for (Node v = vars.getFirstChild(); v != null; v = v.getNext()) {
            Node currentx = v;
            if (v.getType() == 160) {
               List<?> destructuringNames = (List<?>)v.getProp(22);
               Node cx = v.getFirstChild();
               if (cx.getType() != 155) {
                  throw Kit.codeBug();
               }

               if (isExpression) {
                  body = new Node(90, cx.getNext(), body);
               } else {
                  body = new Node(131, new Node(135, cx.getNext()), body);
               }

               if (destructuringNames != null) {
                  list.addAll((Collection<? extends Object>)destructuringNames);

                  for (int i = 0; i < destructuringNames.size(); i++) {
                     objectLiteral.addChildToBack(new Node(128, Node.newNumber(0.0)));
                  }
               }

               currentx = cx.getFirstChild();
            }

            if (currentx.getType() != 39) {
               throw Kit.codeBug();
            }

            list.add(ScriptRuntime.getIndexObject(currentx.getString()));
            Node init = currentx.getFirstChild();
            if (init == null) {
               init = new Node(128, Node.newNumber(0.0));
            }

            objectLiteral.addChildToBack(init);
         }

         objectLiteral.putProp(12, list.toArray());
         Node newVars = new Node(2, objectLiteral);
         result.addChildToBack(newVars);
         result.addChildToBack(new Node(125, body));
         result.addChildToBack(new Node(3));
      }

      return result;
   }
}
