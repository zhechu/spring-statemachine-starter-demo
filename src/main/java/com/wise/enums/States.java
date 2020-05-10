package com.wise.enums;

import lombok.Getter;

public enum States {

    PENDING(1, "待审核"),
    /** 伪状态 */
    MACHINE_AUDIT(2, "机审"),
    MACHINE_AUDIT_PASSED(3, "机审通过"),
    MACHINE_AUDIT_REFUSED(4, "机审不通过"),
    /** 伪状态 */
    MANUAL_AUDIT(5, "人工审核"),
    MANUAL_AUDIT_PASSED(6, "人工审核通过"),
    MANUAL_AUDIT_REFUSED(7, "人工审核不通过"),
    UP(8, "上架"),
    DOWN(9, "下架"),
    DESTROY(10, "销毁"),
    ;

    @Getter
    private Integer code;

    private String name;

    States(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据编码获取状态枚举
     *
     * @param code
     * @return com.wise.enums.States
     * @author lingyuwang
     * @date 2020-05-10 11:21
     * @since 1.0.9
     */
    public static States getByCode(Integer code){
        for (States s : States.values()) {
            if(s.code.equals(code)){
                return s;
            }
        }
        return null;
    }

}
