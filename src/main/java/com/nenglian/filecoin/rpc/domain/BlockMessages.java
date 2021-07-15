package com.nenglian.filecoin.rpc.domain;



import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import java.util.List;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class BlockMessages {

    private List<com.nenglian.filecoin.rpc.domain.types.Message> blsMessages;

    private List<SignedMessage> secpkMessages;

    private List<Cid> cids;
}
