package com.zzy.dsl.tree;

/**
 * Created by ZhengZiyu on 2017/5/22.
 */
public class BinaryTreeNode<T> {
    private T value;
    private BinaryTreeNode<T> child;
    private BinaryTreeNode<T> siblings;

    public BinaryTreeNode(T t){
        this.value = t;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public BinaryTreeNode<T> getChild() {
        return child;
    }

    public void setChild(BinaryTreeNode<T> child) {
        this.child = child;
    }

    public BinaryTreeNode<T> getSiblings() {
        return siblings;
    }

    public void setSiblings(BinaryTreeNode<T> siblings) {
        this.siblings = siblings;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("(");
        result.append(child).append(",").append(value.toString()).append(",").append(siblings);
        result.append(")\n");
        return result.toString();
    }
}
