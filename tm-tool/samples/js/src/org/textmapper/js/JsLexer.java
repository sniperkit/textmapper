package org.textmapper.js;

import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class JsLexer {

	public static class Span {
		public Object value;
		public int symbol;
		public int state;
		public int line;
		public int offset;
		public int endoffset;
	}

	public interface States {
		int initial = 0;
		int div = 1;
	}

	public interface Tokens {
		int Unavailable_ = -1;
		int eoi = 0;
		int space = 1;
		int LineTerminatorSequence = 2;
		int MultiLineComment = 3;
		int SingleLineComment = 4;
		int Identifier = 5;
		int _break = 6;
		int _case = 7;
		int _catch = 8;
		int _continue = 9;
		int debugger = 10;
		int _default = 11;
		int delete = 12;
		int _do = 13;
		int _else = 14;
		int _finally = 15;
		int _for = 16;
		int function = 17;
		int _if = 18;
		int in = 19;
		int _instanceof = 20;
		int _new = 21;
		int _return = 22;
		int _switch = 23;
		int _this = 24;
		int _throw = 25;
		int _try = 26;
		int typeof = 27;
		int var = 28;
		int _void = 29;
		int _while = 30;
		int with = 31;
		int _class = 32;
		int _const = 33;
		int _enum = 34;
		int export = 35;
		int _extends = 36;
		int _import = 37;
		int _super = 38;
		int Lbrace = 39;
		int Rbrace = 40;
		int Lparen = 41;
		int Rparen = 42;
		int Lbrack = 43;
		int Rbrack = 44;
		int Dot = 45;
		int Semicolon = 46;
		int Comma = 47;
		int Lt = 48;
		int Gt = 49;
		int LtAssign = 50;
		int GtAssign = 51;
		int AssignAssign = 52;
		int ExclAssign = 53;
		int AssignAssignAssign = 54;
		int ExclAssignAssign = 55;
		int Plus = 56;
		int Minus = 57;
		int Mult = 58;
		int Rem = 59;
		int PlusPlus = 60;
		int MinusMinus = 61;
		int LtLt = 62;
		int GtGt = 63;
		int GtGtGt = 64;
		int And = 65;
		int Or = 66;
		int Xor = 67;
		int Excl = 68;
		int Tilde = 69;
		int AndAnd = 70;
		int OrOr = 71;
		int Quest = 72;
		int Colon = 73;
		int Assign = 74;
		int PlusAssign = 75;
		int MinusAssign = 76;
		int MultAssign = 77;
		int RemAssign = 78;
		int LtLtAssign = 79;
		int GtGtAssign = 80;
		int GtGtGtAssign = 81;
		int AndAssign = 82;
		int OrAssign = 83;
		int XorAssign = 84;
		int _null = 85;
		int _true = 86;
		int _false = 87;
		int NumericLiteral = 88;
		int StringLiteral = 89;
		int RegularExpressionLiteral = 90;
		int Div = 91;
		int DivAssign = 92;
	}

	public interface ErrorReporter {
		void error(String message, int line, int offset, int endoffset);
	}

	public static final int TOKEN_SIZE = 2048;

	private Reader stream;
	final private ErrorReporter reporter;

	private CharSequence input;
	private int tokenOffset;
	private int l;
	private int charOffset;
	private int chr;

	private int state;

	private int tokenLine;
	private int currLine;
	private int currOffset;

	public JsLexer(CharSequence input, ErrorReporter reporter) throws IOException {
		this.reporter = reporter;
		reset(input);
	}

	public void reset(CharSequence input) throws IOException {
		this.state = 0;
		tokenLine = currLine = 1;
		currOffset = 0;
		this.input = input;
		tokenOffset = l = 0;
		charOffset = l;
		chr = l < input.length() ? input.charAt(l++) : -1;
		if (chr >= Character.MIN_HIGH_SURROGATE && chr <= Character.MAX_HIGH_SURROGATE && l < input.length() &&
				Character.isLowSurrogate(input.charAt(l))) {
			chr = Character.toCodePoint((char) chr, input.charAt(l++));
		}
	}

	protected void advance() {
		if (chr == -1) return;
		currOffset += l - charOffset;
		if (chr == '\n') {
			currLine++;
		}
		charOffset = l;
		chr = l < input.length() ? input.charAt(l++) : -1;
		if (chr >= Character.MIN_HIGH_SURROGATE && chr <= Character.MAX_HIGH_SURROGATE && l < input.length() &&
				Character.isLowSurrogate(input.charAt(l))) {
			chr = Character.toCodePoint((char) chr, input.charAt(l++));
		}
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getTokenLine() {
		return tokenLine;
	}

	public int getLine() {
		return currLine;
	}

	public void setLine(int currLine) {
		this.currLine = currLine;
	}

	public int getOffset() {
		return currOffset;
	}

	public void setOffset(int currOffset) {
		this.currOffset = currOffset;
	}

	public String tokenText() {
		return input.subSequence(tokenOffset, charOffset).toString();
	}

	public int tokenSize() {
		return charOffset - tokenOffset;
	}

	private static final char[] tmCharClass = unpack_vc_char(918000,
		"\11\1\1\63\1\11\2\63\1\10\22\1\1\63\1\30\1\43\1\1\1\2\1\33\1\34\1\45\1\16\1\17\1" +
		"\13\1\31\1\24\1\32\1\22\1\12\1\42\11\54\1\41\1\23\1\25\1\27\1\26\1\40\1\1\4\55\1" +
		"\65\1\55\21\46\1\67\2\46\1\20\1\4\1\21\1\36\1\3\1\1\4\56\1\66\1\56\16\47\1\5\2\47" +
		"\1\44\2\47\1\14\1\35\1\15\1\37\41\1\1\63\11\1\1\52\12\1\1\47\4\1\1\52\5\1\27\46\1" +
		"\1\7\46\30\47\1\1\10\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\2\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\2\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\2\46\1\47\1\46\1\47\1\46\3" +
		"\47\2\46\1\47\1\46\1\47\2\46\1\47\3\46\2\47\4\46\1\47\2\46\1\47\3\46\3\47\2\46\1" +
		"\47\2\46\1\47\1\46\1\47\1\46\1\47\2\46\1\47\1\46\2\47\1\46\1\47\2\46\1\47\3\46\1" +
		"\47\1\46\1\47\2\46\2\47\1\52\1\46\3\47\4\52\1\46\1\50\1\47\1\46\1\50\1\47\1\46\1" +
		"\50\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\2\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\2\47\1\46\1\50\1\47\1\46\1\47\3\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\7\47\2\46\1\47\2\46\2\47\1\46\1\47\4\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\105\47\1\52\33\47\22\51\4\1\14\51\16\1\5\51\7\1\1\51\1\1\1\51" +
		"\21\1\160\57\1\46\1\47\1\46\1\47\1\51\1\1\1\46\1\47\2\1\1\51\3\47\1\1\1\46\6\1\1" +
		"\46\1\1\3\46\1\1\1\46\1\1\2\46\1\47\21\46\1\1\11\46\43\47\1\46\2\47\3\46\3\47\1\46" +
		"\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46" +
		"\1\47\1\46\1\47\1\46\1\47\1\46\5\47\1\46\1\47\1\1\1\46\1\47\2\46\2\47\63\46\60\47" +
		"\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47" +
		"\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47" +
		"\1\46\1\47\1\1\5\57\2\1\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\2\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\2\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\1\46\46\2\1\1\51\7\1\47\47\11\1\55" +
		"\57\1\1\1\57\1\1\2\57\1\1\2\57\1\1\1\57\10\1\33\52\5\1\3\52\35\1\13\57\5\1\40\52" +
		"\1\51\12\52\25\57\12\61\4\1\2\52\1\57\143\52\1\1\1\52\7\57\2\1\6\57\2\51\2\57\1\1" +
		"\4\57\2\52\12\61\3\52\2\1\1\52\20\1\1\52\1\57\36\52\33\57\2\1\131\52\13\57\1\52\16" +
		"\1\12\61\41\52\11\57\2\51\4\1\1\51\5\1\26\52\4\57\1\51\11\57\1\51\3\57\1\51\5\57" +
		"\22\1\31\52\3\57\104\1\25\52\56\1\40\57\1\60\66\52\1\57\1\60\1\57\1\52\3\60\10\57" +
		"\4\60\1\57\2\60\1\52\7\57\12\52\2\57\2\1\12\61\1\1\1\51\17\52\1\57\2\60\1\1\10\52" +
		"\2\1\2\52\2\1\26\52\1\1\7\52\1\1\1\52\3\1\4\52\2\1\1\57\1\52\3\60\4\57\2\1\2\60\2" +
		"\1\2\60\1\57\1\52\10\1\1\60\4\1\2\52\1\1\3\52\2\57\2\1\12\61\2\52\17\1\2\57\1\60" +
		"\1\1\6\52\4\1\2\52\2\1\26\52\1\1\7\52\1\1\2\52\1\1\2\52\1\1\2\52\2\1\1\57\1\1\3\60" +
		"\2\57\4\1\2\57\2\1\3\57\3\1\1\57\7\1\4\52\1\1\1\52\7\1\12\61\2\57\3\52\1\57\13\1" +
		"\2\57\1\60\1\1\11\52\1\1\3\52\1\1\26\52\1\1\7\52\1\1\2\52\1\1\5\52\2\1\1\57\1\52" +
		"\3\60\5\57\1\1\2\57\1\60\1\1\2\60\1\57\2\1\1\52\17\1\2\52\2\57\2\1\12\61\11\1\1\52" +
		"\7\1\1\57\2\60\1\1\10\52\2\1\2\52\2\1\26\52\1\1\7\52\1\1\2\52\1\1\5\52\2\1\1\57\1" +
		"\52\1\60\1\57\1\60\4\57\2\1\2\60\2\1\2\60\1\57\10\1\1\57\1\60\4\1\2\52\1\1\3\52\2" +
		"\57\2\1\12\61\1\1\1\52\20\1\1\57\1\52\1\1\6\52\3\1\3\52\1\1\4\52\3\1\2\52\1\1\1\52" +
		"\1\1\2\52\3\1\2\52\3\1\3\52\3\1\14\52\4\1\2\60\1\57\2\60\3\1\3\60\1\1\3\60\1\57\2" +
		"\1\1\52\6\1\1\60\16\1\12\61\20\1\1\57\3\60\1\1\10\52\1\1\3\52\1\1\27\52\1\1\20\52" +
		"\3\1\1\52\3\57\4\60\1\1\3\57\1\1\4\57\7\1\2\57\1\1\3\52\5\1\2\52\2\57\2\1\12\61\21" +
		"\1\1\57\2\60\1\1\10\52\1\1\3\52\1\1\27\52\1\1\12\52\1\1\5\52\2\1\1\57\1\52\1\60\1" +
		"\57\5\60\1\1\1\57\2\60\1\1\2\60\2\57\7\1\2\60\7\1\1\52\1\1\2\52\2\57\2\1\12\61\1" +
		"\1\2\52\16\1\1\57\2\60\1\1\10\52\1\1\3\52\1\1\51\52\2\1\1\52\3\60\4\57\1\1\3\60\1" +
		"\1\3\60\1\57\1\52\10\1\1\60\7\1\3\52\2\57\2\1\12\61\12\1\6\52\2\1\2\60\1\1\22\52" +
		"\3\1\30\52\1\1\11\52\1\1\1\52\2\1\7\52\3\1\1\57\4\1\3\60\3\57\1\1\1\57\1\1\10\60" +
		"\6\1\12\61\2\1\2\60\15\1\60\52\1\57\2\52\7\57\5\1\6\52\1\51\10\57\1\1\12\61\47\1" +
		"\2\52\1\1\1\52\2\1\2\52\1\1\1\52\2\1\1\52\6\1\4\52\1\1\7\52\1\1\3\52\1\1\1\52\1\1" +
		"\1\52\2\1\2\52\1\1\4\52\1\57\2\52\6\57\1\1\2\57\1\52\2\1\5\52\1\1\1\51\1\1\6\57\2" +
		"\1\12\61\2\1\4\52\40\1\1\52\27\1\2\57\6\1\12\61\13\1\1\57\1\1\1\57\1\1\1\57\4\1\2" +
		"\60\10\52\1\1\44\52\4\1\16\57\1\60\5\57\1\1\2\57\5\52\13\57\1\1\44\57\11\1\1\57\71" +
		"\1\53\52\2\60\4\57\1\60\6\57\1\60\2\57\2\60\2\57\1\52\12\61\6\1\6\52\2\60\2\57\4" +
		"\52\3\57\1\52\3\60\2\52\7\60\3\52\4\57\15\52\1\57\2\60\2\57\6\60\1\57\1\52\1\60\12" +
		"\61\3\60\1\57\2\1\46\46\1\1\1\46\5\1\1\46\2\1\53\52\1\1\1\51\u014c\52\1\1\4\52\2" +
		"\1\7\52\1\1\1\52\1\1\4\52\2\1\51\52\1\1\4\52\2\1\41\52\1\1\4\52\2\1\7\52\1\1\1\52" +
		"\1\1\4\52\2\1\17\52\1\1\71\52\1\1\4\52\2\1\103\52\2\1\3\57\40\1\20\52\20\1\126\46" +
		"\2\1\6\47\3\1\u026c\52\2\1\21\52\1\63\32\52\5\1\113\52\3\1\3\53\10\52\7\1\15\52\1" +
		"\1\4\52\3\57\13\1\22\52\3\57\13\1\22\52\2\57\14\1\15\52\1\1\3\52\1\1\2\57\14\1\64" +
		"\52\2\57\1\60\7\57\10\60\1\57\2\60\13\57\3\1\1\51\4\1\1\52\1\57\2\1\12\61\41\1\3" +
		"\57\2\1\12\61\6\1\43\52\1\51\64\52\10\1\51\52\1\57\1\52\5\1\106\52\12\1\37\52\1\1" +
		"\3\57\4\60\2\57\3\60\4\1\2\60\1\57\6\60\3\57\12\1\12\61\36\52\2\1\5\52\13\1\54\52" +
		"\4\1\32\52\6\1\12\61\46\1\27\52\2\57\2\60\1\57\4\1\65\52\1\60\1\57\1\60\7\57\1\1" +
		"\1\57\1\60\1\57\2\60\10\57\6\60\12\57\2\1\1\57\12\61\6\1\12\61\15\1\1\51\10\1\16" +
		"\57\102\1\4\57\1\60\57\52\1\57\1\60\5\57\1\60\1\57\5\60\1\57\2\60\7\52\4\1\12\61" +
		"\21\1\11\57\14\1\2\57\1\60\36\52\1\60\4\57\2\60\2\57\1\60\3\57\2\52\12\61\54\52\1" +
		"\57\1\60\2\57\3\60\1\57\1\60\3\57\2\60\14\1\44\52\10\60\10\57\2\60\2\57\10\1\12\61" +
		"\3\1\3\52\12\61\36\52\6\51\122\1\3\57\1\1\15\57\1\60\7\57\4\52\1\57\4\52\2\60\1\57" +
		"\2\52\1\1\2\57\6\1\54\47\77\51\15\47\1\51\42\47\45\51\66\57\6\1\4\57\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\11\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\11\47\10\46\6\47\2\1\6\46\2\1\10\47\10\46\10\47\10\46\6\47" +
		"\2\1\6\46\2\1\10\47\1\1\1\46\1\1\1\46\1\1\1\46\1\1\1\46\10\47\10\46\16\47\2\1\10" +
		"\47\10\50\10\47\10\50\10\47\10\50\5\47\1\1\2\47\4\46\1\50\1\1\1\47\3\1\3\47\1\1\2" +
		"\47\4\46\1\50\3\1\4\47\2\1\2\47\4\46\4\1\10\47\5\46\5\1\3\47\1\1\2\47\4\46\1\50\3" +
		"\1\13\63\1\1\1\6\1\7\32\1\2\64\5\1\1\63\17\1\2\62\23\1\1\62\12\1\1\63\21\1\1\51\15" +
		"\1\1\51\20\1\15\51\63\1\15\57\4\1\1\57\3\1\14\57\21\1\1\46\4\1\1\46\2\1\1\47\3\46" +
		"\2\47\3\46\1\47\1\1\1\46\3\1\5\46\6\1\1\46\1\1\1\46\1\1\1\46\1\1\4\46\1\1\1\47\4" +
		"\46\1\47\4\52\1\47\2\1\2\47\2\46\5\1\1\46\4\47\4\1\1\47\21\1\43\53\1\46\1\47\4\53" +
		"\u0a77\1\57\46\1\1\57\47\1\1\1\46\1\47\3\46\2\47\1\46\1\47\1\46\1\47\1\46\1\47\4" +
		"\46\1\47\1\46\2\47\1\46\6\47\2\51\3\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\2\47\6\1\1\46\1\47\1\46\1\47\3\57" +
		"\1\46\1\47\14\1\46\47\1\1\1\47\5\1\1\47\2\1\70\52\7\1\1\51\17\1\1\57\27\52\11\1\7" +
		"\52\1\1\7\52\1\1\7\52\1\1\7\52\1\1\7\52\1\1\7\52\1\1\7\52\1\1\7\52\1\1\40\57\57\1" +
		"\1\51\u01d0\1\1\63\4\1\1\51\1\52\1\53\31\1\11\53\4\57\2\60\1\1\5\51\2\1\3\53\1\51" +
		"\1\52\4\1\126\52\2\1\2\57\2\1\2\51\1\52\1\1\132\52\1\1\3\51\1\52\5\1\51\52\3\1\136" +
		"\52\21\1\33\52\65\1\20\52\u0200\1\u19b6\52\112\1\u51d6\52\52\1\25\52\1\51\u0477\52" +
		"\103\1\50\52\6\51\2\1\u010c\52\1\51\3\1\20\52\12\61\2\52\24\1\1\46\1\47\1\46\1\47" +
		"\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47" +
		"\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47" +
		"\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\52\1\57\4\1\12\57\1\1\1\51\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1" +
		"\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\2\51\2\57\106\52\12\53" +
		"\2\57\45\1\11\51\2\1\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47" +
		"\1\46\3\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47" +
		"\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47" +
		"\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47" +
		"\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47" +
		"\1\51\10\47\1\46\1\47\1\46\1\47\2\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47" +
		"\1\51\2\1\1\46\1\47\1\46\1\47\1\52\1\46\1\47\1\46\3\47\1\46\1\47\1\46\1\47\1\46\1" +
		"\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\1\46\1\47\4\46\2" +
		"\1\5\46\1\47\1\46\1\47\77\1\1\52\2\51\1\47\7\52\1\57\3\52\1\57\4\52\1\57\27\52\2" +
		"\60\2\57\1\60\30\1\64\52\14\1\2\60\62\52\20\60\1\57\13\1\12\61\6\1\22\57\6\52\3\1" +
		"\1\52\1\1\1\52\2\1\12\61\34\52\10\57\2\1\27\52\13\57\2\60\14\1\35\52\3\1\3\57\1\60" +
		"\57\52\1\57\2\60\4\57\2\60\1\57\4\60\16\1\1\51\12\61\6\1\5\52\1\57\1\51\11\52\12" +
		"\61\5\52\1\1\51\52\6\57\2\60\2\57\2\60\2\57\11\1\3\52\1\57\10\52\1\57\1\60\2\1\12" +
		"\61\6\1\20\52\1\51\6\52\3\1\1\52\1\60\1\57\1\60\62\52\1\57\1\52\3\57\2\52\2\57\5" +
		"\52\2\57\1\52\1\57\1\52\30\1\2\52\1\51\2\1\13\52\1\60\2\57\2\60\2\1\1\52\2\51\1\60" +
		"\1\57\12\1\6\52\2\1\6\52\2\1\6\52\11\1\7\52\1\1\7\52\1\1\53\47\1\1\4\51\6\47\12\1" +
		"\120\47\43\52\2\60\1\57\2\60\1\57\2\60\1\1\1\60\1\57\2\1\12\61\6\1\u2ba4\52\14\1" +
		"\27\52\4\1\61\52\u2104\1\u016e\52\2\1\152\52\46\1\7\47\14\1\5\47\5\1\1\52\1\57\12" +
		"\52\1\1\15\52\1\1\5\52\1\1\1\52\1\1\2\52\1\1\2\52\1\1\154\52\41\1\u016b\52\22\1\100" +
		"\52\2\1\66\52\50\1\14\52\4\1\20\57\20\1\20\57\3\1\2\62\30\1\3\62\40\1\5\52\1\1\207" +
		"\52\2\1\1\63\20\1\12\61\7\1\32\46\4\1\1\62\1\1\32\47\13\1\12\52\1\51\55\52\2\51\37" +
		"\52\3\1\6\52\2\1\6\52\2\1\6\52\2\1\3\52\43\1\14\52\1\1\32\52\1\1\23\52\1\1\2\52\1" +
		"\1\17\52\2\1\16\52\42\1\173\52\105\1\65\53\210\1\1\57\202\1\35\52\3\1\61\52\17\1" +
		"\1\57\37\1\40\52\20\1\21\52\1\53\10\52\1\53\5\1\46\52\5\57\5\1\36\52\2\1\44\52\4" +
		"\1\10\52\1\1\5\53\52\1\50\46\50\47\116\52\2\1\12\61\126\1\50\52\10\1\64\52\234\1" +
		"\u0137\52\11\1\26\52\12\1\10\52\230\1\6\52\2\1\1\52\1\1\54\52\1\1\2\52\3\1\1\52\2" +
		"\1\27\52\12\1\27\52\11\1\37\52\101\1\23\52\1\1\2\52\12\1\26\52\12\1\32\52\106\1\70" +
		"\52\6\1\2\52\100\1\1\52\3\57\1\1\2\57\5\1\4\57\4\52\1\1\3\52\1\1\33\52\4\1\3\57\4" +
		"\1\1\57\40\1\35\52\3\1\35\52\43\1\10\52\1\1\34\52\2\57\31\1\66\52\12\1\26\52\12\1" +
		"\23\52\15\1\22\52\156\1\111\52\67\1\63\46\15\1\63\47\u030d\1\1\60\1\57\1\60\65\52" +
		"\17\57\37\1\12\61\17\1\3\57\1\60\55\52\3\60\4\57\2\60\2\57\25\1\31\52\7\1\12\61\6" +
		"\1\3\57\44\52\5\57\1\60\10\57\1\1\12\61\20\1\43\52\1\57\2\1\1\52\11\1\2\57\1\60\60" +
		"\52\3\60\11\57\2\60\4\52\5\1\3\57\3\1\12\61\1\52\1\1\1\52\43\1\22\52\1\1\31\52\3" +
		"\60\3\57\2\60\1\57\1\60\2\57\110\1\7\52\1\1\1\52\1\1\4\52\1\1\17\52\1\1\12\52\7\1" +
		"\57\52\1\57\3\60\10\57\5\1\12\61\6\1\2\57\2\60\1\1\10\52\2\1\2\52\2\1\26\52\1\1\7" +
		"\52\1\1\2\52\1\1\5\52\2\1\1\57\1\52\2\60\1\57\4\60\2\1\2\60\2\1\3\60\2\1\1\52\6\1" +
		"\1\60\5\1\5\52\2\60\2\1\7\57\3\1\5\57\u010b\1\60\52\3\60\6\57\1\60\1\57\4\60\2\57" +
		"\1\60\2\57\2\52\1\1\1\52\10\1\12\61\246\1\57\52\3\60\4\57\2\1\4\60\2\57\1\60\2\57" +
		"\27\1\4\52\2\57\42\1\60\52\3\60\10\57\2\60\1\57\1\60\2\57\3\1\1\52\13\1\12\61\46" +
		"\1\53\52\1\57\1\60\1\57\2\60\6\57\1\60\1\57\10\1\12\61\66\1\32\52\3\1\3\57\2\60\4" +
		"\57\1\60\5\57\4\1\12\61\u0166\1\40\46\40\47\12\61\25\1\1\52\u01c0\1\71\52\u0507\1" +
		"\u039a\52\146\1\157\53\21\1\304\52\u0abc\1\u042f\52\u0fd1\1\u0247\52\u21b9\1\u0239" +
		"\52\7\1\37\52\1\1\12\61\146\1\36\52\2\1\5\57\13\1\60\52\7\57\11\1\4\51\14\1\12\61" +
		"\11\1\25\52\5\1\23\52\u0370\1\105\52\13\1\1\52\56\60\20\1\4\57\15\51\u4060\1\2\52" +
		"\u0bfe\1\153\52\5\1\15\52\3\1\11\52\7\1\12\52\3\1\2\57\u14c6\1\2\60\3\57\3\1\6\60" +
		"\10\1\10\57\2\1\7\57\36\1\4\57\224\1\3\57\u01bb\1\32\46\32\47\32\46\7\47\1\1\22\47" +
		"\32\46\32\47\1\46\1\1\2\46\2\1\1\46\2\1\2\46\2\1\4\46\1\1\10\46\4\47\1\1\1\47\1\1" +
		"\7\47\1\1\13\47\32\46\32\47\2\46\1\1\4\46\2\1\10\46\1\1\7\46\1\1\32\47\2\46\1\1\4" +
		"\46\1\1\5\46\1\1\1\46\3\1\7\46\1\1\32\47\32\46\32\47\32\46\32\47\32\46\32\47\32\46" +
		"\32\47\32\46\32\47\32\46\34\47\2\1\31\46\1\1\31\47\1\1\6\47\31\46\1\1\31\47\1\1\6" +
		"\47\31\46\1\1\31\47\1\1\6\47\31\46\1\1\31\47\1\1\6\47\31\46\1\1\31\47\1\1\6\47\1" +
		"\46\1\47\2\1\62\61\u0200\1\67\57\4\1\62\57\10\1\1\57\16\1\1\57\26\1\5\57\1\1\17\57" +
		"\u0d50\1\305\52\13\1\7\57\u0529\1\4\52\1\1\33\52\1\1\2\52\1\1\1\52\2\1\1\52\1\1\12" +
		"\52\1\1\4\52\1\1\1\52\1\1\1\52\6\1\1\52\4\1\1\52\1\1\1\52\1\1\1\52\1\1\3\52\1\1\2" +
		"\52\1\1\1\52\2\1\1\52\1\1\1\52\1\1\1\52\1\1\1\52\1\1\1\52\1\1\2\52\1\1\1\52\2\1\4" +
		"\52\1\1\7\52\1\1\4\52\1\1\4\52\1\1\1\52\1\1\12\52\1\1\21\52\5\1\3\52\1\1\5\52\1\1" +
		"\21\52\u1144\1\ua6d7\52\51\1\u1035\52\13\1\336\52\2\1\u1682\52\u295e\1\u021e\52\uffff" +
		"\1\uffff\1\uffff\1\uffff\1\uffff\1\uffff\1\uffff\1\uffff\1\uffff\1\uffff\1\uffff" +
		"\1\u06ed\1\360\57");

	private static char[] unpack_vc_char(int size, String... st) {
		char[] res = new char[size];
		int t = 0;
		int count = 0;
		for (String s : st) {
			int slen = s.length();
			for (int i = 0; i < slen; ) {
				count = i > 0 || count == 0 ? s.charAt(i++) : count;
				if (i < slen) {
					char val = s.charAt(i++);
					while (count-- > 0) res[t++] = val;
				}
			}
		}
		assert res.length == t;
		return res;
	}

	private static final short tmStateMap[] = {
		0, 111
	};

	private static final short tmBacktracking[] = {
		89, 3, 89, 28, 90, 69, 94, 92, 2, 106, 95, 83
	};

	private static final int tmFirstRule = -7;

	private static final int[] tmRuleSymbol = unpack_int(97,
		"\uffff\uffff\0\0\5\0\1\0\2\0\3\0\4\0\6\0\7\0\10\0\11\0\12\0\13\0\14\0\15\0\16\0\17" +
		"\0\20\0\21\0\22\0\23\0\24\0\25\0\26\0\27\0\30\0\31\0\32\0\33\0\34\0\35\0\36\0\37" +
		"\0\40\0\41\0\42\0\43\0\44\0\45\0\46\0\47\0\50\0\51\0\52\0\53\0\54\0\55\0\56\0\57" +
		"\0\60\0\61\0\62\0\63\0\64\0\65\0\66\0\67\0\70\0\71\0\72\0\73\0\74\0\75\0\76\0\77" +
		"\0\100\0\101\0\102\0\103\0\104\0\105\0\106\0\107\0\110\0\111\0\112\0\113\0\114\0" +
		"\115\0\116\0\117\0\120\0\121\0\122\0\123\0\124\0\125\0\126\0\127\0\130\0\130\0\130" +
		"\0\131\0\131\0\132\0\133\0\134\0");

	private static final int tmClassesCount = 56;

	private static final short[] tmGoto = unpack_vc_short(6384,
		"\1\ufff8\1\ufff9\2\151\1\144\1\151\2\ufff9\1\143\1\142\1\120\1\116\1\115\1\114\1" +
		"\113\1\112\1\111\1\110\1\103\1\102\1\101\1\75\1\67\1\64\1\61\1\56\1\53\1\51\1\46" +
		"\1\43\1\41\1\40\1\37\1\36\1\33\1\21\1\151\1\7\6\151\1\2\2\151\4\ufff9\1\1\1\142\3" +
		"\151\70\ufff6\22\uffa0\1\6\17\uffa0\1\2\11\uffa0\1\2\10\uffa0\2\uffff\1\uffa0\31" +
		"\ufff9\2\5\7\ufff9\1\4\11\ufff9\1\4\13\ufff9\42\uffa0\1\4\11\uffa0\1\4\13\uffa0\42" +
		"\ufff9\1\4\11\ufff9\1\4\13\ufff9\42\uffa0\1\6\11\uffa0\1\6\10\uffa0\2\uffff\1\uffa0" +
		"\1\ufff9\3\7\1\11\3\7\2\ufff9\33\7\1\10\16\7\1\ufff9\3\7\70\uff9c\1\ufff9\4\7\1\15" +
		"\2\7\1\14\33\7\1\12\7\7\1\ufff9\13\7\42\ufff9\1\13\11\ufff9\3\13\6\ufff9\2\13\43" +
		"\ufff9\1\7\11\ufff9\3\7\6\ufff9\2\7\2\ufff9\3\7\1\11\3\7\1\ufff9\34\7\1\10\16\7\1" +
		"\ufff9\3\7\42\ufff9\1\16\11\ufff9\3\16\6\ufff9\2\16\43\ufff9\1\17\11\ufff9\3\17\6" +
		"\ufff9\2\17\43\ufff9\1\20\11\ufff9\3\20\6\ufff9\2\20\43\ufff9\1\7\11\ufff9\3\7\6" +
		"\ufff9\2\7\2\ufff9\3\21\1\23\3\21\2\ufff9\31\21\1\22\20\21\1\ufff9\3\21\70\uff9d" +
		"\1\ufff9\4\21\1\27\2\21\1\26\33\21\1\24\7\21\1\ufff9\13\21\42\ufff9\1\25\11\ufff9" +
		"\3\25\6\ufff9\2\25\43\ufff9\1\21\11\ufff9\3\21\6\ufff9\2\21\2\ufff9\3\21\1\23\3\21" +
		"\1\ufff9\32\21\1\22\20\21\1\ufff9\3\21\42\ufff9\1\30\11\ufff9\3\30\6\ufff9\2\30\43" +
		"\ufff9\1\31\11\ufff9\3\31\6\ufff9\2\31\43\ufff9\1\32\11\ufff9\3\32\6\ufff9\2\32\43" +
		"\ufff9\1\21\11\ufff9\3\21\6\ufff9\2\21\1\ufff9\22\uffa0\1\6\21\uffa0\1\ufffe\20\uffa0" +
		"\2\uffff\1\ufffe\42\ufff9\1\35\11\ufff9\3\35\6\ufff9\2\35\1\ufff9\42\uff9e\1\35\11" +
		"\uff9e\3\35\6\uff9e\2\35\1\uff9e\70\uffaf\70\uffb0\70\uffb3\27\uffb5\1\42\40\uffb5" +
		"\70\uffa4\27\uffb6\1\45\5\uffb6\1\44\32\uffb6\70\uffb1\70\uffa5\27\uffb7\1\50\4\uffb7" +
		"\1\47\33\uffb7\70\uffb2\70\uffa6\27\uffbd\1\52\40\uffbd\70\uffaa\27\uffbf\1\55\2" +
		"\uffbf\1\54\35\uffbf\70\uffbb\70\uffac\27\uffc0\1\60\1\uffc0\1\57\36\uffc0\70\uffbc" +
		"\70\uffad\27\uffb4\1\62\40\uffb4\27\uffc3\1\63\40\uffc3\70\uffc1\27\uffae\1\65\40" +
		"\uffae\27\uffc4\1\66\40\uffc4\70\uffc2\26\uffc7\1\71\1\70\40\uffc7\70\uffc5\26\uffb9" +
		"\1\73\1\72\40\uffb9\70\uffa8\27\uffb8\1\74\40\uffb8\70\uffa7\25\uffc8\1\77\1\uffc8" +
		"\1\76\40\uffc8\70\uffc6\27\uffba\1\100\40\uffba\70\uffa9\70\uffc9\70\uffca\42\uffcb" +
		"\1\104\11\uffcb\1\104\13\uffcb\42\uff9f\1\104\11\uff9f\1\104\10\uff9f\2\ufffd\1\uff9f" +
		"\31\ufff9\2\107\7\ufff9\1\106\11\ufff9\1\106\13\ufff9\42\uff9f\1\106\11\uff9f\1\106" +
		"\13\uff9f\42\ufff9\1\106\11\ufff9\1\106\13\ufff9\70\uffcc\70\uffcd\70\uffce\70\uffcf" +
		"\70\uffd0\70\uffd1\27\uffbe\1\117\40\uffbe\70\uffab\1\ufff9\3\130\1\127\3\130\2\ufff9" +
		"\1\126\1\123\4\130\1\121\43\130\1\ufff9\3\130\1\ufff9\3\121\1\122\3\121\2\ufff9\7" +
		"\121\1\130\42\121\1\ufff9\3\121\1\ufff9\7\121\2\ufff9\52\121\1\ufff9\3\121\1\ufff9" +
		"\12\123\1\124\54\123\1\ufff9\11\123\1\125\1\124\54\123\70\ufff4\1\ufff3\7\126\2\ufff3" +
		"\52\126\1\ufff3\3\126\1\ufff9\7\130\2\ufff9\52\130\1\ufff9\3\130\1\ufff9\3\130\1" +
		"\141\3\130\2\ufff9\1\133\5\130\1\131\43\130\1\ufff9\3\130\1\ufff9\3\131\1\132\3\131" +
		"\2\ufff9\7\131\1\130\42\131\1\ufff9\3\131\1\ufff9\7\131\2\ufff9\52\131\1\ufff9\3" +
		"\131\2\uff9b\2\133\1\ufffc\3\133\32\uff9b\1\133\1\uff9b\1\133\1\uff9b\15\133\2\uff9b" +
		"\3\133\5\ufff9\1\135\124\ufff9\1\136\11\ufff9\3\136\6\ufff9\2\136\43\ufff9\1\137" +
		"\11\ufff9\3\137\6\ufff9\2\137\43\ufff9\1\140\11\ufff9\3\140\6\ufff9\2\140\43\ufff9" +
		"\1\133\11\ufff9\3\133\6\ufff9\2\133\2\ufff9\7\130\2\ufff9\52\130\1\ufff9\3\130\101" +
		"\ufff5\1\142\56\ufff5\5\ufff9\1\145\124\ufff9\1\146\11\ufff9\3\146\6\ufff9\2\146" +
		"\43\ufff9\1\147\11\ufff9\3\147\6\ufff9\2\147\43\ufff9\1\150\11\ufff9\3\150\6\ufff9" +
		"\2\150\43\ufff9\1\151\11\ufff9\3\151\6\ufff9\2\151\1\ufff9\2\ufff7\2\151\1\ufffb" +
		"\3\151\32\ufff7\1\151\1\ufff7\1\151\1\ufff7\15\151\2\ufff7\3\151\5\ufff9\1\153\124" +
		"\ufff9\1\154\11\ufff9\3\154\6\ufff9\2\154\43\ufff9\1\155\11\ufff9\3\155\6\ufff9\2" +
		"\155\43\ufff9\1\156\11\ufff9\3\156\6\ufff9\2\156\43\ufff9\1\151\11\ufff9\3\151\6" +
		"\ufff9\2\151\3\ufff9\2\151\1\144\1\151\2\ufff9\1\143\1\142\1\160\1\116\1\115\1\114" +
		"\1\113\1\112\1\111\1\110\1\103\1\102\1\101\1\75\1\67\1\64\1\61\1\56\1\53\1\51\1\46" +
		"\1\43\1\41\1\40\1\37\1\36\1\33\1\21\1\151\1\7\6\151\1\2\2\151\4\ufff9\1\1\1\142\3" +
		"\151\12\uff9a\1\126\1\ufffa\13\uff9a\1\161\40\uff9a\70\uff99");

	private static short[] unpack_vc_short(int size, String... st) {
		short[] res = new short[size];
		int t = 0;
		int count = 0;
		for (String s : st) {
			int slen = s.length();
			for (int i = 0; i < slen; ) {
				count = i > 0 || count == 0 ? s.charAt(i++) : count;
				if (i < slen) {
					short val = (short) s.charAt(i++);
					while (count-- > 0) res[t++] = val;
				}
			}
		}
		assert res.length == t;
		return res;
	}

	private static int mapCharacter(int chr) {
		if (chr >= 0 && chr < 918000) return tmCharClass[chr];
		return chr == -1 ? 0 : 1;
	}

	public Span next() throws IOException {
		Span token = new Span();
		int state;

		tokenloop:
		do {
			token.offset = currOffset;
			tokenLine = token.line = currLine;
			tokenOffset = charOffset;

			// TODO use backupRule
			int backupRule = -1;
			for (state = tmStateMap[this.state]; state >= 0; ) {
				state = tmGoto[state * tmClassesCount + mapCharacter(chr)];
				if (state > tmFirstRule && state < 0) {
					token.endoffset = currOffset;
					state = (-1 - state) * 2;
					backupRule = tmBacktracking[state++];
					state = tmBacktracking[state];
				}
				if (state == tmFirstRule && chr == -1) {
					token.endoffset = currOffset;
					token.symbol = 0;
					token.value = null;
					reporter.error("Unexpected end of input reached", token.line, token.offset, token.endoffset);
					token.offset = currOffset;
					break tokenloop;
				}
				if (state >= tmFirstRule && chr != -1) {
					currOffset += l - charOffset;
					if (chr == '\n') {
						currLine++;
					}
					charOffset = l;
					chr = l < input.length() ? input.charAt(l++) : -1;
					if (chr >= Character.MIN_HIGH_SURROGATE && chr <= Character.MAX_HIGH_SURROGATE && l < input.length() &&
							Character.isLowSurrogate(input.charAt(l))) {
						chr = Character.toCodePoint((char) chr, input.charAt(l++));
					}
				}
			}
			token.endoffset = currOffset;

			token.symbol = tmRuleSymbol[tmFirstRule - state];
			token.value = null;

			if (token.symbol == -1) {
				reporter.error(MessageFormat.format("invalid token at line {0}: `{1}`, skipped", currLine, tokenText()), token.line, token.offset, token.endoffset);
			}

		} while (token.symbol == -1 || !createToken(token, tmFirstRule - state));
		return token;
	}

	protected int charAt(int i) {
		if (i == 0) return chr;
		i += l - 1;
		int res = i < input.length() ? input.charAt(i++) : -1;
		if (res >= Character.MIN_HIGH_SURROGATE && res <= Character.MAX_HIGH_SURROGATE && i < input.length() &&
				Character.isLowSurrogate(input.charAt(i))) {
			res = Character.toCodePoint((char) res, input.charAt(i++));
		}
		return res;
	}

	protected boolean createToken(Span token, int ruleIndex) throws IOException {
		boolean spaceToken = false;
		switch (ruleIndex) {
			case 2:
				return createIdentifierToken(token, ruleIndex);
			case 3: // space: /[\t\v\f \xa0\ufeff\p{Zs}]/
				spaceToken = true;
				break;
			case 4: // LineTerminatorSequence: /[\n\r\u2028\u2029]|\r\n/
				spaceToken = true;
				break;
			case 5: // MultiLineComment: /\/\*{commentChars}?\*\//
				spaceToken = true;
				break;
			case 6: // SingleLineComment: /\/\/[^\n\r\u2028\u2029]*/
				spaceToken = true;
				break;
		}
		return !(spaceToken);
	}

	private static Map<String,Integer> subTokensOfIdentifier = new HashMap<>();
	static {
		subTokensOfIdentifier.put("break", 7);
		subTokensOfIdentifier.put("case", 8);
		subTokensOfIdentifier.put("catch", 9);
		subTokensOfIdentifier.put("continue", 10);
		subTokensOfIdentifier.put("debugger", 11);
		subTokensOfIdentifier.put("default", 12);
		subTokensOfIdentifier.put("delete", 13);
		subTokensOfIdentifier.put("do", 14);
		subTokensOfIdentifier.put("else", 15);
		subTokensOfIdentifier.put("finally", 16);
		subTokensOfIdentifier.put("for", 17);
		subTokensOfIdentifier.put("function", 18);
		subTokensOfIdentifier.put("if", 19);
		subTokensOfIdentifier.put("in", 20);
		subTokensOfIdentifier.put("instanceof", 21);
		subTokensOfIdentifier.put("new", 22);
		subTokensOfIdentifier.put("return", 23);
		subTokensOfIdentifier.put("switch", 24);
		subTokensOfIdentifier.put("this", 25);
		subTokensOfIdentifier.put("throw", 26);
		subTokensOfIdentifier.put("try", 27);
		subTokensOfIdentifier.put("typeof", 28);
		subTokensOfIdentifier.put("var", 29);
		subTokensOfIdentifier.put("void", 30);
		subTokensOfIdentifier.put("while", 31);
		subTokensOfIdentifier.put("with", 32);
		subTokensOfIdentifier.put("class", 33);
		subTokensOfIdentifier.put("const", 34);
		subTokensOfIdentifier.put("enum", 35);
		subTokensOfIdentifier.put("export", 36);
		subTokensOfIdentifier.put("extends", 37);
		subTokensOfIdentifier.put("import", 38);
		subTokensOfIdentifier.put("super", 39);
		subTokensOfIdentifier.put("null", 86);
		subTokensOfIdentifier.put("true", 87);
		subTokensOfIdentifier.put("false", 88);
	}

	protected boolean createIdentifierToken(Span token, int ruleIndex) {
		Integer replacement = subTokensOfIdentifier.get(tokenText());
		if (replacement != null) {
			ruleIndex = replacement;
			token.symbol = tmRuleSymbol[ruleIndex];
		}
		return true;
	}

	/* package */ static int[] unpack_int(int size, String... st) {
		int[] res = new int[size];
		boolean second = false;
		char first = 0;
		int t = 0;
		for (String s : st) {
			int slen = s.length();
			for (int i = 0; i < slen; i++) {
				if (second) {
					res[t++] = (s.charAt(i) << 16) + first;
				} else {
					first = s.charAt(i);
				}
				second = !second;
			}
		}
		assert !second;
		assert res.length == t;
		return res;
	}

}
