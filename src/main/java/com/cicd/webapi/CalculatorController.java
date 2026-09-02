package com.cicd.webapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    private final Calculator calculator = new Calculator();

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
