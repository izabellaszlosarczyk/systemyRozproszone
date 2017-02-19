#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <unistd.h>
#include <math.h>

#define BUFLEN 10000
#define ARRINIT 100
#define SCALE 100

char pi(long n){
	char* fileName = "pi.txt";
	FILE *fp = fopen(fileName, "r");
	if (fp == NULL){
		perror("open");
	}
	fseek(fp, n%1000, SEEK_SET);
	char ch = getc(fp);
	close(fp);
	return ch;
}

/*
char pi2(long n){
	long r[2800 + 1], i, k, b, d, c = 0;

    for (i = 0; i < 2800; i++) {
        r[i] = 2000;
    }

    for (k = 2800; k > 0; k -= 14) {
        d = 0;

        i = k;
        for (;;) {
            d += r[i] * 10000;
            b = 2 * i - 1;

            r[i] = d % b;
            d /= b;
            i--;
            if (i == 0) break;
            d *= i;
        }
        //printf("%.4d", c + d / 10000);
        c = d % 10000;
    }
    printf("co obliczylem %ld\n", r[n]);
    return r[n]%10 + '0';
}*/

int main(int argc, char **argv) {
	int sock_fd, cli_fd, len;
	socklen_t cli_len;
	struct sockaddr_in serv_addr, cli_addr;
	char recvline[BUFLEN];
	int ret;

	if (argc != 2) {
		printf("argumenty programu : <port TCP> \n");
		exit(EXIT_FAILURE);
	}
	printf("inicjalizuje server!\n");
	char* port = argv[1];

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
	printf("nasluchuje na klienta\n");

	while (1) {
		cli_fd = accept(sock_fd, (struct sockaddr*)&cli_addr, &cli_len);
		len = recv(cli_fd, recvline, BUFLEN, 0);
		if (len > 0 ){
			printf("cos odebralem...\n");
		}

		char result;
		if (len == 1){
			printf("zmienna całkowita 1-bajtowa");
			char tmp = (char)recvline[0];
			result = pi(tmp);
			printf("received: %c\n", tmp);
		}
		if (len == 2){
			printf("zmienna całkowita 2-bajtowa");
			short int n2;
			n2 = (short int)recvline[0] & 0xFF;
			n2 <<= 8;
			printf("-> %d ", n2);
			n2 |= (short int)recvline[1] & 0xFF;
			result = pi(n2);
			printf(" received: %d\n", n2);

		}
		if (len == 4){
			printf("zmienna całkowita 4-bajtowa");
			int n4;
			n4 = (int)recvline[0] & 0xFF;
			for (int i = 1; i < 4; i = i + 1){
				n4 <<= 8;
				n4 |= (int)recvline[i] & 0xFF;
			}
			result = pi(n4);
			printf("received: %d\n", n4);
		}
		if (len == 8){
			printf("zmienna całkowita 8-bajtowa");
			long n8;
			n8 = (long)recvline[0] & 0xFF;
			for (int k = 1; k < 8; k = k + 1){
				n8 <<= 8;
				n8 |= (int)recvline[k] & 0xFF;
			}
			result = pi(n8);
			printf("received: %ld\n", n8);
		}

		if (send(cli_fd, &result, sizeof(char), 0) < 0){
            perror("ERROR: send");
    	}	
        //bzero(sendFinal, BUFLEN);
		//printf("sent bytes: %d\n", sizeof(char));
		printf("sent: %c\n", result);
		close(cli_fd);
	}
	close(sock_fd);

	return EXIT_SUCCESS;
}

