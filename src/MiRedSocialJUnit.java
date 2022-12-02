import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.junit.Test;


public class MiRedSocialJUnit {
	
	static public String ficheroRef = "miredsocial.ref";
	
	private static int nNodos;
	private static String [] nodos; // nodos
	private static List<Integer>[] adyacentes; //nodos adyacentes
	private static String popular;
	private static class InfoDist {
		String name;
		int [] dist;
	}
	
	private static ArrayList<InfoDist> distancias = new ArrayList<InfoDist>();
	
	private static class Data {
		String name;
		ArrayList<String> elements;
		public Data() {
			elements=new ArrayList<String>();
		}
	}
	
	private static class Tinfo {
		String name;
		ArrayList<Data> lista;
		
		public Tinfo() {
			lista = new ArrayList<Data>();
		}
	}

	//followers
	private static ArrayList<Data> seg = new ArrayList<> ();
	//sugerencias
	private static ArrayList<Data> su = new ArrayList<>();
	//common Followers
	private static ArrayList<Tinfo> coF = new ArrayList<Tinfo>();
	//common followed
	private static ArrayList<Tinfo> coff = new ArrayList<Tinfo>();
	
	private static void leerDatos() {
		RandomAccessFile ref=null;
		try {
			ref = new RandomAccessFile(ficheroRef, "r");
			
		} catch(FileNotFoundException e) {
			System.err.println("  No se pudo abrir el fichero");
			return;
		}
		
		try {
			//leer Nodos y Arcos
			nNodos=ref.readInt();
			System.out.println("nNodos: "+nNodos);
			nodos = new String[nNodos];
			adyacentes =   new ArrayList[nNodos];
			for (int i=0; i<nNodos; i++) {
				nodos[i]=ref.readUTF();
				int nadyacentes = ref.readInt();
				adyacentes[i] = new ArrayList();
				for (int j=0; j<nadyacentes; j++) 
					adyacentes[i].add(ref.readInt());
			}
			
			popular = ref.readUTF(); //most popular
			
			for (int i=0; i<nNodos; i++) {
				InfoDist info=new InfoDist();
				info.name = ref.readUTF();
				info.dist = new int[nNodos];
				for (int j=0; j<nNodos; j++) 
					info.dist[j] = ref.readInt();
				distancias.add(info);
			}
			

			for (int i=0; i<nNodos; i++) {
				String n1 = ref.readUTF();
				Data inf_se = new Data();
				Data inf_su = new Data();
				inf_se.name =n1;
				int nse = ref.readInt();
				for (int k=0; k<nse; k++)
					inf_se.elements.add(ref.readUTF());

				seg.add(inf_se);

				inf_su.name=n1;
				int nsu = ref.readInt();
				for (int k=0; k<nsu; k++)
					inf_su.elements.add(ref.readUTF());

				su.add(inf_su);

				Tinfo inf_cfers = new Tinfo();
				Tinfo inf_cfed = new Tinfo();
				inf_cfers.name =n1;
				inf_cfed.name =n1;
				for (int j=0; j<nNodos-1; j++) {
					Data d = new Data();
					String n2 = ref.readUTF();
					d.name = n2;
					int nElem = ref.readInt();
					for (int k=0; k<nElem; k++)
						d.elements.add(ref.readUTF());
					inf_cfers.lista.add(d);
					
					Data d2 = new Data();
					d2.name = n2;
					nElem = ref.readInt();
					for (int k=0; k<nElem; k++)
						d2.elements.add(ref.readUTF());
					inf_cfed.lista.add(d2);
				}
				coF.add(inf_cfers);
				coff.add(inf_cfed);
					
			}
			ref.close();
		} catch (IOException e) {
			System.err.println("Error en la lectura del fichero de referencia");
			return;
		}
	}



