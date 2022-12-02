import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MiRedSocial {


	/**Ejercicio 1 - leerGrafo
	 *
	 * @param nomfich Nombre del fichero con la información del grafo
	 * @return devuelve un grafo dirigido con los nodos y arcos leídos del fichero
	 */
	public static EDGraph<String, Object>  leerGrafo(String nomfich) {
		EDListGraph<String, Object> grafo = null;

		try { 
			Scanner inf = new Scanner (new FileInputStream(nomfich));
			grafo = new EDListGraph<>(true);

			while (inf.hasNext()) {
				String origen = inf.next();
				String destino = null;
				if (inf.hasNext()) {
					destino = inf.next();
				}
				//Insertar aquí los nodos origen y destino, si no existen aún
				//Y el arco <origen, destino>
				
					
			}
						
			inf.close();
		} catch(FileNotFoundException e){
			System.out.println("Error al abrir el fichero " + nomfich);
			return null;
		}

		return grafo;
		
	}
}
