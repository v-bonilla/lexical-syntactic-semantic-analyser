package procesador;

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

	

	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}
	
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
	
	void P() {
		if (StartOf(1)) {
			B();
			if (StartOf(2)) {
				P();
			}
		} else if (la.kind == 4) {
			F();
			if (StartOf(2)) {
				P();
			}
		} else SynErr(25);
	}

	void B() {
		if (la.kind == 10) {
			Get();
			T();
			Expect(1);
		} else if (StartOf(3)) {
			S();
		} else if (la.kind == 8) {
			Get();
			Expect(20);
			E();
			Expect(21);
			I();
		} else SynErr(26);
	}

	void F() {
		Expect(4);
		if (la.kind == 11 || la.kind == 12 || la.kind == 13) {
			H();
		}
		Expect(1);
		Expect(20);
		if (la.kind == 11 || la.kind == 12 || la.kind == 13) {
			A();
		}
		Expect(21);
		Expect(22);
		C();
		Expect(23);
	}

	void T() {
		if (la.kind == 11) {
			Get();
		} else if (la.kind == 12) {
			Get();
		} else if (la.kind == 13) {
			Get();
		} else SynErr(27);
	}

	void S() {
		if (la.kind == 1) {
			Get();
			X();
		} else if (la.kind == 7) {
			Get();
			if (StartOf(4)) {
				J();
			}
		} else if (la.kind == 5) {
			Get();
			Expect(20);
			E();
			Expect(21);
		} else if (la.kind == 6) {
			Get();
			Expect(20);
			Expect(1);
			Expect(21);
		} else SynErr(28);
	}

	void E() {
		W();
		if (la.kind == 15) {
			R();
		}
	}

	void I() {
		if (StartOf(3)) {
			S();
		} else if (la.kind == 22) {
			Get();
			C();
			Expect(23);
			if (la.kind == 9) {
				M();
			}
		} else SynErr(29);
	}

	void C() {
		B();
		if (StartOf(1)) {
			G();
		}
	}

	void M() {
		Expect(9);
		Expect(22);
		C();
		Expect(23);
	}

	void X() {
		if (la.kind == 14) {
			Get();
			E();
		} else if (la.kind == 20) {
			Get();
			if (StartOf(4)) {
				L();
			}
			Expect(21);
		} else SynErr(30);
	}

	void J() {
		E();
	}

	void L() {
		E();
		if (la.kind == 19) {
			Q();
		}
	}

	void Q() {
		Expect(19);
		E();
		if (la.kind == 19) {
			Q();
		}
	}

	void G() {
		C();
	}

	void H() {
		T();
	}

	void A() {
		T();
		Expect(1);
		if (la.kind == 19) {
			K();
		}
	}

	void K() {
		Expect(19);
		T();
		Expect(1);
		if (la.kind == 19) {
			K();
		}
	}

	void W() {
		U();
		if (la.kind == 16) {
			D();
		}
	}

	void R() {
		Expect(15);
		W();
	}

	void U() {
		V();
		if (la.kind == 17) {
			Z();
		}
	}

	void D() {
		Expect(16);
		U();
	}

	void V() {
		if (la.kind == 1) {
			Get();
			Y();
		} else if (la.kind == 2) {
			Get();
		} else if (la.kind == 3) {
			Get();
		} else if (la.kind == 20) {
			Get();
			E();
			Expect(21);
		} else SynErr(31);
	}

	void Z() {
		Expect(17);
		V();
	}

	void Y() {
		if (StartOf(5)) {
			if (la.kind == 18) {
				N();
			}
		} else if (la.kind == 20) {
			Get();
			E();
			Expect(21);
		} else SynErr(32);
	}

	void N() {
		Expect(18);
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		P();
		Expect(0);

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
