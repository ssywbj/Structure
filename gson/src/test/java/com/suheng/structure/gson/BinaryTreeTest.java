package com.suheng.structure.gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * https://leetcode.cn/problems/binary-tree-inorder-traversal/
 * https://leetcode.cn/problems/binary-tree-postorder-traversal/
 * https://leetcode.cn/problems/binary-tree-preorder-traversal/
 * https://leetcode.cn/problems/binary-tree-level-order-traversal/
 * https://www.cnblogs.com/zhi-leaf/p/10813048.html
 */
public class BinaryTreeTest {

    static class TreeNode {
        String key;
        TreeNode left;
        TreeNode right;

        public TreeNode() {
        }

        public TreeNode(String key) {
            this.key = key;
        }

        public TreeNode(String key, TreeNode left, TreeNode right) {
            this.key = key;
            this.left = left;
            this.right = right;
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
        TreeNode nodeH = new TreeNode("H");
        TreeNode nodeI = new TreeNode("I");
        TreeNode nodeJ = new TreeNode("J");
        TreeNode nodeK = new TreeNode("K");

        TreeNode nodeD = new TreeNode("D", null, nodeH);
        TreeNode nodeE = new TreeNode("E", null, nodeI);
        TreeNode nodeF = new TreeNode("F", nodeJ, nodeK);
        TreeNode nodeG = new TreeNode("G");

        TreeNode nodeB = new TreeNode("B", nodeD, nodeE);
        TreeNode nodeC = new TreeNode("C", nodeF, nodeG);

        /*TreeNode nodeB = new TreeNode("B", null, null);
        TreeNode nodeC = new TreeNode("C", null, null);*/

        TreeNode nodeA = new TreeNode("A", nodeB, nodeC);

        this.inorderTraversal(nodeA);
        System.out.println();
        this.postorderTraversal(nodeA);
        System.out.println();
        this.preorderTraversal(nodeA);
        System.out.println();

        //List<List<String>> lists2 = this.levelOrderTraversal(null);
        //List<List<String>> lists2 = this.levelOrderTraversal(new TreeNode2());
        List<List<String>> lists2 = this.levelOrderTraversal(nodeA);
        System.out.println(lists2);
    }

    public void inorderTraversal(TreeNode treeNode) {
        if (treeNode != null) {
            inorderTraversal(treeNode.left);
            System.out.print(treeNode.key + " ");
            inorderTraversal(treeNode.right);
        }
    }

    public void postorderTraversal(TreeNode treeNode) {
        if (treeNode != null) {
            postorderTraversal(treeNode.left);
            postorderTraversal(treeNode.right);
            System.out.print(treeNode.key + " ");
        }
    }

    public void preorderTraversal(TreeNode treeNode) {
        if (treeNode != null) {
            System.out.print(treeNode.key + " ");
            preorderTraversal(treeNode.left);
            preorderTraversal(treeNode.right);
        }
    }

    public List<List<String>> levelOrderTraversal(TreeNode treeNode) {
        ArrayList<List<String>> resultList = new ArrayList<>();

        if (treeNode == null) {
            return resultList;
        }
        this.levelOrderTraversal(Collections.singletonList(treeNode), resultList);
        return resultList;

        /*this.levelOrderTraversal(Collections.singletonList(treeNode), resultList);
        return resultList;*/
    }

    private void levelOrderTraversal(List<TreeNode> nodes, List<List<String>> resultList) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        List<TreeNode> params = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (TreeNode treeNode : nodes) {
            if (treeNode != null) {
                //System.out.print(treeNode.key + " ");
                result.add(treeNode.key);
                if (treeNode.left != null) {
                    params.add(treeNode.left);
                }
                if (treeNode.right != null) {
                    params.add(treeNode.right);
                }
            }
        }

        //System.out.println();
        resultList.add(result);
        levelOrderTraversal(params, resultList);
    }

    //https://leetcode.cn/problems/zhong-jian-er-cha-shu-lcof/
    private void buildTree() {

    }

}