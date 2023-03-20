#include <stdio.h>

int sum_rec(int n) {
    if(n == 1) {
        return 1;
    }

    return n + sum_rec(n - 1);
}

int sum_cyc(int n) {
    int sum = 0;
    while(n > 0){
        sum += n;
        n--;
    }

    return sum;
}

int sum_rec_tail(int n, int sum) { //tail recursion
    if(n == 0) {
        return sum;
    }

    sum += n; //direct calc result，尾递归的本质是循环
    return sum_rec_tail(n - 1, sum);
}

int sum_rec_tail_invoke(int n) { //invoke tail recursion
    return sum_rec_tail(n, 0);
}

unsigned int fibonacci_item_rec(unsigned int n) { //F0=0, F1=1, Fn=F(n-1)+F(n-2)(n>=2)
    if(n == 0) { //nothing item, F0
        return 0;
    }

    if(n == 1) { //first item, F1
        return 1;
    }

    return fibonacci_item_rec(n - 1) + fibonacci_item_rec(n - 2); //Fn(n>=2)
}

unsigned int fibonacci_item_cyc(unsigned int n) { //F0=0, F1=1, Fn=F(n-1)+F(n-2)(n>=2)
    int tmp = 0, f = 0, f_next = 1;
    while(n > 0){
        tmp = f;
        f = f_next;
        f_next += tmp;

        n--;
        printf("n = %d, tmp = %d, f = %d, f_next = %d\n", n, tmp, f, f_next);
    }

    return f;
}

unsigned int fibonacci_item_rec_tail(unsigned int n, unsigned int f, unsigned int f_next) {
    if(n == 0) {
        return f;
    }

    int tmp = f;
    f = f_next;
    f_next += tmp;
    return fibonacci_item_rec_tail(n - 1, f, f_next);
}

unsigned int fibonacci_item_rec_tail_invoke(unsigned int n) {
    return fibonacci_item_rec_tail(n, 0, 1);
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

    *(arr + n - 1) = fibonacci_item_rec(n - 1) + fibonacci_item_rec(n - 2);
    return fibonacci(n - 1, arr); //Fn(n>=2)
}

int main() {
    const int add = 11;
    int result = sum_rec(add);
    int result_cyc = sum_cyc(add);
    int result_rec_tail = sum_rec_tail_invoke(add);
    printf("result: %d, result_cyc: %d, result_rec_tail: %d\n", result, result_cyc, result_rec_tail);

    const int item = 14, item2 = item;
    int fib_item_rec = fibonacci_item_rec(item);
    int fib_item_cyc = fibonacci_item_cyc(item);
    int fib_item_rec_tail = fibonacci_item_rec_tail_invoke(item2);
    printf("fib_item_rec: %d, fib_item_cyc: %d, fib_item_rec_cyc: %d\n", fib_item_rec, fib_item_cyc, fib_item_rec_tail);

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