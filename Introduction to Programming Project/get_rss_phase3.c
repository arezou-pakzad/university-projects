#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdbool.h>
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



int main()
{
    //get_url();
    int numOfRSS = 0;
    FILE* url_file;
    char url[maxurl], file_name[10] = "file";
    url_file = fopen("url_file_phase3.txt", "r");
    while (fscanf(url_file, "%1000s", url) == 1)
    {
        numOfRSS++;
        sprintf(file_name, "%d", numOfRSS);
        strcat(file_name, "phase3");
        //printf("%s\n", url);
        get_page(url, file_name);
    }
    //return numOfRSS;
}