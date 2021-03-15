package com.project.SLX.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class CustomErrorController {
    @RequestMapping(value = "/error")
    public String getErrorPage(Model model, HttpServletRequest request, HttpServletResponse response) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

                return "404";
            }
        }

        model.addAttribute("error", "There was an error with your request!");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        return "info";
    }
}
