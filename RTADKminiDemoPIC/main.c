/**************************************************************
//  file 名 :　main.c
//  概要    :  RT-ADKmini用ファームウェア
**************************************************************/


//Include files
#include "USB/usb.h"
#include "USB/usb_host_android.h"
#include "Compiler.h"
#include "HardwareProfile - PIC24FJ64GB002.h"
#include <uart.h>


// If a maximum current rating hasn't been defined, then define 500mA by default
#ifndef MAX_ALLOWED_CURRENT
	#define MAX_ALLOWED_CURRENT 			(500)		  // Maximum power we can supply in mA
#endif

// Define a debug error printing
#define DEBUG_ERROR(a)	 Nop(); Nop(); Nop();


//コンフィギュレーションの設定
_CONFIG1(WDTPS_PS1 & FWPSA_PR32 & WINDIS_OFF & FWDTEN_OFF & ICS_PGx1 & GWRP_OFF & GCP_OFF & JTAGEN_OFF)
_CONFIG2(POSCMOD_NONE & I2C1SEL_PRI & IOL1WAY_OFF & OSCIOFNC_ON & FCKSM_CSDCMD & FNOSC_FRCPLL & PLL96MHZ_ON & PLLDIV_DIV2 & IESO_OFF)
_CONFIG3(WPFP_WPFP0 & SOSCSEL_IO & WUTSEL_LEG & WPDIS_WPDIS & WPCFG_WPCFGDIS & WPEND_WPENDMEM)
_CONFIG4(DSWDTPS_DSWDTPS3 & DSWDTOSC_LPRC & RTCOSC_SOSC & DSBOREN_OFF & DSWDTEN_OFF)

// C30 and C32 Exception Handlers
// If your code gets here, you either tried to read or write
// a NULL pointer, or your application overflowed the stack
// by having too many local variables or parameters declared.

#if defined(__C30__)
	void _ISR __attribute__((__no_auto_psv__)) _AddressError(void)
	{
		DEBUG_ERROR("Address error");
		while(1){}
	}
	void _ISR __attribute__((__no_auto_psv__)) _StackError(void)
	{
		DEBUG_ERROR("Stack error");
		while(1){}
	}
#endif

//アンドロイドとやり取りするコマンド名の1byte目にはコマンド名が入る
typedef enum _ACCESSORY_DEMO_COMMANDS{
	COMMAND_SET_OUTPUTPIN       = 0x01,      //AOUTピンとDOUTピンの状態の受信用
	COMMAND_UPDATE_PUSHBUTTONS  = 0x02,      //DINピンについているスイッチの押下状況送信用
	COMMAND_UPDATE_POT0         = 0x03,      //AINのAD値送信用
	COMMAND_UPDATE_POT1         = 0x04,      
	COMMAND_UPDATE_POT2         = 0x05,      
	COMMAND_UPDATE_POT3         = 0x06,      
	COMMAND_SERVO_01            = 0x07,      //サーボモーター1,2の角度情報受信用
	COMMAND_SERVO_02            = 0x08,          
} ACCESSORY_DEMO_COMMANDS;
//アンドロイドとやり取りするコマンドパケットを定義
//このサンプルではアンドロイドと送受信するコマンドパケットのdata部分は
//全て1byteになっている
//1byte以上のdataを送受信する必要がある場合は要変更
typedef struct __attribute__((packed))
{
    BYTE command;
    BYTE data;
}ACCESSORY_APP_PACKET;

//Local prototypes
//#if defined(__C32__)
//static void InitPIC32(void);
//#endif

//プロトタイプ宣言
static void OutputPinControl(BYTE setting);
static BYTE GetPushbuttons(void);

//変数宣言
static BYTE read_buffer[64];
static ACCESSORY_APP_PACKET outgoing_packet; 
static void* device_handle = NULL;
static BOOL device_attached = FALSE;

static char manufacturer[] = "Microchip Technology Inc.";
static char model[] = "Basic Accessory Demo";
static char description[] = DEMO_BOARD_NAME_STRING;
static char version[] = "1.0";
static char uri[] = "http://www.microchip.com/android";
static char serial[] = "N/A";


unsigned int AIN0, AIN1, AIN2, AIN3;   //ADCの読み取り値を格納する変数

ANDROID_ACCESSORY_INFORMATION myDeviceInfo =
{
	manufacturer,
	sizeof(manufacturer),
	model,
	sizeof(model),
	description,
	sizeof(description),
	version,
	sizeof(version),
	uri,
	sizeof(uri),
	serial,
	sizeof(serial)
};


