//Client (Encoder)  PacketSender

//Encodes the data
//Sends the encoded stream to the server through socket

//Mirage Mohammad
//Arun Cheriakara

import java.net.*;
import java.io.*;
import java.util.Random;

public class PacketSender extends Thread{
    private DataOutputStream output = null;
    private Socket client = null;

    //Constructor
    public PacketSender(String address, int port, String datagram){
        try{
            client = new Socket(address,port);
            System.out.println("\n -------------Client------------- \n");
            System.out.println("Connection Is Established...");
            System.out.println("\n");
            System.out.println("Datagram (With Padding): ");
            System.out.println(datagram);
            output = new DataOutputStream(client.getOutputStream());
            output.writeUTF(datagram);
        } catch (IOException e){
            System.out.println(e);
        }
    }


    //stringToHexadecimal Method
    private static String stringToHexadecimal(String data){
        StringBuffer stringBuffer = new StringBuffer();
        char ch[] = data.toCharArray();
        for(int i = 0; i<ch.length; i++){
            String hexadecimalString = Integer.toHexString(ch[i]);
            stringBuffer.append(hexadecimalString);
        }
        //Returning the string representation of string buffer
        return stringBuffer.toString();
    }

    //ipToHexadecimal Method
    private static String ipToHexadecimal(String data){
        StringBuffer stringBuffer = new StringBuffer();
        String[] words = data.split("\\.");
        for(int i = 0; i < words.length; i++){
            int myTemp = Integer.parseInt(words[i]);
            String hexadecimalString = Integer.toHexString(myTemp);
            if(hexadecimalString.length() != 2){
                hexadecimalString = "0"+hexadecimalString;
            }
            stringBuffer.append(hexadecimalString);
        }
        return stringBuffer.toString();
    }

    //generateID Method
    private static String generateID(){
        Random generator = new Random();
        int num = generator.nextInt(65536);//0xFFFF+1
        String fieldID = Integer.toHexString(num);

        if(fieldID.length() == 3){
            return "0"+fieldID;
        }
        else if(fieldID.length() == 2){
            return "00"+fieldID;
        }
        else if(fieldID.length() == 1){
            return "000"+fieldID;
        }
        return fieldID;
    }

    //splitByFour Method
    private static String splitByFour(String data){
        StringBuffer stringBuffer = new StringBuffer();
        char ch[] = data.toCharArray();

        for(int i = 0; i<ch.length; i++){
            stringBuffer.append(ch[i]);
            if((i+1)%4 == 0){
                stringBuffer.append(" ");
            }
        }
        //Returning the string representation of string buffer
        return stringBuffer.toString();
    }

    //findLength Method
    private static String findLength(String data){
        int length = data.length() +20;
        String hexaLength = Integer.toHexString(length);
        if(hexaLength.length() == 3){
            return "0"+hexaLength;
        }
        else if(hexaLength.length()== 2){
            return "00"+hexaLength;
        }
        else if(hexaLength.length() == 1){
            return "000"+hexaLength;
        }

        return hexaLength;
    }

    //calculateChecksum Method

    private static String calculateCheckSum(String data){
        data = splitByFour(data);
        String[] words = data.split(" ");
        int total = 0;
        int index = 0;
        while(index < words.length){
            //Coverting hexadecimal to decimal
            int temp = Integer.parseInt(words[index],16);
            total += temp;
            index++;
        }
        String checksum = Integer.toHexString(total);

        if(checksum.length() != 4){
            String first = checksum.substring(0,1); //first number
            //New checksum
            checksum = checksum.substring(1); //rest of the checksum
            total = Integer.parseInt(checksum, 16) + Integer.parseInt(first, 16);
        }
        total = 65535 -total;
        //Converting total to hexadecimal
        return Integer.toHexString(total);
    }


    //addPadding Method
    private static String addPadding(String data){
        while(data.length()%8 != 0){
            data+="0";
        }
        return data;
    }

    //encode Method
    private static String encode(String clientIP, String serverIP, String payload){
        String myFieldID = generateID();
        String myClientIP = ipToHexadecimal(clientIP);
        String myServerIP = ipToHexadecimal(serverIP);
        String myPayload = stringToHexadecimal(payload);
        String myLength = findLength(payload);
        String myChecksum = calculateCheckSum("4500"+myLength+myFieldID+"40004006"+myClientIP+myServerIP);
        String wholeDatagram = "4500"+myLength+myFieldID+"40004006"+myChecksum+myClientIP+myServerIP+addPadding(myPayload);

        //Returns encoded message
        return splitByFour(wholeDatagram);

    }

    public static void main(String[] args) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\nEnter the IP address of the server: ");
        String serverIP = bufferedReader.readLine();
        System.out.println("\nEnter the Payload: ");
        String payload = bufferedReader.readLine();
        String clientIP = InetAddress.getLocalHost().getHostAddress();
        String datagram = encode(clientIP, serverIP, payload);
        PacketSender client = new PacketSender("localhost",8888,datagram);


    }





}
