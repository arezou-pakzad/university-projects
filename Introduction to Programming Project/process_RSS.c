#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdbool.h>
#include <curl/curl.h>
#include "get_url.c"

#define maxitem 100 //max item digits
#define MAX_TITLE 1000
#define MAX_LINK 10000
#define MAX_DATE 100
#define MAX_DES 100000
#define MAX_CAT 1000
#define MAXN_CAT 20
#define maxchar 1000000

typedef struct NewsStruct News;

struct NewsStruct
{
    char title[MAX_TITLE];
    char link[MAX_LINK];
    char pubDate[MAX_DATE];
    char description[MAX_DES];
    char category[MAXN_CAT][MAX_CAT];
    int catNum;
};

void News_to_Trie(News* object, int request);
void JsonFormatPoster(News* object);
void postNews(char jsonstr[]);
void write_category(News* object, int catName);
//***************************************

bool cmp(char* str1, char* str2, int a) //str1 from a to b ?= str2
{
    int i;
    int len = strlen(str2);
    for (i = 0; i < len; i++)
        if (str1[i + a] != str2[i])
            return false;
    return true;
}

void copy(char* str1, char* str2, int a, int b) //copy str1 from str1[a] to str1[b] to str2
{
    int i;
    for (i = a; i < b; i++)
    {
        if(str1[i] == '\n' || str1[i] == '\t')
            str2[i - a] = ' ';
        else
          str2[i - a] = str1[i];
    }
    str2[i] = '\0';
}

FILE* get_item(FILE * item, int request)
{
    News* news;
    news = (News*)calloc(1, sizeof(News));
    int step = 1;
    char str[maxchar] = {'\0'};
    char tempstr[maxchar] = {'\0'};
    news -> catNum = 0;

    while(step < 5)
    {
        fgets(str, maxchar, item);
        //strcpy(tempstr, "\0");
        char tempstr[maxchar] = {'\0'};
        int i, len = strlen(str);
        for(i = 0; i < len; i++)
        {
            if (str[i] == '<')
            {
                if (step == 1 && cmp(str, "title", i + 1))
                {
                    int j = i + 1;
                    while (str[j] != '<')
                        j++;
                    copy(str, news -> title, i + 7, j);
                    //printf("title : %s\n", news -> title);
                    step++;
                    break;
                }
                else if (step == 2 && cmp(str, "link", i + 1))
                {
                    int j = i + 6;
                    if(str[j] == '\0' || str[j + 1] == '\0')
                    {
                        fgets(tempstr, maxchar, item);
                        for(i = 0; i < strlen(tempstr); i++)
                        {
                            if(tempstr[i] == 'h')
                            {
                                break;
                            }
                        }
                        copy(tempstr, news -> link, i, strlen(tempstr));
                    }
                    else
                    {
                        while (str[j] != '<' && str[j] != '\0')
                            j++;
                        copy(str, news -> link, i + 6, j);
                    }
                    step++;
                    //printf("link : %s\n", news -> link);
                    break;
                    
                }
                else if (step == 3 && cmp(str, "pubDate", i + 1))
                {
                    int j = i + 1;
                    while (str[j] != '<')
                        j++;
                    copy(str, news -> pubDate, i + 9, j);
                    //printf("pubDate : %s\n", news -> pubDate);
                    step++;
                    break;
                }
                else if (step == 4 && cmp(str, "category>", i + 1))
                {
                    int j = i + 10;
                    if(str[j] == '\0' || str[j + 1] == '\0' || str[j + 2] == '\0')
                    {
                        fgets(tempstr, maxchar, item);
                        for(i = 0; i < strlen(tempstr); i++)
                        {
                            if(tempstr[i] == 'T')
                            {
                                break;
                            }
                        }
                        
                        copy(tempstr, news -> category[news -> catNum], i + 4, strlen(tempstr) - 5);  
                    }
                    else
                    {
                        while (str[j] != ']')
                            j++;
                        copy(str, news -> category[news -> catNum], i + 19, j);
                    }
                    //printf("category : %s\n", news -> category[news -> catNum]);
                    news -> catNum++;
                    break;
                }
                else if (step == 4 && cmp(str, "description>", i + 1))
                {
                    int j = i + 13;
                    if(str[j] == '\0' || str[j + 1] == '\0' || str[j + 2] == '\0')
                    {
                        fgets(tempstr, maxchar, item);
                        fgets(tempstr, maxchar, item);
                        i = 0;
                        while ((tempstr[i] < 'a' || tempstr[i] > 'z') && (tempstr[i] < 'A' || tempstr[i] > 'Z') && (tempstr[i] < '0' || tempstr[i] > '9'))
                            i++;
                        copy(tempstr, news -> description, i, strlen(tempstr));
                        if(news -> description[strlen(news -> description) - 1] == '\n')
                            news -> description[strlen(news -> description) - 1] = '\0';
                    }
                    else
                    {
                        while (str[j] != ']')
                        j++;
                         //MODIFY DESCRIPTION
                        int k; //counter -> 0 to len
                        int c = 0; //counter : news -> description length
                        bool inTag = false;
                        int len = strlen(str);
                        for (k = 24; k < len - 2; k++)
                        {
                            if (str[k] == '<')
                            {
                                inTag = true;
                                continue;
                            }
                            if (str[k] == '>')
                            {
                                inTag = false;
                                continue;
                            }
                            if (!inTag && str[k] != ']')
                            {
                                news -> description[c] = str[k];
                                c++;
                            }
                        }
                        news -> description[c] = '\0';
                    }
                    //printf("description : %s\n", news -> description);
                    step++;
                    break;
                }
            }
        }    
    }
    if(request == 1)
    {
        //struct to JSON format
        JsonFormatPoster(news);
    }
    else if(request == 2 || request == 3 || request == 4)
    {
        //if(request == 3)
            //printf("request sent!\n");
        News_to_Trie(news, request);
    }
    // else if(request == 4)
    // {
    //     f(news);
    // }

    free(news);
    return item;
}

