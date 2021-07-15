package com.nenglian.filecoin.rpc.domain;


import java.io.Serializable;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class MessageMatch implements Serializable {

    private String from;

    private String to;

    public static MessageMatch of(String from, String to) {
        MessageMatch messageMatch = new MessageMatch();
        messageMatch.setFrom(from);
        messageMatch.setTo(to);
        return messageMatch;
    }
}
