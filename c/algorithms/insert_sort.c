#include <stdio.h>

int main()
{
    int arr[] = {5, 2, 4, 6, 1, 3};
    const int len = sizeof(arr) / sizeof(arr[0]);
    for(int i = 0; i < len; i++)
    {
        printf("%-2d", arr[i]);
    }
    printf("\n");

    for(int j = 1; j < len; j++)
    {
        int key = arr[j];
        int i = j - 1; //前面(j - 1)个数是已经排序好的
        while(i >= 0 && arr[i] > key) //依次取出前面的数和比较的数比较，升序
        //while(i >= 0 && arr[i] < key) //依次取出前面的数和比较的数比较，降序
        {
            arr[i + 1] = arr[i]; //如果前面的数大于key,则它的位置被后移
            i--; //--:在排好的数中从后向前取出数据
        }

        arr[i + 1] = key; //被比较的数放在合适的位置
    }

    for(int i = 0; i < len; i++)
    {
        printf("%d ", arr[i]);
    }
    printf("\n");

    return 0;
}

