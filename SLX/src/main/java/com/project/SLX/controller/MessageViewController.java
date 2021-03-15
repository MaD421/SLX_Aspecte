package com.project.SLX.controller;

import com.project.SLX.model.User;
import com.project.SLX.service.CustomUserDetailsService;
import com.project.SLX.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("/")
public class MessageViewController {
    private final MessageService messageService;
    private final CustomUserDetailsService customUserDetailsService;
    public final static int messagesPerPage = 2;
    public final static int initialPage = 0;
    public final static int inboxMessagesPerPage = 1;

    @Autowired
    public MessageViewController(MessageService messageService, CustomUserDetailsService customUserDetailsService) {
        this.messageService = messageService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/user/{partnerId}/messages")
    public String viewWithUser(@PageableDefault(value = messagesPerPage, page = initialPage) Pageable page, Model model, @PathVariable Long partnerId, HttpServletResponse response) {
        User partner = customUserDetailsService.getUserById(partnerId).orElseThrow();
        User currentUser = customUserDetailsService.getCurrentUser();

        if (currentUser.getUserId().equals(partnerId)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            return "404";
        }

        model.addAttribute("partnerId", partner.getUserId());
        model.addAttribute("pageTitle", "Conversation with " + partner.getUsername());
        model.addAttribute("currentPage", page.getPageNumber());
        model.addAttribute("messagesPerPage", page.getPageSize());
        model.addAttribute("nrShowMessages", page.getPageSize() * (page.getPageNumber() + 1));
        model.addAttribute("cssConfig", "messages");

        return "message/conversation";
    }

    @GetMapping("/inbox")
    public String inbox(@PageableDefault(value = inboxMessagesPerPage, page = initialPage) Pageable page, Model model) {
        model.addAttribute("pageTitle", "Inbox");
        model.addAttribute("currentPage", page.getPageNumber());
        model.addAttribute("messagesPerPage", page.getPageSize());
        model.addAttribute("nrShowMessages", page.getPageSize() * (page.getPageNumber() + 1));
        model.addAttribute("cssConfig", "inbox");

        return "message/inbox";
    }
}
