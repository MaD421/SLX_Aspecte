package com.project.SLX.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class WebRequestService {

    public String getClientIpFromRequest(HttpServletRequest request) {
        String ipAddress = null;
        if (request != null) {
            ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
        }
        return ipAddress;
    }

}
