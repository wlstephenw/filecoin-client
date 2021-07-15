package com.nenglian.filecoin.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author stephen
 * @since 2021/7/13 下午4:53
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResult {
    int code;
    String msg;
    Object object;
}
