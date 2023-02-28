#include <stdio.h>
#include <string.h>

int main()
{
    char str[] = "Wbj Ssy"; //C语言中没有专门的字符串变量，通常用字符数组来存放字符串
    int len = strlen(str);
    printf("str: %s, len: %d\n", str, len);

    char ch[6] = "123456";
    len = strlen(ch);
    printf("ch: %s, len: %d\n", ch, len);

    char ch0[7] = "123456";
    len = strlen(ch0);
    printf("ch0: %s, len: %d\n", ch0, len);

    puts("------strcat(str, ch0) wrong------");
    printf("strcat(str, ch0): %s\n", strcat(str, ch0));
    len = strlen(str);
    printf("str: %s, len: %d\n", str, len);
    puts("------strcat(str, ch0) right------");
    //char str2[strlen(str)+strlen(ch)] = "Wbj Ssy"
    //puts(str2);

    //puts("------strcat(ch0, str)------");
    //puts(strcat(ch0, str));
    //puts("------strcat(ch0, str) right------");
    //strcat(ch0, str);
    //puts(strcat(ch0, str));

    return 0;
}