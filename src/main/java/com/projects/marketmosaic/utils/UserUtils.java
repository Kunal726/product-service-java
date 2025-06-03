package com.projects.marketmosaic.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {

	public Long getUserId(HttpServletRequest request) {
		return (Long) request.getAttribute("userId");
	}

	public String getRole(HttpServletRequest request) {
		return (String) request.getAttribute("role");
	}

}
