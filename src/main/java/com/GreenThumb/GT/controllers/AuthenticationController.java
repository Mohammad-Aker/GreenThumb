package com.GreenThumb.GT.controllers;


import com.GreenThumb.GT.payload.authentication.AuthenticationPayload;
import com.GreenThumb.GT.payload.authentication.RegisterPayload;
import com.GreenThumb.GT.response.AuthenticationResponse;
import com.GreenThumb.GT.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/GreenThumb/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterPayload request){


        return ResponseEntity.ok(authenticationService.register(request));


    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationPayload request){

        return ResponseEntity.ok(authenticationService.authenticate(request));



    }
}
