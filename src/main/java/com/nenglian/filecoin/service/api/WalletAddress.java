package com.nenglian.filecoin.service.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author stephen
 * @since 2021/7/19 上午11:16
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletAddress{
    String pubKey;
    String address;
}
