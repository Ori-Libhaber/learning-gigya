package com.ted.fileService.logic;

import com.evil.corp.filesystem.IFileSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

public class SingleFileLinerSearchAlgorithm implements SearchAlgorithmSingleResult<IFileSystem, Path, SearchDescriptor<Path>> {

    private static Logger logger = Logger.getLogger(SingleFileLinerSearchAlgorithm.class.getName());

    private String rootPath;

    public SingleFileLinerSearchAlgorithm(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public Optional<Path> findFirst(IFileSystem fileSystem, SearchDescriptor<Path> searchDescriptor) {
        try {
            return Files.list(fileSystem.getPath(rootPath))
                    .filter(path -> searchDescriptor.match(path))
                    .findFirst();
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        return Optional.empty();
    }
}
