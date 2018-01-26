/**
 * TODO
 */
package com.teddy.api.result;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Teddy.D.Share 2018年1月3日
 */
public class ApiMapFormatter implements IApiFormatter {

	private ObjectMapper om = new ObjectMapper();

	private String RESULT_OF_ERROR = "XOFE";
	private String RESULT_OF_NONE = "XOFN";

	public String formate(ApiCell apiCell) {
		// TODO Auto-generated method stub
		return Optional.ofNullable(apiCell).map(_cell -> {
			try {
				return om.writeValueAsString(apiCell);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return RESULT_OF_ERROR;
			}
		}).orElse(RESULT_OF_NONE);
	}
}
