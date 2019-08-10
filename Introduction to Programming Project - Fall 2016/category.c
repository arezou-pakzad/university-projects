#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>
#include <curl/curl.h>
#include "process_RSS.c"

#define Num_of_Categories 7

#define ALPHABET_SIZE (26)
#define CHAR_TO_INDEX(c) ((int)c - (int)'a')

typedef struct TrieNode 
{
    struct TrieNode *children[ALPHABET_SIZE];
    bool isLeaf;
    int num;//tedad bari ke yek kalame dar yek category amade
    int newsNum[8];//tedad khabarayi az yek category ke yek kalame dar anha amade
    //float cat_totalNews
    bool flag;
    double sd;
    //double IDF;    
}TrieNode;

typedef struct keyWords
{
    char str[30];
    int newsNum;
    double IDF;
} KeyWords;

//TrieNode* catRoots[Num_of_Categories] = {NULL};
TrieNode* mergedTrie = NULL;
int total_news_number[8] = {0};
double** c;
int Nwords;
KeyWords* kwords;

//Functions And Structs*****************
void insert(TrieNode *root, char *key, int catName);
int search(TrieNode *root, char *key);
void printTrie(TrieNode* root, int catName);
void deleteNodes(TrieNode* root, int min, int max);
void delete_by_SD(TrieNode* root, double maxSD);
void delete_by_ratio(TrieNode* root);
bool cmp(char* str1, char* str2, int a);
void copy(char* str1, char* str2, int a, int b);
// FILE* get_item(FILE * item);
// void RSS_Processor(char fileName[]);
int Cat_to_Num(char* category);
int check_word(TrieNode* root, char* word);
// double IDF(TrieNode* root, int i);
int findN(TrieNode* root);
int rec_cat(double* d);
void save_keywords(TrieNode* root);
int word_num(char* word, int fr, int to);
void cal_IDFs();
double cal_size(double* d);

//*****TRIE FUNCTIONS*************************
TrieNode *getNode(void)
{
    TrieNode *pNode = NULL;
 
    pNode = (TrieNode *)malloc(sizeof(TrieNode));
 
    if (pNode)
    {
        int i;
 
        pNode->isLeaf = false;
 
        for (i = 0; i < ALPHABET_SIZE; i++)
            pNode->children[i] = NULL;
    }
    pNode -> flag = 0;
    int i = 0;
    for(i = 0; i < 8; i++)
    {
        pNode -> newsNum[i] = 0;
        //pNode -> num[i] = 0;
    }
    return pNode;
}

void insert(TrieNode *root,char *key,int catName)
{
    int level;
    int length = (int)strlen(key);
    int index;
    TrieNode *pCrawl = root;
    //printf("%d\n", length);
    for(level = 0; level < length; level++)
    {
        index = CHAR_TO_INDEX(key[level]);
        if (!pCrawl->children[index])
        {
            pCrawl->children[index] = getNode();
        }
        pCrawl = pCrawl->children[index];

    }
    if(pCrawl -> flag == false)
    {
        pCrawl -> flag = true;
        pCrawl -> newsNum[catName]++;
        pCrawl -> newsNum[0]++;
    }
    //pCrawl -> num[catName]++;
    pCrawl -> isLeaf = true;
}

void reset_flag(TrieNode* root)
{
    root -> flag = false;
    int i = 0;
    for (i = 0; i < ALPHABET_SIZE; i++)
        if (root -> children[i] != NULL)
            reset_flag(root -> children[i]);
}

void printTrie(TrieNode* root, int catName)
{
    static char str[30] = {'\0'};
    static int cnt = 0;
    if (root -> newsNum[catName] > 0)
    {
        str[cnt] = '\0';
        printf("%25s\ns", str);
    }
    for (int i = 0; i < ALPHABET_SIZE; i++)
    {
        str[cnt] = i + 'a';
        cnt++;
        if (root -> children[i] != NULL)
        {
            printTrie(root -> children[i], catName);
        }
        cnt--;
    }
}
//************************************************

int Cat_to_Num(char* category)
{
    if(strcmp(category, "sport") == 0)
        return 1;
    if(strcmp(category, "business") == 0)
        return 2;
    if(strcmp(category, "entertainment") == 0)
        return 3;
    if(strcmp(category, "us") == 0)
        return 4;
    if(strcmp(category, "world") == 0)
        return 5;
    if(strcmp(category, "health") == 0)
        return 6;
    if(strcmp(category, "sci_tech") == 0)
        return 7;
    return 0;

}

