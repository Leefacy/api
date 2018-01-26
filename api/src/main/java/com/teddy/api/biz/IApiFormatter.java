/**
 * TODO
 */
package com.teddy.api.biz;

import com.teddy.api.result.ApiCell;

/**
 * @author Teddy.D.Share 2018年1月3日
 */
public interface IApiFormatter<R> {

	R formate(ApiCell apiCell);
}
