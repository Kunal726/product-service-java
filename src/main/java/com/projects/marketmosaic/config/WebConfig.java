package com.projects.marketmosaic.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final AuthInterceptor authInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
				.addPathPatterns(
						"/seller/**",
						"/products/**",
						"/categories/add-category",
						"/categories/update-category",
						"/categories/delete-category",
						"/tags/add-tags",
						"/tags/update-tag",
						"/tags/delete-tag"
				)
				.excludePathPatterns(
						"/products/suggestions",
						"/categories",
						"tags"
				);
	}

}