void News_to_Trie(News* object, int request)
{
    int catName = Cat_to_Num(object -> category[0]);
    int i = 0, j = 0;
    char word[30];
    int size = (int)strlen(object -> title);

    if(request == 3 || request == 4)
    {
        int* TF;
        double* d;
        TF = (int*)calloc(Nwords, sizeof(int));
        d = (double*)calloc(Nwords, sizeof(int));

        while(i < size)
        {
            if((object -> title[i] < 'a' || object -> title[i] > 'z') && (object -> title[i] < 'A' || object -> title[i] > 'Z'))
            {
                word[j] = '\0';
                if (j > 0 && strlen(word) > 1)
                {
                    int n = 0;
                    n = word_num(word, 0, Nwords - 1); //works like binary search
                    if(n >= 0)
                        TF[n]++;
                }
                j = 0;
                i++;
                continue;
            }
            if(object -> title[i] >= 'A' && object -> title[i] <= 'Z')
            {
                word[j] = object -> title[i] + ('a' - 'A');
            }
            else if(object -> title[i] >= 'a' && object -> title[i] <= 'z')
            {
                word[j] = object -> title[i];
            }
            i++;
            j++;
        }

        for(j = 0; j < 30; j++)
            word[j] = '\0';
        i = 0;
        j = 0;
        size = strlen(object -> description);

        while(i < size)
        {
            if((object -> description[i] < 'a' || object -> description[i] > 'z') && (object -> description[i] < 'A' || object -> description[i] > 'Z'))
            {
                word[j] = '\0';
                if (j > 0 && strlen(word) > 1)
                {
                    
                    int n = 0;
                    n = word_num(word, 0, Nwords - 1);
                    if(n >= 0)
                        TF[n]++;
                    
                }
                j = 0;
                i++;
                continue;
            }
            if(object -> description[i] >= 'A' && object -> description[i] <= 'Z')
            {
                word[j] = object -> description[i] + ('a' - 'A');
            }
            else if(object -> description[i] >= 'a' && object -> description[i] <= 'z')
            {
                word[j] = object -> description[i];
            }
            i++;
            j++;
        }
        
        for(i = 0; i < Nwords; i++)
        {
            d[i] = (double)TF[i] * kwords[i].IDF;
            if(request == 3)
            {
                if(catName == 0)
                    return;
                c[catName - 1][i] += d[i];
            }
            // else if(request == 4)
            // {
            //     int cat = rec_cat(d);
            //     //write_category(object, cat);
            // }
        }
        if (request == 4)
        {
            int cat = rec_cat(d);
            write_category(object, cat);
        }
        // free(TF);
        // free(d);
    }
    else if (request == 2)
    {
        while(i < size)
        {
            if((object -> title[i] < 'a' || object -> title[i] > 'z') && (object -> title[i] < 'A' || object -> title[i] > 'Z'))
            {
                word[j] = '\0';
                if (j > 0 && strlen(word) > 1)
                    insert(mergedTrie, word, catName);
                j = 0;
                i++;
                continue;
            }
            if(object -> title[i] >= 'A' && object -> title[i] <= 'Z')
            {
                word[j] = object -> title[i] + ('a' - 'A');
            }
            else if(object -> title[i] >= 'a' && object -> title[i] <= 'z')
            {
                word[j] = object -> title[i];
            }
            i++;
            j++;
        }

        for(j = 0; j < 30; j++)
            word[j] = '\0';
        i = 0;
        j = 0;
        size = strlen(object -> description);

        while(i < size)
        {
            if((object -> description[i] < 'a' || object -> description[i] > 'z') && (object -> description[i] < 'A' || object -> description[i] > 'Z'))
            {
                word[j] = '\0';
                if (j > 0 && strlen(word) > 1)
                    insert(mergedTrie, word, catName);
                j = 0;
                i++;
                continue;
            }
            if(object -> description[i] >= 'A' && object -> description[i] <= 'Z')
            {
                word[j] = object -> description[i] + ('a' - 'A');
            }
            else if(object -> description[i] >= 'a' && object -> description[i] <= 'z')
            {
                word[j] = object -> description[i];
            }
            i++;
            j++;
        } 

        total_news_number[catName]++;
        total_news_number[0]++;
        reset_flag(mergedTrie); 
    }
    
}

// DELETE FUNCTIONS:*******************
void deleteNodes(TrieNode* root, int min, int max) //less that min, or larger than max
{
    for (int i = 0; i < ALPHABET_SIZE; i++)
    {
        if (root -> isLeaf && (root -> newsNum[0] < min || root -> newsNum[0] > max))
        {
            for (int catName = 0; catName < Num_of_Categories + 1; catName++)
            {
                //root -> num[catName] = 0;
                root -> newsNum[catName] = 0;
            }
            root -> isLeaf = false;
        }
        if (root -> children[i] != NULL)
        {
            deleteNodes(root -> children[i], min, max);
        }
    }
}

void delete_by_SD(TrieNode* root, double maxSD)
{
    int i = 0;
    if (root -> isLeaf)
    {
        double ratio[8] = {0}, SD = 0.;
        for(i = 1; i < Num_of_Categories + 1; i++)
        {
            ratio[i] = (double)(root -> newsNum[i]) / total_news_number[0];
            ratio[0] += ratio[i];
        }
        ratio[0] /= (double)Num_of_Categories;
        for(i = 1; i < Num_of_Categories + 1; i++)
        {
            SD += pow(ratio[i] - ratio[0], 2); 
        }
        SD /= ratio[0];
        SD = sqrt(SD);
        if(SD < maxSD)
        {
            for (i = 0; i < Num_of_Categories + 1; ++i)
            {
                //root -> num[i] = 0;
                root -> newsNum[i] = 0;
            }
            root -> isLeaf = false;
        }
        root -> sd = SD;
    }
    else
        root -> sd = 0;
    for(i = 0; i < ALPHABET_SIZE; i++)
    {
        if(root -> children[i] != NULL)
        {
            delete_by_SD(root -> children[i], maxSD);
        }
    }
}

