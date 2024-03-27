package org.example.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    private String txid;
//    private Integer version;
    private List<TransactionParent> vin;
    private TransactionStatus status;
}
