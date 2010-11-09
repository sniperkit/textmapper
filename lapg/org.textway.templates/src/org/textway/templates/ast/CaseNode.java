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
package org.textway.templates.ast;

import java.util.ArrayList;

import org.textway.templates.ast.AstTree.TextSource;

public class CaseNode extends CompoundNode {
	ExpressionNode caseExpr;

	public CaseNode(ExpressionNode caseExpr, TextSource source, int offset, int endoffset) {
		super(source, offset, endoffset);
		this.caseExpr = caseExpr;
	}

	public ExpressionNode getExpression() {
		return caseExpr;
	}

	public void addInstruction(Node node) {
		if( instructions == null ) {
			instructions = new ArrayList<Node>();
		}
		instructions.add(node);
	}

	public static void add(ArrayList<CaseNode> cases, Node instruction ) {
		CaseNode node = cases.get(cases.size()-1);
		node.addInstruction(instruction);
	}
}