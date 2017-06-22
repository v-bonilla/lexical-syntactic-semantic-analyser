package ts;

import java.io.PrintWriter;

public class Registro {
	private int posicion;
	private String lexema;
	private String tipo;
	private int desplazamiento;
	private int nParametros;
	private String[] tipoParam;
	private int idTabla;
	private boolean funcion;
	
	public Registro(boolean f){
		this.funcion = f;
	}

	public int getPosicion() {
		return posicion;
	}

	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}

	public String getLexema() {
		return lexema;
	}

	public void setLexema(String lexema) {
		this.lexema = lexema;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getDesplazamiento() {
		return desplazamiento;
	}

	public void setDesplazamiento(int desplazamiento) {
		this.desplazamiento = desplazamiento;
	}

	public int getnParametros() {
		return nParametros;
	}

	public void setnParametros(int nParametros) {
		this.nParametros = nParametros;
	}

	public String[] getTipoParam() {
		return tipoParam;
	}

	public void setTipoParam(String[] tipoParam) {
		this.tipoParam = tipoParam;
	}

	public int getIdTabla() {
		return idTabla;
	}

	public void setIdTabla(int idTabla) {
		this.idTabla = idTabla;
	}
	
	public void toString(PrintWriter pw){
		if (this.funcion){
			pw.println("  * (esto es una funcion) " + this.lexema);
			pw.println("    + tipo: " + this.tipo);
			pw.println("    + parametros: " + this.nParametros);
			pw.println("    + idtabla: " + this.idTabla);
			if (this.nParametros > 0){
				for (int i = 0; i < this.tipoParam.length; i++){
					if (tipoParam[i] != null)
						pw.println("    + tipoparam" + i + ": " + tipoParam[i]);
				}
			}
		}else{
			pw.println("  * " + this.lexema);
			pw.println("    + tipo: " + this.tipo);
			pw.println("    + desplazamiento: " + this.desplazamiento);
		}
	}

}
