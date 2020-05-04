package com.wise.bean;

import lombok.Data;

/**
 * 审核内容
 *
 * @author lingyuwang
 * @date 2020-05-03 23:49
 * @since 1.0.9
 */
@Data
public class AuditContent {

    /** 内容ID */
    private Long contentId;

    /** 文本内容 */
    private String textContent;

}
