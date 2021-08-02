package com.nenglian.filecoin.service.api;

import java.math.BigInteger;
import lombok.Builder;
import lombok.Data;

/**
 * @author stephen
 * @since 2021/8/2 上午10:04
 */

@Data
@Builder
public class EstimatedGas {
    BigInteger gas;
    String data;
}
