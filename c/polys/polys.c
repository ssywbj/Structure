#include <stdio.h>

typedef void (*EatPtr)();
typedef void (*PlayPtr)();

typedef struct _virtualPtrTable{
    EatPtr eat;
    PlayPtr play;
}VPtrTable;

typedef struct _base{
    VPtrTable vptrTable;
    int age;
}Base;

typedef struct _deriveA{
    Base base;
    int age;
}DeriveA;

typedef struct _deriveB{
    Base base;
    int age;
}DeriveB;

/** 基类的实现函数 **/
void baseEat(){
    //cout<<"基类在吃饭....."<<endl;
    puts("基类在吃饭.....");
}
void basePlay(){
    //cout<<"基类在玩耍....."<<endl;
    puts("基类在玩耍.....");
}

/** 派生类A的实现函数 **/
void aEat(){
    //cout<<"子类A在吃饭....."<<endl;
    puts("子类A在吃饭.....");
}
void aPlay(){
    //basePlay();
}

/** 派生类B的实现函数 **/
void bEat(){
    //cout<<"子类B在吃饭....."<<endl;
    puts("子类B在吃饭.....");
}
void bPlay(){
    basePlay();
}

int main()
{
    Base *base;
    DeriveA deriveA;
    deriveA.base.vptrTable.eat = aEat;
    deriveA.base.vptrTable.play = aPlay;
    deriveA.base.age = 40;
    deriveA.age = 20;

    DeriveB deriveB;
    deriveB.base.vptrTable.eat = bEat;
    deriveB.base.vptrTable.play = bPlay;
    deriveB.base.age = 40;
    deriveB.age = 21;

    base = (Base *)&deriveA;
    base->vptrTable.eat();
    base->vptrTable.play();
    //cout<<"age："<<base->age<<endl;
    printf("age：%d", base->age);
    //cout<<"---------------------------------------------\n";
    puts("---------------------------------------------");
    base = (Base *)&deriveB;
    base->vptrTable.eat();
    base->vptrTable.play();
    //cout<<"age："<<base->age<<endl;
    printf("age：%d", base->age);

    return 0;
}