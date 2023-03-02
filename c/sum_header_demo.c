#include <stdio.h>
#include "sum_header.h" //引用自定义的头文件

//sum_header_demo.c包含头文件sum_header.h且头文件的函数在源文件sum_headerx.c实现,所以要两个源文件一同编译,否则会报找不到头文件中的函数异常.
//sum方法用源文件sum_header1.c的实现,命令:gcc sum_header_demo.c sum_header1.c,
//sum方法用源文件sum_header2.c的实现,命令:gcc sum_header_demo.c sum_header2.c,

int main()
{
    printf("sum_header.h, sum: %d\n", sum(10, 11));
    return 0;
}