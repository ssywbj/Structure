#include <stdio.h>

#include "student2.h"

int main()
{
    //struct person2 per2 = {"Ssy2", 18};
    //printf("per2, name: %s, age: %d \n", per2.name, per2.age);
    //play(&per2.bh);

    struct student2 stu2 = {{"Suheng", 6}};
    printf("stu2, name: %s, age: %d \n", stu2.psn.name, stu2.psn.age);
    play(&stu2.psn.bh);

    return 0;
}