package com.nenglian.filecoin.transaction;

import com.nenglian.filecoin.rpc.api.LotusAPIFactory;
import com.nenglian.filecoin.rpc.api.LotusChainAPI;
import com.nenglian.filecoin.rpc.api.LotusStateAPI;
import com.nenglian.filecoin.rpc.domain.BlockMessages;
import com.nenglian.filecoin.rpc.domain.MsgLookup;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.MessageReceipt;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.rpc.domain.types.TipSet;
import com.nenglian.filecoin.rpc.jasonrpc.Call;
import com.nenglian.filecoin.rpc.jasonrpc.Callback;
import com.nenglian.filecoin.rpc.jasonrpc.Response;
import com.nenglian.filecoin.transaction.dto.TxEvent;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author stephen
 * @since 2021/7/15 上午11:37
 */
@Component
@EnableScheduling
public class TransactionListener {
    private static final String API_ROUTER = "http://localhost:7777/rpc/v1";
    private static final String AUTHORIZATION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJBbGxvdyI6WyJyZWFkIl19.fNgcqyigMfozXVmBK13lhzPqDrjE3TwRDvcrwx9ReM0";
    protected LotusAPIFactory lotusAPIFactory = new LotusAPIFactory.Builder()
        .apiGateway(API_ROUTER)
        .authorization(AUTHORIZATION)
        .connectTimeout(5)
        .readTimeout(60)
        .writeTimeout(30)
        .build();
    LotusChainAPI lotusChainAPI = lotusAPIFactory.createLotusChainAPI();

    static int latestBlock = 11665;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Scheduled(cron = "0/5 * * * * ?")
    public void listen() throws IOException {
        Long current = lotusChainAPI.head().execute().getResult().getHeight();
        for (int i = latestBlock; i <= current; i++){
            System.out.println("scan block: " + i);
            process(i);
            latestBlock = i;
        }

    }

    public void process(long height){
        CompletableFuture<Map<Cid, Message>> future = getMessagesFutureByHeight(height);
        future.whenComplete((map, error) -> {
            if (map.size() > 0) {
                map.forEach((cid, msg) -> {

                    LotusStateAPI stateAPI = lotusAPIFactory.createLotusStateAPI();
                    MessageReceipt receipt;
                    try {
                        MsgLookup msgLookup = stateAPI
                            .searchMsg(cid).execute().getResult();
                        receipt = msgLookup.getReceipt();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // TODO
                    TxEvent te = TxEvent.builder().cid(cid).from(msg.getFrom()).to(msg.getTo()).value(receipt.getGasUsed())
                        .fee(msg.getGasFeeCap())
                        .message(msg)
                        .build();
                    applicationEventPublisher.publishEvent(te);
                });

            }

        });
    }


    private CompletableFuture<Map<Cid, Message>> getMessagesFutureByHeight(long height) {
        // 根据固定高度拉去TipSet
        CompletableFuture<TipSet> tipSetFuture = call(() -> lotusChainAPI.getTipSetByHeight(height, null));
        // 获取所有状态OK的消息
        return tipSetFuture.thenCompose((ts) -> {
            // 获取当前高度的区块
            List<Cid> blockCids = ts.getCids();
            // 获取每个区块的消息列表
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

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private static <T> CompletableFuture<T> call(Supplier<Call<T>> call) {
        CompletableFuture<T> result = new CompletableFuture<>();
        call.get().enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response == null
                    || response.getRawResponse() == null
                    || !response.getRawResponse().isSuccessful()) {
                    result.completeExceptionally(new IOException("执行Lotus API调用异常，本次处理失败"));
                    return;
                }
                result.complete(response.getResult());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                result.completeExceptionally(new IOException("执行Lotus API调用异常，本次处理失败", t));
            }
        });
        return result;
    }

}
