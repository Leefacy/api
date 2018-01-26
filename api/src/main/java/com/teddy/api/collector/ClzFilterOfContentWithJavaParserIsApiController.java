/**
 * TODO
 */
package com.teddy.api.collector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.Modifier;

/**
 * @author Teddy.D.Share 2018年1月3日
 */
public class ClzFilterOfContentWithJavaParserIsApiController
		extends ClzFilterOfContentWithJavaParser {

	boolean testCompilationUnit(CompilationUnit compilationUnit) {

		// compilationUnit.
		return true;
	}

	public static void main(String[] args) throws FileNotFoundException {
		
		author("");
		
//		File file = new File("F://teddy/api/src/test/resources/SchoolOpenController.java");
//		CompilationUnit compilationUnit = JavaParser.parse(file);
//
//		// compilationUnit.findAll(PackageDeclaration.class)
//		// .forEach(_node -> System.out.println(_node.getNameAsString())); // 包名
//		//
//		// compilationUnit.findAll(FieldDeclaration.class).stream()
//		// .forEach(f -> System.out.println(f.toString())); // 变量
//
//		compilationUnit.findAll(MethodDeclaration.class).stream().forEach(f -> //
//		{
//			Type type = f.getType();
//
//			type.findAll(MethodDeclaration.class).forEach(m -> System.out.println(m.toString()));
//
//			System.out.println("methodName =" + f.getNameAsString());
//
//			f.getParameters().forEach(p -> System.out
//					.println("param =" + p.getNameAsString() + ":" + p.getType().asString()));
//
//			f.getComment().ifPresent(c -> //
//			System.out.println(c.getContent()));
//		}); // 方法
	}
	
	private static String author(String str){
		str = "* @author j.chen@91kge.com";
		String reg = "(?<=\\@author)\\s?[\\w\\.\\@]+";
		Matcher matcher =Pattern.compile(reg).matcher(str);
		while (matcher.find()) {
			System.out.println(matcher.group());
		}
		return "";
	}
	
}
