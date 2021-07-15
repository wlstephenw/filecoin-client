package com.nenglian.filecoin.rpc.domain.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author stephen
 * @since 2021/7/14 下午12:37
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeyInfo {

/*    "Type": "bls",
    "PrivateKey": "Ynl0ZSBhcnJheQ=="*/

    String Type;
    String PrivateKey;

}
