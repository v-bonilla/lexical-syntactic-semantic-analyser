package ts;

import java.io.PrintWriter;
import java.util.LinkedList;

public class TablaSimbolos {

	private String nombre;
	private int idTabla;
	private int nReg;
	private int desp;
	private LinkedList<Registro> registros = new LinkedList<Registro>();

	public TablaSimbolos(String nombre, int id){
		this.nombre = nombre;
		this.idTabla = id;
		this.nReg = 0;
		this.desp = 0;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public int getIdTabla() {
		return idTabla;
	}

	public void setIdTabla(int idTabla) {
		this.idTabla = idTabla;
	}
	
	public void insertaLex(String lex, boolean f){
		if (!existeLex(lex)){
			Registro r = new Registro(f);
			r.setLexema(lex);
			nReg++;
			r.setPosicion(nReg);
			registros.add(r);
		}
	}
	
	public boolean existeLex(String lex){
		boolean res = false;
		if (!registros.isEmpty()){
			for (Registro r : registros)
				if (r.getLexema().contentEquals(lex))
					res = true;
		}
		return res;
	}

	// Usado para pintar la lista de tokens
	public int buscaReg(String lex){
		int res = -1;
		if (!registros.isEmpty()){
			for (Registro r : registros){
				if (r.getLexema().contentEquals(lex)){
					res = r.getPosicion();
				}
			}
		}
		return res;
	}

	public String buscaTipo (String lex){
		String res = null;
		if (!registros.isEmpty()){
			for (Registro r : registros){
				if (r.getLexema().contentEquals(lex)){
					res = r.getTipo();
				}
			}
		}
		if (res == null)
			throw new Error("El id '" + lex + "' no se ha declarado en la tabla de simbolos");
		return res;
	}

	public LinkedList<String> buscaTipoParametros(String lex){
		LinkedList<String> res = null;
		if (!registros.isEmpty()){
			for (Registro r : registros) {
				if (r.getLexema().contentEquals(lex)) {
					res = r.getTipoParam();
				}
			}
		}
//		if (res == null)
//			throw new Error("El id '" + lex + "' no es una funcion");
		return res;
	}

	public String buscaTipoPorId (int id){
		String res = null;
		if (!registros.isEmpty()){
			for (Registro r : registros) {
				if (r.getIdTabla() == id) {
					res = r.getTipo();
				}
			}
		}
		return res;
	}
	
	public void insertaTipo (String lex, String tipo){
		if (!registros.isEmpty()){
			for (Registro r : registros){
				if (r.getLexema().contentEquals(lex)){
					r.setTipo(tipo);
					r.setDesplazamiento(this.desp);
					if(tipo.contentEquals("int"))
						this.desp = this.desp + 2;
					else if(tipo.contentEquals("bool"))
						this.desp = this.desp + 1;
					else if(tipo.contentEquals("chars"))
						this.desp = this.desp + 4;
					else if(tipo.contentEquals("pal. reservada"))
						this.desp = this.desp + 4;
				}
			}
		}
	}
	
	public void insertaDesp (String lex, int desp){
		if (!registros.isEmpty()){
			for (Registro r : registros){
				if (r.getLexema().contentEquals(lex)){
					r.setDesplazamiento(desp);
				}
			}
		}
	}
	
	public void insertaNParam (String lex, int n){
		if (!registros.isEmpty()){
			for (Registro r : registros){
				if (r.getLexema().contentEquals(lex)){
					r.setnParametros(n);
				}
			}
		}
	}
	
	public void insertaTipoParam (String lex, LinkedList<String> list){
		if (!registros.isEmpty()){
			for (Registro r : registros){
				if (r.getLexema().contentEquals(lex)){
					r.setTipoParam(list);
				}
			}
		}
	}
	
	public void insertaIdTabla (String lex, int id){
		if (!registros.isEmpty()){
			for (Registro r : registros){
				if (r.getLexema().contentEquals(lex)){
					r.setIdTabla(id);
				}
			}
		}
	}
	
	public void toString(PrintWriter pw){
		pw.println("Tabla " + this.nombre + " #" + this.idTabla + ":");
		if (!registros.isEmpty()){
			for (Registro r : registros){
				r.toString(pw);
			}
		}
	}

}
