package contratos;

import contratos.iPizarraJuego;

/**
 *
 * @author Sebastian Moreno
 */
public interface iObservador {
    
    void actualiza(iPizarraJuego pizarra, String evento);
    
}
