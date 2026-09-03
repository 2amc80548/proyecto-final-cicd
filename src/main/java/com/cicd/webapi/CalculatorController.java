package com.cicd.webapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    private final Calculator calculator = new Calculator();

    @GetMapping(value = "/calculator", produces = MediaType.TEXT_HTML_VALUE)
    public String calculatorUI() {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Calculadora Visual REST</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #0f172a; color: #f8fafc; margin: 0; padding: 40px 20px; display: flex; justify-content: center; }
                    .card { background: #1e293b; border-radius: 16px; padding: 32px; max-width: 480px; width: 100%; box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.5); border: 1px solid #334155; }
                    h1 { margin-top: 0; color: #38bdf8; font-size: 24px; border-bottom: 2px solid #334155; padding-bottom: 12px; }
                    .input-group { margin-bottom: 16px; }
                    label { display: block; margin-bottom: 6px; color: #94a3b8; font-size: 14px; }
                    input { width: 100%; box-sizing: border-box; background: #0f172a; border: 1px solid #334155; color: white; padding: 12px; border-radius: 8px; font-size: 16px; }
                    .grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 8px; margin: 20px 0; }
                    button { background: #38bdf8; color: #0f172a; border: none; padding: 12px; border-radius: 8px; font-size: 18px; font-weight: bold; cursor: pointer; }
                    button:hover { background: #7dd3fc; }
                    .result-box { background: #0f172a; border: 1px solid #334155; padding: 16px; border-radius: 8px; text-align: center; margin-top: 20px; font-size: 20px; color: #4ade80; }
                    .back-btn { display: inline-block; margin-top: 16px; color: #94a3b8; text-decoration: none; font-size: 14px; }
                    .back-btn:hover { color: #38bdf8; }
                </style>
            </head>
            <body>
                <div class="card">
                    <h1>🧮 Calculadora REST</h1>
                    <div class="input-group">
                        <label>Número A</label>
                        <input type="number" id="numA" value="10">
                    </div>
                    <div class="input-group">
                        <label>Número B</label>
                        <input type="number" id="numB" value="5">
                    </div>
                    <div class="grid">
                        <button onclick="calc('add')">+</button>
                        <button onclick="calc('subtract')">-</button>
                        <button onclick="calc('multiply')">×</button>
                        <button onclick="calc('divide')">÷</button>
                        <button onclick="calc('factorial')">!</button>
                    </div>
                    <div class="result-box" id="result">Resultado: 15</div>
                    <a href="/" class="back-btn">⬅ Volver al Dashboard</a>
                </div>
                <script>
                    async function calc(op) {
                        const a = document.getElementById('numA').value;
                        const b = document.getElementById('numB').value;
                        let url = '/api/calculator/' + op + '?a=' + a + '&b=' + b;
                        if (op === 'factorial') url = '/api/calculator/factorial?n=' + a;
                        const res = await fetch(url);
                        const data = await res.json();
                        if (res.ok) {
                            document.getElementById('result').innerText = 'Resultado: ' + data.result;
                        } else {
                            document.getElementById('result').innerText = 'Error: ' + data.error;
                        }
                    }
                </script>
            </body>
            </html>
            """;
    }

    @GetMapping("/add")
    public Map<String, Object> add(@RequestParam int a, @RequestParam int b) {
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "add");
        response.put("a", a);
        response.put("b", b);
        response.put("result", calculator.add(a, b));
        return response;
    }

    @GetMapping("/subtract")
    public Map<String, Object> subtract(@RequestParam int a, @RequestParam int b) {
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "subtract");
        response.put("a", a);
        response.put("b", b);
        response.put("result", calculator.subtract(a, b));
        return response;
    }

    @GetMapping("/multiply")
    public Map<String, Object> multiply(@RequestParam int a, @RequestParam int b) {
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "multiply");
        response.put("a", a);
        response.put("b", b);
        response.put("result", calculator.multiply(a, b));
        return response;
    }

    @GetMapping("/divide")
    public ResponseEntity<?> divide(@RequestParam double a, @RequestParam double b) {
        if (b == 0) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Denominator cannot be zero");
            return ResponseEntity.badRequest().body(error);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "divide");
        response.put("a", a);
        response.put("b", b);
        response.put("result", calculator.divide(a, b));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/factorial")
    public ResponseEntity<?> factorial(@RequestParam int n) {
        if (n < 0) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Negative numbers are not allowed");
            return ResponseEntity.badRequest().body(error);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "factorial");
        response.put("n", n);
        response.put("result", calculator.factorial(n));
        return ResponseEntity.ok(response);
    }
}
