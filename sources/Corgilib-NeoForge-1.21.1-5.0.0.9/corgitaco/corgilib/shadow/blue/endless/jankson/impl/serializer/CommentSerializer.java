package corgitaco.corgilib.shadow.blue.endless.jankson.impl.serializer;

import corgitaco.corgilib.shadow.blue.endless.jankson.JsonGrammar;
import java.io.IOException;
import java.io.Writer;

public class CommentSerializer {
   public static void print(Writer writer, String comment, int indent, JsonGrammar grammar) throws IOException {
      if (comment != null && !comment.trim().isEmpty()) {
         StringBuilder b = new StringBuilder(comment.length());
         print(b, comment, indent, grammar);
         writer.append(b);
      }
   }

   public static void print(StringBuilder builder, String comment, int indent, JsonGrammar grammar) {
      boolean comments = grammar.hasComments();
      boolean whitespace = grammar.shouldOutputWhitespace();
      print(builder, comment, indent, comments, whitespace);
   }

   public static void print(StringBuilder builder, String comment, int indent, boolean comments, boolean whitespace) {
      if (comments) {
         if (comment != null && !comment.trim().isEmpty()) {
            if (whitespace) {
               if (comment.contains("\n")) {
                  builder.append("/* ");
                  String[] lines = comment.split("\\n");

                  for (int i = 0; i < lines.length; i++) {
                     String line = lines[i];
                     if (i != 0) {
                        builder.append("   ");
                     }

                     builder.append(line);
                     builder.append('\n');

                     for (int j = 0; j < indent + 1; j++) {
                        builder.append('\t');
                     }
                  }

                  builder.append("*/\n");

                  for (int i = 0; i < indent + 1; i++) {
                     builder.append('\t');
                  }
               } else {
                  builder.append("// ");
                  builder.append(comment);
                  builder.append('\n');

                  for (int i = 0; i < indent + 1; i++) {
                     builder.append('\t');
                  }
               }
            } else if (comment.contains("\n")) {
               String[] lines = comment.split("\\n");

               for (int i = 0; i < lines.length; i++) {
                  String line = lines[i];
                  builder.append("/* ");
                  builder.append(line);
                  builder.append(" */ ");
               }
            } else {
               builder.append("/* ");
               builder.append(comment);
               builder.append(" */ ");
            }
         }
      }
   }
}
