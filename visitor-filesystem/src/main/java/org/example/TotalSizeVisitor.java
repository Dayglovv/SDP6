package org.example;

/** Sums the sizes of all files in the visited tree; directories add nothing themselves. */
public final class TotalSizeVisitor extends TraversingVisitor {

    private long totalSize;

    public long getTotalSize() {
        return totalSize;
    }

    @Override
    protected void onStart() {
        totalSize = 0;
    }

    @Override
    protected void onFile(FileNode file) {
        totalSize += file.getSize();
    }
}