void delete_by_ratio(TrieNode* root)
{
    if (root -> isLeaf)
    {
        int max = 1;
        int i = 0;
        for(i = 2; i < Num_of_Categories + 1; i++)
        {
            if(((double)root -> newsNum[max] / total_news_number[max]) < ((double)root -> newsNum[i] / total_news_number[i]))
                max = i;
        }
        for(i = 1; i < Num_of_Categories + 1; i++)
        {
            if(max != i)
            {
                root -> newsNum[i] = 0;
                //root -> num[i] = 0;
            }
        }
    }
    for (int i = 0; i < ALPHABET_SIZE; i++)
        if (root -> children[i] != NULL)
            delete_by_ratio(root -> children[i]);
}

//******************************************

void save_keywords(TrieNode* root)
{
    static char str[30] = {'\0'};
    static int cnt = 0;
    static int j = 0;
    if (root -> isLeaf)
    {
        str[cnt] = '\0';
        strcpy(kwords[j].str, str);
        kwords[j].newsNum = root -> newsNum[0];
        j++;
    }
    for (int i = 0; i < ALPHABET_SIZE; i++)
    {
        str[cnt] = i + 'a';
        cnt++;
        if (root -> children[i] != NULL)
        {
            save_keywords(root -> children[i]);
        }
        cnt--;
    }
}

int word_num(char* word, int fr, int to) //find word from index fr to index to
{
    if (fr <= to)
    {
        int mid = (fr + to) / 2;
        if (strcmp(word, kwords[mid].str) == 0)
            return mid;
       else if (strcmp(word, kwords[mid].str) < 0)
            return word_num(word, fr, mid - 1);
        else if (strcmp(word, kwords[mid].str) > 0)
            return word_num(word, mid + 1, to);
    }
    return -1;
}

void cal_IDFs()
{
    int i;
    for (i = 0; i < Nwords; i++)
        kwords[i].IDF = log10((double)total_news_number[0] / kwords[i].newsNum);
}


// DECIDE THE CATEGORY D NEWS BELONGS TO 
double cal_size(double* d)
{
    double d_size = 0;
    for (int i = 0; i < Nwords; i++)
        d_size += pow(d[i], 2);
    d_size = sqrt(d_size);
    return d_size;
}

int rec_cat(double* d)
{
    double min_cat, max_cosang = 0; //max cos(angle) = min angle
    double d_size = cal_size(d);
    for (int i = 0; i < Num_of_Categories; i++)
    {
        double dot = 0;
        for (int j = 0; j < Nwords; j++)
            dot += d[j] * c[i][j];
        double cosi = dot / (cal_size(c[i]) * d_size);
        if (cosi > max_cosang)
        {
            max_cosang = cosi;
            min_cat = i;
        }
    }
    return min_cat + 1;
}
//FIND NUMBER N**************************************
int findN(TrieNode* root)
{
    int i = 0;
    static int n = 0;
    if(root -> isLeaf)
    {
        n++;
    }
    for(i = 0; i < ALPHABET_SIZE; i++)
        if(root -> children[i] != NULL)
            findN(root -> children[i]);
    return n;
}
//*************************************************


int main()
{
    int n = get_rss();
    int i;
    mergedTrie = getNode();

    char file_name[10];
    for (i = 1; i <= n; i++)
    {
        sprintf(file_name, "%d", i);
        //printf("FILE NUMBER %d\n", i);
        RSS_Processor(file_name, 2);
    }
    int min = (3 * total_news_number[0]) / 200;
    int max = (1 * total_news_number[0]) / 10;

    deleteNodes(mergedTrie, min, max);
    delete_by_SD(mergedTrie, 0.13);
    delete_by_ratio(mergedTrie);

    printTrie(mergedTrie, 0);
    
    Nwords = findN(mergedTrie);

    printf("%d\n", Nwords);
    
    kwords = (KeyWords*)malloc(Nwords * sizeof(KeyWords));
    save_keywords(mergedTrie);
    cal_IDFs();
    c = (double**)calloc(Num_of_Categories, sizeof(double*));
    for(i = 0; i < Num_of_Categories; i++)
    {
        c[i] = (double*)calloc(Nwords, sizeof(double));
    }

    for (i = 1; i <= n; i++)
    {
        sprintf(file_name, "%d", i);
        //printf("FILE NUMBER %d\n", i);
        RSS_Processor(file_name, 3);
    }

    //PHASE 3 RSS *************************************************************************************
    n = 8;//get_rss("http://team46:pkz1376hmd1377@www.fop-project.ir/news/get-urls/?phase=3");
    //printf("%d\n", n);
    for(i = 1; i <= n; i++)
    {
        sprintf(file_name, "%d", i);
        strcat(file_name, "phase3");
        printf("3 FILE NUMBER %d\n", i);
        RSS_Processor(file_name, 4);
    }
}

