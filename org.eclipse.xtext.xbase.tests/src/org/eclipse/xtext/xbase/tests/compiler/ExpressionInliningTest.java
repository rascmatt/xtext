/*******************************************************************************
 * Copyright (c) 2010, 2017 itemis AG (http://www.itemis.eu) and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.xtext.xbase.tests.compiler;

import org.eclipse.xtext.util.JavaVersion;
import org.eclipse.xtext.xbase.compiler.GeneratorConfig;
import org.eclipse.xtext.xbase.compiler.IGeneratorConfigProvider;
import org.eclipse.xtext.xbase.compiler.output.FakeTreeAppendable;
import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * @author Matthias Raschhofer
 */
public class ExpressionInliningTest extends AbstractOutputComparingCompilerTests {
	
	@Override
	protected FakeTreeAppendable createAppendable() {
		return new FakeTreeAppendable();
	}
	
	@Inject
	private IGeneratorConfigProvider generatorConfigProvider;
	
	@Test
	public void testExpression1() throws Exception {
		assertCompilesTo(
				"return (3 + 4);",
				"{3+4}");
	}
	
	@Test
	public void testExpression2() throws Exception {
		assertCompilesTo(""
				+ "final int a = (3 + 4);\n"
				+ "return a;",
				"{val a = 3+4; return a}");
	}
	
	@Test
	public void testExpression3() throws Exception {
		assertCompilesTo(""
				+ "final int a = ((3 + 4) + 5);\n"
				+ "return a;",
				"{val a = 3+4+5; return a}");
	}
	
	@Test
	public void testExpression4() throws Exception {
		assertCompilesTo(""
				+ "final String a = (Integer.valueOf((3 + 4)) + \"_test\");\n"
				+ "return a;",
				"{val a = 3+4+'_test'; return a}");
	}
	
	@Test
	public void testExpression5() throws Exception {
		assertCompilesTo(""
				+ "String _firstUpper = org.eclipse.xtext.xbase.lib.StringExtensions.toFirstUpper(\"test_\");\n"
				+ "final String a = (_firstUpper + Integer.valueOf((3 + 4)));\n"
				+ "return a;",
				"{val a = 'test_'.toFirstUpper + (3 + 4); return a}");
	}
	
	@Test
	public void testExpression6() throws Exception {
		assertCompilesTo(""
				+ "if ((3 == 4)) {\n"
				+ "  final String x = \"test-then\";\n"
				+ "} else {\n"
				+ "  final String x_1 = \"test-else\";\n"
				+ "}",
				"{ if (3 === 4) { val x = 'test-then' } else { val x = 'test-else'} }");
	}
	
	@Test
	public void testExpression7() throws Exception {
		assertCompilesTo(""
				+ "if ((3 == 4)) {\n"
				+ "  final String x = \"test-then\";\n"
				+ "} else {\n"
				+ "  final String x_1 = \"test-else\";\n"
				+ "}",
				"{ if (3 == 4) { val x = 'test-then' } else { val x = 'test-else'} }");
	}
	
	@Test
	public void testExpression8() throws Exception {
		assertCompilesTo(""
				+ "boolean _equals = com.google.common.base.Objects.equal(Integer.valueOf(3), \"123\");\n"
				+ "if (_equals) {\n"
				+ "  final String x = \"test-then\";\n"
				+ "} else {\n"
				+ "  final String x_1 = \"test-else\";\n"
				+ "}",
				"{ if (3 == '123') { val x = 'test-then' } else { val x = 'test-else'} }");
	}
	
	@Test
	public void testExpression9() throws Exception {
		assertCompilesTo(""
				+ "if ((Integer.valueOf(3) == \"123\")) {\n"
				+ "  final String x = \"test-then\";\n"
				+ "} else {\n"
				+ "  final String x_1 = \"test-else\";\n"
				+ "}",
				"{ if (3 === '123') { val x = 'test-then' } else { val x = 'test-else'} }");
	}
	
