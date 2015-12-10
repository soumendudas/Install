#include <string.h>
#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/inotify.h>
#include <unistd.h>
#include <time.h>

#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
//#include <signal.h>

#include "./include/http-client-c.h"


#define MEM_ZERO(pDest, destSize) memset(pDest, 0, destSize)
//LOG
#define LOG_INFO(tag, msg) __android_log_write(ANDROID_LOG_INFO, tag, msg)
#define LOG_DEBUG(tag, msg) __android_log_write(ANDROID_LOG_DEBUG, tag, msg)
#define LOG_WARN(tag, msg) __android_log_write(ANDROID_LOG_WARN, tag, msg)
#define LOG_ERROR(tag, msg) __android_log_write(ANDROID_LOG_ERROR, tag, msg)

#define EVENT_SIZE  ( sizeof (struct inotify_event) +10)
#define BUF_LEN     (  EVENT_SIZE + 64  )

//begin
static char c_TAG[] = "OnAppUninstall-1";
static jboolean b_IS_COPY = JNI_TRUE;
//static jstring dirStr;
//static jstring data;

//jstring nativeFunction(JNIEnv* env, jobject javaThis, jstring disStr, jstring data);
jstring init1( JNIEnv* env,jobject thiz, const char * dirCh, const char* imeiNum);
jstring init2( JNIEnv* env,jobject thiz, const char * dirCh, const char* imeiNum, int pingtimeInt);

void invoke_heartbeat(JNIEnv* env, jobject javaObj);
int sendingMail(JNIEnv* env, char* imei, char *msgURL);
void startInotify( JNIEnv* env, jobject thiz,char* dirCh, char* imeiNo);
void invokeExec();

//void handle_signal(int signal);
//void handle_all_signals(JNIEnv *env, const char* imeiNo);
//The method below is only to be used only for development purpose for debugging; flag=1, Debug; flag=2, Info; flag=3, Error
//void logError(JNIEnv* env, char* body, int flag);
char gImeiNo[100];
JNIEnv* gEnv;

jstring Java_com_installapp_app_InstallService_invokeNativeFunction(JNIEnv* env, jobject javaThis, jstring dirStrGlobal, jstring dataGlobal,
		jint sdkVer, jint lollipopVer, jint pingtime)
{
	jstring tag = (*env)->NewStringUTF(env, c_TAG);
	LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY), (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "init OK-1"), &b_IS_COPY));
	//	LOG_INFO((*env)->GetStringUTFChars(env, tag, &b_IS_COPY), (*env)->GetStringUTFChars(env, dirStrGlobal, &b_IS_COPY));

	const char* dirCh = (*env)->GetStringUTFChars(env, dirStrGlobal, &b_IS_COPY);
	const char* imeiNum = (*env)->GetStringUTFChars(env, dataGlobal, &b_IS_COPY);

	int sdkVerint = (int)sdkVer;
	int lolliVerInt = (int)lollipopVer;
	int pingtimeInt = (int)pingtime;

	jstring retVal;

	if(sdkVerint<lolliVerInt)
		retVal = init1(env, javaThis, dirCh, imeiNum);
	else
		retVal = init2(env,javaThis,dirCh, imeiNum, pingtimeInt);
//	jstring retVal = init2(env, javaThis, dirCh, imeiNum);

	return retVal;
}


