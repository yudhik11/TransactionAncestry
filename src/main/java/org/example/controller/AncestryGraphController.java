package org.example.controller;

import org.example.models.Transaction;
import org.example.models.TransactionParent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AncestryGraphController {
    private Map<String, Integer> inNode = new HashMap<>();
    private Map<String, List<String> > ancestorGraph = new HashMap();

    public AncestryGraphController() {

    }

    public Map<String, Integer> findAncestrySize() {
        List<String> roots = new ArrayList<>();
        for (String txid: inNode.keySet()) {
            if (inNode.get(txid).equals(0)) {
                roots.add(txid);
            }
        }
        Map<String, Integer> ancestrySize = new HashMap<>();
        for (String root: roots) {
            findAncestrySize(root, ancestrySize);
        }
        return ancestrySize;
    }

    public Integer findAncestrySize(String root, Map<String, Integer> ancestrySize) {
        if (ancestorGraph.get(root).isEmpty()) return 0;
        if (ancestrySize.containsKey(root)) {
            return ancestrySize.get(root);
        }
        Integer total = 0;
        for (String childNode: ancestorGraph.get(root)) {
            total += findAncestrySize(childNode, ancestrySize);
        }
        ancestrySize.put(root, total + 1);
        return total + 1;
    }

    public void computeAncestorGraph(List<Transaction> transactions) {
        for(Transaction transaction: transactions) {
                String txid = transaction.getTxid();
                List<TransactionParent> txParents = transaction.getVin();
            if (!ancestorGraph.containsKey(txid)) {
                ancestorGraph.put(txid, new ArrayList<>());
            }
            if (!inNode.containsKey(txid)) {
                inNode.put(txid, 0);
            }
            for (TransactionParent transactionParent : txParents) {
                String parentTxid = transactionParent.getTxid();
                ancestorGraph.get(txid).add(parentTxid);
                if (!inNode.containsKey(parentTxid)) {
                    inNode.put(parentTxid, 0);
                }
                inNode.put(parentTxid, inNode.get(parentTxid) + 1);
                if (!ancestorGraph.containsKey(parentTxid)) {
                    ancestorGraph.put(parentTxid, new ArrayList<>());
                }
            }
        }
    }
}
