package com.zzy.dsl.tree;

import java.util.LinkedHashSet;

/**
 * Created by ZhengZiyu on 2017/3/22.
 */
public class TreeNode<T> {

    private T value;

    private LinkedHashSet<TreeNode<T>> children = new LinkedHashSet<TreeNode<T>>();

    public TreeNode(T t){
        this.value = t;
    }

    public T getValue() {
        return value;
    }

    public void addChild(TreeNode<T> child){
        children.add(child);
    }
    public void removeChild(TreeNode<T> child){
        children.remove(child);
    }

    public LinkedHashSet<TreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(LinkedHashSet<TreeNode<T>> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(value.toString());
        if(children.size() != 0){
            result.append("\n(");
            for(TreeNode treeNode : children){
                result.append(treeNode).append(",");
            }
            result.deleteCharAt(result.lastIndexOf(","));
            result.append(")");
        }

        return result.toString();
    }
}
