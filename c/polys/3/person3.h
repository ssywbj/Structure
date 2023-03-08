#include "virtual_fun3.h"

#ifndef STRUCT_PERSON_3
#define STRUCT_PERSON_3

struct person3 {
    char *name;
    int age;
};

void bplay(char* type);

void bplay2(play p, char* type); //利用指向函数的指针实现方法的重载

#endif