package com.ted.fileService.logic;

import com.evil.corp.filesystem.IFileSystem;

@FunctionalInterface
public interface SearchDescriptor<E> {
    public boolean match(E entry);
}