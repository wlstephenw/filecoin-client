package com.nenglian.filecoin.rpc.api;


import com.nenglian.filecoin.rpc.domain.BlockMessages;
import com.nenglian.filecoin.rpc.domain.MsgLookup;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.*;
import com.nenglian.filecoin.rpc.jasonrpc.*;
import com.nenglian.filecoin.rpc.jasonrpc.Callback;
import com.nenglian.filecoin.rpc.jasonrpc.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author stephen
 */
public class LotusChainAPITest extends AbstractLotusAPITest {

    private final LotusChainAPI lotusChainAPI = lotusAPIFactory.createLotusChainAPI();

    private final LotusStateAPI lotusStateAPI = lotusAPIFactory.createLotusStateAPI();

    @Test
    public void head() throws IOException {
        Response<TipSet> response = lotusChainAPI.head().execute();
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void asyncHead() throws IOException, InterruptedException {
        CountDownLatch cdl = new CountDownLatch(1);
        lotusChainAPI.head().enqueue(new Callback<TipSet>() {
            @Override
            public void onResponse(Call<TipSet> call, Response<TipSet> response) {
                Assert.assertNotNull(response.getResult());

                cdl.countDown();
            }

            @Override
            public void onFailure(Call<TipSet> call, Throwable t) {
                t.printStackTrace(System.err);
            }
        });
        cdl.await();
    }

    @Test
    public void getGenesis() throws IOException {
        Response<TipSet> response = lotusChainAPI.getGenesis().execute();
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void getBlock() throws IOException {
        Response<BlockHeader> response = lotusChainAPI.getBlock(Cid.of("bafy2bzacechdndwv3k6zripfpxm4jtwdvqnzlp6uvldccwf6cycmd2w3elvfc")).execute();
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void getTipSet() throws IOException {
        TipSetKey tipSetKey = TipSetKey.of("bafy2bzacec2qt5iuro25nmrt25amfb3lx6nebtdmdljodcuzen36lxrkjk2o4",
                "bafy2bzacecy5go5sxv7rgm5ncd5zkgwm43rhg7ko35rue5jh52jliym55ofrg",
                "bafy2bzacebkqspb5auphn5dxucsdmc7xsnj6pcxjhc7ivlxsla6oxlhniwkq6");

        Response<TipSet> response = lotusChainAPI.getTipSet(tipSetKey).execute();
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void getBlockMessages() throws IOException {
        Cid blockCid = Cid.of("bafy2bzacebhp7aweqqvurhypprqtqk75b7q2xs2lt5abi7hu7lsgepb5rlfck");

        Response<BlockMessages> response = lotusChainAPI.getBlockMessages(blockCid).execute();
        Assert.assertNotNull(response.getResult());

        BlockMessages blockMessages = response.getResult();
        List<Cid> cids1 = blockMessages.getBlsMessages()
                .stream()
                .map(com.nenglian.filecoin.rpc.domain.types.Message::getCid)
                .collect(Collectors.toList());
        List<Cid> cids2 = blockMessages.getSecpkMessages()
                .stream()
                .map(signedMessage -> signedMessage.getMessage().getCid())
                .collect(Collectors.toList());
        List<Cid> cids = blockMessages.getCids();
        for (Cid cid : cids1) {
            if (!cids.contains(cid)) {
                System.out.println(cids1.indexOf(cid));
            }
        }
        for (Cid cid : cids2) {
            if (!cids.contains(cid)) {
                System.out.println(cids2.indexOf(cid));
            }
        }
    }

    @Test
    public void getParentReceipts() throws IOException {
        Cid blockCid = Cid.of("bafy2bzacechdndwv3k6zripfpxm4jtwdvqnzlp6uvldccwf6cycmd2w3elvfc");

        Response<List<MessageReceipt>> response = lotusChainAPI.getParentReceipts(blockCid).execute();
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void getParentMessages() throws IOException {
        Cid blockCid = Cid.of("bafy2bzacechdndwv3k6zripfpxm4jtwdvqnzlp6uvldccwf6cycmd2w3elvfc");

        Response<List<com.nenglian.filecoin.rpc.domain.Message>> response = lotusChainAPI.getParentMessages(blockCid).execute();
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void getMessage() throws IOException {
        Cid cid = Cid.of("bafy2bzacecoovduvitlpkrjq2q6gbf6hfhe33lyxlhwy4mk4reewb4bhfwuuc");
        Cid cid1 = Cid.of("bafy2bzacebb4y3tfudsl2n42piibb52hnkwmugkb3rtckdryvjuah6vryqyty");

        Response<com.nenglian.filecoin.rpc.domain.types.Message> response = lotusChainAPI.getMessage(cid).execute();
        Response<com.nenglian.filecoin.rpc.domain.types.Message> response1 = lotusChainAPI.getMessage(cid1).execute();
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void getTipSetByHeight() throws IOException {
        TipSetKey tsk = TipSetKey.of("bafy2bzacec2qt5iuro25nmrt25amfb3lx6nebtdmdljodcuzen36lxrkjk2o4",
                "bafy2bzacecy5go5sxv7rgm5ncd5zkgwm43rhg7ko35rue5jh52jliym55ofrg",
                "bafy2bzacebkqspb5auphn5dxucsdmc7xsnj6pcxjhc7ivlxsla6oxlhniwkq6");

        Response<TipSet> response1 = lotusChainAPI.getTipSetByHeight(445113L, tsk).execute();
        Assert.assertNotNull(response1.getResult());

        Response<TipSet> response2 = lotusChainAPI.getTipSetByHeight(445113L, null).execute();
        Assert.assertNotNull(response2.getResult());

        Response<TipSet> response3 = lotusChainAPI.getTipSetByHeight(null, tsk).execute();
        Assert.assertNotNull(response3.getResult());
    }

    @Test
    public void getMessagesByHeight() throws ExecutionException, InterruptedException {
        long height = 131L;
        Map<Cid, com.nenglian.filecoin.rpc.domain.types.Message> messages
                = getMessagesFutureByHeight(height).get();
        Set<Cid> cids = getMessageCidsFutureByHeight(height).get();
        for (Cid cid : cids) {
            com.nenglian.filecoin.rpc.domain.types.Message message = messages.get(cid);
            if (!message.getCid().getStr().equals(cid.getStr())) {
                System.out.println(cid.getStr() + ":" + message);
            }
        }
    }

    @Test
    public void getMessageBlockMsg() throws ExecutionException, InterruptedException, IOException {
        Cid cid = Cid.of("bafy2bzacebdlxmab2o3jcxtwvjmglksy66dujeijmks34565scbu37jnxhxhc");

        CompletableFuture<List<Cid>> resultFuture = getMessageBlockCids(cid);
        System.out.println(resultFuture.get());
    }

    private CompletableFuture<List<Cid>> getMessageBlockCids(Cid cid) {
        CompletableFuture<MsgLookup> msgLookupFuture = call(() -> lotusStateAPI.searchMsg(cid));
        CompletableFuture<TipSet> tsFuture = msgLookupFuture.thenCompose(msgLookup ->
                call(() -> lotusChainAPI.getTipSetByHeight(msgLookup.getHeight() - 1, msgLookup.getTipSet())));
        return tsFuture.thenCompose(ts -> {
            // ???????????????block????????????????????????????????????????????????
            List<Cid> blockCids = ts.getCids();
            // ?????????????????????????????????
            CompletableFuture<?>[] futureArray =
                    blockCids.stream()
                            .map((blockCid) -> call(() -> lotusChainAPI.getBlockMessages(blockCid)))
                            .toArray(CompletableFuture<?>[]::new);
            CompletableFuture<Void> future = CompletableFuture.allOf(futureArray);
            CompletableFuture<List<Boolean>> containFuture = future.thenApply(v -> Stream.of(futureArray)
                    .map(CompletableFuture::join)
                    .map(e -> (BlockMessages) e)
                    .map(e -> e.getCids().contains(cid))
                    .collect(Collectors.toList()));
            List<Boolean> contains = containFuture.join();
            List<Cid> result = new ArrayList<>();
            for (int i = 0; i < contains.size(); ++i) {
                if (Boolean.TRUE.equals(contains.get(i))) {
                    result.add(blockCids.get(i));
                }
            }
            return CompletableFuture.completedFuture(result);
        });
    }

    private CompletableFuture<Set<Cid>> getMessageCidsFutureByHeight(long height) {
        // ????????????????????????TipSet
        CompletableFuture<TipSet> tipSetFuture = call(() -> lotusChainAPI.getTipSetByHeight(height, null));
        // ??????????????????OK?????????
        return tipSetFuture.thenCompose((ts) -> {
            // ???????????????????????????
            List<Cid> blockCids = ts.getCids();
            // ?????????????????????????????????
            CompletableFuture<?>[] futureArray =
                    blockCids.stream()
                            .map((blockCid) -> call(() -> lotusChainAPI.getBlockMessages(blockCid)))
                            .toArray(CompletableFuture<?>[]::new);
            CompletableFuture<Void> future = CompletableFuture.allOf(futureArray);
            return future.thenApply(v -> Stream.of(futureArray)
                    .map(CompletableFuture::join)
                    .map(e -> (BlockMessages) e)
                    .flatMap(e -> e.getCids().stream())
                    .collect(Collectors.toSet()));
        });
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private CompletableFuture<Map<Cid, Message>> getMessagesFutureByHeight(long height) {
        // ????????????????????????TipSet
        CompletableFuture<TipSet> tipSetFuture = call(() -> lotusChainAPI.getTipSetByHeight(height, null));
        // ??????????????????OK?????????
        return tipSetFuture.thenCompose((ts) -> {
            // ???????????????????????????
            List<Cid> blockCids = ts.getCids();
            // ?????????????????????????????????
            CompletableFuture<?>[] futureArray =
                    blockCids.stream()
                            .map((blockCid) -> call(() -> lotusChainAPI.getBlockMessages(blockCid)))
                            .toArray(CompletableFuture<?>[]::new);
            CompletableFuture<Void> future = CompletableFuture.allOf(futureArray);
            return future.thenApply(v -> Stream.of(futureArray)
                    .map(CompletableFuture::join)
                    .map(e -> (BlockMessages) e)
                    .flatMap(e -> {
                        List<Cid> cids = e.getCids();
                        List<Message> blsMessages = e.getBlsMessages();
                        List<Message> signedMessages = e.getSecpkMessages()
                                .stream()
                                .map(SignedMessage::getMessage)
                                .collect(Collectors.toList());
                        Stream.Builder<Pair<Cid, Message>> builder = Stream.builder();
                        for (int i = 0; i < blsMessages.size(); ++i) {
                            builder.add(new Pair<>(cids.get(i), blsMessages.get(i)));
                        }
                        for (int i = 0; i < signedMessages.size(); ++i) {
                            builder.add(new Pair<>(cids.get(i + blsMessages.size()), signedMessages.get(i)));
                        }
                        return builder.build();
                    })
                    .filter(distinctByKey(Pair::getKey))
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue)));
        });
    }

    private static <T> T syncCall(Supplier<Call<T>> call, int times) {
        int retry = 0;
        do {
            try {
                return call(call).get();
            } catch (Exception e) {
                ++retry;
            }
        } while (retry < times);

        throw new RuntimeException("????????????" + times + "????????????????????????");
    }

    private static <T> CompletableFuture<T> call(Supplier<Call<T>> call) {
        CompletableFuture<T> result = new CompletableFuture<>();
        call.get().enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response == null
                        || response.getRawResponse() == null
                        || !response.getRawResponse().isSuccessful()) {
                    result.completeExceptionally(new IOException("??????Lotus API?????????????????????????????????"));
                    return;
                }
                result.complete(response.getResult());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                result.completeExceptionally(new IOException("??????Lotus API?????????????????????????????????", t));
            }
        });
        return result;
    }
}
