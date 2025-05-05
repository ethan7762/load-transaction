package com.example.demo.configuration.auth;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.model.common.ErrorResponse;
import com.example.demo.model.common.ObjectResponse;
import com.example.demo.service.auth.CustomUserDetailsService;
import com.example.demo.service.auth.JwtTokenService;
import com.example.demo.util.JsonUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenAuthFilter extends OncePerRequestFilter {
	
	private final static Logger logger = LoggerFactory.getLogger(JwtTokenAuthFilter.class);
	
	@Autowired
	private JwtTokenService jwtTokenService;
	
    @Autowired
    private CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			
            String jwt = parseJwt(request);
            
			if (jwt != null) {
				
				ObjectResponse<Void> validateObjResponse = jwtTokenService.validateJwtToken(jwt);
	            
	            if (!validateObjResponse.isSuccess()) {
					String msg = "Unauthorized access";
					
					if(!ObjectUtils.isEmpty(validateObjResponse.getMessage())) {
						msg += " - " + validateObjResponse.getMessage();
					}
		        	response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write(JsonUtil.toJson(new ErrorResponse(msg)));
					return;
				}
				
	            String username = jwtTokenService.getUsernameFromToken(jwt);
	            
	            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
	            UsernamePasswordAuthenticationToken authentication =
	                    new UsernamePasswordAuthenticationToken(
	                            userDetails,
	                            null,
	                            userDetails.getAuthorities()
	                    );
	            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	            SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			
			filterChain.doFilter(request, response);
			
        } catch (Exception e) {
        	logger.error("JwtTokenAuthFilter - set user authentication error", e);
        	ErrorResponse errorResponse = new ErrorResponse("Authentication failed");
        	response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(JsonUtil.toJson(errorResponse));
        }
		
	}
	
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

}
