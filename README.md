# asmToOBJCode
**CPU0 architecture**

## 英文說明:
**TO BE CONTINUE**

## 中文說明:
系統程式作業。
- 作業要求: `請撰寫一個程式, 可以將組合語言組譯成目的碼並輸出在對應的文字檔中（檔名：題號.txt) 請將程式碼及對應的文字檔壓縮, 上傳至icourse, 檔名為“學號”, 繳交期限為6月9日中午12點前 組合語言的範例於講義區, sample01-sample06 配合情況如下（寫出幾題就幾分） sample01:10分 sample02:20分 sample03:20分 sample04:20分 sample05:20分 sample06:20分`

此程式能夠將組合語言，轉換成目的碼，輸出其各指令`位址(address)`, `指令(Introduction)`, `目的碼(Object Code)`，並將目的碼額外創立檔案存放。

程式使用說明:
- 環境: **[Java17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)** 以上(低於此版本可能導致無法正常執行)。
- 使用方式: 準備一檔案 `sample01.txt`，並將此檔案與此程式的`.jar`檔案放置於同個資料夾下(不同資料夾下須指定路徑)，使用
```bash
java -jar <程式名>.jar <檔案>.txt
```

***

### 使用範例:
```bash
java -jar asmToOBJCode-1.0.jar sample001.txt
```
*sample001.txt:*
```text
	LD	R3,	XXX
	LD  R4, BBBptr
	ST	R1,	BBB
	RET
XXX:	RESW	3
BBB:	WORD	3000
BBBptr: WORD    BBB
```

執行過後，會輸出:
```text
-----Description-----
Address    Introduction                   Object Code
0000        LD R3, XXX                    003F000C
0004        LD  R4, BBBptr                004F0018
0008        ST R1, BBB                    011F0010
000C        RET                           2C000000
0010       XXX: RESW 3                    000000000000000000000000
001C       BBB: WORD 3000                 00000BB8
0020       BBBptr: WORD    BBB            0000001C
-----Object Code-----
003F000C 004F0018 011F0010 2C000000 000000000000000000000000 00000BB8 0000001C
```

並在當前目錄生成 

*001.txt*:
```text
003F000C 004F0018 011F0010 2C000000 000000000000000000000000 00000BB8 0000001C
```

***

## 使用套件:
- [JSON-java(json.org)](https://github.com/stleary/JSON-java)
  - 用於製作指令編碼對照表(編碼表)

## 開發工具及環境:
- 環境:
  - java version "17.0.7" 2023-04-18 LTS
  - Apache Maven 3.9.2 (c9616018c7a021c1c39be70fb2843d6f5f9b8a1c)
- 工具:
  - IntelliJ IDEA 2023.1.2 (Ultimate Edition)