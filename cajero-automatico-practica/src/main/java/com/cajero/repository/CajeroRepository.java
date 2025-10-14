package com.cajero.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.cajero.modelo.Cajero;

import jakarta.persistence.LockModeType;
import java.util.List;

@Repository
public interface CajeroRepository  extends JpaRepository<Cajero, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Cajero a ORDER BY a.centavos DESC")
    List<Cajero> listAllCajero();

    @Query("SELECT a FROM Cajero a ORDER BY a.centavos DESC")
    List<Cajero> getByIdCajero();
}
