package com.wise.enums;

public enum States {

    PENDING(1, "待审核"),
    /** 伪状态 */
    MACHINE_AUDIT(2, "机审"),
//    MACHINE_AUDIT_PASSED(3, "机审通过"),
//    MACHINE_AUDIT_REFUSED(4, "机审不通过"),
    /** 伪状态 */
//    MANUAL_AUDIT(5, "人工审核"),
//    MANUAL_AUDIT_PASSED(6, "人工审核通过"),
//    MANUAL_AUDIT_REFUSED(7, "人工审核不通过"),
    UP(8, "上架"),
//    DOWN(9, "下架"),
    DESTROY(10, "销毁"),
    ;

    private Integer value;
    private String name;

    States(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

}
