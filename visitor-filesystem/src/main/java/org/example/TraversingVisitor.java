package org.example;

/**
 * Base class for visitors that own the traversal (variant B).
 * The recursion over children is written once here, without instanceof or casts:
 * a directory simply calls child.accept(this) and Double Dispatch does the rest.
 * Every top-level traversal calls {@link #onStart()}, so a visitor can be reused safely.
 */
abstract class TraversingVisitor implements Visitor {

    private int level;

    @Override
    public final void visit(FileNode file) {
        if (level == 0) {
            onStart();
        }
        onFile(file);
    }

    @Override
    public final void visit(DirectoryNode directory) {
        boolean isTopLevel = level == 0;
        if (isTopLevel) {
            onStart();
        }
        onEnterDirectory(directory, isTopLevel);
        level++;
        try {
            for (Element child : directory.getChildren()) {
                child.accept(this);
            }
        } finally {
            level--;
        }
        onLeaveDirectory(directory, isTopLevel);
    }

    /** Resets accumulated results before a new traversal. */
    protected abstract void onStart();

    protected abstract void onFile(FileNode file);

    protected void onEnterDirectory(DirectoryNode directory, boolean isRoot) {
    }

    protected void onLeaveDirectory(DirectoryNode directory, boolean isRoot) {
    }
}
