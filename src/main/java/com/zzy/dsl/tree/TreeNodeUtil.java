package com.zzy.dsl.tree;

import com.zzy.dsl.grammar.Grammar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 树结构数据对象工具类<br />
 * examples:
 *              Office implements TreeEntity.
 *  			List<Office> related = officeService.findRelatedOffices(id);
 *              Office root = officeService.get(id);
 *              List<Office> result = TreeNodeUtil.postIterationResult(related, root);
 * @author ZhengZiyu
 *
 */
public class TreeNodeUtil {
	private TreeNodeUtil(){
		
	}
	/**
	 * 组装树对象
	 * @param father
	 * @param source
	 * @param root
	 * @return
	 */
	public static <T> LinkedHashSet<TreeNode<T>> findChild(T father, List<T> source, TreeNode<T> root){
		LinkedHashSet<TreeNode<T>> nodes = new LinkedHashSet<TreeNode<T>>();
		for(T bean : source){
			if(bean.equals(father)){
				nodes.add(new TreeNode<T>(bean));
			}
		}

		for(TreeNode<T> node : nodes){
			LinkedHashSet<TreeNode<T>> children = findChild(node.getValue(), source, node);
			node.setChildren(children);
		}
		return nodes;
	}
	
	/**
	 * 后序遍历node节点
	 * @param node
	 */
	public static <T> void post(TreeNode<T> node, List<T> result){
		if(node == null){
			return;
		}
		Set<TreeNode<T>> children = node.getChildren();
		for(TreeNode<T> child : children){
			post(child, result);
		}
		result.add(node.getValue());
	}
	
	/**
	 * 返回有序后序遍历结果
	 * @param source
	 * @param root
	 * @return
	 */
	public static <T> List<T> postIterationResult(List<T> source, T root){
		List<T> result = new ArrayList<T>();
		TreeNode<T> rootTreeNode = new TreeNode<T>(root);
		rootTreeNode.setChildren(findChild(rootTreeNode.getValue(), source, rootTreeNode));
		post(rootTreeNode, result);
		return result;
	}

	/**
	 * 返回第一个child节点
	 * @param children
	 * @param parentBTreeNode
	 * @param <T>
	 * @return
	 */
	public static <T> BinaryTreeNode<T> convertToAST(LinkedHashSet<TreeNode<T>> children, BinaryTreeNode<T> parentBTreeNode){
		if(children == null || children.size() == 0){
			return null;
		}
		BinaryTreeNode<T> tempParent = parentBTreeNode;
		BinaryTreeNode<T> firstChild = null;
		BinaryTreeNode<T> temp = null;
		Iterator<TreeNode<T>> iterator = children.iterator();
		int i = 0;
		for(;iterator.hasNext();){
			TreeNode<T> next = iterator.next();
			if(!Grammar.usedToken.contains(next.getValue())){
				BinaryTreeNode<T> current = new BinaryTreeNode<T>(next.getValue());
				if(i == 0){
					tempParent.setChild(current);
					firstChild = current;
					temp = firstChild;
				}else{
					temp = current;
					tempParent.setSiblings(temp);
				}
				BinaryTreeNode<T> childAst = convertToAST(next.getChildren(), temp);
//				while(childAst != null && childAst.getChild() != null && Grammar.hiddenChild.contains(childAst.getValue())){
//					childAst = singleRotateWithRight(childAst.getChild());
//				}
				temp.setChild(childAst);
				tempParent = current;
				i++;
			}
		}
		return firstChild;
	}

	public static <T> List<T> preIterateBinaryTree(BinaryTreeNode<T> binaryTreeNode, List<T> result){
		if(result != null){
			result.add(binaryTreeNode.getValue());
		}else{
			result = new ArrayList<T>();
		}
		if(binaryTreeNode.getChild() != null){
			result.addAll(preIterateBinaryTree(binaryTreeNode.getChild(), result));
		}
		if(binaryTreeNode.getSiblings() != null){
			result.addAll(preIterateBinaryTree(binaryTreeNode.getSiblings(), result));
		}
		return result;
	}

	public static <T> BinaryTreeNode<T> singleRotateWithRight(BinaryTreeNode<T> t){
		if(t.getChild() != null){
			BinaryTreeNode<T> l = t.getChild();
			t.setChild(l.getSiblings());
			l.setSiblings(t);
			return l;
		}else{
			return t;
		}

	}
}
