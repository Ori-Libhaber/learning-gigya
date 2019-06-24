package com.ted.fileService.service;

import com.evil.corp.filesystem.IFileSystem;
import com.ted.fileService.logic.SearchAlgorithm;
import com.ted.fileService.logic.SearchDescriptor;

public interface FileSystemService<T extends IFileSystem, E, D extends SearchDescriptor<E>> {
    public void execute(D searchDescriptor);

    static abstract class SourceToTargetFileSystemService<T extends IFileSystem,E, D extends SearchDescriptor<E>> implements FileSystemService<T,E,D>{

        protected T sourceFileSystem;
        protected T targetFileSystem;
        protected SearchAlgorithm<T, E, SearchDescriptor<E>> searchAlgorithm;

        public SourceToTargetFileSystemService(T sourceFileSystem, T targetFileSystem, SearchAlgorithm<T, E, SearchDescriptor<E>> searchAlgorithm) {
            this.sourceFileSystem = sourceFileSystem;
            this.targetFileSystem = targetFileSystem;
            this.searchAlgorithm = searchAlgorithm;
        }

        protected abstract void doAction(E entry);

    }

}
