package com.nenglian.filecoin.api;

import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("transfer")
    MessageResult transfer(
        @RequestParam String from,
        @RequestParam String to,
        @RequestParam String amount
    );

    @GetMapping("collect")
    MessageResult collect(@RequestParam String fromAddress,
        @RequestParam String toAddress,
        @RequestParam String gasAddress,
        @RequestParam String collectId );

    @GetMapping("withdraw")
    MessageResult withdraw(@RequestParam String toAddress,
        @RequestParam BigDecimal amount,
        @RequestParam BigDecimal fee,
        @RequestParam Boolean isSync,
        @RequestParam String withdrawId);




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

}
