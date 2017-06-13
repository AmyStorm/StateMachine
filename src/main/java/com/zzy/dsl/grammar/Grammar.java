package com.zzy.dsl.grammar;

import com.litb.v3.center.service.impl.procurement.dsl.formula.FormulaCaseRelationEnum;
import com.litb.v3.center.service.impl.procurement.dsl.formula.FormulaRelationEnum;
import com.litb.v3.center.service.impl.procurement.dsl.tree.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhengZiyu on 2017/3/25.
 */
public class Grammar {

    private static final Logger logger = LoggerFactory.getLogger(Grammar.class);
    private static final String FORMULA_START_KEYWORD = "formula";
    private static final String FORMULA_END_KEYWORD = "end";
    private static final String FORMULA_TYPE_KEYWORD = "type";
    private static final String FORMULA_CASE_START_KEYWORD = "case";
    private static final String FORMULA_CASE_THEN_KEYWORD = "then";
    private static final String IN = "in";
    private static final String LGT = "(";
    private static final String RGT = ")";
    private static final String LGE = "[";
    private static final String RGE = "]";
    private static final String SEP = ",";
    public static final List<Token> usedToken = new ArrayList<Token>();
    public static final List<Token> hiddenChild = new ArrayList<Token>();
    static {
        usedToken.add(new Token("root"));
//        usedToken.add(new Token("formula"));
//        usedToken.add(new Token("formulaBlock"));
        usedToken.add(new Token("formulaStartKeyword"));
//        usedToken.add(new Token("formulaBody"));
        usedToken.add(new Token("formulaEndKeyword"));
        usedToken.add(new Token("formulaTypeKeyword"));
//        usedToken.add(new Token("formulaCaseBody"));
        usedToken.add(new Token("formulaCaseStartKeyword"));
//        usedToken.add(new Token("formulaCaseBlock"));
        usedToken.add(new Token("formulaCaseThenKeyword"));
//        usedToken.add(new Token("formulaCaseExpression"));
//        usedToken.add(new Token("formulaCaseExpressionSub"));
//        usedToken.add(new Token("formulaCaseExpressionSub_body"));
//        usedToken.add(new Token("formulaRangeExpression"));
        hiddenChild.add(new Token("formulaCaseExpressionSub"));
        hiddenChild.add(new Token("formulaCaseExpressionSub_body"));
    }

    private TokenContainer tokenContainer;

    private TreeNode<Token> tree;