int sendingMail(JNIEnv* env, char* imei, char *msgURL){
	jstring tag = (*env)->NewStringUTF(env, c_TAG);
	LOG_INFO((*env)->GetStringUTFChars(env, tag, &b_IS_COPY), (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "uninstalled - sending mail\nURL"), &b_IS_COPY));

	char *urlStr = "http://203.92.40.134:8080/webApp/rest/ws/uninstalled/";
	char *endURLStr = "/0/0";
	char *endURL = malloc(strlen(msgURL)+20);
	strcpy(endURL,msgURL);
	strcat(endURL, endURLStr);

	char *completeUrl = malloc(strlen(urlStr)+strlen(endURL)+strlen(imei)+1);
	strcpy(completeUrl,urlStr);
	//	sprintf(completeUrl, "%s", urlStr);
	strcat(completeUrl, imei);
	strcat(completeUrl, endURL);

	LOG_INFO((*env)->GetStringUTFChars(env, tag, &b_IS_COPY), (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, completeUrl), &b_IS_COPY));
	//	logError(env, completeUrl,2);

	/*char cmd[1000];  // to hold the command.
	memset (cmd, 0, 1000);

	char imei_id[] = "1234567890123456";


	char postcmd [1000];
	sprintf (postcmd, "imeiId=%s", imei_id);*/

	//	LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY), (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "uninstalled11 - before execute-NULL"), &b_IS_COPY));
	//	logError(env, "uninstalled11 - before execute-NULL",1);

	//		struct http_response *hresp1 =  http_get("http://www.google.com", "User-agent:MyUserAgent\r\n");
	//		struct http_response *hresp1 =  http_get("http://www.google.com", NULL);
	//		struct http_response *hresp1 =  http_get("http://203.92.40.134:8080/webApp/imeistatus.jsp", "imei:354153069652583\r\n");
	struct http_response *hresp1 =  http_get(completeUrl, NULL);

	http://203.92.40.134:8080/webApp/imeistatus.jsp?imei=354153069652583

	LOG_INFO((*env)->GetStringUTFChars(env, tag, &b_IS_COPY), (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "uninstalled11 - before execute-2"), &b_IS_COPY));
	//	logError(env, "uninstalled11 - before execute-2",1);

	char *getResp = "Response status for get req  1 is - ";
	int getStatus = hresp1->status_code_int;
	char getStatusCh[10];
	memset(getStatusCh,0,10);
	sprintf(getStatusCh,"%d",getStatus);
	char *getStatusTxt = hresp1->status_text;

	char *respStr=malloc(strlen(getResp)+strlen(getStatusTxt)+strlen(getStatusCh)+1);
	strcpy(respStr,getResp);
	strcat(respStr,getStatusCh);
	strcat(respStr,getStatusTxt);
	//		sprintf()

	//		printf ("Response status for get req  1 is %d[%s]\n", hresp1->status_code_int, hresp1->status_text);
	//	LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY), (*env)->GetStringUTFChars(env, respStr, &b_IS_COPY));
	LOG_INFO((*env)->GetStringUTFChars(env, tag, &b_IS_COPY), (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, respStr), &b_IS_COPY));
	//	logError(env, respStr,1);
	/*hresp2 = http_post("http://www.google.com/login.php", "User-agent:MyuserAgent\r\n", "username=Kirk&password=lol123");
		printf ("Response status code for post req 2 is %d[%s]\n", hresp2->status_code_int, hresp2->status_text);
		hresp3 = http_post("http://www.google.com/login.php", "User-agent:MyuserAgent\r\n", postcmd);
		printf ("Response status code for post req 3 is %d[%s]\n", hresp3->status_code_int, hresp3->status_text);*/
	//	free (hresp1);
	//		free (hresp2);
	//		free (hresp3);



	//	system("clear");
	//	system(cmd);     // execute it.

	return (0);
}


jstring init1( JNIEnv* env, jobject thiz, const char * dirStr, const char* imeiNum)
{
	//this method is used iff OS is 4.X. For version >= 5.0
	//observing using inotify event
	char dirpath[] = "/data/data/";
	//	char dirpath[] = "/data/app-lib/";
	char *dirCh = malloc(strlen(dirStr)+1+strlen(dirpath));
	char *imeiNo = malloc(strlen(imeiNum)+1);
	strcpy(dirCh, dirpath);
	strcat(dirCh,dirStr);
	strcpy(imeiNo, imeiNum);
	jstring tag = (*env)->NewStringUTF(env, c_TAG);

	LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
			, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "init OK-1-main process-on Reboot!!!"), &b_IS_COPY));

	LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
			, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, dirCh), &b_IS_COPY));


	//fork
	//	char pidChar[20];
	//	memset(pidChar,0,20);

	char* pidChar = malloc (100*sizeof(char));
	memset (pidChar, 0, 100);

	pid_t pid = fork();
	sprintf(pidChar,"%d",pid);
	if (pid < 0)
	{
		//log
		LOG_ERROR((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
				, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "fork failed !!!"), &b_IS_COPY));
		sendingMail(env,imeiNo, "/FORK_FAILED");
	}
	else if (pid == 0)
	{
		pid_t pidChild = fork();
		if(pidChild<0){
			LOG_ERROR((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
							, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "child fork failed !!!"), &b_IS_COPY));
					sendingMail(env,imeiNo, "/FORK_FAILED");
		}
		else if(pidChild>0){
					LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
									, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "Exiting child, grandchild orphaned before sleep !!!"), &b_IS_COPY));
		//					sendingMail(env,imeiNo, "/FORK_FAILED");
					exit(0);
		}
		else if(pidChild==0){
			setpgrp();
			startInotify( env, thiz,dirCh, imeiNo);
			char grp[50];
			sleep(10);
			sprintf(grp,"after sleep %d",getppid());
			LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
										, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, grp), &b_IS_COPY));
