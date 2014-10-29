package com.zzy.util;

/**
 * 需要用到TreeNode工具转换的实体需要实现此接口
 * @author ZhengZiyu
 *
 */
public interface TreeEntity {
	Long fetchIdentity();
	
	Long fetchFatherIdentity();
}