//////////////////////サーボモータの制御///////////////////////////////////////////
//フタバのRS301,302で動作確認

//サーボ用送信データ
BYTE trqOn[]        = {0xFA, 0xAF, 0x01, 0x00, 0x24, 0x01, 0x01, 0x01, 0x24};
BYTE torqueOn1[]    = {0xFA, 0xAF, 0x01, 0x00, 0x24, 0x01, 0x01, 0x01, 0x24};
BYTE torqueOn2[]    = {0xFA, 0xAF, 0x02, 0x00, 0x24, 0x01, 0x01, 0x01, 0x27};
BYTE rrs1[]         = {0xFA, 0xAF, 0x01, 0x00, 0x1E, 0x04, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00};
BYTE rrs2[]         = {0xFA, 0xAF, 0x02, 0x00, 0x1E, 0x04, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00};
/*----------------------------------------------------------------
// 関数名 int write_uart(char *buf, int len)
//
// 引数 char *buf, int len
//
// 返り値　int c
//
// 概要  サーボ側にデータを送る関数　　　　　　　　
//      第一引数で送信する配列を渡し,第二引数で配列の長さを渡す
------------------------------------------------------------------*/
int write_uart(char *buf,int len)
{
	int c;
        RS485_Send_EN = 1;
	for(c = 0; c < len; c++){
            WriteUART1(buf[c]);
            while(!U1STAbits.TRMT);
	}
        RS485_Send_EN = 0;
	return c;
}
/*----------------------------------------------------------------
// 関数名 void calcCheckSum(BYTE pkt[], BYTE n)
//
// 引数 BYTE pkt[], BYTE n
//
// 返り値　なし
//
// 概要  チェックサムをし配列の最後を書き換えます
------------------------------------------------------------------*/
void calcCheckSum(BYTE pkt[], BYTE n)
{ 
  	int sum = 0;
	int i;
  	for(i=2; i<n-1; i++){
    	sum = sum ^ pkt[i];
  }
  pkt[n-1]=sum;
}
/*----------------------------------------------------------------
// 関数名 void servo_test1,2(BYTE pos)
//
// 引数 BYTE pos
//
// 返り値　なし
//
// 概要  引数posはandroid側から0x00から0xffまでの値が入り中心値が零点,サーボが可動
//      角度の情報のみでスピードは制御していない
------------------------------------------------------------------*/
void servo_test1(int pos)    
{                            
	int deg;                 
    write_uart(torqueOn1,9); 
                              
	deg = (128-pos)*6;       
  	rrs1[7]= deg & 0x00FF;   //Little Endian
  	rrs1[8]= deg >> 8;       //Little Endian
  	int spd=1;               //スピードは固定
  	rrs1[9]=  spd & 0x00FF;  //Little Endian
  	rrs1[10]= spd >> 8;      //Little Endian
  	calcCheckSum(rrs1,12);   
  	write_uart(rrs1,12);     
}

void servo_test2(int pos)    
{                            
	int deg;                 
    write_uart(torqueOn2,9); 
        
    deg = (128-pos)*6;
  	rrs2[7]= deg & 0x00FF;   //Little Endian
  	rrs2[8]= deg >> 8;       //Little Endian
  	int spd=1;               //スピードは固定
  	rrs2[9]=  spd & 0x00FF;  //Little Endian
  	rrs2[10]= spd >> 8;      //Little Endian
  	calcCheckSum(rrs2,12);
  	write_uart(rrs2,12);
}
////////////OUTPUTピンの制御///////////////////////////////////
/*----------------------------------------------------------------
// 関数名 static void OutputPinControl(BYTE setting)
//
// 引数 BYTE setting
//       (1 = ON 0 = OFF)
//        bit 0 = DOUT0
//	  bit 1 = DOUT1
//	  bit 2 = DOUT2
//	  bit 3 = DOUT3
//	  bit 4 = AOUT0
//        bit 5 = AOUT1
//        bit 6 = AOUT2
//        bit 7 = DONTCARE
//
// 返り値 なし
//
// 概要　引数の各ビットに割当てられたそれぞれのOUTPUTPINをON,OFFする
------------------------------------------------------------------*/

