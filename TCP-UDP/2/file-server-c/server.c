
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <unistd.h>

#define BUFLEN 10000


int main(int argc, char **argv) {
	int sock_fd, cli_fd, len;
	socklen_t cli_len;
	struct sockaddr_in serv_addr, cli_addr;
	char sendline[BUFLEN], recvline[BUFLEN], sendTitle[BUFLEN], sendFinal[BUFLEN];;
	int ret;

	if (argc != 3) {
		printf("argumenty programu : <port TCP> <nazwa pliku>\n");
		exit(EXIT_FAILURE);
	}
	printf("inicjalizuje server!\n");
	char* port = argv[1];
	
	char* fileName = argv[2];
	FILE *fp = fopen(fileName, "r");
	if (fp == NULL){
		perror("open");
	}

	sock_fd = socket(AF_INET, SOCK_STREAM, 0);
	if (!sock_fd) {
		perror("socket");
		exit(EXIT_FAILURE);
	}

	bzero(&serv_addr, sizeof(serv_addr));
	bzero((char*)&serv_addr, sizeof(serv_addr));

	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	serv_addr.sin_port = htons(atoi(port));


	int so_reuseaddr = 1;
	ret = setsockopt(sock_fd,SOL_SOCKET,SO_REUSEADDR,&so_reuseaddr, sizeof so_reuseaddr);
	if (ret<0) {
		perror("setsockopt");
	}

	printf("binduje\n");
	ret = bind(sock_fd, (struct sockaddr*)&serv_addr, sizeof(serv_addr));
	if (ret<0) {
		perror("bind");
	}

	listen(sock_fd, 5);
	printf("nasluchuje na klienta");
	strcpy(sendTitle,fileName);

	while (1) {
		cli_fd = accept(sock_fd, (struct sockaddr*)&cli_addr, &cli_len);
		/*
		len = recv(cli_fd, recvline, BUFLEN, 0);
		printf("received bytes: %d\n", len);
		recvline[len] = 0;
		printf("received: %s\n", recvline);
		*/
		while(fread(sendline, sizeof(char), BUFLEN, fp) > 0){
			strcpy(sendFinal,sendTitle);
			strcat(sendFinal, "\n");
			strcat(sendFinal,sendline);
			if(send(cli_fd, sendFinal, strlen(sendFinal), 0) < 0)
                {
                    perror("ERROR: send");
                }
                //bzero(sendFinal, BUFLEN);
			printf("sent bytes: %d\n", (int)strlen(sendFinal));
			printf("sent: %s\n", sendFinal);
		
		}
		close(cli_fd);
	}
	fclose(fp);
	close(sock_fd);

	return EXIT_SUCCESS;
}

