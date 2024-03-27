package org.example.Launcher;

import lombok.extern.slf4j.Slf4j;
import org.example.controller.TransactionAncestryController;

@Slf4j
public class TransactionAncestryService {
    public static void main(String[] args) {

        Integer blockAddress = 680000;
        TransactionAncestryController transactionAncestryController = new TransactionAncestryController();
        transactionAncestryController.getTransactionAncestry(blockAddress);
    }
}
