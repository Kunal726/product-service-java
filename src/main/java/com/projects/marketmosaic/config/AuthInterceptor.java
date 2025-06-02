package com.projects.marketmosaic.config;

import com.projects.marketmosaic.clients.AuthServiceClient;
import com.projects.marketmosaic.common.dto.resp.TokenValidationRespDTO;
import feign.FeignException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	private final ObjectProvider<AuthServiceClient> authClientProvider;

	public AuthInterceptor(ObjectProvider<AuthServiceClient> authClientProvider) {
		this.authClientProvider = authClientProvider;
	}

	private static final Set<String> EXCLUDED_PATHS = Set.of("/health", "/public/info");

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String path = request.getRequestURI();

		if (EXCLUDED_PATHS.contains(path)) {
			return true; // Skip auth for excluded paths
		}

		AuthServiceClient authServiceClient = authClientProvider.getObject();

		String jwtToken = null;
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("JWT_SESSION".equals(cookie.getName())) {
					jwtToken = cookie.getValue();
					break;
				}
			}
		}

		if (jwtToken == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Missing JWT_SESSION cookie");
			return false;
		}

		TokenValidationRespDTO result = authServiceClient.validateSeller("JWT_SESSION=" + jwtToken);

		if (!result.isValid()) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Invalid session");
			return false;
		}

		request.setAttribute("userId", result.getUserId());
		return true;
	}

}
