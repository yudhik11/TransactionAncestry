package org.example.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.math.BigInteger;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionParent {
    private String txid;
//    private BigInteger vout;
}
