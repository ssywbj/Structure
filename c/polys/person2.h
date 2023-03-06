#include "virtual_fun.h"

#ifndef STRUCT_PERSON_2
#define STRUCT_PERSON_2

struct person2 {
    char *name;
    int age;

    beh bh;
};

void pplay(beh (*bh)());

void play(beh *bh);

#endif