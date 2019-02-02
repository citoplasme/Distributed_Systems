import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.Duration;

public class userThread extends Thread implements Serializable {
	private String nome;
	private String password;
	private float divida; // tem de atualizar qd o gajo larga o servidor
	
	private BufferedReader buf;
	private PrintStream ps;
	private Socket cs;
	private List<userThread> threads;
	private int maxClientes;
	private boolean autenticado;
	private List<Servidor> servidores;

	public userThread(Socket cs, List<userThread> threads,boolean estado, List<Servidor> list) {
		this.cs = cs;
		this.setThreads(threads);
		this.maxClientes = threads.size();
		this.setList(list);
		this.autenticado = estado;
	}
	
	public synchronized void setThreads(List<userThread> t) {
		List<userThread> l = new ArrayList<>();
		for (userThread th : t) {
			l.add(th);
		}
		this.threads = l;
	}

	public synchronized void setList(List<Servidor> t) {
		List<Servidor> l = new ArrayList<>();
		for (Servidor s : t) {
			l.add(s);
		}
		this.servidores = l;
	}

	public void run() {
		int max = this.maxClientes;
		List<userThread> l = this.threads;

		try {
			buf = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			ps = new PrintStream(cs.getOutputStream());

			this.ps.println("-> Coneccao efetuada com sucesso. ");
			this.ps.println("-> Proceda com o login.");
			this.ps.println("-> Para obter ajuda digite o comando help.");
			
			String s = "";
			while(!autenticado) {
				s = buf.readLine().toUpperCase();
				if (s.equals("")) {}
				else if (s.startsWith("EXIT")) {
					break;
				}
				else if (s.startsWith("SIGNUP")) {
					autenticado = this.signup(s);
				}
				else if (s.startsWith("LOGIN")) {
					autenticado = this.login(s);
				}
				else if (s.startsWith("HELP")) {
					this.help();
				}
				else {
					this.ps.println("-> Autenticacao necessaria antes de introduzir outros comandos.");
					System.out.println("-> Utilizador nao autenticado tentou executar o comando: " + s);
				}
			}


			while(true) {
				String linha = buf.readLine().toUpperCase();
				if (linha.equals("")) {}
				else if (linha.startsWith("EXIT")) {
					autenticado = false;
					break;
				}
				else if (linha.startsWith("LOGIN")) {
					this.ps.println("-> Já se encontra autenticado com o email " + this.nome);
				}
				else if (linha.startsWith("LIST")) {
					list(this.servidores);
				}
				else if (linha.startsWith("BID")) {
					bid(linha);
				}
				else if (linha.startsWith("BUY")) {
					buy(linha);
				}
				else if (linha.startsWith("RELEASE")) {
					release(linha);
				}
				else if (linha.startsWith("DEBT")) {
					debt(linha);
				}
				else if (linha.startsWith("MINE")) {
					mine(this.servidores);
				}
				else if (linha.startsWith("WANT")) {
					want(linha);
				}
				else if (linha.startsWith("HELP")) {
					help();
				}
				else {
					this.ps.println("-> " + linha + " não é um comando suportado.");
					System.out.println(nome + " tentou executar o comando: " + linha);				}
			}

			this.ps.println("-> Desconectado do servidor :)");

			synchronized(this) {
				for (int i = 0; i < maxClientes; i++) {
					if (this.threads.get(i).equals(this)) {
						threads.set(i, null);
					}
				}
			}
			buf.close();
			ps.close();
			cs.close();
			System.out.println(nome + " desconectado.");

		} catch(IOException e) {}
	}

// Tem que escrever: login <nome> <password>
	public boolean login(String s) {
		String[] k = s.split(" ",3);
		try {
			if ((k[1].equals("JOAO@MAIL.COM") && k[2].equals("123")) || (k[1].equals("BRUNO@MAIL.COM") && k[2].equals("456")) || (k[1].equals("PEDRO@MAIL.COM") && k[2].equals("789")) || (k[1].equals("ROD@MAIL.COM") && k[2].equals("123"))) {
				synchronized(this) {
					for (int i = 0; i < this.maxClientes; i++) {
						if (threads.get(i) != null && threads.get(i).nome.equals(k[1]) && threads.get(i).autenticado == true) {
							this.ps.println("Utilizador " + k[1] + " ja se encontra autenticado.");
							System.out.println("Tentativa de login duplo para o utilizador " + k[1]);
							return false;
						}
					}
				}
				this.nome = k[1];
				this.password = k[2];
				this.ps.println("-> Autenticado como " + this.nome);
				System.out.println(this.nome + " autenticado" );
				return true;
			} else {
				this.ps.println("-> Utilizador inválido " + k[1] + ". Tente de novo.");
				System.out.println("Autentificação inválida para o utilizador " + k[1]);
				return false;
			}
			} catch (ArrayIndexOutOfBoundsException ex) {
				this.ps.println("-> Uso errado do comando login");
				this.ps.println("-> login <mail> <password>");
				System.out.println("Tentativa de uso do comando " + s);
				return false;
			}
	}

// signup <mail> <password> 
	public boolean signup(String s) {
		String[] k = s.split(" ",3);
		try {
			if ((!k[1].equals("JOAO@MAIL.COM")) && (!k[1].equals("BRUNO@MAIL.COM")) && (!k[1].equals("PEDRO@MAIL.COM")) && (!k[1].equals("ROD@MAIL.COM"))){
				synchronized(this) {
					for (int i = 0; i < this.maxClientes; i++) {
						if (threads.get(i) != null && threads.get(i).nome.equals(k[1])) {
							this.ps.println("Utilizador " + k[1] + " ja se encontra no sistema.");
							System.out.println("Tentativa de registo duplo para o utilizador " + k[1]);
							return false;
						}
					}
				}
				this.nome = k[1];
				this.password = k[2];
				this.ps.println("-> Autenticado como " + this.nome);
				System.out.println(this.nome + " autenticado" );
				return true;
			} else {
				this.ps.println("-> Utilizador inválido " + k[1] + ". Tente de novo.");
				System.out.println("Autentificação inválida para o utilizador " + "k[1]");
				return false;
			}
			} catch (ArrayIndexOutOfBoundsException ex) {
				this.ps.println("-> Uso errado do comando signup");
				this.ps.println("-> signup <mail> <password>");
				System.out.println("Tentativa de uso do comando " + s);
				return false;
			}
	}


// bid <item> <valor>
public void bid(String s) {
	try {
		String[] k = s.split(" ", 3);
		System.out.println(this.nome + " executou o comando " + s);

		synchronized(this) {
			int aux = 0;
			for (Servidor se : this.servidores) {
				if (se.getEstado() == 1) aux++;
			}
			if(servidores.size() == 0 || aux == 0) {
				this.ps.println("-> Não há servidores para licitar.");
			}
			else {
				ListIterator<Servidor> it = servidores.listIterator();
				try {
					ps = new PrintStream(cs.getOutputStream());
				} catch (IOException e) {
					System.out.println(e);
				}
				boolean existe = false;
				Servidor tmp;
				while(it.hasNext()) {
					tmp = it.next();

					if (tmp.getId() == Integer.parseInt(k[1])) {
						existe = true;
					
						if (tmp.getEstado() == 1) {
							if (tmp.getprecoBase() <= Float.parseFloat(k[2])) {

								if (tmp.getComprador() == "" || tmp.getBid() < Float.parseFloat(k[2])) {
									
									tmp.setComprador(this.nome);
									tmp.setBid(Float.parseFloat(k[2]));
									tmp.setBids(tmp.getBids() + 1);
									if (tmp.getBids() < 5) {
										this.ps.println("-> Possui o lance mais elevado para o servidor " + tmp.getId() + " com o valor " + tmp.getBid());
									}
									else if (tmp.getBids() >= 5) {
										tmp.setEstado(2);
										tmp.setAdq(LocalDateTime.now());
										this.ps.println("-> Possui o servidor " + tmp.getId() + ", tendo pago " + tmp.getBid());
									}
									
								}
								else {
									this.ps.println("-> O seu lance não é suficientemente alto para o servidor " + tmp.getId() + ". Lance atual: " + tmp.getBid() + ".");
								}
							} else {
								this.ps.println("-> O seu lance não é suficientemente elevado. O servidor possui um preço base de " + tmp.getprecoBase());
							}
						}
						else {
							this.ps.println("-> O servidor não se encontra em leilão.");
						}
					}
				}
				if (!existe) {
					this.ps.println("-> O servidor " + Integer.parseInt(k[1]) + "não existe.");
				}
			}
		}
	} catch(ArrayIndexOutOfBoundsException | NumberFormatException ee) {
		this.ps.println("-> Uso errado do comando bid");
		this.ps.println("-> bid <id do servidor> <valor>");
		System.out.println(this.nome + " tentou executar o comando " + s);
	} 
}

// list
public void list(List<Servidor> s) {

	System.out.println(this.nome + " enviou o comando list");
	if (this.servidores.size() == 0) {
		this.ps.println("-> Não há servidores disponiveis para aquisição.");
	}
	else {
		try {
			synchronized(this) {
			ps = new PrintStream(cs.getOutputStream());
			//for (Servidor serv : this.servidores) {
			ListIterator<Servidor> it = servidores.listIterator();
			Servidor serv;
				while(it.hasNext()) {
					serv = it.next();
				if (serv.getEstado() != 2) {
					this.ps.print("Servidor: " + serv.getId() + " Nome: " + serv.getNome() + " Lance atual: " + serv.getBid() + " Licitante: ");
					if (serv.getComprador().equals("")) this.ps.print("-");
					else this.ps.print(serv.getComprador());
					this.ps.print(" Estado: ");
					if (serv.getEstado() == 0) this.ps.println("VP");
					else this.ps.println("L");
				}
			}
		}
		} catch(IOException e) {
			System.out.println(e);
		}
	}
}

// release <item>
public synchronized void release(String s) {
	try {
		String[] k = s.split(" ", 2);
		System.out.println(this.nome + " executou o comando " + s);

		synchronized(this) {
			ListIterator<Servidor> it = servidores.listIterator();
			try {
				ps = new PrintStream(cs.getOutputStream());
			} catch (IOException e) {
				System.out.println(e);
			}
			boolean existe = false;
			Servidor tmp;
			LocalDateTime dttmp;
			while(it.hasNext()) {
				tmp = it.next();

				if (tmp.getId() == Integer.parseInt(k[1])) {
					existe = true;
				
					if (tmp.getEstado() == 2 && tmp.getComprador().equals(this.nome)) {	
						tmp.setComprador("");
						String ax = tmp.getTipo();
						int l = 0;
						int vp = 0;
						for (Servidor servi : servidores) {
							if(servi.getTipo().equals(tmp.getTipo())) {
								if(servi.getEstado() == 0) vp++;
								if (servi.getEstado() == 1) l++;
							}
						}
						if (l > vp) tmp.setEstado(0);
						else tmp.setEstado(1);
						dttmp = tmp.getAdq();
						tmp.setAdq(null);
						tmp.setBids(0);
						if (tmp.getBid() != tmp.getPVP()) {
							this.divida += tmp.getBid() * Duration.between(dttmp, LocalDateTime.now()).toHours();
						}
						tmp.setBid(0); 
						this.ps.println("-> O servidor " + tmp.getId() + " está livre");
					}
					else {
						this.ps.println("-> O servidor não está na sua posse.");
					}
				}
			}
			if (!existe) {
				this.ps.println("-> O servidor " + Integer.parseInt(k[1]) + "não existe.");
			}
		}
	} catch(ArrayIndexOutOfBoundsException | NumberFormatException ee) {
		this.ps.println("-> Uso errado do comando release");
		this.ps.println("-> release <id do servidor>");
		System.out.println(this.nome + " tentou executar o comando " + s);
	} 
}

// debt
public synchronized void debt(String s) {
	System.out.println(this.nome + " enviou o comando " + s);
	try {
		float x = this.divida;
		for (Servidor serv : this.servidores) {
			if (serv.getEstado() == 2 && serv.getComprador().equals(this.nome)) {
				x += serv.getBid() * Duration.between(serv.getAdq(), LocalDateTime.now()).toMinutes();
			}
		}

		ps = new PrintStream(cs.getOutputStream());
		this.ps.println("-> Dívida atual: " + x + ".");
	} catch(IOException e) {
		System.out.println(e);
	}
}

public synchronized void mine(List<Servidor> s) {
	System.out.println(this.nome + " enviou o comando mine");
	List<Servidor> l = new ArrayList<>();
	try {
		for (Servidor serv : s) {
			if (serv.getEstado() == 2 && serv.getComprador().equals(this.nome)) {
				l.add(serv);
			}
		}

		ps = new PrintStream(cs.getOutputStream());
		if (l.size() == 0) this.ps.println("-> Não possui qualquer servidor de momento.");
		else {
			for (Servidor srv : l) {
				this.ps.println("Servidor: " + srv.getId() + " Nome: " + srv.getNome() + " Adquirido: " + srv.getAdq().toString());		
			}
		}
		} catch(IOException e) {
			System.out.println(e);
		}
}

// buy <item>
public synchronized void buy(String s) {
	try {
		String[] k = s.split(" ", 2);
		System.out.println(this.nome + " executou o comando " + s);

		synchronized(this) {
			int aux = 0;
			for (Servidor se : this.servidores) {
				if (se.getEstado() == 0) aux++;
			}
			if(servidores.size() == 0 || aux == 0) {
				this.ps.println("-> Não há servidores para compra direta.");
			}
			else {
				ListIterator<Servidor> it = servidores.listIterator();
				try {
					ps = new PrintStream(cs.getOutputStream());
				} catch (IOException e) {
					System.out.println(e);
				}
				boolean existe = false;
				Servidor tmp;
				while(it.hasNext()) {
					tmp = it.next();

					if (tmp.getId() == Integer.parseInt(k[1])) {
						existe = true;
					
						if (tmp.getEstado() == 0) {	
							tmp.setComprador(this.nome);
							tmp.setEstado(2);
							tmp.setBid(tmp.getPVP());
							tmp.setAdq(LocalDateTime.now());
							this.divida += tmp.getPVP();
							this.ps.println("-> Possui o servidor " + tmp.getId() + ", tendo pago " + tmp.getPVP());
						}
						else {
							this.ps.println("-> O servidor não se encontra à venda.");
						}
					}
				}
				if (!existe) {
					this.ps.println("-> O servidor " + Integer.parseInt(k[1]) + "não existe.");
				}
			}
		}
	} catch(ArrayIndexOutOfBoundsException | NumberFormatException ee) {
		this.ps.println("-> Uso errado do comando buy");
		this.ps.println("-> buy <id do servidor>");
		System.out.println(this.nome + " tentou executar o comando " + s);
	} 
}

// Isto é pro caso de o gajo querer comprar um certo tipo e nao houver sem ser em leilao
public synchronized void want(String s) {
	try {
		String[] k = s.split(" ", 2);
		System.out.println(this.nome + " executou o comando " + s);

		synchronized(this) {
			int aux = 0;
			for (Servidor se : this.servidores) {
				if (se.getTipo().equals(k[1].toLowerCase())) aux++;
			}
			if(servidores.size() == 0 || aux == 0) {
				this.ps.println("-> Não há servidores do tipo " + k[1] + ".");
			}
			else {
				ListIterator<Servidor> it = servidores.listIterator();
				try {
					ps = new PrintStream(cs.getOutputStream());
				} catch (IOException e) {
					System.out.println(e);
				}
				boolean existe = false;
				Servidor tmp;
				
				List<Servidor> direct = new ArrayList<>();
				List<Servidor> leil = new ArrayList<>();

				while(it.hasNext()) {
					tmp = it.next();

					if (tmp.getTipo().equals(k[1].toLowerCase())) {
						
					
						if (tmp.getEstado() != 2) {	
							existe = true;
							if(tmp.getEstado() == 0) {
								direct.add(tmp);
							}
							else {
								leil.add(tmp);
							}
						}
					}
				}
				if (existe) {
					if (direct.size() > 0) {
						buy("buy " + direct.get(0).getId());
					}
					else if (leil.size() > 0) {
						tmp = leil.get(0);
						
						tmp.setComprador(this.nome);
						tmp.setEstado(2);
						tmp.setBid(tmp.getPVP());
						tmp.setAdq(LocalDateTime.now());
						this.divida += tmp.getPVP();
						this.ps.println("-> Possui o servidor " + tmp.getId() + ", tendo pago " + tmp.getPVP());
					}
				}
	
				if (!existe) {
					this.ps.println("-> Não há servidores do tipo " + k[1] + ".");
				}
			}
		}
	} catch(ArrayIndexOutOfBoundsException | NumberFormatException ee) {
		this.ps.println("-> Uso errado do comando want");
		this.ps.println("-> want <tipo do servidor>");
		System.out.println(this.nome + " tentou executar o comando " + s);
	}
}

