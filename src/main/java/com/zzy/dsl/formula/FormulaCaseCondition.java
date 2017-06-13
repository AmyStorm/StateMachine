package com.zzy.dsl.formula;

import java.math.BigDecimal;

/**
 * Created by ZhengZiyu on 2017/6/1.
 */
public class FormulaCaseCondition implements ConditionTreeNode{

    private FormulaCaseKeyEnum formulaCaseKeyEnum;
    private FormulaCaseRelationEnum formulaCaseRelationEnum;
    private BigDecimal value;

    public FormulaCaseCondition(FormulaCaseKeyEnum formulaCaseKeyEnum, FormulaCaseRelationEnum formulaCaseRelationEnum, BigDecimal value) {
        this.formulaCaseKeyEnum = formulaCaseKeyEnum;
        this.formulaCaseRelationEnum = formulaCaseRelationEnum;
        this.value = value;
    }

    @Override
    public Boolean conditionResult(FormulaConditionTree left, FormulaConditionTree right) {
        //TODO
        return value.compareTo(new BigDecimal(0)) > 0;
    }


    public FormulaCaseKeyEnum getFormulaCaseKeyEnum() {
        return formulaCaseKeyEnum;
    }

    public void setFormulaCaseKeyEnum(FormulaCaseKeyEnum formulaCaseKeyEnum) {
        this.formulaCaseKeyEnum = formulaCaseKeyEnum;
    }

    public FormulaCaseRelationEnum getFormulaCaseRelationEnum() {
        return formulaCaseRelationEnum;
    }

    public void setFormulaCaseRelationEnum(FormulaCaseRelationEnum formulaCaseRelationEnum) {
        this.formulaCaseRelationEnum = formulaCaseRelationEnum;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
