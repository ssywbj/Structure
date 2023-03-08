#include "person3.h"

#ifndef STRUCT_STUDENT_3
#define STRUCT_STUDENT_3

struct student {
    struct person3 psn;
    float score;
};

void splay(char* type);

#endif