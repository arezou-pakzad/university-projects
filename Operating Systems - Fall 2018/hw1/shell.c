#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <termios.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <fcntl.h>

#define FALSE 0
#define TRUE 1
#define INPUT_STRING_SIZE 80

#include "io.h"
#include "parse.h"
#include "process.h"
#include "shell.h"

int cmd_quit(tok_t arg[]) {
    printf("Bye\n");
    exit(0);
    return 1;
}

int cmd_help(tok_t arg[]);
int cmd_cd(tok_t arg[]);
int cmd_pwd(tok_t arg[]);
void signal_handler(int signum);
int cmd_wait(tok_t arg[]);

/* Command Lookup table */
typedef int cmd_fun_t (tok_t args[]); /* cmd functions take token array and return int */
typedef struct fun_desc {
    cmd_fun_t *fun;
    char *cmd;
    char *doc;
} fun_desc_t;

fun_desc_t cmd_table[] = {
    {cmd_help, "?", "show this help menu"},
    {cmd_quit, "quit", "quit the command shell"},
    {cmd_pwd, "pwd", "return working directory name"},
    {cmd_cd, "cd", "change directory to new path"},
    {cmd_wait, "wait", "wait for all background processes to terminate"},
};

int cmd_help(tok_t arg[]) {
    int i;
    for (i=0; i < (sizeof(cmd_table)/sizeof(fun_desc_t)); i++) {
        printf("%s - %s\n",cmd_table[i].cmd, cmd_table[i].doc);
    }
    return 1;
}

int cmd_pwd(tok_t arg[]){
    char pwd[10000];
    getcwd(pwd, sizeof(pwd));
    if (pwd != NULL){
        fprintf(stdout,"%s\n", pwd);
        return 1;
    }
    return 0;
}

int cmd_cd(tok_t arg[]){
    if (chdir(arg[0]) == -1) {
        printf("%s : not a directory\n", arg[0]);
        return 0;
    }
    return 1;
}

int cmd_wait(tok_t arg[]){
    int status;
    while (wait(&status) >= 0){
        //wait
    }

    return 1;
}

int find_file_name(char * file_name) {
    if (access(file_name, F_OK) != 0){
        char * env = getenv("PATH");
        tok_t * t = getToks(env);
        int i = 0;
        char path[1000];
        while (t[i] != NULL) {
            strcpy(path, t[i]);
            strcat(path, "/");
            strcat(path, file_name);
            if (access(path, F_OK) == 0) {
                strcpy(file_name, path);
                freeToks(t);
                return 1;
            }
            i++;
        }
        freeToks(t);
        return 0;
    }
    return 1;
}

int run_program(tok_t *arg){
    int bgp = background_process(arg);
    pid_t pid = fork();
    int status = 0;
    if (pid < 0){
        printf("\nFailed forking child..");
        return 0;
    }
    else if (pid > 0){
        if (bgp == 0) {
            if (waitpid(pid, &status, WUNTRACED) < 0){
                perror("Error in waiting for child process");
                exit(0);
            }
        }
        tcsetpgrp(STDIN_FILENO, getpgid(getpid()));
        tcsetpgrp(STDOUT_FILENO, getpgid(getpid()));
        tcsetpgrp(STDERR_FILENO, getpgid(getpid()));
    }
    else if(pid == 0) {

        setpgid(getpid(), getpid());
        if (bgp == 0){
            tcsetpgrp(STDIN_FILENO, getpgid(getpid()));
            tcsetpgrp(STDOUT_FILENO, getpgid(getpid()));
            tcsetpgrp(STDERR_FILENO, getpgid(getpid()));
            signal(SIGINT, SIG_DFL);
            signal(SIGQUIT, SIG_DFL);
            signal(SIGTSTP, SIG_DFL);
            signal(SIGTTIN, SIG_DFL);
            signal(SIGTTOU, SIG_DFL);
            signal(SIGCONT, SIG_DFL);
            signal(SIGTERM, SIG_DFL);
            signal(SIGKILL, SIG_DFL);
        }


        if (io_redirect(arg) < 0)
            exit(0);

        char filename[1000];
        strcpy(filename, arg[0]);
        if (find_file_name(filename)){
            arg[0] = filename;
            if (execv(filename, arg) == -1)
                printf("%s : not an executable program\n", arg[0]);
            exit(0);
        }
        printf("%s : not an executable program\n", arg[0]);
        exit(0);
    }
    return 1;
}

