#include <stdio.h>

struct student{
    char *name;
    int age;
    float score;
} stu, stu2, stu3 = {"Wbj3", 25, 100.56}, *pstu3 = &stu3; //struct实例、指针

int main()
{
    stu.name = "Wbj";
    stu.age = 23;
    stu.score = 100.5;
    printf("stu name: %s, age: %d, score: %.1f\n", stu.name, stu.age, stu.score);
    printf("stu2 name: %s, age: %d, score: %.1f\n", stu2.name, stu2.age, stu2.score);
    printf("st3 name: %s, age: %d, score: %.1f\n", stu3.name, stu3.age, stu3.score);

    struct student *pstu = &stu;
    printf("(*pstu). name: %s, age: %d, score: %.1f\n", (*pstu).name, (*pstu).age, (*pstu).score);
    printf("pstu3-> name: %s, age: %d, score: %.1f\n", pstu3->name, pstu3->age, pstu3->score);

    struct school{
        char *name;
        float score;
    } sch[] = {
        {"Ssy", 100.54},
        {"Ssy2", 99},
        {"Ssy3", 98}
    }, sch2[2], *psch; //struct数组
    printf("sch[0] name: %s, score: %.1f\n", sch[0].name, sch[0].score);
    sch[0].name = "Ssy yy";
    sch[0].score = 100;
    printf("sch[0] name: %s, score: %.1f\n", sch[0].name, sch[0].score);
    for(int i = 0; i<2; i++)
    {
        sch2[i].name = "Sss";
        sch2[i].score = (90 + i);
        printf("sch2[%d] name: %s, score: %.1f\n", i, sch2[i].name, sch2[i].score);
    }
    int len = sizeof(sch) / sizeof(struct school);
    printf("struct array len: %d\n", len);
    for(psch = sch;psch < sch + len;psch++)
    {
        printf("psch->sch name: %s, score: %.1f\n", psch->name, psch->score);
    }

    struct{
        char *name;
    } anonymous = {"anonymous struct"}; //匿名struct
    printf("anonymous name: %s\n", anonymous.name);

    return 0;
}

