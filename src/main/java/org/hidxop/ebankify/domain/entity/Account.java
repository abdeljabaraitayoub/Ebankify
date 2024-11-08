package org.hidxop.ebankify.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hidxop.ebankify.domain.enumeration.AccountStatus;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    private Double balance;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "sourceAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Transaction> outgoingTransactions;

    @OneToMany(mappedBy = "destinationAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Transaction> incomingTransactions;
}
