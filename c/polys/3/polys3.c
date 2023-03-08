#include <stdio.h>

#include "student3.h"
#include "teacher3.h"

int main()
{
    struct person3 per2 = {"Wbj", 30};
    printf("person3, name: %s, age: %d \n", per2.name, per2.age);

    struct student stu2 = {{"Suheng", 6}, 99.9};
    printf("student, name: %s, age: %d, score: %.1f\n", stu2.psn.name, stu2.psn.age, stu2.score);

    struct teacher tch = {{"Ssy", 26}, 123456};
    printf("teacher, name: %s, age: %d, wages: %.1f\n", tch.psn.name, tch.psn.age, tch.wages);

    extern void bplay2(play p, char*);
    bplay2(bplay, "3parent playing...");
    bplay2(splay, "3student playing...");
    bplay2(tplay, "3teacher playing...");

    return 0;
}