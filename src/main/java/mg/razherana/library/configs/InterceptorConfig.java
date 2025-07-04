package mg.razherana.library.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

  @Bean
  AuthInterceptor authInterceptor() {
    return new AuthInterceptor();
  }

  @Bean
  AccessInterceptor accessInterceptor() {
    return new AccessInterceptor();
  }

  @Override
  public void addInterceptors(@NonNull InterceptorRegistry registry) {
    registry.addInterceptor(authInterceptor())
        .excludePathPatterns("/static/**") // Exclude static resources
        .excludePathPatterns("/auth/login") // Don't require auth for login
        .excludePathPatterns("/auth/logout") // Don't require auth for logout
        .addPathPatterns("/**"); // Require auth for all other API endpoints

    // registrer accessInterceptor
    registry.addInterceptor(accessInterceptor())
        .excludePathPatterns("/static/**") // Exclude static resources
        .excludePathPatterns("/auth/login") // Don't require access for login
        .excludePathPatterns("/auth/logout") // Don't require access for logout
        .addPathPatterns("/**"); // Require access for all other API endpoints
  }
}
