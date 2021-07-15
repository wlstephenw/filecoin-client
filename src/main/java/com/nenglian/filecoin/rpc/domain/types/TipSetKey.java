package com.nenglian.filecoin.rpc.domain.types;

import com.nenglian.filecoin.rpc.domain.cid.Cid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author stephen
 */
public class TipSetKey extends ArrayList<Cid> implements Serializable {

    public static TipSetKey of(Collection<Cid> cids) {
        TipSetKey result = new TipSetKey();
        result.addAll(cids);
        return result;
    }

    public static TipSetKey of(Cid... cids) {
        TipSetKey result = new TipSetKey();
        result.addAll(Arrays.asList(cids));
        return result;
    }

    public static TipSetKey of(String... cids) {
        TipSetKey result = new TipSetKey();
        for (String cid : cids) {
            result.add(Cid.of(cid));
        }
        return result;
    }
}
