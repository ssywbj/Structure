#include <stdio.h>

#include "person2.h"

/*
void pplay(beh (*bh)())
{
    puts("-----parent playing!!!!-----");
};

void play(beh *bh)
{
    puts("-----parent playing-----");
};

beh pl(){}
*/

void bplay(){
    puts("parent playing...");
}

/*
int main()
{

    struct person2 per2 = {"Wbj", 30};
    printf("person2, name: %s, age: %d \n", per2.name, per2.age);
    per2.bh.pl = bplay;
    per2.bh.pl();

    //play(&per2.bh);

    //beh (*b)() = pl;
    //pplay(b);

    return 0;
}*/
