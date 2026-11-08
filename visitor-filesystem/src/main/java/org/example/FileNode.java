package org.example;

/** A regular file with a name and a non-negative size in bytes. */
public final class FileNode extends AbstractNode {

    private final long size;

    public FileNode(String name, long size) {
        super(name);
        if (size < 0) {
            throw new IllegalArgumentException("File size must not be negative: " + size);
        }
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