	@Test
	public final void testinsertEdge() {
		HashMap<Character,Integer> nodos=new HashMap<>();
		HashMap<Character,List<Character>> arcos = new HashMap<>();
		HashMap<Character,List<Integer>> pesos = new HashMap<>();
		ArrayList<Character> vnodos = new ArrayList<>();
		for (char c='A'; c<='Z'; c++) {
			vnodos.add(c);
		}
		//pruebas grafo dirigido sin peso
		EDGraph<Character, Object> grafo1 = new EDListGraph(false);
		System.out.println("Probando insertEdge (origen, destino, peso)");

		//mismo origen y destino, nodo no está
		Character origen=vnodos.get(0);
		Character destino=vnodos.get(0);
		int index = grafo1.getNodeIndex(origen);
		boolean obtenido=grafo1.insertEdge(origen, destino, null);
		System.out.println(" insertando ('"+origen+"', '"+destino+ "',null)");
		System.out.println("     esperado: false ");
		System.out.println("     obtenido: "+obtenido);

		assertFalse(obtenido);
		int indexAfter = grafo1.getNodeIndex(origen);
		assertEquals(-1, indexAfter);

		//mismo origen y destino, nodo sí esta
		index = grafo1.insertNode(origen);
		nodos.put(origen,index);
		arcos.put(origen, new ArrayList<>());
		obtenido=grafo1.insertEdge(origen, destino, null);
		System.out.println(" insertando ('"+origen+"', '"+destino+ "',null)");
		System.out.println("     esperado: false ");
		System.out.println("     obtenido: "+obtenido);

		assertFalse(obtenido);
		indexAfter = grafo1.getNodeIndex(origen);
		assertEquals(index,indexAfter);
		EDEdge<Object> ed = grafo1.getEdge(origen,destino);
		assertNull(ed);

		boolean esperado;
		for (int i =0; i<vnodos.size(); i++) {
			origen = vnodos.get(i);
			Integer index_or = grafo1.getNodeIndex(origen);
			Integer index_or_esperado = nodos.get(origen);
			if (index_or_esperado==null)
				assertEquals(-1,(int) index_or);
			else
				assertEquals(index_or_esperado, index_or);

			for (int j=i+1; j<vnodos.size()-1; j++) {
				destino = vnodos.get(j);
				Integer index_dest = grafo1.getNodeIndex(destino);
				Integer index_dest_esperado = nodos.get(destino);
				if (index_dest_esperado==null)
					assertEquals(-1,(int) index_dest);
				else
					assertEquals(index_dest_esperado, index_dest);

				obtenido=grafo1.insertEdge(origen, destino, null);

				if (!nodos.containsKey(origen) || !nodos.containsKey(destino))
					esperado = true;
				else if (nodos.containsKey(origen) && nodos.containsKey(destino) && arcos.get(origen).contains(destino))
					esperado = false;
				else
					esperado = true;
				//comprobando que devuelve el valor esperado
				System.out.println(" insertando ('"+origen+"', '"+destino+ "',null)");
				System.out.println("     esperado: "+esperado);
				System.out.println("     obtenido: "+obtenido);

				//no dirigido: comprobando que está el arco y en ambos sentidos
				index_or_esperado = grafo1.getNodeIndex(origen);
				index_dest_esperado = grafo1.getNodeIndex(destino);
				ed = grafo1.getEdge(index_or_esperado,index_dest_esperado);
				if (ed!=null)
					System.out.println("  arco insertado: "+ed.toString());
				else
					System.out.println(" Error: arco es null. No se ha insertado");
				assertNotNull(ed);
				EDEdge<Object> inverso = grafo1.getEdge(index_dest_esperado, index_or_esperado);
				if (inverso!=null)
					System.out.println("  Grafo no dirigido. Arco inverso insertado: "+inverso.toString());
				else
					System.out.println(" Error: arco es null. Grafo no dirigido. No se ha insertado arco inverso");
				assertNotNull(inverso);

				if (obtenido) {
					if (!nodos.containsKey(origen)) {
						nodos.put(origen, index_or_esperado);
						arcos.put(origen, new ArrayList<>());
					}
					if (!nodos.containsKey(destino)) {
						nodos.put(destino, index_dest_esperado);
						arcos.put(destino, new ArrayList<>());
					}

					arcos.get(origen).add(destino);
					arcos.get(destino).add(origen); //no dirigido
				}

			}
		}

		//Comprobando que no se insertan arcos inversos (repetidos)
		for (int i=vnodos.size()/2; i>0; i--) {
			origen = vnodos.get(i);
			int index_or = grafo1.getNodeIndex(origen);
			assertEquals((int) nodos.get(origen), index_or);

			for  (int j=i-1; j>=0; j--) {
				destino = vnodos.get(j);
				int index_dest = grafo1.getNodeIndex(destino);
				assertEquals((int) nodos.get(destino), index_dest);

				//comprobando que ya existe el arco
				ed = grafo1.getEdge(index_or, index_dest);
				assertNotNull(ed);

				//comprobando que no se añade
				obtenido = grafo1.insertEdge(origen,destino,null);
				esperado = false;
				System.out.println ("   insertando ('"+origen+"', '"+destino+"', null)");
				System.out.println("      Esperado: "+esperado);
				System.out.println("      Obtenido: "+obtenido);
				assertFalse(obtenido);
			}

		}

		//Comprobando que no se insertan arcos repetidos
		for (int i=0; i<vnodos.size()/2; i++) {
			origen = vnodos.get(i);
			int index_or = grafo1.getNodeIndex(origen);
			assertEquals((int) nodos.get(origen), index_or);

			for  (int j=i+1; j<(vnodos.size()/2)-1; j++) {
				destino = vnodos.get(j);
				int index_dest = grafo1.getNodeIndex(destino);
				assertEquals((int) nodos.get(destino), index_dest);

				//comprobando que ya existe el arco
				ed = grafo1.getEdge(index_or, index_dest);
				assertNotNull(ed);

				//comprobando que no se añade
				obtenido = grafo1.insertEdge(origen,destino,null);
				esperado = false;
				System.out.println ("   insertando ('"+origen+"', '"+destino+"', null)");
				System.out.println("      Esperado: "+esperado);
				System.out.println("      Obtenido: "+obtenido);
				assertFalse(obtenido);
			}

		}

		//Comprobando grafo dirigido con pesos
		nodos=new HashMap<>();
		arcos = new HashMap<>();
		pesos = new HashMap<>();

		ArrayList<Integer> vpesos = new ArrayList<>();
		for (int i=0; i<vnodos.size(); i++)
			vpesos.add(i+10);
		//pruebas grafo dirigido sin peso
		EDGraph<Character,Integer> grafo2 = new EDListGraph(true);
		System.out.println("Probando insertEdge (origen, destino, peso)");

		//mismo origen y destino, nodo no está
		origen=vnodos.get(0);
		destino=vnodos.get(0);
		int weight = vpesos.get(0);
		index = grafo2.getNodeIndex(origen);
		obtenido=grafo2.insertEdge(origen, destino, weight);
		System.out.println(" insertando ('"+origen+"', '"+destino+ "',"+weight+")");
		System.out.println("     esperado: false ");
		System.out.println("     obtenido: "+obtenido);

		assertFalse(obtenido);
		indexAfter = grafo2.getNodeIndex(origen);
		assertEquals(-1, indexAfter);

		//mismo origen y destino, nodo sí esta
		index = grafo2.insertNode(origen);
		nodos.put(origen,index);
		arcos.put(origen, new ArrayList<>());
		pesos.put(origen, new ArrayList<>());
		obtenido=grafo2.insertEdge(origen, destino, weight);
		System.out.println(" insertando ('"+origen+"', '"+destino+ "',"+weight+")");
		System.out.println("     esperado: false ");
		System.out.println("     obtenido: "+obtenido);

		assertFalse(obtenido);
		indexAfter = grafo2.getNodeIndex(origen);
		assertEquals(index,indexAfter);
		EDEdge<Integer> ed2 = grafo2.getEdge(origen,destino);
		assertNull(ed2);

		for (int i =0; i<vnodos.size(); i++) {
			origen = vnodos.get(i);
			weight = vpesos.get(i);
			Integer index_or = grafo2.getNodeIndex(origen);
			Integer index_or_esperado = nodos.get(origen);
			if (index_or_esperado==null)
				assertEquals(-1,(int) index_or);
			else
				assertEquals(index_or_esperado, index_or);

			for (int j=i+1; j<vnodos.size()-1; j++) {
				destino = vnodos.get(j);
				Integer index_dest = grafo2.getNodeIndex(destino);
				Integer index_dest_esperado = nodos.get(destino);
				if (index_dest_esperado==null)
					assertEquals(-1,(int) index_dest);
				else
					assertEquals(index_dest_esperado, index_dest);

				obtenido=grafo2.insertEdge(origen, destino,weight);

				if (!nodos.containsKey(origen) || !nodos.containsKey(destino))
					esperado = true;
				else if (nodos.containsKey(origen) && nodos.containsKey(destino) && arcos.get(origen).contains(destino))
					esperado = false;
				else
					esperado = true;
				//comprobando que devuelve el valor esperado
				System.out.println(" insertando ('"+origen+"', '"+destino+ "',"+weight+")");
				System.out.println("     esperado: "+esperado);
				System.out.println("     obtenido: "+obtenido);

				//comprobando que está el arco
				index_or_esperado = grafo2.getNodeIndex(origen);
				index_dest_esperado = grafo2.getNodeIndex(destino);
				ed2 = grafo2.getEdge(index_or_esperado,index_dest_esperado);
				if (ed2!=null)
					System.out.println("  arco insertado: "+ed2.toString());
				else
					System.out.println(" Error: arco es null. No se ha insertado");
				assertNotNull(ed2);

				if (obtenido) {
					if (!nodos.containsKey(origen)) {
						nodos.put(origen, index_or_esperado);
						arcos.put(origen, new ArrayList<>());
						pesos.put(origen, new ArrayList<>());
					}
					if (!nodos.containsKey(destino)) {
						nodos.put(destino, index_dest_esperado);
						arcos.put(destino, new ArrayList<>());
						pesos.put(destino, new ArrayList<>());
					}

					arcos.get(origen).add(destino);  //dirigido

					pesos.get(origen).add(weight);
				}

			}
		}

		//Comprobando que no se insertan arcos repetidos
		for (int i=0; i<vnodos.size()/2; i++) {
			origen = vnodos.get(i);
			weight = vpesos.get(i);
			int index_or = grafo2.getNodeIndex(origen);
			assertEquals((int) nodos.get(origen), index_or);

			for  (int j=i+1; j<(vnodos.size()/2)-1; j++) {
				destino = vnodos.get(j);
				int index_dest = grafo2.getNodeIndex(destino);
				assertEquals((int) nodos.get(destino), index_dest);

				//comprobando que ya existe el arco
				ed2 = grafo2.getEdge(index_or, index_dest);
				assertNotNull(ed2);

				//comprobando que coincide con la información ya guardada
				int indexLista = arcos.get(origen).indexOf(destino);
				assertEquals(pesos.get(origen).get(indexLista),ed2.getWeight());

				//comprobando que no se añade
				obtenido = grafo2.insertEdge(origen,destino, weight);
				esperado = false;
				System.out.println ("   insertando ('"+origen+"', '"+destino+"',"+weight+")");
				System.out.println("      Esperado: "+esperado);
				System.out.println("      Obtenido: "+obtenido);
				assertFalse(obtenido);
			}

		}

		//Comprobando que se puede insertar el arco inverso
		ArrayList<Integer> vpesos2= new ArrayList<>();
		for (int i=0; i<vnodos.size(); i++)
			vpesos2.add(i+50);
		for (int i=vnodos.size()-1; i>vnodos.size()-1/2; i--) {
			origen=vnodos.get(i);
			weight=vpesos2.get(i);
			for (int j=i-1; j>vnodos.size()-1/2; j--) {
				destino=vnodos.get(j);

				//Comprobar que no esta
				ed2 = grafo2.getEdge(origen,destino);
				assertNull(ed2);

				Integer index_dest = grafo2.getNodeIndex(destino);
				Integer index_dest_esperado = nodos.get(destino);
				if (index_dest_esperado==null)
					assertEquals(-1,(int) index_dest);
				else
					assertEquals(index_dest_esperado, index_dest);

				obtenido=grafo2.insertEdge(origen, destino,weight);

				if (!nodos.containsKey(origen) || !nodos.containsKey(destino))
					esperado = true;
				else if (nodos.containsKey(origen) && nodos.containsKey(destino) && arcos.get(origen).contains(destino))
					esperado = false;
				else
					esperado = true;
				//comprobando que devuelve el valor esperado
				System.out.println(" insertando ('"+origen+"', '"+destino+ "',"+weight+")");
				System.out.println("     esperado: "+esperado);
				System.out.println("     obtenido: "+obtenido);

				//comprobando que está el arco
				int index_or_esperado = grafo2.getNodeIndex(origen);
				index_dest_esperado = grafo2.getNodeIndex(destino);
				ed2 = grafo2.getEdge(index_or_esperado,index_dest_esperado);
				if (ed2!=null)
					System.out.println("  arco insertado: "+ed2.toString());
				else
					System.out.println(" Error: arco es null. No se ha insertado");
				assertNotNull(ed2);

				if (obtenido) {
					if (!nodos.containsKey(origen)) {
						nodos.put(origen, index_or_esperado);
						arcos.put(origen, new ArrayList<>());
						pesos.put(origen, new ArrayList<>());
					}
					if (!nodos.containsKey(destino)) {
						nodos.put(destino, index_dest_esperado);
						arcos.put(destino, new ArrayList<>());
						pesos.put(destino, new ArrayList<>());
					}

					arcos.get(origen).add(destino);  //dirigido
					pesos.get(origen).add(weight);
				}

			}
		}

	}
	@Test
	public final void  testComprobarGrafo() {
		
		String filename = "miredsocial.txt";
		EDGraph grafo = MiRedSocial.leerGrafo(filename);

		if (grafo == null)
			fail("No se pudo leer el grafo");

		leerDatos();
		
		System.out.println(" Comprobando el grafo ");
		
		System.out.println("  Numero de nodos ");
		assertEquals(nNodos, grafo.getSize());
		
		for (int i=0; i<nNodos; i++) {
			int index = grafo.getNodeIndex(nodos[i]);
			System.out.println("Comprobar que están todos los nodos. Nodo "+nodos[i]);
			assertNotEquals(index,-1);
			
			Set<Integer> ady = grafo.getAdyacentNodes(index);
			System.out.println("Comprobar el número de nodos adyacentes: "+adyacentes[i].size());
			assertEquals(adyacentes[i].size(),ady.size());
			System.out.println("Comprobar los adyacentes ");
			assertTrue(ady.containsAll(adyacentes[i]));
		}
		
	}	
		
