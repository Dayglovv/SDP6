package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FileSystemTest {
    @Test
    void t8_negativeFileSizeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad.txt", -100));
    }

    @Test
    void t8_duplicateNameInSameDirectoryIsRejected() {
        DirectoryNode dir = new DirectoryNode("Dir");
        dir.add(new FileNode("a.txt", 1));
        assertThrows(IllegalArgumentException.class, () -> dir.add(new FileNode("a.txt", 2)));
        assertThrows(IllegalArgumentException.class, () -> dir.add(new DirectoryNode("a.txt")));
        assertEquals(1, dir.getChildren().size());
    }

    @Test
    void t8_elementCannotHaveTwoParents() {
        DirectoryNode first = new DirectoryNode("First");
        DirectoryNode second = new DirectoryNode("Second");
        FileNode file = new FileNode("a.txt", 1);
        first.add(file);

        assertThrows(IllegalStateException.class, () -> second.add(file));
        assertTrue(second.getChildren().isEmpty());
        assertSame(first, file.getParent());
    }

    @Test
    void t8_directoryCannotBeAddedToItself() {
        DirectoryNode dir = new DirectoryNode("Dir");
        assertThrows(IllegalArgumentException.class, () -> dir.add(dir));
    }

    @Test
    void t8_cyclesAreRejected() {
        DirectoryNode a = new DirectoryNode("A");
        DirectoryNode b = new DirectoryNode("B");
        DirectoryNode c = new DirectoryNode("C");
        a.add(b);
        b.add(c);

        assertThrows(IllegalArgumentException.class, () -> c.add(a));
        assertThrows(IllegalArgumentException.class, () -> b.add(a));
        assertNull(a.getParent());
    }

    @Test
    void t8_attachToCannotBypassAdd() {
        DirectoryNode dir = new DirectoryNode("Dir");
        FileNode file = new FileNode("a.txt", 1);
        assertThrows(IllegalStateException.class, () -> file.attachTo(dir));
        assertNull(file.getParent());
    }

    @Test
    void t8_invalidNamesAndNullsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new FileNode("  ", 1));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryNode("a/b"));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryNode("Dir").add(null));
    }

    @Test
    void validNestedFileIsAllowed() {
        DirectoryNode root = new DirectoryNode("Root");
        DirectoryNode sub = new DirectoryNode("Sub");
        FileNode file = new FileNode("ok.txt", 0);
        sub.add(file);
        root.add(sub);

        assertSame(sub, file.getParent());
        assertSame(root, sub.getParent());
        assertEquals(1, root.getChildren().size());
    }

    @Test
    void sameNameInDifferentDirectoriesIsAllowed() {
        DirectoryNode root = new DirectoryNode("Root");
        DirectoryNode a = new DirectoryNode("A");
        DirectoryNode b = new DirectoryNode("B");
        a.add(new FileNode("same.txt", 1));
        b.add(new FileNode("same.txt", 1));
        root.add(a);
        root.add(b);
        assertEquals(2, root.getChildren().size());
    }

    @Test
    void childrenListCannotBeModifiedFromOutside() {
        DirectoryNode dir = new DirectoryNode("Dir");
        dir.add(new FileNode("a.txt", 1));

        assertThrows(UnsupportedOperationException.class,
                () -> dir.getChildren().add(new FileNode("hack.txt", 1)));
        assertThrows(UnsupportedOperationException.class, () -> dir.getChildren().clear());
        assertEquals(1, dir.getChildren().size());
    }

    @Test
    void zeroSizeFileIsAllowed() {
        assertEquals(0, new FileNode("empty.txt", 0).getSize());
    }
}
