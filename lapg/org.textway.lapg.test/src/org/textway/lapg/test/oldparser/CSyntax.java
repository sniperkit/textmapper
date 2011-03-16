/**
 * Copyright 2002-2010 Evgeny Gryaznov
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
package org.textway.lapg.test.oldparser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.textway.lapg.api.*;

public class CSyntax implements Grammar {

	public static final String EOI = "eoi";
	public static final String INPUT = "input";
	public static final String ERROR = "error";
	public static final String OPTSUFFIX = "opt";

	private List<String> errors;

	private final CSymbol[] symbols;
	private InputRef[] inputs;
	private final CRule[] rules;
	private final CPrio[] prios;
	private final Map<String, Object> options;
	private final CLexem[] lexems;
	private final String templates;

	private final int myError;
	private final int myTerms;

	public CSyntax(List<CSymbol> symbols, List<CRule> rules, List<CPrio> prios, List<CInputDef> inputDefs, Map<String, String> options,
			List<CLexem> lexems, String templates) {
		this.symbols = symbols.toArray(new CSymbol[symbols.size()]);
		this.rules = rules.toArray(new CRule[rules.size()]);
		this.prios = prios.toArray(new CPrio[prios.size()]);
		this.lexems = lexems.toArray(new CLexem[lexems.size()]);
		this.options = new HashMap<String,Object>(options);
		this.templates = templates;
		sortSymbols();
		enumerateAll();
		this.inputs = extractInputs(inputDefs);
		if(this.inputs == null) {
			int inputIndex = findSymbol(INPUT);
			this.inputs = inputIndex >= 0 ? new InputRef[] { new InputRefImpl(this.symbols[inputIndex]) } : new InputRef[0];
		}

		myError = findSymbol(ERROR);
		int i = 0;
		for (; i < this.symbols.length && this.symbols[i].isTerm(); i++) {
			;
		}
		myTerms = i;
	}

	private static InputRef[] extractInputs(List<CInputDef> inputDefs) {
		InputRef[] result = null;

		if(inputDefs.size() > 0) {
			int size = 0;
			for(CInputDef idef : inputDefs) {
				size += idef.getSymbols().length;
			}
			if(size > 0) {
				result = new InputRef[size];
				int pos = 0;
				for(CInputDef idef : inputDefs) {
					CSymbol[] syms = idef.getSymbols();
					for (CSymbol sym : syms) {
						result[pos++] = new InputRefImpl(sym);
					}
				}
				assert pos == result.length;
			}
		}
		return result;
	}

	private static class InputRefImpl implements InputRef {

		private Symbol sym;

		private InputRefImpl(Symbol sym) {
			this.sym = sym;
		}

		public Symbol getTarget() {
			return sym;
		}

		public boolean hasEoi() {
			return true;
		}

		public String getResourceName() {
			return null;
		}

		public int getOffset() {
			return 0;
		}

		public int getEndOffset() {
			return 0;
		}

		public int getLine() {
			return 0;
		}

		public String getText() {
			return null;
		}
	}

	private int findSymbol(String name) {
		for (int i = 0; i < symbols.length; i++) {
			if (symbols[i].getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Inplace sort of symbols. [eoi term] [other terms] [all non-terminals]
	 */
	private void sortSymbols() {
		int first = 0, end = symbols.length - 1;

		while (first < end) {
			while (symbols[first].isTerm() && first < end) {
				first++;
			}
			while (!symbols[end].isTerm() && first < end) {
				end--;
			}
			if (first < end) {
				CSymbol ex = symbols[end];
				symbols[end] = symbols[first];
				symbols[first] = ex;
			}
		}
		if (symbols.length > 0 && !symbols[0].getName().equals(EOI)) {
			for (int i = 1; i < symbols.length; i++) {
				if (symbols[i].getName().equals(EOI)) {
					CSymbol ex = symbols[i];
					symbols[i] = symbols[0];
					symbols[0] = ex;
					break;
				}
			}
		}
	}

	private void enumerateAll() {
		for (int i = 0; i < symbols.length; i++) {
			symbols[i].index = i;
		}
		for (int i = 0; i < rules.length; i++) {
			rules[i].index = i;
		}
	}

	public CSyntax(List<String> errors) {
		this.errors = errors;
		lexems = null;
		options = null;
		prios = null;
		rules = null;
		symbols = null;
		templates = null;
		myTerms = 0;
		myError = -1;
		inputs = null;
	}

	public boolean hasErrors() {
		return errors != null;
	}

	public CSymbol[] getSymbols() {
		return symbols;
	}

	public CRule[] getRules() {
		return rules;
	}

	public CPrio[] getPriorities() {
		return prios;
	}

	public Map<String, Object> getOptions() {
		return options;
	}

	public List<String> getErrors() {
		return errors;
	}

	public Symbol getEoi() {
		return symbols[0];

	}
	public Symbol getError() {
		return myError >= 0 ? symbols[myError] : null;
	}

	public InputRef[] getInput() {
		return inputs;
	}

	public int getTerminals() {
		return myTerms;
	}

	public Lexem[] getLexems() {
		return lexems;
	}

	public SourceElement getTemplates() {
		return templates == null ? null : new SourceElement() {
			public String getText() {
				return templates;
			}

			public String getResourceName() {
				return null;
			}

			public int getOffset() {
				return 0;
			}

			public int getEndOffset() {
				return templates.length();
			}

			public int getLine() {
				return 1;
			}
		};
	}

	public boolean hasActions() {
		for (CRule r : rules) {
			if (r.getAction() != null) {
				return true;
			}
		}
		return false;
	}

	public boolean hasLexemActions() {
		for (CLexem lexem : lexems) {
			if (lexem.getAction() != null) {
				return true;
			}
		}
		return false;
	}

	public String getLocation() {
		return null;
	}
}
