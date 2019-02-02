import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDateTime;

// item que será vendido
public class Servidor {
	private int id;
	private String nome;
	private float bid;
	private String comprador;
	private float precoBase;
  	private float pvp;
  	private int estado; // 0 = venda publica, 1 = Em Leilao, 2 = adquirido por alguem;
  	private LocalDateTime adq;
  	private int bids;

	public Servidor(int numero, String nome, float base, float pvp, int estado) {
		this.id = numero;
		this.nome = nome;
		this.bid = 0;
		this.comprador = "";
		this.precoBase = base;
		this.pvp = pvp;
		this.estado = estado;
		this.adq = null;
		this.bids = 0;
	}

	public synchronized void setId(int id) {
		this.id = id;
	}

	public synchronized void setNome(String nome) {
		this.nome = nome;
	}

	public synchronized void setBid(float bid) {
		this.bid = bid;
	}

	public synchronized void setComprador(String comp) {
		this.comprador = comp;
	}

	public synchronized void setprecoBase(float base) {
		this.precoBase = base;
	}

	public synchronized void setPVP(float pvp) {
		this.pvp = pvp;
	}

	public synchronized void setEstado(int estado) {
		this.estado = estado;
	}

	public synchronized void setAdq(LocalDateTime adq) {
		this.adq = adq;
	}

	public synchronized void setBids(int x) {
		this.bids = x;
	}

	public synchronized int getId() {
		return this.id;
	}

	public synchronized String getNome() {
		return this.nome;
	}

	public synchronized float getBid() {
		return this.bid;
	}

	public synchronized String getComprador() {
		return this.comprador;
	}

	public synchronized float getprecoBase() {
		return this.precoBase;
	}

	public synchronized float getPVP() {
		return this.pvp;
	}

	public synchronized int getEstado() {
		return this.estado;
	}

  	public synchronized LocalDateTime getAdq() { 
  		return this.adq; 
  	}

  	public synchronized int getBids() {
		return this.bids;
	}

  	public synchronized String getTipo() { 
  		if (this.nome.contains("micro")) return "micro";
  		else if (this.nome.contains("medium")) return "medium";
  		else if (this.nome.contains("large")) return "large";
  		else return "undefined"; 
  	}

}