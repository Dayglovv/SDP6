package org.example;

/**
 * Visitor of the file system. Second step of Double Dispatch:
 * the overload is chosen statically by the type of {@code this} passed from accept().
 */
public interface Visitor {

    void visit(FileNode file);

    void visit(DirectoryNode directory);
}
