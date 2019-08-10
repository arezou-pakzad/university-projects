#include <stdio.h>
#include <string.h>

unsigned long line_count;
unsigned long word_count;
unsigned long char_count;

void counter (FILE * stream, char * streamName){
    unsigned long lines = 0;
    unsigned long words = 0;
    unsigned long chars = 0;

    char c;
    char last_char = ' ';

    if (stream){
        while ((c = getc(stream)) != EOF) {
            if (c == '\n')
                lines++;
            if (c == '\n' || c == ' ' || c == '\t')
                if (last_char != ' ' && last_char != '\n')
                    words++;
            chars++;
            last_char = c;
        }
        //count last word
        if (chars > 1){
            if (last_char != ' ' && last_char != '\n')
                words++;
        }
    }
    printf("%lu %lu %lu %s\n", lines, words, chars, streamName);
    line_count += lines;
    word_count += words;
    char_count += chars;
}


int main(int argc, char *argv[]) {
    int i = 0;

    //iIf arg is (null) then read names from standard input
    if (!argv[1])
        counter(stdin, " ");

    //If arg is - then read names from standard input
    else if (strcmp(argv[1], "-") == 0)
        counter(stdin, "-");

    //Else: try to read from file
    else if (argc > 1){
        for (i = 1; i < argc; ++i) {
            FILE *fp = fopen(argv[i], "r");
            if (fp)
                counter(fp, argv[i]);
            else
                //if fp == null print error msg
                printf("wc: %s: No such file or directory\n", argv[i]);
        }
        //for more than one file print total num of lines, words, chars
        if (argc > 2)
            printf("%lu %lu %lu total\n", line_count, word_count, char_count);
    }

    return 0;
}
