package com.nenglian.filecoin.service;

import com.nenglian.filecoin.service.api.BlockInfo;
import com.nenglian.filecoin.service.api.EasyTransfer;
import com.nenglian.filecoin.service.api.Result;
import com.nenglian.filecoin.service.api.Reconciliation;
import com.nenglian.filecoin.service.api.Transfer;
import com.nenglian.filecoin.service.api.WalletAddress;
import java.math.BigDecimal;
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

    // 1. 地址相关
    @PostMapping("address")
    Result<WalletAddress> createAddress(@RequestParam String account, @RequestParam byte type);
    @GetMapping("toaddress")
    Result<String> toAddress(@RequestParam String hexpk);

    // 2. 交易相关
    @PostMapping("transfer")
    Result<String> transfer(@RequestBody Transfer transfer);
    @PostMapping("gas")
    Result<String> gas(@RequestBody EasyTransfer transfer);

    // 3. 对账
    @GetMapping("reconciliation")
    Result<List<Reconciliation>> reconciliation(
        @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date from,
        @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date to
    );

    // 4. 监听相关
    @PostMapping("pollHeight")
    Result setPollHeight(@RequestParam Integer fromHeight);


    // 5. 查询相关
    @GetMapping("latest")
    Result<BlockInfo> latestBlock();
    @GetMapping("balance")
    Result<BigDecimal> balance(@RequestParam String address);

}
