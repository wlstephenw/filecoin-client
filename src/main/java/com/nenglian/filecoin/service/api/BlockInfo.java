package com.nenglian.filecoin.service.api;

import java.util.Date;
import lombok.Builder;
import lombok.Data;

/**
 * @author stephen
 * @since 2021/7/19 下午12:49
 */

@Data
@Builder
public class BlockInfo {
    Long height;
    Date time;
}
