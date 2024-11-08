package org.hidxop.ebankify.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    private Double principal;
    private float interestRate;
    private int termMonths;
    private boolean isApproved;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
