#include <stdio.h>
#include <string.h>

int main()
{
    char str[] = "Wbj Ssy"; //C语言中没有专门的字符串变量，通常用字符数组来存放字符串
    int len = strlen(str); //strlen：获取字符串长度，不包含'\0'字符
    printf("str: %s, len: %d\n", str, len);

    //在C语言中，字符串总是以'\0'作为结尾，所以'\0'也被称为字符串结束标志或者字符串结束符。
    //C语言在处理字符串时会从前往后逐个扫描字符，遇到'\0'就认为到达字符串的末尾，结束处理。所以'\0'至关重要，没有'\0'就意味着永远也到达不了字符串的结尾。
    //由""包围的字符串会自动在末尾添加'\0'。如"123456"从表面看只包含6个字符，其实不然，C语言会在最后隐式地添加一个'\0'，这个过程会在后台默默地进行，所以用""包围起来的字符串在给它分配数组的长度时，要记得多加1，不然会有意想不到的错误。
    char ch[6] = "123456"; //有六个字符，未给'\0'字符留有位置，这是错误的，可观察其打印结果，场景不同错误可能不一样
    len = strlen(ch);
    printf("ch: %s, len: %d\n", ch, len);
    char ch0[7] = "123456"; //在原来有六个字符的基础上多加了一个长度。
    len = strlen(ch0);
    printf("ch0: %s, len: %d\n", ch0, len);

    //puts("------strcat(str, ch0) wrong------");
    //strcat(str, ch0);
    //len = strlen(str);
    //printf("str: %s, len: %d\n", str, len);
    puts("------strcat(str2, ch0) right------");
    char str2[7 + 6 + 1] = "Wbj Ssy";
    len = strlen(str2);
    printf("str2: %s, len: %d\n", str2, len);
    strcat(str2, ch0);
    len = strlen(str2);
    printf("str2: %s, len: %d\n", str2, len);

    return 0;
}
