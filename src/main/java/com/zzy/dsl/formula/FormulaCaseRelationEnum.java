package com.zzy.dsl.formula;

/**
 * Created by ZhengZiyu on 2017/4/11.
 */
public enum FormulaCaseRelationEnum {

    GT(">"),LT ("<"),GE(">="),LE("<="),EQ("=");

    private String desc;
    FormulaCaseRelationEnum(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }


    public static FormulaCaseRelationEnum findValue(String value){
        for(FormulaCaseRelationEnum formulaCaseRelationEnum : FormulaCaseRelationEnum.values()){
            if(formulaCaseRelationEnum.getDesc().equals(value)){
                return formulaCaseRelationEnum;
            }
        }
        return null;
    }
}
