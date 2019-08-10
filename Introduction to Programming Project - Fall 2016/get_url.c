#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <curl/curl.h>
#define maxurl 10000

void get_page(char* url, char* file_name)
{
    curl_global_init(CURL_GLOBAL_ALL);
    CURL * myHandle;
    CURLcode result;
    myHandle = curl_easy_init();
    curl_easy_setopt(myHandle, CURLOPT_URL, url);
    FILE *file;
    file = fopen(file_name, "w");
    curl_easy_setopt(myHandle, CURLOPT_WRITEDATA, file);
    result = curl_easy_perform(myHandle);
    fclose(file);
}

void get_url()
{
    curl_global_init(CURL_GLOBAL_ALL);
    CURL * myHandle;
    CURLcode result;
    myHandle = curl_easy_init();
    curl_easy_setopt(myHandle, CURLOPT_URL, "http://team46:pkz1376hmd1377@www.fop-project.ir/news/get-urls/?phase=2");
    FILE *url_file;
    url_file = fopen("url_file.txt", "w");
    curl_easy_setopt(myHandle, CURLOPT_WRITEDATA, url_file);

    result = curl_easy_perform(myHandle);

    if(result != CURLE_OK)
    {
        fprintf(stderr, "curl_easy_perform() failed :%sn"
                ,curl_easy_strerror(result));
    }
    curl_easy_cleanup(myHandle);

}

int get_rss()
{
    get_url();
    int numOfRSS = 0;
    FILE* url_file;
    char url[maxurl], file_name[10] = "file";
    url_file = fopen("url_file.txt", "r");
    while (fscanf(url_file, "%1000s", url) == 1)
    {
        numOfRSS++;
        sprintf(file_name, "%d", numOfRSS);
        printf("%s\n", url);
        get_page(url, file_name);
    }
    return numOfRSS;
}
