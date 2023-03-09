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

    struct student stu = {{"Suheng", 6 , splay}, 99.9};
    printf("student, name: %s, age: %d, score: %.1f\n", stu.psn.name, stu.psn.age, stu.score);
    //stu.psn.bh.pl();
    pl = stu.psn.bh.pl;
    pl();

    struct teacher tch = {{"Ssy", 26 , tplay}, 123456};
    printf("teacher, name: %s, age: %d, wages: %.1f\n", tch.psn.name, tch.psn.age, tch.wages);
    //tch.psn.bh.pl();
    pl = tch.psn.bh.pl;
    pl();

    puts("------------------------------------");

    struct person2 *pper;
    pper = (struct person2 *)&stu;
    pper->bh.pl();

    pper = (struct person2 *)&tch;
    pper->bh.pl();

    return 0;
}