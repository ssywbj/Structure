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

    //int i = 0, j = 0;
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

    for(int i = 0; i < 6; i++)
    {
        printf("%d ", arr[i]);
    }

    printf("\n");

    return 0;
}

