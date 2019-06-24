package com.ted.fileService.logic;

import com.evil.corp.filesystem.IFileSystem;

import java.util.Optional;

public interface SearchAlgorithmSingleResult<T extends IFileSystem, E, D extends SearchDescriptor<E>> extends SearchAlgorithm<T,E,D> {
    public Optional<E> findFirst(T fileSystem, D searchDescriptor);
    @Override
    default public Optional<E> search(T fileSystem, D searchDescription){
        return findFirst(fileSystem, searchDescription);
    }
}
