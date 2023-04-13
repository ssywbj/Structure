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

    @Before
    public void before() {
        System.out.println("-------------------SpiralMatrix开始测试-------------------");
    }

    @After
    public void after() {
        System.out.println("-------------------SpiralMatrix结束测试-------------------");
    }

    @Test
    public void testSpiralOrder() {
    }

}