package com.nenglian.filecoin.rpc.cid;

import cn.hutool.core.util.HexUtil;
import com.nenglian.filecoin.rpc.cid.Cid.Codec;
import io.ipfs.multihash.*;
import io.ipfs.multihash.Multihash.Type;
import org.junit.*;
import io.ipfs.multibase.*;

import java.io.*;
import java.security.*;
import java.util.*;

public class CidTest {

    @Test
    public void buildCid() throws IOException {
        String cidHexHash = "3729c301569669ab02fb8c15b7490de96377ada3273e5c86d244d314ce71212d";
        String example = "bafy2bzaceblwrro34tjqxrsflu2iv33v4pkpxauhgxfqn5m6t3txeuj3hunka";
        String example1 = "bafy2bzaceb3bvkbjgcj6ltjmkpx7uwa52jbpmpt3jdgrvqgm2wzhu44tkrwba";

        Cid cid1 = Cid.buildCidV1(Codec.DagCbor, Type.blake2b_256, HexUtil.decodeHex(cidHexHash));
        String s = cid1.toString();

        Cid cid = Cid.decode(example);
        String encoded = cid.toString();
        if (!encoded.equals(example))
            throw new IllegalStateException("Incorrect cid string! " + example + " => " + encoded);
    }

    @Test
    public void validStrings() throws IOException {
        List<String> examples = Arrays.asList(
                "QmPZ9gcCEpqKTo6aq61g2nXGUhM4iCL3ewB6LDXZCtioEB",
                "QmatmE9msSfkKxoffpHwNLNKgwZG8eT9Bud6YoPab52vpy",
                "zdpuAyvkgEDQm9TenwGkd5eNaosSxjgEYd8QatfPetgB1CdEZ"
        );
        for (String example: examples) {
            Cid cid = Cid.decode(example);
            String encoded = cid.toString();
            if (!encoded.equals(example))
                throw new IllegalStateException("Incorrect cid string! " + example + " => " + encoded);
        }
    }

    @Test
    public void emptyStringShouldFail() throws IOException {
        try {
            Cid cid = Cid.decode("");
            throw new RuntimeException();
        } catch (IllegalStateException e) {}
    }

    @Test
    public void basicMarshalling() throws Exception {
        MessageDigest hasher = MessageDigest.getInstance("SHA-512");
        byte[] hash = hasher.digest("TEST".getBytes());

        Cid cid = new Cid(1, Cid.Codec.Raw, Multihash.Type.sha2_512, hash);
        byte[] data = cid.toBytes();

        Cid cast = Cid.cast(data);
        Assert.assertTrue("Invertible serialization", cast.equals(cid));

        Cid fromString = Cid.decode(cid.toString());
        Assert.assertTrue("Invertible toString", fromString.equals(cid));
    }

    @Test
    public void version0Handling() throws Exception {
        String hashString = "QmPZ9gcCEpqKTo6aq61g2nXGUhM4iCL3ewB6LDXZCtioEB";
        Cid cid = Cid.decode(hashString);

        Assert.assertTrue("version 0", cid.version == 0);

        Assert.assertTrue("Correct hash", cid.toString().equals(hashString));
    }

    @Test
    public void version0Error() throws Exception {
        String invalidString = "QmdfTbBqBPQ7VNxZEYEj14VmRuZBkqFbiwReogJgS1zIII";
        try {
            Cid cid = Cid.decode(invalidString);
            throw new RuntimeException();
        } catch (IllegalStateException e) {}
    }
}
