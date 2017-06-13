package com.zzy.dsl.formula;

import com.litb.v3.center.service.impl.procurement.dsl.grammar.Token;
import com.litb.v3.center.service.impl.procurement.dsl.tree.BinaryTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhengZiyu on 2017/4/12.
 */
public class FormulaCompiler {

    private static final Logger logger = LoggerFactory.getLogger(FormulaCompiler.class);
    private BinaryTreeNode<Token> tree;
    private Formula formula;
    public FormulaCompiler(BinaryTreeNode<Token> tree){
        this.tree = tree;
        this.formula = compile(tree);
    }

    private Formula compile(BinaryTreeNode<Token> tree){
        return caseFormula(tree);
    }


    private List<Token> preIterateBinaryTree(BinaryTreeNode<Token> binaryTreeNode, List<Token> result, Formula formula){
        if(result == null){
            result = new ArrayList<Token>();
        }
        result.add(binaryTreeNode.getValue());
        if(binaryTreeNode.getChild() != null){
            result.addAll(preIterateBinaryTree(binaryTreeNode.getChild(), result, formula));
        }
        if(binaryTreeNode.getSiblings() != null){
            result.addAll(preIterateBinaryTree(binaryTreeNode.getSiblings(), result, formula));
        }
        return result;
    }

    private Formula caseFormula(BinaryTreeNode<Token> binaryTreeNode){
        Formula formula = new Formula();
        BinaryTreeNode<Token> child = binaryTreeNode.getChild();
        List<FormulaBlock> list = new ArrayList<FormulaBlock>();
        while(child != null && child.getValue().getToken().equals("formulaBlock")){
            FormulaBlock formulaBlock = caseFormulaBlock(child);
            list.add(formulaBlock);
            child = child.getSiblings();
        }
        formula.setFormulaBlockList(list);
        return formula;
    }

    private FormulaBlock caseFormulaBlock(BinaryTreeNode<Token> binaryTreeNode){
        FormulaBlock formulaBlock = new FormulaBlock();
        BinaryTreeNode<Token> child = binaryTreeNode.getChild();
        if(child != null && child.getValue().getToken() != null){
            formulaBlock.setFormulaEnum(caseFormulaType(child));
            child = child.getSiblings();
        }
        if(child != null && child.getValue().getToken().equals("formulaBody")){
            formulaBlock.setFormulaBody(caseFormulaBody(child));
            child = child.getSiblings();
        }
        return formulaBlock;
    }

    private FormulaEnum caseFormulaType(BinaryTreeNode<Token> binaryTreeNode){
        return FormulaEnum.findValue(binaryTreeNode.getValue().getToken());
    }

    private FormulaBody caseFormulaBody(BinaryTreeNode<Token> binaryTreeNode){
        FormulaBody formulaBody = new FormulaBody();
        BinaryTreeNode<Token> child = binaryTreeNode.getChild();
        if(child != null && child.getValue().getToken() != null){
            formulaBody.setAdjustTypeEnum(caseAdjustTypeEnum(child));
            child = child.getSiblings();
        }
        List<FormulaCaseBody> list = new ArrayList<FormulaCaseBody>();
        while(child != null && child.getValue().getToken().equals("formulaCaseBody")){
            FormulaCaseBody formulaCaseBody = caseFormulaCaseBody(child);
            list.add(formulaCaseBody);
            child = child.getSiblings();
        }
        formulaBody.setFormulaCaseBodyList(list);
        return formulaBody;
    }
    private AdjustTypeEnum caseAdjustTypeEnum(BinaryTreeNode<Token> binaryTreeNode){
        return AdjustTypeEnum.findValue(binaryTreeNode.getValue().getToken());
    }
    private FormulaCaseBody caseFormulaCaseBody(BinaryTreeNode<Token> binaryTreeNode){
        FormulaCaseBody formulaCaseBody = new FormulaCaseBody();
        BinaryTreeNode<Token> child = binaryTreeNode.getChild();
        if(child != null && child.getValue().getToken().equals("formulaCaseBlock")){
            FormulaCaseBlock formulaCaseBlock = caseFormulaCaseBlock(child);
            formulaCaseBody.setFormulaCaseBlock(formulaCaseBlock);
            child = child.getSiblings();
        }
        if(child != null && child.getValue().getToken() != null){
            formulaCaseBody.setEffect(caseEffect(child));
            child = child.getSiblings();
        }
        return formulaCaseBody;
    }

    private FormulaCaseBlock caseFormulaCaseBlock(BinaryTreeNode<Token> binaryTreeNode){
        FormulaCaseBlock formulaCaseBlock = new FormulaCaseBlock();
        BinaryTreeNode<Token> child = binaryTreeNode.getChild();
        FormulaCaseExpression formulaCaseExpression = new FormulaCaseExpression();
        FormulaConditionTree formulaConditionTree = new FormulaConditionTree();
        if(child != null && child.getValue().getToken().equals("formulaCaseExpression")){
            formulaConditionTree = caseFormulaCaseExpression(child, formulaConditionTree);
            child = child.getSiblings();
        }
        while(child != null && child.getValue().getToken().equals("formulaCaseExpressionSub")){
            formulaConditionTree = caseFormulaCaseExpressionSub(child, formulaConditionTree);
            child = child.getSiblings();
        }
        formulaCaseExpression.setFormulaConditionTree(formulaConditionTree);
        formulaCaseBlock.setFormulaCaseExpression(formulaCaseExpression);
        return formulaCaseBlock;
    }

