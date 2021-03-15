package com.project.SLX.controller;

import com.project.SLX.model.dto.CommentDTO;
import com.project.SLX.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;

@RestController
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/viewListing/{listingId}/comment")
    public HashMap<String, String> add(@PathVariable Long listingId, @Valid @RequestBody CommentDTO commentDTO, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        HashMap<String, String> result = new HashMap<>();
        result.put("status", "success");

        if (!commentService.add(listingId, commentDTO)) {
            result.put("status", "error");
            result.put("error", "There was an error with your request!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return result;
        }

        return result;
    }

    @PutMapping("/viewListing/{listingId}/comment/{id}")
    public HashMap<String, String> update(@PathVariable Long listingId, @PathVariable Long id, @Valid @RequestBody CommentDTO commentDTO, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        HashMap<String, String> result = new HashMap<>();
        result.put("status", "success");

        if (!commentService.update(id, commentDTO)) {
            result.put("status", "error");
            result.put("error", "There was an error with your request!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return result;
        }

        return result;
    }

    @DeleteMapping("/viewListing/{listingId}/comment/{id}")
    public HashMap<String, String> delete(@PathVariable Long listingId, @PathVariable Long id, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        HashMap<String, String> result = new HashMap<>();
        result.put("status", "success");

        if (!commentService.delete(id)) {
            result.put("status", "error");
            result.put("error", "There was an error with your request!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return result;
        }

        return result;
    }
}
