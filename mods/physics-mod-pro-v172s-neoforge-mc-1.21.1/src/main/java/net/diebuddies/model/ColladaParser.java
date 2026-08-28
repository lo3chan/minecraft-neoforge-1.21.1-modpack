/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.joml.Vector2f
 *  org.joml.Vector3f
 *  org.joml.Vector4i
 */
package net.diebuddies.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import net.diebuddies.model.ColladaMesh;
import net.diebuddies.model.XmlNode;
import net.diebuddies.model.XmlParser;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4i;

public class ColladaParser {
    public static ColladaMesh loadStaticModel(File file) {
        return ColladaParser.loadMultipleStaticModel(file).values().iterator().next();
    }

    public static Map<String, ColladaMesh> loadMultipleStaticModel(File file) {
        try {
            XmlNode colladaFile = XmlParser.loadFile(file);
            XmlNode geometries = colladaFile.getChild("library_geometries");
            Object2ObjectOpenHashMap meshes = new Object2ObjectOpenHashMap();
            for (XmlNode geometry : geometries.getChildren("geometry")) {
                meshes.put(geometry.getAttribute("name"), ColladaMesh.parseMesh(geometry));
            }
            return meshes;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void convertFolder(File src) {
        for (File file : src.listFiles()) {
            if (!file.getName().endsWith("dae")) continue;
            String name = file.getName();
            String nameNoExtension = name;
            if (nameNoExtension.contains(".")) {
                nameNoExtension = name.substring(0, nameNoExtension.lastIndexOf(46));
            }
            ColladaParser.saveModelsBinary(new File(file.getParentFile().getAbsolutePath() + "/" + nameNoExtension + ".bf"), ColladaParser.loadMultipleStaticModel(file));
        }
    }

    public static void saveModelsBinary(File file, Map<String, ColladaMesh> models) {
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try (FileOutputStream outputStream = new FileOutputStream(file);
             DataOutputStream output = new DataOutputStream(outputStream);){
            output.writeByte(models.size());
            for (Map.Entry<String, ColladaMesh> entry : models.entrySet()) {
                output.writeUTF(entry.getKey());
                ColladaMesh mesh = entry.getValue();
                ColladaParser.write3f(output, mesh.positions);
                ColladaParser.write3f(output, mesh.normals);
                ColladaParser.write2f(output, mesh.texCoords);
                ColladaParser.write3b(output, mesh.colors);
                ColladaParser.write4s(output, mesh.indices);
                ColladaParser.write1s(output, mesh.lineIndices);
                ColladaParser.write1b(output, mesh.polyCount);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static Map<String, ColladaMesh> readModelsBinary(File file) {
        try (FileInputStream inputStream = new FileInputStream(file);){
            Object2ObjectOpenHashMap object2ObjectOpenHashMap;
            try (DataInputStream input = new DataInputStream(inputStream);){
                int size = input.readInt();
                Object2ObjectOpenHashMap models = new Object2ObjectOpenHashMap();
                for (int i = 0; i < size; ++i) {
                    ColladaMesh mesh = new ColladaMesh();
                    String name = input.readUTF();
                    mesh.positions = ColladaParser.read3f(input);
                    mesh.normals = ColladaParser.read3f(input);
                    mesh.texCoords = ColladaParser.read2f(input);
                    mesh.colors = ColladaParser.read3f(input);
                    mesh.indices = ColladaParser.read4s(input);
                    mesh.lineIndices = ColladaParser.read1s(input);
                    mesh.polyCount = ColladaParser.read1b(input);
                    models.put(name, mesh);
                }
                object2ObjectOpenHashMap = models;
            }
            return object2ObjectOpenHashMap;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void write1b(DataOutputStream output, byte[] list) throws IOException {
        output.writeInt(list.length);
        for (byte v : list) {
            output.writeByte(v);
        }
    }

    private static void write1s(DataOutputStream output, List<Integer> list) throws IOException {
        output.writeInt(list.size());
        for (Integer v : list) {
            output.writeShort(v);
        }
    }

    private static void write4s(DataOutputStream output, List<Vector4i> list) throws IOException {
        output.writeInt(list.size());
        for (Vector4i v : list) {
            output.writeShort(v.x);
            output.writeShort(v.y);
            output.writeShort(v.z);
            output.writeShort(v.w);
        }
    }

    private static void write2f(DataOutputStream output, List<Vector2f> list) throws IOException {
        output.writeInt(list.size());
        for (Vector2f v : list) {
            output.writeFloat(v.x);
            output.writeFloat(v.y);
        }
    }

    private static void write3f(DataOutputStream output, List<Vector3f> list) throws IOException {
        output.writeInt(list.size());
        for (Vector3f v : list) {
            output.writeFloat(v.x);
            output.writeFloat(v.y);
            output.writeFloat(v.z);
        }
    }

    private static void write3b(DataOutputStream output, List<Vector3f> list) throws IOException {
        output.writeInt(list.size());
        for (Vector3f v : list) {
            output.writeByte((byte)((int)((double)(v.x * 255.0f) + 0.5) & 0xFF));
            output.writeByte((byte)((int)((double)(v.y * 255.0f) + 0.5) & 0xFF));
            output.writeByte((byte)((int)((double)(v.z * 255.0f) + 0.5) & 0xFF));
        }
    }

    private static byte[] read1b(DataInputStream input) throws IOException {
        byte[] data = new byte[input.readInt()];
        for (int i = 0; i < data.length; ++i) {
            data[i] = input.readByte();
        }
        return data;
    }

    private static List<Integer> read1s(DataInputStream input) throws IOException {
        int size = input.readInt();
        ObjectArrayList data = new ObjectArrayList(size);
        for (int i = 0; i < size; ++i) {
            data.add(Integer.valueOf(input.readShort()));
        }
        return data;
    }

    private static List<Vector4i> read4s(DataInputStream input) throws IOException {
        int size = input.readInt();
        ObjectArrayList data = new ObjectArrayList(size);
        for (int i = 0; i < size; ++i) {
            data.add(new Vector4i((int)input.readShort(), (int)input.readShort(), (int)input.readShort(), (int)input.readShort()));
        }
        return data;
    }

    private static List<Vector2f> read2f(DataInputStream input) throws IOException {
        int size = input.readInt();
        ObjectArrayList data = new ObjectArrayList(size);
        for (int i = 0; i < size; ++i) {
            data.add(new Vector2f(input.readFloat(), input.readFloat()));
        }
        return data;
    }

    private static List<Vector3f> read3f(DataInputStream input) throws IOException {
        int size = input.readInt();
        ObjectArrayList data = new ObjectArrayList(size);
        for (int i = 0; i < size; ++i) {
            data.add(new Vector3f(input.readFloat(), input.readFloat(), input.readFloat()));
        }
        return data;
    }
}

