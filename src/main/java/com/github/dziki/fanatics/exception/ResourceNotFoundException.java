package com.github.dziki.fanatics.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resource;
    private final Long id;

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found: " + id);
        this.resource = resource;
        this.id = id;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.resource = "Resource";
        this.id = null;
    }
}
