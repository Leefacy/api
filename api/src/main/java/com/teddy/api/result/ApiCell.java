/**
 * 
 */
package com.teddy.api.result;

import java.util.List;

/**
 * Api单元
 * 
 * @author Teddy.D.Share
 */
public class ApiCell {

	private String func; // API分类

	private String name; // API名称
	private String author; // API作者
	private String date; // API时间
	private HttpMethod method; // API请求方式
	private String url; // API url
	private List<ParamInfo> params; // API参数

	private List<String> result; // 返回列表

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<ParamInfo> getParams() {
		return params;
	}

	public void setParams(List<ParamInfo> params) {
		this.params = params;
	}

	public List<String> getResult() {
		return result;
	}

	public void setResult(List<String> result) {
		this.result = result;
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "ApiCell [func=" + func + ", name=" + name + ", author=" + author + ", date=" + date + ", method="
				+ method + ", url=" + url + ", params=" + params + ", result=" + result + "]";
	}

}
