package com.project.SLX.model.exception;

public class BookmarkFoundException extends RuntimeException {

    public BookmarkFoundException() {
        super("Listing is already bookmarked!");
    }

}
