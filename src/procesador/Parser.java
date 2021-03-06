package procesador;

import ts.TablaSimbolos;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.ListIterator;

public class Parser {
	public static final int _EOF = 0;
	public static final int _id = 1;
	public static final int _entero = 2;
	public static final int _cadena = 3;
	public static final int _function = 4;
	public static final int _write = 5;
	public static final int _prompt = 6;
	public static final int _return = 7;
	public static final int _if = 8;
	public static final int _else = 9;
	public static final int _var = 10;
	public static final int _int = 11;
	public static final int _bool = 12;
	public static final int _chars = 13;
	public static final int _igual = 14;
	public static final int _and = 15;
	public static final int _diferente = 16;
	public static final int _suma = 17;
	public static final int _pdecre = 18;
	public static final int _coma = 19;
	public static final int _parenI = 20;
	public static final int _parenD = 21;
	public static final int _llaveI = 22;
	public static final int _llaveD = 23;
	public static final int maxT = 24;

	static final boolean _T = true;
	static final boolean _x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	// Atributos para archivos y punteros de escritura
	public FileWriter listaTokens = null;
	public FileWriter parse = null;
	public FileWriter tablaSimbolos = null;
	public PrintWriter pwLT = null;
	public PrintWriter pwP = null;
	public PrintWriter pwTS = null;

	// Usada por writeToken para ver si ha llegado al final del fichero
	public boolean eof = false;

	// Atributos para las tablas de simbolos
	public LinkedList<TablaSimbolos> pilaTS = new LinkedList<TablaSimbolos>();
	public int idTabla = 1;
	public TablaSimbolos tsGlobal = new TablaSimbolos("Global", this.idTabla);
	public TablaSimbolos tsActual;