static void OutputPinControl(BYTE setting)
{
	if((setting & 0x01) == 0x01) { DOUT0_On(); } else { DOUT0_Off(); }
	if((setting & 0x02) == 0x02) { DOUT1_On(); } else { DOUT1_Off(); }
	if((setting & 0x04) == 0x04) { DOUT2_On(); } else { DOUT2_Off(); }
	if((setting & 0x08) == 0x08) { DOUT3_On(); } else { DOUT3_Off(); }
	if((setting & 0x10) == 0x10) { AOUT0_On(); } else { AOUT0_Off(); }
	if((setting & 0x20) == 0x20) { AOUT1_On(); } else { AOUT1_Off(); }
	if((setting & 0x40) == 0x40) { AOUT2_On(); } else { AOUT2_Off(); }
}
/////////DINピンの制御////////////////////////////////////////////////////////////////////
/*----------------------------------------------------------------
// 関数名 BYTE GetPushbuttons(void)
//
// 引数 なし
//
// 返り値 BYTE　toReturn
//
// 概要  現在のDIN0,1,2,3ピンにつながっているボタンの押下状況を返す
//       toReturn
//       (ON=1,OFF=0)
//       0bit DIN0につながっているタクトスイッチ
//       1bit DIN1につながっているタクトスイッチ
//       2bit DIN2につながっているタクトスイッチ
//       3bit DIN3につながっているタクトスイッチ
//       注)タクトスイッチはプルアップ
------------------------------------------------------------------*/

static BYTE GetPushbuttons(void)
{
	BYTE toReturn;
	
	InitAllSwitches();	  

	toReturn = 0;

	if(Switch1Pressed()){toReturn |= 0x1;}
	if(Switch2Pressed()){toReturn |= 0x2;}
	if(Switch3Pressed()){toReturn |= 0x4;}
	if(Switch4Pressed()){toReturn |= 0x8;}
	
	return toReturn;
}
////////////////////////////////////////////////////////////////////////////////

