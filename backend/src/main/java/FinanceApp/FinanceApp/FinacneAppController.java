package FinanceApp.FinanceApp;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/financeapp")
public class FinacneAppController {

    @GetMapping("/users")
    public List<User> getUsers() {
        return List.of(new User("user1", "password1"), new User("user2", "password2"));
    }
}
