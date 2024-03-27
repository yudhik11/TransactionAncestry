package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.example.models.Transaction;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class TransactionAncestryController {

    public static String GET_BLOCK_HASHMAP = "https://blockstream.info/api/block-height/";
    public static String GET_BLOCK_TRANS = "https://blockstream.info/api/block/{block_hash_map}/txs/{pagination_number}";
    private AncestryGraphController ancestryGraphController;

    public TransactionAncestryController() {
        ancestryGraphController = new AncestryGraphController();
    }

    public void getTransactionAncestry(Integer blockId) {
        log.info(String.format("Starting service to generate transaction ancestry for blockId: %d", blockId));
        String blockHash = getGetBlockHashmap(blockId);
        List<Transaction> blockTransactions = getBlockTrasactions(blockHash);
//        System.out.println(blockTransactions);
        ancestryGraphController.computeAncestorGraph(blockTransactions);
        Map<String, Integer> ancestrySize = ancestryGraphController.findAncestrySize();
        List<Map.Entry<String, Integer>> list = new ArrayList<>(ancestrySize.entrySet());
        list.sort(Map.Entry.comparingByValue());
        System.out.println(list);
//        return list;
    }

    private List<Transaction> getBlockTrasactions(String blockHash) {
        String uriBlockTransactions = GET_BLOCK_TRANS.replace("{block_hash_map}", blockHash);
        System.out.println(uriBlockTransactions);
        List<Transaction> blockTransactions = Lists.newArrayList();
        Integer paginationNumber = 0;

        while (true) {
            log.info(String.format("Pagination number in process: %d", paginationNumber));
            HttpRequest requestBlockTrans = HttpRequest.newBuilder()
                    .uri(URI.create(uriBlockTransactions.replace("{pagination_number}", paginationNumber.toString())))
                    .GET()
                    .build();

            HttpResponse<String> responseBlockTrans = null;
            try {
                responseBlockTrans = HttpClient.newHttpClient().send(requestBlockTrans, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            String responseBlockTransStr = responseBlockTrans.body();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<Transaction> blockTransactionsPaginated = objectMapper.readValue(responseBlockTransStr, new TypeReference<List<Transaction>>() {
                });
                log.info(String.format("Processing completed for pagination number: %d", paginationNumber));
                blockTransactions.addAll(blockTransactionsPaginated);
            } catch (JsonProcessingException e) {
                // Todo (yudhik): Instead of processing exception we can also read the message coming after last valid paginationNumber
                log.error(String.format("Last Pagination Number: %d", paginationNumber - 25));
                break;
            }
            paginationNumber += 25;
        }
        log.info(String.format("Total transaction blobs: %d", blockTransactions.size()));
        return blockTransactions;
    }

    private String getGetBlockHashmap(Integer blockId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GET_BLOCK_HASHMAP + blockId))
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(response.body());
        return response.body();
    }

}
