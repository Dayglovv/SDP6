package org.example;

/**
 * Counts files and directories (the root counts as a directory) and the maximum
 * directory depth: root = 0, its sub-directories = 1, and so on.
 */
public final class StatisticsVisitor extends TraversingVisitor {

    private int fileCount;
    private int directoryCount;
    private int maxDepth;
    private int currentDepth;

    public int getFileCount() {
        return fileCount;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    @Override
    protected void onStart() {
        fileCount = 0;
        directoryCount = 0;
        maxDepth = 0;
        currentDepth = -1;
    }

    @Override
    protected void onFile(FileNode file) {
        fileCount++;
    }

    @Override
    protected void onEnterDirectory(DirectoryNode directory, boolean isRoot) {
        directoryCount++;
        currentDepth++;
        maxDepth = Math.max(maxDepth, currentDepth);
    }

    @Override
    protected void onLeaveDirectory(DirectoryNode directory, boolean isRoot) {
        currentDepth--;
    }
}
