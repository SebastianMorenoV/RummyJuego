/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package contratos;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Sebastian Moreno
 */
public interface iAgentePartida {

    public Map<String, String> repartirManos(List<String> jugadorIds);

    public void setMazoSerializado(String mazoSerializado);

    public String getMazoSerializado();
}
