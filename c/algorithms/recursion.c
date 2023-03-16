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

int fibonacci_item_tail(int n, int sum) {
    if(n == 0) { //nothing item, F0
        return sum;
    }

    if(n == 1) { //first item, F1
        return sum;
    }

    sum +=
    return fibonacci_item_tail(n - 1, sum);
}

int main() {
    int result = sum(9);
    int result_tail = sum_tail(9, 0);
    printf("result: %d, result_tail: %d\n", result, result_tail);

    int fib_item = fibonacci_item(11);
    printf("fibonacci_item: %d\n", fib_item);

    return 0;
}