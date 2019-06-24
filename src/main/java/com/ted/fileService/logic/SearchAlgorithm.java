package com.ted.fileService.logic;

import com.evil.corp.filesystem.IFileSystem;

import java.util.Optional;

public interface SearchAlgorithm<T extends IFileSystem, E, D extends SearchDescriptor<E>> {
    public Optional<E> search(T fileSystem, D searchDescription);
}
