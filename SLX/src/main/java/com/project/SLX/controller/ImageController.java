package com.project.SLX.controller;

import com.project.SLX.model.Image;
import com.project.SLX.model.User;
import com.project.SLX.service.CustomUserDetailsService;
import com.project.SLX.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/image")
public class ImageController {
    private final ImageService imageService;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public ImageController(ImageService imageService, CustomUserDetailsService customUserDetailsService) {
        this.imageService = imageService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping(value = "/{imageId}.{extension}")
    public ResponseEntity<byte[]> get(@PathVariable Long imageId, @PathVariable String extension) {
        final HttpHeaders headers = new HttpHeaders();
        byte[] imageData;
        String imageExtension;

        imageData = imageService.getData(imageId);
        imageExtension = imageService.getExtension(imageId);

        if (imageExtension.equals("jpg")) {
            headers.setContentType(MediaType.IMAGE_JPEG);
        } else if (imageExtension.equals("png")) {
            headers.setContentType(MediaType.IMAGE_PNG);
        }

        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{imageId}", produces = "text/plain")
    @ResponseBody
    public String delete(@PathVariable Long imageId) {
        Image image;

        try {
            image = imageService.getById(imageId);
        } catch (Exception e) {
            return "error";
        }

        User user = customUserDetailsService.getCurrentUser();
        User imageUserOwner = image
                .getOwner()
                .getOwner();

        if (!imageUserOwner.equals(user)) {
            log.warn(user.getUsername() + " attempted to delete an image that isn't his/her!");
            return "error";
        }

        try {
            imageService.deleteById(imageId);
        } catch (Exception e) {
            return "error";
        }

        return "success";
    }
}
