package contratos;

/**
 *
 * @author benja
 */
public interface iAgenteConocimiento {

    String getComandoQueManeja();

    iResultadoComando ejecutar(String idCliente, String payload);
}
