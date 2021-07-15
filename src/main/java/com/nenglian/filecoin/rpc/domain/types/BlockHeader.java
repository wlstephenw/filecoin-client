package com.nenglian.filecoin.rpc.domain.types;



import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.crypto.Signature;
import com.nenglian.filecoin.rpc.domain.proof.PoStProof;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class BlockHeader implements Serializable {

    private String miner; // 0

    private Ticket ticket; // 1

    private ElectionProof electionProof; // 2

    private List<BeaconEntry> beaconEntries; // 3

    private List<PoStProof> winPoStProof; // 4

    private List<Cid> parents; // 5

    private BigInteger parentWeight; // 6

    private Long height; // 7

    private Cid parentStateRoot; // 8

    private Cid parentMessageReceipts; // 9

    private Cid messages; //10

    private Signature bLSAggregate; // 11

    private Long timestamp; // 12

    private Signature blockSig; // 13

    private Long forkSignaling; // 14

    private BigInteger parentBaseFee; // 15

    private Boolean validated; // 16
}
