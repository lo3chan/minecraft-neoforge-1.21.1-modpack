/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.diebuddies.model;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.diebuddies.model.XmlNode;

public class XmlParser {
    private static final Pattern DATA = Pattern.compile(">(.+?)<");
    private static final Pattern START_TAG = Pattern.compile("<(.+?)>");
    private static final Pattern END_TAG = Pattern.compile("(</|/>)");
    private static final Pattern ATTRIBUTE_NAME = Pattern.compile("(.+?)=");
    private static final Pattern ATTRIBUTE_VALUE = Pattern.compile("\"(.+?)\"");
    private static final Pattern SPACE_SPLITTER = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

    public static XmlNode loadFile(File file) throws IOException {
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(file));
        reader.readLine();
        XmlNode node = XmlParser.loadNode(reader);
        reader.close();
        return node;
    }

    public static void saveFile(File file, XmlNode node) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        String header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
        try (PrintWriter out = new PrintWriter(file);){
            out.write(header);
            out.write(node.generateString(0));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Couldn't find XML File: " + file.getPath());
        }
    }

    private static XmlNode loadNode(BufferedReader reader) throws IOException {
        String line = reader.readLine().trim();
        while (line.isEmpty()) {
            line = reader.readLine().trim();
        }
        if (line.startsWith("</")) {
            return null;
        }
        Matcher startMatcher = START_TAG.matcher(line);
        startMatcher.find();
        List<String> startSplit = XmlParser.splitAttributes(startMatcher.group(1));
        String nodeName = startSplit.get(0);
        XmlNode node = new XmlNode(nodeName);
        XmlParser.addAttributes(node, startSplit);
        XmlParser.addData(node, line);
        if (END_TAG.matcher(line).find()) {
            return node;
        }
        XmlNode child = null;
        while ((child = XmlParser.loadNode(reader)) != null) {
            node.addChild(child);
        }
        return node;
    }

    private static List<String> splitAttributes(String group) {
        boolean isInside = false;
        StringBuilder stringBuilder = new StringBuilder();
        ObjectArrayList strings = new ObjectArrayList();
        for (int i = 0; i < group.length(); ++i) {
            char ic = group.charAt(i);
            if (ic == '\"') {
                boolean bl = isInside = !isInside;
            }
            if (ic == ' ' && !isInside) {
                strings.add(stringBuilder.toString());
                stringBuilder.setLength(0);
                continue;
            }
            stringBuilder.append(ic);
        }
        strings.add(stringBuilder.toString());
        return strings;
    }

    private static void addData(XmlNode node, String line) {
        Matcher data = DATA.matcher(line);
        if (data.find()) {
            node.setData(data.group(1));
        }
    }

    private static void addAttributes(XmlNode node, List<String> startSplit) {
        for (int i = 1; i < startSplit.size(); ++i) {
            String split = startSplit.get(i);
            if (!split.contains("=")) continue;
            Matcher attribName = ATTRIBUTE_NAME.matcher(split);
            attribName.find();
            Matcher attribValue = ATTRIBUTE_VALUE.matcher(split);
            attribValue.find();
            node.addAttribute(attribName.group(1), attribValue.group(1));
        }
    }
}

