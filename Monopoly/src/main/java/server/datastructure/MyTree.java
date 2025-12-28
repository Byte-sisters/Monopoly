package server.datastructure;

public class MyTree<T> {
    public static class TreeNode<T> {

        private T data;
        private   TreeNode<T> parent;
        private   TreeNode<T> firstChild;
        private   TreeNode<T> nextSibling;

        public TreeNode(T data) {
            this.data = data;
            this.parent = null;
            this.firstChild = null;
            this.nextSibling = null;
        }
    }


        private TreeNode<T> root;

        public MyTree(T rootData) {
            root = new TreeNode<>(rootData);
        }

        public TreeNode<T> getRoot() {
            return root;
        }

        public void insert(TreeNode<T> parent, T data) {
            TreeNode<T> newNode = new TreeNode<>(data);
            newNode.parent = parent;

            if (parent.firstChild == null) {
                parent.firstChild = newNode;
            } else {
                TreeNode<T> current = parent.firstChild;
                while (current.nextSibling != null) {
                    current = current.nextSibling;
                }
                current.nextSibling = newNode;
            }
        }

        public void delete(TreeNode<T> node) {
            if (node == root) {
                root = null;
                return;
            }

            TreeNode<T> parent = node.parent;
            if (parent == null) return;

            TreeNode<T> current = parent.firstChild;
            TreeNode<T> prev = null;

            while (current != null) {
                if (current == node) {
                    if (prev == null) {
                        parent.firstChild = current.nextSibling;
                    } else {
                        prev.nextSibling = current.nextSibling;
                    }
                    return;
                }
                prev = current;
                current = current.nextSibling;
            }
        }

        public void traverse(TreeNode<T> node) {
            if (node == null) return;

            System.out.println(node.data);

            TreeNode<T> child = node.firstChild;
            while (child != null) {
                traverse(child);
                child = child.nextSibling;
            }
        }
}
