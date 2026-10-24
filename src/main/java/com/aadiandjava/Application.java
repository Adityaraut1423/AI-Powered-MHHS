package com.aadiandjava;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.aadiandjava.entity.UserRole;
import com.aadiandjava.repository.StudentUserRepository;

@SpringBootApplication
@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // Temporary boot runner to ensure your Admin credentials are correct and securely encrypted
    @Bean
    CommandLineRunner initializeAdmin(StudentUserRepository repository, PasswordEncoder encoder) {
        return args -> {
            repository.findByEmail("admin@mindspace.edu").ifPresentOrElse(admin -> {
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(UserRole.ROLE_ADMIN);
                repository.save(admin);
                System.out.println(">>> ADMIN CREDENTIALS UPDATED: admin@mindspace.edu / admin123 <<<");
            }, () -> {
                System.out.println(">>> ADMIN USER NOT FOUND IN DATABASE. Please register an account with this email first! <<<");
            });
        };
    }
}