/**
 * 
 */
package com.teddy.api.biz;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.teddy.api.result.ApiCell;

/**
 * api 收集器
 * 
 * @author Teddy.D.Share
 *
 */
public class ApiCollector<T, R extends Collection<ApiCell>> {

	private List<Predicate<?>> filters; // api收集过滤器
	private List<T> source; // api资源列表

	private IApiFormatter<R> apiFormatter; // api转换器

	public ApiCollector() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void addCollectorFilter(Predicate<?> predicate) {
		if (filters == null) {
			filters = new ArrayList<>();
		}
		filters.add(predicate);
	}

	/**
	 * 收集结果
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	public List<ApiCell> collect() throws FileNotFoundException {
		File file = new File("F://teddy/api/src/test/resources/SchoolOpenController.java");
		CompilationUnit compilationUnit = JavaParser.parse(file);

		ApiJavaDoc apiJavaDoc = new ApiJavaDoc();

		compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(_classOrInterfaceDeclaration -> {
			System.out.println(_classOrInterfaceDeclaration.getName());
			_classOrInterfaceDeclaration.getJavadoc().ifPresent(_javaDoc -> {
				_javaDoc.getBlockTags().stream().filter(_tag -> _tag.getType().equals(JavadocBlockTag.Type.AUTHOR))
						.findFirst().ifPresent(_tag -> apiJavaDoc.setAuthor(_tag.toText()));
				String description = _javaDoc.getDescription().toText();
				apiJavaDoc.setDescription(description.replace("#API", ""));
			});

		});
		System.out.println("ApiDoc =" + apiJavaDoc);
		return new ArrayList<>();
	}

	public static void main(String[] args) throws FileNotFoundException {
		ApiCollector<File, Collection<ApiCell>> apiCollector = new ApiCollector<>();
		apiCollector.collect();

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
}
