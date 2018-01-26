/**
 * TODO
 */
package com.teddy.api.collector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 扫描器,扫描给定的文件夹,搜集符合规则的类
 * 
 * @author Teddy.D.Share 2018年1月3日
 */
public class ClzScanner {

	private List<Predicate<File>> filters = new ArrayList<>(); // 过滤条件集合

	/**
	 * 增加过滤条件
	 * 
	 * @param fileFilter
	 */
	public void addFileFilter(Predicate<File> fileFilter) {
		filters.add(fileFilter);
	}

	/**
	 * 清除所有过滤条件
	 */
	public void cleanFilters() {
		this.filters.clear();
	}

	/**
	 * 
	 */
	public ClzScanner() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 扫描某个文件夹，过滤fileFilter，并将结果交给collector返回.
	 * 
	 * @param dir
	 *            目录名称
	 * @param collector
	 *            结果集合
	 * @return
	 */
	public <T> T scan(File dir, Collector<File, ?, T> collector) {
		List<File> files = new ArrayList<>();
		collectFiles(dir, files);
		Predicate<File> predicate = f -> !filters.stream() //
				.filter(_filter -> !_filter.test(f)) //
				.findFirst() //
				.isPresent(); // 过滤所有的过滤条件
		return Arrays.stream(dir.listFiles()).filter(predicate).collect(collector);
	}

	/**
	 * dir下的所有文件集合
	 * 
	 * @param dir
	 * @param source
	 */
	private void collectFiles(File dir, List<File> files) {
		ClzFilterOfDirectory clzFilterOfDirectory = new ClzFilterOfDirectory();
		ClzFilterOfFile clzFilterOfFile = new ClzFilterOfFile();
		if (clzFilterOfDirectory.test(dir)) { // 文件夹
			Arrays.stream(dir.listFiles()).forEach(_f -> collectFiles(_f, files));
		} else if (clzFilterOfFile.test(dir)) {
			files.add(dir);
		} else {
			System.out.println("unknow file:" + dir.getName());
		}
	}

	public static void main(String[] args) {
		File file = new File(
				"F://data/webgame/server/kge_jchen-spring-api-master/spring-api/src/test/resources/");
		ClzScanner clzScanner = new ClzScanner();
		clzScanner.addFileFilter(new ClzFilterOfJava());
		clzScanner.addFileFilter(new ClzFilterOfBiz());
		Map<String, File> files = clzScanner.scan(file,
				Collectors.toMap(_f -> _f.getName(), _f -> _f));
		files.forEach((k, v) -> System.out.println(k));
	}
}
