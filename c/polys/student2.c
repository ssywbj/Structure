#include <stdio.h>

#include "student2.h"

void pplay(beh (*bh)())
{
    puts("-----student playing!!!!-----");
};

void play(beh *bh)
{
    puts("-----student playing-----");
};

/*
int main()
{
    struct student2 stu2 = {{"Suheng", 6}};
    printf("stu2, name: %s, age: %d \n", stu2.psn.name, stu2.psn.age);
    play(&stu2.psn.bh);

    return 0;
}
*/