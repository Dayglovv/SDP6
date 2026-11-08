package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VisitorTest {

    private DirectoryNode root;

    @BeforeEach
    void setUp() {
        root = new DirectoryNode("Root");
        root.add(new FileNode("document.txt", 1000));

        DirectoryNode pictures = new DirectoryNode("Pictures");
        pictures.add(new FileNode("photo.jpg", 2000));
        pictures.add(new FileNode("avatar.png", 500));
        root.add(pictures);

        DirectoryNode documents = new DirectoryNode("Documents");
        documents.add(new FileNode("report.pdf", 3000));
        root.add(documents);
    }

    /** T1: both element types call the matching visit(...) overload (Double Dispatch). */
    @Test
    void t1_elementsCallCorrectVisitMethod() {
        List<String> calls = new ArrayList<>();
        Visitor recorder = new Visitor() {
            @Override
            public void visit(FileNode file) {
                calls.add("file:" + file.getName());
            }

            @Override
            public void visit(DirectoryNode directory) {
                calls.add("dir:" + directory.getName());
            }
        };

        new FileNode("a.txt", 1).accept(recorder);
        new DirectoryNode("Dir").accept(recorder);

        assertEquals(List.of("file:a.txt", "dir:Dir"), calls);
    }

    /** T2: total size across several levels. */
    @Test
    void t2_totalSizeAcrossLevels() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        root.accept(visitor);
        assertEquals(6500, visitor.getTotalSize());
    }

    /** T2b: deeper nesting counts every file exactly once. */
    @Test
    void t2_totalSizeWithDeepNesting() {
        DirectoryNode deep = new DirectoryNode("Deep");
        DirectoryNode deeper = new DirectoryNode("Deeper");
        deeper.add(new FileNode("x.bin", 100));
        deep.add(deeper);
        root.add(deep);

        TotalSizeVisitor visitor = new TotalSizeVisitor();
        root.accept(visitor);
        assertEquals(6600, visitor.getTotalSize());
    }

    /** T3: both filters (name AND minimum size) must hold; the threshold is inclusive. */
    @Test
    void t3_searchAppliesBothFilters() {
        DirectoryNode projects = new DirectoryNode("Projects");
        projects.add(new FileNode("report-final.pdf", 1500));
        projects.add(new FileNode("report-small.pdf", 999));
        projects.add(new FileNode("report-exact.pdf", 1000));
        projects.add(new FileNode("big-photo.jpg", 9000));
        root.add(projects);

        SearchVisitor visitor = new SearchVisitor("report", 1000);
        root.accept(visitor);

        assertEquals(List.of(
                "Documents/report.pdf",
                "Projects/report-exact.pdf",
                "Projects/report-final.pdf"), visitor.getResults());
    }

    /** T4: paths are relative to the root, use '/', and are sorted alphabetically. */
    @Test
    void t4_pathsAreRelativeAndSorted() {
        SearchVisitor visitor = new SearchVisitor("", 0);
        root.accept(visitor);

        assertEquals(List.of(
                "Documents/report.pdf",
                "Pictures/avatar.png",
                "Pictures/photo.jpg",
                "document.txt"), visitor.getResults());
    }

    @Test
    void t4_pathsReflectDeepStructureFromChosenRoot() {
        DirectoryNode a = new DirectoryNode("A");
        DirectoryNode b = new DirectoryNode("B");
        b.add(new FileNode("deep.txt", 10));
        a.add(b);
        root.add(a);

        SearchVisitor fromRoot = new SearchVisitor("deep", 0);
        root.accept(fromRoot);
        assertEquals(List.of("A/B/deep.txt"), fromRoot.getResults());

        SearchVisitor fromA = new SearchVisitor("deep", 0);
        a.accept(fromA);
        assertEquals(List.of("B/deep.txt"), fromA.getResults());
    }

    /** T5: statistics and depth (root = depth 0). */
    @Test
    void t5_statisticsAndDepth() {
        DirectoryNode r = new DirectoryNode("Root");
        r.add(new FileNode("file1.txt", 1));
        DirectoryNode folder1 = new DirectoryNode("Folder1");
        folder1.add(new FileNode("file2.txt", 1));
        DirectoryNode folder2 = new DirectoryNode("Folder2");
        folder2.add(new FileNode("file3.txt", 1));
        folder1.add(folder2);
        r.add(folder1);

        StatisticsVisitor visitor = new StatisticsVisitor();
        r.accept(visitor);

        assertEquals(3, visitor.getFileCount());
        assertEquals(3, visitor.getDirectoryCount());
        assertEquals(2, visitor.getMaxDepth());
    }

    /** T6: empty directories and searches without results. */
    @Test
    void t6_emptyDirectoryAndNoSearchResults() {
        DirectoryNode empty = new DirectoryNode("Empty");

        StatisticsVisitor stats = new StatisticsVisitor();
        empty.accept(stats);
        assertEquals(0, stats.getFileCount());
        assertEquals(1, stats.getDirectoryCount());
        assertEquals(0, stats.getMaxDepth());

        TotalSizeVisitor size = new TotalSizeVisitor();
        empty.accept(size);
        assertEquals(0, size.getTotalSize());

        SearchVisitor search = new SearchVisitor("nothing-like-this", 0);
        root.accept(search);
        assertTrue(search.getResults().isEmpty());
    }

    @Test
    void t6_emptyDirectoriesAreCountedInsideTree() {
        root.add(new DirectoryNode("Empty"));
        StatisticsVisitor stats = new StatisticsVisitor();
        root.accept(stats);
        assertEquals(4, stats.getFileCount());
        assertEquals(4, stats.getDirectoryCount());
    }

    /** T7: running the same visitor twice does not accumulate old results. */
    @Test
    void t7_repeatedRunsDoNotAccumulate() {
        TotalSizeVisitor size = new TotalSizeVisitor();
        root.accept(size);
        root.accept(size);
        assertEquals(6500, size.getTotalSize());

        SearchVisitor search = new SearchVisitor("report", 0);
        root.accept(search);
        root.accept(search);
        assertEquals(List.of("Documents/report.pdf"), search.getResults());

        StatisticsVisitor stats = new StatisticsVisitor();
        root.accept(stats);
        root.accept(stats);
        assertEquals(4, stats.getFileCount());
        assertEquals(3, stats.getDirectoryCount());
        assertEquals(1, stats.getMaxDepth());
    }

    @Test
    void searchRejectsInvalidSettings() {
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(null, 0));
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor("a", -1));
    }

    @Test
    void searchIsCaseInsensitive() {
        SearchVisitor visitor = new SearchVisitor("REPORT", 0);
        root.accept(visitor);
        assertEquals(List.of("Documents/report.pdf"), visitor.getResults());
    }
}
