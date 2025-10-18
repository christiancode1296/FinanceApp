package FinanceApp.FinanceApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProfile extends JpaRepository<UserProfile, UUID> {
}
