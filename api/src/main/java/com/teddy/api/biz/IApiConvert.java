/**
 * 
 */
package com.teddy.api.biz;

import com.teddy.api.result.ApiCell;

/**
 * @author Teddy.D.Share
 */
public interface IApiConvert<T> {

	ApiCell convert(T in);
}
