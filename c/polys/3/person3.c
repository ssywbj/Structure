#include <stdio.h>

#include "person3.h"

void bplay(char* type)
{
    puts(type);
}

void bplay2(play p, char* type){
    p(type);
}

/*
int main()
{
    struct person3 per3 = {"Wbj", 30};
    printf("person3, name: %s, age: %d \n", per3.name, per3.age);

    bplay2(bplay, "parent playing...");

    return 0;
}
*/