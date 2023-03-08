#include <stdio.h>

#include "student2.h"
#include "teacher2.h"

int main()
{
    play pl;

    struct person2 per2 = {"Wbj", 30};
    printf("person2, name: %s, age: %d \n", per2.name, per2.age);
    per2.bh.pl = bplay;
    //per2.bh.pl();
    pl = per2.bh.pl;
    pl();

    struct student stu2 = {{"Suheng", 6 , splay}, 99.9};
    printf("student, name: %s, age: %d, score: %.1f\n", stu2.psn.name, stu2.psn.age, stu2.score);
    //stu2.psn.bh.pl();
    pl = stu2.psn.bh.pl;
    pl();

    struct teacher tch = {{"Ssy", 26 , tplay}, 123456};
    printf("teacher, name: %s, age: %d, wages: %.1f\n", tch.psn.name, tch.psn.age, tch.wages);
    //tch.psn.bh.pl();
    pl = tch.psn.bh.pl;
    pl();

    return 0;
}