void RSS_Processor(char fileName[], int request)
{
    //printf("RSS_Processor\n");
    FILE * rss;
    rss = fopen(fileName, "r");
    char str[maxchar];
    printf("FILE NAME %s\n", fileName);
    while (fgets(str, maxchar, rss) != NULL)
    {
        int i, len = strlen(str);
        for (i = 0; i < len; i++)
        {
            if (str[i] == '<')
                if (cmp(str, "item", i + 1))
                {
                    rss = get_item(rss, request);
                    break;
                }
        }
        
    }

    fclose(rss);
}

void postNews(char jsonstr[])
{
    curl_global_init( CURL_GLOBAL_ALL );
    CURL *myHandle;
    CURLcode result;
    myHandle = curl_easy_init ( ) ;
    struct curl_slist *headers = NULL;
    //headers
    headers = curl_slist_append(headers, "X-Requested-With: XMLHttpRequest");
    headers = curl_slist_append(headers, "Content-Type: application/json");
    headers = curl_slist_append(headers, "charsets: utf-8");
    //posting
    curl_easy_setopt(myHandle, CURLOPT_HTTPHEADER, headers);
    curl_easy_setopt(myHandle, CURLOPT_URL, "http://team46:pkz1376hmd1377@www.fop-project.ir/news");
    curl_easy_setopt(myHandle, CURLOPT_POSTFIELDS, jsonstr);

    result = curl_easy_perform( myHandle );
    printf("sent\n");
    curl_easy_cleanup( myHandle );
}

void JsonFormatPoster(News* object)
{
    char jsonstr[maxchar];
    int i = 0;

    strcpy(jsonstr, "{");
    strcat(jsonstr, "\"title\":\"");
    strcat(jsonstr, object -> title);
    strcat(jsonstr, "\",");

    strcat(jsonstr, "\"date\":\"");
    strcat(jsonstr, object -> pubDate);
    strcat(jsonstr, "\",");

    strcat(jsonstr, "\"description\":\"");
    strcat(jsonstr, object -> description);
    strcat(jsonstr, "\",");

    strcat(jsonstr, "\"categorized\":");
    strcat(jsonstr, "false,");
    strcat(jsonstr, "\"categories\":[");
    for(i = 0; i < object -> catNum; i++)
    {
        strcat(jsonstr, "\"");
        strcat(jsonstr, object -> category[i]);
        strcat(jsonstr,"\"");
        if(i < (object -> catNum - 1) )
            strcat(jsonstr, ", ");
    }
    strcat(jsonstr, "]");
    strcat(jsonstr, "}");

     printf("%s\n", jsonstr);

    // int c = 1, d = 1;
    // for ( c = 1 ; c <= 32767 ; c++ )
    //    for ( d = 1 ; d <= 32767 ; d++ )
    //    {} 

    // curl_global_init( CURL_GLOBAL_ALL );
    // printf("sent299\n");
    // CURL *myHandle;
    // printf("sent301\n");
    // CURLcode result;
    // printf("sent303\n");
    // myHandle = curl_easy_init ( ) ;
    // printf("sent305\n");
    // struct curl_slist *headers = NULL;
    // printf("sent307\n");
    // //headers
    // headers = curl_slist_append(headers, "X-Requested-With: XMLHttpRequest");
    // printf("sent310\n");
    // headers = curl_slist_append(headers, "Content-Type: application/json");
    // printf("sent312\n");
    // headers = curl_slist_append(headers, "charsets: utf-8");
    // printf("sent314\n");
    // //posting
    // curl_easy_setopt(myHandle, CURLOPT_HTTPHEADER, headers);
    // printf("sent317\n");
    // curl_easy_setopt(myHandle, CURLOPT_URL, "http://team46:pkz1376hmd1377@www.fop-project.ir/news");
    // printf("sent319\n");
    // curl_easy_setopt(myHandle, CURLOPT_POSTFIELDS, jsonstr);
    // printf("sent321\n");

    // result = curl_easy_perform( myHandle );
    // printf("sent324\n");
    // curl_easy_cleanup( myHandle );
    // printf("sent326\n");
    
    //postNews(jsonstr);
}


void write_category(News* object, int cat)
{
    strcpy(object -> category[0], "\0");
    if(cat == 1)
        strcpy(object -> category[0], "sport");
    if(cat == 2)
        strcpy(object -> category[0], "business");
    if(cat == 3)
        strcpy(object -> category[0], "entertainment");
    if(cat == 4)
        strcpy(object -> category[0], "us");
    if(cat == 5)
        strcpy(object -> category[0], "world");
    if(cat == 6)
        strcpy(object -> category[0], "health");
    if(cat == 7)
        strcpy(object -> category[0], "sci_tech");
    object -> catNum = 1;
    JsonFormatPoster(object);
}
