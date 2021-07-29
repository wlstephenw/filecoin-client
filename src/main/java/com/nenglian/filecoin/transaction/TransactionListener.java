package com.nenglian.filecoin.transaction;

import com.nenglian.filecoin.rpc.api.LotusAPIFactory;
import com.nenglian.filecoin.rpc.api.LotusChainAPI;
import com.nenglian.filecoin.rpc.api.LotusStateAPI;
import com.nenglian.filecoin.rpc.domain.BlockMessages;
import com.nenglian.filecoin.rpc.domain.InvocResult;
import com.nenglian.filecoin.rpc.domain.MsgLookup;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.BlockHeader;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.MessageReceipt;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.rpc.domain.types.TipSet;
import com.nenglian.filecoin.rpc.jasonrpc.Call;
import com.nenglian.filecoin.rpc.jasonrpc.Callback;
import com.nenglian.filecoin.rpc.jasonrpc.Response;
import com.nenglian.filecoin.service.TransactionSaver;
import com.nenglian.filecoin.transaction.dto.TxReceipt;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javafx.util.Pair;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author stephen
 * @since 2021/7/15 上午11:37
 */
@Data
@Component
@EnableScheduling
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);
    protected LotusAPIFactory lotusAPIFactory = LotusAPIFactory.create();
    LotusChainAPI lotusChainAPI = lotusAPIFactory.createLotusChainAPI();

//    AtomicInteger latestBlock = new AtomicInteger(110748);
    AtomicInteger latestBlock = null;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    TransactionSaver transactionSaver;


    @Scheduled(cron = "0/5 * * * * ?")
    public void listen() throws IOException {
        Long head = lotusChainAPI.head().execute().getResult().getHeight();
        if (null == latestBlock) {
            Long largestHeigt = transactionSaver.getLargestHeigt();
            if (null == largestHeigt) {
                latestBlock = new AtomicInteger(head.intValue() - 10);
            }else {
                latestBlock = new AtomicInteger(largestHeigt.intValue());
            }

        }
        while (latestBlock.get() < head){
            int i = latestBlock.getAndIncrement();

            process(i);
        }
    }

    public void process(long height){
        CompletableFuture<Map<Cid, TxReceipt>> future = getMessagesFutureByHeight(height);
        logger.info("starting scan block: {}", height);
        try {
            Map<Cid, TxReceipt> cidTxReceiptMap = future.get();
            logger.info("scanned block: {}, total messages:{}", height, cidTxReceiptMap.size() );
            cidTxReceiptMap.forEach((cid, ev) -> {
                logger.info("高度:{}, 时间:{}, cid: {}, 交易结果:{},交易内容:{}", ev.getBlockHeight(), new Date(ev.getBlockTime() * 1000), ev.getMessage().getCid(),
                    ev.getReceipt(), ev.getMessage());
                try {
                    applicationEventPublisher.publishEvent(ev);
                } catch (Exception e) {
                    logger.error("error:", e);
                }
            });
        }catch (Exception e){
            logger.error("", e);
        }


//        future.whenComplete((map, error) -> {
//            if (map.size() > 0) {
//                map.forEach((cid, ev) -> {
//                    logger.info("交易高度:{}, cid: {}, 交易结果:{},交易内容:{}", ev.getBlockHeight(), ev.getMessage().getCid(), ev.getReceipt(),ev.getMessage());
//                    try {
//                        applicationEventPublisher.publishEvent(ev);
//                    }catch (Exception e){
//                        logger.error("error:", e);
//                    }
//                });
//
//            }
//
//        });

    }


    public InvocResult replay(Cid cid){
        try {
            return lotusAPIFactory.createLotusStateAPI().replay(null, cid).execute().getResult();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MsgLookup getMsgLookup(Cid cid) {
        LotusStateAPI stateAPI = lotusAPIFactory.createLotusStateAPI();

        try {
            return stateAPI
                .searchMsg(cid).execute().getResult();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TipSet head() throws IOException {
        return lotusChainAPI.head().execute().getResult();
    }

    public Long headHeight() throws IOException {
        return lotusChainAPI.head().execute().getResult().getHeight();
    }

    public CompletableFuture<Map<Cid, TxReceipt>> getMessagesFutureByHeightRange(Date from, Date to) throws IOException {
        Long current = lotusChainAPI.head().execute().getResult().getHeight();
        BlockHeader block = getBlockHeader(current);
        long fromHeight = block.getHeight();
        long toHeight = block.getHeight();
        while (block.getTimestamp() * 1000 > from.getTime() && current > 0){

            fromHeight = block.getHeight();
            if (block.getTimestamp() * 1000 > to.getTime()) {
                toHeight = block.getHeight();
            }
            current--;
            block = getBlockHeader(current);
        }

        return getMessagesFutureByHeightRange(fromHeight, toHeight);
    }

    public BlockHeader getBlockHeader(long height) throws IOException {
        List<BlockHeader> blocks = lotusChainAPI.getTipSetByHeight(height, null).execute().getResult().getBlocks();
        BlockHeader header = blocks.get(0);
        return header;
    }


    public CompletableFuture<Map<Cid, TxReceipt>> getMessagesFutureByHeightRange(long from, long to){
        if (to < from){
            throw new RuntimeException("to must larger than from");
        }
        CompletableFuture<?>[] futures = LongStream.range(from, to + 1)
            .mapToObj(this::getMessagesFutureByHeight)
            .toArray(CompletableFuture<?>[]::new);

        CompletableFuture<Void> all = CompletableFuture.allOf(futures);

        CompletableFuture<Map<Cid, TxReceipt>> listCompletableFuture = all.thenApply(v -> {

            return Stream.of(futures)
                .map(f -> f.join())
                .map(e -> (Map<Cid, TxReceipt>) e)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));      // the type of map to be created
        });
        return listCompletableFuture;
    }

    public CompletableFuture<Map<Cid, TxReceipt>> getMessagesFutureByHeight(long height) {
        // 根据固定高度拉去TipSet
        CompletableFuture<TipSet> tipSetFuture = call(() -> lotusChainAPI.getTipSetByHeight(height, null));
        // 获取所有状态OK的消息
        return tipSetFuture.thenCompose((ts) -> {
            // 获取当前高度的区块
            List<Cid> blockCids = ts.getCids();
            // block number must larger than 0
            final Long blockTime  = ts.getBlocks().get(0).getTimestamp();
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
                    Stream.Builder<Pair<Cid, TxReceipt>> builder = Stream.builder();
                    for (int i = 0; i < blsMessages.size(); ++i) {
                        InvocResult replay = replay(blsMessages.get(i).getCid());
                        builder.add(
                            new Pair<>(cids.get(i),
                            TxReceipt.builder()
                                .message(blsMessages.get(i))
                                .receipt(replay.getMsgRct())
                                .invocResult(replay)
                                .blockHeight(height)
                                .blockTime(blockTime)
                                .build()));
                    }
                    for (int i = 0; i < signedMessages.size(); ++i) {
                        InvocResult replay = replay(signedMessages.get(i).getCid());
                        builder.add(
                            new Pair<>(
                            signedMessages.get(i).getCid(),
                            TxReceipt.builder()
                                .message(signedMessages.get(i))
                                .receipt(replay.getMsgRct())
                                .invocResult(replay)
                                .blockHeight(height)
                                .blockTime(blockTime)
                                .build()));
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
