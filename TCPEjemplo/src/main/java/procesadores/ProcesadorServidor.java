package procesadores;

import contratos.iDespachador;
import contratos.iPizarraJuego;
import contratos.iProcesador;

/**
 * Lógica del Servidor. Su diseño es robusto y cumple su función. Sabe qué hacer
 * con "REGISTRAR" y "MOVER". Al recibir "MOVER", reenvía el payload intacto a
 * los demás clientes.
 *
 * @author Benja
 */
public class ProcesadorServidor implements iProcesador {

    private final iPizarraJuego pizarra;

    public ProcesadorServidor(iPizarraJuego pizarra) {
        this.pizarra = pizarra;
    }

    @Override
    public String procesar(String ipCliente, String mensaje) {
        String[] partes = mensaje.split(":", 3);
        if (partes.length < 2) {
            return "ERROR: Formato inválido.";
        }

        String idCliente = partes[0];
        String comando = partes[1];
        String payload = (partes.length > 2) ? partes[2] : "";
        System.out.println("COMANDO DESDE PROCESADOR SERVIDOR: " + comando);

        // Procesador "tonto" (dummy)
        // Para CUALQUIER OTRO comando, simplemente pasa el comando y el payload CRUDO
        // a la pizarra. La pizarra decidirá qué hacer.
        boolean resultado = pizarra.procesarComando(idCliente, comando, payload);

        if (!resultado) {
            return new String("NO_TE_JALASTE"); // O JUGADA_INVALIDA
        }

        return new String("MOVIMIENTO_RECIBIDO_OK");
    }
}
