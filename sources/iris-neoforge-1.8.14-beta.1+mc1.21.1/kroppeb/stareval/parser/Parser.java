package kroppeb.stareval.parser;

import java.util.ArrayList;
import java.util.List;
import kroppeb.stareval.element.AccessibleExpressionElement;
import kroppeb.stareval.element.Element;
import kroppeb.stareval.element.ExpressionElement;
import kroppeb.stareval.element.PriorityOperatorElement;
import kroppeb.stareval.element.token.IdToken;
import kroppeb.stareval.element.token.NumberToken;
import kroppeb.stareval.element.token.UnaryOperatorToken;
import kroppeb.stareval.element.tree.AccessExpressionElement;
import kroppeb.stareval.element.tree.FunctionCall;
import kroppeb.stareval.element.tree.partial.PartialBinaryExpression;
import kroppeb.stareval.element.tree.partial.UnfinishedArgsExpression;
import kroppeb.stareval.exception.MissingTokenException;
import kroppeb.stareval.exception.ParseException;
import kroppeb.stareval.exception.UnexpectedTokenException;

public class Parser {
   private final List<Element> stack = new ArrayList<>();

   Parser() {
   }

   public static ExpressionElement parse(String input, ParserOptions options) throws ParseException {
      return Tokenizer.parse(input, options);
   }

   private Element peek() {
      return !this.stack.isEmpty() ? (Element)this.stack.getLast() : null;
   }

   private Element pop() {
      if (this.stack.isEmpty()) {
         throw new IllegalStateException("Internal token stack is empty");
      } else {
         return (Element)this.stack.removeLast();
      }
   }

   private void push(Element element) {
      this.stack.add(element);
   }

   private ExpressionElement expressionReducePop() {
      return this.expressionReducePop(2147483647);
   }

   private ExpressionElement expressionReducePop(int priority) {
      ExpressionElement token = (ExpressionElement)this.pop();

      while (!this.stack.isEmpty()) {
         Element x = this.peek();
         if (!(x instanceof PriorityOperatorElement) || ((PriorityOperatorElement)x).getPriority() > priority) {
            break;
         }

         this.pop();
         token = ((PriorityOperatorElement)x).resolveWith(token);
      }

      return token;
   }

   private void commaReduce(int index) throws ParseException {
      ExpressionElement expr = this.expressionReducePop();
      Element args = this.peek();
      if (args == null) {
         throw new MissingTokenException("Expected an opening bracket '(' before seeing a comma ',' or closing bracket ')'", index);
      } else if (args instanceof UnfinishedArgsExpression) {
         ((UnfinishedArgsExpression)args).tokens.add(expr);
      } else {
         throw new UnexpectedTokenException(
            "Expected to see an opening bracket '(' or a comma ',' right before an expression followed by a closing bracket ')' or a comma ','", index
         );
      }
   }

   void visitId(String id) {
      this.push(new IdToken(id));
   }

   boolean canReadAccess() {
      return this.peek() instanceof AccessibleExpressionElement;
   }

   void visitAccess(String access) {
      AccessibleExpressionElement pop = (AccessibleExpressionElement)this.pop();
      this.push(new AccessExpressionElement(pop, access));
   }

   void visitNumber(String numberString) {
      this.push(new NumberToken(numberString));
   }

   void visitOpeningParenthesis() {
      this.push(new UnfinishedArgsExpression());
   }

   void visitComma(int index) throws ParseException {
      if (this.peek() instanceof ExpressionElement) {
         this.commaReduce(index);
      } else {
         throw new UnexpectedTokenException("Expected an expression before a comma ','", index);
      }
   }

   void visitClosingParenthesis(int index) throws ParseException {
      boolean expressionOnTop = this.peek() instanceof ExpressionElement;
      if (expressionOnTop) {
         this.commaReduce(index);
      }

      if (this.stack.isEmpty()) {
         throw new MissingTokenException("A closing bracket ')' can't be the first character of an expression", index);
      } else if (!(this.pop() instanceof UnfinishedArgsExpression args)) {
         throw new UnexpectedTokenException(
            "Expected to see an opening bracket '(' or a comma ',' right before an expression followed by a closing bracket ')' or a comma ','", index
         );
      } else {
         Element top = this.peek();
         if (top instanceof IdToken) {
            this.pop();
            this.push(new FunctionCall(((IdToken)top).getId(), args.tokens));
         } else {
            if (args.tokens.isEmpty()) {
               throw new MissingTokenException("Encountered empty brackets that aren't a call", index);
            }

            if (args.tokens.size() > 1) {
               throw new UnexpectedTokenException("Encountered too many expressions in brackets that aren't a call", index);
            }

            if (!expressionOnTop) {
               throw new UnexpectedTokenException("Encountered a trailing comma in brackets that aren't a call", index);
            }

            this.push((Element)args.tokens.getFirst());
         }
      }
   }

   boolean canReadBinaryOp() {
      return this.peek() instanceof ExpressionElement;
   }

   void visitBinaryOperator(BinaryOp binaryOp) {
      ExpressionElement left = this.expressionReducePop(binaryOp.priority());
      this.stack.add(new PartialBinaryExpression(left, binaryOp));
   }

   void visitUnaryOperator(UnaryOp unaryOp) {
      this.push(new UnaryOperatorToken(unaryOp));
   }

   ExpressionElement getFinal(int endIndex) throws ParseException {
      if (!this.stack.isEmpty()) {
         if (this.peek() instanceof ExpressionElement) {
            ExpressionElement result = this.expressionReducePop();
            if (this.stack.isEmpty()) {
               return result;
            } else if (this.peek() instanceof UnfinishedArgsExpression) {
               throw new MissingTokenException("Expected a closing bracket", endIndex);
            } else {
               throw new UnexpectedTokenException("The stack of tokens isn't empty at the end of the expression: " + this.stack + " top: " + result, endIndex);
            }
         } else {
            Element top = this.peek();
            if (top instanceof UnfinishedArgsExpression) {
               throw new MissingTokenException("Expected a closing bracket", endIndex);
            } else if (top instanceof PriorityOperatorElement) {
               throw new MissingTokenException("Expected a identifier, constant or subexpression on the right side of the operator", endIndex);
            } else {
               throw new UnexpectedTokenException("The stack of tokens contains an unexpected token at the top: " + this.stack, endIndex);
            }
         }
      } else {
         throw new MissingTokenException("The input seems to be empty", endIndex);
      }
   }
}