//-----------------------------------------
int main(void)
{
    DWORD size;
    BOOL responseNeeded;
    BYTE pushButtonValues = 0xFF;

    unsigned int pot0Percentage = 0xFF;
    unsigned int pot1Percentage = 0xFF;
    unsigned int pot2Percentage = 0xFF;
    unsigned int pot3Percentage = 0xFF;

    BOOL buttonsNeedUpdate = FALSE;

    BOOL pot0NeedsUpdate = FALSE;
    BOOL pot1NeedsUpdate = FALSE;
    BOOL pot2NeedsUpdate = FALSE;
    BOOL pot3NeedsUpdate = FALSE;

    BOOL readInProgress = FALSE;
    BOOL writeInProgress = FALSE;
    BYTE tempValue = 0xFF;
    BYTE errorCode;

    unsigned int pll_startup_counter = 600;

    ///////////////各種初期化//////////////////////////////
    CLKDIVbits.PLLEN = 1;
    while(pll_startup_counter--);


    AD1PCFG = 0xffff;    //ポートＡを全デジタル出力に
    CLKDIV = 0x0000;     // Set PLL prescaler (1:1)

    USBInitialize(0);
    AndroidAppStart(&myDeviceInfo);

    responseNeeded = FALSE;

    //タイマ1初期化(タイマ割り込み1に使用)

    T1CON = 0b1000000000110000;
    //PR1 = 31250-1;	//500mSec
    PR1 = 6250-1;	//100mSec
    IPC0bits.T1IP = 5;	// 割り込みレベル 5
    IEC0bits.T1IE = 1; 	// Enable Int
    //各モジュールの初期化
    Init_OC();      //PWM関係初期化
    Init_AD1();     //AD1の初期化 AD割り込み使用
    Init_UART1();   //UART1はサーボモータへのデータ送信に使用
    //////////////////////////////////////////////////////
	
    while(1)
    {
            //この関数をループの最初で呼ぶ必要がある
            USBTasks();
            //デバイスが接続されていない場合は変数を初期化してwhileループの最初に戻る
            if(device_attached == FALSE)
            {
                    buttonsNeedUpdate = TRUE;
                    pot0NeedsUpdate = TRUE;
                    pot1NeedsUpdate = TRUE;
                    pot2NeedsUpdate = TRUE;
                    pot3NeedsUpdate = TRUE;
                    //ピンの初期化を行う
                    InitOutPutPin();

                    continue;
            }

            if(readInProgress == FALSE)
            {
                    errorCode = AndroidAppRead(device_handle, (BYTE*)&read_buffer, (DWORD)sizeof(read_buffer));
                    //もしデバイスが接続されているなら,アプリケーションからのコマンドを待つ
                    if( errorCode != USB_SUCCESS)
                    {
                            //Error
                            DEBUG_ERROR("Error trying to start read");
                    }
                    else
                    {
                            readInProgress = TRUE;
                    }
            }

            //Android側からのコマンドの受信が成功したら実行
            if(AndroidAppIsReadComplete(device_handle, &errorCode, &size) == TRUE)
            {
                    readInProgress = FALSE;

                    if(errorCode == USB_SUCCESS)
                    {
                            ACCESSORY_APP_PACKET* command_packet = (ACCESSORY_APP_PACKET*)&read_buffer[0];

                            while(size > 0)
                            {
                                    switch(command_packet->command)
                                    {
                                            case COMMAND_SET_OUTPUTPIN:
                                                OutputPinControl(command_packet->data);

                                                //このコマンドは2byteなので,2byte分queueを除去する
                                                size -= 2;
                                                //command_packetのポインタを次のパケットにずらす
                                                //このサンプルソースだとすべてのコマンドは2byteに
                                                //なっているが、もし、コマンドによってパケットの
                                                //サイズが変わるなら,そのコマンドのサイズに応じて
                                                //処理することが必要
                                                command_packet++;
                                                    break;

                                            case COMMAND_SERVO_01:
                                                servo_test1((int)command_packet->data);
                                                size -= 2;
                                                command_packet++;
                                                    break;

                                            case COMMAND_SERVO_02:
                                                servo_test2((int)command_packet->data);
                                                size -= 2;
                                                command_packet++;
                                                    break;

                                            default:
                                                //Error, unknown command
                                                DEBUG_ERROR("Error: unknown command received");
                                                    break;
                                    }
                            }
                    }
                    else
                    {
                        //Error
                        DEBUG_ERROR("Error trying to complete read request");
                    }

            }

            //現在のボタンの押下状況を変数にセット
            tempValue = GetPushbuttons();


            //現在のボタンの押下状況を前回のボタン押下状況と比較
            if(tempValue != pushButtonValues)
            {
                    buttonsNeedUpdate = TRUE;
                    pushButtonValues = tempValue;
            }


            //現在のAD値と前回のAD値を比較
            if(AIN0 != pot0Percentage)
            {
                    pot0NeedsUpdate = TRUE;
                    pot0Percentage = (BYTE)(AIN0);
            }


            if(AIN1 != pot1Percentage)
            {
                    pot1NeedsUpdate = TRUE;
                    pot1Percentage = (BYTE)(AIN1);
            }


            if(AIN2 != pot2Percentage)
            {
                    pot2NeedsUpdate = TRUE;
                    pot2Percentage = (BYTE)(AIN2);
            }


            if(AIN3 != pot3Percentage)
            {
                    pot3NeedsUpdate = TRUE;
                    pot3Percentage = (BYTE)(AIN3);
            }

            //Androidへのデータの送信状況をチェック
            if( writeInProgress == TRUE )
            {
                    if(AndroidAppIsWriteComplete(device_handle, &errorCode, &size) == TRUE)
                    {
                            writeInProgress = FALSE;

                            if(errorCode != USB_SUCCESS)
                            {
                                    //Error
                                    DEBUG_ERROR("Error trying to complete write");
                            }
                    }
            }


            //ボタンの更新が必要でかつAndroid側にデータ送信中でないならば,ボタンの
            //状況の更新をAndroid側に送る
            if((buttonsNeedUpdate == TRUE) && (writeInProgress == FALSE))
            {
                    outgoing_packet.command = COMMAND_UPDATE_PUSHBUTTONS;
                    outgoing_packet.data = pushButtonValues;

                    errorCode = AndroidAppWrite(device_handle,(BYTE*)&outgoing_packet, 2);
                    if( errorCode != USB_SUCCESS )
                    {
                            DEBUG_ERROR("Error trying to send button update");
                    }

                    buttonsNeedUpdate = FALSE;
                    writeInProgress = TRUE;
            }

            //AD値の更新が必要でかつAndroid側にデータ送信をしていないならば,AD値の
            //状況の更新をAndroid側に送る
            if((pot0NeedsUpdate == TRUE) && (writeInProgress == FALSE))
            {
                    outgoing_packet.command = COMMAND_UPDATE_POT0;
                    outgoing_packet.data = pot0Percentage;

                    errorCode = AndroidAppWrite(device_handle,(BYTE*)&outgoing_packet, 2);
                    if( errorCode != USB_SUCCESS )
                    {
                            DEBUG_ERROR("Error trying to send pot update");
                    }

                    pot0NeedsUpdate = FALSE;
                    writeInProgress = TRUE;
            }
            if((pot1NeedsUpdate == TRUE) && (writeInProgress == FALSE))
            {
                    outgoing_packet.command = COMMAND_UPDATE_POT1;
                    outgoing_packet.data = pot1Percentage;

                    errorCode = AndroidAppWrite(device_handle,(BYTE*)&outgoing_packet, 2);
                    if( errorCode != USB_SUCCESS )
                    {
                            DEBUG_ERROR("Error trying to send pot update");
                    }

                    pot1NeedsUpdate = FALSE;
                    writeInProgress = TRUE;
            }
            if((pot2NeedsUpdate == TRUE) && (writeInProgress == FALSE))
            {
                    outgoing_packet.command = COMMAND_UPDATE_POT2;
                    outgoing_packet.data = pot2Percentage;

                    errorCode = AndroidAppWrite(device_handle,(BYTE*)&outgoing_packet, 2);
                    if( errorCode != USB_SUCCESS )
                    {
                            DEBUG_ERROR("Error trying to send pot update");
                    }

                    pot2NeedsUpdate = FALSE;
                    writeInProgress = TRUE;
            }
            if((pot3NeedsUpdate == TRUE) && (writeInProgress == FALSE))
            {
                    outgoing_packet.command = COMMAND_UPDATE_POT3;
                    outgoing_packet.data = pot3Percentage;

                    errorCode = AndroidAppWrite(device_handle,(BYTE*)&outgoing_packet, 2);
                    if( errorCode != USB_SUCCESS )
                    {
                            DEBUG_ERROR("Error trying to send pot update");
                    }

                    pot3NeedsUpdate = FALSE;
                    writeInProgress = TRUE;
            }
	} //while(1) main loop
}

