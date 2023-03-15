#include <stdio.h>
#include <limits.h>
#include <stdlib.h>
#include <string.h>

void merge(int *arr, int p, int q, int r)
{
    for(int i = p; i < r; i++)
    {
        printf("%d ", *(arr + i));
    }
    printf("\n");

    const int lLen = q - p, rLen = r - q;
    int lArr[lLen + 1]; //lLen是有效数组长度，+1是给哨兵加了一个位置
    int rArr[rLen + 1];
    for(int i = p; i < q; i++)
    {
        lArr[i - p] = *(arr + i);
    }
    lArr[lLen] = INT_MAX;
    for(int i = q; i < r; i++)
    {
        rArr[i - q] = *(arr + i);
    }
    rArr[rLen] = INT_MAX;

    for(int *i = lArr; i < (lArr + lLen + 1); i++)
    {
        printf("%d ", *i);
    }
    printf("\n");
    for(int *i = rArr; i < (rArr + rLen); i++)
    {
        printf("%d ", *i);
    }
    printf("\n");

    for(int i = 0, j = 0, k = p; k < r; k++)
    {
        if(lArr[i] <= rArr[j])
        {
            arr[k] = lArr[i];
            i++;
        }
        else
        {
            arr[k] = rArr[j];
            j++;
        }
    }

    for(int i = p; i < r; i++)
    {
        printf("%d ", *(arr + i));
    }
    printf("\n");
}

void merge2(int *arr, int p, int q, int r)
{
    const int lLen = q - p, rLen = r - q;
    int lArr[lLen + 1]; //lLen是有效数组长度，+1是给哨兵加了一个位置
    int rArr[rLen + 1];
    for(int i = p; i < q; i++)
    {
        lArr[i - p] = *(arr + i);
    }
    lArr[lLen] = INT_MAX;
    for(int i = q; i < r; i++)
    {
        rArr[i - q] = *(arr + i);
    }
    rArr[rLen] = INT_MAX;

    for(int i = 0, j = 0, k = p; k < r; k++)
    {
        if(lArr[i] <= rArr[j])
        {
            arr[k] = lArr[i];
            i++;
        }
        else
        {
            arr[k] = rArr[j];
            j++;
        }
    }
}

void merge_sort(int *arr, int p, int r)
{
    printf("p: %d, r: %d\n", p, r);

    if(p < r)
    {
        int q = (p + r) / 2;
        merge_sort(arr, p, q);
        merge_sort(arr, q + 1, r);
        merge2(arr, p, q, r);
    }
}

//分治-治
void mergeSort_conquer(int *array, int left, int mid, int right, int *temp) {
    // [left, mid]和[mid+1, right]两个有序数组
    int i = left;
    int j = mid + 1;
    int index = 0;
    while (i <= mid && j <= right) {
        if (array[i] < array[j]) {
            temp[index++] = array[i++];
        } else {
            temp[index++] = array[j++];
        }
    }
    //剩余元素直接放入temp
    while (i <= mid) {
        temp[index++] = array[i++];
    }
    while (j <= right) {
        temp[index++] = array[j++];
    }
    //放回原数组
    index = 0;
    while (left <= right) {
        array[left++] = temp[index++];
    }
}

//分治-分
void mergeSort_divide(int *array, int left, int right, int *temp) {
    if (left < right) {
        int mid = left + (right - left) / 2;
        //左边归并排序
        mergeSort_divide(array, left, mid, temp);
        //右边归并排序
        mergeSort_divide(array, mid + 1, right, temp);
        //合并两个有序序列
        mergeSort_conquer(array, left, mid, right, temp);
    }
}

void mergeSort(int *array, int size) {
    int *temp = (int *) malloc(sizeof(int) * size);
    mergeSort_divide(array, 0, size - 1, temp);
}

int main()
{
    int arr2[] = {2, 4, 5, 7, 1, 2, 3, 6};
    merge(arr2, 0, 4, 8);
    puts("2222222222222222222222222222");
    int arr3[] = {1, 5, 8, 2, 4, 5, 7, 1, 2, 3, 6};
    merge(arr3, 3, 7, 11);
    puts("3333333333333311111111111111");
    merge2(arr3, 0, 3, 11);
    puts("3333333333333322222222222222");

    int arr_src[] = {5, 2, 4, 7, 1, 3, 2, 6, 0};
    const int len = sizeof(arr_src) / sizeof(arr_src[0]);

    int arr4[len];
    memcpy(arr4, arr_src, sizeof(int) * len);
    merge_sort(arr4, 0, len);
    for(int *i = arr4; i < (arr4 + len); i++)
    {
        printf("%d ", *i);
    }
    printf("\n");
    puts("4444444444444444444444444444");

    int arr5[len];
    memcpy(arr5, arr_src, sizeof(int) * len);
    mergeSort(arr5, len);
    for(int *i = arr5; i < (arr5 + len); i++)
    {
        printf("%d ", *i);
    }
    printf("\n");

    //int arr[] = {5, 2, 4, 6, 1, 3};
    /*int larr[] = {2, 4, 5, INT_MAX};
    int rarr[] = {1, 3, 6, INT_MAX};

    int arr[6] = {0};

    for(int i = 0, j = 0, k = 0; k < 6; k++)
    {
        if(larr[i] <= rarr[j])
        {
            arr[k] = larr[i];
            i++;
        }
        else
        {
            arr[k] = rarr[j];
            j++;
        }
    }
    //k = 0, i = 0, j = 0; j++, j = 1
    //k = 1, i = 0, j = 1; i++, i = 1
    //k = 2, i = 1, j = 1; j++, j = 2
    //k = 3, i = 1, j = 2; i++, i = 2
    //k = 4, i = 2, j = 2; i++, i = 3
    //k = 5, i = 3, j = 2; j++, j = 3*/

    /*for(int i = 0; i < 6; i++)
    {
        printf("%d ", arr[i]);
    }

    printf("\n");*/

    return 0;
}

