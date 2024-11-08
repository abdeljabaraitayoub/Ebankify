package org.hidxop.ebankify.domain.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hidxop.ebankify.domain.enumeration.Role;

import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    private String name;
    private int age;
    private Double monthlyIncome;
    private int creditScore;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(unique = true, length = 100, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;


    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Loan> loans;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Account> accounts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Invoice> invoices;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