	public void help() {

	System.out.println(this.nome + " enviou o comando help");
	try {
		synchronized(this) {
			ps = new PrintStream(cs.getOutputStream());
			this.ps.println("-> Comandos definidos internamente. Escreva help para visualizar esta lista.");
			this.ps.println("-> login <nome> <password> ->> Com este comando um utilizador pode iniciar sessão no leilão.");
			this.ps.println("-> signup <nome> <password>  ->> Com este comando um utilizador pode efetuar o registo num leilão, fornecendo um nome de utilizador e uma password.");
			this.ps.println("-> bid <id Servidor> <valor a licitar> ->> Este comando faz uma licitação para comprar um dado servidor.");
			this.ps.println("-> list ->> Este comando apresenta todos os servidores que se encontram em leilão.");
			this.ps.println("-> mine ->> Este comando permite ao utilizador saber que servidores possui.");
			this.ps.println("-> release <id Servidor> ->> Este comando permite ao utilizador cancelar a subscrição de um dado servidor.");
			this.ps.println("-> debt ->> Este comando permite ao utilizador saber quanto é que deve relativamente à utilização dos seus servidores.");
			this.ps.println("-> buy <id Servidor> ->> Este comando permite ao utilizador comprar um servidor que não se encontre em leilão.");
			this.ps.println("-> want <tipo do Servidor> ->> Este comando permite ao utilizador adquirir um servidor de um certo tipo quer ele se encontre em leilão, ou venda pública.");
		}
	} catch(IOException e) {
		System.out.println(e);
		}
	} 

}