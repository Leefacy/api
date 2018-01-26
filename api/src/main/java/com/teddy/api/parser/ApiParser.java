/**
 * TODO
 */
package com.teddy.api.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.javadoc.Javadoc;
import com.teddy.api.result.ApiCell;

/**
 * api解析器
 * 
 * @author Teddy.D.Share 2018年1月3日
 * 
 */
public class ApiParser {

	public List<ApiCell> parse(File file) throws FileNotFoundException {

		CompilationUnit compilationUnit = JavaParser.parse(file);

		// author and func
		ApiJavaDoc apiJavaDoc = parseJavaDoc(compilationUnit);

		compilationUnit.findAll(MethodDeclaration.class).stream()
				.map(methodDeclaration -> parserMethod(methodDeclaration, apiJavaDoc)).collect(Collectors.toList());

		// compilationUnit.findAll(PackageDeclaration.class)
		// .forEach(_node -> System.out.println(_node.getNameAsString())); // 包名
		//
		// compilationUnit.findAll(FieldDeclaration.class).stream()
		// .forEach(f -> System.out.println(f.toString())); // 变量

		// compilationUnit.findAll(MethodDeclaration.class).stream().forEach(f
		// -> //
		// {
		// Type type = f.getType();
		//
		// type.findAll(MethodDeclaration.class).forEach(m ->
		// System.out.println(m.toString()));
		//
		// System.out.println("methodName =" + f.getNameAsString());
		//
		// f.getParameters().forEach(p -> System.out
		// .println("param =" + p.getNameAsString() + ":" +
		// p.getType().asString()));
		//
		// f.getComment().ifPresent(c -> //
		// System.out.println(c.getContent()));
		// }); // 方法

		return null;
	}

	/**
	 * 获取author和description
	 * 
	 * @param compilationUnit
	 * @return
	 */
	private ApiJavaDoc parseJavaDoc(CompilationUnit compilationUnit) {
		ApiJavaDoc apiJavaDoc = new ApiJavaDoc();
		compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(_classOrInterfaceDeclaration -> {
			_classOrInterfaceDeclaration.getJavadoc().ifPresent(_javaDoc -> {
				_javaDoc.getBlockTags().stream().filter(
						_tag -> _tag.getType().equals(com.github.javaparser.javadoc.JavadocBlockTag.Type.AUTHOR))
						.findFirst().ifPresent(_tag -> apiJavaDoc.setAuthor(_tag.toText()));
				String description = _javaDoc.getDescription().toText();
				apiJavaDoc.setDescription(description.replace("#API", ""));
			});

		});
		System.out.println("ApiDoc =" + apiJavaDoc);
		return apiJavaDoc;
	}

	private ApiCell parserMethod(MethodDeclaration methodDeclaration, ApiJavaDoc apiJavaDoc) {

		ApiCell apiCell = new ApiCell();
		apiCell.setAuthor(apiJavaDoc.getAuthor());
		apiCell.setFunc(apiJavaDoc.getDescription());
		NodeList<AnnotationExpr> anns = methodDeclaration.getAnnotations();

		anns.forEach(_ann -> {
			System.out.println(_ann.getNameAsString());
			_ann.ifNormalAnnotationExpr(_normalAnnotation -> {
				NodeList<MemberValuePair> m = _normalAnnotation.getPairs();
				System.out.println("_normalAnnotation:");
				for (MemberValuePair memberValuePair : m) {
					System.out.println(memberValuePair.getNameAsString());
					memberValuePair.getValue().ifStringLiteralExpr(_str -> System.out.println(_str.asString()));
				}
			});
			_ann.ifSingleMemberAnnotationExpr(_singleMemberAnnotation -> {
				System.out.println("_singleMemberAnnotation:");
				_singleMemberAnnotation.getMemberValue()
						.ifStringLiteralExpr(_str -> System.out.println(_str.asString()));
			});
		});
		return apiCell;
	}

	class ApiJavaDoc {
		String author;
		String description;
		String url;

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public ApiJavaDoc() {
			super();
			// TODO Auto-generated constructor stub
		}

		public ApiJavaDoc(String author, String description, String url) {
			super();
			this.author = author;
			this.description = description;
			this.url = url;
		}

		@Override
		public String toString() {
			return "ApiJavaDoc [author=" + author + ", description=" + description + ", url=" + url + "]";
		}
	}

	/**
	 * 包名
	 * 
	 * @param compilationUnit
	 * @return
	 */
	public List<String> packageName(CompilationUnit compilationUnit) {
		return compilationUnit.findAll(PackageDeclaration.class).stream()
				.map(packageDeclaration -> packageDeclaration.getNameAsString()).collect(Collectors.toList());
	}

	// not yet
	public String author(CompilationUnit compilationUnit) {

		compilationUnit.findAll(JavadocComment.class).stream().forEach(c -> //
		{
			System.out.println(c.toString());
			System.out.println(c.getContent());
		});
		return "";
	}

	public static void main(String[] args) throws FileNotFoundException {
		ApiParser apiParser = new ApiParser();
		File file = new File("F://teddy/api/src/test/resources/SchoolOpenController.java");
		apiParser.parse(file);

	}

	public static String getAuthorFromJavaDocComment(String str) {
		str = "#API开学积分测试文件";
		String reg = "(?<=#API)\\s?[\\w\\.\\@]+";
		Matcher matcher = Pattern.compile(reg).matcher(str);

		StringJoiner sJoiner = new StringJoiner("&");

		while (matcher.find()) {
			sJoiner.add(matcher.group());
		}
		return sJoiner.toString();
	}

	private static void parserDoc(ApiCell apiCell, String javaDocComment) {

		String[] lines = javaDocComment.split("\n");

		int index = 0;
		for (String string : lines) {
			System.out.println(index++ + "：" + string);
		}

		System.out.println(parserContent(Arrays.asList(lines), "\\@author"));
		System.out.println(parserContent(Arrays.asList(lines), "#API"));
	}

	private static String parserContent(List<String> strs, String content) {

		String reg = "(?<=" + content + ")\\s?[\\w\\.\\@]+";

		return strs.stream().map(s -> {
			Matcher matcher = Pattern.compile(reg).matcher(s);
			StringJoiner sJoiner = new StringJoiner("&");
			while (matcher.find()) {
				sJoiner.add(matcher.group());
			}
			return sJoiner.toString();
		}).collect(Collectors.joining());
	}
}
