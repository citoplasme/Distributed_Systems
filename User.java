import java.io.*;
import java.net.*;

public class User implements Runnable {
	private static Socket cs;
	private static PrintStream ps;
	private static InputStreamReader leitor;
	private static BufferedReader buf;
	private static BufferedReader input;
	private static boolean fechado;

	public static void main(String[] args) {
		int porta = 2222;
		String host = "localhost";

		try {
			cs = new Socket(host, porta);
			input = new BufferedReader(new InputStreamReader(System.in));
			ps = new PrintStream(cs.getOutputStream());
			leitor = new InputStreamReader(cs.getInputStream());
			buf = new BufferedReader(leitor);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if (cs != null && ps != null && buf != null) {
			try {
				// inicializa a Thread para o user
				new Thread(new User()).start();

				// Permite que se escreva ate o socket estar fechado
				while(!fechado) {
					ps.println(input.readLine().trim());
				}

				// fechar tudo
				input.close();
				ps.close();
				leitor.close();
				buf.close();
				cs.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void run() {
		String s;
		try {
			// loop para ler dados do server e imprimir
			while((s = this.buf.readLine()) != null) {
				System.out.println(s);
			// Acaba ao receber esta mensagem
				if (s.equals("-> Desconectado do servidor :)")) break;
			}
			fechado = true; 
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}