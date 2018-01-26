/**
 * TODO
 */
package com.teddy.api.collector;

import java.io.File;
import java.util.function.Predicate;

/**
 * 业务逻辑过滤
 * 
 * @author Teddy.D.Share 2018年1月3日
 */
public class ClzFilterOfBiz implements Predicate<File> {

	public boolean test(File f) {
		// TODO Auto-generated method stub

		return f.exists() && f.getName().indexOf("Open") > 0;
	}
}
