/**
 * TODO
 */
package com.teddy.api.collector;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

/**
 * @author Teddy.D.Share 2018年1月3日
 */
public class ClzFilterOfContentWithJavaParser extends ClzFilterOfContent {

	public boolean test(File f) {
		// TODO Auto-generated method stub
		try {
			CompilationUnit compilationUnit = JavaParser.parse(f);
			return testCompilationUnit(compilationUnit);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 对javaparse的结果进行判断
	 * 
	 * @param compilationUnit
	 * @return
	 */
	boolean testCompilationUnit(CompilationUnit compilationUnit) {
		return true;
	}
}
