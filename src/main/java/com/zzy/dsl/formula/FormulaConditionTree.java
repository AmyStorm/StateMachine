package com.zzy.dsl.formula;


/**
 * Created by ZhengZiyu on 2017/6/2.
 */
public class FormulaConditionTree {
    private ConditionTreeNode conditionTreeNode;
    private FormulaConditionTree left;
    private FormulaConditionTree right;

    public FormulaConditionTree() {
    }

    public FormulaConditionTree(ConditionTreeNode conditionTreeNode, FormulaConditionTree left, FormulaConditionTree right) {
        this.conditionTreeNode = conditionTreeNode;
        this.left = left;
        this.right = right;
    }
    public boolean calculate(){
        return conditionTreeNode.conditionResult(left, right);
    }

    public ConditionTreeNode getConditionTreeNode() {
        return conditionTreeNode;
    }

    public FormulaConditionTree addFormulaConditionTree(FormulaRelationEnum formulaRelationEnum, FormulaConditionTree formulaConditionTree){
        FormulaConditionTree temp = new FormulaConditionTree();
        ConditionTreeNode conditionTreeNode = null;
        if(this.left != null && this.right != null && this.conditionTreeNode != null){
            if(formulaRelationEnum == null){
                conditionTreeNode = FormulaRelationEnum.AND;
            }else{
                conditionTreeNode = formulaRelationEnum;
            }
            temp.setConditionTreeNode(conditionTreeNode);
            temp.setLeft(this);
            temp.setRight(formulaConditionTree);
        }else{
            if(formulaRelationEnum != null){
                temp.left = this;
                temp.right = formulaConditionTree;
                temp.conditionTreeNode = formulaRelationEnum;
            }else{
                temp.conditionTreeNode = formulaConditionTree.getConditionTreeNode();
            }
        }
        return temp;
    }

    public void setConditionTreeNode(ConditionTreeNode conditionTreeNode) {
        this.conditionTreeNode = conditionTreeNode;
    }

    public FormulaConditionTree getLeft() {
        return left;
    }

    public void setLeft(FormulaConditionTree left) {
        this.left = left;
    }

    public FormulaConditionTree getRight() {
        return right;
    }

    public void setRight(FormulaConditionTree right) {
        this.right = right;
    }

//    public static void main(String[] args) {
//        FormulaConditionTree formulaConditionTree = new FormulaConditionTree();
//        formulaConditionTree.setConditionTreeNode(FormulaRelationEnum.AND);
//        formulaConditionTree.setLeft(new FormulaConditionTree(FormulaRelationEnum.AND, new FormulaConditionTree(new FormulaCaseCondition(FormulaCaseKeyEnum.QUANTITY, FormulaCaseRelationEnum.EQ, new BigDecimal(32)), null, null), new FormulaConditionTree(new FormulaCaseCondition(FormulaCaseKeyEnum.QUANTITY, FormulaCaseRelationEnum.EQ, new BigDecimal(19)), null, null)));
//        formulaConditionTree.setRight(new FormulaConditionTree(new FormulaCaseCondition(FormulaCaseKeyEnum.QUANTITY, FormulaCaseRelationEnum.EQ, new BigDecimal(12)), null, null));
//        System.out.println(formulaConditionTree.calculate());
//    }

}
