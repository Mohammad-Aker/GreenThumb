package com.GreenThumb.GT.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/GreenThumb/api/demo")
@PreAuthorize("hasAuthority('ADMIN')")
public class DemoController {

    @GetMapping("/democ")
   // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hellooooooo");
    }
}
