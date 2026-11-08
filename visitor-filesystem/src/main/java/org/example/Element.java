package org.example;

/**
 * Element of the file system that can be visited by a {@link Visitor}.
 * First step of Double Dispatch: the element decides which visit(...) overload is called.
 */
public interface Element {

    void accept(Visitor visitor);

    String getName();

    /** @return the directory that owns this element, or {@code null} for a root / detached element */
    DirectoryNode getParent();

    /**
     * Internal hook used by {@link DirectoryNode#add(Element)} to record ownership.
     * It is rejected unless the parent really contains this element, so it cannot be
     * used to bypass the tree integrity rules.
     */
    void attachTo(DirectoryNode parent);
}
