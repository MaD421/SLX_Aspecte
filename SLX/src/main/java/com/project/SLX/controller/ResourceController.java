package com.project.SLX.controller;

import com.project.SLX.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping(value = "/css/{cssName}.css")
    public ResponseEntity<String> getCSS(@PathVariable String cssName) {
        final HttpHeaders headers = new HttpHeaders();
        String cssFile = resourceService.getCSS(cssName);

        return new ResponseEntity<>(cssFile, headers, HttpStatus.OK);
    }
}
