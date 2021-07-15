package com.nenglian.filecoin.rpc.domain.types;


import com.nenglian.filecoin.rpc.domain.crypto.Signature;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author stephen
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignedMessage implements Serializable {

    private Message message;

    private Signature signature;
}
