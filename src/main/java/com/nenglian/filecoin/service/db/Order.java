package com.nenglian.filecoin.service.db;

import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.service.TransferStatus;
import com.nenglian.filecoin.service.api.Transfer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * @author stephen
 * @since 2021/7/15 下午4:12
 */

// TODO we do not want have this to be saved...
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    String id;
    String reqId;
    Transfer transfer;
    Message gasMessage;
    TransferStatus status;
    String txId;
    String gasTxId;
}
