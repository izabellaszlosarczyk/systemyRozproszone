#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/un.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <errno.h>
#include <time.h>
#include <signal.h>
#include <pthread.h>
#include <poll.h>
#include <sys/ioctl.h>
#include <netdb.h>

#define PORT 12345
#define GROUPA "225.0.0.37"

#define MAX_LICZBA_KLIENTOW 10
#define MAX_DLUGOSC_NAZWY 20
#define MAX_ROZMIAR_WIADOMOSCI 20
#define ROZMIAR_DATA 27

struct wiadomosc {
	char loginKlienta[MAX_DLUGOSC_NAZWY];
	char tresc[MAX_ROZMIAR_WIADOMOSCI];
	unsigned char* checksum;
	char data[ROZMIAR_DATA];
};

char* login = "";

char* port;
char* grupa;

char *czasWiadomosci(){
	time_t rawtime;
  	struct tm * timeinfo;
  	char *data = malloc(1024);
 	time ( &rawtime );
  	timeinfo = localtime ( &rawtime );
  	strcpy(data, asctime (timeinfo));
  	data[ROZMIAR_DATA] = '0';

  	return data;

}

//tworzy sume kontrolna , wykorzystanie algorytmu fletcher16
uint16_t tmpSuma(uint8_t *data, int count) {
 	uint16_t sum1 = 0;
 	uint16_t sum2 = 0;
 	int index;

 	for( index = 0; index < count; ++index ) {
 		sum1 = (sum1 + data[index]) % 255;
		sum2 = (sum2 + sum1) % 255;
	}

	return (sum2 << 8) | sum1;
}


unsigned char* generujSume(char login[MAX_DLUGOSC_NAZWY], char data[ROZMIAR_DATA], char tresc[MAX_ROZMIAR_WIADOMOSCI]) {
	unsigned char suma[MAX_ROZMIAR_WIADOMOSCI + ROZMIAR_DATA + MAX_DLUGOSC_NAZWY] = "";
	strcat((char *)suma, login);
	strcat((char *)suma, data);
	strcat((char *)suma, tresc);

	uint16_t obliczonaSuma = tmpSuma(suma, strlen(suma));
	char *buf = malloc (6);
	sprintf (buf, "%u", obliczonaSuma);

	return buf;
}

// proces- lekki watek odpowiedzialny za odczytywanie wiadomosci
void odczytywanie() {

    u_int flaga = 1;
    struct wiadomosc odebrana;
    struct sockaddr_in adrGrupa;
    int socketOdczyt, nbytes, dlugoscStruktury;
    struct ip_mreq mreq;           


    if ((socketOdczyt = socket(AF_INET,SOCK_DGRAM,0)) < 0) {
		perror("socket");
		exit(1);
    }

    // wiele socketow na ten sam nr portu
    if (setsockopt(socketOdczyt, SOL_SOCKET, SO_REUSEADDR, &flaga, sizeof(flaga)) < 0) {
    	perror("Reusing ADDR failed");
    	exit(1);
    }

    memset(&adrGrupa,0,sizeof(adrGrupa));
    adrGrupa.sin_family = AF_INET;
    adrGrupa.sin_addr.s_addr = htonl(INADDR_ANY); 
    adrGrupa.sin_port = htons(PORT);

    
    mreq.imr_multiaddr.s_addr = inet_addr(GROUPA);
    mreq.imr_interface.s_addr = htonl(INADDR_ANY);

    if (bind(socketOdczyt,(struct sockaddr *) &adrGrupa,sizeof(adrGrupa)) < 0) {
		perror("bind");
	 	exit(1);
    }
     
    // zapytanie o dolaczenie do multicastowej grupy
    if (setsockopt(socketOdczyt, IPPROTO_IP, IP_ADD_MEMBERSHIP, &mreq, sizeof(mreq)) < 0) {
		perror("setsockopt");
		exit(1);
    }
    int tmp = 0;
    // petla do czytania 
    while (1) {

		dlugoscStruktury = sizeof(adrGrupa);
		if (recvfrom(socketOdczyt,&odebrana,sizeof(struct wiadomosc),0,(struct sockaddr *) &adrGrupa, &dlugoscStruktury) < 0) {
	    	perror("recvfrom");
	    	exit(1);
	 	}else if (tmp == 0){
	 		if (strcmp(generujSume(odebrana.loginKlienta, odebrana.data, odebrana.tresc), odebrana.checksum)){
	  			printf("suma kontrolna nie zgadza sie!");
	  			tmp = 1;
	  		}
	 	}
  		printf("------->%s", odebrana.data);
		printf("%s pisze:", odebrana.loginKlienta);
		printf("%s\n", odebrana.tresc);
    }
}

void wysylanie() {
    struct pollfd fds2[1]; // do czytania stdin
	fds2[0].fd = 0; // czekanie na stdin
	fds2[0].events = POLLIN | POLLPRI;

    struct sockaddr_in addresWysl;
    int socketWysl, cnt;
    struct wiadomosc wyslana;

    if ((socketWysl = socket(AF_INET,SOCK_DGRAM,0)) < 0) {
		perror("socket");
		exit(1);
    }

    memset(&addresWysl, 0, sizeof(addresWysl));
    addresWysl.sin_family = AF_INET;
    addresWysl.sin_addr.s_addr = inet_addr(GROUPA);
    addresWysl.sin_port = htons(PORT);
     
    // petla zczytujaca wiadomosc i wysylajaca
    while (1) {
		int ret = poll(fds2, 1, -1);
		if (ret > 0){
			if (fds2[0].revents & ( POLLERR | POLLHUP | POLLNVAL )){
				perror("blad eventu czytajacego");
				exit(0);
			}
			if (fds2[0].revents & (POLLIN | POLLPRI)){
				int przeczytane;
				przeczytane = read(0, wyslana.tresc, MAX_ROZMIAR_WIADOMOSCI);
				if (przeczytane < 0){
					perror("read blad");
					exit(0);
				}
				else if (strncmp(wyslana.tresc, "exit", 4)== 0){
					exit(0);
				}
				else {
					strcpy(wyslana.loginKlienta, login);
					char *data;
					data = czasWiadomosci();
					wyslana.checksum = generujSume(login, data, wyslana.tresc);
					strcpy(wyslana.data, data);
					printf("------->%s", wyslana.data);
					printf("%s pisze:", wyslana.loginKlienta);
					printf("%s\n", wyslana.tresc);
					
					if (sendto(socketWysl,&wyslana,sizeof(wyslana), 0, (struct sockaddr *) &addresWysl, sizeof(addresWysl)) < 0) {
	       				perror("sendto"); 
	       				exit(1);
	  				}
				}
				
			}	
		}
     }
}

int main(int argc, char **argv)
{
	if (argc != 2) {
		printf("argumenty programu : <login> \n");
		exit(EXIT_FAILURE);
	}
	login = argv[1];
	pid_t pid;
    pid = fork();
    if(pid==0) {
        odczytywanie();
    } else {
        wysylanie();
        wait();
    }

   return 0;
}
