package org.example.models;

import lombok.Data;

import java.util.List;

@Data
public class BlockInfo {
//    private Integer blockAddress;
    private List<Transaction> blockTransactions;
}
