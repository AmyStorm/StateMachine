package com.zzy.dsl.formula;

/**
 * Created by ZhengZiyu on 2017/4/11.
 */
public enum FormulaRelationEnum implements ConditionTreeNode{
    AND("&") {
        @Override
        public Boolean conditionResult(FormulaConditionTree left, FormulaConditionTree right) {
            return left.getConditionTreeNode().conditionResult(left.getLeft(),left.getRight()) &&
                    right.getConditionTreeNode().conditionResult(right.getLeft(), right.getRight());
        }
    }, OR("|") {
        @Override
        public Boolean conditionResult(FormulaConditionTree left, FormulaConditionTree right) {
            return left.getConditionTreeNode().conditionResult(left.getLeft(),left.getRight()) ||
                    right.getConditionTreeNode().conditionResult(right.getLeft(), right.getRight());
        }
    };

    private String desc;
    FormulaRelationEnum(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }


    public static FormulaRelationEnum findValue(String value){
        for(FormulaRelationEnum formulaRelationEnum : FormulaRelationEnum.values()){
            if(formulaRelationEnum.getDesc().equals(value)){
                return formulaRelationEnum;
            }
        }
        return null;
    }

}
