#include "virtual_fun.h"

#ifndef STRUCT_PERSON_2
#define STRUCT_PERSON_2

struct person2 {
    char *name;
    int age;

    beh bh;
};

void bplay();

typedef void (*pointer)(); //利用指向函数的指针实现方法的重载

#endif