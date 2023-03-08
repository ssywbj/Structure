#include "person3.h"

#ifndef STRUCT_TEACHER_3
#define STRUCT_TEACHER_3

struct teacher {
    struct person3 psn;
    float wages;
};

void tplay(char* type);

#endif