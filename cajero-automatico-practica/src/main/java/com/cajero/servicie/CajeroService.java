package com.cajero.servicie;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cajero.modelo.Cajero;
import com.cajero.repository.CajeroRepository;

@Service
@RequiredArgsConstructor
public class CajeroService {

	private final CajeroRepository repository;

	public static class DispenseItem {
		public final Cajero cajero;
		public final int cantidad;

		public DispenseItem(Cajero cajero, int cantidad) {
			this.cajero = cajero;
			this.cantidad = cantidad;
		}
	}

	public static class DispenseResult {
		public final boolean success;
		public final String message;
		public final List<DispenseItem> items;

		public DispenseResult(boolean success, String message, List<DispenseItem> items) {
			this.success = success;
			this.message = message;
			this.items = items;
		}
	}

	@Transactional
	public DispenseResult retirar(double monto) {
		if (monto <= 0) {
			return new DispenseResult(false, "La cantidad debe ser mayor que 0.", List.of());
		}

		long montoCentavos = Math.round(monto * 100);
		List<Cajero> cajeros = repository.listAllCajero();

		long totalDisponible = cajeros.stream().mapToLong(c -> (long) c.getCentavos() * c.getCantidadTotal()).sum();

		if (montoCentavos > totalDisponible) {
			return new DispenseResult(false, "Fondos insuficientes en el cajero.", List.of());
		}

		List<DispenseItem> usados = new ArrayList<>();
		long restante = montoCentavos;

		for (Cajero c : cajeros) {
			if (restante <= 0)
				break;
			int valor = c.getCentavos();
			int maxNecesario = (int) (restante / valor);
			int aUsar = Math.min(maxNecesario, c.getCantidadTotal());
			if (aUsar > 0) {
				usados.add(new DispenseItem(c, aUsar));
				restante -= (long) aUsar * valor;
			}
		}

		if (restante != 0) {
			return new DispenseResult(false,
					"No se puede dispensar el monto exacto con las denominaciones disponibles.", List.of());
		}

		// Actualizar el Cajero
		for (DispenseItem item : usados) {
			Cajero c = item.cajero;
			c.setCantidadTotal(c.getCantidadTotal() - item.cantidad);
		}
		repository.saveAll(cajeros);

		return new DispenseResult(true, "Retiro exitoso.", usados);
	}

	public List<Cajero> getInventario() {
		return repository.getByIdCajero();
	}
	
	@Transactional
    public void reset() {
        repository.deleteAll();

        // VUELVE A INSERTAR Y RESETEA LOS VALORES ANTERIORES USADOS EN ORACLE
        List<Cajero> inicial = List.of(
                new Cajero(null, "Billete", "1000", 100000, 2),
                new Cajero(null, "Billete", "500", 50000, 5),
                new Cajero(null, "Billete", "200", 20000, 10),
                new Cajero(null, "Billete", "100", 10000, 20),
                new Cajero(null, "Billete", "50", 5000, 30),
                new Cajero(null, "Billete", "20", 2000, 40),
                new Cajero(null, "Moneda", "10", 1000, 50),
                new Cajero(null, "Moneda", "5", 500, 100),
                new Cajero(null, "Moneda", "2", 200, 200),
                new Cajero(null, "Moneda", "1", 100, 300),
                new Cajero(null, "Moneda", "0.50", 50, 100)
        );

        repository.saveAll(inicial);
    }
}
