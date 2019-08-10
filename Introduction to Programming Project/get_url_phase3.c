#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <curl/curl.h>

int main(void)
{
    curl_global_init(CURL_GLOBAL_ALL);
    CURL * myHandle;
    CURLcode result;
    myHandle = curl_easy_init();
    curl_easy_setopt(myHandle, CURLOPT_URL, "http://team46:pkz1376hmd1377@www.fop-project.ir/news/get-urls/?phase=3");
    FILE *url_file;
    url_file = fopen("url_file_phase3.txt", "w");
    curl_easy_setopt(myHandle, CURLOPT_WRITEDATA, url_file);

    result = curl_easy_perform(myHandle);

    if(result != CURLE_OK)
    {
        fprintf(stderr, "curl_easy_perform() failed :%sn"
                ,curl_easy_strerror(result));
    }
    curl_easy_cleanup(myHandle);

}
