package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "COIN")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coin {
    @Id
    @Column(name = "NAME")
    private String name;

    @Column(name = "NAMEZH")
    private String nameZH;

    @Column(name = "RATE", precision = 20, scale = 4)
    private BigDecimal rate;

    @CreationTimestamp
    @Column(name = "CREATED", updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "UPDATED", updatable = false)
    private LocalDateTime updated;
}