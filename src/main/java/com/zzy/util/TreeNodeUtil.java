package com.zzy.util;

import java.util.ArrayList;
import java.util.HashSet;
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
	 * @param fid
	 * @param source
	 * @param root
	 * @return
	 */
	public static <T extends TreeEntity> Set<TreeNode<T>> findChild(Long fid, List<T> source, TreeNode<T> root){
		Set<TreeNode<T>> nodes = new HashSet<TreeNode<T>>();
		for(T bean : source){
			if(bean.fetchFatherIdentity() == fid){
				nodes.add(new TreeNode<T>(bean));
			}
		}
		
		for(TreeNode<T> node : nodes){
			Set<TreeNode<T>> children = findChild(node.getValue().fetchIdentity(), source, node);
			node.setChildren(children);
		}
		return nodes;
	}
	
	/**
	 * 后序遍历node节点
	 * @param node
	 */
	public static <T extends TreeEntity> void post(TreeNode<T> node, List<T> result){
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
	public static <T extends TreeEntity> List<T> postIterationResult(List<T> source, T root){
		List<T> result = new ArrayList<T>();
		TreeNode<T> rootTreeNode = new TreeNode<T>(root);
		rootTreeNode.setChildren(findChild(rootTreeNode.getValue().fetchIdentity(), source, rootTreeNode));
		post(rootTreeNode, result);
		return result;
	}
}