//								sendingMail(env,imeiNo, "/FORK_FAILED");
		}


	}
	else
	{
		LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
				, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "ELSE"), &b_IS_COPY));

		char pidCh2[50];
		sprintf(pidCh2,"%d",getpgrp());

		LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
						, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, pidCh2), &b_IS_COPY));

	}

	/*if(pid==0)
	{
		invoke_heartbeat(env,thiz);
		exit(0);
	}*/

	return (*env)->NewStringUTF(env, pidChar);
}


void startInotify( JNIEnv* env, jobject thiz,char* dirCh, char* imeiNo){
	jstring tag = (*env)->NewStringUTF(env, c_TAG);
	LOG_INFO((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
					, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "----Called before uninstall after setpgid!!!----"), &b_IS_COPY));
	//		setpgid(0,0);
			/*pid_t pidNo = setpgrp();
			char pidChVal[100];
			memset(pidChVal, 0, 100);
			sprintf(pidChVal,"pid is %d",pidNo);*/
	/*
			LOG_INFO((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
							, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, pidChVal), &b_IS_COPY));*/

			char pidCh1[50];
			sprintf(pidCh1, "get pid is %d", getpgrp());

			LOG_INFO((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
									, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, pidCh1), &b_IS_COPY));

			int fileDescriptor = inotify_init();
			if (fileDescriptor < 0)
			{
				LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
						, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "inotify_init failed !!!"), &b_IS_COPY));
				sendingMail(env,imeiNo, "/INOTIFY_INIT1_FAILED");

				exit(1);
			}

			int watchDescriptor;
			watchDescriptor = inotify_add_watch(fileDescriptor, dirCh, IN_CREATE|IN_MODIFY|IN_DELETE);
			if (watchDescriptor < 0)
			{
				LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
						, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "inotify_add_watch failed !!!"), &b_IS_COPY));
				sendingMail(env,imeiNo, "/inotify_add_watch_failed_init1");

				exit(1);
			}

			LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
					, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "start observer"), &b_IS_COPY));

			char buffer[BUF_LEN];
			size_t readBytes;

			int count=0;

			while(1){
				count++;
				readBytes = read(fileDescriptor, buffer, BUF_LEN);
				//			readBytes = read(fileDescriptor, p_buf, sizeof(struct inotify_event));
				struct inotify_event *event = (struct inotify_event *) buffer;
				LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
						, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "Hello Name = "), &b_IS_COPY));
				if ( event->len ) {
					LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
													, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, event->name), &b_IS_COPY));

					if ( event->mask & IN_DELETE ) {
						if ( event->mask & IN_ISDIR ) {
							/*LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
									, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, event->name), &b_IS_COPY));*/
							sendingMail(env,imeiNo, "/UNINSTALLED3");
							break;
						}
						else {
							//						printf( "The file %s was deleted.\n", event->name );
							sendingMail(env,imeiNo, "/UNINSTALLED_Else3");
							break;
						}
	//					invokeExec();
					}

				}
				/*sleep(10);
				if(count==20){
					LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
							, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "breaking as count = 30"), &b_IS_COPY));
					sendingMail(env,imeiNo, "/FOUND_200S_INIT1");
					exit(0);
				}*/
			}

			inotify_rm_watch(fileDescriptor, IN_CREATE|IN_MODIFY|IN_DELETE);
			close(fileDescriptor);
			invokeExec();

			//log
			LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
					, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "***********uninstalled**************"), &b_IS_COPY));

	//		sendingMail(env, imeiNo, "/UNINSTALLED_INIT1");

			//		invoke_heartbeat(env,thiz);

			//       execlp("am", "am", "start","--user", "0" ,"-a", "android.intent.action.VIEW", "-d", "https://www.google.com", (char *)NULL);
}


