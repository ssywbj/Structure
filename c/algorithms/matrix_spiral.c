#include <stdio.h>

int main() {

    int matrix[3][5] = {{1, 2, 3, 4, 5}, {12, 13, 14, 15, 6}, {11, 10, 9, 8, 7}};

    puts("---print matrix by index---");
    for(int i = 0;i < 3;i++){
        for(int j = 0;j < 5;j++){
            printf("%d ", matrix[i][j]);
        }
        printf("\n");
    }

    //指向二维数组的指针：指向二维数组的开头，每行有5个元素。因为[]的优先级高于*，所以()是必须要加的。
    //如果地写成int *p[5]，那么应理解为int *(p[5])，p就成了一个指针数组，而不是二维数组指针。
    int (*p)[5] = matrix; //指向二维数组的指针p

    int unitSize = sizeof(matrix[0][0]);
    const int rowSize = sizeof(matrix[0]), rowLen = rowSize / unitSize;
    printf("row size = %d, len = %d\n", rowSize, rowLen);
    int arraySize = sizeof(matrix), arrayLen = arraySize / rowSize;
    printf("array size = %d, len = %d\n", arraySize, arrayLen);

    puts("---print matrix by pointer---");

    //“**p”等价于“c0=*p;d=*c0”, *p解出二维数组第一行的地址c0，*c0解出第一行数组的第一个元素，也可以写成：*(*(p+0)+0)
    printf("matrix[0][0] = %d\n", **p);

    for(int i = 0;i < arrayLen;i++){
        for(int j = 0;j < rowLen;j++){
            printf("%d ", *(*(p + i) + j));
        }
        printf("\n");
    }

    //https://leetcode.cn/problems/spiral-matrix/solution/
    puts("---print matrix spiral---");
    int leftIndex = 0, *pLeftIndex = &leftIndex;
    int rightIndex = rowLen - 1, *pRightIndex = &rightIndex;
    int topIndex = 0, *pTopIndex = &topIndex;
    int bottomIndex = arrayLen - 1, *pBottomIndex = &bottomIndex;
    /*for(int i = topIndex;i <= bottomIndex;i++){
        for(int j = leftIndex;j <= rightIndex;j++){
            if(j == rowLen - 1){
                printf("%d \n", matrix[i][j]);
            }
        }
    }*/

    printf("matrix[0][0] = %d, matrix[0][4] = %d, matrix[1][0] = %d\n", *(*(p + 0)), *(*(p + 0) + 4), *(*(p + 1)));
    int col = 0, row = 0, count = 0;
    while(count != 12){
        //(p)[5] = *(p + i) + j;
        count++;
        printf("count = %d, col = %d, row = %d, d = %d, \n", count, col, row, matrix[col][row]);
        if(count < 5){
            row++;
        } else if(count >= 5 && count < 7){
            col++;
        } else if(count >= 7 && count < 11){
            row--;
        } else if(count >= 11 && count < 12){
            col--;
        }

        //if(j == rowLen - 1){
        //    i++;
        //    if(i == )
        //} else {
        //    j++;
        //}
    }

    return 0;
}