#include <stdio.h>

int sum(int n) {
    if(n == 1) {
        return 1;
    }

    return n + sum(n - 1);
}

int sum_tail(int n, int sum) { //tail recursion
    if(n == 0) {
        return sum;
    }

    sum += n; //direct calc result
    return sum_tail(n - 1, sum);
}

int fibonacci_item(unsigned int n) { //F0=0, F1=1, Fn=F(n-1)+F(n-2)(n>=2)
    if(n == 0) { //nothing item, F0
        return 0;
    }

    if(n == 1) { //first item, F1
        return 1;
    }

    return fibonacci_item(n - 1) + fibonacci_item(n - 2); //Fn(n>=2)
}

int fibonacci_item_tail(unsigned int n, unsigned int f1, unsigned int f2) { //F0=0, F1=1, Fn=F(n-1)+F(n-2)(n>=2)
    if(n == 0) { //nothing item, F0
        return f1;
    }

    if(n == 1) { //first item, F1
        return f2;
    }

    int tmp = f1;
    f1 = f2;
    f2 += tmp;
    return fibonacci_item_tail(n - 1, f1, f2); //Fn(n>=2)
}

int *fibonacci(unsigned int n, int *arr) { //F0=0, F1=1, Fn=F(n-1)+F(n-2)(n>=2)
    if(n == 0) { //nothing item, F0
        *arr = 1;
        return arr;
    }

    if(n == 1) { //first item, F1
        *(arr + 1) = 1;
        return arr;
    }

    *(arr + n - 1) = fibonacci_item(n - 1) + fibonacci_item(n - 2);
    return fibonacci(n - 1, arr); //Fn(n>=2)
}

int main() {
    int result = sum(9);
    int result_tail = sum_tail(9, 0);
    printf("result: %d, result_tail: %d\n", result, result_tail);

    const int item = 5, item2 = item;
    int fib_item = fibonacci_item(item);
    printf("fibonacci_item: %d\n", fib_item);
    int fib_item_tail = fibonacci_item_tail(item2, 0, 1);
    printf("fibonacci_item_tail: %d\n", fib_item_tail);

    printf("-------fibonacci array---------\n");
    int arr[item2];
    //fibonacci(item2, arr);
    //for(int *i = arr, j = 1;i < (arr + item2); i++, j++){
    //    *i = j;
    //}
    //for(int *i = arr;i < (arr + item2); i++){
    //    printf("%d ", *i);
    //}
    //printf("\n");
    //printf("over: %d, item2: %d\n", *(arr - 1), item2);

    return 0;
}