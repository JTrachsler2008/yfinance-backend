package ch.allianz.jt.controller;

import ch.allianz.jt.entity.Account;
import ch.allianz.jt.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
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
}