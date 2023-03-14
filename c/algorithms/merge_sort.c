#include <stdio.h>
#include <limits.h>

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

int main()
{
    int arr2[] = {2, 4, 5, 7, 1, 2, 3, 6};
    merge(arr2, 0, 4, 8);
    puts("2222222222222222222222222222");
    int arr3[] = {1, 5, 8, 2, 4, 5, 7, 1, 2, 3, 6};
    merge(arr3, 3, 7, 11);
    puts("3333333333333311111111111111");
    merge(arr3, 0, 3, 11);

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

