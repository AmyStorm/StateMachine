package com.zzy.dsl.formula;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhengZiyu on 2017/5/31.
 */
public class FormulaBody {
    private AdjustTypeEnum adjustTypeEnum;
    private List<FormulaCaseBody> formulaCaseBodyList = new ArrayList<FormulaCaseBody>();

    public AdjustTypeEnum getAdjustTypeEnum() {
        return adjustTypeEnum;
    }

    public void setAdjustTypeEnum(AdjustTypeEnum adjustTypeEnum) {
        this.adjustTypeEnum = adjustTypeEnum;
    }

    public List<FormulaCaseBody> getFormulaCaseBodyList() {
        return formulaCaseBodyList;
    }

    public void setFormulaCaseBodyList(List<FormulaCaseBody> formulaCaseBodyList) {
        this.formulaCaseBodyList = formulaCaseBodyList;
    }
}
