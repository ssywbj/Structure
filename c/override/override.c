#include <stdio.h>

void func_int(void* a)
{
    printf("func_int, a: %d\n", *(int*)a); //void*强转为int*，输出int
}

void func_double(void* a)
{
    printf("func_double, a: %.2f\n", *(double*)a); //double*强转为double*，输出double
}

typedef void (*pointer) (void*);

void func(pointer p, void* params)
{
    p(params);
}

int main()
{
    int a = 10;
    double b = 3.14;
    func(func_int, &a);
    func(func_double, &b);

    return 0;
}