	@Test
	public void testExpression10() throws Exception {
		assertCompilesTo(""
				+ "Integer _valueOf = Integer.valueOf(10);\n"
				+ "boolean _equals = ((_valueOf).intValue() == 10);\n"
				+ "if (_equals) {\n"
				+ "  final String x = \"test-then\";\n"
				+ "} else {\n"
				+ "  final String x_1 = \"test-else\";\n"
				+ "}",
				"{ if (Integer.valueOf(10) == 10) { val x = 'test-then' } else { val x = 'test-else'} }");
	}
	
	@Test
	public void testExpression11() throws Exception {
		assertCompilesTo(""
				+ "Integer _valueOf = Integer.valueOf(10);\n"
				+ "final boolean result = ((_valueOf).intValue() == 10);",
				"{ val result =  Integer.valueOf(10) == 10 }");
	}
	
	@Test
	public void testExpression12() throws Exception {
		assertCompilesTo(""
				+ "final boolean result = com.google.common.base.Objects.equal(\"10\", Integer.valueOf(10));",
				"{ val result =  '10' == 10 }");
	}
	
	@Test
	public void testExpression13() throws Exception {
		assertCompilesTo(""
				+ "String _firstUpper = org.eclipse.xtext.xbase.lib.StringExtensions.toFirstUpper(\"10\");\n"
				+ "final boolean result = com.google.common.base.Objects.equal(_firstUpper, Integer.valueOf(10));",
				"{ val result =  '10'.toFirstUpper == 10 }");
	}
	
	@Test
	public void testExpression14() throws Exception {
		assertCompilesTo(""
				+ "final String result = \"123\";",
				"{ val result =  {'123'} }");
	}
	
	@Test
	public void testExpression15() throws Exception {
		assertCompilesTo(""
				+ "String _xblockexpression = null;\n"
				+ "{\n"
				+ "  final int x = 3;\n"
				+ "  _xblockexpression = \"123\";\n"
				+ "}\n"
				+ "final String result = _xblockexpression;",
				"{ val result =  {val x = 3; '123'} }");
	}
	
	@Test
	public void testExpression16() throws Exception {
		assertCompilesTo(""
				+ "final String result = (\"123\" + \"456\");",
				"{ val result =  {'123'} + '456' }");
	}
	
	@Test
	public void testExpression17() throws Exception {
		assertCompilesTo(""
				+ "String _xblockexpression = null;\n"
				+ "{\n"
				+ "  final int x = 3;\n"
				+ "  _xblockexpression = \"123\";\n"
				+ "}\n"
				+ "final String result = (_xblockexpression + \"456\");",
				"{ val result =  {val x = 3; '123'} + '456' }");
	}
	
	@Test
	public void testExpression18() throws Exception {
		assertCompilesTo(""
				+ "final org.eclipse.xtext.xbase.lib.Functions.Function1<String, String> _function = new org.eclipse.xtext.xbase.lib.Functions.Function1<String, String>() {\n"
				+ "  public String apply(final String s) {\n"
				+ "    return s.toUpperCase();\n"
				+ "  }\n"
				+ "};\n"
				+ "final org.eclipse.xtext.xbase.lib.Functions.Function1<String, String> toUpperCaseFunction = _function;",
				"{ val toUpperCaseFunction = [ String s | s.toUpperCase ] }");
	}
	
	@Test
	public void testExpression19() throws Exception {
		assertCompilesTo(""
				+ "String _xifexpression = null;\n"
				+ "final org.eclipse.xtext.xbase.lib.Functions.Function1<String, String> _function = new org.eclipse.xtext.xbase.lib.Functions.Function1<String, String>() {\n"
				+ "  public String apply(final String s) {\n"
				+ "    return s.toUpperCase();\n"
				+ "  }\n"
				+ "};\n"
				+ "if ((_function != null)) {\n"
				+ "  _xifexpression = \"123\";\n"
				+ "} else {\n"
				+ "  _xifexpression = \"456\";\n"
				+ "}\n"
				+ "return _xifexpression;",
				"{ if ([ String s | s.toUpperCase ] !== null) '123' else '456' }");
	}
	
	@Test
	public void testExpression20() throws Exception {
		assertCompilesTo(""
				+ "int a = 7;\n"
				+ "int b = 3;\n"
				+ "a = (b = 1);\n"
				+ "return (a + b);",
				"{ var a = 7; var b = 3; a = b = 1; return a + b }");
	}
}
