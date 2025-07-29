package com.mohamf.hello_api;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 🔥 DAY 29: YOUR FIRST SPRING BOOT REST CONTROLLER
 *
 * Mission: Implement 4 endpoints to complete your first backend API
 *
 * After implementing these methods, test each endpoint:
 * 1. GET  /api/hello
 * 2. GET  /api/hello/{name}
 * 3. POST /api/hello
 * 4. GET  /api/status
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    /**
     * 🎯 ENDPOINT 1: Simple Hello World
     *
     * URL: GET /api/hello
     * Response: "Hello, Backend Developer!"
     *
     * Test with: curl http://localhost:8080/api/hello
     */
    @GetMapping("/hello")
    public String hello() {
        // TODO: Return "Hello, Backend Developer!"
        return "Hello, Backend Developer!";
    }

    /**
     * 🎯 ENDPOINT 2: Personalized Hello with Path Variable
     *
     * URL: GET /api/hello/{name}
     * Response: "Hello, {name}! Welcome to Spring Boot!"
     *
     * Test with: curl http://localhost:8080/api/hello/John
     */
    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name) {
        // TODO: Return personalized greeting using the name parameter
        return "Hello, " + name + "! Welcome to Spring Boot!";
    }

    /**
     * 🎯 ENDPOINT 3: Hello with JSON Request Body
     *
     * URL: POST /api/hello
     * Request Body: {"name": "Developer"}
     * Response: "Hello, {name}! You made a POST request!"
     *
     * Test with:
     * curl -X POST http://localhost:8080/api/hello \
     *   -H "Content-Type: application/json" \
     *   -d '{"name": "Developer"}'
     */
    @PostMapping("/hello")
    public String helloPost(@RequestBody Map<String, String> request) {
        // TODO: Extract "name" from request body and return personalized message
        String name = request.get("name");
        if (name == null || name.isEmpty()) {
            name = "Anonymous";
        }
        return "Hello, " + name + "! You made a POST request!";
    }

    /**
     * 🎯 ENDPOINT 4: API Status Information
     *
     * URL: GET /api/status
     * Response: JSON object with status information
     *
     * Test with: curl http://localhost:8080/api/status
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        // TODO: Return JSON with status, timestamp, developer name, and day
        Map<String, Object> status = new HashMap<>();
        status.put("status", "API is running!");
        status.put("timestamp", LocalDateTime.now());
        status.put("developer", "mohamf"); // Replace with your name
        status.put("day", 29);
        status.put("message", "Month 1 Complete - Building Real APIs Now!");
        return status;
    }
}

/*
🎯 YOUR MISSION CHECKLIST:

✅ Step 1: Environment Setup (COMPLETE!)
🔄 Step 2: Create this HelloController file
🔄 Step 3: Test all 4 endpoints
🔄 Step 4: Document and push to GitHub

🚀 AFTER CREATING THIS FILE:
1. Save the file in your project
2. Spring Boot will auto-reload (if you see "Completed restart" in logs)
3. Test each endpoint with curl or Postman
4. All 4 endpoints should work perfectly!

💡 DEBUGGING TIPS:
- If endpoints don't work, check the @RequestMapping("/api") path
- Make sure your controller is in the same package as HelloApiApplication
- Check console for any error messages
- Restart application if needed

🏆 SUCCESS CRITERIA:
All 4 endpoints return expected responses when tested!
*/
