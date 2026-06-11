package ch.allianz.jt.controller;

import ch.allianz.jt.entity.Account;
import ch.allianz.jt.entity.Transaction;
import ch.allianz.jt.repository.TransactionRepository;
import ch.allianz.jt.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;

    public AccountController(AccountService accountService,
                             TransactionRepository transactionRepository) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping("/{portfolioId}")
    public Account create(@PathVariable Long portfolioId,
                          @RequestBody Account account) {
        return accountService.createAccount(portfolioId, account);
    }

    @GetMapping
    public List<Account> getAll() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getById(@PathVariable Long id) {
        return accountService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/transactions")
    public List<Transaction> getTransactionsByAccount(@PathVariable Long id) {
        return transactionRepository.findByAccountId(id);
    }

    @PostMapping("/{accountId}/deposit")
    public Account deposit(@PathVariable Long accountId, @RequestParam Double amount) {
        return accountService.deposit(accountId, amount);
    }

    @PostMapping("/{accountId}/withdraw")
    public Account withdraw(@PathVariable Long accountId, @RequestParam Double amount) {
        return accountService.withdraw(accountId, amount);
    }
}