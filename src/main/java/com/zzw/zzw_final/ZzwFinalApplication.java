package com.zzw.zzw_final;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ZzwFinalApplication {
	// 자동배포 TEST 2233
	public static void main(String[] args) {
		SpringApplication.run(ZzwFinalApplication.class, args);
	}
}
