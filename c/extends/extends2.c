#include <stdio.h>

#include "student.h"
#include "teacher.h"

int main()
{
    struct person psn = {"Ssy", 30};
    printf("person, name: %s, age: %d\n", psn.name, psn.age);

    struct student stu = {{"Suheng", 13}, 100.5};
    printf("student, name: %s, age: %d, score: %.1f\n", stu.psn.name, stu.psn.age, stu.score);
    struct teacher tch = {psn, 15487};
    printf("teacher, name: %s, age: %d, wages: %.1f\n", tch.psn.name, tch.psn.age, tch.wages);

    struct person *pper;
    pper = &stu.psn;
    printf("pstu, name: %s, age: %d\n", pper->name, pper->age);
    //printf("psn, name: %s, age: %d, score: %.1f\n", pper->name, pper->age, pper->score);
    pper = &tch.psn;
    printf("ptch, name: %s, age: %d\n", pper->name, pper->age);

    return 0;
}
