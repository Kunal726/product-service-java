package com.projects.marketmosaic.clients;

import com.projects.marketmosaic.common.dto.resp.TokenValidationRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-gateway", url = "http://auth-service:8081")
public interface AuthServiceClient {

	@GetMapping("/auth/validate-seller")
	TokenValidationRespDTO validateSeller(@RequestHeader("Cookie") String token);

	@GetMapping("/auth/validate-admin")
	TokenValidationRespDTO validateAdmin(@RequestHeader("Cookie") String token);

	@GetMapping("/auth/validate-user")
	TokenValidationRespDTO validateUser(@RequestHeader("Cookie") String token);

}
