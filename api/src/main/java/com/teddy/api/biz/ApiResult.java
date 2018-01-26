/**
 * TODO
 */
package com.teddy.api.biz;

import com.teddy.api.result.ApiCell;

/**
 * @author Teddy.D.Share 2018年1月3日
 */
public class ApiResult<R> {

	private ApiCell apiCell; // api

	private IApiFormatter<R> apiFormatter; // 格式化

	public ApiCell getApiCell() {
		return apiCell;
	}

	public void setApiCell(ApiCell apiCell) {
		this.apiCell = apiCell;
	}

	public IApiFormatter<R> getApiFormatter() {
		return apiFormatter;
	}

	public void setApiFormatter(IApiFormatter<R> apiFormatter) {
		this.apiFormatter = apiFormatter;
	}

	/**
	 * 
	 */
	public ApiResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param apiCell
	 * @param apiFormatter
	 */
	public ApiResult(ApiCell apiCell, IApiFormatter<R> apiFormatter) {
		super();
		this.apiCell = apiCell;
		this.apiFormatter = apiFormatter;
	}

	public R generate() {
		return apiFormatter.formate(apiCell);
	}
}
