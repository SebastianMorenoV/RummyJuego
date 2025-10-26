package contratos;

public interface iAgenteConocimiento {

    String getComandoQueManeja();

    iResultadoComando ejecutar(String idCliente, String payload);
}
