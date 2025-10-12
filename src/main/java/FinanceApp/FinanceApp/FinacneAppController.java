package FinanceApp.FinanceApp;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class FinacneAppController {

    @GetMapping("/financeapp")
    public String hello() {
        return "Hello, FinanceApp! Welcome to our new application.";
    }
}
