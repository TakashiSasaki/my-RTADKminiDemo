≪RTCAMHEADサンプルソース≫

・はじめに
  本サンプルソースはRT-CAMHEAD用のものです。このサンプルではAndroid端末
　の画面にカメラの映像を表示し,画面上のシークバーでサーボモータの角度
　をコントロールすることができます。
  

・ファイル構成
　RTCAMHEAD02　　　     (Android側ソースファイル)
　RTADKminiDemo         (Android側ソースファイル)
  RTADKminiDemoPIC      (PIC側ソースファイル)
  Readme.txt　　　      (この説明ファイル)
　RTADKminiDemo仕様.txt (PIC側ソースファイルの説明)　

・開発環境
  Android側の開発環境はeclipseになります。
  PIC側の開発環境はMPLABXになります。
・使用方法
　各ファイルをeclipseまたはMPLABXに取り込んでください。

・注意
　RT-ADKminiのDIN0,DIN1,DIN2,DIN3ピンはプルアップ抵抗を介して3.3Vの
　ピンにつないでください。これらのピンにつけたタクトスイッチを押す
　とサーボモータの角度を変えることができます。タクトスイッチを使わない
　ときも必ずDIN0,DIN1,DIN2,DIN3ピンはプルアップしてください。
　
--以上--


