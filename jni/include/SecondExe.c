#include <stdio.h>

int main (int argc, char* argv[])
{
	if (argc != 3) {
		printf("2 args needed\n");
		return 1;
	}

	printf ("Exe is %s\n", argv[0]);
	printf ("First is %s\n", argv[1]);
	printf ("Second is %s\n", argv[2]);

	return 0;
}
