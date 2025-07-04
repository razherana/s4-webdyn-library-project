package mg.razherana.library.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SessionConfig implements WebMvcConfigurer {

  @Bean
  public AuthInterceptor authInterceptor() {
    return new AuthInterceptor();
  }

  @Override
  public void addInterceptors(@NonNull InterceptorRegistry registry) {
    registry.addInterceptor(authInterceptor())
        .excludePathPatterns("/static/**") // Exclude static resources
        .excludePathPatterns("/auth/login") // Don't require auth for login
        .excludePathPatterns("/auth/logout") // Don't require auth for logout
        .addPathPatterns("/**"); // Require auth for all other API endpoints
  }
}
