package org.example;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

/**
 * Finds files whose name contains the criterion (case-insensitive) AND whose size is
 * at least the threshold. Results are paths relative to the root, joined with '/',
 * sorted alphabetically.
 */
public final class SearchVisitor extends TraversingVisitor {

    private final String nameCriterion;
    private final long minSize;
    private final Deque<String> currentPath = new ArrayDeque<>();
    private final List<String> results = new ArrayList<>();

    public SearchVisitor(String nameCriterion, long minSize) {
        if (nameCriterion == null) {
            throw new IllegalArgumentException("Name criterion must not be null");
        }
        if (minSize < 0) {
            throw new IllegalArgumentException("Minimum size must not be negative: " + minSize);
        }
        this.nameCriterion = nameCriterion.toLowerCase(Locale.ROOT);
        this.minSize = minSize;
    }

    /** @return sorted, unmodifiable list of matching relative paths */
    public List<String> getResults() {
        List<String> sorted = new ArrayList<>(results);
        Collections.sort(sorted);
        return List.copyOf(sorted);
    }

    @Override
    protected void onStart() {
        results.clear();
        currentPath.clear();
    }

    @Override
    protected void onEnterDirectory(DirectoryNode directory, boolean isRoot) {
        if (!isRoot) {
            currentPath.addLast(directory.getName());
        }
    }

    @Override
    protected void onLeaveDirectory(DirectoryNode directory, boolean isRoot) {
        if (!isRoot) {
            currentPath.removeLast();
        }
    }

    @Override
    protected void onFile(FileNode file) {
        if (matches(file)) {
            currentPath.addLast(file.getName());
            results.add(String.join("/", currentPath));
            currentPath.removeLast();
        }
    }

    private boolean matches(FileNode file) {
        boolean nameMatches = file.getName().toLowerCase(Locale.ROOT).contains(nameCriterion);
        return nameMatches && file.getSize() >= minSize;
    }
}