    private FormulaConditionTree caseFormulaCaseExpression(BinaryTreeNode<Token> binaryTreeNode, FormulaConditionTree formulaConditionTree){
        BinaryTreeNode<Token> child = binaryTreeNode.getChild();
        formulaConditionTree = generateFormulaConditionTree(child);
//        FormulaConditionTree left = new FormulaConditionTree();
//        FormulaConditionTree right = new FormulaConditionTree();
//        formulaConditionTree.setLeft(left);
//        formulaConditionTree.setRight(right);
        return formulaConditionTree;
    }


    private FormulaConditionTree caseFormulaCaseExpressionSub(BinaryTreeNode<Token> binaryTreeNode, FormulaConditionTree formulaConditionTree){

        BinaryTreeNode<Token> child = binaryTreeNode.getChild();
        if(child != null && child.getValue().getToken().equals("formulaCaseExpressionSub_body")){
            BinaryTreeNode<Token> values = child.getChild();
            Token relations = values.getValue();
            BinaryTreeNode<Token> sibling = values.getSiblings();
            if(sibling != null && sibling.getValue().getToken().equals("formulaCaseExpression")){
                if(formulaConditionTree == null){
                    formulaConditionTree = new FormulaConditionTree();
                }
                FormulaConditionTree newTree = generateFormulaConditionTree(sibling.getChild());
                if(newTree != null){
                    formulaConditionTree = formulaConditionTree.addFormulaConditionTree(FormulaRelationEnum.findValue(relations.getToken()), newTree);
                }
            }
        }
        return formulaConditionTree;
    }

    private FormulaConditionTree generateFormulaConditionTree(BinaryTreeNode<Token> binaryTreeNode){
        FormulaConditionTree formulaConditionTree = new FormulaConditionTree();
        Token value1 = null;
        Token value2 = null;
        Token value3 = null;
        BinaryTreeNode<Token> temp = binaryTreeNode;
        if(temp != null){
            value1 = temp.getValue();
            if(temp.getSiblings() != null){
                temp = temp.getSiblings();
                value2 = temp.getValue();
                if(temp.getSiblings() != null){
                    temp = temp.getSiblings();
                    value3 = temp.getValue();
                }
            }
        }
        FormulaCaseCondition formulaCaseCondition = null;
        if(value1 != null && value2 != null && value3 != null) {
            if("formulaRangeExpression".equals(value3.getToken())){
                BinaryTreeNode<Token> child = temp.getChild();
                Token range1 = null;
                Token range2 = null;
                Token range3 = null;
                Token range4 = null;
                Token range5 = null;
                range1 = child.getValue();
                if(child.getSiblings() != null){
                    child = child.getSiblings();
                    range2 = child.getValue();
                    if(child.getSiblings() != null){
                        child = child.getSiblings();
                        range3 = child.getValue();
                        if(child.getSiblings() != null){
                            child = child.getSiblings();
                            range4 = child.getValue();
                            if(child.getSiblings() != null){
                                child = child.getSiblings();
                                range5 = child.getValue();
                            }
                        }
                    }
                }
                if(range1 != null && range2 != null && range3 != null && range4 != null && range5 != null){
                    FormulaRangeEnum lToken = FormulaRangeEnum.findValue(range1.getToken());
                    FormulaRangeEnum rToken = FormulaRangeEnum.findValue(range5.getToken());
                    if(lToken != null && rToken != null){
                        formulaConditionTree.setConditionTreeNode(FormulaRelationEnum.AND);
                        FormulaConditionTree left = new FormulaConditionTree();
                        FormulaConditionTree right = new FormulaConditionTree();
                        if(lToken.equals(FormulaRangeEnum.GT)){
                            left.setConditionTreeNode(new FormulaCaseCondition(FormulaCaseKeyEnum.findValue(value1.getToken()), FormulaCaseRelationEnum.GT, new BigDecimal(range2.getToken())));
                        }else if(lToken.equals(FormulaRangeEnum.GE)){
                            left.setConditionTreeNode(new FormulaCaseCondition(FormulaCaseKeyEnum.findValue(value1.getToken()), FormulaCaseRelationEnum.GE, new BigDecimal(range2.getToken())));
                        }
                        formulaConditionTree.setLeft(left);
                        if(rToken.equals(FormulaRangeEnum.LT)){
                            right.setConditionTreeNode(new FormulaCaseCondition(FormulaCaseKeyEnum.findValue(value1.getToken()), FormulaCaseRelationEnum.LT, new BigDecimal(range4.getToken())));
                        }else if(rToken.equals(FormulaRangeEnum.LE)){
                            right.setConditionTreeNode(new FormulaCaseCondition(FormulaCaseKeyEnum.findValue(value1.getToken()), FormulaCaseRelationEnum.LE, new BigDecimal(range4.getToken())));
                        }
                        formulaConditionTree.setRight(right);
                    }else{
                        logger.error("error FormulaRangeExpression body");
                        return null;
                    }
                }else{
                    logger.error("error FormulaRangeExpression body");
                    return null;
                }
            }else{
                formulaCaseCondition = new FormulaCaseCondition(FormulaCaseKeyEnum.findValue(value1.getToken()), FormulaCaseRelationEnum.findValue(value2.getToken()), new BigDecimal(value3.getToken()));
                formulaConditionTree.setConditionTreeNode(formulaCaseCondition);
            }
        }else{
            logger.error("error FormulaCaseExpression body");
            return null;
        }
        return formulaConditionTree;
    }
    private String caseEffect(BinaryTreeNode<Token> binaryTreeNode){
        return binaryTreeNode.getValue().getToken();
    }
    public BinaryTreeNode<Token> getTree() {
        return tree;
    }

    public Formula getFormula() {
        return formula;
    }
}
