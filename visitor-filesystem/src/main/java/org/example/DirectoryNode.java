package org.example;

import java.util.ArrayList;
import java.util.List;

/** A directory containing files and other directories. Guards the tree integrity. */
public final class DirectoryNode extends AbstractNode {

    private final List<Element> children = new ArrayList<>();

    public DirectoryNode(String name) {
        super(name);
    }

    /**
     * Adds a child. Throws {@link IllegalArgumentException} for invalid input
     * (null, duplicate name, cycle) and {@link IllegalStateException} if the
     * element already belongs to another directory.
     */
    public void add(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Element must not be null");
        }
        if (element.getParent() != null) {
            throw new IllegalStateException("'" + element.getName() + "' already has a parent");
        }
        if (isSelfOrAncestor(element)) {
            throw new IllegalArgumentException("Adding '" + element.getName() + "' would create a cycle");
        }
        if (hasChildNamed(element.getName())) {
            throw new IllegalArgumentException("Duplicate name in '" + getName() + "': " + element.getName());
        }
        children.add(element);
        element.attachTo(this);
    }

    /** @return unmodifiable snapshot of the children in insertion order */
    public List<Element> getChildren() {
        return List.copyOf(children);
    }

    boolean containsChild(Element element) {
        for (Element child : children) {
            if (child == element) {
                return true;
            }
        }
        return false;
    }

    private boolean hasChildNamed(String name) {
        for (Element child : children) {
            if (child.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /** True if candidate is this directory or one of its ancestors. */
    private boolean isSelfOrAncestor(Element candidate) {
        for (Element current = this; current != null; current = current.getParent()) {
            if (current == candidate) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
