#include <stdio.h>

void main()
{
    //http://c.biancheng.net/view/1990.html
    int a = 100;
    char str[3] = "abc";
    printf("%#x %#X\n", &a, str); //以16进制格式打印变量a和str的地址，#x:小写，0xabcdef格式；#X:大写，0XABCDEF格式；&：取地址运算符

    int *p = &a; //定义指针变量p，它指向变量a的地址；*：指针运算符，用于获取地址上的数据。
    printf("a: %d, address: %#x\n", a, &a); 
    printf("a: %d, address: %#x, &p: %#x\n", *p, p, &p); //p：变量a的地址，*p：解指针操作（获取地址上的数据）；&p：变量p的地址。 
}
