/**
 * Copyright 2002-2011 Evgeny Gryaznov
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
package org.textway.templates.types.ast;

import java.util.List;
import org.textway.templates.types.TypesTree.TextSource;

public class AstTypeDeclaration extends AstNode {

	private String name;
	private List<List<String>> _extends;
	private List<IAstMemberDeclaration> memberDeclarationsopt;

	public AstTypeDeclaration(String name, List<List<String>> _extends, List<IAstMemberDeclaration> memberDeclarationsopt, TextSource input, int start, int end) {
		super(input, start, end);
		this.name = name;
		this._extends = _extends;
		this.memberDeclarationsopt = memberDeclarationsopt;
	}

	public String getName() {
		return name;
	}
	public List<List<String>> getExtends() {
		return _extends;
	}
	public List<IAstMemberDeclaration> getMemberDeclarationsopt() {
		return memberDeclarationsopt;
	}
	public void accept(AstVisitor v) {
		if (!v.visit(this)) {
			return;
		}

		// TODO for name
		// TODO for _extends
		if (memberDeclarationsopt != null) {
			for (IAstMemberDeclaration it : memberDeclarationsopt) {
				it.accept(v);
			}
		}
	}
}
