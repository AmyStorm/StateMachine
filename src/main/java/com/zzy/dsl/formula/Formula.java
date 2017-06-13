package com.zzy.dsl.formula;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhengZiyu on 2017/4/11.
 */
public class Formula {
    private List<FormulaBlock> formulaBlockList = new ArrayList<FormulaBlock>();

    public List<FormulaBlock> getFormulaBlockList() {
        return formulaBlockList;
    }

    public void setFormulaBlockList(List<FormulaBlock> formulaBlockList) {
        this.formulaBlockList = formulaBlockList;
    }
}
