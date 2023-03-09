#include <stdio.h>

int main()
{
    int arr[] = {5, 2, 4, 6, 1, 3};
    const int len = sizeof(arr) / sizeof(arr[0]);
    for(int i = 0; i < len; i++)
    {
        printf("%d ", arr[i]);
    }
    printf("\n");

    for(int j = 1; j < len; j++)
    {
        int key = arr[j];
        int i = j - 1;
        while(i >= 0 && arr[i] > key)
        {
            arr[i + 1] = arr[i];
            i--;
        }

        arr[i + 1] = key;
    }

    for(int i = 0; i < len; i++)
    {
        printf("%d ", arr[i]);
    }
    printf("\n");

    return 0;
}

