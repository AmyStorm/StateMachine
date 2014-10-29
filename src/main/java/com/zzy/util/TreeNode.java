package com.zzy.util;

import java.util.HashSet;
import java.util.Set;

/**
 * 树节点对象
 * @author ZhengZiyu
 *
 * @param <T>
 */
public class TreeNode<T extends TreeEntity> {
	private T value;
	
	private Set<TreeNode<T>> children = new HashSet<TreeNode<T>>();
	
	public TreeNode(T t){
		this.value = t;
	}
	
	public T getValue() {
	    return value;
    }
	
	public void addChild(TreeNode<T> child){
		children.add(child);
	}
	
	public Set<TreeNode<T>> getChildren() {
	    return children;
    }
	
	public void setChildren(Set<TreeNode<T>> children) {
	    this.children = children;
    }

	@Override
    public String toString() {
	    return "TreeNode [value=" + value + ", children=" + children + "]";
    }
	
}
