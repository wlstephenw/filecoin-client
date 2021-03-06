package com.nenglian.filecoin.rpc.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.nenglian.filecoin.rpc.jasonrpc.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author stephen
 */
public class LotusAPIFactory {
    private static final Logger logger = LoggerFactory.getLogger(LotusAPIFactory.class);

    public LotusChainAPI createLotusChainAPI() {
        return createLotusAPI(LotusChainAPI.class);
    }

    public LotusGasAPI createLotusGasAPI() {
        return createLotusAPI(LotusGasAPI.class);
    }

    public LotusMinerAPI createLotusMinerAPI() {
        return createLotusAPI(LotusMinerAPI.class);
    }

    public LotusStateAPI createLotusStateAPI() {
        return createLotusAPI(LotusStateAPI.class);
    }

    public LotusSyncAPI createLotusSyncAPI() {
        return createLotusAPI(LotusSyncAPI.class);
    }

    public LotusWalletAPI createLotusWalletAPI() {
        return createLotusAPI(LotusWalletAPI.class);
    }

    public LotusMpoolAPI createLotusMPoolAPI() {
        return createLotusAPI(LotusMpoolAPI.class);
    }

    public LotusBeaconAPI createLotusBeaconAPI() {
        return createLotusAPI(LotusBeaconAPI.class);
    }

    private <T> T createLotusAPI(Class<T> apiClass) {
        return jsonRpcRetrofit.create(apiClass);
    }

    public static LotusAPIFactory create() {
//        String API_ROUTER = "http://localhost:7777/rpc/v0";
//        String AUTHORIZATION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJBbGxvdyI6WyJyZWFkIl19.fNgcqyigMfozXVmBK13lhzPqDrjE3TwRDvcrwx9ReM0";

        final String API_ROUTER = "http://192.168.1.71:12345/rpc/v0";
        final String AUTHORIZATION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJBbGxvdyI6WyJyZWFkIiwid3JpdGUiLCJzaWduIiwiYWRtaW4iXX0.dUnQ95Y4JFdSrB5yqzN9BAMLpBtDutZVc4gFmSd39Hk";

        return new LotusAPIFactory.Builder()
            .apiGateway(API_ROUTER)
            .authorization(AUTHORIZATION)
            .connectTimeout(5)
            .readTimeout(60)
            .writeTimeout(30)
            .build();

    }

    public static class Builder {
        private static final int DEFAULT_READ_TIMEOUT = 30;
        private static final int DEFAULT_CONNECT_TIMEOUT = 5;
        private static final int DEFAULT_WRITE_TIMEOUT = 30;

        private String apiGateway;

        private String authorization;

        private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

        private int readTimeout = DEFAULT_READ_TIMEOUT;

        private int writeTimeout = DEFAULT_WRITE_TIMEOUT;

        public Builder apiGateway(String apiGateway) {
            this.apiGateway = apiGateway;
            return this;
        }

        public Builder authorization(String authorization) {
            this.authorization = authorization;
            return this;
        }

        public Builder connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder writeTimeout(int writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }

        public LotusAPIFactory build() {
            if (apiGateway == null || apiGateway.length() == 0) {
                throw new IllegalArgumentException("Lotus API????????????????????????.");
            }
            return new LotusAPIFactory(apiGateway, authorization,
                    connectTimeout, readTimeout, writeTimeout);
        }
    }

    private static final String HEADER_AUTHORIZATION = "AUTHORIZATION";

    private final JsonRpcRetrofit jsonRpcRetrofit;

    private LotusAPIFactory(String apiGateway, String authorization,
                            int connectTimeout, int readTimeout, int writeTimeout) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .build();

        this.jsonRpcRetrofit = new JsonRpcRetrofit.Builder()
                .httpUrl(apiGateway)
                .jsonBodyConverter(new LotusJsonBodyConverter())
                .callFactory(client)
                .headers(Headers.of(HEADER_AUTHORIZATION, authorization))
                .build();

    }

    private static class LotusJsonBodyConverter implements JsonBodyConverter {

        private static final ObjectMapper mapper = new ObjectMapper()
                // ????????????????????????????????????
                .configure(MapperFeature.USE_STD_BEAN_NAMING, true)
                // ??????????????????????????????
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // JSON????????????????????????
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true)
                // ??????NULL??????
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                // ???????????????
                .setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE)
                .registerModule(new SimpleModule()
                        // BigInteger????????????????????????
                        .addSerializer(BigInteger.class, ToStringSerializer.instance));

        @Override
        public String convertRequest(JsonRpcRequest request) throws JsonConvertException {
            try {
                String requestJson = mapper.writeValueAsString(request);

                logger.debug("JSON-RPC request : \n{}", requestJson);

                return requestJson;
            } catch (JsonProcessingException e) {
                throw new JsonConvertException("JSON-RPC request convert error.", e);
            }
        }

        @Override
        public <T> JsonRpcResponse<T> convertResponse(Type type, String response) throws JsonConvertException {
            logger.debug("JSON-RPC response : \n{}", response);

            try {
                JsonRpcResponse<T> jsonRpcResponse = new JsonRpcResponse<>();

                JsonNode jsonNode = mapper.readTree(response);
                if (jsonNode == null) {
                    return jsonRpcResponse;
                }

                JsonNode id = jsonNode.get("id");
                if (id != null) {
                    jsonRpcResponse.setId(id.asLong());
                }

                JsonNode jsonrpc = jsonNode.get("jsonrpc");
                if (jsonrpc != null) {
                    jsonRpcResponse.setJsonrpc(jsonrpc.asText());
                }

                JsonNode error = jsonNode.get("error");
                if (error != null) {
                    JsonRpcResponse.Error e = new JsonRpcResponse.Error();
                    e.setCode(error.get("code").asInt(0));
                    e.setMessage(error.get("message").asText());
                    JsonNode data = error.get("data");
                    if (data != null) {
                        e.setData(data.toString());
                    }
                    jsonRpcResponse.setError(e);
                } else {
                    jsonRpcResponse.setResult(parseJsonNode(jsonNode.get("result"), type));
                }
                return jsonRpcResponse;
            } catch (Exception e) {
                throw new JsonConvertException("JSON-RPC response convert error.", e);
            }
        }

        private static <T> T parseJsonNode(JsonNode jsonNode, Type type) throws IOException {
            if (jsonNode == null) {
                return null;
            }
            JsonParser parser = mapper.treeAsTokens(jsonNode);
            JavaType javaType = mapper.getTypeFactory().constructType(type);
            return mapper.readValue(parser, javaType);
        }
    }

}
