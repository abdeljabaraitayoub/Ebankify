package org.hidxop.ebankify.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hidxop.ebankify.domain.enumeration.TransactionStatus;
import org.hidxop.ebankify.domain.enumeration.TransactionType;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private Double amount;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    @ManyToOne
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;

}
