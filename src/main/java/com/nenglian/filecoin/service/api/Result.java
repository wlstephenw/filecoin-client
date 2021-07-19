package com.nenglian.filecoin.service.api;

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
public class Result<T> {
    int code;
    String msg;
    T data;
}