	@Test
	public final void testComprobarPopular() {
		String filename = "miredsocial.txt";
		EDGraph grafo = MiRedSocial.leerGrafo(filename);

		if (grafo == null)
			fail("No se pudo leer el grafo");

		leerDatos();
		
		System.out.println("Más popular: "+popular);
		assertEquals(popular,grafo.mostInfluencer());
		
	}

	
	@Test
	public final void testComprobarDistancias() {
		String filename = "miredsocial.txt";
		EDGraph grafo = MiRedSocial.leerGrafo(filename);

		if (grafo == null)
			fail("No se pudo leer el grafo");

		leerDatos();
		
		System.out.println("Comprobando las distancias entre cada par de nodos ");
		for (int i=0; i<nNodos; i++) {
			InfoDist info = distancias.get(i);
			int index = grafo.getNodeIndex(info.name);
			assertNotEquals(index, -1);
			int [] res = grafo.distanceToAll(info.name);
			assertEquals(info.dist.length, res.length);
			System.out.print("   "+info.name+": ");
			for (int j=0; j<nNodos; j++) {
				System.out.print(info.dist[j]+" ");
				assertEquals(info.dist[j], res[j]);
			}
			System.out.println();
		}
		
	}


    @Test
    public final void testComprobarCommonFollowers() {
        String filename = "miredsocial.txt";
        EDGraph grafo = MiRedSocial.leerGrafo(filename);

        if (grafo == null)
            fail("No se pudo leer el grafo");

        leerDatos();

        System.out.println("Comprobando seguidores comunes (common followers) entre cada par de nodos");
        for (int i=0; i<nNodos; i++) {
            Tinfo info = coF.get(i);
            String name1= info.name;
            for (int j=0; j<info.lista.size(); j++) {
                Data data = info.lista.get(j);
                String name2 = data.name;
                Set<String> amigos = grafo.commonFollowers(name1, name2);
				System.out.println("Para "+name1+" y "+name2);
				System.out.println("   Esperado: "+data.elements.size()+ " seguidores comunes. "+data.elements);
				System.out.println("   Obtenido: "+amigos.size()+" seguidores comunes. "+amigos);
                assertEquals(data.elements.size(), amigos.size());
                assertTrue(amigos.containsAll(data.elements));
            }
        }

    }

