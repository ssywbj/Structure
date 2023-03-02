#include <stdio.h>

int max(int a, int b);

int max(int x, int y)
{
    return (x > y) ? x : y;
}

int main()
{
    int a = 100;
    printf("a address: %p\n", &a); //打印变量a的地址,&:取地址运算符
    int *p = &a; //定义指针变量p指向变量a; *:指针运算符,用于获取地址上的数据.
    printf("a address: %p\n", p); //p:变量a的地址
    printf("a: %d, *p: %d\n", a, *p); //*p:解指针操作(获取地址上的数据)

    *p = 101; //改变地址里的值
    printf("a: %d\n", a);

    int b = 99, c = 98;
    p = &b; //改变指针p指向的地址
    b = a; //用变量a给变量b赋值,b值被改变
    c = *p; //*p也会同步改变
    printf("b: %d, c: %d\n", b, c);

    printf("------------pointer is to pointer------------\n");
    int **pp = &p; //pp:指向指针变量地址的变量(指向指针的指针),&p:变量p的地址
    printf("p address: %p\n", pp); //pp:指针变量p的地址
    printf("b address: %p, %p\n", &b, *pp); //*pp:读取地址上存放的值,通过上文可知它存放的也是地址,值为变量b的地址,暂命名为pb
    int *pb = *pp;
    printf("b: %d, %d, %d, %d\n", b, **pp, *(*pp), *pb); //**pp:读取地址pb存放的值.b=**pp=*(*pp)=*pb

    printf("------------array, array------------\n");
    int arr[] = {1, 2, 3, 4};
    p = &arr[0]; //指针p指向数组首元素
    printf("&arr[0]: %p, arr[0]: %d\n", p, *p);
    p = arr; //指向数组的指针实际上是指向数组的首元素:"p = arr"等价于"p = &arr[0]"
    printf("p: %p, *p: %d\n", p, *p);
    for(;p < (arr + 4);p++) //通过指针输出数组的值,"arr + 4":首元素地址向右偏移4个单位
    {
        printf("%d ", *p);
    }
    printf("\n");

    int x, y;
    scanf("%d%d", &x, &y); //scanf:输入函数,x:保存输入的变量,&x:存放的地址
    int z = max(x, y);
    printf("input x: %d, y: %d, max: %d\n", x, y, z);
    
    return 0;
}
