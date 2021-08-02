package com.nenglian.filecoin.service;

import com.nenglian.filecoin.service.api.BlockInfo;
import com.nenglian.filecoin.service.api.EstimatedGas;
import com.nenglian.filecoin.service.api.Reconciliation;
import com.nenglian.filecoin.service.api.Result;
import com.nenglian.filecoin.service.api.Transfer;
import com.nenglian.filecoin.service.api.WalletAddress;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author stephen
 * @since 2021/7/13 下午4:24
 */
@RestController
@RequestMapping("/rpc")
public interface Api {

    /**
     * create address of the given type
     *
     * @param type
     * @return
     */
    @PostMapping("address")
    Result<WalletAddress> createAddress(@RequestParam byte type);


    /**
     * estimate gas
     *
     * @param transfer
     * @return
     */
    @PostMapping("estimate")
    Result<EstimatedGas> estimateGas(@RequestBody Transfer transfer);

    /**
     * perform transfer transaction
     *
     * @param transfer
     * @return
     */
    @PostMapping("transfer")
    Result<String> transfer(@RequestBody Transfer transfer);



    /**
     * get transactions from chain at the given time range
     *
     * @param from
     * @param to
     * @return
     */
    @GetMapping("reconciliation")
    Result<List<Reconciliation>> reconciliation(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date to
    );

    /**
     * set block height to be replayed from
     *
     * @param fromHeight
     * @return
     */
    @PostMapping("pollHeight")
    Result setPollHeight(@RequestParam Long fromHeight);

    /**
     * get latest block info
     *
     * @return
     */
    @GetMapping("latest")
    Result<BlockInfo> latestBlock();

    /**
     * get local coin or token balance of the given address
     *
     * @param
     * @return
     */
    @GetMapping("balance")
    Result<BigInteger> balance(@RequestParam String address, @RequestParam String tokenAddress);


}
