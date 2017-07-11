package com.zzy.dsl;

import com.zzy.dsl.formula.Formula;
import com.zzy.dsl.formula.FormulaCompiler;
import com.zzy.dsl.grammar.Grammar;
import com.zzy.dsl.grammar.Token;
import com.zzy.dsl.grammar.TokenContainer;
import com.zzy.dsl.tree.BinaryTreeNode;
import com.zzy.dsl.tree.TreeNode;
import com.zzy.dsl.tree.TreeNodeUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

/**
 * 公式模板：
 * formula 采购价
 * type 按比例提高
 * case 仓库=19 & 数量 in (5,20] then -1%
 * case 仓库=17 & 数量 in (10,20] then -2%
 * end
 * formula 采购价
 * type 累加
 * case 仓库=11 then 14
 * end
 * Created by ZhengZiyu on 2017/3/22.
 */
public class PriceStrategyAnalysis {

    private String expression;

    /**
     * formula : formulaBlock+
     * formulaBlock : formulaStartKeyword formulaEffect formulaBody formulaEndKeyword
     * formulaEffect: Identifier
     * formulaBody formulaTypeKeyword formulaTypeName formulaCaseBody+
     * formulaCaseBody: formulaCaseStartKeyword formulaCaseBlock formulaCaseThenKeyword formulaCaseEffect
     * formulaCaseBlock: formulaCaseExpression formulaCaseExpressionSub
     * formulaCaseExpressionSub: ('|' | '&' formulaCaseExpression)*
     * formulaCaseExpression: (Identifier '>' | '<' | '=' | '>=' | '<=' Identifier) | (Identifier 'in' formulaRangeExpression)
     * formulaRangeExpression: '(' | '[' Identifier ',' Identifier ')'| ']'
     * formulaCaseEffect: Identifier
     * formulaEndKeyword: Identifier
     */
    private Grammar grammar;
    public PriceStrategyAnalysis(String expression){
        this.expression = expression;
        grammar = generateGrammar(expression);
    }

    private Grammar generateGrammar(String expression){
        BufferedReader reader = new BufferedReader(new StringReader(expression));
        int value;
        StringBuilder builder = new StringBuilder();
        LinkedList<Token> linkedList = new LinkedList<Token>();
        try {
            while((value =  reader.read()) != -1){
                char c = (char) value;
                if(Character.isWhitespace(c) || value == 13 || value == 10){
                    String identifier = builder.toString();
                    if(StringUtils.isNotBlank(identifier)){
                        linkedList.add(new Token(identifier));
                        builder.delete(0, builder.length());
                    }
                }else{
                    if(c == '(' || c == ')' || c == '[' || c == ']' || c == ',' || c == '='){
                        String identifier = builder.toString();
                        if(StringUtils.isNotBlank(identifier)) {
                            linkedList.add(new Token(identifier));
                            builder.delete(0, builder.length());
                        }
                        builder.append(c);
                        identifier = builder.toString();
                        linkedList.add(new Token(identifier));
                        builder.delete(0, builder.length());
                    }else{
                        builder.append(c);
                        String identifier = builder.toString();
                        if(identifier.equals(">=") || identifier.equals("<=")){
                            linkedList.add(new Token(identifier));
                            builder.delete(0, builder.length());
                        }
                    }
                }
            }
            linkedList.add(new Token(builder.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Grammar(new TokenContainer(linkedList));
    }

    public String getExpression() {
        return expression;
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public static void main(String[] args) {
        PriceStrategyAnalysis p = new PriceStrategyAnalysis(" formula 采购价\n" +
                "  type 按比例提高\n" +
                "  case 仓库=19 | 仓库=17 & 数量 in (5,20] then -1%\n" +
                "  case 仓库=17 & 数量 in (10,20] then -2%\n" +
                "  end\n" +
                "  formula 采购价\n" +
                "  type 累加\n" +
                "  case 仓库=11 then 14\n" +
                "  end");
        System.out.println(p.getGrammar().getTree());
        TreeNode<Token> tree = p.getGrammar().getTree();
        BinaryTreeNode<Token> roots = TreeNodeUtil.convertToAST(tree.getChildren(), new BinaryTreeNode<Token>(tree.getValue()));
//        List<Token> result = new ArrayList<Token>();
//        TreeNodeUtil.post(p.getGrammar().getTree(), result);
        FormulaCompiler formulaCompiler = new FormulaCompiler(roots);
        Formula formula = formulaCompiler.getFormula();
        System.out.println(formula);
    }
}