int io_redirect(tok_t *arg){
    int file_descriptor = 0;
    int i = 0;
    int in_ind = isDirectTok(arg, "<");
    int out_ind = isDirectTok(arg, ">");
    if (in_ind < 0 && out_ind < 0)
        return 1;
    if (in_ind > 0){
        if (arg[in_ind + 1] == NULL || !strcmp(arg[in_ind + 1], "<") || !strcmp(arg[in_ind + 1], ">")){
            printf("%s: Syntax error\n", arg[in_ind + 1]);
            return -1;
        }
        if ((file_descriptor = open(arg[in_ind + 1], O_RDONLY, 0)) < 0){
            printf("%s: No such file or directory\n", arg[in_ind + 1]);
            return -1;
        }
        dup2(file_descriptor, STDIN_FILENO);
        close(arg[in_ind + 1]);
        int i = in_ind;
        if (in_ind < out_ind)
            out_ind -= 2;
        while (arg[i] != NULL && arg[i + 1] != NULL) {
            arg[i] = arg[i + 2];
            i++;
        }
    }
    if (out_ind > 0) {
        if (arg[out_ind + 1] == NULL || !strcmp(arg[out_ind + 1], "<") || !strcmp(arg[out_ind + 1], ">")){
            printf("%s: Syntax error\n", arg[out_ind + 1]);
            return -1;
        }
        if ((file_descriptor = open(arg[out_ind + 1], O_WRONLY | O_CREAT | O_TRUNC, S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH)) < 0){
            printf("%s: No such file or directory\n", arg[out_ind + 1]);
            return -1;
        }
        dup2(file_descriptor, STDOUT_FILENO);
        close(arg[out_ind + 1]);
        int i = out_ind;
        while (arg[i] != NULL && arg[i + 1] != NULL) {
            arg[i] = arg[i + 2];
            i++;
        }
    }
    return 1;
}

int background_process(tok_t *t) {
    int ind = isDirectTok(t, "&");
    int i = ind;
    if (ind > 0) {
        while (t[i] != NULL) {
            t[i] = NULL;
            i++;
        }
        return 1;
    }
    return 0;
}


int lookup(char cmd[]) {
    int i;
    for (i=0; i < (sizeof(cmd_table)/sizeof(fun_desc_t)); i++) {
        if (cmd && (strcmp(cmd_table[i].cmd, cmd) == 0)) return i;
    }
    return -1;
}

void init_shell()
{
    /* Check if we are running interactively */
    shell_terminal = STDIN_FILENO;
    
    /** Note that we cannot take control of the terminal if the shell
     is not interactive */
    shell_is_interactive = isatty(shell_terminal);
    
    if(shell_is_interactive){
        
        /* force into foreground */
        while(tcgetpgrp (shell_terminal) != (shell_pgid = getpgrp()))
            kill( - shell_pgid, SIGTTIN);
        
        shell_pgid = getpid();
        /* Put shell in its own process group */
        if(setpgid(shell_pgid, shell_pgid) < 0){
            perror("Couldn't put the shell in its own process group");
            exit(1);
        }
        
        /* Take control of the terminal */
        tcsetpgrp(shell_terminal, shell_pgid);
        tcgetattr(shell_terminal, &shell_tmodes);
    }

    // ignoring signals
    signal(SIGINT, SIG_IGN);
    signal(SIGQUIT, SIG_IGN);
    signal(SIGTSTP, SIG_IGN);
    signal(SIGTTIN, SIG_IGN);
    signal(SIGTTOU, SIG_IGN);
    signal(SIGCONT, SIG_IGN);
    signal(SIGTERM, SIG_IGN);
    signal(SIGKILL, SIG_IGN);
}


int shell (int argc, char *argv[]) {
    char *s = malloc(INPUT_STRING_SIZE+1);            /* user input string */
    tok_t *t;            /* tokens parsed from input */
    int fundex = -1;
    pid_t pid = getpid();        /* get current processes PID */
    pid_t ppid = getppid();    /* get parents PID */
    pid_t cpid, tcpid, cpgid;
    
    init_shell();

//     printf("%s running as PID %d under %d\n",argv[0],pid,ppid);

    while ((s = freadln(stdin))){
        t = getToks(s); /* break the line into tokens */
        fundex = lookup(t[0]); /* Is first token a shell literal */
        if(fundex >= 0)
            cmd_table[fundex].fun(&t[1]);
        else {
            run_program(&t[0]);
        }
    }
    freeToks(t);
    freeln(s);

    return 0;
}