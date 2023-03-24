#include <stdio.h>

int main() {

    int matrix[3][5] = {{1, 2, 3, 4, 5}, {12, 13, 14, 15, 6}, {11, 10, 9, 8, 7}};

    puts("print matrix by index");
    for(int i = 0;i < 3;i++){
        for(int j = 0;j < 5;j++){
            printf("%d ", matrix[i][j]);
        }
        printf("\n");
    }

    //指向二维数组的指针：指向二维数组的开头，每行有5个元素。因为[]的优先级高于*，所以()是必须要加的。
    //如果地写成int *p[5]，那么应理解为int *(p[5])，p就成了一个指针数组，而不是二维数组指针。
    int (*p)[5] = matrix; //指向二维数组的指针p

    int unitSize = matrix[0][0];
    int rowSize = sizeof(matrix[0]);
    printf("row size = %d, len = %d\n", rowSize, rowSize / unitSize);
    int arraySize = sizeof(matrix);
    printf("array size = %d, len = %d\n", arraySize, arraySize / rowSize);

    //“**p”等价于“c0=*p;d=*c0”, *p解出二维数组第一行的地址c0，*c0解出第一行数组的第一个元素，也可以写成：*(*(p+0)+0)
    printf("matrix[0][0] = %d\n", **p);

    for(int i = 0;i < 3;i++){
        for(int j = 0;j < 5;j++){
            printf("%d ", *(*(p + i) + j));
        }
        printf("\n");
    }

    return 0;
}