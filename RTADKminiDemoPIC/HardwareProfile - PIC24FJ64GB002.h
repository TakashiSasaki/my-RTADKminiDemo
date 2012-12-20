/**************************************************************
//  file 名 :　HardwareProfile - PICFJ64GB002.h
//  概要    :  各種ピンの初期化をするヘッダーファイル
**************************************************************/
#ifndef HARDWARE_PROFILE_PIC24FJ64GB002_PIM_H
#define HARDWARE_PROFILE_PIC24FJ64GB002_PIM_H

    #define DEMO_BOARD PIC24FJ64GB002_PIM
    #define EXPLORER_16
    #define PIC24FJ64GB002_PIM
    #define CLOCK_FREQ 32000000
    #define DEMO_BOARD_NAME_STRING "PIC24FJ64GB002 PIM"
	
    /** AOU and DOUT ************************************************************/
    #define InitOutPutPin() LATA &= 0x079F; TRISA &= 0x079F; LATB &= 0x001C; TRISB &= 0x001C;
	
    #define DOUT0 LATBbits.LATB9 // DOUT0 = RB9   RTADKminiの基板上に実装されているLED1につながっている
    #define DOUT1 LATBbits.LATB8 // DOUT1 = RB8
    #define DOUT2 LATBbits.LATB7 // DOUT2 = RB7
    #define DOUT3 LATBbits.LATB5 // DOUT3 = RB5
    
    #define AOUT0 LATBbits.LATB0  // AOUT0 = RB0  
    #define AOUT1 LATBbits.LATB1  // AOUT1 = RB1
    #define AOUT2 LATBbits.LATB15 // AOUT2 = RB15 RS485を使うときはAOUT2はMAX485のENABLEなので使用できない
    #define RS485_Send_EN   LATBbits.LATB15  //MAX485のEnable
    ////////////////////////////////////////////////
   
    
    void Init_OC(void)
    {	
        ///////AOUTピンのわりつけの設定//////////
    	RPOR0bits.RP0R = 18;          //OC1をRP0(AOUT0)にわりつけ
   	RPOR0bits.RP1R = 19;          //OC2をRP1(AOUT1)にわりつけ
    	//RPOR7bits.RP15R = 20;       //OC3をRP15(AOUT2)にわりつけ   RS485を使うときはAOUT2はMAX485のENABLEなので使用できない
        ///////タイマ2に関する設定//////////
	//PR2 = 50000;                //これの値はPWMには関係ない PICFJ64GA002とはPWMの設定の仕方が少し違うので注意
	T2CONbits.TCKPS = 0b11;       //FCY = 16MHz 分周比 256
	T2CONbits.TON = 1;            //タイマ2スタート
        ///////タイマ4に関する設定////////
	//PR4 = 50000;                //これの値はPWMには関係ない
	//T4CONbits.TCKPS = 0b11;     //FCY = 16MHz 分周比 256
	//T4CONbits.TON = 1;          //タイマ4スタート
        ///////タイマ5に関する設定///////
	//PR5 = 50000;                //これの値はPWMには関係ない
	T5CONbits.TCKPS = 0b11;       //FCY = 16MHz 分周比 256
        T5CONbits.TON = 1;            //タイマ5スタート
        ///////PWM関係の設定//////////
	OC1R =  0;                    //PWMのduty設定
	OC1RS = 25000;                //PWMの周波数設定
	OC1CON1 = 0;
	OC1CON2 = 0;
	OC1CON2bits.SYNCSEL = 0x1f;   //同期要因をOCモジュール自身に設定
	OC1CON1bits.OCTSEL = 3;       //タイマ5を使用
	OC1CON1bits.OCM = 0b110;      //PWM動作モードを設定
	 
	OC2R =  0;  //PWMのduty設定
	OC2RS = 25000;  //PWMの周期の設定
	OC2CON1 = 0;
	OC2CON2 = 0;
	OC2CON2bits.SYNCSEL = 0x1f; //同期要因をOCモジュール自身に設定
	OC2CON1bits.OCTSEL = 0;     //タイマ2を使用
	OC2CON1bits.OCM = 0b110;    //PWM動作モードを設定
	 
	//OC3R =  0;   //PWMのduty設定
	//OC3RS = 25000;  //PWMの周期の設定
	//OC3CON1 = 0;
	//OC3CON2 = 0;
	//OC3CON2bits.SYNCSEL = 0x1f; //同期要因をOCモジュール自身に設定
	//OC3CON1bits.OCTSEL = 2;     //タイマ4を使用
	//OC3CON1bits.OCM = 0b110;    //PWM動作モードを設定
    }

    ////////////////////////////////////////////////
    #define DOUT0_On()   DOUT0 = 1;
    #define DOUT1_On()   DOUT1 = 1;
    #define DOUT2_On()   DOUT2 = 1;
    #define DOUT3_On()   DOUT3 = 1;
    #define AOUT0_On()   OC1R = 10000;
    #define AOUT1_On()   OC2R = 20000;
    #define AOUT2_On()   //OC3R = 5000;
	
    #define DOUT0_Off()  DOUT0 = 0;
    #define DOUT1_Off()  DOUT1 = 0;
    #define DOUT2_Off()  DOUT2 = 0;
    #define DOUT3_Off()  DOUT3 = 0;
    #define AOUT0_Off()  OC1R = 0;
    #define AOUT1_Off()  OC2R = 0;
    #define AOUT2_Off()  //OC3R = 0;

    /** SWITCH *********************************************************/
    #define InitAllSwitches()   TRISA|=0b0000000000011100;TRISB|=0b0000000000010000;

    #define DIN0 PORTAbits.RA2  //基板上に実装されているSW1につながっている
    #define DIN1 PORTAbits.RA3
    #define DIN2 PORTBbits.RB4
    #define DIN3 PORTAbits.RA4

    #define Switch1Pressed()	((DIN0  == 0)? TRUE : FALSE) // DIN0 = RA2
    #define Switch2Pressed()	((DIN1  == 0)? TRUE : FALSE) // DIN1 = RA3
    #define Switch3Pressed()	((DIN2  == 0)? TRUE : FALSE) // DIN2 = RB4
    #define Switch4Pressed()	((DIN3  == 0)? TRUE : FALSE) // DIN3 = RA4

    /** UART1 ***********************************************************/
    #define BAUDRATE2		57600UL
    #define BRG_DIV2		4
    #define BRGH2		1
    void Init_UART1(void)
    {
    	RPINR18bits.U1RXR = 13;     // UART1 RXをRP13にわりつけ
        RPOR7bits.RP14R = 3;        // UART1 TXをRP14にわりつけ
        //RPOR7bits.RP15R = 4;      // RP15 for RTS
        //U1BRG = 103;              // 9600bps
        //U1BRG = 51;               // 19.3Kbps
        //U1BRG = 25;               // 38.4Kbps
        //U1BRG = 17;               // 56KKbps
        U1BRG = 8;                  // 115Kbps
        //U1BRG = 3;                // 250KKbps
        U1MODE = 0b1000100000000000;// UART1 Init
        U1STA =  0b0000010000000000;// UART1 Init
    } 
    /** ADC1 **************************************************************/
    void Init_AD1(void)
    {
	//タイマ3
	PR3 = 0x0150;
	T3CON = 0b1000000000110000;
        //ADC1の設定
        AD1CON1 = 0x8044;            //タイマ3トリガ,サンプル自動
        AD1CON2 = 0x042C;            //AVdd,AVss,自動スキャン,12回目割り込み
        AD1CON3 = 0x1F05;
        AD1CHS  = 0x0000;
        AD1PCFG = 0xFFCC;
        AD1CSSL = 0x0033;
        IEC0bits.AD1IE = 1;
    }




#endif	//HARDWARE_PROFILE_PIC24FJ64GB002_PIM_H
