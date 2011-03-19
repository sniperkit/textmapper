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
package org.textway.lapg.test.cases;

import junit.framework.Assert;
import org.textway.lapg.api.Grammar;
import org.textway.lapg.common.FileUtil;
import org.textway.lapg.gen.SyntaxUtil;
import org.textway.lapg.lalr.Builder;
import org.textway.lapg.lalr.ParserTables;
import org.textway.lapg.lex.LexerTables;
import org.textway.lapg.lex.LexicalBuilder;
import org.textway.lapg.parser.LapgTree.TextSource;
import org.textway.lapg.test.TestStatus;
import org.textway.templates.api.SourceElement;
import org.textway.templates.api.TemplatesStatus;
import org.textway.templates.storage.ClassResourceLoader;
import org.textway.templates.storage.ResourceRegistry;
import org.textway.templates.types.TypesRegistry;

/**
 * Gryaznov Evgeny, 3/17/11
 */
public class InterpretedTest extends LapgTestCase {

	private TypesRegistry createDefaultTypesRegistry() {
		ResourceRegistry resources = new ResourceRegistry(
				new ClassResourceLoader(getClass().getClassLoader(), "org/textway/lapg/gen/templates", "utf8"));
		return new TypesRegistry(resources, new TemplatesStatus() {
			public void report(int kind, String message, SourceElement... anchors) {
				Assert.fail(message);
			}
		});
	}

	public void testMultiInputStates() {
		String contents = FileUtil.getFileContents(openStream("syntaxmultiinput", TESTCONTAINER), FileUtil.DEFAULT_ENCODING);
		Grammar g = SyntaxUtil.parseSyntax(new TextSource("syntaxmultiinput", contents.toCharArray(), 1), new TestStatus(), createDefaultTypesRegistry());
		Assert.assertNotNull(g);

		LexerTables l = LexicalBuilder.compile(g.getLexems(), new TestStatus());
		ParserTables r = Builder.compile(g, new TestStatus());

		//InterpretedTree<Object>
	}


}