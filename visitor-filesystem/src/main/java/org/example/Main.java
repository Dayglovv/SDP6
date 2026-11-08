package org.example;

/** Deterministic demonstration of the three visitors on a fixed in-memory file system. */
public class Main {

    public static void main(String[] args) {
        DirectoryNode root = buildSampleFileSystem();

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        System.out.println("=== TotalSizeVisitor ===");
        System.out.println("Total size: " + sizeVisitor.getTotalSize() + " bytes");

        SearchVisitor searchVisitor = new SearchVisitor("report", 1000);
        root.accept(searchVisitor);
        System.out.println("\n=== SearchVisitor (name contains 'report', size >= 1000) ===");
        searchVisitor.getResults().forEach(System.out::println);

        StatisticsVisitor statisticsVisitor = new StatisticsVisitor();
        root.accept(statisticsVisitor);
        System.out.println("\n=== StatisticsVisitor ===");
        System.out.println("Files: " + statisticsVisitor.getFileCount());
        System.out.println("Directories: " + statisticsVisitor.getDirectoryCount());
        System.out.println("Maximum depth: " + statisticsVisitor.getMaxDepth());
    }

    static DirectoryNode buildSampleFileSystem() {
        DirectoryNode root = new DirectoryNode("Root");
        root.add(new FileNode("document.txt", 1000));

        DirectoryNode pictures = new DirectoryNode("Pictures");
        pictures.add(new FileNode("photo.jpg", 2000));
        pictures.add(new FileNode("avatar.png", 500));
        root.add(pictures);

        DirectoryNode documents = new DirectoryNode("Documents");
        documents.add(new FileNode("report.pdf", 3000));
        root.add(documents);

        DirectoryNode projects = new DirectoryNode("Projects");
        projects.add(new FileNode("report-final.pdf", 1500));
        projects.add(new FileNode("report-draft.pdf", 400));
        projects.add(new DirectoryNode("Empty"));
        root.add(projects);
        return root;
    }
}
