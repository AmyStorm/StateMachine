package com.zzy.dsl.formula;

/**
 * Created by ZhengZiyu on 2017/6/2.
 */
public interface ConditionTreeNode{
    Boolean conditionResult(FormulaConditionTree left, FormulaConditionTree right);
}
