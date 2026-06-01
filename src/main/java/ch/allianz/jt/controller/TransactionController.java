package ch.allianz.jt.controller;

import ch.allianz.jt.entity.Transaction;
import ch.allianz.jt.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/{accountId}")
    public Transaction create(@PathVariable Long accountId,
                              @RequestBody Transaction transaction) {
        return transactionService.createTransaction(accountId, transaction);
    }

    @GetMapping
    public List<Transaction> getAll() {
        return transactionService.getAll();
    }
}