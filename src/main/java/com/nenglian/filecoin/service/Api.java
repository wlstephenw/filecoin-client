package com.nenglian.filecoin.service;

import com.nenglian.filecoin.service.api.EasyTransfer;
import com.nenglian.filecoin.service.api.MessageResult;
import java.math.BigDecimal;
import java.util.Date;
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

    @GetMapping("address")
    MessageResult getNewAddress(@RequestParam String account, @RequestParam byte type);

    @PostMapping("transfer")
    MessageResult transfer(
        @RequestBody EasyTransfer transfer
    );

    @GetMapping("reconciliation")
    MessageResult reconciliation(
        @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date from,
        @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date to
    );

    @GetMapping("toaddress")
    MessageResult toAddress(@RequestParam String hexpk);

    @GetMapping("setPollHeight")
    MessageResult setPollHeight(@RequestParam Integer height);



    // TODO 查询接口

    /**
     * 获取当前区块高度
     * @return
     */
    MessageResult blockHeight();


    /**
     * 余额
     * @return
     */
    @GetMapping("balance")
    MessageResult balance(@RequestParam String address);









    @GetMapping("collect")
    MessageResult collect(
        @RequestParam String fromAddress,
        @RequestParam String toAddress,
        @RequestParam String gasAddress,
        @RequestParam String collectId );

    @GetMapping("withdraw")
    MessageResult withdraw(
        @RequestParam String toAddress,
        @RequestParam BigDecimal amount,
        @RequestParam BigDecimal fee,
        @RequestParam Boolean isSync,
        @RequestParam String withdrawId
    );

}
