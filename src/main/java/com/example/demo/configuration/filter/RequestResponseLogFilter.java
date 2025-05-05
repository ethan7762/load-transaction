package com.example.demo.configuration.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.ServletRequestPathUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestResponseLogFilter extends OncePerRequestFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(RequestResponseLogFilter.class);
	
	@Autowired
	private RequestMappingHandlerMapping handlerMapping;
	
	@Override
	public void initFilterBean() throws ServletException {
	    // Adding Autowiring support in Java Filter
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
	    ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
	    
		filterChain.doFilter(wrappedRequest, wrappedResponse);
		
		try {
			
			HandlerMethod currentHandlerMethod = this.getCurrentHandlerMethod(wrappedRequest);

			byte[] requestBytes = wrappedRequest.getContentAsByteArray();
			String requestBody = this.getContentAsString(requestBytes, request.getCharacterEncoding());

			byte[] responseBytes = wrappedResponse.getContentAsByteArray();
			String responseBody = getContentAsString(responseBytes, response.getCharacterEncoding());	
			
			String requestParams = wrappedRequest.getParameterMap().entrySet().stream().map(e -> e.getKey() + "=" + request.getParameter(e.getKey())).collect(Collectors.joining(", "));
			String action = getRequestActionName(currentHandlerMethod);
			
			logger.info("\n"
					+ "Action          : {}\n"
					+ "Request URL     : {}\n"
					+ "Request Method  : {}\n"
					+ "Request Params  : {}\n"
					+ "Request Body    : {}\n"
					+ "Response Code   : {}\n"
					+ "Response Body   : {}\n",
					action,
					wrappedRequest.getRequestURI(),
					wrappedRequest.getMethod(),
					requestParams,
					requestBody,
					Integer.toString(wrappedResponse.getStatus()),
					responseBody);
			
		} catch(Exception e) {
			logger.error("RequestResponseLogFilter error", e);
		}finally {
			wrappedResponse.copyBodyToResponse();
		}
	}
	
	private String getContentAsString(byte[] buf, String charsetName) {
		if (buf == null || buf.length == 0)
			return "";
		try {
			return new String(buf, 0, buf.length, charsetName);
		} catch (UnsupportedEncodingException ex) {
			return "Unsupported Encoding";
		}
	}

	private HandlerMethod getCurrentHandlerMethod(HttpServletRequest request) {
		HandlerMethod currentHandlerMethod = null;
		
		try {
			
			if (!ServletRequestPathUtils.hasParsedRequestPath(request)) {
				ServletRequestPathUtils.parseAndCache(request);
			}
			
			List<HandlerMethod> handlerMethodList = new ArrayList<HandlerMethod>();
			
			HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
			
			if(ObjectUtils.isEmpty(handlerExecutionChain)) {
				// not found any handler -> 404
				return null;
			}
			
			Object handler = handlerExecutionChain.getHandler();
			
			if (handler instanceof HandlerMethod) {
				handlerMethodList.add((HandlerMethod) handler);
			}

			if(ObjectUtils.isEmpty(handlerMethodList)) {
				// not found any handler -> 404
				return null;
			}
			
			// always get top handlerMethod which the best pattern matched
			currentHandlerMethod = handlerMethodList.get(0);

		}catch(HttpRequestMethodNotSupportedException e) {
			currentHandlerMethod = null;
		} catch(Exception e) {
			logger.error("getCurrentHandlerMethod error", e);
			currentHandlerMethod = null;
		}
		
		return currentHandlerMethod;
	}
	
	private String getRequestActionName(HandlerMethod currentHandlerMethod) {
		String actionName = "";
		
		try {
			
			// didn't found any matching, return action name as empty string
			if(ObjectUtils.isEmpty(currentHandlerMethod)) {
				return actionName;
			}

			actionName = currentHandlerMethod.getMethod().getName();
		} catch (Exception e) {
			logger.error("getRequestActionName Error", e);
		}
		
		return actionName;
	}
}
