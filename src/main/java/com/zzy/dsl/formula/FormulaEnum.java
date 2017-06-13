package com.zzy.dsl.formula;

/**
 * Created by ZhengZiyu on 2017/4/10.
 */
public enum FormulaEnum {
    PROCUREMENT_PRICE("采购价");

    private String desc;
    FormulaEnum(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }


    public static FormulaEnum findValue(String value){
        for(FormulaEnum formulaEnum : FormulaEnum.values()){
            if(formulaEnum.getDesc().equals(value)){
                return formulaEnum;
            }
        }
        return null;
    }
}
