package com.zzy.dsl.formula;

/**
 * Created by ZhengZiyu on 2017/4/11.
 */
public enum FormulaCaseKeyEnum {

    WAREHOUSE("仓库"), QUANTITY("数量");

    private String desc;
    FormulaCaseKeyEnum(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }


    public static FormulaCaseKeyEnum findValue(String value){
        for(FormulaCaseKeyEnum formulaCaseKeyEnum : FormulaCaseKeyEnum.values()){
            if(formulaCaseKeyEnum.getDesc().equals(value)){
                return formulaCaseKeyEnum;
            }
        }
        return null;
    }
}
