import java.io.*;
import java.net.*;
import java.util.*;

public class Leilao {

  	private static ServerSocket ss = null;
  	
  	private static Socket soc = null;
  	
  	private static final int max = 7;
  	private static  List<userThread> thrds = new ArrayList<>(Collections.nCopies(max, null));

  public static void main(String args[]) {

    int port = 2222;

	List<Servidor> auctionList = new LinkedList<Servidor>();
	Servidor s1 = new Servidor(1, "s1.micro", 10, 30, 1);
	auctionList.add(s1);
	Servidor s2 = new Servidor(2, "s2.micro", 10, 35, 0);
	auctionList.add(s2);
	Servidor s3 = new Servidor(3, "s3.medium", 20, 45, 1);
	auctionList.add(s3);
	Servidor s4 = new Servidor(4, "s4.medium", 20, 55, 0);
	auctionList.add(s4);
	Servidor s5 = new Servidor(5, "s5.large", 30, 90, 1);
	auctionList.add(s5);
	Servidor s6 = new Servidor(6, "s6.large", 30, 100, 0);
	auctionList.add(s6);
	
	// Abre o servidor e set timeout
	try {
      System.out.println("TCP Server inicializado.");

      ss = new ServerSocket(port);
      //ss.setSoTimeout(500);
      System.out.println("Esperando conexões de clientes.");

    } catch (IOException e) {
    	e.printStackTrace();
	}

	// Cria um socket para cada conexao e passa o para o novo user thread	
	while (true) {

		// Tentativa de conexao 
		try {

			soc = ss.accept();
			System.out.println("Cliente conectado...");
			for (int i = 0; i < max; i++) {
			  if (thrds.get(i) == null) {
				thrds.set(i, new userThread(soc, thrds,false,auctionList));
				(thrds.get(i)).start();
				break;
			  }
			}
		}
		catch (IOException e) {
		//System.out.println(e); -> Faz aparecer os timeouts todos
		}
      } 
    }
}