/**
 * Copyright (c) 2010-2012 Evgeny Gryaznov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package org.textmapper.idea.lang.syntax.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.textmapper.tool.parser.TMLexer.Lexems;

public interface LapgTokenTypes {

	IElementType WHITESPACE = TokenType.WHITE_SPACE;
	TokenSet whitespaces = TokenSet.create(WHITESPACE);

	IElementType COMMENT = new LapgElementType(Lexems._skip_comment, "comment");
	TokenSet comments = TokenSet.create(COMMENT);

	// tokens
	IElementType STRING = new LapgElementType(Lexems.scon, "string");
	IElementType ICON = new LapgElementType(Lexems.icon, "int");
	IElementType ID = new LapgElementType(Lexems.ID, "ID");
	IElementType REGEXP = new LapgElementType(Lexems.regexp, "regexp");

	TokenSet strings = TokenSet.create(STRING);

	// inner tokens
	IElementType TOKEN_ACTION = new LapgTemplatesElementType(Lexems.code, true, "action");
	IElementType TEMPLATES = new LapgTemplatesElementType(Lexems.eoi, false, "templates");

	// [] () {}
	IElementType OP_LBRACKET = new LapgElementType(Lexems.LSQUARE, "[");
	IElementType OP_RBRACKET = new LapgElementType(Lexems.RSQUARE, "]");
	IElementType OP_LPAREN = new LapgElementType(Lexems.LPAREN, "(");
	IElementType OP_RPAREN = new LapgElementType(Lexems.RPAREN, ")");
	IElementType OP_LCURLY = new LapgElementType(Lexems.LCURLY, "{");
	IElementType OP_RCURLY = new LapgElementType(Lexems.RCURLY, "}");

	// punctuation
	IElementType OP_SEMICOLON = new LapgElementType(Lexems.SEMICOLON, ";");
	IElementType OP_DOT = new LapgElementType(Lexems.DOT, ".");
	IElementType OP_COMMA = new LapgElementType(Lexems.COMMA, ",");

	// operators
	IElementType OP_PERCENT = new LapgElementType(Lexems.PERCENT, "%");
	IElementType OP_CCEQ = new LapgElementType(Lexems.COLONCOLONEQUAL, "::=");
	IElementType OP_OR = new LapgElementType(Lexems.OR, "|");
	IElementType OP_EQ = new LapgElementType(Lexems.EQUAL, "=");
	IElementType OP_EQGT = new LapgElementType(Lexems.EQUALGREATER, "=>");
	IElementType OP_COLON = new LapgElementType(Lexems.COLON, ":");
	IElementType OP_COLONCOLON = new LapgElementType(Lexems.COLONCOLON, "::");
	IElementType OP_LT = new LapgElementType(Lexems.LESS, "<");
	IElementType OP_GT = new LapgElementType(Lexems.GREATER, ">");
	IElementType OP_STAR = new LapgElementType(Lexems.MULT, "*");
	IElementType OP_PLUS = new LapgElementType(Lexems.PLUS, "+");
	IElementType OP_PLUSEQ = new LapgElementType(Lexems.PLUSEQUAL, "+=");
	IElementType OP_QMARK = new LapgElementType(Lexems.QUESTIONMARK, "?");
	//TODO IElementType OP_ARROW = new LapgElementType(Lexems.MINUSGREATER, "->");
	IElementType OP_LPAREN_QMARK_EXCL = new LapgElementType(Lexems.LPARENQUESTIONMARKEXCLAMATION, "(?!");
	IElementType OP_AND = new LapgElementType(Lexems.AMPERSAND, "&");
	IElementType OP_AT = new LapgElementType(Lexems.ATSIGN, "@");

	TokenSet operators = TokenSet.create(
			OP_PERCENT, OP_CCEQ, OP_OR, OP_EQ, OP_EQGT, OP_COLON, OP_COLONCOLON,
			OP_LT, OP_GT, OP_PLUSEQ/*, OP_ARROW*/, OP_LPAREN_QMARK_EXCL, OP_AND, OP_AT
	);

	TokenSet quantifiers = TokenSet.create(OP_PLUS, OP_QMARK, OP_STAR);

	// keywords
	IElementType KW_TRUE = new LapgElementType(Lexems.Ltrue, "true");
	IElementType KW_FALSE = new LapgElementType(Lexems.Lfalse, "false");
	IElementType KW_NEW = new LapgElementType(Lexems.Lnew, "new");
	IElementType KW_SEPARATOR = new LapgElementType(Lexems.Lseparator, "separator");
	IElementType KW_AS = new LapgElementType(Lexems.Las, "as");
	IElementType KW_IMPORT = new LapgElementType(Lexems.Limport, "import");

	// soft keywords
	IElementType KW_PRIO = new LapgElementType(Lexems.Lprio, "prio");
	IElementType KW_SHIFT = new LapgElementType(Lexems.Lshift, "shift");
	IElementType KW_REDUCE = new LapgElementType(Lexems.Lreduce, "reduce");
	IElementType KW_INPUT = new LapgElementType(Lexems.Linput, "input");
	IElementType KW_LEFT = new LapgElementType(Lexems.Lleft, "left");
	IElementType KW_RIGHT = new LapgElementType(Lexems.Lright, "right");
	IElementType KW_NONASSOC = new LapgElementType(Lexems.Lnonassoc, "nonassoc");
	IElementType KW_NOEOI = new LapgElementType(Lexems.Lnoeoi, "no-eoi");
	IElementType KW_INLINE = new LapgElementType(Lexems.Linline, "inline");
	IElementType KW_RETURNS = new LapgElementType(Lexems.Lreturns, "returns");
	IElementType KW_INTERFACE = new LapgElementType(Lexems.Linterface, "interface");
	IElementType KW_LANGUAGE = new LapgElementType(Lexems.Llanguage, "language");
	IElementType KW_LALR = new LapgElementType(Lexems.Llalr, "lalr");
	IElementType KW_LEXER = new LapgElementType(Lexems.Llexer, "lexer");
	IElementType KW_PARSER = new LapgElementType(Lexems.Lparser, "parser");
	IElementType KW_SOFT = new LapgElementType(Lexems.Lsoft, "soft");
	IElementType KW_CLASS = new LapgElementType(Lexems.Lclass, "class");
	IElementType KW_SPACE = new LapgElementType(Lexems.Lspace, "space");

	TokenSet keywords = TokenSet.create(
			KW_TRUE, KW_FALSE, KW_NEW, KW_SEPARATOR, KW_AS, KW_IMPORT);

	TokenSet softKeywords = TokenSet.create(
			KW_PRIO, KW_SHIFT, KW_REDUCE,
			KW_INPUT, KW_LEFT, KW_RIGHT, KW_NONASSOC, KW_NOEOI,
			KW_INLINE, KW_RETURNS, KW_INTERFACE, KW_LANGUAGE,
			KW_LALR, KW_LEXER, KW_PARSER, KW_SOFT, KW_CLASS, KW_SPACE);
}
