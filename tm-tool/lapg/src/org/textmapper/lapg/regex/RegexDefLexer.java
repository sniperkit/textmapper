/**
 * Copyright 2002-2017 Evgeny Gryaznov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.textmapper.lapg.regex;

import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class RegexDefLexer {

	public static class Span {
		public Object value;
		public int symbol;
		public int state;
		public int offset;
		public int endoffset;
	}

	public interface States {
		int initial = 0;
		int afterChar = 1;
		int inSet = 2;
	}

	public interface Tokens {
		int Unavailable_ = -1;
		int eoi = 0;
		int _char = 1;
		int escaped = 2;
		int charclass = 3;
		int Dot = 4;
		int Mult = 5;
		int Plus = 6;
		int Quest = 7;
		int quantifier = 8;
		int op_minus = 9;
		int op_union = 10;
		int op_intersect = 11;
		int Lparen = 12;
		int Or = 13;
		int Rparen = 14;
		int LparenQuest = 15;
		int Lbrack = 16;
		int LbrackXor = 17;
		int expand = 18;
		int kw_eoi = 19;
		int Rbrack = 20;
		int Minus = 21;
	}

	public interface ErrorReporter {
		void error(String message, int offset, int endoffset);
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

	private void quantifierReady() {
		if (chr == -1) {
			if (state == 1) state = 0;
			return;
		}
		if (state == 0) state = 1;
	}

	private int parseCodePoint(String s, Span token) {
		int ch = RegexUtil.unescapeHex(s);
		if (Character.isValidCodePoint(ch)) return ch;
		reporter.error("unicode code point is out of range", token.offset, token.endoffset);
		return 0;
	}

	public RegexDefLexer(CharSequence input, ErrorReporter reporter) throws IOException {
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

	private static final short tmCharClass[] = {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 33, 33, 1, 1, 33, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 22, 1, 23, 25, 17, 18, 20, 21, 16, 32,
		35, 35, 35, 35, 35, 35, 35, 35, 31, 31, 26, 1, 1, 1, 1, 19,
		1, 36, 36, 36, 37, 36, 36, 30, 30, 30, 30, 30, 30, 30, 30, 30,
		34, 30, 30, 38, 30, 14, 30, 38, 34, 30, 30, 27, 4, 29, 28, 30,
		1, 5, 6, 36, 37, 36, 7, 30, 30, 39, 30, 30, 30, 30, 8, 30,
		15, 30, 9, 40, 10, 13, 11, 38, 12, 30, 30, 2, 24, 3
	};

	private static final short tmStateMap[] = {
		0, 51, 66
	};

	private static final short tmBacktracking[] = {
		27, 6, 3, 47, 3, 64, 3, 62, 3, 59, 3, 56
	};

	private static final int tmFirstRule = -7;

	private static final int[] tmRuleSymbol = unpack_int(38,
		"\uffff\uffff\0\0\22\0\1\0\2\0\2\0\2\0\2\0\2\0\2\0\2\0\2\0\2\0\2\0\2\0\2\0\3\0\3\0" +
		"\4\0\5\0\6\0\7\0\10\0\11\0\12\0\13\0\1\0\14\0\15\0\16\0\17\0\20\0\21\0\1\0\23\0\24" +
		"\0\25\0\1\0");

	private static final int tmClassesCount = 41;

	private static final short[] tmGoto = unpack_vc_short(2870,
		"\1\ufff8\1\62\1\56\1\62\1\14\13\62\1\13\3\12\1\62\1\11\1\62\1\5\1\4\1\3\1\62\1\1" +
		"\1\62\1\ufff9\2\62\1\ufff9\10\62\34\uffda\1\2\14\uffda\51\uffd9\51\uffdc\51\uffdd" +
		"\23\uffde\1\uffff\25\uffde\25\ufff9\1\7\21\ufff9\2\7\25\ufff9\1\7\4\ufff9\1\10\14" +
		"\ufff9\2\7\51\uffdb\51\uffd8\51\uffdf\51\uffe7\1\ufff9\4\55\1\54\1\53\1\52\1\51\1" +
		"\50\1\47\1\46\1\43\1\36\1\25\1\21\17\55\1\ufff9\1\55\2\ufff9\1\16\1\55\2\15\1\55" +
		"\1\15\51\uffe9\43\ufff9\1\17\50\ufff9\1\20\5\ufff9\51\uffed\2\ufff9\1\22\53\ufff9" +
		"\13\23\16\ufff9\2\23\2\ufff9\7\23\3\ufff9\1\24\1\ufff9\13\23\16\ufff9\2\23\2\ufff9" +
		"\7\23\51\uffe8\5\ufff9\3\26\27\ufff9\1\26\3\ufff9\3\26\10\ufff9\3\27\27\ufff9\1\27" +
		"\3\ufff9\3\27\10\ufff9\3\30\27\ufff9\1\30\3\ufff9\3\30\10\ufff9\3\31\27\ufff9\1\31" +
		"\3\ufff9\3\31\10\ufff9\3\32\27\ufff9\1\32\3\ufff9\3\32\10\ufff9\3\33\27\ufff9\1\33" +
		"\3\ufff9\3\33\10\ufff9\3\34\27\ufff9\1\34\3\ufff9\3\34\10\ufff9\3\35\27\ufff9\1\35" +
		"\3\ufff9\3\35\3\ufff9\51\uffea\5\ufff9\3\37\27\ufff9\1\37\3\ufff9\3\37\10\ufff9\3" +
		"\40\27\ufff9\1\40\3\ufff9\3\40\10\ufff9\3\41\27\ufff9\1\41\3\ufff9\3\41\10\ufff9" +
		"\3\42\27\ufff9\1\42\3\ufff9\3\42\3\ufff9\51\uffeb\5\ufff9\3\44\27\ufff9\1\44\3\ufff9" +
		"\3\44\10\ufff9\3\45\27\ufff9\1\45\3\ufff9\3\45\3\ufff9\51\uffec\51\uffee\51\uffef" +
		"\51\ufff0\51\ufff1\51\ufff2\51\ufff3\51\ufff4\51\ufff5\5\ufff6\13\ufffe\16\ufff6" +
		"\1\ufffe\3\ufff6\1\ufffe\1\ufff6\5\ufffe\3\ufff9\1\61\1\ufff9\13\57\5\ufff9\1\60" +
		"\10\ufff9\2\57\2\ufff9\7\57\5\ufff9\13\57\5\ufff9\1\60\10\ufff9\2\57\2\ufff9\7\57" +
		"\51\ufff7\51\ufff6\1\ufff9\1\62\1\67\1\62\1\14\13\62\1\13\1\66\1\65\1\64\1\62\1\11" +
		"\1\62\1\5\1\4\1\3\1\62\1\1\1\62\1\ufff9\2\62\1\ufff9\10\62\51\uffe4\51\uffe5\51\uffe6" +
		"\5\ufff6\13\ufffe\2\ufff6\1\ufffd\2\ufff6\1\ufffc\1\ufffb\7\ufff6\1\ufffe\1\ufffa" +
		"\2\ufff6\1\ufffe\1\ufffa\5\ufffe\3\ufff9\1\72\20\ufff9\1\71\12\ufff9\1\70\3\ufff9" +
		"\1\70\10\ufff9\1\72\33\ufff9\1\71\3\ufff9\1\71\5\ufff9\51\uffe3\26\ufff9\1\74\25" +
		"\ufff9\1\75\45\ufff9\51\uffe0\3\ufff9\1\77\45\ufff9\51\uffe2\3\ufff9\1\101\45\ufff9" +
		"\51\uffe1\1\ufff9\3\62\1\14\13\62\1\13\3\12\1\62\1\105\1\62\3\104\1\62\1\ufff9\1" +
		"\62\1\103\2\62\1\ufff9\10\62\51\uffd6\51\uffd4\51\uffd5");

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
		if (chr >= 0 && chr < 126) return tmCharClass[chr];
		return chr == -1 ? 0 : 1;
	}

	public Span next() throws IOException {
		Span token = new Span();
		int state;

		tokenloop:
		do {
			token.offset = currOffset;
			tokenLine = currLine;
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
					reporter.error("Unexpected end of input reached", token.offset, token.endoffset);
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
				reporter.error(MessageFormat.format("invalid token at line {0}: `{1}`, skipped", currLine, tokenText()), token.offset, token.endoffset);
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
				return createExpandToken(token, ruleIndex);
			case 3: // char: /[^()\[\]\.|\\\/*?+\-]/
				{ token.value = tokenText().codePointAt(0); quantifierReady(); }
				break;
			case 4: // escaped: /\\[^\r\n\t0-9uUxXwWsSdDpPabfnrtv]/
				{ token.value = (int) tokenText().charAt(1); quantifierReady(); }
				break;
			case 5: // escaped: /\\a/
				{ token.value = (int) 7; quantifierReady(); }
				break;
			case 6: // escaped: /\\b/
				{ token.value = (int) '\b'; quantifierReady(); }
				break;
			case 7: // escaped: /\\f/
				{ token.value = (int) '\f'; quantifierReady(); }
				break;
			case 8: // escaped: /\\n/
				{ token.value = (int) '\n'; quantifierReady(); }
				break;
			case 9: // escaped: /\\r/
				{ token.value = (int) '\r'; quantifierReady(); }
				break;
			case 10: // escaped: /\\t/
				{ token.value = (int) '\t'; quantifierReady(); }
				break;
			case 11: // escaped: /\\v/
				{ token.value = (int) 0xb; quantifierReady(); }
				break;
			case 12: // escaped: /\\[0-7][0-7][0-7]/
				{ token.value = RegexUtil.unescapeOct(tokenText().substring(1)); quantifierReady(); }
				break;
			case 13: // escaped: /\\x{hx}{2}/
				{ token.value = parseCodePoint(tokenText().substring(2), token); quantifierReady(); }
				break;
			case 14: // escaped: /\\u{hx}{4}/
				{ token.value = parseCodePoint(tokenText().substring(2), token); quantifierReady(); }
				break;
			case 15: // escaped: /\\U{hx}{8}/
				{ token.value = parseCodePoint(tokenText().substring(2), token); quantifierReady(); }
				break;
			case 16: // charclass: /\\[wWsSdD]/
				{ token.value = tokenText().substring(1); quantifierReady(); }
				break;
			case 17: // charclass: /\\p\{\w+\}/
				{ token.value = tokenText().substring(3, tokenSize() - 1); quantifierReady(); }
				break;
			case 18: // '.': /\./
				{ quantifierReady(); }
				break;
			case 19: // '*': /\*/
				{ state = States.initial; }
				break;
			case 20: // '+': /\+/
				{ state = States.initial; }
				break;
			case 21: // '?': /\?/
				{ state = States.initial; }
				break;
			case 22: // quantifier: /\{[0-9]+(,[0-9]*)?\}/
				{ state = States.initial; }
				break;
			case 23: // op_minus: /\{\-\}/
				{ state = States.initial; }
				break;
			case 24: // op_union: /\{\+\}/
				{ state = States.initial; }
				break;
			case 25: // op_intersect: /\{&&\}/
				{ state = States.initial; }
				break;
			case 26: // char: /[*+?]/
				{ token.value = tokenText().codePointAt(0); quantifierReady(); }
				break;
			case 27: // '(': /\(/
				{ state = 0; }
				break;
			case 28: // '|': /\|/
				{ state = 0; }
				break;
			case 29: // ')': /\)/
				{ quantifierReady(); }
				break;
			case 30: // '(?': /\(\?[is\-]+:/
				{ state = 0; }
				break;
			case 31: // '[': /\[/
				{ state = States.inSet; }
				break;
			case 32: // '[^': /\[\^/
				{ state = States.inSet; }
				break;
			case 33: // char: /\-/
				{ token.value = tokenText().codePointAt(0); quantifierReady(); }
				break;
			case 35: // ']': /\]/
				{ state = 0; quantifierReady(); }
				break;
			case 37: // char: /[(|)]/
				{ token.value = tokenText().codePointAt(0); }
				break;
		}
		return !(spaceToken);
	}

	private static Map<String,Integer> subTokensOfExpand = new HashMap<>();
	static {
		subTokensOfExpand.put("{eoi}", 34);
	}

	protected boolean createExpandToken(Span token, int ruleIndex) {
		Integer replacement = subTokensOfExpand.get(tokenText());
		if (replacement != null) {
			ruleIndex = replacement;
			token.symbol = tmRuleSymbol[ruleIndex];
		}
		boolean spaceToken = false;
		switch(ruleIndex) {
			case 34:	// {eoi}
				{ state = 0; }
				break;
			case 2:	// <default>
				{ quantifierReady(); }
				break;
		}
		return !(spaceToken);
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
