#include <stdio.h>

void merge(int *arr, int p, int q, int r)
{

}

int main()
{
    //int arr[] = {5, 2, 4, 6, 1, 3};
    int larr[] = {2, 4, 5, 100000};
    int rarr[] = {1, 3, 6, 100000};

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
    //k = 5, i = 3, j = 2; j++, j = 3

    for(int i = 0; i < 6; i++)
    {
        printf("%d ", arr[i]);
    }

    printf("\n");

    return 0;
}

