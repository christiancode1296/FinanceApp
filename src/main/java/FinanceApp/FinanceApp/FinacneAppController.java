package FinanceApp.FinanceApp;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FinacneAppController {

    @GetMapping("/financeapp")
    public List<User> getUsers() {
        return List.of(new User("user1", "password1"), new User("user2", "password2"));
    }
}
