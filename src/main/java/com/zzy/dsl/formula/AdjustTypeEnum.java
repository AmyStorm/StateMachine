package com.zzy.dsl.formula;

/**
 * Created by ZhengZiyu on 2017/4/10.
 */
public enum AdjustTypeEnum {
    BY_RATE("按比例提高"),BY_ADD("累加");

    private String desc;
    AdjustTypeEnum(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }


    public static AdjustTypeEnum findValue(String value){
        for(AdjustTypeEnum adjustTypeEnum : AdjustTypeEnum.values()){
            if(adjustTypeEnum.getDesc().equals(value)){
                return adjustTypeEnum;
            }
        }
        return null;
    }
}