	@Test
	public final void testComprobarCommonFollowed() {
		String filename = "miredsocial.txt";
		EDGraph grafo = MiRedSocial.leerGrafo(filename);

		if (grafo == null)
			fail("No se pudo leer el grafo");

		leerDatos();

		System.out.println("Comprobando seguidos comunes (common followed) entre cada par de nodos");
		for (int i=0; i<nNodos; i++) {
			Tinfo info = coff.get(i);
			String name1= info.name;
			for (int j=0; j<info.lista.size(); j++) {
				Data data = info.lista.get(j);
				String name2 = data.name;
				System.out.println("Seguidos comunes de "+name1+" y "+name2+": ");
				Set<String> amigos = grafo.commonFollowed(name1, name2);

				System.out.println("   Esperado: "+data.elements.size()+ " seguidos comunes. "+data.elements);
				System.out.println("   Obtenido: "+amigos.size()+" seguidos comunes. "+amigos);
				assertEquals(data.elements.size(), amigos.size());
				assertTrue(amigos.containsAll(data.elements));
			}
		}

	}

	@Test
	public final void testComprobarSuggest() {
		String filename = "miredsocial.txt";
        EDGraph grafo = MiRedSocial.leerGrafo(filename);

        if (grafo == null)
            fail("No se pudo leer el grafo");

		leerDatos();
		
		System.out.println("Comprobando amigos sugeridos para cada nodo");
		for (int i=0; i<nNodos; i++) {
			Data info = su.get(i);
			String name1= info.name;
			System.out.println("Para "+name1+": ");
			Set<String> amigos = grafo.suggest(name1);
			assertEquals(info.elements.size(), amigos.size());
			System.out.println("Esperado: "+info.elements);
			System.out.println("Obtenido: "+amigos);
			assertTrue(amigos.containsAll(info.elements));
		}
					
	}

	@Test
	public final void testComprobarFollowers() {
		String filename = "miredsocial.txt";
		EDGraph grafo = MiRedSocial.leerGrafo(filename);

		if (grafo == null)
			fail("No se pudo leer el grafo");

		leerDatos();

		System.out.println("Comprobando followers para cada nodo");
		for (int i=0; i<nNodos; i++) {
			Data info = seg.get(i);
			String name1= info.name;
			System.out.println("Para "+name1+": ");
			Set<String> amigos = grafo.followers(name1);
			assertEquals(info.elements.size(), amigos.size());
			System.out.println("   Esperado: "+info.elements);
			System.out.println("   Obtenido: "+amigos);
			assertTrue(amigos.containsAll(info.elements));
		}

	}
}
