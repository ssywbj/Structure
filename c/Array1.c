#include <stdio.h>
#include <string.h>
#include <stdlib.h>

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
    //确保字符串在最末尾带上字符'\0'的通常做法：1.原字符长度加1，如一个数组如果要保存26个大写字母，那么它要开辟27个长度空间；2.每个元素初始化为0，'\0'的ASCII码就是0。
    char letter[27] = {0};
    int index = 0;
    for(char c = 65;c <= 90;c++,index++)
    {
        letter[index] = c;
    }
    printf("letter: %s\n", letter);

    //puts("------strcat(str, ch0) wrong------");
    //strcat(str, ch0);
    //len = strlen(str);
    //printf("str: %s, len: %d\n", str, len);
    puts("------strcat(str2, ch0) right------");
    char str2[7 + 6 + 1] = "Wbj Ssy"; //需要给str2数组足够的空间
    len = strlen(str2);
    printf("before strcat str2: %s, len: %d\n", str2, len);
    //strcat(str1, str2)，字符串连接：把str2连接到str1后面并删除str1的结束标志'\0'。这函数要求str1必须足够长，要能同时容纳str1和str2，否则会有越界异常。
    strcat(str2, ch0);
    len = strlen(str2);
    printf("after strcat str2: %s, len: %d\n", str2, len);

    puts("-----------variable-sized array-----------");
    const int str3len = strlen(str) + strlen(ch0) + 1;
    //变长数组：使用变量指明数组的长度，标准上变长数组不能被初始化。变长数组是说数组的长度在定义之前可以改变，
    //但一旦定义了就不能再改变了，所以变长数组的容量也是不能扩大或缩小的，它仍然是静态数组。
    char str3[str3len];
    strcpy(str3, str); //先通过strcpy函数向数组赋值
    printf("before strcat str3: %s, len: %lu\n", str3, strlen(str3));
    strcat(str3, ch0); //再向数组追加内容
    printf("after strcat str3: %s, len: %lu\n", str3, strlen(str3));

    puts("-----------dynamic array: malloc、 realloc-----------");
    //动态数组：用动态分配内存法实现变长数组，然后使用realloc实现内存的再次分配，此时的数组可以看成是动态数组。
    //T* arr = (T*)malloc(len * sizeof(T)); //T是类型，如int；len变量是长度
    char* str4 = (char*) malloc(str3len * sizeof(char)); //malloc分配堆内存空间
    printf("str3len: %d, str3len * sizeof(char): %lu\n", str3len, str3len * sizeof(char));
    strcpy(str4, str); //先通过strcpy函数赋值
    printf("before strcat str4: %s, len: %lu\n", str4, strlen(str4));
    strcat(str4, ch0); //再追加
    printf("after strcat str4: %s, len: %lu\n", str4, strlen(str4));
    //free(str4);

    char append[] = "Suheng Children";
    //不断向str4追加内容，但str4的内存空间并不足以显示那么多内容，此时要需要为str4扩容
    str4 = (char*) realloc(str4, (str3len + strlen(append)) * sizeof(char)); //realloc重新分配堆内存空间
    strcat(str4, "Suheng Children");
    printf("over str4: %s, len: %lu\n", str4, strlen(str4));

    free(str4); //务必使用free()函数释放内存

    return 0;
}
