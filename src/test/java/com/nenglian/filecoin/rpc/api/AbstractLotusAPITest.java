package com.nenglian.filecoin.rpc.api;

/**
 * @author stephen
 */
public class AbstractLotusAPITest {

    //private static final String API_ROUTER = "https://1mPFsTLsLORQ38w5qLhBNMwJ1pz:7288a96f1a57d28747b299e3b87ed120@filecoin.infura.io";
    //private static final String AUTHORIZATION = "Basic MW1QRnNUTHNMT1JRMzh3NXFMaEJOTXdKMXB6OjcyODhhOTZmMWE1N2QyODc0N2IyOTllM2I4N2VkMTIw";
//    private static final String API_ROUTER = "http://113.250.13.195:1234/rpc/v0";

//    private static final String API_ROUTER = "http://localhost:7777/rpc/v0";
//    private static final String AUTHORIZATION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJBbGxvdyI6WyJyZWFkIl19.fNgcqyigMfozXVmBK13lhzPqDrjE3TwRDvcrwx9ReM0";

//    private static final String API_ROUTER = "http://192.168.1.71:12345/rpc/v0";
//    private static final String AUTHORIZATION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJBbGxvdyI6WyJyZWFkIiwid3JpdGUiLCJzaWduIiwiYWRtaW4iXX0.dUnQ95Y4JFdSrB5yqzN9BAMLpBtDutZVc4gFmSd39Hk";
//
//    protected LotusAPIFactory lotusAPIFactory = new LotusAPIFactory.Builder()
//            .apiGateway(API_ROUTER)
//            .authorization(AUTHORIZATION)
//            .connectTimeout(5)
//            .readTimeout(60)
//            .writeTimeout(30)
//            .build();

    protected LotusAPIFactory lotusAPIFactory = LotusAPIFactory.create();
}
