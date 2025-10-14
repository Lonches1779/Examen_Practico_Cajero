package com.cajero.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CAJERO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cajero {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAJERO_SEQ")
    @SequenceGenerator(name = "CAJERO_SEQ", sequenceName = "CAJERO_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "DENOMINACION")
    private String denominacion; 

    @Column(name = "CENTAVOS")
    private Integer centavos; 

    @Column(name = "CANTIDAD_TOTAL")
    private Integer cantidadTotal; 
    
}