	// Tipos usados por el An. semantico
	public static final String tipoInt = "int";
	public static final String tipoBool = "bool";
	public static final String tipoChars = "chars";
	public static final String tipoOK = "OK";
	public static final String tipoERROR = "ERROR";
	public static final String tipoNULO = "NULO";

	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
		// Declaramos archivos y punteros de escritura
		try {
			listaTokens = new FileWriter("lista de tokens.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		pwLT = new PrintWriter(listaTokens);

		try {
			parse = new FileWriter("parse.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		pwP = new PrintWriter(parse);

		try {
			tablaSimbolos = new FileWriter("tabla de simbolos.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		pwTS = new PrintWriter(tablaSimbolos);
	}

	public TablaSimbolos getTsActual() {
		return tsActual;
	}

	public void setTsActual(TablaSimbolos tsActual) {
		this.tsActual = tsActual;
	}

	public void writeToken(String t){
		if (!eof){
			if (t.contentEquals("document.write"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("function"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("prompt"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("return"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("if"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("else"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("var"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("int"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("bool"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("chars"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("void"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("="))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("&&"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("!="))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("+"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("--"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals(","))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("("))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals(")"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("{"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("}"))
				pwLT.println("< " + t + " , - >");
			else if (t.contentEquals("")){
				pwLT.println("< eof , - >");
				eof = true;
			}
			else if (t.startsWith("\""))
				pwLT.println("< cadena , " + t + " >");
			else if (t.startsWith("0") || t.startsWith("1") || t.startsWith("2") || t.startsWith("3") || t.startsWith("4")
					|| t.startsWith("5") || t.startsWith("6") || t.startsWith("7") || t.startsWith("8") || t.startsWith("9"))
				pwLT.println("< entero , " + t + " >");
		}
	}

	public void closeFiles(){
		for (TablaSimbolos ts : pilaTS){
			ts.toString(pwTS);
		}
		// Cierra ficheros
		if (null != listaTokens)
			try {
				listaTokens.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if(null != parse)
			try {
				parse.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if(null != tablaSimbolos)
			try {
				tablaSimbolos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public void createAndPushTS(String name){
		idTabla++;
		// El nombre se suele extraer del token
		TablaSimbolos ts = new TablaSimbolos(name, idTabla);
		pilaTS.add(ts);
		tsActual = ts;
	}

	public void popTS(){
		// Puesto que no se pueden declarar funciones dentro de una funcion, solo hay un nivel de tablas
		// No se destruye la tabla a hacer pop para pintarla al final de la ejecucion
		tsActual = tsGlobal;
	}

	public void insertaIdEnTSActual(String lex, String tipo){
		tsActual.insertaLex(lex, false);
		tsActual.insertaTipo(lex, tipo);
	}

	public void insertaFuncionEnTSActual(String lex, String tipo, int idTablaF, int nParam, LinkedList<String> list){
		tsActual.insertaLex(lex, true);
		tsActual.insertaTipo(lex, tipo);
		tsActual.insertaIdTabla(lex, idTablaF);
		tsActual.insertaNParam(lex, nParam);
		tsActual.insertaTipoParam(lex, list);
	}

	public void insertaPalReservadas(){
		tsGlobal.insertaLex("function", false);
		tsGlobal.insertaTipo("function", "pal. reservada");
		tsGlobal.insertaLex("write", false);
		tsGlobal.insertaTipo("write", "pal. reservada");
		tsGlobal.insertaLex("prompt", false);
		tsGlobal.insertaTipo("prompt", "pal. reservada");
		tsGlobal.insertaLex("return", false);
		tsGlobal.insertaTipo("return", "pal. reservada");
		tsGlobal.insertaLex("var", false);
		tsGlobal.insertaTipo("var", "pal. reservada");
		tsGlobal.insertaLex("if", false);
		tsGlobal.insertaTipo("if", "pal. reservada");
		tsGlobal.insertaLex("else", false);
		tsGlobal.insertaTipo("else", "pal. reservada");
		tsGlobal.insertaLex("int", false);
		tsGlobal.insertaTipo("int", "pal. reservada");
		tsGlobal.insertaLex("bool", false);
		tsGlobal.insertaTipo("bool", "pal. reservada");
		tsGlobal.insertaLex("chars", false);
		tsGlobal.insertaTipo("chars", "pal. reservada");

	}

	public boolean comparaTipoParametros(LinkedList<String> params1, LinkedList<String> params2){
		boolean res = false;
		if (params1.size() == params2.size()){
			if (!params1.isEmpty() && !params2.isEmpty()){
				ListIterator it1 = params1.listIterator();
				ListIterator it2 = params2.listIterator();
				boolean equals = true;
				while (it1.hasNext() && it2.hasNext()){
					String param1 = (String) it1.next();
					String param2 = (String) it2.next();
					if (!param1.equals(param2)){
						equals = false;
					}
				}
				res = equals;
			}
			else
				res = true;
		}
		return res;
	}

	public String buscaTipoRetorno(){
		String res = null;
		if (!tsActual.getNombre().equals(tsGlobal.getNombre())){
			int idTS = tsActual.getIdTabla();
			res = tsGlobal.buscaTipoPorId(idTS);
		}
		else
			throw new Error("La sentencia return debe estar dentro de una funcion");
		return res;
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}

	// Establece el token y lee el siguiente guardando en la
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			writeToken(la.val);
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}

	// Si es el token esperado, lee el siguiente
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	String P() {
		String tipo = null;
		if (StartOf(1)) {
			// 1. P -> B P
			boolean l = true;
			pwP.print(" 1");
			String tipoB = B();
			String tipoP1 = null;
			if (StartOf(2)) {
				tipoP1 = P();
				l = false;
			}
			// 3. P -> lambda
			if (l) {
				pwP.print(" 3");
				tipoP1 = tipoNULO;
			}
			if (tipoB.equals(tipoOK) && tipoP1.equals(tipoOK))
				tipo = tipoOK;
			else if (tipoP1.equals(tipoNULO))
				tipo = tipoB;
			else
				tipo = tipoERROR;
		} else if (la.kind == 4) {
			// 2. P -> F P
			boolean l = true;
			pwP.print(" 2");
			String tipoF = F();
			String tipoP1 = null;
			if (StartOf(2)) {
				tipoP1 = P();
				l = false;
			}
			// 3. P -> lambda
			if (l) {
				pwP.print(" 3");
				tipoP1 = tipoNULO;
			}
			if (tipoF.equals(tipoOK) && tipoP1.equals(tipoOK))
				tipo = tipoOK;
			else if (tipoP1.equals(tipoNULO))
				tipo = tipoF;
			else
				tipo = tipoERROR;
		} else SynErr(25);
		return tipo;
	}

	String B() {
		String tipo = null;
		if (la.kind == 10) {
			// 4. B -> var T id
			pwP.print(" 4");
			Get();
			// el token es var
			String tipoT = T();
			Expect(1);
			if (tsActual.existeLex(t.val)) {
				throw new Error("El id '" + t.val + "'ya ha sido declarado");
			}
			else{
				insertaIdEnTSActual(t.val, tipoT);
				tipo = tipoOK;
			}
			// Añade el token id en la lista de tokens con su posicion en la TS
			pwLT.println("< " + t.val + " , " + tsActual.buscaReg(t.val) + " >");
		} else if (StartOf(3)) {
			// 5. B -> S
			pwP.print(" 5");
			tipo = S();
		} else if (la.kind == 8) {
			// 6. B -> if ( E ) I
			pwP.print(" 6");
			Get();
			Expect(20);
			String tipoE = E();
			Expect(21);
			String tipoI = I();
			if (tipoE.equals(tipoBool))
				tipo = tipoI;
			else
				throw new Error("La expresion condicional del if debe ser de tipo bool");
		} else SynErr(26);
		return tipo;
	}

	String F() {
		// 29. F -> function H id ( A ) { C }
		String tipo= null;
		String tipoH = null;
		LinkedList<String[]> tipoAConParams = null;
		String tipoC = null;
		int nParams = 0;
		boolean lH = true;
		boolean lA = true;
		pwP.print(" 29");
		Expect(4);
		if (la.kind == 11 || la.kind == 12 || la.kind == 13) {
			tipoH = H();
			lH = false;
		}
		// 31. H -> lambda
		if (lH){
			pwP.print(" 31");
			tipoH = tipoNULO;
		}
		Expect(1);
		String lexId = t.val;
		if (tsActual.existeLex(lexId))
			throw new Error("El id '" + lexId + "'ya ha sido declarado");
		// Añade el token id en la lista de tokens con su posicion en la TS
		pwLT.println("< " + t.val + " , " + tsActual.buscaReg(t.val) + " >");
		Expect(20);
		if (la.kind == 11 || la.kind == 12 || la.kind == 13) {
			tipoAConParams = A();
			lA = false;
		}
		// 33. A -> lambda
		if (lA) pwP.print(" 33");
		Expect(21);
		// Acciones semanticas

		// Para sacar los nombres de los parametros
		LinkedList<String> tipoParams = new LinkedList<>();;
		if (!(tipoAConParams == null)) {
			nParams = tipoAConParams.size();
			ListIterator it = tipoAConParams.listIterator();
			while (it.hasNext()){
				String[] tipoP = (String[]) it.next();
				tipoParams.add(tipoP[1]);
			}
		}
		insertaFuncionEnTSActual(lexId, tipoH, idTabla + 1, nParams, tipoParams);
		Expect(22);
		createAndPushTS(lexId);
		// Inserta los parametros en la TS de la funcion
		if (!(tipoAConParams == null)){
			ListIterator it = tipoAConParams.listIterator();
			int i = 1;
			while (it.hasNext()){
				String[] param = (String[]) it.next();
				insertaIdEnTSActual(param[0], param[1]);
				i++;
			}
		}
		tipoC = C();
		popTS();
		Expect(23);
		if (!tipoC.equals(tipoERROR))
			tipo = tipoOK;
		else
			tipo = tipoERROR;
		return tipo;
	}

	String T() {
		String tipo = null;
		if (la.kind == 11) {
			// 11. T -> int
			pwP.print(" 11");
			tipo = tipoInt;
			Get();
		} else if (la.kind == 12) {
			// 12. T -> bool
			pwP.print(" 12");
			tipo = tipoBool;
			Get();
		} else if (la.kind == 13) {
			// 13. T -> chars
			pwP.print(" 13");
			tipo = tipoChars;
			Get();
		} else SynErr(27);
		return tipo;
	}

	String S() {
		// 14. S -> id X
		String tipo = null;
		if (la.kind == 1) {
			pwP.print(" 14");
			Get();
			String lexId = t.val;
			Object tipoX = X();
			if (tipoX instanceof LinkedList) {
				if (comparaTipoParametros(tsGlobal.buscaTipoParametros(lexId), (LinkedList<String>) tipoX))
					tipo = tipoOK;
				else
					throw new Error("Los argumentos no coinciden con los argumentos declarados para la funcion '" + lexId + "'");
			} else if (tsActual.buscaTipo(lexId).equals(tipoX))
				tipo = tipoOK;
			else if (t.val.equals(")"))
				throw new Error("Los argumentos no coinciden con los argumentos declarados para la funcion '" + lexId + "'");
			else
				throw new Error("El tipo del valor a asignar no coincide con el tipo de la variable a ser asignada");
		} else if (la.kind == 7) {
			// 15. S -> return J
			boolean l = true;
			pwP.print(" 15");
			Get();
			String tipoJ = null;
			if (StartOf(4)) {
				tipoJ = J();
				l = false;
			}
			// 21. J -> lambda
			if (l){
				pwP.print(" 21");
				tipoJ = tipoNULO;
			}
			if (buscaTipoRetorno().equals(tipoJ))
				tipo = tipoOK;
			else
				throw new Error("La sentencia return devuelve un tipo incorrecto");
		} else if (la.kind == 5) {
			// 16. S -> write ( E )
			pwP.print(" 16");
			Get();
			Expect(20);
			String tipoE = E();
			Expect(21);
			if (tipoE.equals(tipoInt) || tipoE.equals(tipoChars))
				tipo = tipoOK;
			else
				throw new Error("La sentencia write solo evalua expresiones de tipo int o chars");
		} else if (la.kind == 6) {
			// 17. S -> prompt ( id )
			pwP.print(" 17");
			Get();
			Expect(20);
			Expect(1);
			if (tsActual.buscaTipo(t.val).equals(tipoInt) || tsActual.buscaTipo(t.val).equals(tipoChars))
				tipo = tipoOK;
			else
				throw new Error("La sentencia prompt solo almacena en variables de tipo int o chars");
			Expect(21);
		} else SynErr(28);
		return tipo;
	}

	String E() {
		String tipo = null;
		// 36. E -> W R
		boolean l = true;
		pwP.print(" 36");
		String tipoW = W();
		String tipoR = null;
		if (la.kind == 15) {
			tipoR = R();
			l = false;
		}
		// 38. R -> lambda
		if (l){
			pwP.print(" 38");
			tipoR = tipoNULO;
		}
		if (tipoR.equals(tipoBool) && tipoW.equals(tipoBool))
			tipo = tipoW;
		else if (tipoR.equals(tipoNULO))
			tipo = tipoW;
		else
			throw new Error(" La operacion AND se debe realizar con tipos bool");
		return tipo;
	}

	String I() {
		String tipo = null;
		if (StartOf(3)) {
			// 7. I -> S
			pwP.print(" 7");
			tipo = S();
		} else if (la.kind == 22) {
			// 8. I -> { C } M
			boolean l = true;
			pwP.print(" 8");
			Get();
			String tipoC = C();
			String tipoM = null;
			Expect(23);
			if (la.kind == 9) {
				tipoM = M();
				l = false;
			}
			// 10. M -> lambda
			if (l){
				pwP.print(" 10");
				tipoM = tipoNULO;
			}
			if (tipoC.equals(tipoOK) && tipoM.equals(tipoOK))
				tipo = tipoOK;
			else if (tipoM.equals(tipoNULO))
				tipo = tipoC;
			else
				tipo = tipoERROR;
		} else SynErr(29);
		return tipo;
	}

	String C() {
		String tipo = null;
		// 26. C -> B G
		boolean l = true;
		pwP.print(" 26");
		String tipoB = B();
		String tipoG = null;
		if (StartOf(1)) {
			tipoG = G();
			l = false;
		}
		// 28. G -> lambda
		if (l){
			pwP.print(" 28");
			tipoG = tipoNULO;
		}
		if (tipoB == null)
			throw new Error("El cuerpo no puede estar vacio");
		if (tipoB.equals(tipoOK) && tipoG.equals(tipoOK))
			tipo = tipoOK;
		else if (tipoG.equals(tipoNULO))
			tipo = tipoB;
		else
			tipo = tipoERROR;
		return tipo;
	}

	String M() {
		// 9. M -> else { C }
		pwP.print(" 9");
		Expect(9);
		Expect(22);
		String tipo = C();
		Expect(23);
		return tipo;
	}

	Object X() {
		Object tipo = null;
		if (la.kind == 14) {
			// 18. X -> = E
			pwP.print(" 18");
			Get();
			tipo = E();
		} else if (la.kind == 20) {
			// 19. X -> ( L )
			boolean l = true;
			pwP.print(" 19");
			Get();
			if (StartOf(4)) {
				tipo = L();
				l = false;
			}
			Expect(21);
			// 23. L -> lambda
			if (l){
				pwP.print(" 23");
				tipo = tipoNULO;
			}
		} else SynErr(30);
		return tipo;
	}

	String J() {
		// 20. J -> E
		pwP.print(" 20");
		String tipo = E();
		return tipo;
	}

	LinkedList<String> L() {
		// 22. L -> E Q
		LinkedList<String> res = new LinkedList<>();
		boolean l = true;
		pwP.print(" 22");
		String tipoE = E();
		res.add(tipoE);
		if (la.kind == 19) {
			Q(res);
			l = false;
		}
		// 25. Q -> lambda
		if (l){
			pwP.print(" 25");
		}
		return res;
	}

	LinkedList<String> Q(LinkedList<String> list) {
		// 24. Q -> , E Q
		boolean l = true;
		pwP.print(" 24");
		Expect(19);
		String tipoE = E();
		list.add(tipoE);
		if (la.kind == 19) {
			Q(list);
			l = false;
		}
		// 25. Q -> lambda
		if (l) pwP.print(" 25");
		return list;
	}

	String G() {
		// 27. G -> C
		pwP.print(" 27");
		String tipo = C();
		return tipo;
	}

	String H() {
		// 30. H -> T
		pwP.print(" 30");
		String tipo = T();
		return tipo;
	}

	LinkedList<String[]> A() {
		// 32. A -> T id K
		LinkedList<String[]> res = new LinkedList<>();
		boolean l = true;
		pwP.print(" 32");
		String tipoT = T();
		Expect(1);
		String[] p = {t.val, tipoT};
		res.add(p);
		if (la.kind == 19) {
			K(res);
			l = false;
		}
		// 35. K -> lambda
		if (l) pwP.print(" 35");
		return res;
	}

	LinkedList<String[]> K(LinkedList<String[]> list) {
		// 34. K -> , T id K
		boolean l = true;
		pwP.print(" 34");
		Expect(19);
		String tipoT = T();
		Expect(1);
		String[] p = {t.val, tipoT};
		list.add(p);
		if (la.kind == 19) {
			K(list);
			l = false;
		}
		// 35. K -> lambda
		if (l) pwP.print(" 35");
		return list;
	}

	String W() {
		String tipo = null;
		// 39. W -> U D
		boolean l = true;
		pwP.print(" 39");
		String tipoU = U();
		String tipoD = null;
		if (la.kind == 16) {
			tipoD = D();
			l = false;
		}
		// 41. D -> lambda
		if (l){
			pwP.print(" 41");
			tipoD = tipoNULO;
		}
		if (tipoD.equals(tipoInt) && tipoU.equals(tipoInt))
			tipo = tipoBool;
		else if (tipoD.equals(tipoNULO))
			tipo = tipoU;
		else
			throw new Error("No se pueden comparar tipos distintos");
		return tipo;
	}

	String R() {
		// 37. R -> && W
		pwP.print(" 37");
		Expect(15);
		String tipo = W();
		return tipo;
	}

	String U() {
		// 42. U -> V Z
		String tipo = null;
		boolean l = true;
		pwP.print(" 42");
		String tipoV = V();
		String tipoZ = null;
		if (la.kind == 17) {
			tipoZ = Z();
			l = false;
		}
		// 44. Z -> lambda
		if (l){
			pwP.print(" 44");
			tipoZ = tipoNULO;
		}
		if (tipoZ.equals(tipoInt) && tipoV.equals(tipoInt))
			tipo = tipoV;
		else if (tipoZ.equals(tipoNULO))
			tipo = tipoV;
		else
			throw new Error("La suma debe ser entre tipos int");
		return tipo;
	}

	String D() {
		// 40. D -> != U
		pwP.print(" 40");
		Expect(16);
		String tipo = U();
		return tipo;
	}

	String V() {
		String tipo = null;
		if (la.kind == 1) {
			// 45. V -> id Y
			pwP.print(" 45");
			Get();
			// el token es id
			String lexId = t.val;
			Object tipoY = Y();
			if (tsActual.buscaTipo(lexId).equals(tipoInt) && tipoY.equals(tipoOK)){
				tipo = tsActual.buscaTipo(lexId);
			}
			else if (tipoY.equals(tipoNULO))
				tipo = tsActual.buscaTipo(lexId);
			else if (comparaTipoParametros(tsActual.buscaTipoParametros(lexId), (LinkedList<String>) tipoY))
				tipo = tsActual.buscaTipo(lexId);
			else
				throw new Error("No se puede decrementar un tipo diferente de int o los tipos de los parametros de la funcion no son correctos");
		} else if (la.kind == 2) {
			// 46. V -> entero
			pwP.print(" 46");
			Get();
			tipo = tipoInt;
		} else if (la.kind == 3) {
			// 47. V -> cadena
			pwP.print(" 47");
			Get();
			tipo = tipoChars;
		} else if (la.kind == 20) {
			// 48. V -> ( E )
			pwP.print(" 48");
			Get();
			tipo = E();
			Expect(21);
		} else SynErr(31);
		return tipo;
	}

	String Z() {
		// 43. Z -> + V
		pwP.print(" 43");
		Expect(17);
		String tipo = V();
		return tipo;
	}

	Object Y() {
		Object tipo = null;
		if (StartOf(5)) {
			// 49. Y -> N
			boolean l = true;
			pwP.print(" 49");
			if (la.kind == 18) {
				tipo = N();
				l = false;
			}
			// 52. N -> lambda
			if (l){
				pwP.print(" 52");
				tipo = tipoNULO;
			}
		} else if (la.kind == 20) {
			// 50. Y -> ( E )
			pwP.print(" 50");
			Get();
			tipo = E();
			Expect(21);
		} else SynErr(32);
		return tipo;
	}

	String N() {
		// 51. N -> --
		pwP.print(" 51");
		String tipo = tipoOK;
		Expect(18);
		return tipo;
	}



	public void Parse() {
		pwP.print("D");
		la = new Token();
		la.val = "";		
		Get();
		insertaPalReservadas();
		pilaTS.add(tsGlobal);
		tsActual = tsGlobal;
		String tipo = P();
		Expect(0);
		closeFiles();
		if (tipo.equals(tipoERROR))
			throw new Error("El fichero tiene algun error semantico");
	}

	private static final boolean[][] set = {
		{_T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x},
		{_x,_T,_x,_x, _x,_T,_T,_T, _T,_x,_T,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x},
		{_x,_T,_x,_x, _T,_T,_T,_T, _T,_x,_T,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x},
		{_x,_T,_x,_x, _x,_T,_T,_T, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x},
		{_x,_T,_T,_T, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _T,_x,_x,_x, _x,_x},
		{_T,_T,_x,_x, _T,_T,_T,_T, _T,_x,_T,_x, _x,_x,_x,_T, _T,_T,_T,_T, _x,_T,_x,_T, _x,_x}

	};
} // end Parser


class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "id expected"; break;
			case 2: s = "entero expected"; break;
			case 3: s = "cadena expected"; break;
			case 4: s = "function expected"; break;
			case 5: s = "write expected"; break;
			case 6: s = "prompt expected"; break;
			case 7: s = "return expected"; break;
			case 8: s = "if expected"; break;
			case 9: s = "else expected"; break;
			case 10: s = "var expected"; break;
			case 11: s = "int expected"; break;
			case 12: s = "bool expected"; break;
			case 13: s = "chars expected"; break;
			case 14: s = "igual expected"; break;
			case 15: s = "and expected"; break;
			case 16: s = "diferente expected"; break;
			case 17: s = "suma expected"; break;
			case 18: s = "pdecre expected"; break;
			case 19: s = "coma expected"; break;
			case 20: s = "parenI expected"; break;
			case 21: s = "parenD expected"; break;
			case 22: s = "llaveI expected"; break;
			case 23: s = "llaveD expected"; break;
			case 24: s = "??? expected"; break;
			case 25: s = "invalid P"; break;
			case 26: s = "invalid B"; break;
			case 27: s = "invalid T"; break;
			case 28: s = "invalid S"; break;
			case 29: s = "invalid I"; break;
			case 30: s = "invalid X"; break;
			case 31: s = "invalid V"; break;
			case 32: s = "invalid Y"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}