    public Grammar(TokenContainer tokenContainer){
        this.tokenContainer = tokenContainer;
        tree = new TreeNode<Token>(new Token("root"));
        if(!formula(tree)){
            throw new RuntimeException("grammar compiles failed");
        }
    }
    public boolean formula(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(new Token("formula"));
        fNode.addChild(child);
        boolean success = formulaBlock(child);
        if(success){
            while(true){
                if (!formulaBlock(child)) {
                    break;
                }
            }
        }

        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean formulaBlock(TreeNode<Token> fNode) {
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(new Token("formulaBlock"));
        fNode.addChild(child);
        boolean success = formulaStartKeyword(child) && formulaEffect(child) && formulaBody(child) && formulaEndKeyword(child);
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean formulaStartKeyword(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(new Token("formulaStartKeyword"));
        fNode.addChild(child);
        boolean isEq = FORMULA_START_KEYWORD.equals(tokenContainer.getCurrentToken().getToken());
        boolean success = isEq && tokenContainer.popToken();
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }

    private boolean formulaEffect(TreeNode<Token> fNode){
        return popIdentifier(fNode);
    }

    private boolean formulaBody(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(new Token("formulaBody"));
        fNode.addChild(child);
        boolean success = formulaTypeKeyword(child) && formulaTypeName(child);
        if(success){
            success = formulaCaseBody(child);
            if(success){
                while(true){
                    if(!formulaCaseBody(child)){
                        break;
                    }
                }
            }

        }
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean formulaEndKeyword(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(new Token("formulaEndKeyword"));
        fNode.addChild(child);
        boolean isEq = FORMULA_END_KEYWORD.equals(tokenContainer.getCurrentToken().getToken());
        boolean success = isEq && tokenContainer.popToken();
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }

    private boolean formulaTypeKeyword(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(new Token("formulaTypeKeyword"));
        fNode.addChild(child);
        boolean isEq = FORMULA_TYPE_KEYWORD.equals(tokenContainer.getCurrentToken().getToken());
        boolean success = isEq && tokenContainer.popToken();
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }

    private boolean formulaTypeName(TreeNode<Token> fNode){
        return popIdentifier(fNode);
    }
    private boolean formulaCaseBody(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(new Token("formulaCaseBody"));
        fNode.addChild(child);
        boolean success = formulaCaseStartKeyword(child) && formulaCaseBlock(child) && formulaCaseThenKeyword(child) && formulaCaseEffect(child);
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean formulaCaseStartKeyword(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(new Token("formulaCaseStartKeyword"));
        fNode.addChild(child);
        boolean isEq = FORMULA_CASE_START_KEYWORD.equals(tokenContainer.getCurrentToken().getToken());
        boolean success = isEq && tokenContainer.popToken();
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean formulaCaseBlock(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(new Token("formulaCaseBlock"));
        fNode.addChild(child);
        boolean success = formulaCaseExpression(child) && formulaCaseExpressionSub(child);
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean formulaCaseThenKeyword(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(new Token("formulaCaseThenKeyword"));
        fNode.addChild(child);
        boolean isEq = FORMULA_CASE_THEN_KEYWORD.equals(tokenContainer.getCurrentToken().getToken());
        boolean success = isEq && tokenContainer.popToken();
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean formulaCaseEffect(TreeNode<Token> fNode){
        return popIdentifier(fNode);
    }

    private boolean formulaCaseExpression_1(TreeNode<Token> fNode){
        return popIdentifier(fNode);
    }
    private boolean formulaCaseExpression_2(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(tokenContainer.getCurrentToken());
        fNode.addChild(child);
        boolean success = (FormulaCaseRelationEnum.GE.getDesc().equals(tokenContainer.getCurrentToken().getToken()) || FormulaCaseRelationEnum.LE.getDesc().equals(tokenContainer.getCurrentToken().getToken())
                || FormulaCaseRelationEnum.GT.getDesc().equals(tokenContainer.getCurrentToken().getToken()) || FormulaCaseRelationEnum.LT.getDesc().equals(tokenContainer.getCurrentToken().getToken())
                || FormulaCaseRelationEnum.EQ.getDesc().equals(tokenContainer.getCurrentToken().getToken())) && tokenContainer.popToken();
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean formulaCaseExpression_3(TreeNode<Token> fNode){
        return popIdentifier(fNode);
    }
    private boolean formulaCaseExpression_4(TreeNode<Token> fNode){
        return popIdentifier(fNode);
    }
    private boolean formulaCaseExpression_5(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(tokenContainer.getCurrentToken());
        fNode.addChild(child);
        boolean success = IN.equals(tokenContainer.getCurrentToken().getToken()) && tokenContainer.popToken();
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean formulaCaseExpression(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(new Token("formulaCaseExpression"));
        fNode.addChild(child);
        boolean success;
        if(formulaCaseExpression_1(child) && formulaCaseExpression_2(child) && formulaCaseExpression_3(child)){
            success = true;
        }else {
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
            child = new TreeNode<Token>(new Token("formulaCaseExpression"));
            fNode.addChild(child);

            if(formulaCaseExpression_4(child) && formulaCaseExpression_5(child)
                    && formulaRangeExpression(child)){
                success = true;
            }else{
                success = false;
            }
        }
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean formulaCaseExpressionSub(TreeNode<Token> fNode){

        while(true){
            int save = tokenContainer.getCurrentIndex();
            TreeNode<Token> child = new TreeNode<Token>(new Token("formulaCaseExpressionSub"));
            fNode.addChild(child);
            boolean success = formulaCaseExpressionSub_body(child);
            if(!success){
                fNode.removeChild(child);
                tokenContainer.resetToIndex(save);
                break;
            }
        }
        return true;
    }

    //'|' | '&' formulaCaseExpression
    private boolean formulaCaseExpressionSub_body(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(new Token("formulaCaseExpressionSub_body"));
        fNode.addChild(child);
        boolean success = formulaCaseExpressionSub_body_1(child) && formulaCaseExpression(child);
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean formulaCaseExpressionSub_body_1(TreeNode<Token> fNode) {
        int save = tokenContainer.getCurrentIndex();
        //tokenContainer.getCurrentToken().getToken() AND OR
        TreeNode<Token> child = new TreeNode<Token>(new Token(tokenContainer.getCurrentToken().getToken()));
        fNode.addChild(child);
        boolean success = (FormulaRelationEnum.AND.getDesc().equals(tokenContainer.getCurrentToken().getToken()) ||
                FormulaRelationEnum.OR.getDesc().equals(tokenContainer.getCurrentToken().getToken())) && tokenContainer.popToken();
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean formulaRangeExpression(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(new Token("formulaRangeExpression"));
        fNode.addChild(child);
        boolean success = formulaRangeExpression_1(child) &&
                formulaRangeExpression_2(child) &&
                formulaRangeExpression_3(child) &&
                formulaRangeExpression_4(child) &&
                formulaRangeExpression_5(child);
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean formulaRangeExpression_1(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(tokenContainer.getCurrentToken());
        fNode.addChild(child);
        boolean success = (LGT.equals(tokenContainer.getCurrentToken().getToken()) || LGE.equals(tokenContainer.getCurrentToken().getToken())) && tokenContainer.popToken();
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }

    private boolean formulaRangeExpression_2(TreeNode<Token> fNode){
        return popIdentifier(fNode);
    }
    private boolean formulaRangeExpression_3(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(tokenContainer.getCurrentToken());
        fNode.addChild(child);
        boolean success = SEP.equals(tokenContainer.getCurrentToken().getToken()) && tokenContainer.popToken();
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean formulaRangeExpression_4(TreeNode<Token> fNode){
        return popIdentifier(fNode);
    }

    private boolean formulaRangeExpression_5(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        TreeNode<Token> child = new TreeNode<Token>(tokenContainer.getCurrentToken());
        fNode.addChild(child);
        boolean success = (RGT.equals(tokenContainer.getCurrentToken().getToken()) || RGE.equals(tokenContainer.getCurrentToken().getToken())) && tokenContainer.popToken();
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }
    private boolean popIdentifier(TreeNode<Token> fNode){
        int save = tokenContainer.getCurrentIndex();
        Token token = tokenContainer.getCurrentToken();
        TreeNode<Token> child = new TreeNode<Token>(token);
        fNode.addChild(child);
        boolean success = tokenContainer.popToken();
        if(!success){
            fNode.removeChild(child);
            tokenContainer.resetToIndex(save);
        }
        return success;
    }

    public TreeNode<Token> getTree() {
        return tree;
    }
}
