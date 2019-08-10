#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <curl/curl.h>
#include <stdbool.h>

const int MAX_WORDS = 100000;
const int MAX_CHAR= 100;

char* FindMostRepeated(char** words, int listLength)
{
    char **mostRepeated;
    int *n;
    int i = 0, j = 0, max = 0;
    n = (int *)calloc(listLength, sizeof(int));
    for(i = 0; i < listLength; i++)
    {
        for(j = 0; j < i; j++)
        {
            if(strcmp(words[i], words[j]) == 0)
                n[i]++;
        }
    }
    for(i = 0; i < listLength; i++)
    {
        if(max < n[i])
            max = n[i];
    }

    j = 0;
    mostRepeated = (char**)malloc(listLength * sizeof(char*));
    for(i = 0; i < listLength; i++)
    {
        if (max == n[i])
        {
            mostRepeated[j] = (char*) malloc((strlen(words[i]) + 1) * sizeof(char));
            strcpy(mostRepeated[j], words[i]);
            j++;
        }
    }
    for(i = 0; i < j - 2; i++)
    {
        if(strcmp(mostRepeated[i], mostRepeated[i + 1]) < 0)
        {
            strcpy(mostRepeated[i + 1], mostRepeated[i]);
        }
    }
    return mostRepeated[i - 1];
}

int words_w_maxlen(char **words, int listLength) //motemayez
{
    int result = 0;
    int maxlen = 0;
    char **mlwords; //words with maximum length
    mlwords = (char**)calloc(listLength, sizeof(char*));
    
    int i, j;
    for (i = 0; i < listLength; i++)
        if (maxlen < strlen(words[i]))
            maxlen = strlen(words[i]);
    
    for (i = 0; i < listLength; i++)
        if (strlen(words[i]) == maxlen)
        {
            bool inList = false;
            for (j = 0; j < result; j++)
                if (strcmp(words[i], mlwords[j]) == 0)
                {
                    inList = true;
                    break;
                }
            
            if (!inList)
            {
                mlwords[result] = calloc(strlen(words[i]) + 1, sizeof(char));
                strcpy(mlwords[result], words[i]);
                result++;
            }
        }
    return result;
}

int main()
{
    char **words;
    words = (char **)malloc(MAX_WORDS * sizeof(char*));
    char str[MAX_CHAR];
    int n = 0;
    int task = 0;
     //*************************************gereftane file az site va save kardane an dar output_file.txt
    curl_global_init(CURL_GLOBAL_ALL);
	CURL *myHandle;
	CURLcode result;
	myHandle = curl_easy_init();
    curl_easy_setopt(myHandle, CURLOPT_URL, "http://team46:pkzhmdatsut@www.fop-project.ir/phase0");
    FILE *output_file;
    output_file = fopen("output_file.txt", "w");
    curl_easy_setopt(myHandle, CURLOPT_WRITEDATA, output_file);

    result = curl_easy_perform(myHandle);

    if(result != CURLE_OK)
    {
        fprintf(stderr, "curl_easy_perform() failed :%sn"
                ,curl_easy_strerror(result));
    }
    curl_easy_cleanup(myHandle);
    //************************************
  
    output_file = fopen("output_file.txt", "r");
    while (fscanf(output_file, "%s", str) == 1)
    {
        words[n] = (char *)malloc((strlen(str) + 1) * sizeof(char));
        strcpy(words[n], str);
        n++;
    }

    scanf("%d", &task);
    if (task == 1)
    {
        printf("%s\n", FindMostRepeated(words, n));
    }
    else if (task == 2)
    {
        int out = 0;
        out = words_w_maxlen(words, n);
        printf("%d\n", out);
    }

    return 0;

}