///////////usb_host_android.hでプロトタイプ宣言されている///////////////
//-----------------------------------------
BOOL USB_ApplicationDataEventHandler( BYTE address, USB_EVENT event, void *data, DWORD size )
{
	return FALSE;
}
//-----------------------------------------
BOOL USB_ApplicationEventHandler( BYTE address, USB_EVENT event, void *data, DWORD size )
{
	switch( event )
	{
		case EVENT_VBUS_REQUEST_POWER:

			if (((USB_VBUS_POWER_EVENT_DATA*)data)->current <= (MAX_ALLOWED_CURRENT / 2))
			{
				return TRUE;
			}
			else
			{
				DEBUG_ERROR( "\r\n***** USB Error - device requires too much current *****\r\n" );
			}
			break;

		case EVENT_VBUS_RELEASE_POWER:
		case EVENT_HUB_ATTACH:
		case EVENT_UNSUPPORTED_DEVICE:
		case EVENT_CANNOT_ENUMERATE:
		case EVENT_CLIENT_INIT_ERROR:
		case EVENT_OUT_OF_MEMORY:
		case EVENT_UNSPECIFIED_ERROR:	     
		case EVENT_DETACH:		     // USBのケーブルが未接続
		case EVENT_ANDROID_DETACH:
			device_attached = FALSE;
			return TRUE;
			break;

		// Android Specific events
		case EVENT_ANDROID_ATTACH:
			device_attached = TRUE;
			device_handle = data;
			return TRUE;

		default :
			break;
	}
	return FALSE;
}
//////////////////////////////////////////////////////////////////////////////



// ADC1割り込み関数/////////////////////////////////////////////////////////////////
void __attribute__((interrupt, auto_psv)) _ADC1Interrupt(void)
{
	IFS0bits.AD1IF = 0;         //フラグクリア
	AIN0 = ((ADC1BUF0 + ADC1BUF4 + ADC1BUF8)/3)>>2;
	AIN1 = ((ADC1BUF1 + ADC1BUF5 + ADC1BUF9)/3)>>2; 
	AIN2 = ((ADC1BUF2 + ADC1BUF6 + ADC1BUFA)/3)>>2; 
	AIN3 = ((ADC1BUF3 + ADC1BUF7 + ADC1BUFB)/3)>>2; 
    //三回のAD変換値の平均を2bitシフトしているのは、アンドロイドとやりとりしているデータのパケットが1byteのため
    //実際の分解能は10bit
}
//  タイマ1割り込み関数////////////////////////////////////////////////////////////////
void __attribute__((interrupt, no_auto_psv)) _T1Interrupt(void)
{
	IFS0bits.T1IF = 0;			//フラグクリア
}




