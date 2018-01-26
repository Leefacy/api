/**
 * 
 */
package com.teddy.api.biz;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Teddy.D.Share
 */
public class Api<T, R> {

	private ApiCollector<T, List<T>> apiCollector; // Api收集器

	// private ApiResult<R> apiResult; // Api文档生成
	//
	// private ApiRequest apiRequest; // Api 模拟请求

	public Api(ApiCollector<T, List<T>> apiCollector, IApiFormatter<R> apiFormatter, ApiRequest apiRequest) {
		super();
		this.apiCollector = apiCollector;
	}

	public R result() {

		List<T> list = this.apiCollector.doCollector(Collectors.toList());
		return null;
	}
}
