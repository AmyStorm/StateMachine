package com.zzy.dsl.formula;

/**
 * Created by ZhengZiyu on 2017/6/11.
 */
public enum FormulaRangeEnum {
    GT("("),GE("["),LT(")"),LE("]");

    private String desc;
    FormulaRangeEnum(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }


    public static FormulaRangeEnum findValue(String value){
        for(FormulaRangeEnum formulaRangeEnum : FormulaRangeEnum.values()){
            if(formulaRangeEnum.getDesc().equals(value)){
                return formulaRangeEnum;
            }
        }
        return null;
    }
}
