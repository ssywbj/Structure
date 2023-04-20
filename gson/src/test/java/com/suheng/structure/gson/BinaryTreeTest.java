package com.suheng.structure.gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * https://leetcode.cn/problems/binary-tree-inorder-traversal/
 * https://www.cnblogs.com/zhi-leaf/p/10813048.html
 */
public class BinaryTreeTest {

    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        public TreeNode() {
        }

        public TreeNode(int val) {
            this.val = val;
        }

        public TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    class TreeNode2 {
        String key;
        TreeNode2 left;
        TreeNode2 right;

        public TreeNode2() {
        }

        public TreeNode2(String key) {
            this.key = key;
        }

        public TreeNode2(String key, TreeNode2 left, TreeNode2 right) {
            this.key = key;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "TreeNode2{" +
                    "key='" + key + '\'' +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }
    }

    @Before
    public void before() {
        System.out.println("-------------------Inorder Traversal开始测试-------------------");
    }

    @After
    public void after() {
        System.out.println("-------------------Inorder Traversal结束测试-------------------");
    }

    @Test
    public void testInorderTraversal() {
        TreeNode treeNode3 = new TreeNode(3);

        TreeNode treeNode2 = new TreeNode(2, treeNode3, null);

        TreeNode treeNode1 = new TreeNode(1, null, treeNode2);

        this.inorderTraversal(treeNode1);
        System.out.println();

        TreeNode2 nodeH = new TreeNode2("H");
        TreeNode2 nodeI = new TreeNode2("I");
        TreeNode2 nodeJ = new TreeNode2("J");
        TreeNode2 nodeK = new TreeNode2("K");

        TreeNode2 nodeD = new TreeNode2("D", null, nodeH);
        TreeNode2 nodeE = new TreeNode2("E", null, nodeI);
        TreeNode2 nodeF = new TreeNode2("F", nodeJ, nodeK);
        TreeNode2 nodeG = new TreeNode2("G");

        TreeNode2 nodeB = new TreeNode2("B", nodeD, nodeE);
        TreeNode2 nodeC = new TreeNode2("C", nodeF, nodeG);

        TreeNode2 nodeA = new TreeNode2("A", nodeB, nodeC);

        this.inorderTraversal(nodeA);
        System.out.println();
    }

    public void inorderTraversal(TreeNode treeNode) {
        if (treeNode != null) {
            inorderTraversal(treeNode.left);
            System.out.print(treeNode.val + "  ");
            inorderTraversal(treeNode.right);
        }
    }

    public void inorderTraversal(TreeNode2 treeNode) {
        if (treeNode != null) {
            inorderTraversal(treeNode.left);
            System.out.print(treeNode.key + " ");
            inorderTraversal(treeNode.right);
        }
    }

}