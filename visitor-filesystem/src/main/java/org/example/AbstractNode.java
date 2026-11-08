package org.example;

/** Common state of files and directories: validated name and parent link. */
abstract class AbstractNode implements Element {

    private final String name;
    private DirectoryNode parent;

    protected AbstractNode(String name) {
        this.name = validateName(name);
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }
        if (name.contains("/") || name.contains("\\")) {
            throw new IllegalArgumentException("Name must not contain path separators: " + name);
        }
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DirectoryNode getParent() {
        return parent;
    }

    @Override
    public void attachTo(DirectoryNode newParent) {
        if (newParent == null) {
            throw new IllegalArgumentException("Parent must not be null");
        }
        if (parent != null) {
            throw new IllegalStateException("'" + name + "' already has a parent");
        }
        if (!newParent.containsChild(this)) {
            throw new IllegalStateException("Elements can only be attached via DirectoryNode.add()");
        }
        this.parent = newParent;
    }
}
