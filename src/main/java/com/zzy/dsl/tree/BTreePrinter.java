package com.zzy.dsl.tree;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ZhengZiyu on 2017/5/24.
 */
public class BTreePrinter {

    public static <T> void printNode(BinaryTreeNode<T> root, Writer writer) throws IOException {
        int maxLevel = BTreePrinter.maxLevel(root);

        printNodeInternal(Collections.singletonList(root), 1, maxLevel, writer);
    }

    private static <T> void printNodeInternal(List<BinaryTreeNode<T>> nodes, int level, int maxLevel, Writer writer) throws IOException {
        if (nodes.isEmpty() || BTreePrinter.isAllElementsNull(nodes))
            return;

        int floor = maxLevel - level;
        int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
        int firstSpaces = (int) Math.pow(2, (floor)) - 1;
        int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

        BTreePrinter.printWhitespaces(firstSpaces, writer);

        List<BinaryTreeNode<T>> newNodes = new ArrayList<BinaryTreeNode<T>>();
        for (BinaryTreeNode<T> node : nodes) {
            if (node != null) {
                writer.write(node.getValue().toString());
                newNodes.add(node.getChild());
                newNodes.add(node.getSiblings());
            } else {
                newNodes.add(null);
                newNodes.add(null);
                writer.write(" ");
//                System.out.print(" ");
            }

            BTreePrinter.printWhitespaces(betweenSpaces, writer);
        }
        writer.write("\n");
//        System.out.println("");

        for (int i = 1; i <= endgeLines; i++) {
            for (int j = 0; j < nodes.size(); j++) {
                BTreePrinter.printWhitespaces(firstSpaces - i, writer);
                if (nodes.get(j) == null) {
                    BTreePrinter.printWhitespaces(endgeLines + endgeLines + i + 1, writer);
                    continue;
                }

                if (nodes.get(j).getChild() != null)
                    writer.write("/");
//                    System.out.print("/");
                else
                    BTreePrinter.printWhitespaces(1, writer);

                BTreePrinter.printWhitespaces(i + i - 1, writer);

                if (nodes.get(j).getSiblings() != null)
                    writer.write("\\");
//                    System.out.print("\\");
                else
                    BTreePrinter.printWhitespaces(1, writer);

                BTreePrinter.printWhitespaces(endgeLines + endgeLines - i, writer);
            }
            writer.write("\n");
//            System.out.println("");
        }

        printNodeInternal(newNodes, level + 1, maxLevel, writer);
    }

    private static void printWhitespaces(int count, Writer writer) throws IOException {
        for (int i = 0; i < count; i++){
            writer.write(" ");
        }
//            System.out.print(" ");
    }

    private static <T> int maxLevel(BinaryTreeNode<T> node) {
        if (node == null)
            return 0;

        return Math.max(BTreePrinter.maxLevel(node.getChild()), BTreePrinter.maxLevel(node.getSiblings())) + 1;
    }

    private static <T> boolean isAllElementsNull(List<T> list) {
        for (Object object : list) {
            if (object != null)
                return false;
        }

        return true;
    }

}