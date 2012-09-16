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
package org.textmapper.lapg.idea.lang.syntax.refactoring;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import org.textmapper.lapg.parser.LapgLexer;
import org.textmapper.lapg.parser.LapgLexer.Lexems;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * evgeny, 8/13/12
 */
public class TMNamesValidator implements NamesValidator {

	private static final Set<String> keywords = new HashSet<String>(Arrays.asList("new", "separator", "true", "false"));

	public boolean isKeyword(String name, Project project) {
		return keywords.contains(name);
	}

	public boolean isIdentifier(String name, Project project) {
		try {
			LapgLexer lapgLexer = new ValidatingLexer(name);
			return lapgLexer.next().lexem == Lexems.ID && lapgLexer.next().lexem == Lexems.eoi;
		} catch (Exception ignored) {
		}
		return false;
	}

	private static class ValidatingLexer extends LapgLexer {
		public ValidatingLexer(String name) throws java.io.IOException {
			super(new StringReader(name), new ErrorReporter() {
				@Override
				public void error(int start, int end, int line, String s) {
					throw new IllegalArgumentException();
				}
			});
		}

		@Override
		protected boolean createToken(LapgSymbol lapg_n, int lexemIndex) throws IOException {
			super.createToken(lapg_n, lexemIndex);
			/* include comments and spaces */
			return true;
		}
	}
}