jstring init2( JNIEnv* env, jobject thiz, const char * dirStr, const char* imeiNum, int pingtimeInt)
{
	//this method is used iff OS version >= 5.0
	//observing using inotify event
	char *imeiNo = malloc(strlen(imeiNum)+1);
	strcpy(imeiNo, imeiNum);
	jstring tag = (*env)->NewStringUTF(env, c_TAG);
	if(pingtimeInt==0)
	{
		pingtimeInt = 86400;
	}

	LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
			, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "init2 OK-2-main process\nComplete Dir is = "), &b_IS_COPY));


//	handle_all_signals(env, imeiNum);
	//fork
	//	char pidChar[20];
	//	memset(pidChar,0,20);

	pid_t pid = fork();
	if (pid < 0)
	{
		//log
		LOG_ERROR((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
				, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "fork failed !!!"), &b_IS_COPY));
		sendingMail(env,imeiNo, "/FORK_FAILED");
	}
	else if (pid == 0)
	{
		int count=0;
		char intCh[50];
		while(1){
			count++;
			memset(intCh,0,50);
			sprintf(intCh,"/ISALIVE%d",count);
			LOG_INFO((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
							, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, intCh), &b_IS_COPY));
			sendingMail(env,imeiNo, intCh);
			sleep(pingtimeInt);
		}

	}
	else
	{
		LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
				, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "ELSE"), &b_IS_COPY));
//		return (*env)->NewStringUTF(env, pidChar);
	}



	return (*env)->NewStringUTF(env, "from init2");
}


void invoke_heartbeat(JNIEnv* env, jobject javaObj)
{
	jstring tag = (*env)->NewStringUTF(env, c_TAG);

	jclass uninstall_Class;
	jmethodID main_method;
	char heartCh[10];
	memset(heartCh, 0, 10);
	jint heartint;

	LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
			, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "Calling Java - begin "), &b_IS_COPY));

	uninstall_Class = (*env)->GetObjectClass(env, javaObj);
	if(uninstall_Class==NULL){
		LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
				, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "CLASS IS NULL-EXITING!"), &b_IS_COPY));
	}
	//	main_method = (*env)->GetStaticMethodID(env, uninstall_Class, "detectUninstall", "(Ljava/lang/String;)Ljava/lang/String;");
	main_method = (*env)->GetMethodID(env, uninstall_Class, "heartBeat", "(I)I");
	if(main_method==NULL){
		LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
				, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "METHOD_ID IS NULL-EXITING!"), &b_IS_COPY));
		exit(1);
	}

	LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
			, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "Calling Java-!! "), &b_IS_COPY));

	if(javaObj==NULL){
		LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
				, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "javaThis IS NULL-EXITING!"), &b_IS_COPY));
		exit(1);
	}

	heartint = (*env)->CallIntMethod(env, javaObj, main_method, 2);
	LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
			, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "Called Java "), &b_IS_COPY));
	sprintf(heartCh,"%d",heartint);

	LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
			, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, heartCh), &b_IS_COPY));

	LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY)
			, (*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "Calling Java - Done "), &b_IS_COPY));
}

void invokeExec(){
	char pwd[1024];
		   if (getcwd(pwd, sizeof(pwd)) != NULL) {
		       printf("Current working dir: %s\n", pwd);
		   }
		   else {
		       perror("getcwd() error");
		       exit(1);
		    }


		    char* exeName = (char*) malloc (strlen(pwd)+100);
		    strcpy (exeName, pwd);
		    strcat (exeName, "./include/");
		    strcat (exeName, "SecondExe");

		    char *name[] = {
		        NULL,
		        "firstArg",
		        "secondArg",
		        NULL
		    };

		    name[0] = exeName;

		    printf ("CALLING THE EXE NOW \n");

		    execv(name[0], name);

		    perror("Error: ");
}

