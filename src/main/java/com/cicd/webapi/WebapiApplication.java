package com.cicd.webapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class WebapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebapiApplication.class, args);
	}

}

@RestController
class HelloController {

    @Value("${app.instance:BLUE}")
    private String instance;

    @Value("${server.port:8080}")
    private String port;

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String hello() {
        String badgeColor = "BLUE".equalsIgnoreCase(instance) ? "#3b82f6" : "#22c55e";
        return "<!DOCTYPE html>" +
            "<html lang=\"es\">" +
            "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Proyecto Final CI/CD</title>" +
                "<style>" +
                    "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #0f172a; color: #f8fafc; margin: 0; padding: 40px 20px; display: flex; justify-content: center; }" +
                    ".card { background: #1e293b; border-radius: 16px; padding: 32px; max-width: 650px; width: 100%; box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.5); border: 1px solid #334155; }" +
                    "h1 { margin-top: 0; color: #38bdf8; font-size: 26px; border-bottom: 2px solid #334155; padding-bottom: 12px; }" +
                    ".status-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 16px; margin: 24px 0; }" +
                    ".status-item { background: #0f172a; padding: 16px; border-radius: 10px; border: 1px solid #334155; }" +
                    ".status-item label { font-size: 12px; color: #94a3b8; text-transform: uppercase; display: block; margin-bottom: 4px; }" +
                    ".status-item span { font-size: 18px; font-weight: bold; }" +
                    ".badge { background-color: " + badgeColor + "; color: white; padding: 4px 10px; border-radius: 20px; font-size: 14px; display: inline-block; }" +
                    ".btn-group { display: flex; gap: 12px; flex-wrap: wrap; margin-top: 24px; }" +
                    ".btn { background: #38bdf8; color: #0f172a; text-decoration: none; padding: 10px 18px; font-weight: bold; border-radius: 8px; transition: 0.2s; }" +
                    ".btn:hover { background: #7dd3fc; }" +
                    ".btn-outline { background: transparent; color: #38bdf8; border: 1px solid #38bdf8; }" +
                    ".btn-outline:hover { background: rgba(56, 189, 248, 0.1); }" +
                "</style>" +
            "</head>" +
            "<body>" +
                "<div class=\"card\">" +
                    "<h1>🚀 Proyecto Final — CI/CD & Blue-Green</h1>" +
                    "<p style=\"color: #cbd5e1;\">Aplicación Spring Boot desplegada en la nube de <strong>AWS EC2</strong> con <strong>Nginx</strong> y <strong>GitHub Actions</strong>.</p>" +
                    "<div class=\"status-grid\">" +
                        "<div class=\"status-item\">" +
                            "<label>Estado del Servidor</label>" +
                            "<span style=\"color: #4ade80;\">🟢 ONLINE (200 OK)</span>" +
                        "</div>" +
                        "<div class=\"status-item\">" +
                            "<label>Instancia Activa</label>" +
                            "<span class=\"badge\">" + instance + " (" + port + ")</span>" +
                        "</div>" +
                        "<div class=\"status-item\">" +
                            "<label>Fecha Servidor</label>" +
                            "<span>" + java.time.LocalDate.now() + "</span>" +
                        "</div>" +
                    "</div>" +
                    "<div class=\"btn-group\">" +
                        "<a href=\"/calculator\" class=\"btn\">🧮 Abrir Calculadora Visual</a>" +
                        "<a href=\"/api/instance\" class=\"btn btn-outline\" target=\"_blank\">🔍 GET /api/instance</a>" +
                        "<a href=\"/health\" class=\"btn btn-outline\" target=\"_blank\">💚 GET /health</a>" +
                    "</div>" +
                "</div>" +
            "</body>" +
            "</html>";
    }
}

@RestController
class HealthController {
    @GetMapping("/health")
    public String health() {
        return "Server Healthy!";
    }
}

@RestController
class DateController {
    @GetMapping("/date")
    public String date() {
        return "Current Server Date: " + java.time.LocalDate.now();
    }
}