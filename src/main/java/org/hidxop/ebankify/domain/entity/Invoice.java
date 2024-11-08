package org.hidxop.ebankify.domain.entity;


import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    private Double amountDue;
    private Date dueDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
