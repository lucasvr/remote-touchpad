#include <stdio.h>
#include <errno.h>
#include <sys/socket.h>
#include <resolv.h>
#include <arpa/inet.h>
#include <errno.h>
#include <strings.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <X11/X.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>
// c++
#include <string>
#include <sstream>
#include <vector>
#include <iostream>

#define MY_PORT		5000
#define MAXBUF		1024 * 1024

using namespace std;


typedef enum mouse_states {
	MOUSE_STATES_NONE,
	MOUSE_STATES_START_MOVE,
	MOUSE_STATES_MOVE,
	MOUSE_STATES_END_MOVE,
	MOUSE_STATES_RIGHT_CLICK,
	MOUSE_STATES_LEFT_CLICK
} mouse_state;

/*
static int mouse_pos_dx;
static int mouse_pos_dy;

static int refference_x;
static int refference_y;*/
static mouse_state state;
static std::vector<pair<float, float> > positions;

static Display *display = NULL;
static Window root;


static void process_and_update_mouse_position(std::vector<std::pair<float, float> > &positions) {
	/*for (auto it : positions) {
		std::cout << "*[" << it.first << " " << it.second << "]\n";
		//mouseClick(Button1);
	}*/
	//XSelectInput(dpy, root_window, KeyReleaseMask);

	for (size_t  i = 1; i < positions.size(); ++i) {
		float dx = positions[i].first;// - positions[i - 1].first;
		float dy = positions[i].second;// - positions[i - 1].second;

		std::cout << "should move with " << dx << " " << dy << "\n";
		XWarpPointer(display, None, root, 0, 0, 0, 0, dx, dy);
		XFlush(display);
	}
	positions.clear();
}


static int execute_raw_input(std::string& raw_data) {
	int ret = 0; // success

	switch (state)
	{
	case MOUSE_STATES_NONE:
		if (raw_data.find("start") != std::string::npos) {
			state = MOUSE_STATES_START_MOVE;
		} else if (raw_data.find("right") != std::string::npos) {

		}if (raw_data.find("left") != std::string::npos) {

		}
		break;
	case MOUSE_STATES_START_MOVE:
		if (raw_data == "end") {
			state = MOUSE_STATES_NONE;
			process_and_update_mouse_position(positions);
		} else {
			std::stringstream ss(raw_data);

			float x, y;
			while(!ss.eof()) {
				ss >> x >> y;
				positions.push_back(make_pair(x, y));
			}
		}
		break;
	default:
		break;
	}
	return ret;
}

int main(int Count, char *Strings[])
{   int sockfd;
	struct sockaddr_in self;
	char buffer[MAXBUF];

	display = XOpenDisplay(0);
	root = DefaultRootWindow(display);

	/*---Create streaming socket---*/
    if ( (sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0 )
	{
		perror("Socket");
		exit(errno);
	}

	/*---Initialize address/port structure---*/
	bzero(&self, sizeof(self));
	self.sin_family = AF_INET;
	self.sin_port = htons(MY_PORT);
	self.sin_addr.s_addr = INADDR_ANY;

	/*---Assign a port number to the socket---*/
    if ( bind(sockfd, (struct sockaddr*)&self, sizeof(self)) != 0 )
	{
		perror("socket--bind");
		exit(errno);
	}

	int enable = 1;
	if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &enable, sizeof(int)) < 0)
	{
		perror("socket--listen");
		exit(errno);
	}


	/*---Make it a "listening socket"---*/
	if ( listen(sockfd, 20) != 0 )
	{
		perror("socket--listen");
		exit(errno);
	}

	/*---Forever... ---*/
	//while (1)
	{
		int clientfd;
		struct sockaddr_in client_addr;
		socklen_t addrlen=sizeof(client_addr);


		/*---accept a connection (creating a data pipe)---*/
		printf("[SERVER] Waiting for clients\n");
		clientfd = accept(sockfd, (struct sockaddr*)&client_addr, &addrlen);
		printf("%s:%d connected\n", inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));

		int byte_cnt = 1;
		int i = 0;
		while(byte_cnt && ++i < 250) {
			byte_cnt = recv(clientfd, buffer, MAXBUF, 0);
			printf("[SERVER] Received from the client: [%s]sizeofvector: [%zd]\n", buffer, positions.size());
			std::string buf(buffer);
			execute_raw_input(buf);
			bzero(buffer, sizeof(buffer));
		}
		/*---Close data connection---*/
		close(clientfd);
	}
	close(sockfd);
	return 0;
}
