package app.wendo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Wendo API",
                version = "1.0",
                description = "API documentation for Wendo Application",
                contact = @Contact(
                        name = "Wendo Support",
                        email = "support@wendo.app",
                        url = "https://wendo.app"
                ),
                license = @License(
                        name = "Wendo License",
                        url = "https://wendo.app/license"
                )
        ),
        servers = {
                @Server(
                        url = "/",
                        description = "Default Server URL"
                )
        }
        // Removed the global security requirement to make docs accessible without authentication
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth token",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        // Create an OpenAPI instance without global security requirements
        return new OpenAPI()
            .components(new Components());
            // Security schemes are still defined but not required globally
    }
}
